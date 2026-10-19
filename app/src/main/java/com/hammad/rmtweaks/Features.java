package com.hammad.rmtweaks;

import java.util.ArrayList;
import java.util.List;

/** Single source of truth for every tweak: used by the settings UI and read by the hooks. */
public final class Features {
    private Features() {}

    public static final String PREFS = "tweaks";
    public static final String PKG = "com.hammad.rmtweaks";

    public enum Type { SWITCH, CHOICE, ACTION }

    public static final class Feature {
        public final String key, title, desc;
        public final Type type;
        public final String[] choices;   // CHOICE only: labels; stored value = index
        public final int def;            // CHOICE default index; SWITCH: 1 = on by default
        Feature(String key, String title, String desc) { this(key, title, desc, Type.SWITCH, null, 0); }
        Feature(String key, String title, String desc, Type type, String[] choices, int def) {
            this.key = key; this.title = title; this.desc = desc; this.type = type; this.choices = choices; this.def = def;
        }
    }

    public static final class Section {
        public final String id, title, subtitle;
        public final int icon;
        public final String[] packages;
        public final Feature[] features;
        Section(String id, String title, String subtitle, int icon, String[] packages, Feature... features) {
            this.id = id; this.title = title; this.subtitle = subtitle; this.icon = icon; this.packages = packages; this.features = features;
        }
    }

    // ---- keys (kept as constants so hooks never typo them) ----
    public static final String GAME_UNFREEZE = "game_unfreeze";
    public static final String GAME_MACRO_ANY = "game_macro_any";
    public static final String GAME_COMBO_SPEED = "game_combo_speed";
    public static final String GAME_TGK_ALL = "game_tgk_link_all";
    public static final String GAME_SR_COEXIST = "game_sr_coexist";
    public static final String GAME_NO_DIALOGS = "game_no_dialogs";
    public static final String GAME_KEEP_RMTIME = "game_keep_redmagic_time";
    public static final String GAME_AI_SPEED = "game_ai_speed";
    public static final String GAME_RECORD_FREE = "game_record_free";

    public static final String LAUNCHER_PAGED = "launcher_paged_drawer";
    public static final String LAUNCHER_PAGED_ROWS = "launcher_paged_rows";
    public static final String LAUNCHER_ROTATION = "launcher_rotation";
    public static final String LAUNCHER_COLS = "launcher_cols";
    public static final String LAUNCHER_ROWS = "launcher_rows";
    public static final String LAUNCHER_HOTSEAT = "launcher_hotseat";
    public static final String LAUNCHER_HOTSEAT_TEXT = "launcher_hotseat_text";
    public static final String LAUNCHER_TWO_LINE = "launcher_two_line";
    public static final String LAUNCHER_CIRCLE = "launcher_circle_scroll";
    public static final String LAUNCHER_NO_FASTSCROLL = "launcher_hide_fastscroller";

    public static final String THEME_VARIANT = "theme_variant";
    public static final String FP_UNLOCK_ALL = "fp_unlock_all";
    public static final String FP_CHISA = "fp_chisa";
    public static final String FP_FIGHTING = "fp_fighting";
    public static final String FP_WW = "fp_wuthering_waves";
    public static final String FP_LTY = "fp_lty";
    public static final String FP_SAGA = "fp_golden_saga";

    public static final String CHARGE_SET = "charge_set";
    public static final String CHARGE_IMPORT = "charge_import";
    public static final String CHARGE_EVERY_LOCK = "charge_every_lock";
    public static final String[] CHARGE_SETS = {"Stock", "Black Myth: Wukong collab (3-stage)", "RedMagic wired frames", "RedMagic wireless frames", "Classic Mifavor (layered)", "Custom frames (imported below)"};

