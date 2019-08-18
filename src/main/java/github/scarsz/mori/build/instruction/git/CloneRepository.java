package github.scarsz.mori.build.instruction.git;

import github.scarsz.mori.build.Job;
import github.scarsz.mori.build.instruction.Instruction;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static java.util.Collections.singleton;

public class CloneRepository extends Instruction {

    @NotNull
    @Override
    public String getName() {
        return "Clone repository to build directory";
    }
    @NotNull
    @Override
    public String getDescription() {
        return "This instruction will clone the repository for this job using the repository's clone URL.";
    }

    @Override
    public int perform(Job job) throws Exception {
        Git.cloneRepository()
                .setURI(job.getRepository().getCloneUrl())
                .setDirectory(job.getBuildDirectory())
                .setCloneSubmodules(true)
                .setCallback(new CloneCommand.Callback() {
                    @Override public void initializedSubmodules(Collection<String> submodules) { job.info("Initialized submodules " + submodules); }
                    @Override public void cloningSubmodule(String path) { job.info("Cloning submodule " + path); }
                    @Override public void checkingOut(AnyObjectId commit, String path) { job.info("Checking out " + commit.abbreviate(7)); }
                })
                .setProgressMonitor(new TextProgressMonitor() {
                    @Override protected void onUpdate(String taskName, int current) {}
                    @Override protected void onUpdate(String taskName, int current, int total, int percent) {}
                    @Override protected void onEndTask(String taskName, int current) {}
                    @Override protected void onEndTask(String taskName, int current, int total, int percent) {
                        job.info("Git", "Finished with " + taskName.toLowerCase());
                    }
                })
                .setBranchesToClone(singleton("refs/heads/" + job.getAction().getBranch()))
                .setBranch("refs/heads/" + job.getAction().getBranch())
                .call()
                .close();

        return 0;
    }

//    private static void moveAllToParent(File target) {
//        boolean matchingNameFound = false;
//
//        File[] files = target.listFiles();
//        if (files == null) return;
//        for (File file : files) {
//            File newFile = new File(target.getParentFile(), file.getName());
//            if (file.isDirectory()) {
//                if (!newFile.mkdir()) {
//                    Log.error("Failed to create new folder " + newFile.getPath());
//                } else {
//                    if (!file.renameTo(newFile)) {
//                        Log.error("Failed to move folder " + file.getPath());
//                    }
//                }
//                if (file.getName().equals(target.getName())) matchingNameFound = true;
//            } else {
//                if (!file.renameTo(newFile)) {
//                    Log.error("Failed to move file " + file.getPath());
//                }
//            }
//        }
//
//        if (!matchingNameFound) {
//            if (!target.delete()) {
//                Log.error("Failed to delete " + target);
//            }
//        }
//    }

//    private static void moveAllToParent(File target) throws IOException {
//        boolean matchingNameFound = false;
//
//        File[] files = target.listFiles();
//        if (files == null) return;
//        for (File file : files) {
//            if (file.isDirectory()) {
//                FileUtils.moveDirectoryToDirectory(file, target.getParentFile(), true);
//                if (file.getName().equals(target.getName())) matchingNameFound = true;
//            } else {
//                FileUtils.moveFileToDirectory(file, target.getParentFile(), true);
//            }
//        }
//
//        if (!matchingNameFound) {
//            if (!target.delete()) {
//                Log.error("Failed to delete " + target);
//            }
//        }
//    }

}
