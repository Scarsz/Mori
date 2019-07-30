package github.scarsz.mori.git;

public class User {

    private final String name;
    private final String profileUrl;
    private final String avatarUrl;
    private final String email;

    public User(String name, String profileUrl, String avatarUrl, String email) {
        this.name = name;
        this.profileUrl = profileUrl;
        this.avatarUrl = avatarUrl;
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}
