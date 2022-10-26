package util;

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
            val = random.nextInt(10);
            // Prevent generating 0 on very first time.
            if(i == 0 && val == 0) {
                i--;
                continue;
            }
            pin = (pin * 10) + val;
        }

        return pin;
    }
}