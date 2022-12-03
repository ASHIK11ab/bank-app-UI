package comparator;

import java.util.Comparator;

import model.Beneficiary;

public class BeneficiaryComparator implements Comparator<Beneficiary> {
	
	// Beneficiaries are compared based on their names (dictionary format).
    @Override
    public int compare(Beneficiary base, Beneficiary target) {
    	if(base == null || target == null)
    		return -1;
    	
    	char baseCharacter, targetCharacter;
    	    	
    	int minLength = (base.getName().length() < target.getName().length()) ? base.getName().length() : target.getName().length();
    	
    	for(int index = 0; index < minLength; ++index) {
    		baseCharacter = Character.toLowerCase(base.getName().charAt(index));
    		targetCharacter = Character.toLowerCase(target.getName().charAt(index));
    		
    		if(baseCharacter != targetCharacter) {
    			return baseCharacter - targetCharacter;
    		}
    	}
    	
    	// When first 'minLength' characters are same, string with lesser length
    	// is stored first.
    	if(base.getName().length() <= target.getName().length())
    		return -1;
    	else
    		return 1;
    }
}