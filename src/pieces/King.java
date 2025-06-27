package pieces;

import main.GamePanel;
import main.Type;

public class King extends Piece{

	public King(int color,int col, int row) {
		super(color, row, col);
		
		type=Type.KING;
		if(color==GamePanel.WHITE) {
			image=getImage("/piece/w-king.png");
		}
		else {
			image=getImage("/piece/b-king.png");
		}
	}

	public boolean canMove(int targetCol, int targetRow) {
		if(isWithInBoard(targetCol,targetRow) && isSameSquare(targetCol,targetRow)==false) {
			//Movement
			if(Math.abs(targetCol-preCol)+Math.abs(targetRow-preRow)==1 ||
					Math.abs(targetCol-preCol)*Math.abs(targetRow-preRow)==1) {
				if(isValidSquare(targetCol,targetRow)) return true;
			}
			
			//castling
			if(!moved) {
				//Right castling
				if(targetRow==preRow && targetCol==preCol+2 && !pieceIsOnStraightLine(targetCol,targetRow)) {
					for(Piece piece:GamePanel.simPieces) {
						//getting the rook
						if(piece.col==preCol+3 && piece.row==preRow && piece.moved==false) {
							GamePanel.castlingP=piece;
							return true;
						}
					}
				}
				//left
				if(targetRow==preRow && targetCol==preCol-2 && !pieceIsOnStraightLine(targetCol,targetRow)) {
					Piece p[]=new Piece[2];
					for(Piece piece:GamePanel.simPieces) {
						//position of knight 
						if(piece.col==preCol-3 && piece.row==preRow) {
							p[0]=piece;
						}
						//position of rook
						if(piece.col==preCol-4 && piece.row==preRow) {
							p[1]=piece;
						}
						
					}
					if(p[0]==null &&p[1]!=null && p[1].moved==false) {
						GamePanel.castlingP=p[1];
						return true;
					}
				}
			}
		}
		
		return false;
	}
	

}
