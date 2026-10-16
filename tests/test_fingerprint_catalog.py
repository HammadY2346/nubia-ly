"""Upstream completeness, unchanged artwork, stable IDs, and real Drawable resource delegation."""
from pathlib import Path
import hashlib,json,re,subprocess,tempfile,xml.etree.ElementTree as E
root=Path(__file__).resolve().parents[1]
base=root/'app/src/main/java/com/elitedarkkaiser/nubialy'
manifest=json.loads((root/'third_party/rising-udfps/catalog.json').read_text())
upstream=[i.text for i in E.parse(root/'third_party/rising-udfps/upstream-arrays.xml').getroot().find('string-array')]
assert len(upstream)==len(set(upstream))==184
assert [i['name'] for i in manifest['icons']]==upstream
catalog=(base/'FingerprintStyles.java').read_text()
resources=json.loads('['+re.search(r'RESOURCES=\{(.*?)\};',catalog,re.S).group(1)+']')
assert resources[:5]==['']*5 and resources[5:]==['rising_fp_'+n for n in upstream]
for icon in manifest['icons']:
    data=(root/icon['path']).read_bytes()
    assert hashlib.sha256(data).hexdigest()==icon['sha256']
    if icon['path'].endswith('.xml'):E.fromstring(data)
    else:assert data.startswith(b'RIFF') and data[8:12]==b'WEBP' or data.startswith(b'\x89PNG\r\n\x1a\n')
sources={
'android/graphics/Rect.java':'package android.graphics;public class Rect {public int width(){return 100;}public int height(){return 100;}public float exactCenterX(){return 50;}public float exactCenterY(){return 50;}}',
'android/graphics/Paint.java':'package android.graphics;public class Paint {public static final int ANTI_ALIAS_FLAG=1;public enum Style {STROKE}public enum Cap {ROUND}public enum Join {ROUND}public Paint(int f){}public void setColor(int c){}public void setStyle(Style s){}public void setStrokeCap(Cap c){}public void setStrokeJoin(Join j){}public void setStrokeWidth(float f){}public void setAlpha(int a){}public void setColorFilter(ColorFilter f){}}',
'android/graphics/Path.java':'package android.graphics;public class Path {public void moveTo(float x,float y){}public void lineTo(float x,float y){}public void close(){}}',
'android/graphics/Canvas.java':'package android.graphics;public class Canvas {public void drawCircle(float x,float y,float r,Paint p){}public void drawLine(float a,float b,float c,float d,Paint p){}public void drawPath(Path p,Paint paint){}}',
'android/graphics/ColorFilter.java':'package android.graphics;public class ColorFilter {}',
'android/graphics/PixelFormat.java':'package android.graphics;public class PixelFormat {public static final int TRANSLUCENT=-3;}',
'android/graphics/drawable/Drawable.java':'''package android.graphics.drawable;import android.graphics.*;public class Drawable {private Rect bounds=new Rect();public int draws,alpha=255;public ColorFilter filter;public Drawable mutate(){return this;}public Rect getBounds(){return bounds;}public void setBounds(Rect r){bounds=r;}public void draw(Canvas c){draws++;}public void setAlpha(int a){alpha=a;}public void setColorFilter(ColorFilter f){filter=f;}public int getOpacity(){return 0;}public void invalidateSelf(){}}''',
'android/content/res/Resources.java':'''package android.content.res;public class Resources {public android.graphics.drawable.Drawable last;public int calls;public int getIdentifier(String n,String type,String pkg){if(!type.equals("drawable")||!pkg.equals("com.elitedarkkaiser.nubialy")||!n.startsWith("rising_fp_"))throw new AssertionError();return 1;}public android.graphics.drawable.Drawable getDrawable(int id,Object theme){calls++;return last=new android.graphics.drawable.Drawable();}}''',
'CatalogTest.java':'''import com.elitedarkkaiser.nubialy.FingerprintStyles;import com.elitedarkkaiser.nubialy.xposed.FingerprintGlyphDrawable;public class CatalogTest {static void check(boolean b){if(!b)throw new AssertionError();}public static void main(String[] args){check(FingerprintStyles.NAMES.length==189);check(!FingerprintStyles.valid(-1)&&!FingerprintStyles.valid(189));check(FingerprintStyles.NAMES[0].equals("Stock")&&FingerprintStyles.NAMES[4].equals("Diamond"));try{new FingerprintGlyphDrawable(5,0);throw new AssertionError();}catch(IllegalStateException expected){}var r=new android.content.res.Resources();FingerprintGlyphDrawable.artworkResources(r);for(int i=0;i<189;i++){var glyph=new FingerprintGlyphDrawable(i,0xff123456);var bounds=new android.graphics.Rect();glyph.setBounds(bounds);glyph.draw(new android.graphics.Canvas());if(i>=5){check(r.last.draws==1&&r.last.getBounds()==bounds&&r.last.filter==null);glyph.setAlpha(128);check(r.last.alpha==128);var f=new android.graphics.ColorFilter();glyph.setColorFilter(f);check(r.last.filter==f);}}check(r.calls==184);try{new FingerprintGlyphDrawable(189,0);throw new AssertionError();}catch(IllegalArgumentException expected){}System.out.println("Fingerprint catalog passed: 184 unchanged assets, append-only IDs, every graphic loads and draws, original colors, alpha/bounds delegation, missing-resource fence");}}'''
}
with tempfile.TemporaryDirectory(prefix='nubialy-catalog-') as d:
    work=Path(d)
    for name,content in sources.items():
        p=work/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(content)
    files=[str(p) for p in work.rglob('*.java')]+[str(base/'FingerprintStyles.java'),str(base/'xposed/FingerprintGlyphDrawable.java')]
    subprocess.run(['java','com.sun.tools.javac.Main','-d',str(work/'classes'),*files],check=True)
    subprocess.run(['java','-cp',str(work/'classes'),'CatalogTest'],check=True)
