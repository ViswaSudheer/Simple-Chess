package pieces;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece{

	public Bishop(int color,int col, int row) {
		super(color, row, col);
		
		type=Type.BISHOP;
		if(color==GamePanel.WHITE) {
			image=getImage("/piece/w-bishop.png");
		}
		else {
			image=getImage("/piece/b-bishop.png");
		}
	}
	public boolean canMove(int targetCol,int targetRow) {
		if(isWithInBoard(targetCol,targetRow) && isSameSquare(targetCol,targetRow)==false) {
			if(Math.abs(targetCol-preCol)==Math.abs(targetRow-preRow) ) {
				if(isValidSquare(targetCol,targetRow) && !pieceIsOnDiagonalLine(targetCol,targetRow)) {
					return true;
				}
			}
		}
		return false;
	}

}
