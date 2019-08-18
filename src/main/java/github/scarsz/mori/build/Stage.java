package github.scarsz.mori.build;

import github.scarsz.mori.build.instruction.Instruction;
import github.scarsz.mori.build.instruction.build.RunMavenGoals;
import github.scarsz.mori.build.instruction.deploy.ArtifactInstruction;
import github.scarsz.mori.build.instruction.git.CloneRepository;
import github.scarsz.mori.build.instruction.notify.DiscordStatusInstruction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum Stage {

    ACQUIRE(CloneRepository.class),
    BUILD(RunMavenGoals.class),
    TEST(RunMavenGoals.class),
    DEPLOY(ArtifactInstruction.class),
    NOTIFY(DiscordStatusInstruction.class);

    private final Set<Class<? extends Instruction>> availableInstructions = new HashSet<>();

    Stage(Class<? extends Instruction>... availableInstructions) {
        this.availableInstructions.addAll(Arrays.asList(availableInstructions));
    }

    public static Stage of(String name) {
        return Arrays.stream(values())
                .filter(s -> s.name().equalsIgnoreCase(name))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid stage name given: " + name));
    }

    public Set<Class<? extends Instruction>> getAvailableInstructions() {
        return availableInstructions;
    }

}
