package sk.nixone.schrodingersminesweeper.gui;

import java.awt.Container;

import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import sk.nixone.schrodingersminesweeper.Game.Result;

public class ResultDialog extends JDialog {
	private Result result;
	private JLabel resultLabel;
	
	public ResultDialog(JFrame frame, Result result) {
		super(frame, "Schrodingers Minesweeper Game Result");
		
		this.result = result;
		
		setUpComponents();
		setUpLayout();
		
		setResizable(false);

		pack();
	}
	
	private void setUpComponents() {
		resultLabel = new JLabel(
				"<html><div style=\"text-align: center;\">"+
				"<h2>Result of the game</h2>"+
				"<h1>You "+(result.didDie() ? "failed" : "won")+"!</h1>"+
				"</div></html>"
		);
		
		add(resultLabel);
	}

	private void setUpLayout() {
		Container pane = getContentPane();
		
		GroupLayout layout = new GroupLayout(pane);
		pane.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(resultLabel));
		layout.setHorizontalGroup(layout.createParallelGroup().addComponent(resultLabel));
	}
}
