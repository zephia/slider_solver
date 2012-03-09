import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.io.*;
import java.util.*;
import javax.swing.Timer;

public class Interface extends JFrame {

	private JButton uploadButton, divideButton, shuffleButton, solveButton;
	private JLayeredPane desktop;
	private JTextField input;
	private JInternalFrame shuffleFrame;
	private JLabel shuffleLabel;
	private BufferedImage puzzleImage;
	private BufferedImage resizedImage;
	private BufferedImage[][] tiledImage;
	private BufferedImage shuffleImage;
	private Graphics2D g;
	private final int imageWidth = 200;
	private final int imageHeight = 300;
	private int tilesImageWidth;
	private int tilesImageHeight;
	private int partWidth;
	private int partHeight;
	private int N;
	private int totalTiles;
	private int[] indices;
	private int[][] boardIndex;
	private Queue<Board> path;
	private Board currentBoard;
	
	public Interface() {
		super("Slider Solver");
		setSize(500, 500);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		Container contentPane = getContentPane();
		
		desktop = new JDesktopPane();
		contentPane.add(desktop, BorderLayout.CENTER);
		
		uploadButton = new JButton("Upload Image");
		divideButton = new JButton("Divide");
		shuffleButton = new JButton("Shuffle Tiles");
		solveButton = new JButton("Solve Puzzle");
		
		uploadButton.setVisible(true);
		divideButton.setVisible(false);
		shuffleButton.setVisible(false);
		solveButton.setVisible(false);
		
		uploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				uploadAction(e);
			}
		});
		divideButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				divideAction(e);
			}
		});
		shuffleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shuffleAction(e);
			}
		});
		solveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				solveAction(e);
			}
		});
		
		input = new JTextField();
		input.setColumns(5);
		input.setVisible(false);
		
		JPanel panel = new JPanel();
		panel.add(uploadButton);
		panel.add(input);
		panel.add(divideButton);
		panel.add(shuffleButton);
		panel.add(solveButton);
		
		panel.setVisible(true);
		contentPane.add(panel, BorderLayout.SOUTH);
		
		contentPane.setVisible(true);
	}
	
	public void uploadAction(ActionEvent e) {
		JFileChooser imageChooser = new JFileChooser();
		String fileName = new String();
		imageChooser.setMultiSelectionEnabled(false);
		imageChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int option = imageChooser.showOpenDialog(Interface.this);
		if (option == JFileChooser.APPROVE_OPTION) {
			File image = imageChooser.getSelectedFile();
			fileName = image.getName();
			int iLength = fileName.length();
			String extension = fileName.toLowerCase().substring(iLength - 4, iLength);
			System.out.println(extension);
			if ((extension.equals(".png")) || (extension.equals(".jpg")) || 
					(extension.equals("jpeg"))) {
				try {
					puzzleImage = ImageIO.read(image);
				} catch(IOException error) {
					JOptionPane.showMessageDialog(null, error.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
				input.setVisible(true);
				divideButton.setVisible(true);
				showImage();
			}
			else
				JOptionPane.showMessageDialog(null, "Invalid file type!", "Error", 
						JOptionPane.ERROR_MESSAGE);
		}
		System.out.println("upload");
		System.out.println(fileName);
	}
	
	public void divideAction(ActionEvent e) {
		String dim = input.getText();
		if (dim == null)
			JOptionPane.showMessageDialog(null,  "Input an integer!", "Error",
					JOptionPane.ERROR_MESSAGE);
		try {
			N = Integer.parseInt(dim);
		} catch(NumberFormatException error) {
			JOptionPane.showMessageDialog(null, "Wrong format! Enter an integer!", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		
		totalTiles = N * N;
		indices = new int[totalTiles];
		boardIndex = new int[N][N];
		for (int i = 0; i < totalTiles; i++) {
			indices[i] = i + 1;
		}
		indices[totalTiles - 1] = 0;
		
		int width = resizedImage.getWidth();
		int height = resizedImage.getHeight();
		partWidth = width / N;
		partHeight = height / N;
		
		tilesImageWidth = partWidth * N;
		tilesImageHeight = partHeight * N;
		
		tiledImage = new BufferedImage[N][N];
		int k = 1;
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				tiledImage[i][j] = 
						resizedImage.getSubimage(i * partWidth, j * partHeight, partWidth, partHeight);
				boardIndex[i][j] = k++;
			}
		}
		tiledImage[N - 1][N - 1] = null;
		boardIndex[N - 1][N - 1] = 0;
		
		shuffleFrame = new JInternalFrame("Shuffle", true, true, true, true);
		shuffleFrame.setBounds(250, 5, 230, 330);
		
		desktop.add(shuffleFrame, new Integer(1));
		shuffleFrame.setVisible(true);
				
		drawTiles();
		
		shuffleButton.setVisible(true);
		System.out.println("divide");
	}
	
	public void shuffleAction(ActionEvent e) {
		StdRandom.shuffle(indices);
		int indexTracker = 0;
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				boardIndex[i][j] = indices[indexTracker++];
				System.out.print(boardIndex[i][j] + " ");
			}
			System.out.println();
		}
		
		drawTiles();
		solveButton.setVisible(true);
		System.out.println("shuffle");
	}
	
	public void solveAction(ActionEvent e) {
		System.out.println("solve");
		
		Board initial = new Board(boardIndex);
		Solver solver = new Solver(initial);
		path = new Queue<Board>();
		
		if (!solver.isSolvable())
			JOptionPane.showMessageDialog(null, "This is not solvable! Shuffle again!", "Error", 
					JOptionPane.ERROR_MESSAGE);
		else {
			for (Board board : solver.solution()) {
				path.enqueue(board);
				System.out.println(board);
			}
			startTimer();
		}
	}
	
	public void startTimer() {
		Timer timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (!path.isEmpty()) {
					Board temp = path.dequeue();
					boardIndex = temp.returnBoard();
					drawTiles();
				}
			}
		});
		if (timer.isRunning() && path.isEmpty())
			timer.stop();
		timer.start();
	}
	
	public void showImage() {
		int width = puzzleImage.getWidth();
		int height = puzzleImage.getHeight();
		double wFactor = width / imageWidth;
		double hFactor = height / imageHeight;
		
		int newWidth = 0;
		int newHeight = 0;
		
		if (wFactor > hFactor) {
			newWidth = imageWidth;
			newHeight = (int) (height / wFactor);
		}
		else {
			newWidth = (int) (width / hFactor);
			newHeight = imageHeight;
		}
		
		resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		g = resizedImage.createGraphics();
		g.drawImage(puzzleImage, 0, 0, newWidth, newHeight, null);
		g.dispose();
		
		JInternalFrame imageFrame = new JInternalFrame("Original", true, true, true, true);
		imageFrame.setBounds(5, 5, 230, 330);
		JLabel picLabel = new JLabel(new ImageIcon(resizedImage));
		imageFrame.getContentPane().add(picLabel, BorderLayout.CENTER);
		
		desktop.add(imageFrame, new Integer(1));
		imageFrame.setVisible(true);
		
	}
	
	public void drawTiles() {
		shuffleImage = new BufferedImage(tilesImageWidth, tilesImageHeight, BufferedImage.TYPE_INT_ARGB);		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (boardIndex[i][j] == 0)
					continue;
				int currentIndex = boardIndex[i][j];
				int correctI = (currentIndex - 1) / N;
				int correctJ = (currentIndex - 1) % N;
				System.out.println(currentIndex + " " + correctI + " " + correctJ);
				for (int m = 0; m < partWidth; m++) {
					for (int n = 0; n < partHeight; n++) {
						int rgb = tiledImage[correctI][correctJ].getRGB(m, n);
						shuffleImage.setRGB((i * partWidth) + m, (j * partHeight) + n, rgb);
					}
				}
			}
		}
		
		
		shuffleFrame.getContentPane().removeAll();
		shuffleLabel = new JLabel(new ImageIcon(shuffleImage));
		shuffleFrame.getContentPane().add(shuffleLabel, BorderLayout.CENTER);
		SwingUtilities.updateComponentTreeUI(shuffleFrame.getContentPane());
	}

	public static void main(String args[]) {
		Interface start = new Interface();
		start.setVisible(true);
	}
}
