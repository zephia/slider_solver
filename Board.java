
public class Board {

	private int[][] board;
	private int[][] goal;
	private int N;
	
	// construct a board from an N-by-N array of tiles, where tiles[i][j] = 
	// tile in row i, column j
	public Board(int[][] tiles) {
		N = tiles.length;
		board = new int[N][N];
		goal = new int[N][N];
		int k = 1;
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) { 
				board[i][j] = tiles[i][j];
				goal[i][j] = (k++);
			}
		}
		goal[N - 1][N - 1] = 0;
	}
	
	// private function that swaps two tiles on the board
	private void swap(int[][] swapBoard, int ia, int ja, int ib, int jb) {
		int iTemp = swapBoard[ia][ja];
		swapBoard[ia][ja] = swapBoard[ib][jb];
		swapBoard[ib][jb] = iTemp;
	}
	
	// number of blocks out of place
	public int hamming() {
		int iNum = 0;
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if ((board[i][j] != goal[i][j]) && (board[i][j] != 0))
					iNum++;
			}
		}
		
		return iNum;
	}
	
	// sum of Manhattan distances between blocks and goal
	public int manhattan() {
		int iCount = 0;
		int iTarget = 0;
		int jTarget = 0;
		int iTemp = 0;
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				iTemp = board[i][j];
				if ((iTemp != goal[i][j]) && (iTemp != 0)) {
					iTarget = (iTemp - 1) / N;
					jTarget = (iTemp - 1) % N;
					iCount += (Math.abs(iTarget - i) + Math.abs(jTarget - j));
				}
			}
		}
		
		return iCount;
	}
	
	// is this board the goal board?
	public boolean isGoal() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (board[i][j] != goal[i][j])
					return false;
			}
		}
		return true;
	}
	
	// a board obtained by exchanging two adjacent blocks in the same row
	public Board twin() {
		int[][] newBoard = new int[N][N];
		int iRow = 0;
		int iTemp = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				newBoard[i][j] = board[i][j];
				if (newBoard[i][j] != 0)
					iTemp++;
			}
			if (iTemp == N)
				iRow = i;
			iTemp = 0;
		}
		
		swap(newBoard, iRow, 0, iRow, 1);
		
		return new Board(newBoard);
	}
	
	// does this board equal y?
	public boolean equals(Object y) {
		if (y == this)
			return true;
		if (y == null)
			return false;
		if (y.getClass() != this.getClass())
			return false;
		Board that = (Board) y;
		if (this.N != that.N)
			return false;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (this.board[i][j] != that.board[i][j])
					return false;
			}
		}
		return true;
	}
	
	// all neighboring boards
	public Iterable<Board> neighbors() {
		int iFree = 0;
		int jFree = 0;
		Stack<Board> neighbors = new Stack<Board>();
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (board[i][j] == 0) {
					iFree = i;
					jFree = j;
				}
			}
		}
		
		if (iFree != 0) {
			swap(board, iFree, jFree, iFree - 1, jFree);
			neighbors.push(new Board(board));
			swap(board, iFree, jFree, iFree - 1, jFree);
		}
		if (iFree != (N - 1)) {
			swap(board, iFree, jFree, iFree + 1, jFree);
			neighbors.push(new Board(board));
			swap(board, iFree, jFree, iFree + 1, jFree);
		}
		if (jFree != 0) {
			swap(board, iFree, jFree, iFree, jFree - 1);
			neighbors.push(new Board(board));
			swap(board, iFree, jFree, iFree, jFree - 1);
		}
		if (jFree != (N - 1)) {
			swap(board, iFree, jFree, iFree, jFree + 1);
			neighbors.push(new Board(board));
			swap(board, iFree, jFree, iFree, jFree + 1);
		}
		
		return neighbors;
		
	}
	
	public int[][] returnBoard() {
		return board;
	}
	
	// string representation of the board in the output format specified above
	public String toString() {		
		StringBuilder s = new StringBuilder();
		s.append(N + "\n");
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) 
				s.append(String.format("%2d ", board[i][j]));
			s.append("\n");
		}
		return s.toString();
	}
	
	public static void main(String[] args) {
		int[][] tiles = new int[][] {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
		
		Board boardA = new Board(tiles);
		System.out.println(boardA);
		System.out.println(boardA.isGoal());
		System.out.println(boardA.hamming());
		System.out.println(boardA.manhattan());
		
		Board boardB = boardA.twin();
		System.out.println(boardB);
		System.out.println(boardB.hamming());
		System.out.println(boardB.manhattan());
		System.out.println(boardB.isGoal());
		
		Iterable<Board> neighbors = boardA.neighbors();
		for (Board a : neighbors)
			System.out.println(a);
	}
}
