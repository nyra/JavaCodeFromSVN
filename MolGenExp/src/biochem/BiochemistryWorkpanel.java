package biochem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import molGenExp.WorkPanel;
import preferences.GlobalDefaults;
import preferences.MGEPreferences;

public class BiochemistryWorkpanel extends WorkPanel {

	final BiochemistryWorkbench protex;

	JPanel proteinPanel;
	JTextField proteinSequence;
	TripleLetterCodeDocument tlcDoc;
	FoldedProteinPanel foldedProteinPanel;

	JPanel buttonPanel;
	JPanel resultPanel;
	JButton foldButton;
	JButton loadSampleButton;
	JLabel colorLabel;
	JLabel colorChip;

	Polypeptide polypeptide;
	FoldingManager manager;

	StandardTable table;

	FoldedProteinWithImages foldedProteinWithImages;
	BufferedImage fullSizePic;

	Action foldProteinAction;

	public BiochemistryWorkpanel(String title, 
			final BiochemistryWorkbench protex) {
		super();
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createTitledBorder(title));
		this.setBackground(Color.lightGray);

		this.protex = protex;

		foldedProteinWithImages = null;

		fullSizePic = null;

		proteinPanel = new JPanel();
		proteinPanel.setLayout(new BorderLayout());
		tlcDoc = new TripleLetterCodeDocument();
		proteinSequence = new JTextField(50);
		proteinSequence.setBorder(BorderFactory.createTitledBorder("Amino Acid Sequence"));
		proteinSequence.setDocument(tlcDoc);
		proteinSequence.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(foldButton.isEnabled()) {
					foldProtein();
				}
			}			
		});

		resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.X_AXIS));
		resultPanel.setBorder(BorderFactory.createTitledBorder("Folded Protein"));
		foldedProteinPanel = new FoldedProteinPanel();
		JScrollPane scroller = new JScrollPane(foldedProteinPanel);
		resultPanel.add(scroller);
		resultPanel.add(Box.createHorizontalGlue());
		
		proteinPanel.add(proteinSequence, BorderLayout.NORTH);
		proteinPanel.add(resultPanel, BorderLayout.CENTER);

		loadSampleButton = new JButton("Load Sample Protein");

		foldButton = new JButton("FOLD");
		foldButton.setEnabled(false);
		tlcDoc.setLinkedFoldingWindow(this);

		colorLabel = new JLabel("Color:");
		colorChip = new JLabel("     ");
		colorChip.setOpaque(true);
		colorChip.setBackground(Color.WHITE);
		colorChip.setBorder(new LineBorder(Color.BLACK));

		buttonPanel = new JPanel();
		buttonPanel.add(foldButton);
		buttonPanel.add(colorLabel);
		buttonPanel.add(colorChip);
		buttonPanel.add(loadSampleButton);

		this.add(proteinPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);

		manager = new FoldingManager();

		table = new StandardTable();

		foldButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				foldProtein();
			}						
		});

		loadSampleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				proteinSequence.setText(GlobalDefaults.sampleProtein);
				aaSeqChanged();
			}
		});

	}

	private void foldProtein() {
		try {
			foldedProteinWithImages = manager.foldWithPix(proteinSequence.getText().trim());
			
			// if it folded into a corner, it will have a null for a pic
			//  detect this and warn user
			if (foldedProteinWithImages.getFullSizePic() == null) {
				JOptionPane.showMessageDialog(protex, 
						GlobalDefaults.paintedInACornerNotice,
						"Folding Error", JOptionPane.WARNING_MESSAGE);	
				return;
			}
			//display it
			Color proteinColor = foldedProteinWithImages.getColor();
			colorChip.setBackground(proteinColor);
			if (MGEPreferences.getInstance().isShowColorNameText()) {
				colorChip.setToolTipText(
						GlobalDefaults.colorModel.getColorName(proteinColor));
			} else {
				colorChip.setToolTipText(null);
			}

			protex.addToHistoryList(foldedProteinWithImages);

			foldButton.setEnabled(false);
			foldedProteinPanel.setBackground(Color.lightGray);

		} catch (FoldingException e) {
			JOptionPane.showMessageDialog(protex, 
					GlobalDefaults.paintedInACornerNotice,
					"Folding Error", JOptionPane.WARNING_MESSAGE);
		}	

	}	


	public String getAaSeq() {
		return proteinSequence.getText();
	}

	public Color getColor() {
		
		if (foldedProteinWithImages == null) return null;
		
		if (foldedProteinWithImages.getColor() == null) {
			return Color.WHITE;
		}
		return foldedProteinWithImages.getColor();
	}

	public FoldedProteinWithImages getFoldedProteinWithImages() {
		return foldedProteinWithImages;
	}

	public BufferedImage getFullSizePic() {
		return fullSizePic;
	}

	//callback from the Document in the text field when
	// the aa seq is changed
	public void aaSeqChanged() {
		foldButton.setEnabled(true);
		foldedProteinPanel.setBackground(Color.PINK);		
	}

	public void setFoldedProteinWithImages(FoldedProteinWithImages fp) {

		foldedProteinWithImages = fp;

		//process amino acid sequence
		PolypeptideFactory factory = PolypeptideFactory.getInstance();
		AminoAcid[] acids = null;
		try {
			acids = factory.parseInputStringToAmAcArray(fp.getAaSeq());
		} catch (FoldingException e) {
			e.printStackTrace();
		}
		((TripleLetterCodeDocument)
				proteinSequence.getDocument()).removeAll();
		StringBuffer abAASeq = new StringBuffer();
		for (int i = 0; i < acids.length; i++) {
			abAASeq.append(acids[i].getAbName());
		}
		proteinSequence.setText(abAASeq.toString());

		//update the color chip on the folding window
		colorChip.setBackground(fp.getColor());
		if (MGEPreferences.getInstance().isShowColorNameText()) {
			colorChip.setToolTipText(
					GlobalDefaults.colorModel.getColorName(fp.getColor()));
		} else {
			colorChip.setToolTipText(null);
		}

		//update the combined color chip
		protex.updateCombinedColor();

		//update the picture as well
		foldedProteinPanel.updateImage(foldedProteinWithImages.getFullSizePic());

		foldedProteinPanel.setBackground(Color.LIGHT_GRAY);
		foldButton.setEnabled(false);

	}

	public BufferedImage takeSnapshot() {
		ImageIcon fullSizePic = foldedProteinWithImages.getFullSizePic();
		int width = fullSizePic.getIconWidth();
		int height = fullSizePic.getIconHeight();

		BufferedImage imageBuffer = new BufferedImage(
				width,
				height + 60,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = imageBuffer.getGraphics();

		//the protein
		g.drawImage(fullSizePic.getImage(), 0, 0, null);

		//fill in extra space for aa seq and color
		g.setColor(Color.WHITE);
		g.fillRect(0, height, width, 60);

		//the amino acid sequence
		// be sure it'll fit
		String aaSeq = foldedProteinWithImages.getAaSeq();
		Font defaultFont = g.getFont();
		FontMetrics defaultFm = g.getFontMetrics(defaultFont);
		int defaultWidth = defaultFm.stringWidth(aaSeq);
		if (defaultWidth > width) {
			Font smallFont = defaultFont.deriveFont(defaultFont.getSize() * 0.75f);
			g.setFont(smallFont);
			FontMetrics smallFm = g.getFontMetrics(smallFont);
			int smallWidth = smallFm.stringWidth(aaSeq);
			if (smallWidth > width) {
				Font tinyFont = defaultFont.deriveFont(defaultFont.getSize() * 0.5f);
				g.setFont(tinyFont);
			}	
		}
		g.setColor(Color.BLACK);
		g.drawString(aaSeq, 0, height + 15);

		//the color chip
		g.setFont(defaultFont);
		g.setColor(Color.BLACK);
		g.drawString("Color:", 5, height + 30);
		g.drawString(
				GlobalDefaults.colorModel.getColorName(foldedProteinWithImages.getColor()),
				5, height + 45);
		g.setColor(foldedProteinWithImages.getColor());
		g.fillRect(60, height + 20, 30, 30);
		g.setColor(Color.BLACK);
		g.drawRect(59, height + 19, 31, 31);

		return imageBuffer;
	}
}
