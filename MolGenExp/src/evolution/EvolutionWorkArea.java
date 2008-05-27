package evolution;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import molGenExp.MolGenExp;
import molGenExp.Organism;
import preferences.MGEPreferences;
import utilities.GlobalDefaults;

public class EvolutionWorkArea extends JPanel {

	private MolGenExp mge;
	private MGEPreferences preferences;

	private JPanel leftPanel;
	private JPanel controlPanel;
	private JButton loadButton;
	private JButton startButton;
	private JButton stopButton;
	private JPanel fitnessPanel;
	private JPanel rightPanel;
	private World world;
	private JLabel generationLabel;
	private int generation = 0;
	private boolean running = false;

	Color backgroundColor = new Color(128,128,128);

	String[] colorList = {"White", "Blue", "Yellow", "Green",
			"Red", "Purple", "Orange", "Black"};
	ColorFitnessSpinner[] spinners = new ColorFitnessSpinner[colorList.length];
	ColorPopulationLabel[] populationLabels = new ColorPopulationLabel[colorList.length];

	public EvolutionWorkArea(MolGenExp mge) {
		this.mge = mge;
		preferences = MGEPreferences.getInstance();
		setupUI();
	}

	private void setupUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(Box.createRigidArea(new Dimension(200,1)));

		fitnessPanel = new JPanel();
		fitnessPanel.setBorder(BorderFactory.createTitledBorder("Color Fitness and Population Counts"));
		fitnessPanel.setBackground(backgroundColor);

		JPanel settingsAndCountPanel = new JPanel();
		settingsAndCountPanel.setOpaque(true);
		settingsAndCountPanel.setBackground(Color.BLACK);
		
		settingsAndCountPanel.setLayout(new GridLayout(9, 3, 2, 2));
		
		JLabel cLabel = new JLabel("Color");
		cLabel.setOpaque(true);
		cLabel.setBackground(backgroundColor);
		settingsAndCountPanel.add(cLabel);
		
		JLabel rfLabel = new JLabel("Relative Fitness");
		rfLabel.setOpaque(true);
		rfLabel.setBackground(backgroundColor);
		settingsAndCountPanel.add(rfLabel);
		
		JLabel pcLabel = new JLabel("Population Count");
		pcLabel.setOpaque(true);
		pcLabel.setBackground(backgroundColor);
		settingsAndCountPanel.add(pcLabel);
		
		JLabel[] colorLabels = new JLabel[colorList.length];
		for (int i = 0; i < colorList.length; i++) {
			spinners[i] = new ColorFitnessSpinner(colorList[i]);
			colorLabels[i] = new JLabel(spinners[i].getColorString());
			colorLabels[i].setBackground(backgroundColor);
			colorLabels[i].setForeground(spinners[i].getColor());
			colorLabels[i].setOpaque(true);
			settingsAndCountPanel.add(colorLabels[i]);
			colorLabels[i].setLabelFor(spinners[i]);

			spinners[i].setOpaque(true);
			spinners[i].setBackground(backgroundColor);
			settingsAndCountPanel.add(spinners[i]);

			populationLabels[i] = new ColorPopulationLabel(colorList[i]);
			populationLabels[i].setOpaque(true);
			populationLabels[i].setBackground(backgroundColor);
			
			settingsAndCountPanel.add(populationLabels[i]);
		}

		fitnessPanel.add(settingsAndCountPanel);

		leftPanel.add(fitnessPanel);

		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
		loadButton = new JButton("Load");
		controlPanel.add(loadButton);
		startButton = new JButton("Start");
		startButton.setEnabled(false);
		controlPanel.add(startButton);
		stopButton = new JButton("Stop");
		stopButton.setEnabled(false);
		controlPanel.add(stopButton);
		leftPanel.add(controlPanel);

		this.add(leftPanel);

		rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBorder(BorderFactory.createTitledBorder("World"));

		rightPanel.add(Box.createRigidArea(new Dimension(500,1)));
		world = new World();
		rightPanel.add(world);
		generationLabel = new JLabel("Generation 0");
		rightPanel.add(generationLabel);

