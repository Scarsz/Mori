package github.scarsz.mori.build;

import com.esotericsoftware.minlog.Log;
import github.scarsz.mori.Mori;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BuildLog {

    private final List<Entry> entries = new LinkedList<>();
    private final long timestamp = System.currentTimeMillis();
    private final File file;
    private BufferedWriter writer;

    public BuildLog(File file) {
        this.file = file;

        try {
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            writer = null;
            Log.error("Failed to create build log, attempting to save at build finish", e);
        }
    }

    public void append(String... lines) {
        append(Level.INFO, lines);
    }

    public void append(Level level, String... lines) {
        Arrays.stream(lines)
                .map(line -> new Entry(this, level, line))
                .forEach(entry -> {
//                    if (entry.line.contains("null")) {
//                        Arrays.stream(ExceptionUtils.getStackTrace(new Throwable()).split("\n"))
//                                .filter(s -> s.contains(".mori."))
//                                .forEach(Log::info);
//                    }

                    this.entries.add(entry);

                    if (writer != null) {
                        try {
                            writer.append(entry.toString());
                        } catch (IOException e) {
                            Log.error("Failed appending to build log", e);
                        }
                    }
                });
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public List<String> getLines() {
        return entries.stream()
                .map(Entry::toString)
                .collect(Collectors.toList());
    }

    public void save() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                Log.error("Failed closing build log writer", e);
            }

            // ensure there's some content in the file before abandoning the all-at-once save
            if (file.length() > 0) return;
        }

        try {
            FileUtils.writeStringToFile(
                    file,
                    String.join("\n", getLines()),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            Log.error("Failed to save build log to " + file, e);
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    static class Entry {

        private final BuildLog log;
        private final Level level;
        private final String line;
        private final long timestamp;

        Entry(BuildLog log, String line) {
            this.log = log;
            this.level = Level.INFO;
            this.line = line;
            this.timestamp = System.currentTimeMillis();
        }

        Entry(BuildLog log, Level level, String line) {
            this.log = log;
            this.level = level;
            this.line = line;
            this.timestamp = System.currentTimeMillis();
        }

        public String getLine() {
            return line;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return ((String) Mori.INSTANCE.getConfig().get("build.log.format"))
                    .replace("{timestamp}", new SimpleDateFormat(Mori.INSTANCE.getConfig().get("build.log.timestamp")).format(new Date()))
                    .replace("{elapsed}", "T+" + (this.timestamp - log.timestamp) + "ms")
                    .replace("{level}", level.getName())
                    .replace("{line}", StringUtils.isNotBlank(line) ? line : "");
        }

    }

}
