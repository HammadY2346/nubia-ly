package com.elitedarkkaiser.nubialy;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
/** Four sample glyphs; never live radio state. */
final class SystemIconPackPreview extends View {
    private final Drawable[] icons=new Drawable[4];
    SystemIconPackPreview(Context context,int pack){super(context);setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);String[] keys={"wifi_4","signal_4","airplane","flashlight"};for(int i=0;i<4;i++){icons[i]=SystemIconPacks.artwork(pack==0?4:pack,keys[i]);if(icons[i]!=null)icons[i].setTint(context.getColor(R.color.primary));}}
    @Override protected void onDraw(Canvas c){super.onDraw(c);float side=Math.min(getWidth()/2.8f,getHeight()/2.3f);for(int i=0;i<4;i++){Drawable d=icons[i];if(d==null)continue;float x=getWidth()*(i%2==0?.28f:.72f)-side/2,y=getHeight()*(i<2?.25f:.75f)-side/2;d.setBounds(Math.round(x),Math.round(y),Math.round(x+side),Math.round(y+side));d.draw(c);}}
}
