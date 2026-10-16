#!/system/bin/sh
MODDIR=${0%/*}
# Only ephemeral test receipts expire on boot; durable enabled identity is explicit user state.
sh "$MODDIR/guardctl.sh" resume

# CE preferences become available after first unlock. BOOT_COMPLETED reaches the app
# then; bounded nudges also wake a previously force-stopped app, without an Activity.
[ -f /data/adb/nubia_ly_guard/clock_id ] || exit 0
(
    attempts=0
    while [ "$attempts" -lt 60 ]; do
        [ ! -e "$MODDIR/disable" ] && [ ! -e "$MODDIR/remove" ] || exit 0
        [ -f /data/adb/nubia_ly_guard/clock_id ] || exit 0
        if [ "$(getprop sys.boot_completed)" = 1 ] && [ "$(am get-started-user-state 0 2>/dev/null)" = RUNNING_UNLOCKED ]; then
            sh "$MODDIR/guardctl.sh" resume_app
            exit 0
        fi
        attempts=$((attempts + 1))
        sleep 2
    done
) >/dev/null 2>&1 &
