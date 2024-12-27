package support;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class GameSen extends ScreenSetting implements MouseListener {

    public Cursor customCursor;
    private Cursor defaultCursor, pointerCursor;

    public GameSen(int width, int height) {
        super(width, height);
        initCursors();
        this.addMouseListener(this);
    }

    private void initCursors() {
        try {
            BufferedImage cursorImage = ImageIO.read(getClass().getResource("/resources/cursor.png"));
            customCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "Custom Cursor");

            BufferedImage pointerCursorImage = ImageIO.read(getClass().getResource("/resources/pointer.png"));
            pointerCursor = Toolkit.getDefaultToolkit().createCustomCursor(pointerCursorImage, new Point(0, 0), "Pointer Cursor");

            BufferedImage defaultCursorImage = ImageIO.read(getClass().getResource("/resources/cursor.png"));
            defaultCursor = Toolkit.getDefaultToolkit().createCustomCursor(defaultCursorImage, new Point(0, 0), "Default Cursor");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.setCursor(customCursor);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.setCursor(Cursor.getDefaultCursor());
    }

    public Cursor getDefaultCursor() {
        return defaultCursor;
    }

    public Cursor getPointerCursor() {
        return pointerCursor;
    }
}
