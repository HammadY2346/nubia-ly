package com.elitedarkkaiser.nubialy;
import org.junit.Test;
import static org.junit.Assert.*;
public final class SurfaceStyleTest {
    @Test public void boundedScopeAndLegacyDefaults(){
        assertEquals(SurfaceStyle.STOCK,SurfaceStyle.decode("0,0,0,0,0,0,0,0,0"));assertFalse(SurfaceStyle.STOCK.any());
        var chosen=new SurfaceStyle(15,StatusBarStyle.appearance(120,25),0,StatusBarStyle.appearance(80,100),0,StatusBarStyle.transform(-8,8,20),0,0,0,3);assertTrue(chosen.valid());assertEquals(chosen,SurfaceStyle.decode(chosen.encode()));
        for(String invalid:new String[]{"","0,0","-1,0,0,0,0,0,0,0,0","0,1,0,0,0,0,0,0,0","0,0,0,0,0,0,0,0,0,4","0,0,0,0,0,0,0,0,16777216","0,0,0,0,0,0,0,0,0,0,4","0,0,0,0,0,0,0,0,0,0,-1","0,0,0,0,0,0,0,0,0,0,0,0"})assertNull(SurfaceStyle.decode(invalid));
        assertEquals(SurfaceStyle.STOCK,SurfaceStyle.decode("0,0,0,0,0,0,0,0,0,0,0"));
        assertEquals(0,SurfaceStyle.decode("0,0,0,0,0,0,0,0,0,3").largeTileShape());
        for(int small=0;small<=3;small++)for(int large=0;large<=3;large++){
            var split=new SurfaceStyle(0,0,0,0,0,0,0,0,0,small,large);
            assertTrue(split.valid());assertEquals(split,SurfaceStyle.decode(split.encode()));
            assertEquals(small,split.tileShape());assertEquals(large,split.largeTileShape());
        }
        assertFalse(StatusBarStyle.STOCK.withSurfaces(null).valid());assertEquals(SurfaceStyle.STOCK,StatusBarStyle.STOCK.surfaces());
    }
}
