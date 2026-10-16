package com.elitedarkkaiser.nubialy;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.Gravity;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(sdk=35) @GraphicsMode(GraphicsMode.Mode.NATIVE)
public final class BrightnessEffectsTest {
    @Test public void nativeClipStillDeterminesProgressAtEveryLevel()throws Throwable{
        for(int style=1;style<=3;style++)for(int level:new int[]{0,1,2500,5000,9999,10000}){
            final int selected=style;
            GradientDrawable fill=new GradientDrawable(){@Override public void draw(Canvas c){try{assertTrue(BrightnessEffects.draw(selected,c,this,1,()->true,()->super.draw(c)));}catch(Throwable failure){throw new AssertionError(failure);}}};fill.setColor(Color.GREEN);fill.setCornerRadius(16);fill.setBounds(0,0,160,40);
            ClipDrawable clip=new ClipDrawable(fill,Gravity.LEFT,ClipDrawable.HORIZONTAL);clip.setBounds(0,0,160,40);clip.setLevel(level);
            Bitmap bitmap=Bitmap.createBitmap(160,40,Bitmap.Config.ARGB_8888);Canvas canvas=new Canvas(bitmap);int count=canvas.getSaveCount();
            // Exercise the real platform ClipDrawable and its zero-level early return.
            clip.draw(canvas);
            assertEquals(count,canvas.getSaveCount());assertEquals(level,clip.getLevel());assertSame(fill,clip.getDrawable());assertEquals(new Rect(0,0,160,40),fill.getBounds());
            int end=level==0?0:160-(160*(10000-level)/10000);for(int x=end;x<160;x++)for(int y=0;y<40;y++)assertEquals(0,bitmap.getPixel(x,y));
            if(level>=5000)assertTrue(Color.alpha(bitmap.getPixel(40,20))>0);
            if(style==3)for(int y=0;y<16;y++)for(int x=0;x<160;x++)assertEquals(0,bitmap.getPixel(x,y));bitmap.recycle();
        }
    }
    @Test public void revocationAndFailureDoNotLeavePixelsOrCanvasChanges()throws Throwable{
        for(int style=1;style<=3;style++){
            GradientDrawable d=new GradientDrawable();d.setColor(Color.BLUE);d.setCornerRadius(20);d.setBounds(0,0,160,40);Bitmap image=Bitmap.createBitmap(160,40,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(image);int count=c.getSaveCount();AtomicBoolean allowed=new AtomicBoolean(true);
            assertFalse(BrightnessEffects.draw(style,c,d,1,allowed::get,()->{d.draw(c);allowed.set(false);}));assertEquals(0,image.getPixel(40,20));assertEquals(count,c.getSaveCount());
            try{BrightnessEffects.draw(style,c,d,1,()->true,()->{d.draw(c);throw new IllegalStateException();});fail();}catch(IllegalStateException expected){}assertEquals(0,image.getPixel(40,20));assertEquals(count,c.getSaveCount());image.recycle();
        }
    }
}
