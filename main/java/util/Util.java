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
}