package github.scarsz.mori.build;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum BuildTool {

    JAVA_JDK_8("java8", "jdk8"),
    JAVA_JDK_9("java9", "jdk9"),
    JAVA_JDK_10("java10", "jdk10"),
    JAVA_JDK_11("java11", "jdk11"),
    JAVA_JDK_12("java12", "jdk12"),

    MAVEN,
    GRADLE;

    private final Set<String> aliases;

    BuildTool(String... aliases) {
        this.aliases = new HashSet<>(Arrays.asList(aliases));
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public static BuildTool of(String name) throws IllegalArgumentException {
        if (StringUtils.isBlank(name)) throw new IllegalArgumentException("Blank build tool name given");

        for (BuildTool tool : values()) {
            if (tool.name().equalsIgnoreCase(name) ||
                    tool.aliases.stream().anyMatch(s -> s.equalsIgnoreCase(name)))
                return tool;
        }

        throw new IllegalArgumentException("Invalid build tool name given: " + name);
    }

}
