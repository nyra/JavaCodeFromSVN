package PhenotypeImages;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import GeneticModels.Phenotype;
import GeneticModels.Trait;

public class PhenotypeImageMaker {

	private static final int IMAGE_SIZE = 150;
	private ArrayList<Phenotype> phenotypes;
	private Graphics2D g2d;

	private Color bodyColor;
	private Color eyeColor;

	private Color antennaColor;
	private String antennaShape;
	private int antennaNumber;

	private Color wingColor;
	private String wingShape;
	private int wingNumber;

	private Color legColor;
	private String legShape;
	private int legNumber;

	private String bodyShape;

	/**
	 * hard-coded numbers for drawing things
	 */
	// for antennae
	private static final int[] ONE_ANT = {125};
	private static final int[] TWO_ANT = {120, 130};
	private static final int[] THREE_ANT = {120, 125, 130};
	private static final int[] FOUR_ANT = {116, 122, 128, 134};
	private static final int[] FIVE_ANT = {115, 120, 125, 130, 135};
	private static final int[] SIX_ANT = {115, 119, 123, 127, 131, 135};
	// for legs & wings
	private static final int[] ONE = {72};
	private static final int[] TWO = {63, 81};
	private static final int[] THREE = {59, 73, 87};
	private static final int[] FOUR = {56, 67, 78, 89};
	private static final int[] FIVE = {47, 59, 71, 83, 95};
	private static final int[] SIX = {47, 57, 67, 77, 87, 97};

	public PhenotypeImageMaker() {
		bodyColor = Color.LIGHT_GRAY;
		eyeColor = Color.WHITE;

		antennaColor = Color.LIGHT_GRAY;
		antennaShape = "Long";
		antennaNumber = 2;

		wingColor = Color.LIGHT_GRAY;
		wingShape = "Long";
		wingNumber = 2;

		legColor = Color.LIGHT_GRAY;
		legShape = "Long";
		legNumber = 6;

		bodyShape = "Long";
	}

	public ImageIcon makeImage(ArrayList<Phenotype> phenotypes) {
		this.phenotypes = phenotypes;
		BufferedImage image = 
			new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
		g2d = (Graphics2D)image.getGraphics();

		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);

		drawHead();
		drawAbdomen();

