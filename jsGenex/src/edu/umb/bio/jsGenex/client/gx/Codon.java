package edu.umb.bio.jsGenex.client.gx;
//class for translating mRNA
//returns string of three-letter amino acid abbreviation
//stop codons are returned as ""

public class Codon {

  public Codon() {
  }
  
  public static String getAA(String codon) {
  
      if (codon.equals("UUU")) return "Phe";
      if (codon.equals("UUC")) return "Phe";
      if (codon.equals("UUA")) return "Leu";
      if (codon.equals("UUG")) return "Leu";
      
      if (codon.equals("CUU")) return "Leu";
      if (codon.equals("CUC")) return "Leu";
      if (codon.equals("CUA")) return "Leu";
      if (codon.equals("CUG")) return "Leu";
      
      if (codon.equals("AUU")) return "Ile";
      if (codon.equals("AUC")) return "Ile";
      if (codon.equals("AUA")) return "Ile";
      if (codon.equals("AUG")) return "Met";
      
      if (codon.equals("GUU")) return "Val";
      if (codon.equals("GUC")) return "Val";
      if (codon.equals("GUA")) return "Val";
      if (codon.equals("GUG")) return "Val";


      if (codon.equals("UCU")) return "Ser";
      if (codon.equals("UCC")) return "Ser";
      if (codon.equals("UCA")) return "Ser";
      if (codon.equals("UCG")) return "Ser";
      
      if (codon.equals("CCU")) return "Pro";
      if (codon.equals("CCC")) return "Pro";
      if (codon.equals("CCA")) return "Pro";
      if (codon.equals("CCG")) return "Pro";
      
      if (codon.equals("ACU")) return "Thr";
      if (codon.equals("ACC")) return "Thr";
      if (codon.equals("ACA")) return "Thr";
      if (codon.equals("ACG")) return "Thr";
      
      if (codon.equals("GCU")) return "Ala";
      if (codon.equals("GCC")) return "Ala";
      if (codon.equals("GCA")) return "Ala";
      if (codon.equals("GCG")) return "Ala";


      if (codon.equals("UAU")) return "Tyr";
      if (codon.equals("UAC")) return "Tyr";
      if (codon.equals("UAA")) return "";
      if (codon.equals("UAG")) return "";
      
      if (codon.equals("CAU")) return "His";
      if (codon.equals("CAC")) return "His";
      if (codon.equals("CAA")) return "Gln";
      if (codon.equals("CAG")) return "Gln";
      
      if (codon.equals("AAU")) return "Asn";
      if (codon.equals("AAC")) return "Asn";
      if (codon.equals("AAA")) return "Lys";
      if (codon.equals("AAG")) return "Lys";
      
      if (codon.equals("GAU")) return "Asp";
      if (codon.equals("GAC")) return "Asp";
      if (codon.equals("GAA")) return "Glu";
      if (codon.equals("GAG")) return "Glu";
      

      if (codon.equals("UGU")) return "Cys";
      if (codon.equals("UGC")) return "Cys";
      if (codon.equals("UGA")) return "";
      if (codon.equals("UGG")) return "Trp";
      
      if (codon.equals("CGU")) return "Arg";
      if (codon.equals("CGC")) return "Arg";
      if (codon.equals("CGA")) return "Arg";
      if (codon.equals("CGG")) return "Arg";
      
      if (codon.equals("AGU")) return "Ser";
      if (codon.equals("AGC")) return "Ser";
      if (codon.equals("AGA")) return "Arg";
      if (codon.equals("AGG")) return "Arg";
      
      if (codon.equals("GGU")) return "Gly";
      if (codon.equals("GGC")) return "Gly";
      if (codon.equals("GGA")) return "Gly";
      if (codon.equals("GGG")) return "Gly";
      
      return "";
  }


}