package sk.nixone.schrodingersminesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sk.nixone.Eventable;
import sk.nixone.Eventable.Invoker;

public class Game {
	public class Result {
		private boolean failed = false;
		private double minePerSecond = 0;
		
		private Result() {
			failed = true;
		}
		
		private Result(int minesDiscovered, long timeInMiliseconds) {
			failed = false;
			minePerSecond = ((double)mines) / (timeInMiliseconds/1000);
		}
		
		public double getMinesPerSecond() {
			return minePerSecond;
		}
		
		public boolean didDie() {
			return failed;
		}
	}
		
	class SchrodingerThread extends Thread {
		public SchrodingerThread() {
			super("SchrodingerThread");
			setDaemon(true);
		}
		
		@Override
		public void run() {
			while(!Game.this.isFinished() && mineCount > 0) {
				long time = halfLife / mineCount;
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					return;
				}
				if(random.nextBoolean()) {
					System.out.println("Cat died!");
					Game.this.deleteRandomMine();
				} else {
					System.out.println("Cat stays alive...");
				}
			}
		}
	}
	
	public class Field {
		public boolean hasMine;
		public boolean isMarked = false;
		public boolean isDiscovered = false;
		
		public Field(boolean hasMine) {
			this.hasMine = hasMine;
		}
		
		public void mark() {
			if(isMarked)
				throw new IllegalStateException("Field is already marked");
			isMarked = true;
		}
		
		public void unmark() {
			if(!isMarked)
				throw new IllegalStateException("Field is not marked");
			isMarked = false;
		}
		
		public void discover() {
			if(isDiscovered)
				throw new IllegalStateException("Field is discovered");
			isDiscovered = true;
		}
	}
	
	/**
	 * Event handling of UPDATED
	 */
	public static interface UpdatedListener {
		public void onGameUpdate(Game event);
	}

	private final Invoker<UpdatedListener, Game> UPDATED_INVOKER = new Invoker<UpdatedListener, Game>() {
		@Override
		public void invoke(UpdatedListener listener, Game event) {
			listener.onGameUpdate(event);
		}
	};

	public final Eventable<UpdatedListener, Game> UPDATED = new Eventable<UpdatedListener, Game>(
			UPDATED_INVOKER);
	
	/**
	 * Event handling of GAME_END
	 */
	public static interface GameEndedListener {
		public void onGameEnded(Result event);
	}

	private final Invoker<GameEndedListener, Result> GAME_END_INVOKER = new Invoker<GameEndedListener, Result>() {
		@Override
		public void invoke(GameEndedListener listener, Result event) {
			listener.onGameEnded(event);
		}
	};

	public final Eventable<GameEndedListener, Result> GAME_END = new Eventable<GameEndedListener, Result>(
			GAME_END_INVOKER);

	static private Random random = new Random();
	static private long halfLife = 120000;

	private int columns, rows, mines;
	private Field [] fields = null;
	private float difficulty;
	private Result result;
	private int mineCount = 0;
	private SchrodingerThread schrodingerThread = new SchrodingerThread();
	
	public Game(int columns, int rows, float difficulty) {
		this.columns = columns;
		this.rows = rows;
		this.difficulty = difficulty;
		
		UPDATED.addListener(new UpdatedListener() {
			@Override
			public void onGameUpdate(Game game) {
				if(game.isFinished()) {
					game.GAME_END.invoke(game.result);
				}
			}
		});
	}
	
	private void createAndStart(int startColumn, int startRow) {
		fields = new Field[rows*columns];
		
		for(int row = 0; row < rows; row++) {
			for(int column = 0; column < columns; column++) {
				if(row == startRow && startColumn == column) {
					fields[row*columns + column] = new Field(false);
				} else {
					boolean isMine = random.nextFloat() <= difficulty ? true : false;
					fields[row*columns + column] = new Field(isMine);
					if(isMine)
						mineCount++;
				}
			}
		}
		
		schrodingerThread.start();
	}
	
	public synchronized void discover(int column, int row) {
		if(isFinished())
			throw new IllegalStateException("Cannot discover if finished");
		
		if(fields == null)
			createAndStart(column, row);
		
		fields[row*columns + column].discover();
		
		checkIfFinished();

		UPDATED.invoke(this);
		
		if(isFinished()) {
			return;
		}
		
		if(getSurroundingMines(column, row) == 0) {
			for(int subRow = row - 1; subRow <= row + 1; subRow++) {
				if(subRow >= rows || subRow < 0)
					continue;
				for(int subColumn = column - 1; subColumn <= column + 1; subColumn++) {
					
					if(subColumn >= columns || subColumn < 0)
						continue;
					if(row == subRow && column == subColumn)
						continue;						
					
					if(!fields[subRow*columns + subColumn].isDiscovered) {
						discover(subColumn, subRow);
					}
				}
			}
		}
	}
	
	public synchronized void mark(int column, int row) {
		if(fields == null)
			throw new IllegalStateException("You cannot be sure to mark a field because it is not generated yet");
		fields[row*columns + column].mark();
		
		checkIfFinished();
		
		UPDATED.invoke(this);
	}
	
	public synchronized void unmark(int column, int row) {
		if(fields == null)
			throw new IllegalStateException("Field is not marked");
		fields[row*columns + column].unmark();
		
		checkIfFinished();
		
		UPDATED.invoke(this);
	}
	
	public synchronized boolean isMarked(int column, int row) {
		if(fields == null)
			return false;
		
		return fields[row*columns + column].isMarked;
	}
	
	private void checkIfFinished() {
		for(int row = 0; row < rows; row++) {
			for(int column = 0; column < columns; column++) {
				Field field = fields[row*columns + column];
				if(field.hasMine && field.isDiscovered) {
					result = new Result();
					return;
				}
			}
		}
		
		for(int row = 0; row < rows; row++) {
			for(int column = 0; column < columns; column++) {
				Field field = fields[row*columns + column];
				// if there is an unmarked mine, or there is unproperly marked field that has no mine or it is not discovered at all
				if((field.hasMine && !field.isMarked) || (!field.hasMine && field.isMarked) || (!field.isDiscovered && !field.isMarked)) {
					return;
				}
			}
		}
		
		result = new Result(1000, 500);
	}
	
	public int getSurroundingMines(int fieldColumn, int fieldRow) {
		int surroundings = 0;
		
		for(int row = fieldRow - 1; row <= fieldRow + 1; row++) {
			if(row >= rows || row < 0)
				continue;
			for(int column = fieldColumn - 1; column <= fieldColumn + 1; column++) {
				if(column >= columns || column < 0)
					continue;
				if(fields[row*columns + column].hasMine)
					surroundings++;
			}
		}
		
		return surroundings;
	}
	
	public Field getField(int column, int row) {
		if(fields == null)
			return null;
		return fields[row*columns + column];
	}
	
	public int getColumns() {
		return columns;
	}
	
	public int getRows() {
		return rows;
	}
	
	public boolean isFinished() {
		return result != null;
	}
	
	@Override
	public String toString() {
		String output = "";
		
		for(int row = 0; row < rows; row++) {
			for(int column = 0; column < columns; column++) {
				if(fields == null)
					output += "?";
				else {
					Field field = fields[row*columns + column];
					if(field.isDiscovered) {
						if(field.hasMine) {
							output += "*";
						} else {
							int count = getSurroundingMines(column, row);
							if(count == 0)
								output += " ";
							else
								output += String.valueOf(count);
						}
					} else {
						if(field.isMarked) {
							output += "!";
						} else {
							output += "?";
						}
					}
				}
			}
			output += "\n";
		}
		
		if(isFinished())
			output += "This game is already finished!";
		
		return output;
	}
	
	public synchronized void deleteRandomMine() {
		if(fields == null)
			return;
		
		List<int[]> mines = new ArrayList<int[]>();
		for(int row = 0; row < rows; row++) {
			for(int column = 0; column < columns; column++) {
				Field field = getField(column, row);
				if(field.hasMine && !field.isDiscovered && !field.isMarked) {
					mines.add(new int [] { column, row });
				}
			}
		}
		
		if(mines.isEmpty()) {
			return;
		}
		
		int [] chosen = mines.get(random.nextInt(mines.size()));
		Field f = getField(chosen[0], chosen[1]);
		f.hasMine = false;
		
		UPDATED.invoke(this);
	}
	
	public synchronized void stop() {
		if(isFinished()) {
			return;
		}
		schrodingerThread.interrupt();
	}
}
