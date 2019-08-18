package github.scarsz.mori.build.agent;

import com.esotericsoftware.wildcard.Paths;
import github.scarsz.mori.build.BuildTool;
import github.scarsz.mori.build.Job;
import github.scarsz.mori.build.Result;
import github.scarsz.mori.build.Stage;
import github.scarsz.mori.build.instruction.Instruction;
import github.scarsz.mori.git.Repository;
import org.apache.commons.io.FileUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class LocalAgent implements IAgent {

    private Set<BuildTool> tools = new HashSet<>();

    public LocalAgent() {
        this.tools.add(BuildTool.JAVA_JDK_8);
        this.tools.add(BuildTool.MAVEN);
    }

    @Override
    public void perform(Job job) {
        job.info("Agent LOCAL", "Performing " + job + "...");
        int counter = 0;

        build:
        for (Stage stage : Arrays.asList(Stage.ACQUIRE, Stage.BUILD, Stage.TEST, Stage.DEPLOY)) {
            counter = 0;
            Set<Instruction> instructions = job.getRepository().getConfig().getInstructions().getOrDefault(stage, Collections.emptySet());
            for (Instruction instruction : instructions) {
                counter++;
                try {
                    job.info("Agent LOCAL", "- " + counter + "/" + instructions.size() + " " + instruction);
                    int code = instruction.perform(job);
                    if (code != 0) throw new RuntimeException("Result code was non-zero: " + code);
                } catch (Exception e) {
                    job.error("Agent LOCAL", "Job " + job + " encountered error on " + stage.name().toLowerCase() + " instruction " + counter + "/" + instructions.size(), e);
                    job.setResult(Result.FAIL, e);
                    break build;
                }
            }
        }

        if (!job.getResult().isCompletedExceptionally()) {
            job.setResult(Result.PASS);
        }

        counter = 0;
        for (Instruction instruction : job.getRepository().getConfig().getInstructions().getOrDefault(Stage.NOTIFY, Collections.emptySet())) {
            counter++;
            try {
                int code = instruction.perform(job);
                if (code != 0) throw new RuntimeException("Result code for notify instruction " + counter + " was non-zero: " + code);
            } catch (Exception e) {
                job.error("Agent LOCAL", "Job " + job + " notify instruction " + counter + " encountered exception", e);
            }
        }

        if (job.getRepository().getConfig().shouldCleanAfterBuild()) {
            Paths paths = new Paths(job.getJobDirectory().getAbsolutePath(), "*", "!build.log", "!artifacts");
            paths.getFiles().forEach(file -> {
                job.info("Cleaning", "Deleting " + file.getPath());
                FileUtils.deleteQuietly(file);
                file.deleteOnExit();
            });
        }

        job.info("Local agent finished action in " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - job.getLog().getTimestamp()) + " seconds");

        job.getLog().save();
    }

    @Override
    public boolean isCapable(Repository repository) {
        return tools.containsAll(repository.getConfig().getRequiredBuildTools());
    }

    @Override
    public Set<BuildTool> getTools() {
        return Collections.unmodifiableSet(tools);
    }

}
