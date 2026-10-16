"""Build pinned ZIP assets plus low-resolution, finite source-frame previews. No device/root operations."""
from pathlib import Path
from zipfile import ZipFile
from PIL import Image,ImageOps,ImageChops
from io import BytesIO
import subprocess,sys,json,re,math,shutil,argparse
ROOT=Path(__file__).resolve().parents[1]
p=argparse.ArgumentParser();p.add_argument('--cache',type=Path,default=ROOT/'build/boot-cache');a=p.parse_args()
subprocess.run([sys.executable,str(ROOT/'scripts/inspect-bootanimations.py'),str(a.cache),'--download'],check=True,stdout=subprocess.DEVNULL)
catalog=json.loads((ROOT/'third_party/rising-bootanimations/catalog.json').read_text())
out=ROOT/'app/src/main/assets/bootanimations';out.mkdir(parents=True,exist_ok=True)
for entry in catalog['animations']:
 source=a.cache/entry['file'];shutil.copyfile(source,out/(entry['id']+'.zip'))
 with ZipFile(source) as z:
  lines=z.read('desc.txt').decode().splitlines();sequence=[];frame_sets={};palette=None
  for line in lines:
   if line.startswith('dynamic_colors '):palette=[tuple(bytes.fromhex(color[1:])) for color in line.split()[2:6]]
  for line in lines[1:]:
   fields=line.split()
   if not fields or fields[0] not in ('p','c'):continue
   part=fields[3];names=sorted(n for n in z.namelist() if n.startswith(part+'/') and n.lower().endswith(('.png','.jpg','.jpeg')))
   trims=[]
   if part+'/trim.txt' in z.namelist():trims=z.read(part+'/trim.txt').decode().splitlines()
   frame_sets[part]=names
   sequence.extend((name,trims[i] if i<len(trims) else None) for i,name in enumerate(names))
  step=max(1,math.ceil(len(sequence)/90));images=[]
  for name,trim in sequence[::step]:
   im=Image.open(BytesIO(z.read(name))).convert('RGBA')
   if palette:
    # AOSP/inspected player: RGBA are four color masks, not an alpha channel.
    masks=im.split();components=[]
    for component in range(3):
     weighted=[mask.point(lambda v,c=color[component]:round(v*c/255)) for mask,color in zip(masks,palette)]
     components.append(ImageChops.add(ImageChops.add(weighted[0],weighted[1]),ImageChops.add(weighted[2],weighted[3])))
    colored=Image.merge('RGB',components);white=Image.new('L',im.size,255);gray=Image.new('L',im.size,0)
    for mask in masks:
     white=ImageChops.multiply(white,mask.point(lambda v:255 if v>=13 else 0));gray=ImageChops.add(gray,mask.point(lambda v:round(v/4)))
    colored.paste(Image.merge('RGB',(gray,gray,gray)),mask=white);im=colored.convert('RGBA')
   canvas=Image.new('RGBA',(entry['width'],entry['height']),'black')
   x=(entry['width']-im.width)//2;y=(entry['height']-im.height)//2
   if trim:
    match=re.fullmatch(r'(\d+)x(\d+)\+(\d+)\+(\d+)',trim.strip())
    if not match:raise ValueError('Unsupported trim')
    w,h,x,y=map(int,match.groups())
    if im.size!=(w,h):raise ValueError('Frame/trim mismatch')
   canvas.alpha_composite(im,(x,y));images.append(ImageOps.contain(canvas.convert('RGB'),(270,600),Image.Resampling.LANCZOS))
  if not images:raise ValueError('No preview frames')
  duration=max(40,round(step*1000/entry['fps']))
  images[0].save(out/(entry['id']+'.webp'),save_all=True,append_images=images[1:],duration=duration,loop=1,quality=72,method=4)
  max(images,key=lambda frame:sum(frame.convert('L').point(lambda v:255 if v>35 else 0).histogram()[128:])).save(out/(entry['id']+'-thumb.webp'),quality=80)
 print(entry['id'],entry['bytes'],'bytes; preview',len(images),'frames')
