package github.scarsz.mori.web.hook;

import alexh.weak.Dynamic;
import com.esotericsoftware.minlog.Log;
import com.google.gson.JsonSyntaxException;
import github.scarsz.mori.Mori;
import github.scarsz.mori.build.action.IAction;
import github.scarsz.mori.build.action.PushAction;
import github.scarsz.mori.git.Commit;
import github.scarsz.mori.git.GitUser;
import github.scarsz.mori.git.Repository;
import github.scarsz.mori.util.WebUtil;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.http.InternalServerErrorResponse;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class GitHubHookHandler {

    public static void handle(Context context) {
        String bodyRaw = context.body();
        Dynamic payload;
        try {
            payload = Dynamic.from(Mori.INSTANCE.getGson().fromJson(bodyRaw, Map.class));
        } catch (JsonSyntaxException e) {
            throw new HttpResponseException(415, "The webhook media type must be set to JSON", Collections.emptyMap());
        }
        IAction action;

        try {
            String event = context.req.getHeader("X-GitHub-Event");
            Method method = GitHubHookHandler.class.getMethod("on" + event.substring(0, 1).toUpperCase() + event.substring(1), Context.class, Dynamic.class);
            action = (IAction) method.invoke(null, context, payload);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("Event not implemented");
        } catch (Exception e) {
            Log.error("Failed to process a GitHub webhook from " + WebUtil.getRealRemoteAddress(context.req), e);
            throw new InternalServerErrorResponse();
        }

        if (action == null) {
            Log.info("No action necessary for webhook, skipping.");
            return;
        }

        if (action.getRepository() != null) {
            if (Mori.INSTANCE.getRepositoryConfig(action.getRepository()) == null) {
                Log.warn("Skipping webhook because no repository is configured for it");
                return;
            }

            if (!verifySecret(context, Mori.INSTANCE.getRepositoryConfig(action.getRepository()).getWebhookSecret())) {
                Log.error("Received GitHub webhook whose signature didn't match. Is the webhook secret set properly?");
                return;
            }
        }

        Mori.INSTANCE.enqueue(action);
    }

    public static void onPing(Context context, Dynamic payload) {
        Log.info("Received GitHub webhook ping from user " + payload.dget("sender.login").asString() + " for " + payload.dget("organization.login").asString() + ": " + payload.get("zen").asString());

        if (!payload.dget("hook.config.content_type").asString().equals("json")) {
            Log.warn("Received webhook ping is configured to send the wrong content type! The type must be JSON!");
            throw new HttpResponseException(415, "Webhook content type must be JSON", Collections.emptyMap());
        }
    }

    public static PushAction onPush(Context context, Dynamic payload) {
        return new PushAction.Builder()
                .repository(new Repository(
                        payload.dget("repository.name").asString(),
                        payload.dget("repository.full_name").asString(),
                        payload.dget("repository.html_url").asString(),
                        payload.dget("repository.clone_url").asString(),
                        new GitUser(
                                payload.dget("repository.owner.name").asString(),
                                payload.dget("repository.owner.html_url").asString(),
                                payload.dget("repository.owner.avatar_url").asString(),
                                null
                        ),
                        payload.dget("repository.description").asString(),
                        payload.dget("repository.language").asString()
                ))
                .branch(payload.get("ref").asString().replace("refs/heads/", ""))
                .user(new GitUser(
                        payload.dget("sender.login").asString(),
                        payload.dget("sender.html_url").asString(),
                        payload.dget("sender.avatar_url").asString(),
                        payload.dget("pusher.email").isPresent() ? payload.dget("pusher.email").asString() : null
                ))
                .commits(
                        payload.get("commits").children()
                                .map(d -> new Commit(
                                        d.get("id").asString(),
                                        d.get("message").asString(),
                                        d.get("url").asString(),
                                        new DateTime(d.get("timestamp").asString()).getMillis(),
                                        new GitUser(
                                                d.dget("author.name").asString(),
                                                "https://github.com/" + d.dget("author.username").asString(),
                                                null,
                                                d.dget("author.email").asString()
                                        ),
                                        new GitUser(
                                                d.dget("committer.name").asString(),
                                                "https://github.com/" + d.dget("committer.username").asString(),
                                                "https://github.com/" + d.dget("committer.username") + ".png",
                                                d.dget("committer.email").asString()
                                        ),
                                        d.get("added").asList(),
                                        d.get("removed").asList(),
                                        d.get("modified").asList()
                                ))
                                .collect(Collectors.toList())
                )
                .hashes(
                        payload.get("before").asString(),
                        payload.get("after").asString()
                )
                .build();
    }

    /**
     * Verify that the hash of the given request's content matches the X-Hub-Signature header
     * @param context the context to verify
     * @param secret the secret key used for the webhook that initiated the context
     * @return whether or not the request is valid
     * @throws IllegalArgumentException when the provided secret doesn't match the HmacSHA1 specifications
     * @throws RuntimeException when the HmacSHA1 algorithm isn't available, should never happen
     */
    public static boolean verifySecret(Context context, String secret) {
        String signature = context.req.getHeader("X-Hub-Signature");
        if (StringUtils.isBlank(signature) && StringUtils.isBlank(secret)) return true;

        SecretKeySpec spec = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(spec);
            String expected = "sha1=" + Hex.encodeHexString(mac.doFinal(context.bodyAsBytes()));
            return expected.equals(signature);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("Invalid HMAC key specified", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("HmacSHA1 algorithm unavailable", e);
        }
    }

}
