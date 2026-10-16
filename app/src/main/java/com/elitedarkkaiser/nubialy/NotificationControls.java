package com.elitedarkkaiser.nubialy;
/** Packed cosmetic draft: opacity 0=stock, otherwise 50..100; corner 0=stock, 1=12dp, 2=24dp, 3=32dp. */
public final class NotificationControls {
 private NotificationControls(){}
 public static int opacity(int value){return value&255;}
 public static int corners(int value){return value>>>8;}
 public static int encode(int opacity,int corners){int v=opacity|(corners<<8);if(!valid(v))throw new IllegalArgumentException();return v;}
 public static boolean valid(int value){int a=opacity(value),c=corners(value);return value>=0&&value<=868&&(a==0||(a>=50&&a<=100&&a%5==0))&&c>=0&&c<=3;}
 public static int radius(int value){return switch(corners(value)){case 1->12;case 2->24;case 3->32;default->0;};}
}
