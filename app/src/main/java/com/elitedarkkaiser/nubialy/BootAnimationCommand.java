package com.elitedarkkaiser.nubialy;
import android.content.Context;
import android.os.SystemClock;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
/** Root reads one fixed app staging file. No caller paths, scripts, mounts or automatic retries. */
final class BootAnimationCommand {
 private static final AtomicBoolean busy=new AtomicBoolean();
 static String run(Context context,String operation){
  if(operation==null)return "Unsupported operation.";
  BootAnimations.Entry entry=operation.startsWith("stage_")?BootAnimations.find(operation.substring(6)):null;
  if(!(operation.equals("status")||operation.equals("restore")||entry!=null))return "Unsupported operation.";
  if(!busy.compareAndSet(false,true))return "An animation operation is still finishing. Check status shortly.";
  Process process=null;boolean deferred=false;File file=new File(context.getFilesDir(),"boot-stage.zip");long deadline=SystemClock.elapsedRealtime()+15000;
  try{
   if(entry!=null){File temp=new File(context.getFilesDir(),"boot-stage.tmp");MessageDigest digest=MessageDigest.getInstance("SHA-256");long total=0;try(InputStream input=context.getAssets().open("bootanimations/"+entry.id()+".zip");FileOutputStream output=new FileOutputStream(temp)){byte[] buffer=new byte[65536];int n;while((n=input.read(buffer))!=-1){total+=n;if(total>entry.bytes()||SystemClock.elapsedRealtime()>=deadline)throw new IOException("Staging limit");digest.update(buffer,0,n);output.write(buffer,0,n);}output.getFD().sync();}if(total!=entry.bytes()||!hex(digest.digest()).equals(entry.sha256()))throw new IOException("Archive identity mismatch");if(!temp.renameTo(file))throw new IOException("Could not stage file");}
   if(SystemClock.elapsedRealtime()>=deadline)return "Staging expired. No apply was requested.";
   String args=operation+(entry==null?"":" "+(deadline/1000));
   process=new ProcessBuilder("su","-c","sh /data/adb/modules/nubia_ly_guard/bootctl.sh "+args).redirectErrorStream(true).start();process.getOutputStream().close();
   if(!process.waitFor(Math.max(1,deadline-SystemClock.elapsedRealtime()),TimeUnit.MILLISECONDS)){
    Process pending=process;process=null;deferred=true;
    new Thread(()->{try{pending.waitFor();}catch(InterruptedException interrupted){Thread.currentThread().interrupt();}finally{close(pending);file.delete();busy.set(false);}},"nubia-ly-boot-completion").start();
    return "Operation not confirmed in time. Check status before retrying.";
   }
   byte[] output=process.getInputStream().readNBytes(4097);if(output.length>4096)return "Module response exceeded its limit. Check status.";
   String text=new String(output,StandardCharsets.UTF_8).trim();if(process.exitValue()!=0)return "Not applied. "+(text.isEmpty()?"Install the updated recovery guard and grant root access.":text);
   return text.isEmpty()?"Stock selected for next boot.":text;
  }catch(Exception unavailable){if(process!=null&&process.isAlive()){Process pending=process;process=null;deferred=true;new Thread(()->{try{pending.waitFor();}catch(InterruptedException interrupted){Thread.currentThread().interrupt();}finally{close(pending);file.delete();busy.set(false);}},"nubia-ly-boot-completion").start();}return "Animation operation could not be confirmed. Check the module and its status.";}
  finally{if(process!=null)close(process);if(!deferred){file.delete();new File(context.getFilesDir(),"boot-stage.tmp").delete();busy.set(false);}}
 }
 private static String hex(byte[] bytes){StringBuilder text=new StringBuilder();for(byte value:bytes)text.append(Character.forDigit((value>>>4)&15,16)).append(Character.forDigit(value&15,16));return text.toString();}
 private static void close(Process p){try{p.getInputStream().close();p.getOutputStream().close();p.getErrorStream().close();}catch(IOException ignored){}}
}
