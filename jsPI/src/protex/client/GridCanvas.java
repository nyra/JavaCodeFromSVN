//GridCanvas.java


//Copyright 2004, Ethan Bolker and Bogdan Calota
/* 
 * License Information
 * 
 * This  program  is  free  software;  you can redistribute it and/or modify it 
 * under  the  terms  of  the GNU General Public License  as  published  by the 
 * Free  Software  Foundation; either version 2  of  the  License,  or (at your 
 * option) any later version.
 */

//.1 .2 .3 .4 .5 .6 .7 .8 

package protex.client;


import protex.client.java.awt.Color;
import protex.client.java.awt.Dimension;
import protex.client.java.awt.Graphics;
import protex.client.java.awt.geom.Ellipse2D;
import protex.client.java.awt.geom.Rectangle2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

/**
 * Display a Grid.
 *  
 */
public abstract class GridCanvas {
	
	protected int cellRadius = 20;
	protected int cellDiameter = 2 * cellRadius;
	protected int size; // 2*numAcids + 1
	private int width, height;
	protected Grid grid = null;
	protected int numAcids;
	protected Polypeptide pp;
	private Dimension requiredCanvasSize;
	private boolean strictMatchDisplayMode;
	public Canvas canvas;
	private Context2d ctx;
	private Graphics g;
	public boolean ssBondsOn; //Change later on
	boolean buildTargetShapeMode = false; //Change later on

	public GridCanvas() {
		strictMatchDisplayMode = false;
	}
	
	public GridCanvas(int width, int height) {
		this.width = width;
		this.height = height;
		canvas = Canvas.createIfSupported();
		canvas.setPixelSize(width, height);
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
		ctx = canvas.getContext2d();
		g = new Graphics(ctx);
		strictMatchDisplayMode = false;
	}
	
	public void setCanvas(int width, int height) {
		this.width = width;
		this.height = height;
		canvas = Canvas.createIfSupported();
		canvas.setPixelSize(width, height);
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
		ctx = canvas.getContext2d();
		g = new Graphics(ctx);
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public Dimension getRequiredCanvasSize() {
		calculateRequiredCanvasSize();
		return requiredCanvasSize;
	}

	protected void setGrid(Grid grid) {
		this.grid = grid;
		this.size = grid.getSize();
		this.pp = grid.getPP();
		numAcids = pp.getLength();
	}
	
	public void setStrictMatchDisplayMode(boolean b) {
		strictMatchDisplayMode = b;
	}
	
	public void setssBondsOn(boolean b) {
		ssBondsOn = b;
	}
	
	public void setBuildTargetShapeMode(boolean b) {
		buildTargetShapeMode = b;
	}

	/**
	 * Added by [DAP] on 3 Mar 2005
	 * 
	 * @return Grid
	 */
	protected Grid getGrid() {
		return grid;
	}

	protected GridPoint getMin() {
		int minX = size;
		int minY = size;
		int minZ = size;
		for (int i = 0; i < numAcids; i++) {
			AcidInChain a = pp.getAminoAcid(i);
			if (a.xyz.x < minX)
				minX = a.xyz.x;
			if (a.xyz.y < minY)
				minY = a.xyz.y;
			if (a.xyz.z < minZ)
				minZ = a.xyz.z;
		}
		return new GridPoint(minX - 1, minY - 1, minZ - 1);
	}

	private GridPoint getMin(GridPoint[] points) {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int minZ = Integer.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			if (points[i].x < minX)
				minX = points[i].x;
			if (points[i].y < minY)
				minY = points[i].y;
			if (points[i].z < minZ)
				minZ = points[i].z;
		}
		return new GridPoint(minX - cellDiameter, minY - cellDiameter, minZ);
	}

	protected GridPoint getMax() {
		int maxX = 0;
		int maxY = 0;
		int maxZ = 0;
		for (int i = 0; i < numAcids; i++) {
			AcidInChain a = pp.getAminoAcid(i);
			if (a.xyz.x > maxX)
				maxX = a.xyz.x;
			if (a.xyz.y > maxY)
				maxY = a.xyz.y;
			if (a.xyz.z > maxZ)
				maxZ = a.xyz.z;
		}
		return new GridPoint(maxX, maxY, maxZ);
	}

	/**
	 * Compute a plane projection of a (possibly three dimensional) GridPoint to
	 * use to paint an AminoAcid on this GridCanvas.
	 * 
	 * @param p
	 *            the point to project.
	 * 
	 * @return the projection.
	 */
	protected abstract GridPoint project(GridPoint p);
	
	protected int getAcidRadius() {
		return cellRadius;
	}

	/**
	 * Returns constants used for center the name of the polypeptide
	 */
	protected int getStringIndentationConstant(String name, int r) {
		// the values returned are hardcoded with values that
		//   look best when the canvas is drawn. Their value
		//   was establish through trials, and best was picked.

		int length = name.trim().length();
		if (length == 1) // 1
			return 0;
		else if (length == 2) // -1
			return 0;
		else if (length == 3) // 0.x
			return (int) (1 / 8f * r);
		else if (length == 4) // -0.x
			return (int) (1 / 2f * r);
		else if (length == 5) // -0.xx
			return (int) (2 / 3f * r);
		else
			// length == 6. can't be longer. -0.xxx
			return (int) (3 / 4f * r);
	}

