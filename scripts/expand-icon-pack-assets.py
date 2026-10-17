#!/usr/bin/env python3
"""Complete standard QS glyph coverage from a pinned Rising checkout.
Usage: python3 scripts/expand-icon-pack-assets.py /path/to/android_vendor_addons
"""
import sys,json,hashlib,re,copy
from pathlib import Path
import xml.etree.ElementTree as E
root=Path(__file__).resolve().parents[1];up=Path(sys.argv[1]);out=root/'app/src/main/res/drawable'
a='http://schemas.android.com/apk/res/android';apt='http://schemas.android.com/aapt';E.register_namespace('android',a);E.register_namespace('aapt',apt)
manifest=json.loads((root/'docs/ICON_PACK_ASSETS.json').read_text());entries={(x['pack'],x['key']):x for x in manifest['files']}
families=['Acherus','Aurora','Circular','Filled','Gradicon','Kai','Lorn','Nostalgic','OOS','Outline','Plumpy','PUI','Rounded','Sam','Victor','Xperia']
candidates={'controls':['controls_icon','ic_settings_home'],'qr':['ic_qr_code_scanner','ic_qrcode_24dp','ic_qrcode_32dp'],'share':['ic_share'],'sync':['ic_qs_sync','ic_qs_sync_on','ic_sync'],'invert':['ic_invert_colors'],'dark':['ic_qs_ui_mode_night','qs_light_dark_theme_icon_on'],'extra_dim':['ic_reduce_bright_colors','ic_accessibility_reduce_bright_colors_foreground'],'screenshot':['ic_qs_screenshot','ic_screenshot']}
candidates.update({'nfc':['ic_qs_nfc','stat_sys_nfc'],'hotspot':['ic_hotspot','stat_sys_hotspot'],'screenrecord':['ic_screenrecord','ic_screen_recorder'],'ring':['ic_qs_ringer_audible','ic_volume_ringer']})
paths={'controls':'M3,11 L12,3 L21,11 M5,10 L5,21 L19,21 L19,10 M10,21 L10,15 L14,15 L14,21','qr':'M3,3 L9,3 L9,9 L3,9 Z M15,3 L21,3 L21,9 L15,9 Z M3,15 L9,15 L9,21 L3,21 Z M15,15 L18,15 L18,18 L21,18 L21,21 L15,21 Z','share':'M17,5 L7,12 L17,19 M20,5 A3,3 0,1 1,14,5 A3,3 0,1 1,20,5 M10,12 A3,3 0,1 1,4,12 A3,3 0,1 1,10,12 M20,19 A3,3 0,1 1,14,19 A3,3 0,1 1,20,19','sync':'M4,10 A8,8 0,0 1,18,6 L21,9 M21,4 L21,9 L16,9 M20,14 A8,8 0,0 1,6,18 L3,15 M3,20 L3,15 L8,15','invert':'M12,2 C9,6 4,11 4,15 A8,8 0,0 0,20,15 C20,11 15,6 12,2 Z M12,5 L12,20','dark':'M19,16 A9,9 0,0 1,8,4 A9,9 0,1 0,19,16 Z','extra_dim':'M4,5 L20,5 L20,19 L4,19 Z M8,12 L16,12','screenshot':'M8,3 L3,3 L3,8 M16,3 L21,3 L21,8 M3,16 L3,21 L8,21 M21,16 L21,21 L16,21 M8,8 L16,8 L16,16 L8,16 Z'}
paths.update({'nfc':'M4,3 L20,3 L20,21 L4,21 Z M8,17 L8,7 L16,17 L16,7','hotspot':'M3,16 A10,10 0,1 1,21,16 M7,14 A6,6 0,1 1,17,14 M12,12 L12,21 M10,21 L14,21','screenrecord':'M3,5 L16,5 L16,19 L3,19 Z M16,10 L22,6 L22,18 L16,14','ring':'M6,16 L6,10 A6,6 0,0 1,18,10 L18,16 L20,18 L4,18 Z M10,21 L14,21'})
candidates.update({k:[] for k in ('game','lighting','fan','liquid')})
paths.update({'game':'M7,7 L17,7 Q20,7 21,12 L22,17 Q22,21 18,19 L15,16 L9,16 L6,19 Q2,21 2,17 L3,12 Q4,7 7,7 Z M5,12 L11,12 M8,9 L8,15 M16,11 L16,12 M19,13 L19,14','lighting':'M8,2 L16,2 L16,22 L8,22 Z M11,6 L13,6 M11,12 L13,12 M11,18 L13,18','fan':'M12,12 C3,10 2,3 8,3 C13,3 14,8 12,12 C14,3 21,2 21,8 C21,13 16,14 12,12 C21,14 22,21 16,21 C11,21 10,16 12,12 C10,21 3,22 3,16 C3,11 8,10 12,12 Z','liquid':'M12,2 C10,6 5,11 5,15 A7,7 0,0 0,19,15 C19,11 14,6 12,2 Z M8,15 A4,4 0,0 0,12,19'})
def convert(p,index,seen=()):
 if p in seen:raise ValueError('cyclic drawable')
 elem=E.fromstring(p.read_text());seen=(*seen,p)
 for node in list(elem.iter()):
  for attr,val in list(node.attrib.items()):
   if val.startswith('?'):node.set(attr,'#FFFFFFFF')
   elif val.startswith('@color/'):node.set(attr,{'qs_tile_icon_oos':'#FF909090','color_idk':'#FF000000'}.get(val[7:],'#FFFFFFFF'))
   elif val.startswith('@drawable/'):
    dep=index[val[10:]];child=convert(dep,index,seen);del node.attrib[attr]
    if attr.split('}')[-1] in ('fillColor','strokeColor'):
     wrapper=E.SubElement(node,'{'+apt+'}attr',{'name':'android:'+attr.split('}')[-1]});wrapper.append(child)
    else:node.append(child)
 return elem
