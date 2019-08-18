package github.scarsz.mori.git;

import github.scarsz.mori.Mori;
import github.scarsz.mori.config.RepositoryConfig;

import java.io.File;
import java.util.Objects;

public class Repository {

    private final String name;
    private final String fullName;
    private final String url;
    private final String cloneUrl;
    private final GitUser owner;
    private final String description;
    private final String language;

    public Repository(String name, String fullName, String url, String cloneUrl, GitUser owner, String description, String language) {
        this.name = name;
        this.fullName = fullName;
        this.url = url;
        this.cloneUrl = cloneUrl;
        this.owner = owner;
        this.description = description;
        this.language = language;
    }

    public RepositoryConfig getConfig() {
        return Mori.INSTANCE.getRepositoryConfig(this);
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUrl() {
        return url;
    }

    public String getCloneUrl() {
        return cloneUrl;
    }

    public GitUser getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public File getDataFolder() {
        return new File("jobs/" + fullName);
    }

    @Override
    public String toString() {
        return "Repository{" +
                "fullName='" + fullName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repository that = (Repository) o;
        return getCloneUrl().equals(that.getCloneUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCloneUrl());
    }

}
