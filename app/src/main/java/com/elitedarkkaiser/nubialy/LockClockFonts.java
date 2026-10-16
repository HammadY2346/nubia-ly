package com.elitedarkkaiser.nubialy;
/** Named Android font families and faces; no downloaded font or theme writes. */
public final class LockClockFonts {
    public record Face(String name,String category,String family,int style){}
    public static final Face[] FACES={
        new Face("Stock","Original",null,0),
        new Face("Sans","Modern","sans-serif",0),new Face("Sans bold","Modern","sans-serif",1),new Face("Sans italic","Modern","sans-serif",2),new Face("Sans bold italic","Modern","sans-serif",3),
        new Face("Thin","Modern","sans-serif-thin",0),new Face("Thin italic","Modern","sans-serif-thin",2),new Face("Light","Modern","sans-serif-light",0),new Face("Light italic","Modern","sans-serif-light",2),new Face("Medium","Modern","sans-serif-medium",0),new Face("Black","Modern","sans-serif-black",0),
        new Face("Condensed","Compact","sans-serif-condensed",0),new Face("Condensed bold","Compact","sans-serif-condensed",1),new Face("Condensed italic","Compact","sans-serif-condensed",2),new Face("Condensed bold italic","Compact","sans-serif-condensed",3),
        new Face("Serif","Classic","serif",0),new Face("Serif bold","Classic","serif",1),new Face("Serif italic","Classic","serif",2),new Face("Serif bold italic","Classic","serif",3),
        new Face("Mono","Technical","monospace",0),new Face("Mono bold","Technical","monospace",1),new Face("Mono italic","Technical","monospace",2),new Face("Serif mono","Technical","serif-monospace",0),
        new Face("Casual","Expressive","casual",0),new Face("Cursive","Expressive","cursive",0),new Face("Small caps","Expressive","sans-serif-smallcaps",0)
    };
    public static boolean valid(int value){return value>=0&&value<FACES.length;}
    private LockClockFonts(){}
}