		this.add(rightPanel);

		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mge.loadSelectedIntoWorld();
				mge.getGreenhouse().clearSelection();
			}
		});

		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				loadButton.setEnabled(false);
				running = true;
				mge.startEvolving();
			}
		});

		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopButton.setEnabled(false);
				startButton.setEnabled(true);
				loadButton.setEnabled(true);
				running = false;
				mge.stopEvolving();
			}
		});

	}





	public boolean running() {
		return running;
	}

	public void setReadyToRun() {
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		running = false;
	}

	public void updateGenerationLabel() {
		generation++;
		generationLabel.setText("Generation " + generation);
	}

	public int getGeneration() {
		return generation;
	}

	public World getWorld() {
		return world;
	}

	public int[] getFitnessValues() {
		int[] values = new int[spinners.length];
		for (int i = 0; i < spinners.length; i++) {
			values[i] = ((Integer)spinners[i].getValue()).intValue();
		}
		return values;
	}

	public void clearSelection() {
		world.clearSelectedOrganism();
	}

	public void saveWorldToFile() {
		if (world.getThinOrganism(0, 0) == null) {
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		JFileChooser outfileChooser = new JFileChooser();
		outfileChooser.setDialogTitle("Enter a file name...");
		int resultVal = outfileChooser.showSaveDialog(this);
		if (resultVal == JFileChooser.APPROVE_OPTION) {
			File outFile = outfileChooser.getSelectedFile();
			Writer output = null;
			try {
				output = new BufferedWriter(new FileWriter(outFile) );
				output.write("Aipotu world file\n");
				output.write("#" + preferences.getWorldSize() + "\n");
				output.write("X,Y,Gene#,DNA,Protein,R,G,B\n");
				for (int x = 0; x < preferences.getWorldSize(); x++) {
					for (int y = 0; y < preferences.getWorldSize(); y++) {
						Organism o = new Organism(world.getThinOrganism(x, y));
						output.write(x + "," + y + ",0,");
						output.write(o.getGene1().getExpressedGene().getDNA() + ",");
						output.write(
								o.getGene1()
								.getFoldedPolypeptide()
								.getFullSizeGrid()
								.getPP()
								.getSingleLetterAASequence() + ",");
						output.write(
								o.getGene1().getFoldedPolypeptide().getColor().getRed() 
								+ ",");
						output.write(
								o.getGene1().getFoldedPolypeptide().getColor().getGreen() 
								+ ",");
						output.write(
								o.getGene1().getFoldedPolypeptide().getColor().getBlue() 
								+ "\n");

						output.write(x + "," + y + ",1,");
						output.write(o.getGene2().getExpressedGene().getDNA() + ",");
						output.write(
								o.getGene2()
								.getFoldedPolypeptide()
								.getFullSizeGrid()
								.getPP()
								.getSingleLetterAASequence() + ",");
						output.write(
								o.getGene2().getFoldedPolypeptide().getColor().getRed() 
								+ ",");
						output.write(
								o.getGene2().getFoldedPolypeptide().getColor().getGreen() 
								+ ",");
						output.write(
								o.getGene2().getFoldedPolypeptide().getColor().getBlue() 
								+ "\n");
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if (output != null)
					try {
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}

	public void loadWorldFromFile() {

		ThinOrganism[][] newWorld = null;

		String DNA1 = "";
		String DNA2 = "";
		Color color1 = null;
		Color color2 = null;

		JFileChooser infileChooser = new JFileChooser();
		infileChooser.setDialogTitle(
		"Choose a file of organisms to load into the world");
		int resultVal = infileChooser.showOpenDialog(this);
		if (resultVal == JFileChooser.APPROVE_OPTION) {
			File infile = infileChooser.getSelectedFile();
			BufferedReader input = null;
			try {
				input = new BufferedReader(new FileReader(infile));
				String line = null;
				boolean haveReadFirstLine = false;
				boolean haveSetWorldSize = false;
				while ((line = input.readLine()) != null) {
					//check first line to be sure it's a world file
					if (!line.equals("Aipotu world file") && !haveReadFirstLine) {
						JOptionPane.showMessageDialog(
								null, 
								"<html>Aipotu cannot read the file,<br>"
								+ "it is probably the wrong format.<br>"
								+ "You should check it or try another.</html>",
								"Unreadable file format", 
								JOptionPane.WARNING_MESSAGE);
						break;
					} else {
						haveReadFirstLine = true;
					}

					// read second line to get world size
					if (line.startsWith("#")) {
						line = line.replaceAll("#", "");
						int worldSize = Integer.parseInt(line);
						newWorld = new ThinOrganism[worldSize][worldSize];
						haveSetWorldSize = true;
					}

					//now, the rest - parse each line
					if (haveSetWorldSize) {
						//ignore the header line
						if (!line.startsWith("X")) {
							String[] lineParts = line.split(",");
							if (lineParts.length == 8) {
								int x = Integer.parseInt(lineParts[0]);
								int y = Integer.parseInt(lineParts[1]);
								int geneNum = Integer.parseInt(lineParts[2]);
								if (geneNum == 0) {
									DNA1 = lineParts[3];
									color1 = new Color(
											Integer.parseInt(lineParts[5]),
											Integer.parseInt(lineParts[6]),
											Integer.parseInt(lineParts[7]));
								} else {
									DNA2 = lineParts[3];
									color2 = new Color(
											Integer.parseInt(lineParts[5]),
											Integer.parseInt(lineParts[6]),
											Integer.parseInt(lineParts[7]));
									newWorld[x][y] = new ThinOrganism(
											DNA1, 
											DNA2, 
											GlobalDefaults.colorModel.mixTwoColors(
													color1, color2));
								}
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					if (input!= null) {
						input.close();
					}
				}
				catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		world.setOrganisms(newWorld);
		setReadyToRun();
		world.repaint();
	}
}