	public void paint() {
		
		if (grid == null)
			return;
		
		ColorCoder cc = null;
		Color darkGreen = new Color(48, 200, 48); //Replaces original green that was too light
		
		//g.fill(new Rectangle2D.Double(0, 0, requiredCanvasSize.width, requiredCanvasSize.height));
		
		//TO DO setBackground(Color.WHITE);
		//TO DO g.setColor(Color.WHITE);
		//TO DO g.fillRect(0, 0, requiredCanvasSize.width, requiredCanvasSize.height);
		
		if (!buildTargetShapeMode) {
			if (ssBondsOn) {
				g.setFillColor(Protex.SS_BONDS_ON_BACKGROUND);
				cc = new ShadingColorCoder(pp.getTable().getContrastScaler());
				//This sets the background. Use css instead?
				g.fill(new Rectangle2D.Double(0, 0, width, height));
				//g.fillRect(0, 0, requiredCanvasSize.width, requiredCanvasSize.height);
			} else {
				g.setFillColor(Protex.SS_BONDS_OFF_BACKGROUND);
				cc = new ShadingColorCoder(pp.getTable().getContrastScaler());
				//This sets the background. Use css instead?
				g.fill(new Rectangle2D.Double(0, 0, width, height));
				//g.fillRect(0, 0, requiredCanvasSize.width, requiredCanvasSize.height);
			}
		} else {
			//g.setFillColor(Color.WHITE);
			g.setFillColor(Protex.SS_BONDS_OFF_BACKGROUND);
			g.fill(new Rectangle2D.Double(0, 0, 300, 300));
		}
		
		/*if (!buildTargetShapeMode) {
			if (((OutputPalette)parentPanel).getssBondsOn()) {
				setBackground(Protex.SS_BONDS_ON_BACKGROUND);
				cc = new ShadingColorCoder(pp.getTable().getContrastScaler());
				g.setColor(Protex.SS_BONDS_ON_BACKGROUND);
				g.fillRect(0, 0, requiredCanvasSize.width, requiredCanvasSize.height);
			} else {
				setBackground(Protex.SS_BONDS_OFF_BACKGROUND);
				cc = new ShadingColorCoder(pp.getTable().getContrastScaler());
				g.setColor(Protex.SS_BONDS_OFF_BACKGROUND);
				g.fillRect(0, 0, requiredCanvasSize.width, requiredCanvasSize.height);
			}
		} else {
			setBackground(Color.WHITE);
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, requiredCanvasSize.width, requiredCanvasSize.height);
		}*/
		cc = new ShadingColorCoder(pp.getTable().getContrastScaler());
	
		
		GridPoint[] spots = new GridPoint[numAcids];
		AcidInChain[] acidsByZ = new AcidInChain[numAcids];
		for (int i = 0; i < numAcids; i++) {
			AcidInChain a = pp.getAminoAcid(i);
			spots[i] = project(a.xyz);
			acidsByZ[i] = a;
		}
		GridPoint min = getMin(spots);
		for (int i = 0; i < numAcids; i++) {
			spots[i] = spots[i].subtract(min);
		}
		Arrays.sort(acidsByZ, new SortByZ());
		int r = getAcidRadius();
		for (int i = 0; i < numAcids; i++) {
			AcidInChain a = acidsByZ[i];
			GridPoint here = project(a.xyz).subtract(min);
			
			// fills the circle
			if (a.getAbName().equals("X")) {
				//g.setColor(Color.BLUE);
				g.setFillColor(Color.BLUE);
			} else {
				//g.setColor(cc.getCellColor(a.getNormalizedHydrophobicIndex()));
				g.setFillColor(cc.getCellColor(a.getNormalizedHydrophobicIndex()));
			}
			//g.fillOval(here.x - r, here.y - r, 2 * r, 2 * r);
			g.setFillColor(cc.getCellColor(a.getNormalizedHydrophobicIndex()));
			g.fill(new Ellipse2D.Double(here.x - r, here.y - r, 2 * r, 2 * r));
		}
		
		// draw the backbone
		g.setColor(Color.red);
		for (int i = 0; i < numAcids; i++) {
			AcidInChain a = pp.getAminoAcid(i);
			int offset = getStringIndentationConstant(a.name, r);
			
			// default color for names is white
			//g.setColor(Color.white);
			Color aaLetterColor = Color.white;
			
			//if philic - then add stuff
			if (a.getName().equals("Arg") ||
					a.getName().equals("Lys") ||
					a.getName().equals("His")) {
				//g.setColor(Color.blue);
				//g.drawString("+", spots[i].x - 15, spots[i].y);
				g.drawFilledString("+", spots[i].x - 15, spots[i].y, "12pt sans-serif", Color.blue);
				//g.setColor(Color.black);
				aaLetterColor = Color.black;
			}
			
			if (a.getName().equals("Asp") ||
					a.getName().equals("Glu")) {
				//g.setColor(Color.red);
				//g.drawString("-", spots[i].x - 15, spots[i].y);
				g.drawFilledString("\u2212", spots[i].x - 15, spots[i].y,  "12pt sans-serif", Color.red);
				//g.setColor(Color.black);
				aaLetterColor = Color.black;
			}
			
			if (a.getName().equals("Asn") ||
					a.getName().equals("Gln") ||
					a.getName().equals("Ser") ||
					a.getName().equals("Thr") ||
					a.getName().equals("Tyr")) {
				g.setColor(Color.green);
				//g.drawString("*", spots[i].x - 15, spots[i].y);
				g.drawFilledString("\u25CF", spots[i].x - 15, spots[i].y, "12pt sans-serif", darkGreen);
				//g.setColor(Color.BLACK);
				aaLetterColor = Color.black;
			}
			
			if (a.getAbName().equals("X")) {
				if (strictMatchDisplayMode) {
					g.drawString(Integer.toString(a.getIndex() + 1), spots[i].x - offset, spots[i].y);
				}
			} else {
				g.drawFilledString(a.name, spots[i].x - offset, spots[i].y, "8pt sans-serif", aaLetterColor);
				//g.drawString(a.name, spots[i].x - offset, spots[i].y);
				// string is drawn to an left offset from center of disk.;
				g.drawFilledString(a.getAbName(), spots[i].x - 2, spots[i].y + 12, "8pt sans-serif", aaLetterColor);
				//g.drawString(a.getAbName(), spots[i].x - 2, spots[i].y + 12);
			}
			g.setColor(Color.magenta);
			
			
			//draw the backbone
			if (strictMatchDisplayMode || !buildTargetShapeMode){
				if (i < numAcids - 1) {
					g.drawLine(spots[i].x, spots[i].y, spots[i + 1].x,
							spots[i + 1].y);
				}
			}
		}
		
		//draw the ss bonds
		if (ssBondsOn) {
			if (grid.getssBondList().size() != 0) {
				g.setColor(Color.yellow);
				ArrayList ssBondList = grid.getssBondList();
				for (int i = 0; i < ssBondList.size(); i++) {
					int first = ((SsBond)ssBondList.get(i)).getFirst();
					int second = ((SsBond)ssBondList.get(i)).getSecond();
					g.drawLine(spots[first].x, spots[first].y, 
							spots[second].x, spots[second].y);
				}
			}
		}
	}
	
