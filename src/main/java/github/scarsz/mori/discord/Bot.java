package github.scarsz.mori.discord;

import com.esotericsoftware.minlog.Log;
import github.scarsz.mori.Mori;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.commons.lang3.StringUtils;

import javax.security.auth.login.LoginException;
import java.util.regex.Pattern;

public class Bot {

    /**
     * A Regex pattern that matches a Discord bot token
     */
    private static final Pattern TOKEN_PATTERN = Pattern.compile("[MN][A-Za-z\\d]{23}\\.[\\w-]{6}\\.[\\w-]{27}");

    private JDA jda;

    public Bot() {
        String token = Mori.INSTANCE.getConfig().get("discord.token");
        if (StringUtils.isBlank(token) || !TOKEN_PATTERN.matcher(token).matches()) {
            Log.error("Discord token isn't valid, not starting");
            Runtime.getRuntime().exit(1);
        }
        try {
            long timer = System.currentTimeMillis();
            Log.info("Connecting to Discord...");
            login(token);
            Log.info("Successfully connected to Discord in " + (System.currentTimeMillis() - timer) + "ms");
        } catch (LoginException e) {
            Log.error("Failed to login to Discord", e);
        }
    }

    public void login() throws LoginException {
        login(Mori.INSTANCE.getConfig().get("discord.token"));
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

    public JDA getJda() {
        return jda;
    }

}
