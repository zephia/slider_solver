import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.event.*;

public class ImageFrame extends JInternalFrame {
	Interface parent;
	JLabel picLabel;

	public ImageFrame(String name, BufferedImage image, Interface inter) {
		super(name, true, true, true, true);
		parent = inter;
		setBounds(5, 5, 250, 350);
		
		picLabel = new JLabel(new ImageIcon(image));
		Container contentPane = getContentPane();
		contentPane.add(picLabel, BorderLayout.CENTER);
	}
}