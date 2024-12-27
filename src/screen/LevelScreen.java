package screen;

import javax.swing.*;

import model.LevelInfo;
import model.PlayInfo;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import support.DatabaseConnector;
import support.GameSen;
import support.LevelManager;
import support.SceneManager;

public class LevelScreen extends JPanel {
    private BufferedImage background, star, levelImage, lockedLevelImage, back, starZ;
    private List<LevelInfo> levels;
    private int hoveredLevelIndex = -1;
    private GameSen gameSen;
    private Font customFont;
    private int userIDs;
    private int maxTotalStars = -1; 
    private SceneManager sceneManager;
    private PlayScreen playScreen;

    public LevelScreen(GameSen gameSen, SceneManager sceneManager) {
        System.out.println("LevelScreen is running...");
        this.gameSen = gameSen;
        this.sceneManager = sceneManager;
        
        // Initialize other components
        try {
            // Load custom font
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/font.ttf");
            if (stream != null) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(24f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(customFont);
                stream.close();
            } else {
                System.out.println("Font resource not found");
            }
        } catch (IOException | FontFormatException e) {
            System.out.println("Error loading font: " + e.getMessage());
        }

        // Load images
        loadImageBackground();
        getMaxTotalStarsAvailable();
        loadImageLevel();
        loadImageLockLevel();
        loadImageStar();
        loadImageBack();
        loadImageStarZ();
        // Initialize levels
        initLevels();

        // Initialize mouse listeners
        initListeners();
        System.out.println("User ID(LevelScreen1): " + userIDs);
    }

    public void setUserId(int userID) {
        userIDs = userID;
        initLevels();
        getMaxTotalStarsAvailable();
    }
    
    public int getUserId() {
    	return userIDs;
    }

    private void initLevels() {
        levels = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT level, stars_achieved, total_stars_available FROM player_progress WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userIDs);
            ResultSet rs = ps.executeQuery();

            // Thêm màn chơi mặc định
            for (int i = 1; i <= 10; i++) {
                levels.add(new LevelInfo(String.valueOf(i), 0, 3, i == 1)); // Màn 1 mở mặc định, các màn khác khóa
            }

            // Cập nhật thông tin từ cơ sở dữ liệu
            while (rs.next()) {
                int level = rs.getInt("level");
                int stars = rs.getInt("stars_achieved");
                int totalStars = rs.getInt("total_stars_available");
                boolean unlocked = level == 1 || (level > 1 && levels.get(level - 2).getStars() >= 1);

                LevelInfo levelInfo = new LevelInfo(String.valueOf(level), stars, totalStars, unlocked);
                levels.set(level - 1, levelInfo);
            }

            updateLevelUnlockStatus();

            updateUnlockedLevels(conn);
            
            // Cập nhật lại giao diện
            repaint();
            revalidate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void updateLevelUnlockStatus() {
        for (int i = 1; i < levels.size(); i++) {
            LevelInfo previousLevel = levels.get(i - 1);
            LevelInfo currentLevel = levels.get(i);

            // Cập nhật trạng thái mở khóa của màn chơi hiện tại
            if (previousLevel.getStars() >= 1) {
                currentLevel.setUnlocked(true);
            } else {
                currentLevel.setUnlocked(true);
            }
        }
    }


    private void updateUnlockedLevels(Connection conn) throws SQLException {
        String checkUserSql = "SELECT COUNT(*) FROM user WHERE id = ?";
        String checkLevelSql = "SELECT level FROM player_progress WHERE user_id = ? AND level = ?";
        String insertSql = "INSERT INTO player_progress (user_id, level, stars_achieved, total_stars_available) VALUES (?, ?, ?, ?)";

        try (PreparedStatement checkUserPs = conn.prepareStatement(checkUserSql);
             PreparedStatement checkLevelPs = conn.prepareStatement(checkLevelSql);
             PreparedStatement insertPs = conn.prepareStatement(insertSql)) {

            checkUserPs.setInt(1, userIDs);
            ResultSet userRs = checkUserPs.executeQuery();
            if (userRs.next() && userRs.getInt(1) == 0) {
                System.out.println("User ID does not exist.");
                return;
            }

            for (LevelInfo level : levels) {
                if (level.isUnlocked() && level.getStars() == 0) { 
                    checkLevelPs.setInt(1, userIDs);
                    checkLevelPs.setInt(2, Integer.parseInt(level.getLevel()));
                    ResultSet levelRs = checkLevelPs.executeQuery();
                    if (!levelRs.next()) {
                        insertPs.setInt(1, userIDs);
                        insertPs.setInt(2, Integer.parseInt(level.getLevel()));
                        insertPs.setInt(3, 0); 
                        insertPs.setInt(4, 3); 
                        insertPs.executeUpdate();
                    }
                }
            }
        }
    }

    private int getMaxTotalStarsAvailable() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            if (conn == null) {
                System.out.println("Connection is null");
                return maxTotalStars;
            }

