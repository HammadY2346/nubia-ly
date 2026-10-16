package com.elitedarkkaiser.nubialy;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import java.util.*;
import java.util.concurrent.atomic.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(sdk=35) @GraphicsMode(GraphicsMode.Mode.NATIVE)
public final class VolumeEffectsTest {
    @Test public void sixLooksAreDistinctAndNativeDrawRunsOnce()throws Throwable{
        Set<Integer> signatures=new HashSet<>();float[] radii={18,18,18,18,18,18,18,18};
        for(int style=1;style<=6;style++){
            GradientDrawable nativeTrack=new GradientDrawable();nativeTrack.setColor(Color.BLUE);nativeTrack.setCornerRadii(radii);nativeTrack.setBounds(5,5,155,43);nativeTrack.setAlpha(173);
            Bitmap image=Bitmap.createBitmap(160,48,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(image);int saved=c.getSaveCount();AtomicInteger calls=new AtomicInteger();float[] before=radii.clone();
            assertTrue(VolumeEffects.draw(style,c,new RectF(5,5,155,43),radii,1,()->true,()->{calls.incrementAndGet();nativeTrack.draw(c);}));assertEquals(1,calls.get());assertEquals(saved,c.getSaveCount());assertArrayEquals(before,radii,0f);assertEquals(173,nativeTrack.getAlpha());assertEquals(new Rect(5,5,155,43),nativeTrack.getBounds());assertEquals(0,image.getPixel(0,0));
            if(style==1||style==4)assertTrue(Color.alpha(image.getPixel(80,24))<173);else assertEquals(173,Color.alpha(image.getPixel(80,24)),1);
            int[] pixels=new int[160*48];image.getPixels(pixels,0,160,0,0,160,48);signatures.add(Arrays.hashCode(pixels));image.recycle();
        }assertEquals(6,signatures.size());
    }
    @Test public void revocationAndFailureDiscardTemporaryLayer()throws Throwable{
        for(int style=1;style<=6;style++){Bitmap image=Bitmap.createBitmap(120,40,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(image);int save=c.getSaveCount();float[] corners=new float[8];AtomicBoolean allowed=new AtomicBoolean(true);
            assertFalse(VolumeEffects.draw(style,c,new RectF(0,0,120,40),corners,1,allowed::get,()->{c.drawColor(Color.BLUE);allowed.set(false);}));assertEquals(0,image.getPixel(50,20));assertEquals(save,c.getSaveCount());
            try{VolumeEffects.draw(style,c,new RectF(0,0,120,40),corners,1,()->true,()->{c.drawColor(Color.BLUE);throw new IllegalStateException();});fail();}catch(IllegalStateException expected){}assertEquals(0,image.getPixel(50,20));assertEquals(save,c.getSaveCount());image.recycle();}
    }
}
