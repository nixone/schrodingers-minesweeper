package sk.nixone.schrodingersminesweeper.gui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import sk.nixone.schrodingersminesweeper.Game;
import sk.nixone.schrodingersminesweeper.Game.GameEndedListener;
import sk.nixone.schrodingersminesweeper.Game.Result;

/**
 *
 * @author nixone
 */
public class MainFrame extends JFrame {
    static public void main(String [] arguments) {
		MainFrame frame = new MainFrame();
		frame.setVisible(true);
    }

	private JLabel welcomeLabel = new JLabel(
			"<html>"+
			"<div style=\"text-align: center;\">"+
			"<h3>Welcome in</h3>"+
			"<h2>Schrodingers Minesweeper</h2>"+
			"It's like an ordinery minesweeper<br>"+
			"just that undiscovered mines disappear<br>"+
			"at a random time :)<br>"+
			"<h2>Let's play!</h2>"+
			"</div></html>"
	);
	
	private JButton newGameButton = new JButton("Start new game");
	
	private JComboBox<Float> difficultySelector = new JComboBox<Float>();
	
	private Game game = null;
	private GameFrame gameFrame = null;
	
	public MainFrame() {
		super("Schrodingers Minesweeper main menu");
		
		setUpComponents();
		setUpLayout();
		setUpMainMenu();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pack();
		setLocationByPlatform(true);
		setResizable(false);
	}
	
	private void setUpComponents() {
		add(welcomeLabel);
		add(newGameButton);
		
		newGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainFrame.this.createNewGame();
			}
		});
	}
	
	private void setUpLayout() {
		Container pane = getContentPane();
		
		GroupLayout layout = new GroupLayout(pane);
		pane.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addComponent(welcomeLabel, GroupLayout.Alignment.CENTER)
				.addComponent(newGameButton, GroupLayout.Alignment.CENTER)
		);
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(welcomeLabel)
				.addComponent(newGameButton)
		);
	}
	
	private void setUpMainMenu() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu gameMenu = new JMenu("Game");
		
		JMenuItem newGameItem = new JMenuItem("Create new game");
		newGameItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainFrame.this.createNewGame();
			}
		});
		
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainFrame.this.dispose();
			}
		});
		
		gameMenu.add(newGameItem);
		gameMenu.addSeparator();
		gameMenu.add(exitItem);
		
		menuBar.add(gameMenu);
		
		JMenu infoMenu = new JMenu("Info");
		
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(MainFrame.this, "Created by nixone @ hackcraft.sk");
			}
		});
		
		infoMenu.add(about);
		
		menuBar.add(infoMenu);
		
		this.setJMenuBar(menuBar);
	}
	
	private void createNewGame() {
		game = new Game(20, 20, 0.125f);
		
		gameFrame = new GameFrame(game);
		gameFrame.setLocation(this.getLocation());
		
		game.GAME_END.addListener(new GameEndedListener() {
			@Override
			public void onGameEnded(Result result) {
				endGame();
				
				ResultDialog resultDialog = new ResultDialog(MainFrame.this, result);
				resultDialog.setVisible(true);
				resultDialog.requestFocus();
			}
		});
		
		this.setVisible(false);
		gameFrame.setVisible(true);
	}
	
	public void endGame() {
		if(game == null) {
			throw new IllegalStateException("Cannot end game. No game playing!");
		}
		
		gameFrame.dispose();
		gameFrame = null;
		game.stop();
		game = null;
		
		this.setVisible(true);
		this.requestFocus();
	}
}
