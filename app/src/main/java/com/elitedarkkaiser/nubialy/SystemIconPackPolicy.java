package com.elitedarkkaiser.nubialy;

/** Exact inspected NX809J resource aliases. No substring matching of warnings or app icons. */
public final class SystemIconPackPolicy {
    private SystemIconPackPolicy(){}
    /** Semantic QS mapping: firmware resource names are not the identity of a tile. */
    public static String tileKey(String spec, boolean transientState, String nativeKey) {
        if(spec==null||spec.isEmpty())return nativeKey;
        if(transientState)return null;
        return switch(spec) {
            case "wifi","internet" -> nativeKey!=null&&nativeKey.startsWith("wifi_")?nativeKey:null;
            case "bt","bluetooth" -> "bluetooth_connected".equals(nativeKey)?nativeKey:"bluetooth";
            case "data","cell" -> "data";
            case "flashlight" -> "flashlight";
            case "hotspot" -> "hotspot";
            case "airplane" -> "airplane";
            case "location" -> "location";
            case "rotation" -> nativeKey; // Preserve locked portrait/landscape versus auto-rotate.
            case "sound" -> nativeKey; // Preserve the native three-way ringer mode.
            case "dnd","modes" -> "dnd";
            case "nfc" -> "nfc";
            case "screenrecord","videoscreen" -> "screenrecord";
            case "cast" -> "cast_connected".equals(nativeKey)?nativeKey:"cast";
            case "battery","saver","superpower" -> "battery_saver";
            case "saver_data","data_saver" -> "data_saver";
            case "night","night_display","readMode" -> "night";
            case "alarm" -> "alarm";
            case "controls" -> "controls";
            case "qr_code_scanner" -> "qr";
            case "nearby","custom(com.google.android.gms/.nearby.sharing.SharingTileService)" -> "share";
            case "gamespace" -> "game";
            case "lamp","lampMode" -> "lighting";
            case "coolingFan" -> "fan";
            case "liquidCooling" -> "liquid";
            case "sync" -> "sync";
            case "inversion","invert" -> "invert";
            case "dark","ui_mode_night" -> "dark";
            case "reduce_brightness","extra_dim" -> "extra_dim";
            case "screenshot" -> "screenshot";
            default -> null; // Unmapped vendor features and arbitrary third-party tiles stay native.
        };
    }
    public static String key(String name){
        if(name==null)return null;
        int wifi=ConnectivityResourcePolicy.level(true,name);
        if(wifi>=0)return "wifi_"+wifi;
        if(name.matches("ic_qs_wifi_(?:[4-7]_)?[0-4]"))return "wifi_"+name.charAt(name.length()-1);
        if(name.matches("ic_signal_cellular_[0-4]_4_bar"))return "signal_"+name.charAt(19);
        return switch(name){
            case "airplanemode_off","airplanemode_on","airplaneoff_on","airplaneoff_on_fw","airplaneon_off","airplane_offtoon","outer_airplanemode","sp_airplanemode","statusbar_airplanemode","qs_airplane_icon_on","qs_airplane_icon_off","intl_ic_qs_airplane","stat_sys_airplane_mode","ic_signal_airplane","ic_qs_airplane" -> "airplane";
            case "flashlight","flashlight_animation","flashlight_off","flashlight_offtoon","flashlight_ontooff","ic_flashlight_off","ic_flashlight_on","sp_flashlight","sp_flashlight_x","qs_flashlight_icon_on","qs_flashlight_icon_off","ic_eqs_flashlight_on","ic_eqs_flashlight_off","ic_eqs_flashlight_on_from_off_animation","ic_qs_flashlight_off","ic_signal_flashlight","stat_sys_flashlight","ic_qs_flashlight" -> "flashlight";
            case "bluetooth","qs_bluetooth_icon_on","qs_bluetooth_icon_off","intl_ic_qs_bluetooth","ic_qs_bluetooth_on","ic_qs_bluetooth","stat_sys_data_bluetooth" -> "bluetooth";
            case "bluetooth_connected","ic_bluetooth_connected","stat_sys_data_bluetooth_connected","ic_qs_bluetooth_connected" -> "bluetooth_connected";
            case "ic_qs_screen_rotation_portrait_to_auto_rotate","ic_qs_screen_rotation_portrait_to_auto_rotate_animation","qs_auto_rotate_icon_on","ic_qs_auto_rotate" -> "rotate";
            case "ic_qs_screen_rotation_portrait_from_auto_rotate","ic_qs_screen_rotation_portrait_from_auto_rotate_animation","stat_sys_rotate_portrait","stat_sys_rotate_landscape","statusbar_rotate_portrait","statusbar_rotate_landscape","qs_auto_rotate_icon_off" -> "portrait";
            case "ic_wifi_4_hotspot","ic_wifi_5_hotspot","ic_wifi_6_hotspot","qs_hotspot_icon_on","qs_hotspot_icon_off","ic_hotspot","stat_sys_hotspot" -> "hotspot";
            case "qs_location_icon_on","qs_location_icon_off","ic_location","ic_signal_location","stat_sys_location" -> "location";
            case "stat_sys_alarm","stat_sys_alarm_dim","ic_alarm","ic_alarm_dim","ic_qs_alarm" -> "alarm";
            case "notifications_off_lock","sp_sound_slient","stat_sys_ringer_silent","ic_qs_ringer_silent","ic_volume_ringer_mute" -> "silent";
            case "vibration_lock","vibration","notifications_vibration","sp_vibrate","sp_vibration","stat_sys_ringer_vibrate","ic_qs_ringer_vibrate","ic_volume_ringer_vibrate" -> "vibrate";
            case "notifications_full","sp_ringtone_notification","ic_qs_ringer_audible","ic_volume_ringer" -> "ring";
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
