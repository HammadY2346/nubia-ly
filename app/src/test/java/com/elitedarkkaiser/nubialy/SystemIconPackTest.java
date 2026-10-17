package com.elitedarkkaiser.nubialy;
import android.graphics.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(sdk=35) @GraphicsMode(GraphicsMode.Mode.NATIVE)
public final class SystemIconPackTest {
 @Test public void allPackPreviewsAndImportedAssetsInflateAndRender(){
  var resources=RuntimeEnvironment.getApplication().getResources();SystemIconPacks.resources(resources);
  String[] keys={"airplane","flashlight","bluetooth","bluetooth_connected","rotate","portrait","hotspot","location","alarm","silent","vibrate","ring","dnd","data_saver","nfc","cast","cast_connected","battery_saver","screenrecord","data","vpn","headset","headset_mic","night","wifi_0","wifi_1","wifi_2","wifi_3","wifi_4","signal_0","signal_1","signal_2","signal_3","signal_4","controls","qr","share","sync","invert","dark","extra_dim","screenshot","game","lighting","fan","liquid"};
  int count=0;for(int pack=1;pack<SystemIconPacks.NAMES.length;pack++){
   for(String key:new String[]{"wifi_4","signal_4","airplane","flashlight"})assertNotNull(SystemIconPacks.NAMES[pack]+" preview "+key,SystemIconPacks.artwork(pack,key));
   for(String key:keys){var d=SystemIconPacks.artwork(pack,key);if(d==null){assertTrue(pack==12&&(key.equals("headset")||key.equals("headset_mic")));continue;}count++;Bitmap b=Bitmap.createBitmap(48,48,Bitmap.Config.ARGB_8888);d.setBounds(0,0,48,48);d.setTint(Color.WHITE);d.draw(new Canvas(b));boolean visible=false;for(int x=0;x<48&&!visible;x++)for(int y=0;y<48;y++)if(Color.alpha(b.getPixel(x,y))!=0){visible=true;break;}assertTrue(SystemIconPacks.NAMES[pack]+" "+key,visible);b.recycle();}
  }assertEquals(734,count);assertNull(SystemIconPacks.artwork(0,"airplane"));assertNull(SystemIconPacks.artwork(17,"airplane"));
 }
 @Test public void onlyOrdinaryConnectedStrengthAndExactAliasesMatch(){assertEquals("wifi_3",SystemIconPackPolicy.key("signal_wifi6_3_bar"));assertEquals("wifi_2",SystemIconPackPolicy.key("ic_qs_wifi_7_2"));assertEquals("signal_4",SystemIconPackPolicy.key("ic_signal_cellular_4_4_bar"));for(String name:new String[]{"ic_no_internet_wifi_signal_3","signal_cellular_no_service","ic_signal_cellular_4_4_bar_exclamation","qs_bluetooth_icon_search","fake_flashlight","stat_sys_camera"})assertNull(name,SystemIconPackPolicy.key(name));}
}
