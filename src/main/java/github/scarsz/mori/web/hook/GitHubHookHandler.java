package github.scarsz.mori.web.hook;

import alexh.weak.Dynamic;
import github.scarsz.mori.Mori;
import github.scarsz.mori.build.action.Action;
import github.scarsz.mori.build.action.PushAction;
import github.scarsz.mori.git.Commit;
import github.scarsz.mori.git.Repository;
import github.scarsz.mori.git.User;
import io.javalin.http.Context;
import org.joda.time.DateTime;

import java.util.Map;
import java.util.stream.Collectors;

public class GitHubHookHandler {

    public static void handle(Context context) {
        String bodyRaw = context.body();
        Dynamic payload = Dynamic.from(Mori.INSTANCE.getGson().fromJson(bodyRaw, Map.class));

        Action action;
        if ("push".equals(context.req.getHeader("X-GitHub-Event"))) {
            action = new PushAction.Builder()
                    .repository(new Repository(
                            payload.dget("repository.name").asString(),
                            payload.dget("repository.full_name").asString(),
                            payload.dget("repository.html_url").asString(),
                            payload.dget("repository.clone_url").asString(),
                            new User(
                                    payload.dget("repository.owner.name").asString(),
                                    payload.dget("repository.owner.html_url").asString(),
                                    payload.dget("repository.owner.avatar_url").asString(),
                                    null
                            ),
                            payload.dget("repository.description").asString(),
                            payload.dget("repository.language").asString()
                    ))
                    .branch(payload.get("ref").asString().replace("refs/heads/", ""))
                    .user(new User(
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
                                            new DateTime(d.get("timestamp").asString()).getMillis(),
                                            new User(
                                                    d.dget("author.name").asString(),
                                                    "https://github.com/" + d.dget("author.username").asString(),
                                                    null,
                                                    d.dget("author.email").asString()
                                            ),
                                            new User(
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
        } else {
            throw new UnsupportedOperationException();
        }

        Mori.INSTANCE.enqueue(action);
    }

}
