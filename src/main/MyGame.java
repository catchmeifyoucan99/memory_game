package main;

import support.DatabaseConnector;
import support.GameSen;
import support.GameSound;
import support.SceneManager;

import javax.swing.*;


import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;

public class MyGame extends GameSen {
    private SceneManager sceneManager;
    private GameSound gameSound;

    public MyGame() {
        super(1200, 750); // Set kích thước màn hình
        gameSound = new GameSound();
        gameSound.defaultSound();
        sceneManager = new SceneManager(this, gameSound); // Truyền gameSound vào SceneManager
        
        try {
            Connection connection = DatabaseConnector.getConnection();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       
        this.setResizable(false);
        this.setVisible(true);
    }
    
    public SceneManager getSceneManager() {
        return sceneManager;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Lấy tọa độ chuột
        System.out.println("Chuột được click tại: " + e.getPoint());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Xử lý sự kiện nhấn chuột
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Xử lý sự kiện thả chuột
    }
}
