package github.scarsz.mori.config;

import github.scarsz.mori.build.BuildTool;
import github.scarsz.mori.build.Stage;
import github.scarsz.mori.build.instruction.Instruction;
import github.scarsz.mori.git.Repository;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class RepositoryConfig {

    private final Repository repository;
    private Set<BuildTool> requiredBuildTools;
    private Map<Stage, Set<Instruction>> instructions;
    private String webhookSecret;
    private boolean cleanAfterBuild;

    public RepositoryConfig(Repository repository, Set<BuildTool> requiredBuildTools, Map<Stage,
            Set<Instruction>> instructions, String webhookSecret, boolean cleanAfterBuild) {
        this.repository = repository;
        this.requiredBuildTools = requiredBuildTools;
        this.instructions = instructions;
        this.webhookSecret = webhookSecret;
        this.cleanAfterBuild = cleanAfterBuild;
    }

    public Map<Stage, Set<Instruction>> getInstructions() {
        return instructions;
    }
    public Set<BuildTool> getRequiredBuildTools() {
        return requiredBuildTools;
    }
    public String getWebhookSecret() {
        return webhookSecret;
    }
    public boolean shouldCleanAfterBuild() {
        return cleanAfterBuild;
    }

    public int getNextJobId() {
        File[] files = repository.getDataFolder().listFiles();
        if (files == null) return 1;

        int biggest = 0;
        for (File file : files) {
            if (!StringUtils.isNumeric(file.getName())) continue;
            int number = Integer.parseInt(file.getName());
            if (number > biggest) biggest = number;
        }
        return biggest + 1;
    }

    @Override
    public String toString() {
        return "RepositoryConfig{" +
                "repository=" + repository +
                ", requiredBuildTools=" + requiredBuildTools +
                ", instructions=" + instructions +
                '}';
    }
}
