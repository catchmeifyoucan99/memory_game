package screen;

import javax.imageio.ImageIO;
import javax.swing.*;

import controller.Rate;
import controller.SoundPanel;
import support.GameSound;
import support.SceneManager;
import support.GameSen;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MainScreen extends JPanel implements LoginListener {
    private BufferedImage btnPlaynow;
    private boolean isHovered;
    private BufferedImage titleGame;
    private BufferedImage backGround, backGroundZ;
    private BufferedImage btnSetting;
    private BufferedImage btnAccount;
    private BufferedImage btnRate;
    private Runnable onPlayNowClicked;
    private SceneManager sceneManager;
    private GameSound gameSound;
    private JFrame parentFrame;
    private boolean isLoggedIn = false; // Kiểm tra đăng nhập

    public MainScreen(GameSen gameSen, SceneManager sceneManager, GameSound gameSound, JFrame parentFrame) {
        System.out.println("MainScreen is running...");
        this.sceneManager = sceneManager;
        this.gameSound = gameSound;
        this.parentFrame = parentFrame;
        getImage();
        initializeComponents();
        initListeners(gameSen);
    }

    private void initializeComponents() {
        setLayout(null);
    }

    public void initListeners(GameSen gameSen) {
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                boolean wasHovered = isHovered;
                //Play button ==> hover
                isHovered = isHover(mouseX, mouseY, 490, 450, 200, 90); //Set location hover (thisX = drawX [location button]; thisY = drawY [location button])
                if (wasHovered != isHovered) {
                    gameSen.setCursor(isHovered ? gameSen.getPointerCursor() : gameSen.getDefaultCursor());
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isHover(e.getX(), e.getY(), 490, 450, 200, 90)) {
                    if (isLoggedIn) { // Kiểm tra đã đăng nhập hay chưa
                        if (onPlayNowClicked != null) {
                            onPlayNowClicked.run();
                        } else {
                            gameSound.clickSound();
                            sceneManager.showScreen(SceneManager.LEVEL_SCREEN);
                        }
                    } else {
                        JOptionPane.showMessageDialog(MainScreen.this, "Bạn cần đăng nhập trước khi chơi.");
                    }
                }

                //Setting button ==> Setting volume
                else if (isHover(e.getX(), e.getY(), 1090, 45, 37, 37)) {
                    gameSound.clickSound();
                    SoundPanel soundPanel = new SoundPanel(parentFrame, gameSound);
                    soundPanel.setVisible(true);
                }

                //Account button ==> Login panel
                else if (isHover(e.getX(), e.getY(), 1090, 94, 37, 37)) {
                    gameSound.clickSound();
                    LoginScreen loginScreen = new LoginScreen(parentFrame); // Tạo mới LoginScreen
                    loginScreen.addLoginListener(MainScreen.this); // Thêm LoginListener
                    loginScreen.setVisible(true);
                }
                
                //Rate button 
                else if (isHover(e.getX(), e.getY(), 1090, 144, 37, 37)) {
                    gameSound.clickSound();
                    Rate rate = new Rate(parentFrame); // Tạo mới LoginScreen
                    rate.setVisible(true);
                }
            }
        });
    }

    public void getImage() {
        try {
            backGround = ImageIO.read(getClass().getResource("/resources/Default.png"));
            backGroundZ = ImageIO.read(getClass().getResource("/resources/Shadow.png"));
            titleGame = ImageIO.read(getClass().getResource("/resources/Title.png"));
            btnPlaynow = ImageIO.read(getClass().getResource("/resources/playNow.png"));
            btnSetting = ImageIO.read(getClass().getResource("/resources/Setting.png"));
            btnAccount = ImageIO.read(getClass().getResource("/resources/User.png"));
            btnRate = ImageIO.read(getClass().getResource("/resources/Rate.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (backGround != null) {
            g2d.drawImage(backGround, 0, 0, 1200, 750, null);
        } else {
            g2d.drawImage(backGroundZ, 0, 0, 1200, 750, null);
        }

        if (titleGame != null) {
            g2d.drawImage(titleGame, 320, 0, 550, 400, null);
        }

        if (btnPlaynow != null) {
            int drawX = 490; // Set location button
            int drawY = 450;
            drawX += isHovered ? 3 : 0;
            drawY += isHovered ? 3 : 0;
            g2d.drawImage(btnPlaynow, drawX, drawY, 200, 90, null);
        }

        if (btnSetting != null) {
            g2d.drawImage(btnSetting, 1090, 45, 37, 37, null);
        }

        if (btnAccount != null) {
            g2d.drawImage(btnAccount, 1090, 94, 37, 37, null);
        }

        if (btnRate != null) {
            g2d.drawImage(btnRate, 1090, 144, 37, 37, null);
        }
    }

    private boolean isHover(int getMouseX, int getMouseY, int thisX, int thisY, int width, int height) {
        return getMouseX >= thisX && getMouseX <= thisX + width && getMouseY >= thisY && getMouseY <= thisY + height;
    }

    public void setOnPlayNowClicked(Runnable onPlayNowClicked) {
        this.onPlayNowClicked = onPlayNowClicked;
    }

    @Override
    public void onLoginSuccess() {
        this.isLoggedIn = true;

        // Sau khi đăng nhập thành công, có thể cập nhật lại giao diện hoặc hiển thị LevelScreen
        // Ví dụ:
        sceneManager.showScreen(SceneManager.LEVEL_SCREEN);
    }
}
