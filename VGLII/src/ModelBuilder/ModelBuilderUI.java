package ModelBuilder;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import GeneticModels.GeneModel;
import GeneticModels.GeneticModel;
import GeneticModels.PhenotypeProcessor;
import GeneticModels.ProblemTypeSpecification;
import VGL.Messages;

public class ModelBuilderUI extends JDialog {

	private JFrame parentFrame;
	private WorkingModel workingModel;
	private GeneticModel geneticModel;
	private LinkagePanel linkagePanel;

	// from saved work file
	public ModelBuilderUI (
			JFrame parentFrame, 
			WorkingModel workingModel, 
			GeneticModel geneticModel) {
		super(parentFrame);
		this.parentFrame = parentFrame;
		this.workingModel = workingModel;
		this.geneticModel = geneticModel;
		setupUI();
	}

	// for new problem
	public ModelBuilderUI(
			JFrame parentFrame,
			GeneticModel geneticModel) {
		this(parentFrame, new WorkingModel(geneticModel), geneticModel);
	}

	private void setupUI() {
		this.setTitle(Messages.getInstance().getString("VGLII.ModelBuilder"));

		JPanel outerPanel = new JPanel();
		outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.X_AXIS));
		outerPanel.add(Box.createRigidArea(new Dimension(1,300)));

		JPanel masterPanel = new JPanel();
		masterPanel.setLayout(new BoxLayout(masterPanel,BoxLayout.Y_AXIS));
		masterPanel.add(Box.createRigidArea(new Dimension(400,1)));
		JTabbedPane tabs = new JTabbedPane();
		GeneModel[] geneModels = new GeneModel[geneticModel.getNumberOfGeneModels()];
		ModelPane[] modelPanes = new ModelPane[geneticModel.getNumberOfCharacters()];

		ProblemTypeSpecification specs = geneticModel.getProblemTypeSpecification();
		if (specs.getPhenotypeInteraction() == 0.0) {
			// simple if no complementation or epistasis
			for (int i = 0; i < geneticModel.getNumberOfGeneModels(); i++) {
				geneModels[i] = geneticModel.getGeneModelByIndex(i);
				modelPanes[i] = new ModelPane(i, geneModels[i].getTraits(), specs, this);
				tabs.addTab(geneModels[i].getCharacter(), modelPanes[i]);
				modelPanes[i].setupActionListeners();
			}
		} else {
			// more of a pain if epistasis or complementation
			//	first pane is the possibly interacting pheno
			geneModels[0] = geneticModel.getGeneModelByIndex(0);
			if (geneticModel.getPhenoTypeProcessor().getInteractionType() == PhenotypeProcessor.NO_INTERACTION) {
				// if no interaction, just get the first gene model
				modelPanes[0] = new ModelPane(0, 
						geneModels[0].getTraits(), 
						specs, 
						this);
				tabs.addTab(geneModels[0].getCharacter(), modelPanes[0]);
			} else {
				// if there is an interaction, need to get the phenotypes from the PhenoProcessor
				modelPanes[0] = new ModelPane(0, 
						geneticModel.getPhenoTypeProcessor().getTraits(), 
						specs, 
						this);
				tabs.addTab(geneticModel.getPhenoTypeProcessor().getCharacter()	, modelPanes[0]);
			}
			modelPanes[0].setupActionListeners();

			// if a last character, add it (it's index = 2 since 1st 2 are interacting)
			if (geneticModel.getNumberOfCharacters() == 2) {
				geneModels[1] = geneticModel.getGeneModelByIndex(2);
				modelPanes[1] = new ModelPane(1, geneModels[1].getTraits(), specs, this);
			}

		}

		// set up for linkage if needed
		if (geneticModel.getNumberOfGeneModels() > 1) {
			if ((specs.getGene2_chSameChrAsGene1() != 0.0) &&
					(specs.getGene2_minRfToGene1() < 0.5)) {
				setupLinkagePanel(geneModels, tabs);
			}
			if ((geneticModel.getNumberOfGeneModels() > 2) &&
					(specs.getGene3_chSameChrAsGene1() != 0.0) &&
					(specs.getGene3_chSameChrAsGene1() < 0.5)) {
				setupLinkagePanel(geneModels, tabs);
			}
		}

		masterPanel.add(tabs);
		outerPanel.add(masterPanel);
		this.add(outerPanel);
		this.pack();

	}

	private void setupLinkagePanel(GeneModel[] geneModels, JTabbedPane tabs) {
		ArrayList<String> characters = new ArrayList<String>();
		characters.add(geneModels[0].getCharacter());
		characters.add(geneModels[1].getCharacter());
		if (geneticModel.getNumberOfGeneModels() == 3) {
			characters.add(geneModels[2].getCharacter());
		}
		String[] chars = new String[characters.size()];
		for (int i = 0; i < characters.size(); i++) {
			chars[i] = characters.get(i);
		}
		linkagePanel = new LinkagePanel(chars);
		tabs.add(Messages.getInstance().getString("VGLII.Linkage"), linkagePanel);
	}

}