		return new ImageIcon(image);
	}

	private void drawHead() {
		Phenotype p = findPhenotypeMatching("Body", "Color");
		if (p != null) {
			bodyColor = getColorFromString(p.getTrait().getTraitName());
		}
		g2d.setColor(bodyColor);

		//the head
		GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		path.moveTo(115, 50);
		path.lineTo(135, 50);
		path.quadTo(145, 50, 145, 60);
		path.lineTo(145, 90);
		path.quadTo(145, 100, 135, 100);
		path.lineTo(115, 100);
		path.quadTo(105, 100, 105, 90);
		path.lineTo(105, 60);
		path.quadTo(105, 50, 115, 50);
		path.closePath();
		g2d.draw(path);
		g2d.fill(path);

		//the eye
		p = findPhenotypeMatching("Eye", "Color");
		if (p != null) {
			eyeColor = getColorFromString(p.getTrait().getTraitName());
		}
		g2d.setColor(eyeColor);
		g2d.fillOval(130, 65, 15, 15);

		//antennae
		p = findPhenotypeMatching("Antenna", "Number");
		if (p != null) {
			antennaNumber = getIntFromString(p.getTrait().getTraitName());
		}

		int[] antennaLocations = null;
		switch (antennaNumber) {
		case 0:
			antennaLocations = null;
			break;
		case 1:
			antennaLocations = ONE_ANT;
			break;
		case 2:
			antennaLocations = TWO_ANT;
			break;
		case 3:
			antennaLocations = THREE_ANT;
			break;
		case 4:
			antennaLocations = FOUR_ANT;
			break;
		case 5:
			antennaLocations = FIVE_ANT;
			break;
		case 6:
			antennaLocations = SIX_ANT;
			break;
		}

		p = findPhenotypeMatching("Antenna", "Color");
		if (p != null) {
			antennaColor = getColorFromString(p.getTrait().getTraitName());
		}
		g2d.setColor(antennaColor);

		p = findPhenotypeMatching("Antenna", "Shape");
		if (p != null) {
			antennaShape = p.getTrait().getTraitName();
		}

		//draw the antennae
		if (antennaLocations != null) {
			for (int i = 0; i < antennaLocations.length; i++) {
				int x = antennaLocations[i];
				if (antennaShape.equals("Forked")) {
					g2d.drawLine(x, 50, x, 40);
					g2d.drawLine(x, 40, (x - 1), 30);
					g2d.drawLine(x, 40, (x + 1), 30);
				} else if (antennaShape.equals("Long")) {
					g2d.drawLine(x, 50, x, 20);
				} else if (antennaShape.equals("Short")) {
					g2d.drawLine(x, 50, x, 35);
				} else if (antennaShape.equals("Bent")) {
					g2d.drawLine(x, 50, x, 40);
					g2d.drawLine(x, 40, (x - 4), 30);
				} else if (antennaShape.equals("Pointy")) {
					g2d.drawLine(x, 50, x, 34);		
					path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
					path.moveTo((x - 1), 34);
					path.lineTo(x, 30);
					path.lineTo((x + 1), 34);
					path.lineTo((x - 1), 34);
					path.closePath();
					g2d.draw(path);
					g2d.fill(path);
				} else if (antennaShape.equals("Knobbed")) {
					g2d.drawLine(x, 50, x, 32);
					g2d.fillRect((x - 1), 32, 2, 2);
				} else if (antennaShape.equals("Zigzag")) {
					g2d.drawLine(x, 50, x - 1, 48);
					g2d.drawLine(x - 1, 48, x + 1, 44);
					g2d.drawLine(x + 1, 44, x - 1, 40);
					g2d.drawLine(x - 1, 40, x + 1, 36);
					g2d.drawLine(x + 1, 36, x - 1, 32);
					g2d.drawLine(x - 1, 32, x + 1, 28);
				}
			}
		}
	}

	private void drawAbdomen() {
		g2d.setColor(bodyColor);

		//the abdomen
		GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		path.moveTo(45, 55);
		path.lineTo(100, 55);
		path.quadTo(105, 55, 105, 60);
		path.lineTo(105, 90);
		path.quadTo(105, 95, 100, 95);
		path.lineTo(45, 95);
		path.quadTo(40, 95, 40, 90);
		path.lineTo(40, 60);
		path.quadTo(40, 55, 45, 55);
		path.closePath();
		g2d.draw(path);
		g2d.fill(path);

		// wings
		Phenotype p = findPhenotypeMatching("Wing", "Number");
		if (p != null) {
			wingNumber = getIntFromString(p.getTrait().getTraitName());
		}

		int[] wingLocations = null;
		switch (wingNumber) {
		case 0:
			wingLocations = null;
			break;
		case 1:
			wingLocations = ONE;
			break;
		case 2:
			wingLocations = TWO;
			break;
		case 3:
			wingLocations = THREE;
			break;
		case 4:
			wingLocations = FOUR;
			break;
		case 5:
			wingLocations = FIVE;
			break;
		case 6:
			wingLocations = SIX;
			break;
		}

		p = findPhenotypeMatching("Wing", "Color");
		if (p != null) {
			wingColor = getColorFromString(p.getTrait().getTraitName());
		}
		g2d.setColor(wingColor);

		p = findPhenotypeMatching("Wing", "Shape");
		if (p != null) {
			wingShape = p.getTrait().getTraitName();
		}

		//draw the wing(s)
		if (wingLocations != null) {
			for (int i = 0; i < wingLocations.length; i++) {
				int x = wingLocations[i];
				if (wingShape.equals("Forked")) {
					path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
					path.moveTo((x - 2), 55);
					path.lineTo(x - 2, 50);
					path.quadTo(x - 4, 50, x - 4, 48);
					path.lineTo(x - 2, 20);
					path.lineTo(x, 48);
					path.lineTo(x + 2, 20);
					path.lineTo(x + 4, 48);
					path.quadTo(x + 4, 50, x + 2, 50);
					path.lineTo(x + 2, 55);
					path.lineTo(x - 2, 55);
					path.closePath();
					g2d.draw(path);
					g2d.fill(path);
				} else if (wingShape.equals("Long")) {
					path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
					path.moveTo((x - 2), 55);
					path.lineTo(x - 2, 50);
					path.quadTo(x - 4, 50, x - 4, 48);
					path.lineTo(x - 4, 8);
					path.quadTo(x - 4, 6, x - 2, 6);
					path.lineTo(x + 2, 6);
					path.quadTo(x + 4, 6, x + 4, 8);
					path.lineTo(x + 4, 48);
					path.quadTo(x + 4, 50, x + 2, 50);
					path.lineTo(x + 2, 55);
					path.lineTo(x - 2, 55);
					path.closePath();
					g2d.draw(path);
					g2d.fill(path);
				} else if (wingShape.equals("Short")) {
					path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
					path.moveTo((x - 2), 55);
					path.lineTo(x - 2, 50);
					path.quadTo(x - 4, 50, x - 4, 48);
					path.lineTo(x - 4, 40);
					path.quadTo(x - 4, 38, x - 2, 38);
					path.lineTo(x + 2, 38);
					path.quadTo(x + 4, 38, x + 4, 40);
					path.lineTo(x + 4, 48);
					path.quadTo(x + 4, 50, x + 2, 50);
					path.lineTo(x + 2, 55);
					path.lineTo(x - 2, 55);
					path.closePath();
					g2d.draw(path);
					g2d.fill(path);
				} else if (wingShape.equals("Bent")) {
					path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
					path.moveTo((x - 2), 55);
					path.lineTo(x - 2, 50);
					path.quadTo(x - 4, 50, x - 4, 48);
					path.lineTo(x - 4, 40);
					path.lineTo(x - 20, 26);
					path.quadTo(x - 21, 24, x - 18, 24);
					path.lineTo(x - 12, 24);
					path.quadTo(x - 11, 26, x - 10, 26);
					path.lineTo(x + 4, 40);
					path.lineTo(x + 4, 48);
					path.quadTo(x + 4, 50, x + 2, 50);
					path.lineTo(x + 2, 55);
					path.lineTo(x - 2, 55);
					path.closePath();
					g2d.draw(path);
					g2d.fill(path);
				} else if (wingShape.equals("Pointy")) {
					path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
					path.moveTo((x - 2), 55);
					path.lineTo(x - 2, 50);
					path.quadTo(x - 4, 50, x - 4, 48);
					path.lineTo(x, 20);
					path.lineTo(x + 4, 48);
					path.quadTo(x + 4, 50, x + 2, 50);
					path.lineTo(x + 2, 55);
					path.lineTo(x - 2, 55);
					path.closePath();
					g2d.draw(path);
					g2d.fill(path);
				} else if (wingShape.equals("Knobbed")) {
					path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
					path.moveTo((x - 2), 55);
					path.lineTo(x - 2, 36);
					path.quadTo(x - 4, 36, x - 4, 34);
					path.lineTo(x - 4, 22);
					path.quadTo(x - 4, 20, x - 2, 20);
					path.lineTo(x + 2, 20);
					path.quadTo(x + 4, 20, x + 4, 22);
					path.lineTo(x + 4, 34);
					path.quadTo(x + 4, 36, x + 2, 36);
					path.lineTo(x + 2, 55);
					path.lineTo(x - 2, 55);
					path.closePath();
					g2d.draw(path);
					g2d.fill(path);
				} else if (wingShape.equals("Zigzag")) {
					path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
					path.moveTo((x - 2), 55);
					path.lineTo(x - 2, 50);
					path.lineTo(x - 4, 45);
					path.lineTo(x, 35);
					path.lineTo(x - 4, 25);
					path.lineTo(x, 15);
					path.lineTo(x + 4, 15);
					path.lineTo(x, 25);
					path.lineTo(x + 4, 35);
					path.lineTo(x, 45);
					path.lineTo(x + 2, 50);
					path.lineTo(x + 2, 55);
					path.lineTo(x - 2, 55);
					path.closePath();
					g2d.draw(path);
					g2d.fill(path);
				}
			}
		}

		// legs
		p = findPhenotypeMatching("Leg", "Number");
		if (p != null) {
			legNumber = getIntFromString(p.getTrait().getTraitName());
		}

		int[] legLocations = null;
		switch (legNumber) {
		case 0:
			legLocations = null;
			break;
		case 1:
			legLocations = ONE;
			break;
		case 2:
			legLocations = TWO;
			break;
		case 3:
			legLocations = THREE;
			break;
		case 4:
			legLocations = FOUR;
			break;
		case 5:
			legLocations = FIVE;
			break;
		case 6:
			legLocations = SIX;
			break;
		}

		p = findPhenotypeMatching("Leg", "Color");
		if (p != null) {
			legColor = getColorFromString(p.getTrait().getTraitName());
		}
		g2d.setColor(legColor);

		p = findPhenotypeMatching("Leg", "Shape");
		if (p != null) {
			legShape = p.getTrait().getTraitName();
		}

		//draw the leg(s)
		if (legLocations != null) {
			for (int i = 0; i < legLocations.length; i++) {
				int x = legLocations[i];
				if (legShape.equals("Forked")) {
					path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
					path.moveTo((x - 1), 95);
					path.lineTo(x - 1, 105);
					path.lineTo(x - 4, 115);
					path.lineTo(x, 108);
					path.lineTo(x + 4, 115);
					path.lineTo(x + 1, 105);
					path.lineTo(x + 1, 95);
					path.lineTo(x - 1, 95);
					path.closePath();
					g2d.draw(path);
					g2d.fill(path);
				} else if (legShape.equals("Long")) {
					path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
					path.moveTo((x - 1), 95);
					path.lineTo(x - 1, 120);
					path.lineTo(x, 121);
					path.lineTo(x + 1, 120);
					path.lineTo(x + 1, 95);
					path.lineTo(x - 1, 95);
					path.closePath();
					g2d.draw(path);
					g2d.fill(path);
				} else if (legShape.equals("Short")) {
					path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
					path.moveTo((x - 1), 95);
					path.lineTo(x - 1, 104);
					path.lineTo(x, 105);
					path.lineTo(x + 1, 104);
					path.lineTo(x + 1, 95);
					path.lineTo(x - 1, 95);
					path.closePath();
					g2d.draw(path);
					g2d.fill(path);
				} else if (legShape.equals("Bent")) {
					path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
					path.moveTo((x - 1), 95);
					path.lineTo(x - 1, 105);
					path.lineTo(x - 5, 115);
					path.lineTo(x - 4, 116);
					path.lineTo(x - 3, 115);
					path.lineTo(x + 1, 105);
					path.lineTo(x + 1, 95);
					path.lineTo(x - 1, 95);
					path.closePath();
					g2d.draw(path);
					g2d.fill(path);
				} else if (legShape.equals("Pointy")) {
					path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
					path.moveTo((x - 1), 95);
					path.lineTo(x - 1, 110);
					path.lineTo(x - 4, 110);
					path.lineTo(x, 115);
					path.lineTo(x + 4, 110);
					path.lineTo(x + 1, 110);
					path.lineTo(x + 1, 95);
					path.lineTo(x - 1, 95);
					path.closePath();
					g2d.draw(path);
					g2d.fill(path);
				} else if (legShape.equals("Knobbed")) {
					path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
					path.moveTo((x - 1), 95);
					path.lineTo(x - 1, 110);
					path.quadTo(x - 3, 110, x - 3, 112);
					path.lineTo(x - 3, 114);
					path.quadTo(x - 3, 116, x - 1, 116);
					path.lineTo(x + 1, 116);
					path.quadTo(x + 3, 116, x + 3, 114);
					path.lineTo(x + 3, 112);
					path.quadTo(x + 3, 110, x + 1, 110);
					path.lineTo(x + 1, 95);
					path.lineTo(x - 1, 95);
					path.closePath();
					g2d.draw(path);
					g2d.fill(path);
				} else if (legShape.equals("Zigzag")) {
					path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
					path.moveTo((x - 1), 95);
					path.lineTo(x - 1, 99);
					path.lineTo(x - 3, 103);
					path.lineTo(x + 1, 110);
					path.lineTo(x - 3, 118);
					path.lineTo(x - 2, 119);
					path.lineTo(x - 1, 118);
					path.lineTo(x + 3, 110);
					path.lineTo(x - 1, 103);
					path.lineTo(x + 1, 99);
					path.lineTo(x + 1, 95);
					path.lineTo(x - 1, 95);
					path.closePath();
					g2d.draw(path);
					g2d.fill(path);
				}
			}
		}

		// body
		p = findPhenotypeMatching("Body", "Color");
		if (p != null) {
			bodyColor = getColorFromString(p.getTrait().getTraitName());
		}
		g2d.setColor(bodyColor);

		p = findPhenotypeMatching("Body", "Shape");
		if (p != null) {
			bodyShape = p.getTrait().getTraitName();
		}

		if (bodyShape.equals("Forked")) {
			path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			path.moveTo(35, 55);
			path.quadTo(40, 55, 40, 60);
			path.lineTo(40, 90);
			path.quadTo(40, 95, 35, 95);
			path.lineTo(5, 85);
			path.lineTo(30, 75);
			path.lineTo(5, 65);
			path.lineTo(35, 55);
			path.closePath();
			g2d.draw(path);
			g2d.fill(path);
		} else if (bodyShape.equals("Long")) {
			path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			path.moveTo(35, 55);
			path.quadTo(40, 55, 40, 60);
			path.lineTo(40, 90);
			path.quadTo(40, 95, 35, 95);
			path.lineTo(10, 95);
			path.quadTo(5, 95, 5, 90);
			path.lineTo(5, 60);
			path.quadTo(5, 55, 10, 55);
			path.lineTo(35, 55);
			path.closePath();
			g2d.draw(path);
			g2d.fill(path);
		} else if (bodyShape.equals("Short")) {
			path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			path.moveTo(35, 55);
			path.quadTo(40, 55, 40, 60);
			path.lineTo(40, 90);
			path.quadTo(40, 95, 35, 95);
			path.lineTo(25, 95);
			path.quadTo(20, 95, 20, 90);
			path.lineTo(20, 60);
			path.quadTo(20, 55, 25, 55);
			path.lineTo(35, 55);
			path.closePath();
			g2d.draw(path);
			g2d.fill(path);
		} else if (bodyShape.equals("Bent")) {
			path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			path.moveTo(35, 55);
			path.quadTo(40, 55, 40, 60);
			path.lineTo(40, 90);
			path.quadTo(40, 95, 35, 95);
			path.lineTo(25, 85);
			path.lineTo(25, 100);
			path.quadTo(25, 110, 15, 110);
			path.quadTo(5, 110, 5, 100);
			path.lineTo(5, 70);
			path.lineTo(25, 55);
			path.lineTo(35, 55);
			path.closePath();
			g2d.draw(path);
			g2d.fill(path);
		} else if (bodyShape.equals("Pointy")) {
			path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			path.moveTo(35, 55);
			path.quadTo(40, 55, 40, 60);
			path.lineTo(40, 90);
			path.quadTo(40, 95, 35, 95);
			path.quadTo(25, 80, 5, 75);
			path.quadTo(25, 70, 35, 55);
			path.closePath();
			g2d.draw(path);
			g2d.fill(path);
		} else if (bodyShape.equals("Knobbed")) {
			path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			path.moveTo(35, 55);
			path.quadTo(40, 55, 40, 60);
			path.lineTo(40, 90);
			path.quadTo(40, 95, 35, 95);
			path.quadTo(30, 85, 20, 85);
			path.quadTo(20, 90, 15, 90);
			path.lineTo(10, 90);
			path.quadTo(5, 90, 5, 85);
			path.lineTo(5, 65);
			path.quadTo(5, 60, 10, 60);
			path.lineTo(15, 60);
			path.quadTo(20, 60, 20, 65);
			path.quadTo(30, 60, 35, 55);
			path.closePath();
			g2d.draw(path);
			g2d.fill(path);
		} else if (bodyShape.equals("Zigzag")) {
			path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			path.moveTo(35, 55);
			path.quadTo(40, 55, 40, 60);
			path.lineTo(40, 90);
			path.quadTo(40, 95, 35, 95);
			path.quadTo(30, 95, 30, 90);
			path.lineTo(30, 80);
			path.lineTo(25, 75);
			path.lineTo(20, 80);
			path.lineTo(15, 75);
			path.lineTo(10, 80);
			path.lineTo(5, 75);
			path.lineTo(5, 65);
			path.lineTo(10, 70);
			path.lineTo(15, 65);
			path.lineTo(20, 70);
			path.lineTo(25, 65);
			path.lineTo(30, 70);
			path.lineTo(30, 60);
			path.quadTo(30, 55, 35, 55);
			path.closePath();
			g2d.draw(path);
			g2d.fill(path);
		}
	}

	//see if the organism has a specified phenotype
	// for this body part and type & return it
	// if not, return null
	private Phenotype findPhenotypeMatching(String bodyPart, String type) {
		Phenotype result = null;
		for (int i = 0; i < phenotypes.size(); i++) {
			Phenotype p = phenotypes.get(i);
			Trait t = p.getTrait();
			if (t.getBodyPart().equals(bodyPart) && t.getType().equals(type)) {
				result = p;
				break;
			}
		}
		return result;
	}

	private Color getColorFromString(String s) {
		if (s.equals("Red")) return Color.RED;
		if (s.equals("Green")) return Color.GREEN;
		if (s.equals("Blue")) return Color.BLUE;
		if (s.equals("Yellow")) return Color.YELLOW;
		if (s.equals("Purple")) return Color.MAGENTA;
		if (s.equals("Black")) return Color.BLACK;
		if (s.equals("Brown")) return new Color(165, 42, 42);
		if (s.equals("Gray")) return Color.LIGHT_GRAY;
		else return Color.WHITE;
	}

	private int getIntFromString(String s) {
		if (s.equals("No")) return 0;
		if (s.equals("One")) return 1;
		if (s.equals("Two")) return 2;
		if (s.equals("Three")) return 3;
		if (s.equals("Four")) return 4;
		if (s.equals("Five")) return 5;
		if (s.equals("Six")) return 6;
		else return 0;
	}
}