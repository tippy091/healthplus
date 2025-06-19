package vn.com.nested.backend.common.operation.crypto;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/**
 * @author tippy091
 * @created 17/06/2025
 * @project healthcare_backend
 **/

public class RandomPassword {
    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String lower;
    public static final String digits = "0123456789";
    public static final String alphanum;
    private final Random random;
    private final char[] symbols;
    private final char[] buf;

    public RandomPassword(int length, Random random, String symbols) {
        if (length < 1) {
            throw new IllegalArgumentException();
        } else if (symbols.length() < 2) {
            throw new IllegalArgumentException();
        } else {
            this.random = (Random) Objects.requireNonNull(random);
            this.symbols = symbols.toCharArray();
            this.buf = new char[length];
        }
    }

    public RandomPassword(int length, Random random) {
        this(length, random, alphanum);
    }

    public RandomPassword(int length) {
        this(length, new SecureRandom());
    }

    public RandomPassword() {
        this(21);
    }

    public String nextString() {
        for(int idx = 0; idx < this.buf.length; ++idx) {
            this.buf[idx] = this.symbols[this.random.nextInt(this.symbols.length)];
        }

        return new String(this.buf);
    }

    static {
        lower = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase(Locale.ROOT);
        alphanum = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + lower + "0123456789";
    }
}
