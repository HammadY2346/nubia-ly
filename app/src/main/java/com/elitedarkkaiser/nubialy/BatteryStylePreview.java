package com.elitedarkkaiser.nubialy;
import android.content.Context;
import android.graphics.*;
import android.view.View;
/** Fixed sample state only. No battery receiver or polling in the app picker. */
final class BatteryStylePreview extends View {
 private final int style;BatteryStylePreview(Context context,int style){super(context);this.style=style;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
 static void paint(Canvas c,int style,float width,float height){for(int i=0;i<3;i++){float slot=width/3f,gap=slot*.12f;RectF b=new RectF(i*slot+gap,height*.17f,(i+1)*slot-gap,height*.82f);int level=new int[]{20,60,95}[i];int color=i==0?0xffed7979:0xffd3cbef;if(style==BatteryStyles.HIDDEN){Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);p.setColor(0xff98909f);p.setStrokeWidth(1.5f);p.setStyle(Paint.Style.STROKE);c.drawRoundRect(b,8,8,p);c.drawLine(b.left,b.bottom,b.right,b.top,p);}else BatteryRenderer.draw(style==0?7:style,c,b,level,i==2,false,true,color,0xffa29aaf,()->true);}}
 @Override protected void onDraw(Canvas c){super.onDraw(c);paint(c,style,getWidth(),getHeight());}
}