	public void calculateRequiredCanvasSize() {
		if (grid == null)
			return;
		
		GridPoint[] spots = new GridPoint[numAcids];
		AcidInChain[] acidsByZ = new AcidInChain[numAcids];
		for (int i = 0; i < numAcids; i++) {
			AcidInChain a = pp.getAminoAcid(i);
			spots[i] = project(a.xyz);
			acidsByZ[i] = a;
		}
		GridPoint min = getMin(spots);
		for (int i = 0; i < numAcids; i++) {
			spots[i] = spots[i].subtract(min);
		}
		Arrays.sort(acidsByZ, new SortByZ());
		GridPoint here = null;
		int maxX = 0;
		int maxY = 0;
		for (int i = 0; i < numAcids; i++) {
			AcidInChain a = acidsByZ[i];
			here = project(a.xyz).subtract(min);
			if (i == 0) {
				maxX = here.x;
				maxY = here.y;
			} else {
				if (here.x > maxX)
					maxX = here.x;
				if (here.y > maxY)
					maxY = here.y;
			}
		}
		requiredCanvasSize = new Dimension(maxX + 2 * cellDiameter, 
				maxY + 2 * cellDiameter);
	}

	private class SortByZ implements Comparator {
		public int compare(Object o1, Object o2) {
			AcidInChain a1 = (AcidInChain) o1;
			AcidInChain a2 = (AcidInChain) o2;
			return (a1.xyz.z - a2.xyz.z);
		}
	}

	private void drawDottedLine(Graphics g, int x1, int y1, int x2, int y2) {
	}

	// for hex grid - never called
	private void paintEmpties(Graphics g) {
		g.setColor(Color.black);
		for (int row = 0; row < size; row++) {
			int rowstart = (row <= numAcids) ? numAcids - row : 0;
			int rowend = (row <= numAcids) ? size : size - row + numAcids;
			for (int col = rowstart; col < rowend; col++) {
				GridPoint here = new GridPoint(row, col);
				// redo if needed g.drawOval(
				// 			   getCenterX(here) - cellRadius,
				// 			   getCenterY(here) - cellRadius,
				// 			   cellDiameter, cellDiameter);
				// commented out code draws the grid
				// 		hexagon.translate(getCornerX(row, col),
				// 				  getCornerY(row,col));
				// 		g.drawPolygon(hexagon);
				// 		hexagon.translate(-getCornerX(row, col),
				// 				  -getCornerY(row,col));
			}
		}
	}

}

