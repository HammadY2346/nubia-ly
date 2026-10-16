package com.elitedarkkaiser.nubialy;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import java.util.function.BooleanSupplier;

/** Outline composition reuses stock drawing, including its state colors, tint and alpha. */
public final class QsOutlineRenderer {
    private QsOutlineRenderer(){}
    @FunctionalInterface public interface NativeDraw {void draw() throws Throwable;}
    public static boolean draw(Canvas canvas,GradientDrawable drawable,float density,BooleanSupplier permitted,NativeDraw stock) throws Throwable {
        if(!permitted.getAsBoolean()||!Float.isFinite(density)||density<=0)return false;
        Rect bounds=drawable.getBounds();int shape=drawable.getShape();
        if(bounds.isEmpty()||(shape!=GradientDrawable.RECTANGLE&&shape!=GradientDrawable.OVAL))return false;
        float stroke=Math.min(2.5f*density,Math.min(bounds.width(),bounds.height())/4f);
        float[] radii=drawable.getCornerRadii();
        if(radii==null){radii=new float[8];java.util.Arrays.fill(radii,drawable.getCornerRadius());}
        if(radii.length!=8)return false;for(float r:radii)if(!Float.isFinite(r)||r<0)return false;
        RectF outer=new RectF(bounds),inner=new RectF(bounds);inner.inset(stroke,stroke);
        Path ring=new Path(),interior=new Path();ring.setFillType(Path.FillType.EVEN_ODD);
        if(shape==GradientDrawable.OVAL){ring.addOval(outer,Path.Direction.CW);interior.addOval(inner,Path.Direction.CW);}
        else {ring.addRoundRect(outer,radii,Path.Direction.CW);float[] inset=radii.clone();for(int i=0;i<8;i++)inset[i]=Math.max(0,inset[i]-stroke);interior.addRoundRect(inner,inset,Path.Direction.CW);}
        ring.addPath(interior);
        int saved=canvas.save();
        try{
            int layer=canvas.saveLayerAlpha(outer,128);
            try{canvas.clipPath(interior);if(!permitted.getAsBoolean())return false;stock.draw();}finally{canvas.restoreToCount(layer);}
            if(!permitted.getAsBoolean())return false;
            canvas.clipPath(ring);stock.draw();return true;
        }finally{canvas.restoreToCount(saved);}
    }
}
