package com.utils;

import java.math.BigDecimal;

public class NumberUtils {
    public NumberUtils() {
    }

    public static int parseInt(Object o, int defaultValue) {
        int returnValue = defaultValue;
        if (o != null) {
            try {
                returnValue = Integer.parseInt(o.toString());
            } catch (Exception var4) {
                ;
            }
        }

        return returnValue;
    }

    public static float parseFloat(Object o, float defaultValue) {
        float returnValue = defaultValue;
        if (o != null) {
            try {
                returnValue = Float.parseFloat(o.toString());
            } catch (Exception var4) {
                ;
            }
        }

        return returnValue;
    }

    public static double parseDouble(Object o, double defaultValue) {
        double returnValue = defaultValue;
        if (o != null) {
            try {
                returnValue = Double.parseDouble(o.toString());
            } catch (Exception var6) {
                ;
            }
        }

        return returnValue;
    }

    public static double getMoney(double d) {
        double returnValue = 0.0D;
        returnValue = (new BigDecimal(d)).setScale(2, 4).doubleValue();
        return returnValue;
    }

    public static float getMoney(float d) {
        float returnValue = 0.0F;
        returnValue = (new BigDecimal((double)d)).setScale(2, 4).floatValue();
        return returnValue;
    }

    public static String getMoneyStr(double d) {
        String returnValue = "0";
        double temp = (new BigDecimal(d)).setScale(2, 4).doubleValue();
        returnValue = String.valueOf(temp);
        if (temp % 1.0D == 0.0D) {
            returnValue = String.valueOf((long)temp);
        }

        return returnValue;
    }

    public static String getMoneyStr(float f) {
        String returnValue = "0";
        float temp = (new BigDecimal((double)f)).setScale(2, 4).floatValue();
        returnValue = String.valueOf(temp);
        if ((double)temp % 1.0D == 0.0D) {
            returnValue = String.valueOf((long)temp);
        }

        return returnValue;
    }

    public static void main(String[] args) {
        int i = parseInt((Object)null, 0);
        System.out.println(i);
    }
}