            String sql = "SELECT SUM(stars_achieved) as sum FROM player_progress WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userIDs);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                maxTotalStars = rs.getInt("sum");
            } else {
                System.out.println("No data found for user_id: " + userIDs);
            }
            

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxTotalStars;
    }

    // Initialize mouse motion listener
    private void initListeners() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                hoveredLevelIndex = -1;
                int yPosition = 70;
                int xPosition = 195;
                int objectWidth = 75; // Độ rộng của mỗi đối tượng
                int objectHeight = 88; // Độ cao của mỗi đối tượng

                for (int i = 1; i < levels.size(); i++) {
                    int objectX = xPosition; // Vị trí X bắt đầu của đối tượng
                    int objectY = yPosition; // Vị trí Y bắt đầu của đối tượng
                    
                    //Button back
                    if (mouseX >= 30 && mouseX <= 30 + back.getWidth() && mouseY >= 20 && mouseY <= 20 + back.getHeight()) {
                        sceneManager.showScreen(SceneManager.MAIN_SCREEN);
                        return;
                    }

                    // Kiểm tra xem tọa độ click có nằm trong đối tượng thứ i không
                    if (mouseX >= objectX && mouseX <= objectX + objectWidth && mouseY >= objectY && mouseY <= objectY + objectHeight) {
                        hoveredLevelIndex = i;
                        break; // Dừng
                    }

                    // Di chuyển đến hàng tiếp theo nếu đã điền đầy cột
                    xPosition += 180; // Khoảng cách giữa các cột

                    if (xPosition + objectWidth > getWidth() -20) //Chiều ngang list 
                    {
                        xPosition = 195; // Bắt đầu từ đầu cột
                        yPosition += 150; // Di chuyển đến hàng tiếp theo
                    }
                }

                repaint();

                if (hoveredLevelIndex != -1) {
                    LevelInfo selectedLevel = levels.get(hoveredLevelIndex);
                    System.out.println("hoveredLevelIndex: " + hoveredLevelIndex);
                    System.out.println("Level unlocked: " + selectedLevel.isUnlocked());
                    
                    // Check if the level is unlocked before navigating
                    if (selectedLevel.isUnlocked()) {
                        sceneManager.showPlayScreen(hoveredLevelIndex);
                    } else {
                        System.out.println("Level is locked.");
                    }
                }
            }
        });
    }


    // Load background image
    private void loadImageBackground() {
        try {
            background = ImageIO.read(getClass().getResource("/resources/Default.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load level image
    private void loadImageLevel() {
        try {
            levelImage = ImageIO.read(getClass().getResource("/resources/UnClock.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load locked level image
    private void loadImageLockLevel() {
        try {
            lockedLevelImage = ImageIO.read(getClass().getResource("/resources/Locked.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load star for level image
    private void loadImageStar() {
        try {
            star = ImageIO.read(getClass().getResource("/resources/Active.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Load star image
    private void loadImageStarZ() {
        try {
            starZ = ImageIO.read(getClass().getResource("/resources/star.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Load back image
    private void loadImageBack() {
        try {
            back = ImageIO.read(getClass().getResource("/resources/back.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw background
        if (background != null) {
            g2d.drawImage(background, 0, 0, 1200, 750, null);
        }

        int xPosition = 195; // X position of levels
        int yPosition = 70; // Y position of levels
        int imageWidth = 75; // Width of level image
        int imageHeight = 88; // Height of level image
        int textYOffset = -48; // Y offset for text
        int starYOffset = -26; // Y offset for stars
        int columnWidth = 180; // Width of each column
        int starSpacing = 19; // Distance between stars

        // Set custom font and color
        if (customFont != null) {
            g2d.setFont(customFont);
            Color customColor = new Color(0xFDE294);
            g2d.setColor(customColor);
        } else {
            g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        }

        // Loop through all levels
        for (int i = 0; i < levels.size(); i++) {
            LevelInfo level = levels.get(i);
            boolean isLocked = !level.isUnlocked(); // Check if level is locked

            // Draw level image
            if (isLocked) {
                if (lockedLevelImage != null) {
                    g2d.drawImage(lockedLevelImage, xPosition, yPosition, imageWidth, imageHeight, null);
                }
            } else {
                if (levelImage != null) {
                    g2d.drawImage(levelImage, xPosition, yPosition, imageWidth, imageHeight, null);
                }

                // Draw level name only if level is unlocked
                int textWidth = g2d.getFontMetrics().stringWidth(level.getLevel()); // Width of the text
                int textX = xPosition + (imageWidth / 2) - (textWidth / 2); // Center the text
                g2d.drawString(level.getLevel(), textX, yPosition + imageHeight + textYOffset);
            }

            // Draw stars
            for (int j = 0; j < level.getStars(); j++) {
                int starXPosition = xPosition + j * starSpacing + 11; // Set x position of each star with spacing
                int starYPosition = yPosition + imageHeight + starYOffset; // Set y position of each star
                g2d.drawImage(star, starXPosition, starYPosition, 16, 16, null);
            }

            // Move to the next column
            xPosition += columnWidth;

            // Wrap to the next row if the current row is full
            if (xPosition + columnWidth > getWidth()) {
                xPosition = 195; // Start from the beginning of the row
                yPosition += 150; // Move to the next row
            }
        }
        
        // Draw total stars
        String totalStarsText = maxTotalStars  + " / 30";
        int totalStarsX = 1088; // X position
        int totalStarsY = 40; // Y position
        g2d.drawString(totalStarsText, totalStarsX, totalStarsY);    
        if (starZ != null) {
            g2d.drawImage(starZ, 1050, 15 , 30, 30, null);
        }
        
        // Draw back button
        if (back != null) {
            g2d.drawImage(back, 40, 20, null);
        }
    }
}
