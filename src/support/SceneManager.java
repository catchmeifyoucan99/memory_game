package support;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.MyGame;
import model.LevelInfo;
import screen.LevelScreen;
import screen.MainScreen;
import screen.LoginScreen;
import screen.PlayScreen;

public class SceneManager {
    public static final int MAIN_SCREEN = 1;
    public static final int LEVEL_SCREEN = 2;
    public static final int PLAY_SCREEN = 3;

    private final Map<Integer, JPanel> screenMap = new HashMap<>();
    private MyGame myGame;
    private JPanel currentScreen;
    private GameSound gameSound;
    private int userID; // User ID to pass to LevelScreen
    private LoginScreen loginScreen;

    public SceneManager(MyGame myGame, GameSound gameSound) {
        this.myGame = myGame;
        this.gameSound = gameSound;

        // Initialize MainScreen and set as initial screen
        MainScreen mainScreen = new MainScreen(myGame, this, gameSound, myGame);
        screenMap.put(MAIN_SCREEN, mainScreen);
        currentScreen = mainScreen;

        LevelScreen levelScreen = new LevelScreen(myGame, this);
        System.out.println("UserId(SceneManager1): " + userID);
        screenMap.put(LEVEL_SCREEN, levelScreen);
        
        PlayScreen playScreen = new PlayScreen(myGame, this, levelScreen);
        screenMap.put(PLAY_SCREEN, playScreen);

        
        
        // Set the content pane of myGame
        myGame.setContentPane(currentScreen);
        myGame.validate();
        myGame.repaint();
    }

    public void setUserId(int userId) {
    	userID = userId;
        LevelScreen levelScreen = (LevelScreen) screenMap.get(LEVEL_SCREEN);
        if (levelScreen != null) {
            levelScreen.setUserId(userID);
    	}
    }
    
    public int getUserId() {
    	return userID;
    }

    public void showScreen(int screen) {
        JPanel newScreen = screenMap.get(screen);

        if (newScreen == null) {
            System.out.println("Requested screen not found: " + screen);
            return;
        }

        if (screen == LEVEL_SCREEN && !isLoggedIn()) {
            return;
        }

        if (screen == LEVEL_SCREEN && isLoggedIn()) {
            setUserId(userID); 
        }
        
        if(screen == PLAY_SCREEN) {
        	
        }
        // Update content pane for myGame and display the new screen
        myGame.setContentPane(newScreen);
        myGame.validate();
        myGame.repaint();
        currentScreen = newScreen;
    }
    
    public void showPlayScreen(int levelIndex) {
    	PlayScreen playScreen = (PlayScreen) screenMap.get(PLAY_SCREEN);
    	if(playScreen != null) {
    		playScreen.setLevelIndex(levelIndex);
    		playScreen.setIdUser(userID);;
    	}
    	myGame.setContentPane(playScreen);
        myGame.validate();
        myGame.repaint();
        currentScreen = playScreen;
    }

    private boolean isLoggedIn() {
        return userID != 0; 
    }
    
    public JPanel getCurrentScreen(LevelInfo info) {
        return currentScreen;
    }
    
}

