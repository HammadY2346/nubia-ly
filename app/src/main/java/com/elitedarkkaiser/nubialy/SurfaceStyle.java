package com.elitedarkkaiser.nubialy;
/** Fixed view transforms for four inspected text targets; configuration cannot name arbitrary views. */
public record SurfaceStyle(int hidden, int primary, int secondary, int clock, int date,
        int primaryPosition,int secondaryPosition,int clockPosition,int datePosition,int tileShape,int largeTileShape) {
    public SurfaceStyle(int hidden,int primary,int secondary,int clock,int date,int primaryPosition,int secondaryPosition,int clockPosition,int datePosition,int tileShape){this(hidden,primary,secondary,clock,date,primaryPosition,secondaryPosition,clockPosition,datePosition,tileShape,0);}
    public SurfaceStyle(int hidden,int primary,int secondary,int clock,int date,int primaryPosition,int secondaryPosition,int clockPosition,int datePosition){this(hidden,primary,secondary,clock,date,primaryPosition,secondaryPosition,clockPosition,datePosition,0);}
    public static final SurfaceStyle STOCK=new SurfaceStyle(0,0,0,0,0,0,0,0,0);
    public boolean valid(){return largeTileShape>=0&&largeTileShape<=3&&tileShape>=0&&tileShape<=3&&hidden>=0&&hidden<=15&&StatusBarStyle.appearanceValid(primary)&&StatusBarStyle.appearanceValid(secondary)&&StatusBarStyle.appearanceValid(clock)&&StatusBarStyle.appearanceValid(date)
        &&StatusBarStyle.transformValid(primaryPosition)&&StatusBarStyle.transformValid(secondaryPosition)&&StatusBarStyle.transformValid(clockPosition)&&StatusBarStyle.transformValid(datePosition);}
    public int appearance(int group){return switch(group){case 0->primary;case 1->secondary;case 2->clock;case 3->date;default->throw new IllegalArgumentException();};}
    public int position(int group){return switch(group){case 0->primaryPosition;case 1->secondaryPosition;case 2->clockPosition;case 3->datePosition;default->throw new IllegalArgumentException();};}
    public boolean hide(int group){return (hidden&(1<<group))!=0;}
    public boolean any(){return !equals(STOCK);}
    public String encode(){return hidden+","+primary+","+secondary+","+clock+","+date+","+primaryPosition+","+secondaryPosition+","+clockPosition+","+datePosition+","+tileShape+","+largeTileShape;}
    public static SurfaceStyle decode(String text){
        if(text==null||text.length()>160)return null;String[] fields=text.split(",",-1);if(fields.length!=9&&fields.length!=10&&fields.length!=11)return null;
        int[] n=new int[11];try{for(int i=0;i<fields.length;i++){if(!fields[i].matches("[0-9]{1,8}"))return null;n[i]=Integer.parseInt(fields[i]);}}catch(NumberFormatException bad){return null;}
        SurfaceStyle result=new SurfaceStyle(n[0],n[1],n[2],n[3],n[4],n[5],n[6],n[7],n[8],n[9],n[10]);return result.valid()?result:null;
    }
}
