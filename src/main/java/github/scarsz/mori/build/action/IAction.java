package github.scarsz.mori.build.action;

import github.scarsz.mori.git.Repository;

public interface IAction {

    Repository getRepository();
    String getBranch();

}
