package ir.batna.neda.utils;

import java.security.SecureRandom;
import java.util.Random;

public class SabaSecureRandom {

    public String generateSecureRandom() {

        final String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final Random random = new SecureRandom();
        final char[] buf;
        buf = new char[32];
        for (int i = 0; i < buf.length; ++i)
            buf[i] = symbols.charAt(random.nextInt(symbols.length()));
        String result = new String(buf);
        return result;
    }
}
