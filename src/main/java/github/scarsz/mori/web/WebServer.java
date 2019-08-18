package github.scarsz.mori.web;

import com.esotericsoftware.minlog.Log;
import github.scarsz.mori.Mori;
import github.scarsz.mori.util.WebUtil;
import github.scarsz.mori.web.hook.GitHubHookHandler;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.HttpResponseException;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.staticfiles.Location;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.util.Collections;
import java.util.Objects;

public class WebServer {

    private Javalin app;

    public WebServer() {
        app = Javalin.create(c -> {
            c.addStaticFiles("/public");
            if (new File("public").exists()) c.addStaticFiles("public", Location.EXTERNAL);
            c.prefer405over404 = true;
            c.showJavalinBanner = false;
            c.requestCacheSize = (long) (1024 * 1024 * 1024);
        });
        app.before(context -> {
            if (context.userAgent() == null) throw new BadRequestResponse("Missing user agent");
        });
        app.post("/webhook", context -> {
            String agent = Objects.requireNonNull(context.userAgent());
            Log.info("Received /webhook POST from " + WebUtil.getRealRemoteAddress(context.req) + " citing User-Agent \"" + agent + "\"");
            try {
                if (agent.startsWith("GitHub-Hookshot")) {
                    GitHubHookHandler.handle(context);
                }
                context.status(204);
            } catch (UnsupportedOperationException e) {
                throw new HttpResponseException(422, e.getMessage(), Collections.emptyMap());
            } catch (Exception e) {
                Log.error("Failed to process webhook:\n" + ExceptionUtils.getStackTrace(e));
                throw new InternalServerErrorResponse(e.getMessage() != null ? e.getMessage() : e.getStackTrace()[1].toString());
            }
        });
    }

    public void start() {
        app.start(Mori.INSTANCE.getConfig().get("web.port"));
    }

}
