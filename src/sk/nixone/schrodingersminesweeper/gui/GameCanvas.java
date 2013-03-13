package sk.nixone.schrodingersminesweeper.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import sk.nixone.schrodingersminesweeper.Game;
import sk.nixone.schrodingersminesweeper.Game.Field;
import sk.nixone.schrodingersminesweeper.Game.UpdatedListener;

public class GameCanvas extends JPanel {
	int fieldWidth = 20;
	int fieldHeight = 20;
	
	private GameFrame frame;
	private Game game;
	
	private Image buttonEmpty, buttonMarked, discoveredMine, discoveredEmpty;
	private Image [] discoveredNumbers;
	
	public GameCanvas(GameFrame frame, Game game) {
		super();
		
		this.frame = frame;
		
		try {
			buttonEmpty = ImageIO.read(new File("res/button.png"));
			buttonMarked = ImageIO.read(new File("res/button_marked.png"));
			discoveredMine = ImageIO.read(new File("res/discovered_mine.png"));
			discoveredEmpty = ImageIO.read(new File("res/discovered_empty.png"));

			discoveredNumbers = new Image [] {
				ImageIO.read(new File("res/discovered_1.png")),
				ImageIO.read(new File("res/discovered_2.png")),
				ImageIO.read(new File("res/discovered_3.png")),
				ImageIO.read(new File("res/discovered_4.png")),
				ImageIO.read(new File("res/discovered_5.png")),
				ImageIO.read(new File("res/discovered_6.png")),
				ImageIO.read(new File("res/discovered_7.png")),
				ImageIO.read(new File("res/discovered_8.png"))
			};
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		this.game = game;
		
		setMinimumSize(new Dimension(game.getRows()*fieldWidth, game.getColumns()*fieldHeight));
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				int button = e.getButton();
				
				int column = e.getX() / fieldWidth;
				int row = e.getY() / fieldHeight;
				
				if(button == MouseEvent.BUTTON1) {
					handleLeftClick(column, row);
				} else {
					handleRightClick(column, row);
				}
			}
		});
		
		game.UPDATED.addListener(new UpdatedListener() {
			@Override
			public void onGameUpdate(Game game) {
				if(game.isFinished()) {
					System.out.println("Game is finished");
				}
				GameCanvas.this.repaint();
			}
		});
	}
	
	protected void handleLeftClick(int column, int row) {
		if(column < 0 || row < 0 || column >= game.getColumns() || row >= game.getRows())
			return;
		
		game.discover(column, row);
	}
	
	protected void handleRightClick(int column, int row) {
		if(column < 0 || row < 0 || column >= game.getColumns() || row >= game.getRows())
			return;
		
		if(game.isMarked(column, row))
			game.unmark(column, row);
		else
			game.mark(column, row);
	}
	
	@Override
	public void paintComponent(Graphics graphics) {
		for(int row = 0; row < game.getRows(); row++) {
			for(int column = 0; column < game.getColumns(); column++) {
				Field field = game.getField(column, row);
				paint(graphics, field, column, row);
			}
		}
	}
	
	private void paint(Graphics g, Field field, int column, int row) {
		int x = fieldWidth*column;
		int y = fieldHeight*row;
		int width = fieldWidth;
		int height = fieldHeight;
		
		Image image = null;
		
		if(field == null) {
			image = buttonEmpty;
		} else {
			if(field.isDiscovered) {
				if(field.hasMine) {
					image = discoveredMine;
				} else {
					int count = game.getSurroundingMines(column, row);
					if(count != 0) {
						image = discoveredNumbers[count-1];
					} else {
						image = discoveredEmpty;
					}
				}
			} else {
				if(field.isMarked) {
					image = buttonMarked;
				} else {
					image = buttonEmpty;
				}
			}
		}
		
		g.drawImage(image, x, y, x+width, y+height, 0, 0, 16, 16, null);
	}
}
