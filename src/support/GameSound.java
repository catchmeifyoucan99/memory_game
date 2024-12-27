package support;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

public class GameSound extends JPanel {
    private Clip clipSound, clickSound;
    private JSlider volumeSlider;
    private JButton toggleSoundButton;

    // Khởi tạo âm thanh nền
    public void defaultSound() {
        try {
            URL soundURL = getClass().getResource("/sound/musicbg.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            clipSound = AudioSystem.getClip();
            clipSound.open(audioIn);
            clipSound.start();
            clipSound.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public GameSound() {
        setLayout(new BorderLayout());

        // Toggle Sound Button
        toggleSoundButton = new JButton("Toggle Sound");
        toggleSoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSound();
            }
        });
        add(toggleSoundButton, BorderLayout.NORTH);

        // Volume Slider
        volumeSlider = new JSlider(65, 100); // Khởi tạo thanh trượt với giá trị từ 0 đến 100
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int volume = volumeSlider.getValue();

                setVolume(volume);
            }
        });
        add(volumeSlider, BorderLayout.CENTER);
    }

    public void toggleSound() {
        if (clipSound != null) {
            if (clipSound.isActive()) {
                clipSound.stop();
            } else {
                clipSound.start();
                clipSound.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    public JSlider getVolumeSlider() {
        return volumeSlider;
    }

    public void setVolume(int volume) {
        if (clipSound != null && clipSound.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volumeControl = (FloatControl) clipSound.getControl(FloatControl.Type.MASTER_GAIN);
            float minVol = volumeControl.getMinimum();
            float maxVolume = volumeControl.getMaximum();
            float newVolume = minVol + (maxVolume - minVol) * volume / 100;
            volumeControl.setValue(newVolume);
        }
    }
    
    public void clickSound()
    {
    	try {
            URL soundURL = getClass().getResource("/sound/defaultClick.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            clickSound = AudioSystem.getClip();
            clickSound.open(audioIn);
            clickSound.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
    public void playSound() {
        if (clipSound != null && !clipSound.isRunning()) {
            clipSound.start();
            clipSound.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopSound() {
        if (clipSound != null && clipSound.isRunning()) {
            clipSound.stop();
        }
    }
}
