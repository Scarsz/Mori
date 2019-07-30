package github.scarsz.mori.config;

import alexh.weak.Dynamic;
import com.esotericsoftware.minlog.Log;
import github.scarsz.mori.Mori;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Configuration {

    private final Yaml yaml;
    private final File source;
    private final Map<String, Object> values = new HashMap<>();
    private final Map<String, Object> defaults = new HashMap<>();

    public Configuration(File source) {
        this.yaml = new Yaml();
        this.source = source;
        loadDefaults();
    }

    public void saveDefaultConfig() throws IOException {
        saveDefaultConfig(false);
    }

    public void saveDefaultConfig(boolean overwrite) throws IOException {
        if (!source.exists() || overwrite) {
            long timer = System.currentTimeMillis();
            FileUtils.copyInputStreamToFile(Mori.class.getResourceAsStream("/config.yml"), source);
            Log.info("Saved default config in " + (System.currentTimeMillis() - timer) + "ms");
        }
    }

    public void loadDefaults() {
        try {
            defaults.putAll(
                    yaml.load(
                            IOUtils.toString(
                                    Mori.class.getResourceAsStream("/config.yml"), StandardCharsets.UTF_8
                            )
                    )
            );
        } catch (IOException e) {
            Log.error("Failed to load default values: " + e.getMessage());
        }
    }

    public void load() throws IOException {
        saveDefaultConfig();
        synchronized (values) {
            long timer = System.currentTimeMillis();
            values.clear();
            values.putAll(yaml.load(FileUtils.readFileToString(source, "UTF-8")));
            Log.info("Loaded config in " + (System.currentTimeMillis() - timer) + "ms");
        }
    }

    private static final Pattern KEY_PATTERN = Pattern.compile("^[A-z0-9.]+$");

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        if (!KEY_PATTERN.matcher(key).matches()) throw new IllegalArgumentException("Invalid config key given: " + key);

        Dynamic value = Dynamic.from(values).dget(key);
        if (value.isPresent()) {
            return (T) value.asObject();
        } else {
            value = Dynamic.from(defaults).dget(key);
            if (value.isPresent()) {
                return (T) value.asObject();
            } else {
                throw new IllegalArgumentException("Unknown config key " + key);
            }
        }
    }

}
