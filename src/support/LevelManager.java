package support;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import model.LevelInfo;
import model.PlayInfo;

public class LevelManager {
	
	public static List<PlayInfo> levelList = new ArrayList<>();
		
    public LevelManager() {
    	levelList.add(new PlayInfo(1, 4, 10));
    	levelList.add(new PlayInfo(2, 6, 20));
    	levelList.add(new PlayInfo(3, 12, 30));
    	levelList.add(new PlayInfo(4, 16, 45)); 
    	levelList.add(new PlayInfo(5, 20, 70));
    	levelList.add(new PlayInfo(6, 20, 60)); 
    	levelList.add(new PlayInfo(7, 30, 120));
    	levelList.add(new PlayInfo(8, 30, 100));
    	levelList.add(new PlayInfo(9, 36, 150)); 
    	levelList.add(new PlayInfo(10, 1, 1)); 
    }
    
    public static PlayInfo getLevel(int level) {
    	return levelList.get(level-1);
    }
    
    public static List<PlayInfo> getLevelList() {
        return levelList;
    }
}
