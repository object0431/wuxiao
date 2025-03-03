package com.asiainfo.fsip.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class GenerateUtil {

    public static String generateProjectId() {
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmssSSS");
        String timeZone = format.format(Calendar.getInstance().getTime());
        return timeZone.concat(randomString(3));
    }

    public static String randomString(int len) {
        StringBuilder s = new StringBuilder();
        Random random = new Random();
        while(len > 0) {
            s.append(random.nextInt(10));
            len--;
        }
        return s.toString();
    }
}
