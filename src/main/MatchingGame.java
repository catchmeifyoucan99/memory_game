package main;

import javax.swing.*;

import support.GameSen;
import support.GameThread;
import support.ScreenSetting;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MatchingGame{
	
	
	
    public MatchingGame() {
    	System.out.println("=> Game is running..."); 
    	
    	new MyGame();
    	
           
         
    }

    public static void main(String[] args) {
        new MatchingGame();
    }
   
}
