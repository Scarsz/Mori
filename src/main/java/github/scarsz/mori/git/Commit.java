package github.scarsz.mori.git;

import java.util.List;

public class Commit {

    private final String id;
    private final String message;
    private final String url;
    private final long timestamp;
    private final GitUser author;
    private final GitUser committer;
    private final List<String> added;
    private final List<String> removed;
    private final List<String> modified;

    public Commit(String id, String message, String url, long timestamp, GitUser author, GitUser committer,
                  List<String> added, List<String> removed, List<String> modified) {
        this.id = id;
        this.message = message;
        this.url = url;
        this.timestamp = timestamp;
        this.author = author;
        this.committer = committer;
        this.added = added;
        this.removed = removed;
        this.modified = modified;
    }

    public String getId() {
        return id;
    }

    public String getShortId() {
        return id.substring(0, 6);
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public GitUser getAuthor() {
        return author;
    }

    public GitUser getCommitter() {
        return committer;
    }

    public List<String> getAdded() {
        return added;
    }

    public List<String> getRemoved() {
        return removed;
    }

    public List<String> getModified() {
        return modified;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "id='" + getShortId() + '\'' +
                ", message='" + message + '\'' +
                ", author=" + author +
                ", timestamp=" + timestamp +
                ", url=" + url +
                '}';
    }

}
