package sk.nixone.schrodingersminesweeper.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.GroupLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import sk.nixone.schrodingersminesweeper.Game;

public class GameFrame extends JFrame {
	public GameFrame(Game game) {
		super("Schrodinger's Minesweeper");
		
		JPanel canvas = new GameCanvas(this, game);
		
		add(canvas);
		
		Container pane = getContentPane();
		GroupLayout layout = new GroupLayout(pane);
		pane.setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(canvas));
		layout.setVerticalGroup(layout.createParallelGroup().addComponent(canvas));
		
		setResizable(false);
		
		pack();
	}
}
