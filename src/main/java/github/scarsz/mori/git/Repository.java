package github.scarsz.mori.git;

public class Repository {

    private final String name;
    private final String fullName;
    private final String url;
    private final String cloneUrl;
    private final User owner;
    private final String description;
    private final String language;

    public Repository(String name, String fullName, String url, String cloneUrl, User owner, String description, String language) {
        this.name = name;
        this.fullName = fullName;
        this.url = url;
        this.cloneUrl = cloneUrl;
        this.owner = owner;
        this.description = description;
        this.language = language;
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

    public User getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public String toString() {
        return "Repository{" +
                "fullName='" + fullName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

}
