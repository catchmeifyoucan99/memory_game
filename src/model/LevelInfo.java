package model;

public class LevelInfo {
    private String level;
    private int stars;
    private int totalStars;
    private boolean unlocked;

    public LevelInfo(String level, int stars, int totalStars, boolean unlocked) {
        this.level = level;
        this.stars = stars;
        this.totalStars = totalStars;
        this.unlocked = unlocked;
    }

    // Add getters and setters as needed
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getTotalStars() {
        return totalStars;
    }

    public void setTotalStars(int totalStars) {
        this.totalStars = totalStars;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}
