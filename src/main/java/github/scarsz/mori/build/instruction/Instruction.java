package github.scarsz.mori.build.instruction;

import github.scarsz.mori.build.Job;
import org.jetbrains.annotations.NotNull;

public abstract class Instruction {

    @NotNull public abstract String getName();
    @NotNull public abstract String getDescription();

    public abstract int perform(Job job) throws Exception;

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{}";
    }

}
