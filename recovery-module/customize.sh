[ "$(getprop ro.product.model)" = NX809J ] || abort "NX809J only"
[ "$(getprop ro.build.version.sdk)" = 36 ] || abort "Android 16 only"
set_perm_recursive "$MODPATH" 0 0 0755 0644
for script in guardctl.sh bootctl.sh post-fs-data.sh action.sh service.sh uninstall.sh; do
    set_perm "$MODPATH/$script" 0 0 0755
done
ui_print "Independent recovery guard with saved-feature resume after first unlock. Installation enables no new features."
ui_print "Action revokes all nubia-ly permits. Only explicitly saved enabled clock authority resumes after reboot."

ui_print "Optional boot animations use one verified systemless file bind. Stock files remain untouched."
ui_print "Action disarms boot animation for the next boot; any current bind ends on reboot."
