package util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Util {
    public static String genPassword() {
        String pass = "";
        Random random = new Random();
        for(byte i = 0; i < 4; ++i)
            pass += (char) ('a' + random.nextInt(26));

        for(byte i = 0; i < 4; ++i)
            pass += random.nextInt(10);

        return pass;
    }
    
    
    public static int genPin(int digit) {
        int pin = 0;
        int val;

        Random random = new Random();
        for(byte i = 0; i < digit; ++i) {
        	
        	// Prevent generating 0 on very first time.
        	if(i == 0)
        		val = random.nextInt(1, 10);
        	else
        		val = random.nextInt(10);

            pin = (pin * 10) + val;
        }

        return pin;
    }
    
    
    public static int getNoOfDigits(long number) {
    	int digits = 0;
    	number = Math.abs(number);
    	
    	while(number != 0) {
    		number = number / 10;
    		digits++;
    	}
    	
    	return digits;
    }
    
    
    public static int compareByName(String baseName, String targetName) {
    	char baseCharacter, targetCharacter;
    	
    	int minLength = (baseName.length() < targetName.length()) ? baseName.length() : targetName.length();
    	
    	for(int index = 0; index < minLength; ++index) {
    		baseCharacter = Character.toLowerCase(baseName.charAt(index));
    		targetCharacter = Character.toLowerCase(targetName.charAt(index));
    		
    		if(baseCharacter != targetCharacter) {
    			return baseCharacter - targetCharacter;
    		}
    	}
    	
    	// When first 'minLength' characters are same, string with lesser length
    	// is stored first.
    	if(baseName.length() < targetName.length())
    		return -1;
    	else
    		return 1;
    }
    
    
    public static String createNotification(String msg, String status) {
    	String html = "";
    	html += "<div class='notification " + status + "'>";
    	html += msg;
    	html += "</div>";
    	return html;
    }
    
    
    public static String getFormattedTime(LocalTime time) {
    	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("hh:mm a");
    	return time.format(fmt);
    }
}