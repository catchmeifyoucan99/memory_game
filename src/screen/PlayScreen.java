package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import model.Cell;
import model.LevelInfo;
import model.PlayInfo;
import support.DatabaseConnector;
import support.GameSen;
import support.LevelManager;
import support.SceneManager;

public class PlayScreen extends JPanel {
    private GameSen gameSen;
    private BufferedImage background, time, pause, resume, continuE, btnlevel, backgroundZ, star, unStar, idea;
    private SceneManager sceneManager;
    private LevelInfo levelInfo;
    private Font customFont;
    private boolean isPaused = false;
    private boolean isFinal = false;
    private int levelIndex = 0;
    private LevelManager levelManager = new LevelManager();
    private PlayInfo playInfo;
    private List<Cell> cells = new ArrayList<>();
    private List<BufferedImage> images = new ArrayList<>();
    private Cell firstSelectedCell = null;
    private Cell secondSelectedCell = null;
    private int userId;
    
    private int remainingTime;
    private Timer timer;

    public PlayScreen(GameSen gameSen,SceneManager sceneManager, LevelScreen levelScreen) {
        System.out.println("PlayScreen is running...");
        this.gameSen = gameSen;
        this.sceneManager = sceneManager;
        
        // Font
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
        loadImageBackgroundZ();
        loadImageTime();
        loadImageComplete();
        loadImageOptions();
        loadImageIdea();
        // Initialize timer
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingTime > 0) {
                    remainingTime--;
                } else {
                    timer.stop();
                    endGame();
                }
                repaint();
            }
        });

        initListeners(gameSen);
    }

    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
        System.out.println(levelIndex);
        List<PlayInfo> levelList = LevelManager.getLevelList();

        if (levelIndex > 0 && levelIndex <= levelList.size()) {
            playInfo = levelList.get(levelIndex - 1);
            setPlayInfo(new PlayInfo(playInfo.getLevel(), playInfo.getNumCells(), playInfo.getTime()));
            System.out.println("Level " + playInfo.getLevel() + ":");
            System.out.println("Number of Cells: " + playInfo.getNumCells());
            System.out.println("Time: " + playInfo.getTime());

            loadCellImages(playInfo.getNumCells());

            createCells(playInfo.getNumCells());

            remainingTime = playInfo.getTime();
            timer.start();
        } else {
            System.out.println("Invalid level index");
        }
    }

    private void createCells(int numCells) {
        cells.clear();
        if (numCells > 0) {
            int cellSize = 0; 
            int padding = 0; 
            int startX = 0; 
            int startY = 0;

            if (numCells == 4) { //1
                startX = 450;
                startY = 200;
                cellSize = 130; 
                padding = 10;  
            }
            if (numCells == 6) { //2
                startX = 380;
                startY = 200;
                cellSize = 130; 
                padding = 10;  
            }
            if (numCells == 12) { //3
                startX = 350;
                startY = 170;
                cellSize = 110; 
                padding = 10;  
            }
            if (numCells == 16) { //4
                startX = 350;
                startY = 110;
                cellSize = 110; 
                padding = 10;  
            }
            if (numCells == 20) { //5
                startX = 340;
                startY = 130;
                cellSize = 90; 
                padding = 10;  
            }
            if (numCells == 24) { //6
                startX = 340;
                startY = 130;
                cellSize = 80; 
                padding = 10;  
            }
            if (numCells == 28) { //7
                startX = 325;
                startY = 120;
                cellSize = 80; 
                padding = 10;  
            }
            if (numCells == 30) { //8
                startX = 325;
                startY = 120;
                cellSize = 80; 
                padding = 10;  
            }
            if (numCells == 36) { //9
                startX = 330;
                startY = 90;
                cellSize = 80; 
                padding = 10;  
            }
            if (numCells == 40) { //10
  
            }

            int cols = (int) Math.ceil(Math.sqrt(numCells)); 
            int rows = (int) Math.ceil((double) numCells / cols); 

            int totalWidth = cols * cellSize + (cols - 1) * padding;
            int totalHeight = rows * cellSize + (rows - 1) * padding;

            for (int i = 0; i < numCells; i++) {
                int row = i / cols;
                int col = i % cols;

                int x = startX + col * (cellSize + padding);
                int y = startY + row * (cellSize + padding);

                cells.add(new Cell(new Rectangle(x, y, cellSize, cellSize), images.get(i)));
            }
            repaint();
        }
    }

    private void loadCellImages(int numCells) {
        images.clear();
        try {
            int numPairs = numCells / 2; 
            for (int i = 1; i <= numPairs; i++) {
                BufferedImage img = ImageIO.read(getClass().getResource("/resources/image" + i + ".jpg"));
                images.add(img);
                images.add(img); 
            }

            Collections.shuffle(images);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlayInfo(PlayInfo playInfo) {
        this.playInfo = playInfo;
    }

    public void initListeners(GameSen gameSen) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                if (!isPaused) {
                    for (Cell cell : cells) {
                        if (cell.getRectangle().contains(mouseX, mouseY) && !cell.isFlipped() && !cell.isMatched()) {
                            cell.setFlipped(true);
                            if (firstSelectedCell == null) {
                                firstSelectedCell = cell;
                            } else if (secondSelectedCell == null) {
                                secondSelectedCell = cell;
                                checkForMatch();
                            }
                            repaint();
                            break;
                        }
                    }
                }

                // Kiểm tra vị trí nhấp chuột vào nút pause
                if (mouseX >= 1097 && mouseX <= 1099 + pause.getWidth() && mouseY >= 20 && mouseY <= 24 + pause.getHeight()) {
                    isPaused = true; 
                    timer.stop();
                    repaint();
                }
                
                if (mouseX >= 1047 && mouseX <= 1047 + idea.getWidth() && mouseY >= 20 && mouseY <= 24 + idea.getHeight()) {
                    System.out.println("Clicked within idea area");

                    // Tìm hai ô giống nhau CHƯA được lật 
                    List<Cell> matchingCells = findMatchingUnflippedCells();

                    System.out.println("Number of matching cells: " + matchingCells.size());

                    if (matchingCells.size() >= 2) {
                        Random random = new Random();
                        int index1 = random.nextInt(matchingCells.size());
                        int index2 = random.nextInt(matchingCells.size());

                        // Đảm bảo chọn hai ô khác nhau
                        while (index1 == index2) {
                            index2 = random.nextInt(matchingCells.size());
                        }

                        Cell cell1 = matchingCells.get(index1);
                        Cell cell2 = matchingCells.get(index2);

                        System.out.println("Selected cells: " + cell1 + ", " + cell2);

                        // Hiển thị hai ô đó
                        cell1.setFlipped(true);
                        cell2.setFlipped(true);
                        repaint();

                        System.out.println("Cells flipped");

                        // Đặt một Timer để úp lại hai ô sau 2 giây
                        Timer flipBackTimer = new Timer(2000, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                cell1.setFlipped(false);
                                cell2.setFlipped(false);
                                repaint();
                                System.out.println("Cells flipped back");
                            }
                        });
                        flipBackTimer.setRepeats(false);
                        flipBackTimer.start();
                    }
                }

                if (isPaused && remainingTime != 0) {
                	// Resume
                	if (mouseX >= 450 && mouseX <= 450 + resume.getWidth() - 25 && mouseY >= 400 && mouseY <= 400 + resume.getHeight() - 25) {
                	    isPaused = false; 
                	    isFinal = false;
                	    timer.start();
                	    refreshPlayScreen(); 
                	    repaint(); 
                	}
                    // Continue
                    if (mouseX >= 550 && mouseX <= 550 + continuE.getWidth() - 25 && mouseY >= 400 && mouseY <= 400 + continuE.getHeight() - 25) {
                        isPaused = false; 
                        isFinal = false;
                        timer.start(); 
                        repaint(); 
                    }
                    // Back to levelScreen
                    if (mouseX >= 650 && mouseX <= 650 + btnlevel.getWidth() - 25 && mouseY >= 400 && mouseY <= 400 + btnlevel.getHeight() - 25) {
                    	sceneManager.showScreen(sceneManager.LEVEL_SCREEN);
                    	isPaused = false;
                    	isFinal = false;
                    }
                } else if(remainingTime == 0) {
                	// Resume
                	if (mouseX >= 500 && mouseX <= 500 + resume.getWidth() - 25 && mouseY >= 400 && mouseY <= 400 + resume.getHeight() - 25) {
                	    isPaused = false; 
                	    isFinal = false;
                	    timer.start();
                	    refreshPlayScreen(); 
                	    repaint(); 
                	}
                	// Back to levelScreen
                    if (mouseX >= 600 && mouseX <= 600 + btnlevel.getWidth() - 25 && mouseY >= 400 && mouseY <= 400 + btnlevel.getHeight() - 25) {
                    	sceneManager.showScreen(sceneManager.LEVEL_SCREEN);
                    	isPaused = false;
                    	isFinal = false;
                    }
                } else if (isFinal && remainingTime != 0) {
                	// Resume
                	if (mouseX >= 450 && mouseX <= 450 + resume.getWidth() - 25 && mouseY >= 400 && mouseY <= 400 + resume.getHeight() - 25) {
                	    isPaused = false; 
                	    isFinal = false;
                	    timer.start();
                	    refreshPlayScreen(); 
                	    repaint(); 
                	}
                    // Next level
                    if (mouseX >= 550 && mouseX <= 550 + continuE.getWidth() - 25 && mouseY >= 400 && mouseY <= 400 + continuE.getHeight() - 25) {
                        isPaused = false; 
                        isFinal = false;
                        if (levelIndex < LevelManager.getLevelList().size()) {
                            levelIndex++;
                            setLevelIndex(levelIndex);
                        } else {
                            System.out.println("Bạn đã hoàn thành tất cả các màn chơi!");
                            sceneManager.showScreen(sceneManager.LEVEL_SCREEN);
                        }
                        repaint();
                    }
                    // Back to levelScreen
                    if (mouseX >= 650 && mouseX <= 650 + btnlevel.getWidth() - 25 && mouseY >= 400 && mouseY <= 400 + btnlevel.getHeight() - 25) {
                    	sceneManager.showScreen(sceneManager.LEVEL_SCREEN);
                    	isPaused = false;
                    	isFinal = false;
                    }
                }
            }
        });
    }
    
    private List<Cell> findMatchingUnflippedCells() { //idea
        List<Cell> matchingCells = new ArrayList<>();
        for (int i = 0; i < cells.size(); i++) {
            Cell cell1 = cells.get(i);
            if (!cell1.isFlipped() && !cell1.isMatched()) { // Chưa bị lật
                for (int j = i + 1; j < cells.size(); j++) {
                    Cell cell2 = cells.get(j);
                    if (!cell2.isFlipped() && !cell2.isMatched() && cell1.getImage().equals(cell2.getImage())) { //Chưa ghép với anh trùng nps
                        matchingCells.add(cell1);
                        matchingCells.add(cell2);
                        return matchingCells; // Found a pair, return
                    }
                }
            }
        }
        return matchingCells; // No matching pair found
    }
    
    // Resume run
    private void refreshPlayScreen() {
        cells.clear();
        loadCellImages(playInfo.getNumCells()); 
        createCells(playInfo.getNumCells()); 
        remainingTime = playInfo.getTime();
    }

    //Caculator game final
    private void checkForMatch() {
        if (firstSelectedCell != null && secondSelectedCell != null) {
            if (firstSelectedCell.getImage().equals(secondSelectedCell.getImage())) {
                firstSelectedCell.setMatched(true);
                secondSelectedCell.setMatched(true);
                
                // Kiểm tra nếu tất cả các ô đều được ghép thành công
                if (areAllCellsMatched()) {
                    endGame(); // Dừng thời gian và xử lý điểm số
                }
            } else {
                firstSelectedCell.setFlipped(false);
                secondSelectedCell.setFlipped(false);
            }
            firstSelectedCell = null;
            secondSelectedCell = null;
        }
        repaint();
    }
    
    private boolean areAllCellsMatched() {
        for (Cell cell : cells) {
            if (!cell.isMatched()) {
                return false;
            }
        }
        return true;
    }

    private void endGame() {
        timer.stop();
        int stars = calculateStarsBasedOnTime(remainingTime, playInfo.getNumCells());
        System.out.println("Get " + stars + " stars!");      
        isFinal = true;
        
        
        saveStarsToDatabase(stars);
    }
    
    public void setIdUser(int idUser) {
    	this.userId = idUser;
    }
    
	private void saveStarsToDatabase(int stars) {
	    Connection connection = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    try {
	        // Kết nối đến cơ sở dữ liệu
	        connection = DatabaseConnector.getConnection();
	        
	        // Lấy số sao hiện tại từ cơ sở dữ liệu
	        String query = "SELECT stars_achieved FROM player_progress WHERE user_id = ? AND level = ?";
	        stmt = connection.prepareStatement(query);
	        stmt.setInt(1, userId); // Thay userId bằng giá trị ID của người chơi hiện tại
	        stmt.setInt(2, levelIndex); // Thay levelIndex bằng giá trị màn chơi hiện tại
	        rs = stmt.executeQuery();
	        
	        if (rs.next()) {
	            int currentStars = rs.getInt("stars_achieved");
	            
	            // So sánh và cập nhật số sao nếu cần thiết
	            if (stars > currentStars) {
	                String updateQuery = "UPDATE player_progress SET stars_achieved = ? WHERE user_id = ? AND level = ?";
	                stmt = connection.prepareStatement(updateQuery);
	                stmt.setInt(1, stars);
	                stmt.setInt(2, userId); // Thay userId bằng giá trị ID của người chơi hiện tại
	                stmt.setInt(3, levelIndex); // Thay levelIndex bằng giá trị màn chơi hiện tại
	                stmt.executeUpdate();
	                System.out.println("Updated stars to " + stars + " for level " + levelIndex);
	            } else {
	                System.out.println("Current stars " + currentStars + " is higher or equal to achieved stars " + stars);
	            }
	        } else {
	            // Nếu không có bản ghi nào, thêm bản ghi mới
	            String insertQuery = "INSERT INTO player_progress (user_id, level, stars_achieved) VALUES (?, ?, ?)";
	            stmt = connection.prepareStatement(insertQuery);
	            stmt.setInt(1, userId); // Thay userId bằng giá trị ID của người chơi hiện tại
	            stmt.setInt(2, levelIndex); // Thay levelIndex bằng giá trị màn chơi hiện tại
	            stmt.setInt(3, stars);
	            stmt.executeUpdate();
	            System.out.println("Inserted new record with " + stars + " stars for level " + levelIndex);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        // Đóng các tài nguyên
	        try {
	            if (rs != null) rs.close();
	            if (stmt != null) stmt.close();
	            if (connection != null) connection.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
    
    private int calculateStarsBasedOnTime(int remainingTime, int numCells) {
        int maxStars = 0;
        int timeLimit1 = 0, timeLimit2 = 0, timeLimit3 = 0;

        switch (numCells) {
            case 4:
                maxStars = 3;
                timeLimit1 = 7; 
                timeLimit2 = 5; 
                timeLimit3 = 3; 
                break;
            case 6:
                maxStars = 3;
                timeLimit1 = 15;
                timeLimit2 = 10;
                timeLimit3 = 5;
                break;
            case 12:
                maxStars = 3;
                timeLimit1 = 20; 
                timeLimit2 = 15;
                timeLimit3 = 10;
                break;
            case 16:
                maxStars = 3;
                timeLimit1 = 30; 
                timeLimit2 = 20;
                timeLimit3 = 10;
                break;
            case 20:
                maxStars = 3;
                timeLimit1 = 50; 
                timeLimit2 = 30;
                timeLimit3 = 10;
                break;
            case 30:
                maxStars = 3;
                timeLimit1 = 70; 
                timeLimit2 = 50;
                timeLimit3 = 30;
                break;
            case 36:
                maxStars = 3;
                timeLimit1 = 100; 
                timeLimit2 = 70;
                timeLimit3 = 50;
                break;
            default:
                return 0; 
        }

        if (remainingTime >= timeLimit1) {
            return maxStars;
        } else if (remainingTime >= timeLimit2) {
            return maxStars - 1;
        } else if (remainingTime <= timeLimit3) {
            return maxStars - 2;
        } else {
            return 0;
        }
    }
    
    private void loadImageBackground() {
        try {
            background = ImageIO.read(getClass().getResource("/resources/Default.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadImageBackgroundZ() {
        try {
            backgroundZ = ImageIO.read(getClass().getResource("/resources/Shadow.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadImageTime() {
        try {
            time = ImageIO.read(getClass().getResource("/resources/time.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadImageOptions() {
        try {
            pause = ImageIO.read(getClass().getResource("/resources/pause.png"));
            resume = ImageIO.read(getClass().getResource("/resources/resume.png"));
            continuE = ImageIO.read(getClass().getResource("/resources/continue.png"));
            btnlevel = ImageIO.read(getClass().getResource("/resources/btnlevel.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadImageComplete() {
        try {
            star = ImageIO.read(getClass().getResource("/resources/star.png"));
            unStar = ImageIO.read(getClass().getResource("/resources/unstar.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadImageIdea() {
        try {
            idea = ImageIO.read(getClass().getResource("/resources/idea.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Color customColor = new Color(0xFDE294);
        // Vẽ hình nền
        g2d.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        
        // Draw a background image for the cell area
        int cellAreaX = 0; 
        int cellAreaY = 0; 
        int cellAreaWidth = 0; 
        int cellAreaHeight = 0; 
        if (playInfo != null) {
            switch (playInfo.getNumCells()) {
                case 4:
                	cellAreaX = 390; 
                    cellAreaY = 170;
                    cellAreaWidth = 375;
                    cellAreaHeight = 330;
                    break;
                case 6:
                	cellAreaX = 300; 
                    cellAreaY = 170;
                    cellAreaWidth = 550;
                    cellAreaHeight = 330;
                    break;
                case 12:
                	cellAreaX = 260; 
                    cellAreaY = 135;
                    cellAreaWidth = 630;
                    cellAreaHeight = 420;
                    break;
                case 16:
                	cellAreaX = 255; 
                    cellAreaY = 70;
                    cellAreaWidth = 630;
                    cellAreaHeight = 550;
                    break;
                case 20:
                	cellAreaX = 255; 
                    cellAreaY = 100;
                    cellAreaWidth = 635;
                    cellAreaHeight = 450;
                    break;
                case 30:
                	cellAreaX = 235; 
                    cellAreaY = 90;
                    cellAreaWidth = 680;
                    cellAreaHeight = 500;
                    break;
                case 36:
                	cellAreaX = 237; 
                    cellAreaY = 55;
                    cellAreaWidth = 690;
                    cellAreaHeight = 600;
                    break;
            }
        }

        // Load background image for the cell area
        BufferedImage cellAreaBackgroundImage = null;
        try {
            cellAreaBackgroundImage = ImageIO.read(getClass().getResource("/resources/backgroundPlay.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cellAreaBackgroundImage != null) {
            g2d.drawImage(cellAreaBackgroundImage, cellAreaX, cellAreaY, cellAreaWidth, cellAreaHeight, null);
        }

        // Vẽ các ô nhớ
        for (Cell cell : cells) {
            Rectangle rect = cell.getRectangle();
            if (cell.isFlipped() || cell.isMatched()) {
                Shape clip = g2d.getClip();
                g2d.setClip(new RoundRectangle2D.Float(rect.x, rect.y, rect.width, rect.height, 20, 20));
                g2d.drawImage(cell.getImage(), rect.x, rect.y, rect.width, rect.height, this);

                g2d.setClip(clip);

                g2d.setColor(customColor);
                g2d.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20);
            } else {
                // Vẽ viền và nền cho ô
                g2d.setColor(customColor);
                g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20);
                g2d.setColor(customColor);
                g2d.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20);
            }
        }

        if (isPaused && remainingTime != 0) {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.drawImage(backgroundZ, 0, 0, getWidth(), getHeight(), this);
            Font largeFont = customFont.deriveFont(40f);
            g2d.setFont(largeFont);
            g2d.setColor(Color.WHITE);
            g2d.drawString("PAUSE", 520, 300);
            g2d.drawImage(resume, 460, 400, 60, 60, this);
            g2d.drawImage(continuE, 560, 400, 60, 60, this);
            g2d.drawImage(btnlevel, 660, 400, 60, 60, this);
        } else if(remainingTime == 0) {
        	g2d.drawImage(backgroundZ, 0, 0, getWidth(), getHeight(), this);
        	Font largeFont = customFont.deriveFont(40f);
            g2d.setFont(largeFont);
            g2d.setColor(Color.WHITE);
            g2d.drawString("GAME OVER", 470, 300);
            g2d.drawImage(resume, 510, 400, 60, 60, this);
            g2d.drawImage(btnlevel, 610, 400, 60, 60, this);
        } else if (isFinal && remainingTime != 0) {
        	g2d.drawImage(backgroundZ, 0, 0, getWidth(), getHeight(), this);
        	Font largeFont = customFont.deriveFont(40f);
            g2d.setFont(largeFont);
            g2d.setColor(Color.WHITE);
            g2d.drawString("LEVEL COMPLETE", 400, 200);
            int stars = calculateStarsBasedOnTime(remainingTime, playInfo.getNumCells());
            
            int starSize = 70;
            int xStart = 470;
            int yPosition = 250;
            int spacing = 13;
     
            for (int i = 0; i < 3; i++) {
                if (i < stars) {
                    g2d.drawImage(star, xStart + i * (starSize + spacing), yPosition, starSize, starSize, this);
                } else {
                    g2d.drawImage(unStar, xStart + i * (starSize + spacing), yPosition, starSize, starSize, this);
                }
            }
            g2d.drawImage(resume, 460, 400, 60, 60, this);
            g2d.drawImage(continuE, 560, 400, 60, 60, this);
            g2d.drawImage(btnlevel, 660, 400, 60, 60, this);
        }
        else {
            g2d.drawImage(time, 30,20, 95, 40, this);
            g2d.drawImage(pause, 1100, 20, 40, 40, this);
            g2d.drawImage(idea, 1050, 20, 40, 40, this);

            if (playInfo != null) {
                g2d.setFont(customFont);
                g2d.setColor(customColor);
                g2d.drawString(remainingTime + "", 70, 48);
            }
        }
    }
}
