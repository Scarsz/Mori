package github.scarsz.mori.build.instruction.deploy;

import com.esotericsoftware.wildcard.Paths;
import github.scarsz.mori.build.Artifact;
import github.scarsz.mori.build.Job;
import github.scarsz.mori.build.instruction.Instruction;
import net.lingala.zip4j.ZipFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ArtifactInstruction extends Instruction {

    private final String target;

    public ArtifactInstruction(String target) {
        this.target = target;
    }

    @NotNull
    @Override
    public String getName() {
        return "Save artifact";
    }
    @NotNull
    @Override
    public String getDescription() {
        return "Save a build artifact to be available for download";
    }

    @Override
    public int perform(Job job) throws Exception {
        Paths paths = new Paths(job.getBuildDirectory().getAbsolutePath(), target);
        Set<Artifact> artifacts = new HashSet<>();

        for (String dirPath : paths.dirsOnly()) {
            File dir = new File(dirPath);
            File zip = new File(dir.getParentFile(), dir.getName() + ".zip");
            job.info("Zipping " + dir.getPath() + " to " + zip.getName() + " & marking as artifact");
            ZipFile zipFile = new ZipFile(zip);
            zipFile.addFolder(dir);
            artifacts.add(new Artifact(job, zip));
        }

        for (String filePath : paths.filesOnly()) {
            File file = new File(filePath);
            job.info("Marking " + file.getPath() + " as artifact");
            artifacts.add(new Artifact(job, file));
        }

        job.getArtifacts().addAll(artifacts);

        if (artifacts.size() > 0) {
            artifacts.forEach(Artifact::stash);
            return 0;
        } else {
            job.error("No artifacts gathered for search string \"" + target + "\"");
            return 1;
        }
    }

}
