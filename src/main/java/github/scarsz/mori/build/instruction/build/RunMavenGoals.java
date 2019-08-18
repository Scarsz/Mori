package github.scarsz.mori.build.instruction.build;

import com.esotericsoftware.minlog.Log;
import github.scarsz.mori.build.Job;
import github.scarsz.mori.build.instruction.Instruction;
import org.apache.maven.shared.invoker.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

public class RunMavenGoals extends Instruction {

    private static final Invoker INVOKER = new DefaultInvoker();
    private static final File MAVEN_HOME = new File("tools/maven");
    static {
        INVOKER.setMavenHome(MAVEN_HOME);
        INVOKER.setInputStream(null);
        INVOKER.setOutputHandler(line -> Log.info("Agent LOCAL", "Maven > " + line));
        INVOKER.setErrorHandler(line -> Log.error("Agent LOCAL", "Maven > " + line));
//        INVOKER.setLogger(new InvokerLogger() {
//            @Override
//            public void debug(String message) {
//                Log.debug("Agent LOCAL", "Maven > " + message);
//            }
//
//            @Override
//            public void debug(String message, Throwable throwable) {
//                Log.debug("Agent LOCAL", "Maven > " + message, throwable);
//            }
//
//            @Override
//            public boolean isDebugEnabled() {
//                return false;
//            }
//
//            @Override
//            public void info(String message) {
//                Log.info("Agent LOCAL", "Maven > " + message);
//            }
//
//            @Override
//            public void info(String message, Throwable throwable) {
//                Log.info("Agent LOCAL", "Maven > " + message);
//            }
//
//            @Override
//            public boolean isInfoEnabled() {
//                return true;
//            }
//
//            @Override
//            public void warn(String message) {
//                Log.warn("Agent LOCAL", "Maven > " + message);
//            }
//
//            @Override
//            public void warn(String message, Throwable throwable) {
//                Log.warn("Agent LOCAL", "Maven > " + message);
//            }
//
//            @Override
//            public boolean isWarnEnabled() {
//                return true;
//            }
//
//            @Override
//            public void error(String message) {
//                Log.error("Agent LOCAL", "Maven > " + message);
//            }
//
//            @Override
//            public void error(String message, Throwable throwable) {
//                Log.error("Agent LOCAL", "Maven > " + message, throwable);
//            }
//
//            @Override
//            public boolean isErrorEnabled() {
//                return true;
//            }
//
//            @Override
//            public void fatalError(String message) {
//                Log.error("Agent LOCAL", "Maven > " + message);
//            }
//
//            @Override
//            public void fatalError(String message, Throwable throwable) {
//                Log.error("Agent LOCAL", "Maven > " + message, throwable);
//            }
//
//            @Override
//            public boolean isFatalErrorEnabled() {
//                return true;
//            }
//
//            @Override
//            public void setThreshold(int threshold) {
//                return;
//            }
//
//            @Override
//            public int getThreshold() {
//                return InvokerLogger.INFO;
//            }
//        });
    }

    private final String goals;

    public RunMavenGoals(String goals) {
        this.goals = goals;
    }

    @Override
    public int perform(Job job) throws MavenInvocationException {
        DefaultInvocationRequest request = new DefaultInvocationRequest();
//        request.setDebug(true);
        request.setOutputHandler(s -> job.log("Maven", s));
        request.setBatchMode(true);
        request.setJavaHome(new File("tools/jdk8u222-b10"));
        request.setMavenOpts("-Dbuild.number=" + job.getId() + " -Dbuild.origin=Mori");
        Properties properties = new Properties();
        properties.setProperty("build.number", String.valueOf(job.getId()));
        properties.setProperty("built.by", "Mori");
        properties.setProperty("build.origin", job.getUrl());
        request.setProperties(properties);
        request.setBaseDirectory(job.getBuildDirectory());
        request.setGoals(Arrays.asList(goals.split(" ")));
        InvocationResult result = INVOKER.execute(request);
        if (result.getExecutionException() != null) job.error("Error occurred during Maven build", result.getExecutionException());
        return result.getExitCode();
    }

    @NotNull
    @Override
    public String getName() {
        return "Run Maven goal";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Execute the given Maven goals on the workspace";
    }

}