for pack,family in enumerate(families,1):
 files=sorted((up/'themes/iconpacks').glob('IconPack'+family+'*/res/drawable*/*.xml'))
 index={p.stem:p for p in files}
 for key,names in candidates.items():
  dest=out/f'iconpack_{pack}_{key}.xml'
  if key in ('nfc','hotspot','screenrecord','ring') and dest.exists():continue
  source=next((index[n] for n in names if n in index),None)
  try:
   if source is None:raise KeyError(key)
   element=convert(source,index);comments='\n'.join(re.findall(r'<!--.*?-->',source.read_text(),re.S))
   text='<?xml version="1.0" encoding="utf-8"?>\n'+comments+'\n'+E.tostring(element,encoding='unicode')+'\n'
   entry={'pack':pack,'key':key,'source':str(source.relative_to(up)),'sha256':hashlib.sha256(source.read_bytes()).hexdigest()}
  except (KeyError,ValueError):
   weight=2.8 if family in ('Plumpy','PUI','Sam') else 1.7 if family in ('Outline','Kai','Circular','Xperia') else 2.2
   cap='round' if family not in ('Filled','Nostalgic','Victor') else 'square'
   text=f'''<?xml version="1.0" encoding="utf-8"?>
<!-- nubia-ly original fallback for a standard tile absent from this upstream family. -->
<vector xmlns:android="{a}" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24"><path android:pathData="{paths[key]}" android:fillColor="#00000000" android:strokeColor="#FFFFFFFF" android:strokeWidth="{weight}" android:strokeLineCap="{cap}" android:strokeLineJoin="round"/></vector>
'''
   entry={'pack':pack,'key':key,'source':'nubia-ly original family-weight fallback','sha256':hashlib.sha256(text.encode()).hexdigest()}
  dest.write_text(text);entries[pack,key]=entry
 # Mobile Data uses this family's cellular glyph, rather than stock swap arrows.
 source=out/f'iconpack_{pack}_signal_4.xml';dest=out/f'iconpack_{pack}_data.xml';dest.write_text(source.read_text());entry=dict(entries[pack,'signal_4']);entry['key']='data';entries[pack,'data']=entry
manifest['files']=list(entries.values());(root/'docs/ICON_PACK_ASSETS.json').write_text(json.dumps(manifest,indent=2)+'\n')
print('Added standard QS glyphs for all 16 packs')
