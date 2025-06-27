package pieces;

import main.GamePanel;
import main.Type;

public class Rook extends Piece {

	public Rook(int color,int col, int row) {
		super(color, row, col);
		
		type=Type.ROOK;
		if(color==GamePanel.WHITE) {
			image=getImage("/piece/w-rook.png");
		}
		else {
			image=getImage("/piece/b-rook.png");
		}
	}
	public boolean canMove(int targetCol,int targetRow) {
		if(isWithInBoard(targetCol,targetRow) && isSameSquare(targetCol,targetRow)==false) {
			if(targetCol==preCol ||targetRow==preRow ) {
				if(isValidSquare(targetCol,targetRow) && !pieceIsOnStraightLine(targetCol,targetRow)) {
					return true;
				}
			}
		}
		return false;
	}

}
