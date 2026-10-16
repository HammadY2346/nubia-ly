#!/system/bin/sh
MODDIR=${0%/*}
# One verified file bind before the player starts. Every failure leaves the stock target untouched.
sh "$MODDIR/bootctl.sh" mount >/dev/null 2>&1 || true
