package org.jusecase.properties.ui;

import java.awt.*;

public enum LookAndFeel {
    Default(new Color(248, 215, 218),
            new Color(246, 235, 188),
            new Color(230, 230, 255)
    );

    public final Color devKeyBackgroundColor;
    public final Color sparseKeyBackgroundColor;
    public final Color searchHighlightColor;

    LookAndFeel( Color devKeyBackgroundColor, Color sparseKeyBackgroundColor, Color searchHighlightColor ) {
        this.devKeyBackgroundColor = devKeyBackgroundColor;
        this.sparseKeyBackgroundColor = sparseKeyBackgroundColor;
        this.searchHighlightColor = searchHighlightColor;
    }
}
