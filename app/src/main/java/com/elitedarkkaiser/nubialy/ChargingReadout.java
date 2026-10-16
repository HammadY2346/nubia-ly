package com.elitedarkkaiser.nubialy;
import java.util.Locale;
/** Battery broadcast units: mV and tenths Celsius; BatteryManager current: microamps. */
public final class ChargingReadout {
 private ChargingReadout(){}
 public static String format(int millivolts,int microamps,int temperature){if(millivolts<2000||millivolts>20000)return "Battery voltage unavailable";String result=String.format(Locale.getDefault(),"Battery %.3f V",millivolts/1000d);if(microamps!=Integer.MIN_VALUE&&Math.abs((long)microamps)<=30000000)result+=String.format(Locale.getDefault()," · %+,d mA",Math.round(microamps/1000d));if(temperature>=-200&&temperature<=1000)result+=String.format(Locale.getDefault()," · %.1f °C",temperature/10d);return result;}
}
