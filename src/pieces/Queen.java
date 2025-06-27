package pieces;

import main.GamePanel;
import main.Type;

public class Queen extends Piece{

	public Queen(int color,int col, int row) {
		super(color, row, col);
		
		type=Type.QUEEN;
		if(color==GamePanel.WHITE) {
			image=getImage("/piece/w-queen.png");
		}
		else {
			image=getImage("/piece/b-queen.png");
		}
	}
	public boolean canMove(int targetCol,int targetRow) {
		if(isWithInBoard(targetCol,targetRow) && isSameSquare(targetCol,targetRow)==false) {
			//rook
			if(targetCol==preCol ||targetRow==preRow ) {
				if(isValidSquare(targetCol,targetRow) && !pieceIsOnStraightLine(targetCol,targetRow)) {
					return true;
				}
			}
			//bishop
			if(Math.abs(targetCol-preCol)==Math.abs(targetRow-preRow) ) {
				if(isValidSquare(targetCol,targetRow) && !pieceIsOnDiagonalLine(targetCol,targetRow)) {
					return true;
				}
			}
		}
		return false;
	}
}
