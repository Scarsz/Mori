package github.scarsz.mori.build;

import java.awt.*;

public enum Result {

    PASS(Color.GREEN),
    FAIL(Color.RED);

    private final Color color;

    Result(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

}
