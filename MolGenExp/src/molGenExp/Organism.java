package molGenExp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.swing.ImageIcon;

import molBiol.ExpressedGene;

public class Organism implements Serializable {
	
	public static final int GREENHOUSE = 0;
	
	private static int imageSize = 50; //size of image for greenhouse
	private String name;
	private int trayLocation;  // 0 = greenhouse
	
	private ColorModel colorModel;
	
	private ExpressedGene gene1;
	private ExpressedGene gene2;
	private Color color;
	private ImageIcon image;

	public Organism(int location,
			String name, 
			ExpressedGene gene1, 
			ExpressedGene gene2,
			ColorModel colorModel) {
		this.trayLocation = location;
		this.name = name;
		this.gene1 = gene1;
		this.gene2 = gene2;
		this.colorModel = colorModel;
		
		//calculate color
		color = colorModel.mixTwoColors(
				gene1.getFoldedPolypeptide().getColor(), 
				gene2.getFoldedPolypeptide().getColor());
		
		//generate icon
		BufferedImage pic = new BufferedImage(
				imageSize,
				imageSize,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = pic.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, 50, 50);
		g.setColor(color);
		int[] xPoints = {0, 22, 25, 28, 50,
				36, 50, 36, 50, 28,
				25, 22, 0, 14, 0, 14};
		int[] yPoints = {0, 14, 0, 14, 0,
				22, 25, 28, 50, 36,
				50, 36, 50, 28, 25, 22};
		int nPoints = xPoints.length;
		g.fill(new Polygon(xPoints, yPoints, nPoints));
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(25, 25, 22, 14);
		g.drawLine(25, 25, 28, 14);
		g.drawLine(25, 25, 36, 22);
		g.drawLine(25, 25, 36, 28);		
		g.drawLine(25, 25, 28, 36);
		g.drawLine(25, 25, 22, 36);
		g.drawLine(25, 25, 14, 28);
		g.drawLine(25, 25, 14, 22);		

		g.dispose();

		image = new ImageIcon(pic);
	}
	
	public String getName() {
		return name;
	}

	public ExpressedGene getGene1() {
		return gene1;
	}

	public ExpressedGene getGene2() {
		return gene2;
	}

	public Color getColor() {
		return color;
	}
		
	public ImageIcon getImage() {
		return image;
	}

	public int getTrayLocation() {
		return trayLocation;
	}
	
	public void setTrayLocation(int loc) {
		trayLocation = loc;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
