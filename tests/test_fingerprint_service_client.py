"""Production fingerprint diagnostic target, nonce/version checks and cancellation."""
from pathlib import Path
import ast,tempfile,subprocess
root=Path(__file__).resolve().parents[1]
module=ast.parse((root/'tests/test_inventory_client.py').read_text());sources=ast.literal_eval(next(n.value for n in module.body if isinstance(n,ast.Assign) and any(isinstance(t,ast.Name) and t.id=='sources' for t in n.targets)))
sources.pop('com/elitedarkkaiser/nubialy/InventoryTest.java')
sources['android/content/Intent.java']=sources['android/content/Intent.java'].replace('public Intent setPackage(String p){return this;}','public String pkg;public Intent setPackage(String p){pkg=p;return this;}')
sources['com/elitedarkkaiser/nubialy/FingerprintClientTest.java']='''package com.elitedarkkaiser.nubialy;import android.os.*;import android.content.*;public class FingerprintClientTest {static String result;static int replies;static void check(boolean b){if(!b)throw new AssertionError(result);}static Bundle response(Context c){var b=new Bundle();b.putString("nonce",(String)c.intent.extras.get("nonce"));b.putInt("version",19);b.putString("fingerprint_status","Fingerprint guard: readable");return b;}public static void main(String[] a){var c=new Context();var client=new FingerprintServiceClient(c);java.util.function.Consumer<String> cb=s->{result=s;replies++;};client.check(cb);check(c.intent.pkg.equals("com.fingerprint.sensorservice"));c.receiver.deliver(c,1201,response(c));check(result.contains("readable"));client.check(cb);var b=response(c);b.putString("nonce","bad");c.receiver.deliver(c,1201,b);check(!result.contains("readable"));client.check(cb);b=response(c);b.putInt("version",18);c.receiver.deliver(c,1201,b);check(!result.contains("readable"));client.check(cb);b=response(c);SystemClock.now+=9000;c.receiver.deliver(c,1201,b);check(!result.contains("readable"));client.check(cb);var late=c.receiver;b=response(c);client.cancel();int n=replies;late.deliver(c,1201,b);check(replies==n);client.check(cb);b=response(c);b.putString("fingerprint_status","x".repeat(801));c.receiver.deliver(c,1201,b);check(result.contains("rejected"));System.out.println("Fingerprint client passed: exact target, validated response, latency and cancellation fences");}}'''
with tempfile.TemporaryDirectory() as temp:
    temp=Path(temp)
    for name,content in sources.items():
        p=temp/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(content)
    names=['FingerprintServiceClient','GuardBrokerPolicy','SystemUiGuardProtocol','PreviewPolicy']
    subprocess.run(['java','com.sun.tools.javac.Main','-d',str(temp/'classes'),*[str(p) for p in temp.rglob('*.java')],*[str(root/'app/src/main/java/com/elitedarkkaiser/nubialy'/(n+'.java')) for n in names]],check=True)
    subprocess.run(['java','-cp',str(temp/'classes'),'com.elitedarkkaiser.nubialy.FingerprintClientTest'],check=True)
