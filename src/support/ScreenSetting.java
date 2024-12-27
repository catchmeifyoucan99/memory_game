package support;

import javax.swing.*;
import java.awt.*;

public class ScreenSetting extends JFrame {
	
	public static int CUSTOM_WIDTH = 1200;
	
    public static int CUSTOM_HEIGHT = 750;
    
    public static int DEFAULT_WIDTH = 1200, DEFAULT_HEIGHT = 750;
    
    private int currentFPS = 0;
    
    public ScreenSetting(int width, int height) {
    	this.CUSTOM_WIDTH = width;
        this.CUSTOM_HEIGHT = height;
        DEFAULT_WIDTH = CUSTOM_WIDTH;
        DEFAULT_HEIGHT = CUSTOM_HEIGHT;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - width) / 2; 
        int y = (screenSize.height - height) / 2;
        setLocation(x, y);
        
        InitScreen();
    }
    
    private void InitScreen() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(CUSTOM_WIDTH, CUSTOM_HEIGHT);
        setTitle("ROBIN Game");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/logo.png"))); // Icon logo
        setVisible(true);
    }
}
