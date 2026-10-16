package com.elitedarkkaiser.nubialy;
import android.graphics.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.*;
import org.robolectric.RuntimeEnvironment;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(sdk=35) @GraphicsMode(GraphicsMode.Mode.NATIVE)
public final class PersonalizationControlsTest {
 @Test public void fullDraftRoundTripsAndOldDraftsDefaultToStock(){
  SurfaceStyle s=new SurfaceStyle(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,7,5,NotificationControls.encode(75,3),1,1);assertTrue(s.valid());assertEquals(s,SurfaceStyle.decode(s.encode()));String old="0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";assertEquals(SurfaceStyle.STOCK,SurfaceStyle.decode(old));for(int field=20;field<26;field++){String[] fields=s.encode().split(",");fields[field]="99999999";assertNull(SurfaceStyle.decode(String.join(",",fields)));}
 }
 @Test public void everyImportedStrengthAssetInflatesAndDraws(){ConnectivityStyles.resources(RuntimeEnvironment.getApplication().getResources());for(boolean wifi:new boolean[]{true,false})for(int style=1;style<ConnectivityStyles.NAMES.length;style++)for(int level=0;level<5;level++){var drawable=ConnectivityStyles.artwork(wifi,style,level);assertNotNull(drawable);Bitmap bitmap=Bitmap.createBitmap(48,48,Bitmap.Config.ARGB_8888);drawable.setBounds(0,0,48,48);drawable.setTint(Color.WHITE);drawable.draw(new Canvas(bitmap));bitmap.recycle();}}
 @Test public void powerPreviewBalancesCanvas(){Bitmap b=Bitmap.createBitmap(200,100,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(b);int saves=c.getSaveCount();for(int style=0;style<=5;style++){c.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR);PowerStylePreview.paint(c,style,200,100,1);assertEquals(saves,c.getSaveCount());}b.recycle();}
}
