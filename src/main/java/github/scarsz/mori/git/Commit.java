package github.scarsz.mori.git;

import java.util.List;

public class Commit {

    private final String id;
    private final String message;
    private final long timestamp;
    private final User author;
    private final User committer;
    private final List<String> added;
    private final List<String> removed;
    private final List<String> modified;

    public Commit(String id, String message, long timestamp, User author, User committer,
                  List<String> added, List<String> removed, List<String> modified) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.author = author;
        this.committer = committer;
        this.added = added;
        this.removed = removed;
        this.modified = modified;
    }

    public String getShortId() {
        return id.substring(0, 6);
    }

    @Override
    public String toString() {
        return "Commit{" +
                "id='" + getShortId() + '\'' +
                ", message='" + message + '\'' +
                ", author=" + author +
                ", timestamp=" + timestamp +
                '}';
    }

}