    /** Theme variants: {label, theme-resource-feature, variant-id}. Index 0 = off. */
    public static final String[][] THEME_VARIANTS = {
        {"Off (stock)", "", ""},
        {"Phantom · Mora Pink (10S Pro)", "nx789j_ip_mp_cn", "IP_MP_CN"},
        {"CAMELLYA · Wuthering Waves (10S Pro)", "nx789s_ip_mc_cn", "IP_MC_CN"},
        {"Black Myth: Wukong (10S Pro)", "nx789s_w_ip_mk_cn", "IP_MK_CN"},
        {"GOLDEN SAGA (10S Pro)", "nx789s_w_ip_pb_cn", "IP_PB_CN"},
        {"Chisa · Wuthering Waves (11 Pro)", "nx809j_w_ip_mc_cn", "IP_MC_W_CN"},
        {"GOLDEN SAGA (11 Pro)", "nx809j_w_saga", "IP_PB_CN"},
        {"Bumblebee (9S Pro)", "nx769j_ip_tf_cn", "IP_TF_CN"},
        {"Bumblebee (8S Pro)", "nx729j_ip_tf_cn", "IP_TF_CN"},
    };
    public static final String[] AI_SPEED = {"Stock", "2× faster", "3× faster", "5× faster", "10× faster"};
    public static final int[] AI_SPEED_DIV = {1, 2, 3, 5, 10};
    public static final String[] GRID_COLS = {"Stock", "4", "5", "6"};
    public static final String[] GRID_ROWS = {"Stock", "5", "6", "7", "8"};
    public static final String[] HOTSEAT = {"Stock", "4", "5", "6", "7"};
    public static final String[] PAGED_ROWS = {"Auto", "4", "5", "6", "7", "8"};

    private static String[] labels(String[][] v) { String[] l = new String[v.length]; for (int i = 0; i < v.length; i++) l[i] = v[i][0]; return l; }

    public static final Section GAME = new Section("game", "Game Space",
        "Unlocks for Game Assist, macros, triggers and performance modes",
        R.drawable.ic_game,
        new String[]{"cn.nubia.gameassist", "cn.nubia.gamelauncher", "cn.nubia.gamehelpmodule", "cn.nubia.gamelab", "cn.nubia.gamehighlights"},
        new Feature(GAME_UNFREEZE, "Game Assist in every app", "Shoulder keys, plugins and the in-game panel work in any app, not just whitelisted games. Restart Game Assist after changing."),
        new Feature(GAME_MACRO_ANY, "Macros and combos in any game", "Removes the macro / one-key combo blacklist."),
        new Feature(GAME_COMBO_SPEED, "Combo playback speed control", "Enables the hidden playback-speed option for recorded combos."),
        new Feature(GAME_TGK_ALL, "Trigger-key linkage everywhere", "Shoulder-trigger key mapping is never disabled per game."),
        new Feature(GAME_SR_COEXIST, "Super Resolution with Diablo mode", "Lets Super Resolution stay on while Diablo (extreme) performance mode is active, and stops auto-disable."),
        new Feature(GAME_NO_DIALOGS, "Skip Diablo confirmation dialogs", "Toggling Diablo mode applies immediately without the warning pop-up."),
        new Feature(GAME_KEEP_RMTIME, "Keep RedMagic Time enabled", "Game Space never turns RedMagic Time off by itself."),
        new Feature(GAME_AI_SPEED, "AI trigger reaction speed", "Shortens template-scan, click and cooldown delays of Game Lab AI triggers.", Type.CHOICE, AI_SPEED, 0),
        new Feature(GAME_RECORD_FREE, "Record Red Magic Moments anywhere", "Removes the abnormal-state abort so recording works in any mode.")
    );

