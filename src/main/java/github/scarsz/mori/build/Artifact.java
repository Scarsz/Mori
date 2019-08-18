package github.scarsz.mori.build;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Artifact {

    private final Job job;
    private final File target;
    private final File stashed;

    public Artifact(Job job, File target) {
        this.job = job;
        this.target = target;
        this.stashed = new File(
                target.getAbsolutePath().replace(
                        job.getBuildDirectory().getAbsolutePath(),
                        job.getArtifactDirectory().getAbsolutePath()
                )
        );
    }

    public void stash() {
        if (!stashed.getParentFile().exists() && !stashed.getParentFile().mkdirs()) {
            job.error("Stashing artifacts", "Failed to create directory " + stashed.getParentFile().getPath());
        }

        try {
            FileUtils.copyFile(target, stashed);
        } catch (IOException e) {
            job.error("Stashing artifacts", "Failed to copy artifact " + target.getPath() + " to " + stashed.getPath(), e);
        }
    }

    public String getName() {
        return target.getName();
    }

    public String getTargetPath() {
        return target.getAbsolutePath().replace(job.getBuildDirectory().getAbsolutePath(), "");
    }

    public String getStashedPath() {
        return stashed.getAbsolutePath().replace(job.getArtifactDirectory().getAbsolutePath(), "");
    }

    public long getSize() {
        return target.length();
    }

    public String getUrl() {
        return "https://ci.scarsz.me/job/" + job.getRepository().getFullName() + "/" + job.getId() + "/artifacts" + getStashedPath();
    }

}
