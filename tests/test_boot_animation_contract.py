"""Pinned catalog/status schema and app rejection before root/staging; host Java only."""
from pathlib import Path
import subprocess,tempfile,json,zipfile
ROOT=Path(__file__).resolve().parents[1]
sources={
'android/os/SystemClock.java':'package android.os;public class SystemClock {public static long elapsedRealtime(){return System.nanoTime()/1000000;}}',
'android/content/Context.java':'package android.content;public class Context {public java.io.File folder=new java.io.File(System.getProperty("test.folder"));public java.io.File getFilesDir(){return folder;}public android.content.res.AssetManager getAssets(){return new android.content.res.AssetManager();}}',
'android/content/res/AssetManager.java':'package android.content.res;public class AssetManager {public java.io.InputStream open(String name){return new java.io.ByteArrayInputStream(new byte[]{1,2,3});}}',
'com/elitedarkkaiser/nubialy/BootContractTest.java':'''package com.elitedarkkaiser.nubialy;public class BootContractTest {static void check(boolean b){if(!b)throw new AssertionError();}public static void main(String[] args){check(BootAnimations.find("../rising")==null);check(BootAnimations.find("rising;id")==null);check(BootAnimations.find(null)==null);for(var e:BootAnimations.ENTRIES){check(e.bytes()>0&&e.bytes()<40000000&&e.sha256().matches("[a-f0-9]{64}"));String report="nubia-ly-boot-v1\\nprofile=ready\\nselection="+e.id()+"\\nmounted=stock\\nresult=none";var state=BootAnimationState.parse(report);check(state!=null&&state.ready()&&state.selection().equals(e.id()));check(BootAnimationState.parse(report+"\\nextra")==null);check(BootAnimationState.parse(report.replace(e.id(),"../evil"))==null);}check(BootAnimationState.parse("nubia-ly-boot-v0\\nprofile=ready\\nselection=stock\\nmounted=stock\\nresult=none")==null);check(BootAnimationState.parse(null)==null);var context=new android.content.Context();check(BootAnimationCommand.run(context,null).equals("Unsupported operation."));check(BootAnimationCommand.run(context,"mount").equals("Unsupported operation."));check(BootAnimationCommand.run(context,"stage_rising;id").equals("Unsupported operation."));check(BootAnimationCommand.run(context,"stage_rising").contains("could not be confirmed"));check(context.folder.list().length==0);System.out.println("Boot app contract passed: fixed catalog IDs, strict versioned state, no shell/path input and corrupt assets rejected before root");}}'''
}
with tempfile.TemporaryDirectory(prefix='nubialy-boot-app-') as work:
 w=Path(work)
 for name,text in sources.items():
  p=w/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(text)
 files=[str(p) for p in w.rglob('*.java')]+[str(ROOT/'app/src/main/java/com/elitedarkkaiser/nubialy'/n) for n in ['BootAnimations.java','BootAnimationState.java','BootAnimationCommand.java']]
 subprocess.run(['java','com.sun.tools.javac.Main','-d',str(w/'classes'),*files],check=True)
 folder=w/'files';folder.mkdir();subprocess.run(['java','-Dtest.folder='+str(folder),'-cp',str(w/'classes'),'com.elitedarkkaiser.nubialy.BootContractTest'],check=True)
# Packaging must include recovery hooks and retain automatic-mount opt-out.
subprocess.run(['python3',str(ROOT/'scripts/package-guard.py')],check=True,stdout=subprocess.DEVNULL)
with zipfile.ZipFile(ROOT/'build/nubia-ly-guard-test.zip') as z:
 for name in ['post-fs-data.sh','bootctl.sh','skip_mount','action.sh','uninstall.sh','service.sh']:assert name in z.namelist()
 assert not any(n.startswith('system/') for n in z.namelist())
print('Guard package contains scoped loader hooks, keeps skip_mount, and ships no system replacement tree')
