package com.elitedarkkaiser.nubialy;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
public final class ConnectivityStyles {
 public static final String[] NAMES={"Stock","Nothing Dot","Capsule","Round","Sharp","Wavy","Stroke","Xperia","ZigZag"};
 private static Resources resources;
 private ConnectivityStyles(){}
 public static void resources(Resources value){resources=value;}
 public static Drawable artwork(boolean wifi,int style,int level){if(resources==null||style<1||style>=NAMES.length||level<0||level>4)return null;int id=resources.getIdentifier("rising_"+(wifi?"wifi":"signal")+"_"+style+"_"+level,"drawable","com.elitedarkkaiser.nubialy");return id==0?null:resources.getDrawable(id,null).mutate();}
}
