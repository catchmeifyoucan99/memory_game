package controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import support.GameSound;

public class SoundPanel extends JDialog {
    private GameSound gameSound;
    private BufferedImage backgroundImage, soundOffImage, soundOnImage, closeImage, thumbImage, backgroundColor;
    private JButton closeButton, toggleSoundButton;
    private JSlider volumeSlider;
    private JPanel contentPanel;
    private boolean isSoundOn = true;

    public SoundPanel(JFrame parentFrame, GameSound gameSound) {
        super(parentFrame, true);
        this.gameSound = gameSound;
        
        loadBackgroundImage();
        initializeComponents();
        
        setSize(500, 300); // size
        setResizable(false); //no bg move
        setLocationRelativeTo(parentFrame);
        setUndecorated(true); //no decorate
        setBackground(new Color(0, 0, 0, 0)); //transparent bg
    }

    //get image
    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/resources/Begin.png"));
            soundOnImage = ImageIO.read(getClass().getResource("/resources/soundOn.png"));
            soundOffImage = ImageIO.read(getClass().getResource("/resources/soundOff.png"));
            closeImage = ImageIO.read(getClass().getResource("/resources/close1.png"));
            thumbImage = ImageIO.read(getClass().getResource("/resources/Thumb.png")); // Use the correct thumb image
            backgroundColor = ImageIO.read(getClass().getResource("/resources/03_Straight.png"));
            
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load background image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            backgroundImage = null;
        }
    }

    //set inside of panel
    private void initializeComponents() {
        //background
        contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        contentPanel.setLayout(null);
        setContentPane(contentPanel);

        // Close button
        closeButton = new JButton(new ImageIcon(closeImage));
        closeButton.setBounds(410, 40, closeImage.getWidth(), closeImage.getHeight()); // Adjust position and size as needed
        closeButton.addActionListener(e -> setVisible(false));
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        contentPanel.add(closeButton);

        // Toggle sound button
        toggleSoundButton = new JButton(new ImageIcon(soundOnImage));
        toggleSoundButton.setBounds(80, 72, soundOnImage.getWidth(), soundOnImage.getHeight());
        toggleSoundButton.setContentAreaFilled(false);
        toggleSoundButton.setBorderPainted(false);
        toggleSoundButton.setFocusPainted(false);
        
        toggleSoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSound();  
            }
        });
        contentPanel.add(toggleSoundButton);

        // Volume slider
        Color trackColor = new Color(253, 226, 148);
        volumeSlider = gameSound.getVolumeSlider();
        volumeSlider.setBounds(150, 68, 200, 50); 
        volumeSlider.setUI(new CustomSliderUI(volumeSlider, thumbImage, trackColor, backgroundColor));
        volumeSlider.setOpaque(false); //background transparent
        contentPanel.add(volumeSlider);
    }

    private void toggleSound() {
        gameSound.toggleSound();
        isSoundOn = !isSoundOn;
        gameSound.clickSound();
        toggleSoundButton.setIcon(new ImageIcon(isSoundOn ? soundOnImage : soundOffImage));
    }
}
