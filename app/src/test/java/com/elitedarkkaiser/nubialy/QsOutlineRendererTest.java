package com.elitedarkkaiser.nubialy;

import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import java.util.concurrent.atomic.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=35)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public final class QsOutlineRendererTest {
    private GradientDrawable tile(){var d=new GradientDrawable();d.setColor(Color.BLUE);d.setBounds(0,0,64,64);d.setCornerRadius(12);return d;}
    @Test public void borderReusesNativeTintAndFillIsHalfStrength()throws Throwable{
        for(int shape:new int[]{GradientDrawable.RECTANGLE,GradientDrawable.OVAL}){
            var tile=tile();tile.setShape(shape);tile.setTint(Color.GREEN);tile.setAlpha(180);Bitmap image=Bitmap.createBitmap(64,64,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(image);int saved=c.getSaveCount();AtomicInteger calls=new AtomicInteger();
            assertTrue(QsOutlineRenderer.draw(c,tile,1,()->true,()->{calls.incrementAndGet();tile.draw(c);}));
            assertEquals(2,calls.get());assertEquals(saved,c.getSaveCount());assertEquals(180,Color.alpha(image.getPixel(1,32)),2);assertEquals(90,Color.alpha(image.getPixel(32,32)),2);assertEquals(255,Color.green(image.getPixel(32,32)));assertEquals(180,tile.getAlpha());assertEquals(shape,tile.getShape());assertEquals(12f,tile.getCornerRadius(),0f);assertEquals(new Rect(0,0,64,64),tile.getBounds());image.recycle();
        }
    }
    @Test public void asymmetricCornersAndNativeStateColorsRemainValid()throws Throwable{
        var tile=tile();float[] corners={20,20,0,0,20,20,0,0};tile.setCornerRadii(corners);tile.setColor(new android.content.res.ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{}},new int[]{Color.RED,Color.BLUE}));
        for(boolean selected:new boolean[]{false,true}){tile.setState(selected?new int[]{android.R.attr.state_checked}:new int[]{});Bitmap image=Bitmap.createBitmap(64,64,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(image);assertTrue(QsOutlineRenderer.draw(c,tile,1,()->true,()->tile.draw(c)));int pixel=image.getPixel(32,32);assertEquals(selected?255:0,Color.red(pixel));assertEquals(selected?0:255,Color.blue(pixel));assertArrayEquals(corners,tile.getCornerRadii(),0f);image.recycle();}
    }
    @Test public void revokedUnsupportedAndFailedDrawRestoreCanvas()throws Throwable{
        var tile=tile();Canvas canvas=new Canvas(Bitmap.createBitmap(64,64,Bitmap.Config.ARGB_8888));int count=canvas.getSaveCount();AtomicInteger calls=new AtomicInteger();AtomicBoolean permit=new AtomicBoolean(true);
        assertFalse(QsOutlineRenderer.draw(canvas,tile,1,permit::get,()->{calls.incrementAndGet();permit.set(false);}));assertEquals(1,calls.get());assertEquals(count,canvas.getSaveCount());
        tile.setShape(GradientDrawable.LINE);assertFalse(QsOutlineRenderer.draw(canvas,tile,1,()->true,()->calls.incrementAndGet()));assertEquals(1,calls.get());tile.setShape(GradientDrawable.RECTANGLE);
        try{QsOutlineRenderer.draw(canvas,tile,1,()->true,()->{throw new IllegalStateException("draw failed");});fail();}catch(IllegalStateException expected){}assertEquals(count,canvas.getSaveCount());
    }
}
