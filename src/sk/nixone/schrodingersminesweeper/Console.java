package sk.nixone.schrodingersminesweeper;

import java.util.Scanner;

public class Console {
	public static void main(String[] args) {
		Game game = new Game(10, 10, 0.1f);
		
		System.out.print(game);
		
		Scanner scanner = new Scanner(System.in);
		
		while(true) {
			String token = scanner.next();
			if(token.contentEquals("exit")) {
				return;
			} else if(token.contentEquals("discover")) {
				boolean result = game.discover(scanner.nextInt(), scanner.nextInt());
			} else if(token.contentEquals("mark")) {
				game.mark(scanner.nextInt(), scanner.nextInt());
			} else if(token.contentEquals("unmark")) {
				game.unmark(scanner.nextInt(), scanner.nextInt());
			}
			System.out.print(game);
		}
	}
}
