package com.elitedarkkaiser.nubialy;

/** Exact inspected NX809J resource aliases. No substring matching of warnings or app icons. */
public final class SystemIconPackPolicy {
    private SystemIconPackPolicy(){}
    public static String key(String name){
        if(name==null)return null;
        int wifi=ConnectivityResourcePolicy.level(true,name);
        if(wifi>=0)return "wifi_"+wifi;
        if(name.matches("ic_qs_wifi_[0-4]"))return "wifi_"+name.charAt(name.length()-1);
        if(name.matches("ic_signal_cellular_[0-4]_4_bar"))return "signal_"+name.charAt(19);
        return switch(name){
            case "qs_airplane_icon_on","qs_airplane_icon_off","intl_ic_qs_airplane","stat_sys_airplane_mode","ic_signal_airplane","ic_qs_airplane" -> "airplane";
            case "qs_flashlight_icon_on","qs_flashlight_icon_off","ic_eqs_flashlight_on","ic_eqs_flashlight_off","ic_eqs_flashlight_on_from_off_animation","ic_qs_flashlight_off","ic_signal_flashlight","stat_sys_flashlight","ic_qs_flashlight" -> "flashlight";
            case "qs_bluetooth_icon_on","qs_bluetooth_icon_off","intl_ic_qs_bluetooth","ic_qs_bluetooth_on","ic_qs_bluetooth","stat_sys_data_bluetooth" -> "bluetooth";
            case "ic_bluetooth_connected","stat_sys_data_bluetooth_connected","ic_qs_bluetooth_connected" -> "bluetooth_connected";
            case "qs_auto_rotate_icon_on","ic_qs_auto_rotate" -> "rotate";
            case "qs_auto_rotate_icon_off" -> "portrait";
            case "qs_hotspot_icon_on","qs_hotspot_icon_off","ic_hotspot","stat_sys_hotspot" -> "hotspot";
            case "qs_location_icon_on","qs_location_icon_off","ic_location","ic_signal_location","stat_sys_location" -> "location";
            case "stat_sys_alarm","stat_sys_alarm_dim","ic_alarm","ic_alarm_dim","ic_qs_alarm" -> "alarm";
            case "stat_sys_ringer_silent","ic_qs_ringer_silent","ic_volume_ringer_mute" -> "silent";
            case "stat_sys_ringer_vibrate","ic_qs_ringer_vibrate","ic_volume_ringer_vibrate" -> "vibrate";
            case "ic_qs_ringer_audible","ic_volume_ringer" -> "ring";
            case "qs_dnd_icon_on","qs_dnd_icon_off","stat_sys_dnd","stat_sys_dnd_vec","ic_no_disturb","ic_qs_dnd","ic_qs_dnd_on" -> "dnd";
            case "qs_data_saver_icon_on","qs_data_saver_icon_off","ic_data_saver","stat_sys_data_saver","stat_sys_data_saver_vec" -> "data_saver";
            case "stat_sys_nfc","ic_qs_nfc" -> "nfc";
            case "ic_cast","ic_qs_cast_off" -> "cast";
            case "ic_cast_connected","ic_qs_cast_on","stat_sys_cast" -> "cast_connected";
            case "qs_battery_saver_icon_on","qs_battery_saver_icon_off","ic_qs_battery_saver","ic_power_saver" -> "battery_saver";
            case "qs_screen_record_icon_on","qs_screen_record_icon_off","ic_screenrecord","ic_screen_recorder" -> "screenrecord";
            case "ic_swap_vert","ic_qs_cellular" -> "data";
            case "stat_sys_vpn_ic","stat_sys_branded_vpn" -> "vpn";
            case "ic_headset","stat_sys_headset" -> "headset";
            case "ic_headset_mic","stat_sys_headset_mic" -> "headset_mic";
            case "qs_nightlight_icon_on","qs_nightlight_icon_off","ic_qs_night_display_on","ic_qs_ui_mode_night" -> "night";
            default -> null;
        };
    }
}
