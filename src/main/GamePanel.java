package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;

public class GamePanel extends JPanel implements Runnable{
	public static final int WIDTH=1100;
	public static final int HEIGHT=800;
	public int FPS=60;
	Thread gameThread;
	Board board=new Board();
	Mouse mouse=new Mouse();
	Piece activeP,checkingP;
	public static Piece castlingP;
	
	//PIECES
	public static ArrayList<Piece> pieces=new ArrayList<>();
	public static ArrayList<Piece> simPieces=new ArrayList<>();
	ArrayList<Piece> promoPieces=new ArrayList<>();
	
	//COLOR
	public static final int WHITE=0;
	public static final int BLACK=1;
	int currentColor=WHITE;
	
	//booleans
	boolean canMove;
	boolean validSquare;
	boolean promotion;
	boolean gameOver;
	boolean staleMate;
	
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		setBackground(Color.BLACK);
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		
		setPieces();
		copyPieces(pieces,simPieces);
	}
	
	public void launchGame() {
		gameThread =new Thread(this);
		gameThread.start();
	}
	
	public void setPieces() {
		
		pieces.add(new Pawn(WHITE,0,6));
		pieces.add(new Pawn(WHITE,1,6));
		pieces.add(new Pawn(WHITE,2,6));
		pieces.add(new Pawn(WHITE,3,6));
		pieces.add(new Pawn(WHITE,4,6));
		pieces.add(new Pawn(WHITE,5,6));
		pieces.add(new Pawn(WHITE,6,6));
		pieces.add(new Pawn(WHITE,7,6));
		pieces.add(new Rook(WHITE,0,7));
		pieces.add(new Rook(WHITE,7,7));
		pieces.add(new Knight(WHITE,1,7));
		pieces.add(new Knight(WHITE,6,7));
		pieces.add(new Bishop(WHITE,2,7));
		pieces.add(new Bishop(WHITE,5,7));
		pieces.add(new Queen(WHITE,3,7));
		pieces.add(new King(WHITE,4,7));
		
		pieces.add(new Pawn(BLACK,0,1));
		pieces.add(new Pawn(BLACK,1,1));
		pieces.add(new Pawn(BLACK,2,1));
		pieces.add(new Pawn(BLACK,3,1));
		pieces.add(new Pawn(BLACK,4,1));
		pieces.add(new Pawn(BLACK,5,1));
		pieces.add(new Pawn(BLACK,6,1));
		pieces.add(new Pawn(BLACK,7,1));
		pieces.add(new Rook(BLACK,0,0));
		pieces.add(new Rook(BLACK,7,0));
		pieces.add(new Knight(BLACK,1,0));
		pieces.add(new Knight(BLACK,6,0));
		pieces.add(new Bishop(BLACK,2,0));
		pieces.add(new Bishop(BLACK,5,0));
		pieces.add(new Queen(BLACK,3,0));
		pieces.add(new King(BLACK,4,0));
		
	}
	private void copyPieces(ArrayList<Piece> source,ArrayList<Piece> target) {
		
		target.clear();
		for(int i=0;i<source.size();i++) {
			target.add(source.get(i));
		}
	}
	
	@Override
	public void run() {
		double drawInterval= 1000000000/FPS;
		double delta=0;
		long lastTime=System.nanoTime();
		long currentTime;
		while(gameThread!=null) {
			currentTime=System.nanoTime();
			
			delta+=(currentTime-lastTime)/drawInterval;
			lastTime=currentTime;
			
			if(delta>=1) {
				update();
				repaint();
				delta--;
			}
		}
	}
	private void update() {
		if(promotion) {
			promoting();
		}
		
		else if(!gameOver && !staleMate){
			
			//mouse button pressed
			if(mouse.pressed) {
				if(activeP==null) {
					// If the activeP is null, check if u can pick a piece
					for(Piece piece:simPieces) {
						// If mouse is on ally piece, pick it
						if(piece.color==currentColor && piece.col==mouse.x/Board.SQUARE_SIZE && piece.row==mouse.y/Board.SQUARE_SIZE) {
							activeP=piece;
						}
					}
				}
				else {
					// if piece is picked
					simulate();
				}
			}
			if(mouse.pressed==false) {
				if(activeP!=null) {
					if(validSquare) {
						// update the piece which has been removed
						copyPieces(simPieces,pieces);
						activeP.updatePosition();
						if(castlingP!=null) {
							castlingP.updatePosition();
						}
						if(isKingInCheck() && isCheckMate()) {
							gameOver=true;
						}
						else if(isStaleMate() && !isKingInCheck()) {
							staleMate=true;
						}
						else {
							
							if(canPromote()) promotion=true;
							else changePlayer();
						}
					}
					else {
						//if move is not valid reset
						copyPieces(pieces,simPieces);
						activeP.resetPosition();
						activeP=null;
					}
				}
			}
		}
	}
	private void simulate() {
		canMove=false;
		validSquare=false;
		// reset the pieces in every loop
		// for restoring removed piece during sim
		copyPieces(pieces,simPieces);
		
		//Reset castling piece position
		if(castlingP!=null) {
			castlingP.col=castlingP.preCol;
			castlingP.x=castlingP.getX(castlingP.col);
			castlingP=null;
		}
		
		// if piece is held ,update its position
		activeP.x=mouse.x-Board.HALF_SQUARE_SIZE;
		activeP.y=mouse.y-Board.HALF_SQUARE_SIZE;
		activeP.col=activeP.getCol(activeP.x);
		activeP.row=activeP.getCol(activeP.y);
		
		//check
		if(activeP.canMove(activeP.col, activeP.row)) {
			canMove=true;
			//if hitting a piece remove it from the list
			if(activeP.hittingP!=null) {
				simPieces.remove(activeP.hittingP.getIndex());
			}
			checkCastling();
			if(!isIllegal(activeP)  && !opponentCanCaptureKing()) {				
				validSquare=true;
			}
		}
	}
	//illegal kings move
	public boolean isIllegal(Piece king) {
		if(king.type==Type.KING) {
			for(Piece piece:simPieces) {
				if(piece!=king && piece.color!=king.color && piece.canMove(king.col,king.row)) {
					return true;
				}
			}
		}
		return false;
	}
	//when king incheck no other oieces can move
	public boolean opponentCanCaptureKing() {
		Piece king=getKing(false);
		
		for(Piece piece:simPieces) {
			if(piece.color!=king.color && piece.canMove(king.col,king.row)) {
				return true;
			}
		}
		return false;
	}
	//king in check
	public boolean isKingInCheck() {
		Piece king=getKing(true);
		
		if(activeP.canMove(king.col,king.row)) {
			checkingP=activeP;
			return true;
		}
		else checkingP=null;
		return false;
	}
	public Piece getKing(boolean opponent) {
		Piece king=null;
		for(Piece piece:pieces) {
			
			if(opponent) {
				if(piece.type==Type.KING && piece.color!=currentColor) {
					king=piece;
				}
			}
			else {
				if(piece.type==Type.KING && piece.color==currentColor) {
					king=piece;
				}
			}
		}
		return king;
	}
	//checkmate
	public boolean isCheckMate() {
		Piece king=getKing(true);
		if(kingCanMove(king)) {
			//if king can move its not check mate
			return false;
		}
		else {
			//opponent king can move
			//there is chance of opponent blocking the path with another pieces
			int colDiff=Math.abs(checkingP.col-king.col);
			int rowDiff=Math.abs(checkingP.row-king.row);
			
			if(colDiff==0) {
				//attacking vertically
				if(checkingP.row<king.row) {
					//above the king
					for(int row=checkingP.row;row<king.row;row++) {
						for(Piece piece:simPieces) {
							if(piece!=king && piece.color!=currentColor && piece.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
				if(checkingP.row<king.row) {
					//below the king
					for(int row=checkingP.row;row>king.row;row--) {
						for(Piece piece:simPieces) {
							if(piece!=king && piece.color!=currentColor && piece.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
			}
			else if(rowDiff==0) {
				//attacking horizontally
				if(checkingP.col<king.col) {
					//left to the king
					for(int col=checkingP.col;col<king.col;col++) {
						for(Piece piece:simPieces) {
							if(piece!=king && piece.color!=currentColor && piece.canMove(col, checkingP.row)) {
								return false;
							}
						}
					}
				}
				if(checkingP.row<king.row) {
					//right to the king
					for(int col=checkingP.col;col>king.col;col--) {
						for(Piece piece:simPieces) {
							if(piece!=king && piece.color!=currentColor && piece.canMove(col, checkingP.row)) {
								return false;
							}
						}
					}
				}
			}
			else if(colDiff==rowDiff) {
				//attacking diagonally
				if (checkingP.row < king.row) {
				    // The checking piece is above the king
				    if (checkingP.col < king.col) {
				        // The checking piece is in the upper left
				        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++) {
				            for (Piece piece : simPieces) {
				                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
				                    return false;
				                }
				            }
				        }
				    }

				    if (checkingP.col > king.col) {
				        // The checking piece is in the upper right
				        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--, row++) {
				            for (Piece piece : simPieces) {
				                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
				                    return false;
				                }
				            }
				        }
				    }
				}
				if (checkingP.row > king.row) {
					// The checking piece is below the king
				    if (checkingP.col < king.col) {
				        // The checking piece is in the lower left
				        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--) {
				            for (Piece piece : simPieces) {
				                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
				                    return false;
				                }
				            }
				        }
				    }
				    if (checkingP.col > king.col) {
				        // The checking piece is in the lower right
				        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--, row--) {
				            for (Piece piece : simPieces) {
				                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
				                    return false;
				                }
				            }
				        }
				    }
				}

			}
			
		}
		return true;
		
	}
	public boolean kingCanMove(Piece king) {
	    // Simulate if there is any square where the king can move to
	    if (isValidMove(king, -1, -1)) { return true; }
	    if (isValidMove(king,  0, -1)) { return true; }
	    if (isValidMove(king,  1, -1)) { return true; }
	    if (isValidMove(king, -1,  0)) { return true; }
	    if (isValidMove(king,  1,  0)) { return true; }
	    if (isValidMove(king, -1,  1)) { return true; }
	    if (isValidMove(king,  0,  1)) { return true; }
	    if (isValidMove(king,  1,  1)) { return true; }

	    return false;
	}
	public boolean isValidMove(Piece king, int colPlus, int rowPlus) {
	    boolean isValidMove = false;

	    // Update the king's position temporarily
	    king.col += colPlus;
	    king.row += rowPlus;

	    if (king.canMove(king.col, king.row)) {
	        if (king.hittingP != null) {
	            simPieces.remove(king.hittingP.getIndex());
	        }

	        if (!isIllegal(king)) {
	            isValidMove = true;
	        }
	    }

	    // Reset king position and restore board state
	    king.resetPosition();
	    copyPieces(pieces, simPieces);

	    return isValidMove;
	}

	//stalemate
	public boolean isStaleMate() {
		int count=0;
		for(Piece piece:pieces) {
			if(piece.color!=currentColor) count++;
		}
		
		if(count==1) {
			if(!kingCanMove(getKing(true))) return true;
		}
		return false;
	}

	public void checkCastling() {
		
		if(castlingP!=null) {
			if(castlingP.col==0) castlingP.col+=3;
			else if(castlingP.col==7) castlingP.col-=2;
			castlingP.x=castlingP.getX(castlingP.col);
		}
	}
	
	public void changePlayer() {
		if(currentColor==WHITE) {
			currentColor=BLACK;
			
			//reset blacks 2 stepped status
			for(Piece piece:pieces) {
				if(piece.color==BLACK) {
					piece.twoStepped=false;
				}
			}
		}
		else {
			currentColor=WHITE;
			
			//reset white 2 stepped status
			for(Piece piece:pieces) {
				if(piece.color==WHITE) {
					piece.twoStepped=false;
				}
			}
		}
		activeP=null;
	}

	//promotion
	public boolean canPromote() {
		if (activeP.type == Type.PAWN) {
		    if ((currentColor == WHITE && activeP.row == 0) || (currentColor == BLACK && activeP.row == 7)) {
		        
		        promoPieces.clear();
		        promoPieces.add(new Rook(currentColor, 9, 2));
		        promoPieces.add(new Knight(currentColor, 9, 3));
		        promoPieces.add(new Bishop(currentColor, 9, 4));
		        promoPieces.add(new Queen(currentColor, 9, 5));

		        return true;
		    }
		}
		return false;

	}
	public void promoting() {
		if (mouse.pressed) {
		    for (Piece piece : promoPieces) {
		        if (piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) {
		            switch (piece.type) {
		                case ROOK:   simPieces.add(new Rook(currentColor, activeP.col, activeP.row)); break;
		                case KNIGHT: simPieces.add(new Knight(currentColor, activeP.col, activeP.row)); break;
		                case BISHOP: simPieces.add(new Bishop(currentColor, activeP.col, activeP.row)); break;
		                case QUEEN:  simPieces.add(new Queen(currentColor, activeP.col, activeP.row)); break;
		                default:     break;
		            }

		            simPieces.remove(activeP.getIndex());
		            copyPieces(simPieces, pieces);
		            activeP = null;
		            promotion = false;
		            changePlayer();
		        }
		    }
		}

	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2; g2=(Graphics2D)g;
		
		//Board
		board.draw(g2);
		
		//Pieces
		for(Piece p:simPieces) {
			p.draw(g2);
		}
		
		if(activeP!=null) {
			if(canMove) {
				if(isIllegal(activeP) || opponentCanCaptureKing()) {
					g2.setColor(Color.RED);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activeP.col*Board.SQUARE_SIZE,activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}else {
					g2.setColor(Color.white);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activeP.col*Board.SQUARE_SIZE,activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
				
			}
			activeP.draw(g2);
		}
		
		//status messages printing
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Book Antiqua",Font.PLAIN,40));
		g2.setColor(Color.WHITE);
		
		if(promotion) {
			g2.drawString("Promote to:", 840, 150);
			for(Piece piece:promoPieces) {
				g2.drawImage(piece.image,piece.getX(piece.col),piece.getY(piece.row),Board.SQUARE_SIZE,Board.SQUARE_SIZE,null);
			}
		}
		else {
			if(currentColor==WHITE) {
				g2.drawString("White's Turn", 840, 550);
				if(checkingP!=null && checkingP.color==BLACK) {
					g2.setColor(Color.red);
					g2.drawString("The King", 840, 650);
					g2.drawString("is in check", 840, 700);
				}
			}
			else {
				g2.drawString("Black's Turn", 840, 250);
				if(checkingP!=null && checkingP.color==WHITE) {
					g2.setColor(Color.red);
					g2.drawString("The King", 840, 100);
					g2.drawString("is in check", 840, 150);
				}
			}
		}
		if (gameOver) {
		    String s = "";
		    if (currentColor == WHITE) {
		        s = "WHITE WINS";
		    } else {
		        s = "BLACK WINS";
		    }

		    g2.setFont(new Font("Book Antiqua", Font.BOLD, 90));
		    g2.setColor(Color.YELLOW);
		    g2.drawString(s, 200, 420);
		}

		if(staleMate) {
			g2.setFont(new Font("Book Antiqua", Font.BOLD, 90));
		    g2.setColor(Color.YELLOW);
		    g2.drawString("StaleMate", 200, 420);
		}
	}
	

}