    public static final Section LAUNCHER = new Section("launcher", "Launcher",
        "Stock launcher drawer, grid and behaviour",
        R.drawable.ic_launcher_grid,
        new String[]{"com.zte.mifavor.launcher"},
        new Feature(LAUNCHER_PAGED, "Horizontal paged app drawer", "The app drawer scrolls sideways page by page instead of a vertical list. Swipe down anywhere to close."),
        new Feature(LAUNCHER_PAGED_ROWS, "Rows per drawer page", "Only used with the paged drawer.", Type.CHOICE, PAGED_ROWS, 0),
        new Feature(LAUNCHER_NO_FASTSCROLL, "Hide A–Z fast scroller", "Hides the alphabet scroller in the drawer (recommended with the paged drawer)."),
        new Feature(LAUNCHER_ROTATION, "Allow home screen rotation", "Home screen and drawer follow the phone orientation."),
        new Feature(LAUNCHER_COLS, "Home grid columns", "", Type.CHOICE, GRID_COLS, 0),
        new Feature(LAUNCHER_ROWS, "Home grid rows", "", Type.CHOICE, GRID_ROWS, 0),
        new Feature(LAUNCHER_HOTSEAT, "Dock icons", "", Type.CHOICE, HOTSEAT, 0),
        new Feature(LAUNCHER_HOTSEAT_TEXT, "Show labels in dock", ""),
        new Feature(LAUNCHER_TWO_LINE, "Two-line app labels", ""),
        new Feature(LAUNCHER_CIRCLE, "Infinite home screen scrolling", "Last page wraps around to the first.")
    );

    public static final Section THEME = new Section("theme", "Themes & fingerprint",
        "Collaboration themes and fingerprint animations",
        R.drawable.ic_theme,
        new String[]{"com.zte.beautify", "com.zte.fingerprints", "com.fingerprint.sensorservice"},
        new Feature(THEME_VARIANT, "Collaboration theme pack", "Makes the Theme Store believe the phone is the chosen collaboration edition so its preset theme, AOD and wallpapers can be applied.", Type.CHOICE, labels(THEME_VARIANTS), 0),
        new Feature(FP_UNLOCK_ALL, "Unlock all fingerprint animations", "Adds every animation shipped in the firmware to the picker (Chisa, Luo Tianyi, Fighting Buddha, Wuthering Waves, Force Matrix…)."),
        new Feature(FP_CHISA, "Chisa edition", "Unlocks the Chisa collaboration fingerprint style and its rendering."),
        new Feature(FP_FIGHTING, "Fighting Buddha edition", "Unlocks the Black Myth: Wukong fingerprint style."),
        new Feature(FP_WW, "Wuthering Waves edition", "Unlocks the Wuthering Waves style (replaces Force Matrix with the collab version)."),
        new Feature(FP_LTY, "Luo Tianyi edition", "Unlocks the Luo Tianyi fingerprint style."),
        new Feature(FP_SAGA, "GOLDEN SAGA edition", "Unlocks the GOLDEN SAGA reskins.")
    );

    public static final Section SYSTEMUI = new Section("systemui", "System UI",
        "Charging animation and lock screen",
        R.drawable.ic_bolt,
        new String[]{"com.android.systemui"},
        new Feature(CHARGE_SET, "Charging animation", "Which frame set System UI plays when the charger is connected. Custom frames come from the folder you import below.", Type.CHOICE, CHARGE_SETS, 0),
        new Feature(CHARGE_IMPORT, "Import custom charging frames…", "Pick a folder of PNG/WEBP/JPG frames (sorted by file name). They are copied into RM Tweaks and served to System UI.", Type.ACTION, null, 0),
        new Feature(CHARGE_EVERY_LOCK, "Play charging animation on every screen-on", "Shows the animation each time the lock screen appears while charging, not only when the charger is plugged in.")
    );

    public static final Section[] SECTIONS = {GAME, LAUNCHER, THEME, SYSTEMUI};

    public static List<Feature> all() {
        List<Feature> l = new ArrayList<>();
        for (Section s : SECTIONS) for (Feature f : s.features) l.add(f);
        return l;
    }

    public static Section section(String id) {
        for (Section s : SECTIONS) if (s.id.equals(id)) return s;
        return null;
    }
}
