package com.elitedarkkaiser.nubialy;

import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import java.util.function.BooleanSupplier;

/** Native color/alpha is the base; SRC_ATOP confines static shading to that background. */
public final class QsTileEffects {
    public static final String[] NAMES={"Stock","Outline","CyberPunk","Neumorph","Reflected","Shaded","Surround","Thin"};
    private QsTileEffects(){}
    public static boolean draw(int style,Canvas canvas,GradientDrawable tile,float density,BooleanSupplier permitted,QsOutlineRenderer.NativeDraw stock)throws Throwable{
        if(style==1)return QsOutlineRenderer.draw(canvas,tile,density,permitted,stock);
        if(style<2||style>=NAMES.length||!permitted.getAsBoolean()||!Float.isFinite(density)||density<=0||tile.getBounds().isEmpty()||(tile.getShape()!=GradientDrawable.RECTANGLE&&tile.getShape()!=GradientDrawable.OVAL))return false;
        RectF b=new RectF(tile.getBounds());float unit=Math.min(density,Math.min(b.width(),b.height())/20f);
        int saved=canvas.saveLayer(b,null);boolean complete=false;
        try{
            if(!permitted.getAsBoolean())return false;stock.draw();if(!permitted.getAsBoolean())return false;
            Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);p.setBlendMode(BlendMode.SRC_ATOP);
            switch(style){
                case 2->{
                    gradient(canvas,b,p,0x14ffffff,0x50000000,false);
                    Path facets=new Path();facets.moveTo(b.left+3*unit,b.top+7*unit);facets.lineTo(b.centerX()-2*unit,b.top+7*unit);facets.lineTo(b.centerX()+2*unit,b.bottom-3*unit);facets.lineTo(b.left+3*unit,b.bottom-3*unit);facets.close();
                    facets.moveTo(b.centerX()+2*unit,b.top+3*unit);facets.lineTo(b.right-3*unit,b.top+3*unit);facets.lineTo(b.right-3*unit,b.bottom-7*unit);facets.lineTo(b.centerX()+6*unit,b.bottom-7*unit);facets.close();p.setShader(null);p.setColor(0x30ffffff);canvas.drawPath(facets,p);
                }
                case 3->{
                    bevel(canvas,tile,b,p,2*unit,0x60ffffff,0x60000000);
                    RectF inset=new RectF(b);inset.inset(2*unit,2*unit);Path ring=ring(tile,b,2*unit,4*unit);p.setShader(new LinearGradient(0,inset.top,0,inset.bottom,0x38000000,0x38ffffff,Shader.TileMode.CLAMP));canvas.drawPath(ring,p);
                }
                case 4->{
                    gradient(canvas,b,p,0x34ffffff,0x24000000,true);Path reflection=new Path();reflection.moveTo(b.left,b.top);reflection.lineTo(b.right,b.top);reflection.lineTo(b.right,b.top+b.height()*0.22f);reflection.lineTo(b.left,b.top+b.height()*0.72f);reflection.close();p.setShader(new LinearGradient(b.left,b.top,b.right,b.bottom,0x45ffffff,0x00ffffff,Shader.TileMode.CLAMP));canvas.drawPath(reflection,p);
                }
                case 5->gradient(canvas,b,p,0x60000000,0x00000000,false);
                case 6->{
                    p.setColor(0x30000000);canvas.drawRect(b,p);p.setColor(0x48ffffff);float inset=4*unit;RectF capsule=new RectF(b.left+inset,b.top+inset,b.left+inset+Math.min(b.height()-2*inset,b.width()-2*inset),b.bottom-inset);canvas.drawRoundRect(capsule,capsule.height()/2,capsule.height()/2,p);
                }
                case 7->{gradient(canvas,b,p,0x0cffffff,0x10000000,false);bevel(canvas,tile,b,p,1.5f*unit,0x65ffffff,0x35000000);}
                default->throw new IllegalArgumentException();
            }
            if(!permitted.getAsBoolean())return false;complete=true;return true;
        }finally{
            try{if(!complete)canvas.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR);}finally{canvas.restoreToCount(saved);}
        }
    }
    private static void gradient(Canvas c,RectF b,Paint p,int first,int last,boolean diagonal){p.setShader(new LinearGradient(b.left,b.top,diagonal?b.right:b.left,b.bottom,first,last,Shader.TileMode.CLAMP));c.drawRect(b,p);p.setShader(null);}
    private static void bevel(Canvas c,GradientDrawable tile,RectF b,Paint p,float width,int top,int bottom){p.setShader(new LinearGradient(0,b.top,0,b.bottom,top,bottom,Shader.TileMode.CLAMP));c.drawPath(ring(tile,b,0,width),p);p.setShader(null);}
    private static Path ring(GradientDrawable tile,RectF bounds,float start,float end){Path p=outline(tile,bounds,start);p.setFillType(Path.FillType.EVEN_ODD);p.addPath(outline(tile,bounds,end));return p;}
    private static Path outline(GradientDrawable tile,RectF bounds,float inset){RectF box=new RectF(bounds);box.inset(inset,inset);Path p=new Path();if(box.isEmpty())return p;if(tile.getShape()==GradientDrawable.OVAL)p.addOval(box,Path.Direction.CW);else{float[] original=tile.getCornerRadii();float[] radii=original==null?null:original.clone();if(radii==null){radii=new float[8];java.util.Arrays.fill(radii,tile.getCornerRadius());}for(int i=0;i<radii.length;i++)radii[i]=Math.max(0,radii[i]-inset);p.addRoundRect(box,radii,Path.Direction.CW);}return p;}
}
