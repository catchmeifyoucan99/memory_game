package controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import support.DatabaseConnector;

public class Rate extends JDialog {
    
    private BufferedImage backgroundImage, closeImage, rank1,rank2,rank3;
    private JPanel contentPanel;
    private JButton closeButton;
    private JTextArea playerListTextArea;
    private Font customFont;
    private Font titleFont; // Phông chữ riêng cho tiêu đề
    private Color customColor, customColor2;
    private JLabel titleLabel, rank1Label, rank2Label, rank3Label;
    
    public Rate(JFrame parentFrame) {
        super(parentFrame, true);
        
        // Load font
        try {
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/font.ttf");
            if (stream != null) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(24f);
                titleFont = customFont.deriveFont(50f); 
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(customFont);
                ge.registerFont(titleFont);
                stream.close();
            } else {
                System.out.println("Font resource not found");
            }
        } catch (IOException | FontFormatException e) {
            System.out.println("Error loading font: " + e.getMessage());
        }
        
        customColor = new Color(0xFDE294); // Custom color
        customColor2 = new Color(0x967b74);
        setSize(900, 650); // size
        setResizable(false); // no bg move
        setLocationRelativeTo(parentFrame);
        
        loadBackgroundImage();
        initializeComponents();
        setUndecorated(true); // no decorate
        setBackground(new Color(0, 0, 0, 0)); // transparent bg
        
        displayTopPlayers();
    }
    
    // get image
    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/resources/backgroundRank.png"));          
            closeImage = ImageIO.read(getClass().getResource("/resources/close1.png"));
            rank1 = ImageIO.read(getClass().getResource("/resources/rank1.png"));
            rank2 = ImageIO.read(getClass().getResource("/resources/rank2.png"));
            rank3 = ImageIO.read(getClass().getResource("/resources/rank3.png"));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load background image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            backgroundImage = null;
        }
    }
    
    private void initializeComponents() {
        // background
        contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        
        // Close button
        closeButton = new JButton(new ImageIcon(closeImage));
        closeButton.setBounds(770, 85, closeImage.getWidth(), closeImage.getHeight()); // Adjust position and size as needed
        closeButton.addActionListener(e -> setVisible(false));
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        contentPanel.add(closeButton);
        
        // Title label
        titleLabel = new JLabel("RANK");
        titleLabel.setBounds(405, 35, 150, 50); // Adjust size and position as needed
        titleLabel.setForeground(customColor2); // Set customColor2 for title
        titleLabel.setFont(titleFont); // Use the larger font for the title
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setVerticalAlignment(JLabel.CENTER); // Align text vertically
        contentPanel.add(titleLabel);
        
        // Player list text area
        playerListTextArea = new JTextArea();
        playerListTextArea.setBounds(200, 130, 600, 400); // Adjust position to increase distance
        playerListTextArea.setOpaque(false);
        playerListTextArea.setForeground(customColor); // Use custom color
        playerListTextArea.setEditable(false);
        playerListTextArea.setFont(customFont); // Use custom font
        contentPanel.add(playerListTextArea);
        
        // Rank 1 label
        rank1Label = new JLabel(new ImageIcon(rank1));
        rank1Label.setBounds(130, 212, rank1.getWidth(), rank1.getHeight()); // Adjust position and size as needed
        contentPanel.add(rank1Label);
        
     // Rank 2 label
        rank2Label = new JLabel(new ImageIcon(rank2));
        rank2Label.setBounds(130, 272, rank2.getWidth(), rank2.getHeight()); // Adjust position and size as needed
        contentPanel.add(rank2Label);
        
     // Rank 3 label
        rank3Label = new JLabel(new ImageIcon(rank3));
        rank3Label.setBounds(130, 332, rank3.getWidth(), rank3.getHeight()); // Adjust position and size as needed
        contentPanel.add(rank3Label);
        
        contentPanel.setLayout(null);
        setContentPane(contentPanel);
    }
    
    private void displayTopPlayers() {
        List<Player> topPlayers = getTopPlayers(10);
        StringBuilder playerList = new StringBuilder();
        playerList.append(String.format("%-10s %-20s %-15s\n", "ID", "Username", "Total Stars"));
        playerList.append("--------------------------------------------------\n\n");
        for (Player player : topPlayers) {
            playerList.append(String.format("%-10d %-20s %-15d\n\n", player.getId(), player.getUsername(), player.getTotalStarsAchieved()));
        }
        playerListTextArea.setText(playerList.toString());
    }
    
    private List<Player> getTopPlayers(int limit) {
        List<Player> players = new ArrayList<>();
        String query = "SELECT u.id, u.username, SUM(pp.stars_achieved) AS total_stars_achieved " +
                       "FROM user u " +
                       "JOIN player_progress pp ON u.id = pp.user_id " +
                       "GROUP BY u.id, u.username " +
                       "ORDER BY total_stars_achieved DESC " +
                       "LIMIT ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                int totalStarsAchieved = rs.getInt("total_stars_achieved");
                players.add(new Player(id, username, totalStarsAchieved));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return players;
    }
    
    static class Player {
        private int id;
        private String username;
        private int totalStarsAchieved;
        
        public Player(int id, String username, int totalStarsAchieved) {
            this.id = id;
            this.username = username;
            this.totalStarsAchieved = totalStarsAchieved;
        }
        
        public int getId() {
            return id;
        }
        
        public String getUsername() {
            return username;
        }
        
        public int getTotalStarsAchieved() {
            return totalStarsAchieved;
        }
    }
}
