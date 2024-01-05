package com.zcunsoft.clklog.api.utils;

import java.text.DecimalFormat;

public class MathUtils {

	private static final ThreadLocal<DecimalFormat> decimalFormat =
            new ThreadLocal<DecimalFormat>() {
                @Override
                protected DecimalFormat initialValue() {
                    return new DecimalFormat("0.####");
                }
            };
	
	 public static float getRate(int total,int value) {
	    	return total > 0 ? value * 1.0f / total : 0.0f;
	 }
	 
	 public static float getRateByMultip100(int total,int value) {
	    	float rate = total > 0 ? value * 1.0f / total : 0.0f;
	    	return Float.parseFloat(decimalFormat.get().format(rate*100));
	 }
}
