package com.elitedarkkaiser.nubialy;
import android.graphics.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(sdk=35) @GraphicsMode(GraphicsMode.Mode.NATIVE)
public final class BatteryRendererTest {
 private static int[] pixels(Bitmap bitmap){int[] n=new int[bitmap.getWidth()*bitmap.getHeight()];bitmap.getPixels(n,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());return n;}
 @Test public void everyStyleDrawsInsideBoundsAndTracksLevel(){for(int style=1;style<BatteryStyles.NAMES.length;style++){Bitmap b=Bitmap.createBitmap(160,100,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(b);int save=c.getSaveCount();RectF bounds=new RectF(30,20,130,80);assertTrue(BatteryRenderer.draw(style,c,bounds,20,false,false,false,Color.WHITE,Color.GRAY,()->true));int[] low=pixels(b);assertEquals(save,c.getSaveCount());assertEquals(0,b.getPixel(0,0));assertEquals(0,b.getPixel(29,50));if(style==BatteryStyles.HIDDEN){assertTrue(Arrays.stream(low).allMatch(v->v==0));}else{assertTrue(BatteryStyles.NAMES[style],Arrays.stream(low).anyMatch(v->v!=0));c.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR);assertTrue(BatteryRenderer.draw(style,c,bounds,95,false,false,false,Color.WHITE,Color.GRAY,()->true));assertFalse(BatteryStyles.NAMES[style],Arrays.equals(low,pixels(b)));}b.recycle();}}
 @Test public void invalidOrRevokedPassLeavesOriginalPixelsAndCanvasUntouched(){Bitmap b=Bitmap.createBitmap(100,60,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(b);c.drawColor(Color.BLUE);int[] before=pixels(b);int saves=c.getSaveCount();assertFalse(BatteryRenderer.draw(1,c,new RectF(0,0,100,60),-1,false,false,true,Color.WHITE,Color.GRAY,()->true));assertArrayEquals(before,pixels(b));AtomicInteger calls=new AtomicInteger();assertFalse(BatteryRenderer.draw(22,c,new RectF(0,0,100,60),76,true,false,true,Color.WHITE,Color.GRAY,()->calls.incrementAndGet()==1));assertArrayEquals(before,pixels(b));assertEquals(saves,c.getSaveCount());b.recycle();}
 @Test public void migrationKeepsEveryEarlierDraftAtStock(){for(int length:new int[]{9,10,11,15,16,17,18,19,20,26}){String[] zero=new String[length];Arrays.fill(zero,"0");assertEquals(SurfaceStyle.STOCK,SurfaceStyle.decode(String.join(",",zero)));}var old=SurfaceStyle.STOCK.encode().split(",");old[26]="24";var chosen=SurfaceStyle.decode(String.join(",",old));assertNotNull(chosen);assertEquals(24,chosen.batteryStyle());assertEquals(chosen,SurfaceStyle.decode(chosen.encode()));old[26]="25";assertNull(SurfaceStyle.decode(String.join(",",old)));}
 @Test public void previewsHandleStockHiddenAndAllStateSamples(){var context=org.robolectric.RuntimeEnvironment.getApplication();for(int style=0;style<BatteryStyles.NAMES.length;style++){Bitmap b=Bitmap.createBitmap(240,100,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(b);BatteryStylePreview.paint(c,style,240,100);assertEquals(1,c.getSaveCount());b.recycle();}}
}
