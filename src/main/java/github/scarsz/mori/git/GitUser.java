package github.scarsz.mori.git;

import java.util.Objects;

public class GitUser {

    private final String name;
    private final String profileUrl;
    private final String avatarUrl;
    private final String email;

    public GitUser(String name, String profileUrl, String avatarUrl, String email) {
        this.name = name;
        this.profileUrl = profileUrl;
        this.avatarUrl = avatarUrl;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitUser gitUser = (GitUser) o;
        return Objects.equals(name, gitUser.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name + "{" +
                "email='" + email + '\'' +
                '}';
    }

}
