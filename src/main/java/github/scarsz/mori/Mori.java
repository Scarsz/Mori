package github.scarsz.mori;

import com.esotericsoftware.minlog.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import github.scarsz.mori.build.Job;
import github.scarsz.mori.build.action.Action;
import github.scarsz.mori.build.action.PushAction;
import github.scarsz.mori.config.Configuration;
import github.scarsz.mori.discord.Bot;
import github.scarsz.mori.git.Commit;
import github.scarsz.mori.web.WebServer;

import java.io.File;
import java.io.IOException;

public class Mori {

    public static Mori INSTANCE;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Bot bot;
    private final Configuration config;
    private final WebServer webServer;

    public Mori(String[] args) throws IOException {
        INSTANCE = this;

        config = new Configuration(new File("config.yml"));
        config.load();

        bot = new Bot(this);

        webServer = new WebServer(this);
        webServer.start();
    }

    public Job enqueue(Action action) {
        Log.info("Action " + action + " was added to the queue");

        PushAction pushAction = (PushAction) action;
        Log.info(pushAction.getUser() + " pushed " + pushAction.getCommits().size() + " commits to " + pushAction.getRepository() + " @ " + pushAction.getBranch() + ":");
        for (Commit commit : pushAction.getCommits()) {
            Log.info("- " + commit);
        }

        return null;
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

}
