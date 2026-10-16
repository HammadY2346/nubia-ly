package com.elitedarkkaiser.nubialy;
/** Append-only app draft IDs; stock remains zero. */
public final class BatteryStyles {
 private BatteryStyles(){}
 public static final int TEXT=5,HIDDEN=6;
 public static final String[] NAMES={"Stock","Portrait","Circle","Dotted circle","Filled circle","Text only","Hidden","Landscape right","Landscape left","Big circle","Big dotted circle","Capsule","Lorn","Portrait iOS","Portrait Mx","Airoo","Landscape right A","Landscape left A","Landscape right B","Landscape left B","iOS 15","iOS 16","Origami","OneUI 7","MiUI Pill"};
 public static boolean valid(int id){return id>=0&&id<NAMES.length;}
}
