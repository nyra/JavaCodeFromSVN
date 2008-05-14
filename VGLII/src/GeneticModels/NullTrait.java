package GeneticModels;

// null trait to put on a sex chromosome if there are no other traits there
//  it is a place holder so the genetic models work right and the 
//  sex chromosomes are segregated right and the sex determination works
//  even if there are no sex-linked traits
public class NullTrait extends Trait {

	public NullTrait(String traitName, String type, String bodyPart) {
		super(traitName, type, bodyPart);
	}
	
	public NullTrait() {
		super("", "Null", "");
	}

}
