
public class Solver {
	SearchNode solutionNode;
	SearchNode twinNode;

	// find a solution to the initial board
	public Solver(Board initial) {
		MinPQ<SearchNode> pq = new MinPQ<SearchNode>();
		MinPQ<SearchNode> pqTwin = new MinPQ<SearchNode>();
		pq.insert(new SearchNode(initial, 0, null));
		pqTwin.insert(new SearchNode(initial.twin(), 0, null));
		
		boolean found = false;
		boolean foundTwin = false;
		
		while (!found && !foundTwin) {
			solutionNode = pq.delMin();
			twinNode = pqTwin.delMin();
			//pq = new MinPQ<SearchNode>();
			//pqTwin = new MinPQ<SearchNode>();
			if (solutionNode.board.isGoal()) {
				found = true;
			}
			if (twinNode.board.isGoal()) {
				foundTwin = true;
			}

			//System.out.println(solutionNode.board);
			for (Board b : solutionNode.board.neighbors()) {
				if (solutionNode.prev != null) {
					if (!b.equals(solutionNode.prev.board)) { 
						pq.insert(new SearchNode(b, solutionNode.iMoves + 1, solutionNode));
						//System.out.println(b);
					}
				}
				else
					pq.insert(new SearchNode(b, solutionNode.iMoves + 1, solutionNode));
			}
			for (Board b : twinNode.board.neighbors()) {
				if (twinNode.prev != null) {
					if (!b.equals(twinNode.prev.board))
						pqTwin.insert(new SearchNode(b, twinNode.iMoves + 1, twinNode));
				}
				else
					pqTwin.insert(new SearchNode(b, twinNode.iMoves + 1, twinNode));
			}
		}
	}
	
	private class SearchNode implements Comparable<SearchNode>{
		private Board board;
		private int iMoves;
		private SearchNode prev;
		private int iPriority;
		
		public SearchNode(Board board, int iMoves, SearchNode prev) {
			this.board = board;
			this.iMoves = iMoves;
			this.prev = prev;
			iPriority = iMoves + board.manhattan();
		}

		public int compareTo(SearchNode that) {
			if (this.iPriority < that.iPriority)
				return -1;
			if (this.iPriority > that.iPriority)
				return +1;
			return 0;
		}
	}
	
	// is the initial board solvable?
	public boolean isSolvable() {
		if (solutionNode.board.isGoal())
			return true;
		return false;
	}
	
	// return min number of moves to solve initial board; -1 if no solution
	public int moves() {
		if (isSolvable())
			return solutionNode.iMoves;
		return -1;
		
	}
	
	// return sequence of boards in a shortest solution; null if no solution
	public Iterable<Board> solution() {
		if (!isSolvable())
			return null;
		
		Stack<Board> path = new Stack<Board>();
		SearchNode currentNode = solutionNode;
		while (currentNode != null) {
			path.push(currentNode.board);
			currentNode = currentNode.prev;
		}
		return path;
	}
	
	public static void main(String[] args) {
		
		// create initial board from standard input
		int N = StdIn.readInt();
		int[][] tiles = new int[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) 
				tiles[i][j] = StdIn.readInt();
		}
		Board initial = new Board(tiles);
		
		// solve the puzzle
		Solver solver = new Solver(initial);
		
		// print solution to standard output
		if (!solver.isSolvable())
			StdOut.println("No solution possible");
		else {
			StdOut.println("Minimum number of moves = " + solver.moves());
			for (Board board : solver.solution())
				StdOut.println(board);
		}
	}
}
