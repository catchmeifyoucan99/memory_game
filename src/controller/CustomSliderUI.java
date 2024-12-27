package controller;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CustomSliderUI extends BasicSliderUI {
    private Image thumbImage;
    private Color trackColor;
    private BufferedImage backgroundColor;

    public CustomSliderUI(JSlider slider, Image thumbImage, Color trackColor, BufferedImage backgroundColor) {
        super(slider);
        this.thumbImage = thumbImage;
        this.trackColor = trackColor; 
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        recalculateIfInsetsChanged(); // Cần gọi lại nếu có thay đổi inset
        recalculateIfOrientationChanged(); // Cần gọi lại nếu có thay đổi orientation
        Rectangle clip = g.getClipBounds();

        if (!clip.intersects(trackRect) && slider.getPaintTrack()) {
            calculateGeometry();
        }
        if (slider.getPaintTrack() && clip.intersects(trackRect)) {
            paintTrack(g);
        }
        if (slider.getPaintTicks() && clip.intersects(tickRect)) {
            paintTicks(g);
        }
        if (slider.getPaintLabels() && clip.intersects(labelRect)) {
            paintLabels(g);
        }
        if (slider.hasFocus() && clip.intersects(focusRect)) {
            paintFocus(g);
        }
        if (clip.intersects(thumbRect)) {
            paintThumb(g);
        }

    }

	@Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(thumbImage, thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height, null);
    }

    @Override
    protected Dimension getThumbSize() {
        return new Dimension(thumbImage.getWidth(null), thumbImage.getHeight(null));
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (backgroundColor != null) {
            g2d.drawImage(backgroundColor, trackRect.x, trackRect.y, trackRect.width + 8, trackRect.height, null);//Background volume slider
        } else {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(trackRect.x, trackRect.y, trackRect.width, trackRect.height);
        }
        
        g2d.setStroke(new BasicStroke(16)); // Độ dày của đường track
        
        int trackLeft = trackRect.x + 13; // Điểm bắt đầu vẽ đường track
        int trackRight = thumbRect.x + thumbRect.width / 2 ; // Điểm kết thúc vẽ đường track
        int trackTop = trackRect.y + trackRect.height / 2; // Vị trí y để vẽ
        
        if (trackLeft < trackRight) {
            g2d.setColor(trackColor); // Màu sắc track
            g2d.drawLine(trackLeft, trackTop, trackRight, trackTop); // Vẽ đường track
        }
    }
   

    @Override
    public void paintFocus(Graphics g) {
    }
    
    
}
