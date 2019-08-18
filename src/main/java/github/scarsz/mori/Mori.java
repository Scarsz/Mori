package github.scarsz.mori;

import alexh.weak.Dynamic;
import alexh.weak.Weak;
import com.esotericsoftware.minlog.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import github.scarsz.mori.build.BuildTool;
import github.scarsz.mori.build.Job;
import github.scarsz.mori.build.QueueWorker;
import github.scarsz.mori.build.Stage;
import github.scarsz.mori.build.action.IAction;
import github.scarsz.mori.build.action.PushAction;
import github.scarsz.mori.build.agent.IAgent;
import github.scarsz.mori.build.agent.LocalAgent;
import github.scarsz.mori.build.instruction.Instruction;
import github.scarsz.mori.build.instruction.build.RunMavenGoals;
import github.scarsz.mori.build.instruction.deploy.ArtifactInstruction;
import github.scarsz.mori.build.instruction.git.CloneRepository;
import github.scarsz.mori.build.instruction.notify.DiscordStatusInstruction;
import github.scarsz.mori.config.Configuration;
import github.scarsz.mori.config.RepositoryConfig;
import github.scarsz.mori.discord.Bot;
import github.scarsz.mori.git.Commit;
import github.scarsz.mori.git.Repository;
import github.scarsz.mori.web.WebServer;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Mori {

    public static Mori INSTANCE;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Bot bot;
    private final Configuration config;
    private final Configuration repositories;
    private final WebServer webServer;
    private final QueueWorker queueWorker;
    private final Set<IAgent> agents = new LinkedHashSet<>();
    private final Map<IAction, CompletableFuture<Job>> queue = new LinkedHashMap<>();

    public Mori(String[] args) throws IOException {
        INSTANCE = this;

        printBanner();

        config = new Configuration(new File("config.yml"));
        config.saveDefaultConfig();
        config.load();
        if (!((String) config.get("discord.token")).matches("[MN][A-Za-z\\d]{23}\\.[\\w-]{6}\\.[\\w-]{27}")) {
            throw new IllegalArgumentException("Discord bot token is not valid! Make sure it's filled out in your config.");
        }

        repositories = new Configuration(new File("repositories.yml"));
        repositories.saveDefaultConfig();
        repositories.load();

        bot = new Bot();

        webServer = new WebServer();
        webServer.start();

        agents.add(new LocalAgent());

        queueWorker = new QueueWorker();
        queueWorker.start();
    }

    private void printBanner() {
        String[] lines = new String[]{" ███▄ ▄███▓ ▒█████   ██▀███   ██▓",
                                      "▓██▒▀█▀ ██▒▒██▒  ██▒▓██ ▒ ██▒▓██▒",
                                      "▓██    ▓██░▒██░  ██▒▓██ ░▄█ ▒▒██▒",
                                      "▒██    ▒██ ▒██   ██░▒██▀▀█▄  ░██░",
                                      "▒██▒   ░██▒░ ████▓▒░░██▓ ▒██▒░██░",
                                      "░ ▒░   ░  ░░ ▒░▒░▒░ ░ ▒▓ ░▒▓░░▓  ",
                                      "░  ░      ░  ░ ▒ ▒░   ░▒ ░ ▒░ ▒ ░",
                                      "░      ░   ░ ░ ░ ▒    ░░   ░  ▒ ░",
                                      "       ░       ░ ░     ░      ░  "};
        for (String line : lines) Log.info(line);
    }

    public Future<Job> enqueue(IAction action) {
        Log.info("Action " + action + " was added to the queue");

        PushAction pushAction = (PushAction) action;
        Log.info(pushAction.getUser() + " pushed " + pushAction.getCommits().size() + " commits to " + pushAction.getRepository() + " @ " + pushAction.getBranch() + ":");
        for (Commit commit : pushAction.getCommits()) {
            Log.info("- " + commit);
        }

        CompletableFuture<Job> future = new CompletableFuture<>();
        synchronized (queue) {
            queue.put(action, future);

            synchronized (queueWorker) {
                queueWorker.notifyAll();
            }
        }
        return future;
    }

    private final Map<String, RepositoryConfig> configCache = new HashMap<>();
    public RepositoryConfig getRepositoryConfig(Repository repository) throws NullPointerException {
        try {
            Dynamic repositoryConfig = repositories.dget(repository.getFullName());
            Map<Stage, Set<Instruction>> instructions = new HashMap<>();
            Log.debug("Parsing instructions", repositoryConfig.get("instructions") + " [" + repositoryConfig.get("instructions").children().count() + "]");
            repositoryConfig.get("instructions").children()
                    .forEach(d -> {
                        Stage stage = Stage.of(d.key().asString());
                        Log.debug("Parsing instructions", "Stage: " + stage.name());
                        Set<Instruction> stageInstructions = new LinkedHashSet<>();
                        Log.debug("Parsing instructions", d.children().count() + " children...");
                        d.children().forEach(inst -> {
                            Log.debug("Parsing instructions", "Parse: " + inst.asString());
                            Pattern pattern = Pattern.compile("^(\\w+)\\(?|\\)$");
                            Matcher matcher = pattern.matcher(inst.asString());
                            if (!matcher.find()) throw new IllegalArgumentException("Invalid instruction given: " + inst.asString());
                            String[] instructionRaw = inst.asString().replace(matcher.group(1), "").split("^\\(|\\)$");
                            String instructionName = matcher.group(1).toLowerCase();
                            String argument = instructionRaw.length >= 2 && StringUtils.isNotBlank(instructionRaw[1]) ? instructionRaw[1] : null;

                            Instruction instruction;
                            switch (instructionName) {
                                case "clone": instruction = new CloneRepository(); break;
                                case "maven": instruction = new RunMavenGoals(argument); break;
                                case "artifact": instruction = new ArtifactInstruction(argument); break;
                                case "discord": instruction = new DiscordStatusInstruction(argument); break;
                                default: throw new IllegalArgumentException("Unknown instruction name: " + instructionName);
                            }

                            if (stage.getAvailableInstructions().stream().noneMatch(aClass -> instruction.getClass().equals(aClass))) {
                                throw new IllegalArgumentException("Instruction " + instruction.getClass().getSimpleName() + " is not available for build stage " + stage.name().toLowerCase());
                            }

                            stageInstructions.add(instruction);
                        });

                        instructions.put(stage, stageInstructions);
                    });
            return new RepositoryConfig(
                    repository,
                    repositoryConfig.get("tools").children()
                            .map(Weak::asString)
                            .map(BuildTool::of)
                            .collect(Collectors.toSet()),
                    instructions,
                    repositoryConfig.get("webhook secret").asString(),
                    true
            );
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new NullPointerException("Non-configured repository " + repository.getFullName());
        }
    }

    public Gson getGson() {
        return gson;
    }
    public Configuration getConfig() {
        return config;
    }
    public WebServer getWebServer() {
        return webServer;
    }
    public Map<IAction, CompletableFuture<Job>> getQueue() {
        return queue;
    }
    public Set<IAgent> getAgents() {
        return agents;
    }
    public Bot getBot() {
        return bot;
    }

}
