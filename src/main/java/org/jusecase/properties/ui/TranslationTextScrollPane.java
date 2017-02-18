package org.jusecase.properties.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class TranslationTextScrollPane extends JScrollPane {
    private MouseWheelListener[] originalMouseWheelListeners;
    private MouseWheelListener specialMouseWheelListener;
    private boolean enabled;


    public TranslationTextScrollPane(Component component, int vsbPolicy, int hsbPolicy) {
        super(component, vsbPolicy, hsbPolicy);
        originalMouseWheelListeners = getMouseWheelListeners().clone();
        specialMouseWheelListener = new SpecialMouseWheelListener();
        setTextScrollingEnabledInternal(false);
    }

    public void setTextScrollingEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            setTextScrollingEnabledInternal(enabled);
        }
    }

    private void setTextScrollingEnabledInternal(boolean enabled) {
        if (enabled) {
            removeMouseWheelListener(specialMouseWheelListener);
            for (MouseWheelListener mouseWheelListener : originalMouseWheelListeners) {
                addMouseWheelListener(mouseWheelListener);
            }
        } else {
            addMouseWheelListener(specialMouseWheelListener);
            for (MouseWheelListener mouseWheelListener : originalMouseWheelListeners) {
                removeMouseWheelListener(mouseWheelListener);
            }
        }
        this.enabled = enabled;
    }

    class SpecialMouseWheelListener implements MouseWheelListener {

        private JScrollPane parentScrollPane;

        private JScrollPane getParentScrollPane() {
            if (parentScrollPane == null) {
                Component parent = getParent();
                while (!(parent instanceof JScrollPane) && parent != null) {
                    parent = parent.getParent();
                }
                parentScrollPane = (JScrollPane)parent;
            }
            return parentScrollPane;
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            JScrollPane parent = getParentScrollPane();
            if (parent != null) {
                parent.dispatchEvent(cloneEvent(e));
            }
        /*
         * If parent scrollpane doesn't exist, remove this as a listener.
         * We have to defer this till now (vs doing it in constructor)
         * because in the constructor this item has no parent yet.
         */
            else {
                TranslationTextScrollPane.this.removeMouseWheelListener(this);
            }
        }

        private MouseWheelEvent cloneEvent(MouseWheelEvent e) {
            return new MouseWheelEvent(getParentScrollPane(), e.getID(), e
                    .getWhen(), e.getModifiers(), 1, 1, e
                    .getClickCount(), false, e.getScrollType(), e
                    .getScrollAmount(), e.getWheelRotation());
        }
    }
}
