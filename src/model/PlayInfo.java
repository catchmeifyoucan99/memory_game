package model;

public class PlayInfo {
	private int level;
	private int numCells;
	private int time;
	
	public PlayInfo(int level, int numCells, int time) {
		this.setLevel(level);
		this.setNumCells(numCells);
		this.setTime(time);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getNumCells() {
		return numCells;
	}

	public void setNumCells(int numCells) {
		this.numCells = numCells;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
}
