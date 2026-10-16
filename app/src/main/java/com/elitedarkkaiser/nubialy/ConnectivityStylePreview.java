package com.elitedarkkaiser.nubialy;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.View;
/** Strength samples, never live network information. */
final class ConnectivityStylePreview extends View {
 private final boolean wifi;private final int style;
 ConnectivityStylePreview(Context context,boolean wifi,int style){super(context);this.wifi=wifi;this.style=style;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
 @Override protected void onDraw(Canvas c){super.onDraw(c);Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);int color=getContext().getColor(R.color.primary);p.setColor(color);float size=Math.min(getWidth()/4f,getHeight()*.65f),top=(getHeight()-size)/2;
  for(int i=0;i<3;i++){int level=new int[]{1,3,4}[i];float left=getWidth()*(i+1)/4f-size/2;Drawable artwork=ConnectivityStyles.artwork(wifi,style,level);if(artwork!=null){artwork.setTint(color);artwork.setBounds(Math.round(left),Math.round(top),Math.round(left+size),Math.round(top+size));artwork.draw(c);}else{int save=c.save();c.translate(left,top);c.scale(size/24,size/24);if(wifi){p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2);for(int n=1;n<=3;n++){p.setAlpha(n<=level?255:70);c.drawArc(12-n*7,23-n*7,12+n*7,23+n*7,225,90,false,p);}p.setStyle(Paint.Style.FILL);p.setAlpha(255);c.drawCircle(12,22,1.5f,p);}else for(int n=0;n<4;n++){p.setAlpha(n<level?255:70);c.drawRoundRect(2+n*5,19-n*4,5+n*5,23,1,1,p);}p.setAlpha(255);c.restoreToCount(save);}}
 }
}
