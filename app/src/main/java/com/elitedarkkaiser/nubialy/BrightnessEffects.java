package com.elitedarkkaiser.nubialy;

import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import java.util.function.BooleanSupplier;

/** Draw-only adaptations of Rising's Outline, Neumorph and Thin slider treatments. */
public final class BrightnessEffects {
    public static final String[] NAMES={"Stock","Outline","Neumorph","Thin"};
    private BrightnessEffects(){}
    public static boolean draw(int style,Canvas canvas,GradientDrawable drawable,float density,BooleanSupplier allowed,QsOutlineRenderer.NativeDraw nativeDraw)throws Throwable{
        if(style==1)return QsOutlineRenderer.draw(canvas,drawable,density,allowed,nativeDraw);
        if(style==2)return QsTileEffects.draw(3,canvas,drawable,density,allowed,nativeDraw);
        if(style!=3||!allowed.getAsBoolean()||!Float.isFinite(density)||density<=0||drawable.getShape()!=GradientDrawable.RECTANGLE||drawable.getBounds().isEmpty())return false;
        RectF bounds=new RectF(drawable.getBounds());float height=Math.min(bounds.height(),4*density);
        RectF line=new RectF(bounds.left,bounds.centerY()-height/2,bounds.right,bounds.centerY()+height/2);
        int layer=canvas.saveLayer(bounds,null);boolean complete=false;
        try{
            Path clip=new Path();clip.addRoundRect(line,height/2,height/2,Path.Direction.CW);canvas.clipPath(clip);
            if(!allowed.getAsBoolean())return false;nativeDraw.draw();if(!allowed.getAsBoolean())return false;
            complete=true;return true;
        }finally{try{if(!complete)canvas.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR);}finally{canvas.restoreToCount(layer);}}
    }
}
