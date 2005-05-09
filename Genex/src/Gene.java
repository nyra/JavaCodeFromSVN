//the class that does the gene expression logic// transcription, processing, and translation// also generates the HTML for displayimport java.util.*;public class Gene {    // specifications for processing    String _promoterSequence;    String _terminatorSequence;    String _intronStartSequence;    String _intronEndSequence;    String _polyATail;        int _promoterStart;    int _terminatorStart;    int _numSpacesBeforeRNA_Start;       int _numberOfExons;        int _numCharsInDisplayBeforeFirstDNA_Base;        //note that all sequences start with 0 as the first member        //the starting DNA sequence for expression; this is not changed    // for displaying DNA, use DNASequence.length()    String _DNASequence;    String _premRNASequence;    String _mRNASequence;    String _proteinSequence;          String _proteinString;   //the protein sequence with leading spaces                             //for alignment and html markup        //the input DNA string is converted into this array list of individual nucleotides    // each nucleotide keeps info about what strands it is in, etc.    //  NOTE: this can be longer than DNASequence.length() IF the polyA tail extends    //    after the end of the DNA sequence.  So, for processing & display of mRNA & protein    //    DNANucleotides.size() should be used.    public ArrayList _DNANucleotides;        //variable used in processing exons    ArrayList _exons;    //a list of the exons in the gene    public Gene(String inputDNASequence,                 String promoterSequence,                 String terminatorSequence,                 String intronStartSequence,                 String intronEndSequence,                 String polyATail) {    // constructor that sets up DNA strand & gene specs        _DNANucleotides = new ArrayList();        _DNASequence = inputDNASequence;        _promoterSequence = promoterSequence;        _terminatorSequence = terminatorSequence;        _intronStartSequence = intronStartSequence;        _intronEndSequence = intronEndSequence;        _polyATail = polyATail;                _promoterStart = -1;        _terminatorStart = -1;        _numSpacesBeforeRNA_Start = 0;        _numberOfExons = 0;                _numCharsInDisplayBeforeFirstDNA_Base = 0;                _premRNASequence = new String("");        _mRNASequence = new String("");        _proteinSequence = new String("");                _proteinString = new String("");                //read in the gene        for (int i = 0; i < inputDNASequence.length(); i++) {            _DNANucleotides.add(new Nucleotide(_DNASequence.charAt(i), i ));        }    }        public void transcribe() {    	int promoterSite = _DNASequence.indexOf(_promoterSequence);	    int terminatorSite = _DNASequence.indexOf(_terminatorSequence, promoterSite);        StringBuffer pre_mRNAbuffer = new StringBuffer();	    if ((promoterSite != -1) && (terminatorSite != -1)) {  // if we have a gene	        int mRNANucleotideNumber = 0;	        //set the values for this gene	        _promoterStart = promoterSite;	        _terminatorStart = terminatorSite;	        _numSpacesBeforeRNA_Start = _promoterStart + _promoterSequence.length();	        	        //mark nucleotides in the pre mRNA from promoter to terminator	        for (int i = _numSpacesBeforeRNA_Start; i < terminatorSite; i++) {	            Nucleotide nucleotide = (Nucleotide)_DNANucleotides.get(i);	            nucleotide.setInPremRNA();	            nucleotide.setPremRNABaseNum(mRNANucleotideNumber);	            mRNANucleotideNumber++;	        }	            	    //here, pre_mRNA buffer includes leading & trailing whitespace for	        // correct alignment with the DNA	        // count up to DNASequence.length() beacuse this is based on the DNA without the	        //    poly A tail	            for (int i = 0; i < _DNASequence.length(); i++) {	               Nucleotide nucleotide = (Nucleotide)_DNANucleotides.get(i);	               pre_mRNAbuffer.append(nucleotide.getRNABase());	            }	        //strip off leading & trailing whitespace	        _premRNASequence = pre_mRNAbuffer.toString().trim();	    } else {	        _premRNASequence = "";	    }    }        public void process() {        //only process if there's an mRNA        if (!_premRNASequence.equals("")) {            int currentNucleotideNum = 0;  //start with first nuc in mRNA            StringBuffer mRNAbuffer = new StringBuffer();            int currentmRNABase = 0;                       while (currentNucleotideNum != -1) {                Exon currentExon = findNextExon(currentNucleotideNum);                _numberOfExons++;                currentNucleotideNum = currentExon.getStartOfNext();                            //mark the nucleotides in the exons as being in mRNA                //and build up the mature mRNA sequence                for (int i = currentExon.getStart();                         i < currentExon.getEnd();                         i++) {                    Nucleotide nucleotide = (Nucleotide)_DNANucleotides.get(i + _numSpacesBeforeRNA_Start);                    nucleotide.setInmRNA();                    nucleotide.setmRNABaseNum(currentmRNABase);                    currentmRNABase++;                    mRNAbuffer.append(nucleotide.getRNABase());                }            }        	        //add on the poly A tail here	        for (int i = _terminatorStart; i < (_terminatorStart + _polyATail.length()); i++) {	            //see if it's after the end of the gene	            if (i >= _DNANucleotides.size()) {	                //if it is after the end, add another one	                Nucleotide nucleotide = new Nucleotide('A', i);                    //it's in the mRNA but not the pre mRNA	                nucleotide.setInmRNA();	                //if it is after the end of the DNA sequence, add a DNA Nucleotide                    _DNANucleotides.add(nucleotide);	            } else {	                //mark it as in the mRNA but not the pre mRNA	                Nucleotide nucleotide = (Nucleotide)_DNANucleotides.get(i);	                nucleotide.setInmRNA();	            }	        }           	        // add the poly A tail            _mRNASequence = mRNAbuffer.toString() + _polyATail;        } else {            // if no pre-mRNA, there's no mRNA            _mRNASequence = "";        }    }        public Exon findNextExon(int currentPosition) {        //see if there's a start intron sequence up ahead        int startSite = _premRNASequence.indexOf(_intronStartSequence, currentPosition);                //if not, we're done        if (startSite == -1) return new Exon(currentPosition, _premRNASequence.length(), -1);                //since there's a start, look for an end        int endSite = _premRNASequence.indexOf(_intronEndSequence, startSite);                //if not, we're done also        if (endSite == -1) return new Exon(currentPosition, _premRNASequence.length(), -1);                //so we have an intron        // mark the exon & move on        return new Exon(currentPosition, startSite, endSite + _intronEndSequence.length());    }        public void translate() {        //if no mRNA, no protein        if (!_mRNASequence.equals("")) {            int aaNum = 0;              //the number of the amino acid in the sequence                                        // the stop codon has no number            int baseInCodon = 0;        //0 = first base; 1 = 2nd; 2 = third base in codon            int currentmRNABase = 0;    //number of base in mRNA strand            String currentAA = new String("");     //the amino acid            StringBuffer proteinBuffer = new StringBuffer();                   //look for start codon            for (int i = 0; i < _DNANucleotides.size(); i++) {                Nucleotide base = (Nucleotide)_DNANucleotides.get(i);                //find the next 3 mRNA bases (even looking across splice junctions)                if (base.getInmRNA()) {                    Nucleotide base1 = getNextmRNANucleotide(i);                    Nucleotide base2 = getNextmRNANucleotide(base1.getDNABaseNum() + 1);                    Nucleotide base3 = getNextmRNANucleotide(base2.getDNABaseNum() + 1);                    String codon = base1.getRNABase() + base2.getRNABase() + base3.getRNABase();                    currentmRNABase = base3.getDNABaseNum();                    if (codon.equals("AUG")) {                        //found it - now start translation                        setCodonValues(0, base1, base2, base3);                        proteinBuffer.append(Codon.getAA(codon));                        break;                    }                }             }            //now we're in a protein            int codonNum = 1;                //next codon            int j = (currentmRNABase + 1);   //next base            while (j <= _DNANucleotides.size()) {                //get next codon                Nucleotide first = getNextmRNANucleotide(j);                Nucleotide second = getNextmRNANucleotide(first.getDNABaseNum() + 1);                Nucleotide third = getNextmRNANucleotide(second.getDNABaseNum() + 1);                String codon = first.getRNABase() + second.getRNABase() + third.getRNABase();                                //if we are going to read after end of mRNA, stop before doing so                if ((j + 2) >= _DNANucleotides.size()) break;                                //point to next codon                j = third.getDNABaseNum() + 1;                                //get the amino acid for this codon                proteinBuffer.append(Codon.getAA(codon));                setCodonValues(codonNum, first, second, third);                                //see if stop codon aaNum = -2 if a stop codon (-1 means not in protein)                if (Codon.getAA(codon).equals("")) {                    setCodonValues(-2, first, second, third);                    break;                }                codonNum++;            }                              _proteinSequence = proteinBuffer.toString();       } else {           //if no mRNA, no protein           _proteinSequence = "";       }    }        public Nucleotide getNextmRNANucleotide(int DNABaseNum) {            //be sure it's not out of range        if (DNABaseNum >= _DNANucleotides.size()) {            DNABaseNum = _DNANucleotides.size() - 1;        }                //see if the current DNA base corresponds to a base in the mRNA        Nucleotide nucleotide = (Nucleotide)_DNANucleotides.get(DNABaseNum);                // if not, then loop until you find one        while (!nucleotide.getInmRNA() && (DNABaseNum < _DNANucleotides.size())) {            nucleotide = (Nucleotide)_DNANucleotides.get(DNABaseNum);            DNABaseNum++;        }        return nucleotide;    }        //set the values for the Nucleotides in the codon    public void setCodonValues(int AANum, Nucleotide first, Nucleotide second, Nucleotide third) {        first.setInProtein();        first.setAANum(AANum);        first.setCodonPosition(0);        second.setInProtein();        second.setAANum(AANum);        second.setCodonPosition(1);        third.setInProtein();        third.setAANum(AANum);        third.setCodonPosition(2);    }        //for debugging only - list the specs on each nucleotide    public void showItAll() {        System.out.println("#\tDb\tRb\tpm\tm\tprot\taa\tcp\tsel");        for (int i = 0; i < _DNANucleotides.size(); i++) {            Nucleotide nuc = (Nucleotide)_DNANucleotides.get(i);            System.out.println(i + "\t" + nuc.getBase()                                 + "\t" + nuc.getRNABase()                                 + "\t" + convertBoolean(nuc.getInPremRNA())                                      + " " + nuc.getPremRNABaseNum()                                 + "\t" + convertBoolean(nuc.getInmRNA())                                     + " " + nuc.getmRNABaseNum()                                 + "\t" + convertBoolean(nuc.getInProtein())                                 + "\t" + nuc.getAANum()                                  + "\t" + nuc.getCodonPosition()                                 + "\t" + convertBoolean(nuc.getSelected()));        }    }        //used for showItAll    public String convertBoolean(boolean b) {        if (b) return "Y";        return "N";    }        public HTMLContainer generateHTML(int selectedDNABase) {        //mark the selected base (-1 means no base selected)        if (selectedDNABase != -1) {            Nucleotide nuc = (Nucleotide)_DNANucleotides.get(selectedDNABase);            nuc.setSelected();        }                                        //the bufffer for the color html	    StringBuffer colorHTMLbuffer = new StringBuffer();	    	    //the buffer for the black & white HTML	    StringBuffer bwHTMLbuffer = new StringBuffer();	    	    HTMLContainer headerHTML = generateHTMLHeader();	    colorHTMLbuffer.append(headerHTML.getColorHTML());	    bwHTMLbuffer.append(headerHTML.getBwHTML());	    	    HTMLContainer DNA_HTML = generateDNA_HTML(selectedDNABase);	    colorHTMLbuffer.append(DNA_HTML.getColorHTML());	    bwHTMLbuffer.append(DNA_HTML.getBwHTML());	    _numCharsInDisplayBeforeFirstDNA_Base 		              = calcCharsBeforeFirstDNA_Base(DNA_HTML.getColorHTML());	    	    HTMLContainer premRNA_HTML = generatepremRNA_HTML(selectedDNABase);	    colorHTMLbuffer.append(premRNA_HTML.getColorHTML());	    bwHTMLbuffer.append(premRNA_HTML.getBwHTML());	    HTMLContainer mRNA_HTML = generatemRNA_HTML(selectedDNABase);	    colorHTMLbuffer.append(mRNA_HTML.getColorHTML());	    bwHTMLbuffer.append(mRNA_HTML.getBwHTML());        HTMLContainer protein_HTML = generateProteinHTML(selectedDNABase);	    colorHTMLbuffer.append(protein_HTML.getColorHTML());	    bwHTMLbuffer.append(protein_HTML.getBwHTML());	    return new HTMLContainer (colorHTMLbuffer.toString(), bwHTMLbuffer.toString());	}        public HTMLContainer generateHTMLHeader() {        //the html header that sets up the styles for display        StringBuffer headerBuffer = new StringBuffer();	    headerBuffer.append("<html><head>");        headerBuffer.append("<style type=\"text/css\">");        headerBuffer.append("EM.selected {font-style: normal; background: blue; color: red}");        headerBuffer.append("EM.promoter {font-style: normal; background: #90FF90; color: black}");        headerBuffer.append("EM.terminator {font-style: normal; background: #FF9090; color: black}");        headerBuffer.append("EM.exon {font-style: normal; background: #FF90FF; color: black}");        headerBuffer.append("EM.next {font-style: normal; background: #90FFFF; color: black}");        headerBuffer.append("EM.another {font-style: normal; background: #FFFF50; color: black}");        headerBuffer.append("</style></head><body>");        return new HTMLContainer(headerBuffer.toString(), "");    }        public HTMLContainer generateDNA_HTML(int selectedBase){    	StringBuffer BW_Buffer = new StringBuffer();    	StringBuffer ColorBuffer = new StringBuffer();    	boolean highlighted = false;    	    	String fivePrimeSpaces = "    ";    	    	ColorBuffer.append("<html><h3>DNA: <EM class=promoter>Promoter</EM>");    	ColorBuffer.append("<EM class=terminator>Terminator</EM></h3><pre>\n");        BW_Buffer.append("<h3>DNA: promoter, terminator</h3><pre>\n");	    // then, set up the DNA numbering bars	    //insert some blank spaces for the "5'-"	    ColorBuffer.append(fivePrimeSpaces);	    BW_Buffer.append(fivePrimeSpaces);	    	    //first the numbers	    for (int i = 0; i < _DNASequence.length(); i = i + 10) {	    	String numberLabel = new String("");	        if (i == 0) {	            numberLabel = "";	        } else  if (i < 100 ){	            numberLabel = "        " + i;   	        } else {	            numberLabel = "       " + i;	        }		    ColorBuffer.append(numberLabel);		    BW_Buffer.append(numberLabel);	    }	    ColorBuffer.append("\n");	    BW_Buffer.append("\n");	    	    //then the tick marks	    final String tickMarkString = "    .    |";	    ColorBuffer.append(fivePrimeSpaces);	    BW_Buffer.append(fivePrimeSpaces);	   	    for (int i = 0; i < _DNASequence.length(); i = i + 10) {	        if (i > 0) {	            ColorBuffer.append(tickMarkString);	            BW_Buffer.append(tickMarkString);	        }	    }	    ColorBuffer.append("\n");	    BW_Buffer.append("\n");	    	    	    	    //do the three strands in color and B/W in parallel	    StringBuffer ColorTopStrandBuffer = new StringBuffer();	    StringBuffer ColorBasePairBuffer = new StringBuffer();	    StringBuffer ColorBottomStrandBuffer = new StringBuffer();	    StringBuffer BW_TopStrandBuffer = new StringBuffer();	    StringBuffer BW_BasePairBuffer = new StringBuffer();	    StringBuffer BW_BottomStrandBuffer = new StringBuffer();		            for (int i = 0; i < _DNASequence.length(); i++) {            Nucleotide n = (Nucleotide)_DNANucleotides.get(i);            if (i == _promoterStart) {                highlighted = true;            }                        if (i == _promoterStart + _promoterSequence.length()) {                highlighted = false;            }                        if (i == _terminatorStart) {                highlighted = true;            }                        if (i == _terminatorStart + _terminatorSequence.length()) {                highlighted = false;            }                        ColorTopStrandBuffer.append(            		markUpNucleotideSymbol(            				i, n.getBase(), n.getSelected(), highlighted)							.getColorHTML());            ColorBasePairBuffer.append(            		markUpNucleotideSymbol(            				i, "|", n.getSelected(), highlighted)							.getColorHTML());            ColorBottomStrandBuffer.append(            		markUpNucleotideSymbol(            				i, n.getComplementBase(), n.getSelected(), highlighted)							.getColorHTML());                        BW_TopStrandBuffer.append(            		markUpNucleotideSymbol(            				i, n.getBase(), n.getSelected(), highlighted)							.getBwHTML());            BW_BasePairBuffer.append(            		markUpNucleotideSymbol(            				i, "|", n.getSelected(), highlighted)							.getBwHTML());            BW_BottomStrandBuffer.append(            		markUpNucleotideSymbol(            				i, n.getComplementBase(), n.getSelected(), highlighted)							.getBwHTML());         }                ColorBuffer.append("5\'-"); 	    ColorBuffer.append(ColorTopStrandBuffer.toString() + "</EM>-3\'\n" 	    		          + "   " + ColorBasePairBuffer.toString() + "</EM>\n"						  + "3\'-" + ColorBottomStrandBuffer.toString());        ColorBuffer.append("</EM>-5\'\n");     //the added </b> is if the last base                                              //  is the end of the terminator                BW_Buffer.append("5\'-");        BW_Buffer.append(BW_TopStrandBuffer.toString() + "-3\'\n"        		        + "   " + BW_BasePairBuffer.toString() + "\n"						+ "3\'-" + BW_BottomStrandBuffer.toString());        BW_Buffer.append("-5\'\n");        return new HTMLContainer(ColorBuffer.toString(), BW_Buffer.toString());    }        public HTMLContainer generatepremRNA_HTML(int selectedDNABase){    	StringBuffer BW_Buffer = new StringBuffer();    	StringBuffer ColorBuffer = new StringBuffer();    	boolean highlighted = false;        ColorSequencer exonColorSequencer = new ColorSequencer();        //see if a prokaryote        if (!(_intronStartSequence.equals("none") || _intronEndSequence.equals("none"))) {    	    //then the label for the pre-mRNA	        ColorBuffer.append("</pre><h3>pre-mRNA: <EM class=exon>Ex</EM><EM class=next>o</EM>"	                          + "<EM class=another>n</EM> Intron</h3><pre>");	        BW_Buffer.append("</pre><h3>pre-mRNA: EXON intron</h3><pre>");            //then the pre mRNA            //first the leading spaces            if (!_premRNASequence.equals("")) {                for (int i = 0; i < _numSpacesBeforeRNA_Start; i++ ) {                    ColorBuffer.append(" ");                    BW_Buffer.append(" ");                }                ColorBuffer.append("5\'-");	                 BW_Buffer.append("5\'-");	                             //print out bases and mark exons as you go                 for (int i = 0; i < _DNASequence.length(); i++) {                    //get this & the previous nucleotide                    Nucleotide current = (Nucleotide)_DNANucleotides.get(i);                    Nucleotide prev;                    if (i != 0) {                        prev = (Nucleotide)_DNANucleotides.get(i - 1);                    } else {                        prev = (Nucleotide)_DNANucleotides.get(i);                    }                            //see if it's in the pre mRNA                    if (current.getInPremRNA()) {                        //see if start of exon                        if (!prev.getInmRNA() && current.getInmRNA()) {                            ColorBuffer.append("<EM class=" + exonColorSequencer.getNextColor() + ">");                            highlighted = true;                        }                                            //see if end of exon                        if (prev.getInmRNA() && !current.getInmRNA()) {                             ColorBuffer.append("</EM>");                             highlighted = false;                        }                                              //see if selected                        if (current.getSelected()) {                            ColorBuffer.append("<EM class=selected>");                            ColorBuffer.append(current.getRNABase());                            ColorBuffer.append("</EM>");                            if (highlighted){                            	BW_Buffer.append(current.getRNABase().toLowerCase());                            } else {                            	BW_Buffer.append(current.getRNABase());                            }                                        } else {                            ColorBuffer.append(current.getRNABase());                            if (highlighted){                            	BW_Buffer.append(current.getRNABase());                            } else {                            	BW_Buffer.append(current.getRNABase().toLowerCase());                            }                                        }                    }                }	            ColorBuffer.append("</EM>-3\'\n");    //needs the </em> for the end of the last exon	            BW_Buffer.append("-3\'\n");            } else {	            ColorBuffer.append("<font color=red>none</font>\n");	            BW_Buffer.append("none\n");	        }	    }        return new HTMLContainer(ColorBuffer.toString(), BW_Buffer.toString());    }        public HTMLContainer generatemRNA_HTML(int selectedDNABase){     	StringBuffer BW_Buffer = new StringBuffer();    	    StringBuffer ColorBuffer = new StringBuffer();    	    boolean highlighted = false;    	    boolean inPolyA = false;    	        ColorSequencer exonColorSequencer = new ColorSequencer();	    //then the label for the mature mRNA & the protein	    ColorBuffer.append("</pre><h3>");	    BW_Buffer.append("</pre><h3>");	    if (!(_intronStartSequence.equals("none") || _intronEndSequence.equals("none"))) {	        ColorBuffer.append("mature-");	        BW_Buffer.append("mature-");	    }	    ColorBuffer.append("mRNA and Protein (<font color=blue>previous</font>):</h3><pre>");	    BW_Buffer.append("mRNA and Protein (previous on line below):</h3><pre>");	    	    //if it's a prokaryote, add the leading spaces	    if (_intronStartSequence.equals("none") || _intronEndSequence.equals("none")) {	        for (int i = 0; i < _numSpacesBeforeRNA_Start; i++ ) {	            ColorBuffer.append(" ");	            BW_Buffer.append(" ");	        }	    }	    	    //then the mature mRNA itself with exons, start, & stop codons marked	    if (!_mRNASequence.equals("")) {    	    ColorBuffer.append("5\'-");    	    BW_Buffer.append("5\'-");            for (int i = 0; i < _DNANucleotides.size(); i++) {                //get this & the previous nucleotide                Nucleotide current = (Nucleotide)_DNANucleotides.get(i);                Nucleotide prev;                if (i != 0) {                    prev = (Nucleotide)_DNANucleotides.get(i - 1);                } else {                    prev = (Nucleotide)_DNANucleotides.get(i);                }                                Nucleotide next;                if (i != (_DNANucleotides.size() - 1)) {                    next = (Nucleotide)_DNANucleotides.get(i + 1);                } else {                	   next = (Nucleotide)_DNANucleotides.get(i);                }                        //see if it's in the mRNA and the pre mRNA (to avoid the poly A tail)                if (current.getInmRNA()) {                    //see if start of exon                    if (!prev.getInmRNA() && current.getInmRNA()) {                        ColorBuffer.append("<EM class=" + exonColorSequencer.getNextColor() + ">");                        if (highlighted) {                            ColorBuffer.append("<u>");  // do this because sometimes the exon boundaries                                                        // mess up the underlining                        }                    }                                                                         //see if end of last exon (start of poly A)                    if (!current.getInPremRNA() && current.getInmRNA() && !inPolyA) {                    		ColorBuffer.append("</EM>");                    		inPolyA = true;                    }                                    //see if start of start or stop codon                    if (((current.getAANum() == 0) || (current.getAANum() == -2))                           && ((current.getCodonPosition() == 0) && (current.getInPremRNA()))) {                        ColorBuffer.append("<u>");                        highlighted = true;                    }                                    //see is end of start codon                    if ((current.getAANum() == 1) && (current.getCodonPosition() == 0)) {                         ColorBuffer.append("</u>");                         highlighted = false;                    }                                    //see if end of stop codon                    if ((current.getAANum() == -1) && (prev.getAANum() == -2)) {                        ColorBuffer.append("</u>");                        highlighted = false;                     }                                    //see if selected (& in pre mRNA - to avoid selecting poly A tail)                    if (current.getSelected() && current.getInPremRNA()) {                        ColorBuffer.append("<EM class=selected>");                        ColorBuffer.append(current.getRNABase());                        ColorBuffer.append("</EM>");                        if (highlighted){                        	BW_Buffer.append(current.getRNABase());                        } else {                        	BW_Buffer.append(current.getRNABase().toLowerCase());                        }                    } else {                        ColorBuffer.append(current.getRNABase());                        if (highlighted){                        	BW_Buffer.append(current.getRNABase().toLowerCase());                        } else {                        	BW_Buffer.append(current.getRNABase());                        }                    }                                        // see if its at the end of an exon                    if (current.getInmRNA() && !next.getInmRNA()){                    	   ColorBuffer.append("</EM>");                    }                 }            }	        ColorBuffer.append("-3\'\n");    	        BW_Buffer.append("-3\'\n");	    } else {	        ColorBuffer.append("<font color=red>none</font>\n");	        BW_Buffer.append("none\n");	    }	    return new HTMLContainer(ColorBuffer.toString(), BW_Buffer.toString());    }        public HTMLContainer generateProteinHTML(int selectedDNABase){    	StringBuffer BW_Buffer = new StringBuffer();    	StringBuffer ColorBuffer = new StringBuffer();    	boolean highlighted = false;    	StringBuffer proteinStringBuffer = new StringBuffer();	    	    //if a prokaryote, need more leader to align with top DNA	    if (_intronStartSequence.equals("none") || _intronEndSequence.equals("none")) {	        for (int i = 0; i < _numSpacesBeforeRNA_Start; i++ ) {	            proteinStringBuffer.append(" ");	            BW_Buffer.append(" ");	        }	    }	    if (!_proteinSequence.equals("")) {    	    for (int i = 0; i < _DNASequence.length(); i++) {	            Nucleotide n = (Nucleotide)_DNANucleotides.get(i);	            if (n.getInmRNA()) {	                if (n.getAANum() == 0) {	                    break;	                }	                proteinStringBuffer.append(" ");	                BW_Buffer.append(" ");	            }	        }	        proteinStringBuffer.append(" N-");	        BW_Buffer.append(" N-" + _proteinSequence + "-C\n");	    	        if (selectedDNABase != -1) {        	    //mark the selected amino acid	            // do this by marking up the protein sequence string	            StringBuffer proteinBuffer = new StringBuffer(_proteinSequence);	            Nucleotide n = (Nucleotide)_DNANucleotides.get(selectedDNABase);	    	            //see if it is in an amino acid	            if (n.getAANum() >= 0) {            	    //first, the end of the selected AA	                proteinBuffer = proteinBuffer.insert(((n.getAANum() * 3) + 3), "</EM>");	                //then, the end of the part of the codon	                proteinBuffer = proteinBuffer.insert(((n.getAANum() * 3) + n.getCodonPosition() + 1), "</u>");	                //then, the start of the psrt of the codon	                proteinBuffer = proteinBuffer.insert(((n.getAANum() * 3) + n.getCodonPosition()), "<u>");	                //then the start	                proteinBuffer = proteinBuffer.insert((n.getAANum() * 3), "<EM class=selected>");	            }	            proteinStringBuffer.append(proteinBuffer.toString() + "-C");            } else {	            proteinStringBuffer.append(_proteinSequence + "-C");	        }	    } else {	        proteinStringBuffer.append("<font color=red>none</font>\n");	        BW_Buffer.append("none\n");	        	    }	    _proteinString = proteinStringBuffer.toString();	    ColorBuffer.append(_proteinString + "\n");	    return new HTMLContainer(ColorBuffer.toString(), BW_Buffer.toString());    }        public HTMLContainer markUpNucleotideSymbol(int nucleotideNum, String nucleotideSymbol,    		                                   boolean selected, boolean highlighted){    	StringBuffer BW_Buffer = new StringBuffer();    	StringBuffer ColorBuffer = new StringBuffer();    	        if (nucleotideNum == _promoterStart) {            ColorBuffer.append("<EM class=promoter>");        }                if (nucleotideNum == _promoterStart + _promoterSequence.length()) {            ColorBuffer.append("</EM>");        }               if (nucleotideNum == _terminatorStart) {            ColorBuffer.append("<EM class=terminator>");        }          if (nucleotideNum == _terminatorStart + _terminatorSequence.length()) {            ColorBuffer.append("</EM>");        }                if (selected) {            ColorBuffer.append("<EM class=selected>");            ColorBuffer.append(nucleotideSymbol);            ColorBuffer.append("</EM>");            if (highlighted){            	BW_Buffer.append(nucleotideSymbol);            } else {            	BW_Buffer.append(nucleotideSymbol.toLowerCase());            }        } else {            ColorBuffer.append(nucleotideSymbol);            if (highlighted){            	BW_Buffer.append(nucleotideSymbol.toLowerCase());            } else {            	BW_Buffer.append(nucleotideSymbol);            }        }	      	return new HTMLContainer(ColorBuffer.toString(), BW_Buffer.toString());    }            public int calcCharsBeforeFirstDNA_Base(String HTMLstring){    	String noHTMLstring = HTMLstring.replaceAll("<[^<]*>","");    	return noHTMLstring.indexOf("5\'-") + 4;    }    public int getHeaderLength() {    	return _numCharsInDisplayBeforeFirstDNA_Base;    }        public int getDNASequenceLength() {    	return _DNASequence.length();    }        //remove html tags from protein string so it can be displayed as previous sequence    public String getProteinString() {        String s = new String(_proteinString);        s = s.replaceAll("<EM class=selected>", "");        s = s.replaceAll("</EM>","");        s = s.replaceAll("<u>","");        return s.replaceAll("</u>", "");    }    }