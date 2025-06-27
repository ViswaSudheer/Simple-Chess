package pieces;

import main.GamePanel;
import main.Type;

public class Knight extends Piece{

	public Knight(int color,int col, int row) {
		super(color, row, col);
		
		type=Type.KNIGHT;
		if(color==GamePanel.WHITE) {
			image=getImage("/piece/w-knight.png");
		}
		else {
			image=getImage("/piece/b-knight.png");
		}
	}
	public boolean canMove(int targetCol,int targetRow) {
		if(isWithInBoard(targetCol,targetRow)) {
			if(Math.abs(targetCol-preCol)*Math.abs(targetRow-preRow)==2) {
				if(isValidSquare(targetCol,targetRow)) {
					return true;
				}
			}
		}
		return false;
	}

}
