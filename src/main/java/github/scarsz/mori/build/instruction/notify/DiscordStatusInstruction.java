package github.scarsz.mori.build.instruction.notify;

import github.scarsz.mori.Mori;
import github.scarsz.mori.build.Job;
import github.scarsz.mori.build.Result;
import github.scarsz.mori.build.action.PushAction;
import github.scarsz.mori.build.instruction.Instruction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DiscordStatusInstruction extends Instruction {

    private final String target;

    public DiscordStatusInstruction(String target) {
        this.target = target;
    }

    @NotNull
    @Override
    public String getName() {
        return "Notify Discord channel of build status";
    }
    @NotNull
    @Override
    public String getDescription() {
        return "Send an embed to the given Discord channel ID showing the build's status and artifacts, if any";
    }

    @Override
    public int perform(Job job) throws Exception {
        TextChannel channel = Objects.requireNonNull(Mori.INSTANCE.getBot().getJda().getTextChannelById(target), "Unknown channel ID " + target);
        Result result = job.getResult().get();
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(job.getRepository().getFullName() + " #" + job.getId());
        embed.setColor(result.getColor());
        embed.addField("Status", result == Result.PASS ? "Success" : "Failure", true);
        embed.addField("Time taken", TimeUnit.MILLISECONDS.toSeconds(job.getCompletionTime() - job.getLog().getTimestamp()) + " seconds", true);

        // will probably always happen but just to be safe
        if (job.getAction() instanceof PushAction) {
            PushAction action = (PushAction) job.getAction();
            embed.setTitle(job.getRepository().getFullName() + " #" + job.getId(), action.getRepository().getUrl() + "/compare/" + action.getBeforeHash() + "..." + action.getAfterHash());
            embed.setDescription(
                    action.getCommits().stream()
                            .map(commit -> String.format("[`%s`](%s) %s%s",
                                    commit.getShortId(),
                                    commit.getUrl(),
                                    commit.getMessage(),
                                    !commit.getCommitter().equals(action.getUser())
                                            ? " - [**" + commit.getCommitter().getName() + "**](" + commit.getCommitter().getProfileUrl() + ")"
                                            : ""
                                    )
                            )
                            .collect(Collectors.joining("\n"))
            );
        }

        if (job.getArtifacts().size() > 0) {
            embed.addField("Artifacts", job.getArtifacts().stream()
                            .map(artifact -> String.format("[%s](%s) (`%s`)",
                                    artifact.getName(), artifact.getUrl(), readableFileSize(artifact.getSize())
                            )).collect(Collectors.joining("\n")),
                    false
            );
        }

        try {
            channel.sendMessage(embed.build()).complete();
            return 0;
        } catch (Exception e) {
            job.error("Agent LOCAL", "Failed to send job status to " + channel, e);
            return 1;
        }
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0B";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB", "PB", "EB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1000));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1000, digitGroups)) + " " + units[digitGroups];
    }

}
