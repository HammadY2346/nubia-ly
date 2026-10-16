package com.elitedarkkaiser.nubialy;
import android.content.Context;
import android.graphics.*;
import android.view.View;
/** App-only sample. It never invokes a power action. */
final class PowerStylePreview extends View {
 private final int style;PowerStylePreview(Context c,int style){super(c);this.style=style;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
 static void paint(Canvas c,int style,float w,float h,float density){Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);float size=Math.min(w*.30f,h*.70f),gap=size*.35f,left=(w-size*2-gap)/2,top=(h-size)/2;
  for(int i=0;i<2;i++){float x=left+i*(size+gap);RectF b=new RectF(x,top,x+size,top+size);p.setColor(0xff56515f);c.drawRoundRect(b,size/2,size/2,p);float[] corners=new float[8];java.util.Arrays.fill(corners,size/2);try{NotificationEffects.draw(style,c,b,corners,density,()->true,()->{});}catch(Throwable ignored){}p.setColor(0xffd3cbef);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(size*.045f);float cx=b.centerX(),cy=b.centerY();c.drawArc(cx-size*.19f,cy-size*.19f,cx+size*.19f,cy+size*.19f,-45,270,false,p);if(i==0)c.drawLine(cx,cy-size*.27f,cx,cy,p);else{c.drawLine(cx+size*.19f,cy-size*.19f,cx+size*.19f,cy,p);c.drawLine(cx+size*.19f,cy-size*.19f,cx,cy-size*.19f,p);}p.setStyle(Paint.Style.FILL);}
 }
 @Override protected void onDraw(Canvas c){super.onDraw(c);paint(c,style,getWidth(),getHeight(),getResources().getDisplayMetrics().density);}
}
