package com.elitedarkkaiser.nubialy;
import java.util.regex.*;
/** Only inspected ordinary strength resources; unknown, no-internet, satellite and alert variants remain native. */
public final class ConnectivityResourcePolicy {
 private ConnectivityResourcePolicy(){}
 private static final Pattern WIFI=Pattern.compile("(?:(?:intl_)?ic_wifi_(?:[4-7]_)?signal_|statusbar_wifi(?:6|7)?(?:_(?:link|lock|link_lock|hs|mlo|link_mlo))?_)([0-4])");
 private static final Pattern WIFI_BAR=Pattern.compile("signal_wifi(?:6|7)?_([0-4])(?:_(?:link|lock|lock_link|hs|lock_hs))?_bar");
 private static final Pattern WIFI_NEW=Pattern.compile("ic_wifi_([0-3])");
 private static final Pattern MOBILE=Pattern.compile("(?:(?:sp_signal_cellular|statusbar_signal)_([0-5])|signal_cellular_([0-5])_bar_single)");
 public static int level(boolean wifi,String resource){if(resource==null)return -1;Matcher match;if(wifi){match=WIFI.matcher(resource);if(match.matches())return Integer.parseInt(match.group(1));match=WIFI_BAR.matcher(resource);if(match.matches())return Integer.parseInt(match.group(1));match=WIFI_NEW.matcher(resource);return match.matches()?Math.round(Integer.parseInt(match.group(1))*4f/3f):-1;}match=MOBILE.matcher(resource);if(!match.matches())return -1;String raw=match.group(1)==null?match.group(2):match.group(1);return Math.round(Integer.parseInt(raw)*4f/5f);}
}
