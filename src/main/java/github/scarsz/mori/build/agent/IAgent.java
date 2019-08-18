package github.scarsz.mori.build.agent;

import github.scarsz.mori.build.BuildTool;
import github.scarsz.mori.build.Job;
import github.scarsz.mori.build.action.IAction;
import github.scarsz.mori.git.Repository;

import java.util.Set;

public interface IAgent {

    /**
     * Check whether this agent is capable of fulfilling the given repository's agent requirements
     * @param repository the repository to check
     * @return whether or not this agent is able run builds for the given repository
     */
    boolean isCapable(Repository repository);

    /**
     * Check whether this agent is capable of fulfilling the given action's repository's agent requirements
     * <br>Convenience method for isCapable({@link github.scarsz.mori.build.action.IAction#getRepository()})
     * @param action the action who's repository to check
     * @return whether or not this agent is able run builds for the given repository
     */
    default boolean isCapable(IAction action) {
        return isCapable(action.getRepository());
    }

    /**
     * Get the list of {@link BuildTool}s ready to be used on this agent
     * @return available build tools
     */
    Set<BuildTool> getTools();

    /**
     * Command the agent to perform the given job
     * @param job the job to perform
     */
    void perform(Job job) throws Exception;

}
