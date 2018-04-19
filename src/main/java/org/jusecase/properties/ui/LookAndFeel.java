package org.jusecase.properties.ui;

import java.awt.*;

public enum LookAndFeel {
    Default(
            new Color(246, 235, 188),
            new Color(230, 230, 255)
    ),
    Darcula(
            new Color(108, 81, 15),
            new Color(41, 94, 0)
    );

    public final Color sparseKeyBackgroundColor;
    public final Color searchHighlightColor;

    LookAndFeel(Color sparseKeyBackgroundColor, Color searchHighlightColor) {
        this.sparseKeyBackgroundColor = sparseKeyBackgroundColor;
        this.searchHighlightColor = searchHighlightColor;
    }
}
