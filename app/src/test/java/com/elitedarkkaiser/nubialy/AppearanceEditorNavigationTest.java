package com.elitedarkkaiser.nubialy;

import android.content.Intent;
import android.view.View;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowBuild;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=36)
public final class AppearanceEditorNavigationTest {
    @Test public void everySectionOpensItsDetailPagesAndBackKeepsOverview(){
        ShadowBuild.setModel("NX809J");
        String[][] routes={{"lock_screen","clock","date","fonts","fingerprint","widgets"},{"quick_settings","style","tiles","labels"},{"status_bar","typography","date","color","icons","inventory"}};
        for(String[] section:routes){
            var controller=Robolectric.buildActivity(ClockEditorActivity.class,new Intent().putExtra("section",section[0])).setup();var activity=controller.get();assertFalse(activity.isFinishing());EditorPages router=ReflectionHelpers.getField(activity,"pages");
            for(int i=1;i<section.length;i++){
                View row=activity.getWindow().getDecorView().findViewWithTag("editor-route:"+section[0]+":"+section[i]);assertNotNull(row);assertTrue(row.isShown());row.performClick();assertEquals(section[i],router.page());activity.getOnBackPressedDispatcher().onBackPressed();assertEquals("",router.page());assertFalse(activity.isFinishing());
            }
            controller.pause().stop().destroy();
        }
    }
    @Test public void outlineSelectionSavesOnlyADraft() throws Exception {
        ShadowBuild.setModel("NX809J");var controller=Robolectric.buildActivity(ClockEditorActivity.class,new Intent().putExtra("section","quick_settings")).setup();var activity=controller.get();
        var recovery=activity.getSharedPreferences("recovery",android.content.Context.MODE_PRIVATE);var before=recovery.getAll();
        activity.getWindow().getDecorView().findViewWithTag("editor-route:quick_settings:style").performClick();
        activity.getWindow().getDecorView().findViewWithTag("qs-style:1").performClick();
        java.util.concurrent.ExecutorService worker=ReflectionHelpers.getField(activity,"worker");worker.submit(()->{}).get(3,java.util.concurrent.TimeUnit.SECONDS);
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertEquals(1,StatusBarPreferences.read(activity).surfaces().qsStyle());assertEquals(before,recovery.getAll());
        activity.getOnBackPressedDispatcher().onBackPressed();EditorPages router=ReflectionHelpers.getField(activity,"pages");assertEquals("",router.page());controller.pause().stop().destroy();
    }
    @Test public void recreationRestoresTheFocusedPage(){
        ShadowBuild.setModel("NX809J");var controller=Robolectric.buildActivity(ClockEditorActivity.class,new Intent().putExtra("section","lock_screen")).setup();var activity=controller.get();activity.getWindow().getDecorView().findViewWithTag("editor-route:lock_screen:widgets").performClick();controller.recreate();EditorPages router=ReflectionHelpers.getField(controller.get(),"pages");assertEquals("lock_screen",router.section());assertEquals("widgets",router.page());controller.pause().stop().destroy();
    }
}
