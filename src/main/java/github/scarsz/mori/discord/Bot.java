package github.scarsz.mori.discord;

import com.esotericsoftware.minlog.Log;
import github.scarsz.mori.Mori;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.commons.lang3.StringUtils;

import javax.security.auth.login.LoginException;

public class Bot {

    private final Mori mori;
    private JDA jda;

    public Bot(Mori mori) {
        this.mori = mori;

        long timer;
        String token = mori.getConfig().get("discord.token");
        if (StringUtils.isBlank(token) || token.length() != 59) {
            Log.error("Discord token isn't valid, not starting");
        }
        try {
            timer = System.currentTimeMillis();
            Log.info("Connecting to Discord...");
            login(token);
            Log.info("Successfully connected to Discord in " + (System.currentTimeMillis() - timer) + "ms");
        } catch (LoginException e) {
            Log.error("Failed to login to Discord", e);
        }
    }

    public void login() throws LoginException {
        login(mori.getConfig().get("discord.token"));
    }

    public void login(String token) throws LoginException {
        this.jda = new JDABuilder()
                .setToken(token)
                .build();

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
