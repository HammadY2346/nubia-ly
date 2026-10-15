package com.elitedarkkaiser.nubialy.xposed;
import android.graphics.*;
import android.graphics.drawable.Drawable;
/** Small code-native glyphs; no biometric animation, sensor or brightness changes. */
public final class FingerprintGlyphDrawable extends Drawable {
    public static final String[] NAMES={"Stock","Rings","Scan frame","Hexagon","Diamond"};
    private final int style;private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    public FingerprintGlyphDrawable(int style,int color){this.style=style;paint.setColor(color);paint.setStyle(Paint.Style.STROKE);paint.setStrokeCap(Paint.Cap.ROUND);paint.setStrokeJoin(Paint.Join.ROUND);}
    @Override public void draw(Canvas canvas){Rect b=getBounds();float size=Math.min(b.width(),b.height()),cx=b.exactCenterX(),cy=b.exactCenterY(),r=size*0.34f;paint.setStrokeWidth(Math.max(1,size*0.025f));
        if(style==1){for(int i=0;i<3;i++)canvas.drawCircle(cx,cy,r*(1-i*0.28f),paint);}
        else if(style==2){float d=r*0.45f;for(int x:new int[]{-1,1})for(int y:new int[]{-1,1}){canvas.drawLine(cx+x*r,cy+y*r,cx+x*(r-d),cy+y*r,paint);canvas.drawLine(cx+x*r,cy+y*r,cx+x*r,cy+y*(r-d),paint);}canvas.drawCircle(cx,cy,r*0.4f,paint);}
        else {Path path=new Path();int sides=style==3?6:4;for(int i=0;i<sides;i++){double a=-Math.PI/2+2*Math.PI*i/sides;float x=cx+(float)Math.cos(a)*r,y=cy+(float)Math.sin(a)*r;if(i==0)path.moveTo(x,y);else path.lineTo(x,y);}path.close();canvas.drawPath(path,paint);canvas.drawCircle(cx,cy,r*0.34f,paint);}
    }
    @Override public void setAlpha(int alpha){paint.setAlpha(alpha);invalidateSelf();}
    @Override public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);invalidateSelf();}
    @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
