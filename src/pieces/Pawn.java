package pieces;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece{

	public Pawn(int color,int col, int row) {
		super(color, row, col);
		type=Type.PAWN;
		if(color==GamePanel.WHITE) {
			image=getImage("/piece/w-pawn.png");
		}
		else {
			image=getImage("/piece/b-pawn.png");
		}
	}
	public boolean canMove(int targetCol,int targetRow) {
		if(isWithInBoard(targetCol,targetRow) && isSameSquare(targetCol,targetRow)==false) {
			
			//define move value based on color
			int moveValue;
			if(color==GamePanel.WHITE) moveValue=-1;
			else moveValue=1;
			
			//check hittingP
			hittingP=getHittingP(targetCol,targetRow);
			
			//1 step movement
			if(targetCol==preCol && targetRow==preRow+moveValue && hittingP==null) {
				return true;
			}
			
			//2 step movement
			if(targetCol==preCol && targetRow==preRow+moveValue*2 && hittingP==null && !moved && !pieceIsOnStraightLine(targetCol,targetRow)) {
				return true;
			}
			
			// diagonal movement and capturing pieces
			if(Math.abs(targetCol-preCol)==1 && targetRow==preRow+moveValue && hittingP!=null && hittingP.color!=color) {
				return true;
			}
			
			//en-passant
			if(Math.abs(targetCol-preCol)==1 && targetRow==preRow+moveValue ) {
				for(Piece piece:GamePanel.simPieces) {
					if(piece.col==targetCol && piece.row==preRow && piece.twoStepped==true) {
						hittingP=piece;
						return true;
					}
				}
			}
		}
		
		
		return false;
	}
}
