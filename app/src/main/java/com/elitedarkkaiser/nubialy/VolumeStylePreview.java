package com.elitedarkkaiser.nubialy;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
/** Static draft art, never touches audio. */
final class VolumeStylePreview extends View {
    private final int style;
    VolumeStylePreview(Context context,int style){super(context);this.style=style;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
    static void paint(Canvas c,int style,float w,float h,float density){
        for(int i=0;i<3;i++){final int index=i;float width=Math.min(w/5f,(h-8)/5f),height=width*5,x=w*(i+0.5f)/3f-width/2,top=(h-height)/2;RectF b=new RectF(x,top,x+width,top+height);float[] radii=new float[8];java.util.Arrays.fill(radii,width/2);GradientDrawable d=new GradientDrawable();d.setColor(0xff67626f);d.setCornerRadius(width/2);d.setBounds(Math.round(b.left),Math.round(b.top),Math.round(b.right),Math.round(b.bottom));
            QsOutlineRenderer.NativeDraw nativeDraw=()->{d.draw(c);int saved=c.save();c.clipRect(b.left,b.top+b.height()*(0.25f+iValue(index)),b.right,b.bottom);d.setColor(0xffd3cbef);d.draw(c);c.restoreToCount(saved);d.setColor(0xff67626f);};
            try{if(!VolumeEffects.draw(style,c,b,radii,density,()->true,nativeDraw))nativeDraw.draw();}catch(Throwable unavailable){d.draw(c);}
        }
    }
    private static float iValue(int i){return i*0.15f;}
    @Override protected void onDraw(Canvas c){super.onDraw(c);paint(c,style,getWidth(),getHeight(),getResources().getDisplayMetrics().density);}
}
