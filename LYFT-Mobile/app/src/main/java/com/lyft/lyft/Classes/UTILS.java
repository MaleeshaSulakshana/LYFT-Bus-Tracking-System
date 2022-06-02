package com.lyft.lyft.Classes;

import android.util.Patterns;

import java.util.Random;

public class UTILS {

    public static String generateRandomNumber() {

        int min = 1000000;
        int max = 9999999;
        String random_number = String.valueOf(new Random().nextInt(max) + min);

        return random_number;
    }

    public static boolean isValidEmail(CharSequence target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
