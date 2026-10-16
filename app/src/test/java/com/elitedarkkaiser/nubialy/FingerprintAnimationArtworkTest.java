package com.elitedarkkaiser.nubialy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class)
@Config(sdk=35)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public final class FingerprintAnimationArtworkTest {
    @Test public void everyAnimationAtlasDecodes(){
        FingerprintAnimationArtwork.resources(RuntimeEnvironment.getApplication().getResources());
        for(int id=1;id<FingerprintAnimations.NAMES.length;id++){
            var frames=FingerprintAnimationArtwork.load(id);
            assertEquals(id,frames.id());assertEquals(2048,frames.bitmap().getWidth());
            assertEquals(((FingerprintAnimations.DURATIONS[id].length+7)/8)*256,frames.bitmap().getHeight());
            assertTrue(frames.bitmap().getAllocationByteCount()<=16*1024*1024);
        }
        FingerprintAnimationArtwork.clear();
    }
    @Test public void atlasDecodesWithinBoundAndStockReleasesCache(){FingerprintAnimationArtwork.resources(RuntimeEnvironment.getApplication().getResources());assertNull(FingerprintAnimationArtwork.load(0));var frames=FingerprintAnimationArtwork.load(25);assertEquals(25,frames.id());assertEquals(2048,frames.bitmap().getWidth());assertEquals(2048,frames.bitmap().getHeight());assertTrue(frames.bitmap().getAllocationByteCount()<=16*1024*1024);assertSame(frames,FingerprintAnimationArtwork.load(25));var preview=new FingerprintAnimationArtwork.Preview(frames);preview.start();assertTrue(preview.isRunning());assertEquals(256,preview.getIntrinsicWidth());assertEquals(256,preview.getIntrinsicHeight());preview.stop();assertFalse(preview.isRunning());FingerprintAnimationArtwork.clear();assertFalse(frames.bitmap().isRecycled());}
}
