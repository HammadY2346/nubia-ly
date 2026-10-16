package com.elitedarkkaiser.nubialy;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=35)
public final class FingerprintIconAdapterTest {
    private android.app.Activity activity;
    private Context context(){var controller=Robolectric.buildActivity(android.app.Activity.class);controller.get().setTheme(R.style.Theme_NubiaLy);controller.setup();activity=controller.get();return activity;}
    private void tap(View view,float x,float y){
        activity.setContentView(view);
        view.measure(View.MeasureSpec.makeMeasureSpec(180,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(180,View.MeasureSpec.EXACTLY));view.layout(0,0,180,180);
        long now=android.os.SystemClock.uptimeMillis();
        MotionEvent down=MotionEvent.obtain(now,now,MotionEvent.ACTION_DOWN,x,y,0),up=MotionEvent.obtain(now,now+30,MotionEvent.ACTION_UP,x,y,0);
        assertTrue(view.dispatchTouchEvent(down));assertTrue(view.dispatchTouchEvent(up));down.recycle();up.recycle();shadowOf(android.os.Looper.getMainLooper()).idle();
    }
    @Test public void tapOnArtworkSelectsItsIcon(){
        Context c=context();AtomicInteger choice=new AtomicInteger(-1);var adapter=new FingerprintIconAdapter(c,1,choice::set);
        View card=adapter.getView(5,null,new FrameLayout(c));
        tap(card,90,90);assertEquals(5,choice.get());
    }
    @Test public void tapOnCardBorderSelectsItsIcon(){
        Context c=context();AtomicInteger choice=new AtomicInteger(-1);var adapter=new FingerprintIconAdapter(c,1,choice::set);
        View card=adapter.getView(6,null,new FrameLayout(c));tap(card,20,90);assertEquals(6,choice.get());
    }
    @Test public void reusedCardSelectsReboundIconAndSupportsAccessibilityClick(){
        Context c=context();AtomicInteger choice=new AtomicInteger(-1);var adapter=new FingerprintIconAdapter(c,1,choice::set);FrameLayout parent=new FrameLayout(c);
        View original=adapter.getView(5,null,parent);View reused=adapter.getView(188,original,parent);assertSame(original,reused);
        tap(reused,90,90);assertEquals(188,choice.get());choice.set(-1);
        assertTrue(reused.performAccessibilityAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK,null));assertEquals(188,choice.get());
        assertTrue(reused.getContentDescription().toString().contains(FingerprintStyles.NAMES[188]));
    }
}
