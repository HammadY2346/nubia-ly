package com.elitedarkkaiser.nubialy;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
/** Static selection-card art using the same native composition as the tile backend. */
final class QsStylePreview extends View {
    private final int style;
    QsStylePreview(Context context,int style){super(context);this.style=style;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
    @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);int w=getWidth(),h=getHeight();if(w<12||h<12)return;
        for(int i=0;i<2;i++){GradientDrawable tile=new GradientDrawable();tile.setColor(i==0?0xffd3cbef:0xff73707e);tile.setCornerRadius(h/5f);int gap=PersonalizationUi.dp(getContext(),6);tile.setBounds(gap,i*h/2+gap,w-gap,(i+1)*h/2-gap);
            if(style==0)tile.draw(canvas);else try{if(!QsTileEffects.draw(style,canvas,tile,getResources().getDisplayMetrics().density,()->true,()->tile.draw(canvas)))tile.draw(canvas);}catch(Throwable unavailable){tile.draw(canvas);}
        }
    }
}
