package main;

import java.awt.Color;
import java.awt.Graphics2D;

public class Board {
	final int MAX_COL=8;
	final int MAX_ROW=8;
	public static final int SQUARE_SIZE=100;
	public static final int HALF_SQUARE_SIZE=SQUARE_SIZE/2;
	
	public void draw(Graphics2D g2) {
		int c=0;
		
		for(int i=0;i<MAX_ROW;i++) {
			for(int j=0;j<MAX_COL;j++) {
				if(c==0) {
					g2.setColor(new Color(150,200,200));
					c=1;
				}else {
					g2.setColor(new Color(50,50,50));
					c=0;
				}
				g2.fillRect(j*SQUARE_SIZE, i*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
			}
			c= (c==0) ? 1:0;
			
		}
	}
}
