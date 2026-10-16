package com.elitedarkkaiser.nubialy;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=35)
public final class ClockFontPickerActivityTest {
    @Test public void choosingFontOnlyReturnsDraftAfterApply(){
        var controller=Robolectric.buildActivity(ClockFontPickerActivity.class,new Intent().putExtra("selection",2)).setup();
        var activity=controller.get();
        var prefs=activity.getSharedPreferences("status_bar_editor",Activity.MODE_PRIVATE);var before=prefs.getAll();
        activity.getWindow().getDecorView().findViewWithTag("font-selector").performClick();
        android.widget.PopupWindow popup=ReflectionHelpers.getField(activity,"popup");ListView list=(ListView)popup.getContentView();
        int chosen=15;View row=list.getAdapter().getView(chosen,null,list);list.performItemClick(row,chosen,chosen);
        assertFalse(activity.isFinishing());assertEquals(before,prefs.getAll());
        controller.recreate();activity=controller.get();
        activity.getWindow().getDecorView().findViewWithTag("font-apply").performClick();
        assertEquals(Activity.RESULT_OK,shadowOf(activity).getResultCode());assertEquals(chosen,shadowOf(activity).getResultIntent().getIntExtra("selection",-1));assertEquals(before,prefs.getAll());controller.pause().stop().destroy();
    }
    @Test public void backDoesNotReturnASelection(){
        var controller=Robolectric.buildActivity(ClockFontPickerActivity.class,new Intent().putExtra("selection",6)).setup();var activity=controller.get();activity.finish();assertEquals(Activity.RESULT_CANCELED,shadowOf(activity).getResultCode());assertNull(shadowOf(activity).getResultIntent());controller.pause().stop().destroy();
    }
    @Test public void recreationPreservesSelectionAndInvalidInputUsesStock(){
        var controller=Robolectric.buildActivity(ClockFontPickerActivity.class,new Intent().putExtra("selection",999)).setup();controller.recreate();var activity=controller.get();activity.getWindow().getDecorView().findViewWithTag("font-apply").performClick();assertEquals(0,shadowOf(activity).getResultIntent().getIntExtra("selection",-1));controller.pause().stop().destroy();
    }
}
