package screen;

import javax.swing.*;

import main.MyGame;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import support.DatabaseConnector;
import support.SceneManager;

public class LoginScreen extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JButton switchToRegisterButton;

    private JTextField regUsernameField;
    private JPasswordField regPasswordField;
    private JPasswordField regConfirmPasswordField;
    private JButton registerButton;
    private JButton switchToLoginButton;

    private JPanel loginPanel;
    private JPanel registerPanel;
    private CardLayout cardLayout;

    private int userId; // Thêm thuộc tính này
    private LoginListener loginListener;
    private SceneManager sceneManager;
    private LevelScreen levelScreen;
    
    public LoginScreen(JFrame parentFrame) {
        super(parentFrame, "Login", true);
        System.out.println("LoginScreen is running...");
        
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setSize(300, 200);
        setLocationRelativeTo(parentFrame); // Center the dialog on the parent frame

        createLoginPanel();
        createRegisterPanel();

        add(loginPanel, "Login");
        add(registerPanel, "Register");

        cardLayout.show(getContentPane(), "Login");
    }

    private void createLoginPanel() {
        loginPanel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> onLogin());
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> onCancel());
        switchToRegisterButton = new JButton("Register");
        switchToRegisterButton.addActionListener(e -> switchToRegister());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(switchToRegisterButton);

        loginPanel.add(panel, BorderLayout.CENTER);
        loginPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createRegisterPanel() {
        registerPanel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Username:"));
        regUsernameField = new JTextField();
        panel.add(regUsernameField);

        panel.add(new JLabel("Password:"));
        regPasswordField = new JPasswordField();
        panel.add(regPasswordField);

        panel.add(new JLabel("Confirm Password:"));
        regConfirmPasswordField = new JPasswordField();
        panel.add(regConfirmPasswordField);

        registerButton = new JButton("Register");
        registerButton.addActionListener(e -> onRegister());
        switchToLoginButton = new JButton("Login");
        switchToLoginButton.addActionListener(e -> switchToLogin());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerButton);
        buttonPanel.add(switchToLoginButton);

        registerPanel.add(panel, BorderLayout.CENTER);
        registerPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void onLogin() {
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();

        if (username.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(this, "Username or Password cannot be empty");
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT id FROM user WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, new String(password));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("id");
                        
                        JOptionPane.showMessageDialog(this, "Login successful!");
                        
                        if (loginListener != null) {
                            loginListener.onLoginSuccess(); // Gọi hàm onLoginSuccess của LoginListener
                        }
                        System.out.println("LoginListener :" + true);
        
                        // Cập nhật userId trong SceneManager
                        if (getParent() instanceof JFrame) {
                            JFrame parentFrame = (JFrame) getParent();
                            if (parentFrame instanceof MyGame) {
                                MyGame myGame = (MyGame) parentFrame;
                                SceneManager sceneManager = myGame.getSceneManager();
                                sceneManager.setUserId(userId);
                            }
                        }
                        
                        dispose(); // Đóng màn hình login
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid username or password");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while logging in");
        } finally {
            Arrays.fill(password, '0'); // Clear the password
        }
    }

    private void onCancel() {
        dispose();
    }

    private void switchToRegister() {
        cardLayout.show(getContentPane(), "Register");
    }

    private void onRegister() {
        String username = regUsernameField.getText();
        char[] password = regPasswordField.getPassword();
        char[] confirmPassword = regConfirmPasswordField.getPassword();

        if (username.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(this, "Username or Password cannot be empty");
            return;
        }

        if (!Arrays.equals(password, confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match");
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO user (username, password) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, new String(password));
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registration successful!");
                switchToLogin();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while registering");
        } finally {
            Arrays.fill(password, '0'); // Clear the password
            Arrays.fill(confirmPassword, '0'); // Clear the confirm password
        }
    }

    private void switchToLogin() {
        cardLayout.show(getContentPane(), "Login");
    }

    public int getUserId() {
        return userId; // Trả về userId sau khi đăng nhập
    }

    public void addLoginListener(LoginListener listener) {
        this.loginListener = listener; // Thêm LoginListener
    }
}
