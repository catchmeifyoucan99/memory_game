package support;

import javax.swing.*;
import java.awt.*;

public class GameThread extends Thread implements Runnable {
	private boolean running = false;
	private int FPS = 0;
	private int fps = 60;
	private int tps = 120;
	int frame = 0;
	private long timer = System.currentTimeMillis();
    JFrame jframe;
    
    public GameThread() {
    	Begin();
    }
    
    public void Begin() {
    	start();
    	running = true;
    }

    @Override
    public void run() {
    	long lastFrameTime = System.nanoTime();
        long lastTickTime = System.nanoTime();
        float nsPerTick = 1e9f / tps;
        float nsPerFrame = 1e9f / fps;  
        long lastTime = System.currentTimeMillis();

        while (running) {
            long now = System.nanoTime();
            if (now - lastFrameTime > nsPerFrame) {
                updateFPS();
                frame++;
                lastFrameTime = now;
            }
            if (System.currentTimeMillis() - lastTime > 1000) {
                FPS = frame;
                frame = 0;
                lastTime = System.currentTimeMillis();
            }
        }
    }
    
    public void updateFPS() {
        frame++;
        if (System.currentTimeMillis() - timer > 1000) {
            FPS = frame;
            frame = 0;
            timer += 1000;
//            getFPS();
        }
    }
//    
//    private void getFPS() {
//        jframe.setTitle(String.format("ROBIN Game - FPS: %d", FPS));
//    }
}
