import java.util.Stack;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
public class Maze {
	
	public static final HashMap<String, Point> dirOffsets;
	static {
		dirOffsets = new HashMap<String, Point>();
		dirOffsets.put("left", new Point(0, -1));
		dirOffsets.put("up", new Point(-1, 0));
		dirOffsets.put("right", new Point(0, 1));
		dirOffsets.put("down", new Point(1, 0));
	}
	
	public static class Point{
		int row; int col;
		
		public Point(int r, int c) {
			this.row = r;
			this.col = c;
		}
		
		public Point combineWith(Point p) {
			return new Point(row + p.row, col + p.col);
		}
		
		public String toString() {
			return "(" + row + ", " + col + ")";
		}
	}
	
	public String randomDirection() {
		double r = Math.random() * 4;
		if(r < 1) return "left";
		else if(r < 2) return "up";
		else if(r < 3) return "right";
		else return "down";
	}

	public Point randomDirectionOffset() {
		double r = Math.random() * 4;
		if(r < 1) return dirOffsets.get("left");
		else if(r < 2) return dirOffsets.get("up");
		else if(r < 3) return dirOffsets.get("right");
		else return dirOffsets.get("down");
	}
	
	private int board[][];
	private int boardRows, boardCols;
	
	private int rows, cols;
	
	private int progress;
	private long startTick;
	
	public Maze(int rows, int cols) {
		if(rows < 2 || cols < 2) throw new RuntimeException("Maze size must have dimensions > 2");

		ProgressGraphics pg = new ProgressGraphics();
		startTick = System.nanoTime();
		
		this.rows = rows;
		this.cols = cols;
		
		boardRows = (rows-1) * 2 + 1;
		boardCols = (cols-1) * 2 + 1;
		board = new int[boardRows][boardCols];
		for(int i = 0;i<boardRows;i++) {
			for(int j = 0;j<boardCols;j++) {
				if(i % 2 == 1 || j % 2 == 1) {
					board[i][j] = -1;
				}
			}
		}
		
		//boolean filledBoard = false;
		
		Stack<Point> locations = new Stack<Point>();
		Point p = new Point(0, 0);
		board[boardR(p.row)][boardC(p.col)] = 1;
		locations.add(p);
		while(!locations.empty()) {
			Point toCheck = locations.peek();
			Debug.output(this.toString());
			Debug.output("locations: " );
			for(Point pl: locations) {
				Debug.output(pl.toString());
			}
			Debug.output("Checking: " + toCheck.row + " " + toCheck.col);
			if(toCheck.row == rows-1 && toCheck.col == cols-1) {
				locations.pop();
			}
			else {
				String randomDirection = randomDirection();
				int dirIndex = -1;
				switch(randomDirection) {
				case "left":
					dirIndex = 0;
					break;
				case "up":
					dirIndex = 1;
					break;
				case "right":
					dirIndex = 2;
					break;
				case "down":
					dirIndex = 3;
					break;
				}
				//System.out.println("\tChecking dir: " + randomDirection);
				Point offset = dirOffsets.get(randomDirection);
				Point otherCheck = toCheck.combineWith(offset);
				boolean[] checked = new boolean[4];
				boolean checkedAll = false;
				boolean madeConnection = false;
				while(!checkedAll && !madeConnection) {
					//System.out.println(randomDirection + " " + "(" + offset.row + ", " + offset.col + ") " + Arrays.toString(checked));
					if(checked[0] && checked[1] && checked[2] && checked[3]) {
						checkedAll = true;
					}
					else if(!checked[dirIndex]) {
						if(isWithinBoard(otherCheck.row, otherCheck.col) && 
								isEmpty(otherCheck.row, otherCheck.col)){
							connect(toCheck.row, toCheck.col, otherCheck.row, otherCheck.col);
							locations.add(otherCheck);
							progress++;
							pg.setPercentage(progressPercent());
							//System.out.println(progressPercent());
							madeConnection = true;
						}
					}
					checked[dirIndex] = true;
					
					randomDirection = randomDirection();
					switch(randomDirection) {
					case "left":
						dirIndex = 0;
						break;
					case "up":
						dirIndex = 1;
						break;
					case "right":
						dirIndex = 2;
						break;
					case "down":
						dirIndex = 3;
						break;
					}
					//System.out.println("\tChecking dir: " + randomDirection);
					offset = dirOffsets.get(randomDirection);
					otherCheck = toCheck.combineWith(offset);
				}
				if(!madeConnection) { // it's a dead end
					//backtrack and put in new location
					//markAsDeadEnd(toCheck.row, toCheck.col, toCheck.row, toCheck.col);
					locations.pop();
				}
			}
		}
		System.out.println("Time it took to generate: " + (System.nanoTime() - startTick) / 10e9 + " sec");
		pg.endGraphic();
	}
	
	public int boardR(int r) {
		return r * 2;
	}
	
	public int boardC(int c) {
		return c * 2;
	}
	
	public boolean isWithinBoard(int r, int c) {
		return 0 <= r && r < rows && 0 <= c && c < cols;
	}
	
	public boolean isEmpty(int r, int c) {
		return board[boardR(r)][boardC(c)] == 0;
	}
	
	public boolean isDeadEnd(int r, int c) {
		return board[boardR(r)][boardC(c)] == 3;
	}
	
	public boolean isConnectedElsewhere(int r, int c) {
		return board[boardR(r)][boardC(c)] == 2;
	}
	
	public boolean isStart(int r, int c) {
		return board[boardR(r)][boardC(c)] == 1;
	}
	
	public void connect(int r0, int c0, int r1, int c1) {
		board[(boardR(r0) + boardR(r1)) / 2][(boardC(c0) + boardC(c1)) / 2] = 2;
		board[boardR(r1)][boardC(c1)] = 2;
	}
	
	public void disconnect(int r0, int c0, int r1, int c1) {
		board[(boardR(r0) + boardR(r1)) / 2][(boardC(c0) + boardC(c1)) / 2] = -1;
		board[boardR(r1)][boardC(c1)] = 0;
	}
	
	public void markAsDeadEnd(int r0, int c0, int r1, int c1) {
		board[(boardR(r0) + boardR(r1)) / 2][(boardC(c0) + boardC(c1)) / 2] = 3;
		board[boardR(r1)][boardC(c1)] = 3;
	}

	
	public double progressPercent() {
		return (double)progress / (rows * cols) * 100;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<boardCols +1;i++) sb.append("XX");
		sb.append("\n");
		for(int i = 0;i<board.length;i++) {
			sb.append("X");
			for(int j = 0;j<board[i].length;j++) {
				if(board[i][j] == -1) {
					sb.append(String.format("%2s", "X"));
				}
				else if(board[i][j] == 1) {
					sb.append(String.format("%2s", "*"));
				}
				else if(board[i][j] == 3) {
					sb.append(String.format("%2s", "@"));
				}
				else if(board[i][j] == 2) {
					sb.append(String.format("%2s", " "));
				}
				else {
					sb.append(String.format("%2s", "X"));
				}
			}
			sb.append("X\n");
		}

		for(int i = 0;i<boardCols+1;i++) sb.append("XX");
		sb.append("\n");
		return sb.toString();
	}
}
