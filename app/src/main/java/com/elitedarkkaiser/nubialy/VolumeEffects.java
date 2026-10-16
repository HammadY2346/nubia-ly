package com.elitedarkkaiser.nubialy;
import android.graphics.*;
import java.util.function.BooleanSupplier;
/** Static Rising-inspired shading over one native vendor slider draw. */
public final class VolumeEffects {
    public static final String[] NAMES={"Stock","Outline","Gradient","Neumorph","Neumorph Outline","Double Layer","Shaded Layer"};
    private VolumeEffects(){}
    public static boolean draw(int style,Canvas canvas,RectF bounds,float[] corners,float density,BooleanSupplier allowed,QsOutlineRenderer.NativeDraw stock)throws Throwable{
        if(style<1||style>6||!allowed.getAsBoolean()||bounds.isEmpty()||!Float.isFinite(density)||density<=0||corners==null||corners.length!=8)return false;
        for(float n:corners)if(!Float.isFinite(n)||n<0)return false;
        int save=canvas.saveLayer(null,null);boolean complete=false;
        try{if(!allowed.getAsBoolean())return false;stock.draw();if(!allowed.getAsBoolean())return false;
            Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);p.setBlendMode(BlendMode.SRC_ATOP);float unit=Math.min(density,Math.min(bounds.width(),bounds.height())/12f);
            if(style==2){p.setShader(new LinearGradient(bounds.left,bounds.top,bounds.right,bounds.bottom,0x50d3cbef,0x60353778,Shader.TileMode.CLAMP));canvas.drawRect(bounds,p);}
            if(style==3||style==4||style==5){
                p.setShader(new LinearGradient(bounds.left,bounds.top,bounds.left,bounds.bottom,0x70ffffff,0x70000000,Shader.TileMode.CLAMP));canvas.drawPath(ring(bounds,corners,0,2*unit),p);
                p.setShader(new LinearGradient(bounds.left,bounds.top,bounds.left,bounds.bottom,0x50000000,0x50ffffff,Shader.TileMode.CLAMP));canvas.drawPath(ring(bounds,corners,2*unit,4*unit),p);
                if(style==5){p.setShader(new LinearGradient(bounds.left,bounds.top,bounds.right,bounds.bottom,0x45ffffff,0x55000000,Shader.TileMode.CLAMP));canvas.drawPath(ring(bounds,corners,5*unit,7*unit),p);}
            }
            if(style==6){p.setShader(new LinearGradient(bounds.left,bounds.top,bounds.right,bounds.bottom,0x70000000,0x10ffffff,Shader.TileMode.CLAMP));canvas.drawRect(bounds,p);}
            if(style==1||style==4){p.setShader(null);p.setBlendMode(BlendMode.DST_OUT);p.setColor(0x80000000);canvas.drawPath(outline(bounds,corners,(style==4?4:2.5f)*unit),p);}
            if(!allowed.getAsBoolean())return false;complete=true;return true;
        }finally{try{if(!complete)canvas.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR);}finally{canvas.restoreToCount(save);}}
    }
    private static Path outline(RectF bounds,float[] corners,float inset){RectF b=new RectF(bounds);b.inset(inset,inset);Path path=new Path();if(b.isEmpty())return path;float[] radii=corners.clone();for(int i=0;i<8;i++)radii[i]=Math.max(0,radii[i]-inset);path.addRoundRect(b,radii,Path.Direction.CW);return path;}
    private static Path ring(RectF bounds,float[] corners,float start,float end){Path path=outline(bounds,corners,start);path.setFillType(Path.FillType.EVEN_ODD);path.addPath(outline(bounds,corners,end));return path;}
}
