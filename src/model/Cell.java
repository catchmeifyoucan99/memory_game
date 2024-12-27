package model;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Cell {
    private Rectangle rectangle;
    private BufferedImage image;
    private boolean isFlipped = false;
    private boolean isMatched = false;

    public Cell(Rectangle rectangle, BufferedImage image) {
        this.rectangle = rectangle;
        this.image = image;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public BufferedImage getImage() {
        return image;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }
}

