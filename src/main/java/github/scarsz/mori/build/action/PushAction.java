package github.scarsz.mori.build.action;

import github.scarsz.mori.git.GitUser;
import github.scarsz.mori.git.Commit;
import github.scarsz.mori.git.Repository;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PushAction implements IGitUserAction, IAction {

    private final Repository repository;
    private final String branch;
    private final GitUser user;
    private final List<Commit> commits;
    private final String beforeHash;
    private final String afterHash;

    public PushAction(Repository repository, String branch, GitUser user, List<Commit> commits, String beforeHash, String afterHash) {
        this.repository = repository;
        this.branch = branch;
        this.user = user;
        this.commits = commits;
        this.beforeHash = beforeHash;
        this.afterHash = afterHash;
    }

    public Repository getRepository() {
        return repository;
    }

    public String getBranch() {
        return branch;
    }

    public GitUser getUser() {
        return user;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public String getBeforeHash() {
        return beforeHash;
    }

    public String getAfterHash() {
        return afterHash;
    }

    public static class Builder {

        private Repository repository = null;
        private String branch = null;
        private GitUser user = null;
        private List<Commit> commits = new LinkedList<>();
        private String beforeHash = null;
        private String afterHash = null;

        public Builder repository(Repository repository) {
            this.repository = Objects.requireNonNull(repository);
            return this;
        }

        public Builder branch(String branch) {
            this.branch = Objects.requireNonNull(branch);
            return this;
        }

        public Builder user(GitUser user) {
            this.user = Objects.requireNonNull(user);
            return this;
        }

        public Builder commits(Commit... commits) {
            Objects.requireNonNull(commits);
            Arrays.stream(commits)
                    .filter(Objects::nonNull)
                    .forEach(this.commits::add);
            return this;
        }

        public Builder commits(List<Commit> commits) {
            Objects.requireNonNull(commits);
            commits.stream()
                    .filter(Objects::nonNull)
                    .forEach(this.commits::add);
            return this;
        }

        public Builder hashes(String before, String after) {
            this.beforeHash = Objects.requireNonNull(before);
            this.afterHash = Objects.requireNonNull(after);
            return this;
        }

        public PushAction build() {
            if (repository == null) throw new IllegalStateException("Repository object was not set before building");
            if (branch == null) throw new IllegalStateException("Branch object was not set before building");
            if (user == null) throw new IllegalStateException("Author object was not set before building");
            if (commits == null || commits.size() == 0) throw new IllegalStateException("Commits list was empty before building");
            if (beforeHash == null) throw new IllegalStateException("BeforeHash object was not set before building");
            if (afterHash == null) throw new IllegalStateException("AfterHash object was not set before building");

            return new PushAction(
                    repository,
                    branch,
                    user,
                    commits,
                    beforeHash,
                    afterHash
            );
        }

    }

    @Override
    public String toString() {
        return "PushAction{" +
                "repository=" + repository +
                ", branch='" + branch + '\'' +
                ", user=" + user +
                ", commits=" + commits.size() +
                ", hash='" + afterHash + '\'' +
                '}';
    }

}
