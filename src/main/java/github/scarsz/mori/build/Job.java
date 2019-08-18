package github.scarsz.mori.build;

import com.esotericsoftware.minlog.Log;
import github.scarsz.mori.Mori;
import github.scarsz.mori.build.action.IAction;
import github.scarsz.mori.git.Repository;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Job {

    private final IAction action;
    private final int id;
    private final File jobDirectory;
    private final BuildLog log;
    private final Set<Artifact> artifacts = new HashSet<>();

    private CompletableFuture<Result> result = new CompletableFuture<>();
    private CompletableFuture<Optional<Exception>> reason = new CompletableFuture<>();
    private long completionTime;

    public Job(IAction action) {
        this.action = action;
        this.id = action.getRepository().getConfig().getNextJobId();
        this.jobDirectory = new File("jobs/" + action.getRepository().getFullName() + "/" + id);

        if (!this.jobDirectory.exists() && !this.jobDirectory.mkdirs()) {
            throw new RuntimeException("Failed to create build directory for " + this);
        }

        this.log = new BuildLog(new File(jobDirectory, "build.log"));
    }

    public int getId() {
        return id;
    }
    public IAction getAction() {
        return action;
    }
    public Repository getRepository() {
        return action.getRepository();
    }
    public File getJobDirectory() {
        return jobDirectory;
    }
    public File getBuildDirectory() {
        return new File(jobDirectory, "build");
    }
    public Set<Artifact> getArtifacts() {
        return artifacts;
    }
    public File getArtifactDirectory() {
        return new File(jobDirectory, "artifacts");
    }
    public long getCompletionTime() {
        return completionTime;
    }
    public String getUrl() {
        return "https://ci.scarsz.me/job/" + getRepository().getFullName() + "/" + id;
    }

    private static final Pattern LEVEL_PATTERN = Pattern.compile("^\\[([A-Z]+)] (.+)?");
    public void log(String line) {
        log("Job", line);
    }
    public void log(String category, String line) {
        Matcher matcher = LEVEL_PATTERN.matcher(line);
        if (matcher.find()) {
            String level = matcher.group(1);
            line = matcher.group(2);
            if (StringUtils.isBlank(line)) return;
            switch (level) {
                case "INFO":    info(category, line); break;
                case "WARNING": warn(category, line); break;
                case "ERROR":
                case "SEVERE":  error(category, line); break;
                default:        info(category, "[" + level + "] " + line); break;
            }
        } else {
            info(category, line);
        }
    }
    public void info(String line) {
        info("Job", line);
    }
    public void info(String category, String line) {
        getLog().append(Level.INFO, line);
        if (Mori.INSTANCE.getConfig().get("build.agent.verbose"))
            Log.info("Agent LOCAL", category + " > " + line);
    }
    public void warn(String line) {
        warn("Job", line);
    }
    public void warn(String category, String line) {
        getLog().append(Level.WARNING, line);
        if (Mori.INSTANCE.getConfig().get("build.agent.verbose"))
            Log.warn("Agent LOCAL", category + " > " + line);
    }
    public void error(String line) {
        error("Job", line);
    }
    public void error(String category, String line) {
        error(category, line, null);
    }
    public void error(String line, Throwable throwable) {
        error("Job", line, throwable);
    }
    public void error(String category, String line, Throwable throwable) {
        getLog().append(Level.SEVERE, line);
        if (Mori.INSTANCE.getConfig().get("build.agent.verbose"))
            Log.error("Agent LOCAL", category + " > " + line, throwable);
    }
    public BuildLog getLog() {
        return log;
    }

    public CompletableFuture<Result> getResult() {
        return result;
    }
    public void setResult(Result result) {
        setResult(result, null);
    }
    public void setResult(Result result, Exception exception) {
        this.result.complete(result);
        this.reason.complete(Optional.ofNullable(exception));
        this.completionTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Job{" + action.getRepository().getFullName() + "#" + id + "}";
    }

}
