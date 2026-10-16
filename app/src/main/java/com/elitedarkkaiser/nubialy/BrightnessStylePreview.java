package com.elitedarkkaiser.nubialy;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
/** Illustrative static track and native-style clipped progress, with no display access. */
final class BrightnessStylePreview extends View {
    private final int style;
    BrightnessStylePreview(Context context,int style){super(context);this.style=style;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
    @Override protected void onDraw(Canvas c){super.onDraw(c);int w=getWidth(),h=getHeight(),inset=PersonalizationUi.dp(getContext(),4);if(w<12||h<12)return;
        for(int i=0;i<2;i++){GradientDrawable d=new GradientDrawable();d.setColor(i==0?0xff73707e:0xffd3cbef);d.setCornerRadius(h/3f);d.setBounds(inset,h/4,w-inset,h*3/4);int save=c.save();if(i==1)c.clipRect(inset,0,w*0.6f,h);
            try{if(!BrightnessEffects.draw(style,c,d,getResources().getDisplayMetrics().density,()->true,()->d.draw(c)))d.draw(c);}catch(Throwable unavailable){d.draw(c);}finally{c.restoreToCount(save);}
        }
    }
}
