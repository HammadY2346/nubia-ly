package com.elitedarkkaiser.nubialy;
import org.junit.Test;
import static org.junit.Assert.*;
public final class ConnectivityResourcePolicyTest {
 @Test public void mapsBothSimFamiliesToMatchingFiveArtworkSteps(){for(int level=0;level<=5;level++){int expected=Math.round(level*4f/5f);assertEquals(expected,ConnectivityResourcePolicy.level(false,"statusbar_signal_"+level));assertEquals(expected,ConnectivityResourcePolicy.level(false,"signal_cellular_"+level+"_bar_single"));}}
 @Test public void mapsVendorWifiFamiliesAndAlternateFourLevelPath(){for(int n=0;n<=4;n++)for(String prefix:new String[]{"statusbar_wifi_","statusbar_wifi6_","statusbar_wifi6_link_","statusbar_wifi7_mlo_","intl_ic_wifi_6_signal_"})assertEquals(n,ConnectivityResourcePolicy.level(true,prefix+n));assertEquals(4,ConnectivityResourcePolicy.level(true,"ic_wifi_3"));}
 @Test public void neverTreatsWarningSatelliteOrUnknownResourcesAsOrdinaryStrength(){for(String name:new String[]{"statusbar_wifi6_no_3","statusbar_wifi7_nointernet_3","ic_no_internet_wifi_signal_4","signal_wifi6_3_no_bar","wifi_tethering_4","statusbar_signal_nointernet_5","sp_signal_cellular_alert_4","statusbar_satellite_signal_3","signal_cellular_6_bar_single","statusbar_signal_4_extra","statusbar_wifi6_5"}){assertEquals(-1,ConnectivityResourcePolicy.level(true,name));assertEquals(-1,ConnectivityResourcePolicy.level(false,name));}}
}
