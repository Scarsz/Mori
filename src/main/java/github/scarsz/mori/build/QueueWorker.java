package github.scarsz.mori.build;

import com.esotericsoftware.minlog.Log;
import github.scarsz.mori.Mori;
import github.scarsz.mori.build.action.IAction;
import github.scarsz.mori.build.agent.IAgent;
import github.scarsz.mori.error.IncompatibleAgentsException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class QueueWorker extends Thread {

    private static final ExecutorService SERVICE = Executors.newCachedThreadPool();

    public QueueWorker() {
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            Map.Entry<IAction, CompletableFuture<Job>> next;
            try {
                next = getNextTask();
            } catch (InterruptedException e) {
                Log.error("Queue worker was interrupted: " + e);
                return;
            }

            IAction action = next.getKey();
            CompletableFuture<Job> future = next.getValue();

            Set<IAgent> agents = Mori.INSTANCE.getAgents().stream()
                    .filter(agent -> agent.isCapable(action))
                    .collect(Collectors.toSet());
            if (agents.size() == 0) {
                Log.error("No agent fulfils tooling requirements for " + action + ". Skipping.");
                future.completeExceptionally(new IncompatibleAgentsException());
                continue;
            }

            IAgent agent = agents.iterator().next();
            try {
                Job job = new Job(action);
                SERVICE.execute(() -> {
                    try {
                        agent.perform(job);
                    } catch (Exception e) {
                        job.setResult(Result.FAIL, e);
                        Log.error("Error occurred while " + agent + " was performing job " + job, e);
                    }
                });
                future.complete(job);
            } catch (Exception e) {
                future.completeExceptionally(e);
                Log.error("Failed to submit job to queue executor service", e);
            }
        }
    }

    private Map.Entry<IAction, CompletableFuture<Job>> getNextTask() throws InterruptedException {
        synchronized (this) {
            while (Mori.INSTANCE.getAgents().size() == 0 || getQueue().size() == 0) {
                this.wait();
            }

            synchronized (getQueue()) {
                Map.Entry<IAction, CompletableFuture<Job>> next = getQueue().entrySet().iterator().next();
                getQueue().entrySet().remove(next);
                return next;
            }
        }
    }

    private Map<IAction, CompletableFuture<Job>> getQueue() {
        return Mori.INSTANCE.getQueue();
    }

}
