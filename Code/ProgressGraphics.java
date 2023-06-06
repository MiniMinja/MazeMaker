import javax.swing.*;
import java.awt.*;
public class ProgressGraphics extends JPanel{
	private static final int WIDTH = 300, HEIGHT = 200;
	
	private double percentageToDraw;
	
	private JFrame window;
	public ProgressGraphics() {
		window = new JFrame("Generating Maze...");
		
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		window.setContentPane(this);
		window.pack();
		
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}
	
	public void setPercentage(double p) {
		percentageToDraw = p;
		this.repaint();
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		g.drawRect(10, 75, WIDTH - 20, HEIGHT - 150);
		g.fillRect(10, 75, (int)((WIDTH - 20) * percentageToDraw / 100), HEIGHT - 150);
	}
	
	public void endGraphic() {
		window.dispose();
	}
}
