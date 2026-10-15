package com.elitedarkkaiser.nubialy;
import android.content.*;
import android.os.*;
import java.util.UUID;
import java.util.function.Consumer;
/** Signature-protected diagnostic. It cannot enable a style; the service reads the guard itself. */
public final class FingerprintServiceClient {
    private final Context context;private final Handler main=new Handler(Looper.getMainLooper());private long generation;private Runnable timeout;private Consumer<String> callback;
    public FingerprintServiceClient(Context context){this.context=context;}
    public void check(Consumer<String> result){cancel();callback=result;long ticket=generation,started=SystemClock.elapsedRealtime();String nonce=UUID.randomUUID().toString();timeout=()->finish(ticket,"Fingerprint service did not reply. Enable nubia-ly for FingerprintSensorUi / com.fingerprint.sensorservice in Vector, then reboot.");main.postDelayed(timeout,SystemUiGuardProtocol.DEADLINE_MS);
        Intent intent=new Intent(GuardBrokerPolicy.FINGERPRINT_ACTION).setPackage(GuardBrokerPolicy.SENSOR_PACKAGE).addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);intent.putExtra("nonce",nonce);intent.putExtra("request_deadline_ms",started+SystemUiGuardProtocol.DEADLINE_MS);
        try{context.sendOrderedBroadcast(intent,null,new BroadcastReceiver(){@Override public void onReceive(Context ignored,Intent response){Bundle extras=getResultExtras(false);if(getResultCode()!=SystemUiGuardProtocol.RESPONSE||extras==null||!SystemUiGuardProtocol.accepts(nonce,extras.getString("nonce"),BuildConfig.VERSION_CODE,extras.getInt("version",-1),SystemClock.elapsedRealtime()-started)){finish(ticket,"No valid fingerprint service reply. Check Vector scope and reboot.");return;}String status=extras.getString("fingerprint_status","");finish(ticket,status==null||status.isBlank()||status.length()>800?"Fingerprint diagnostic rejected":status);}},main,0,null,null);}catch(RuntimeException unavailable){finish(ticket,"Fingerprint diagnostic unavailable. Check Vector scope and reboot.");}
    }
    private void finish(long ticket,String message){if(ticket!=generation||callback==null)return;Consumer<String> cb=callback;callback=null;if(timeout!=null)main.removeCallbacks(timeout);timeout=null;cb.accept(message);}
    public void cancel(){generation++;callback=null;if(timeout!=null)main.removeCallbacks(timeout);timeout=null;}
}
