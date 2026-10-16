"""Inspect pinned upstream candidates offline, or download to an explicitly supplied cache.
No root, extraction, overlays, system paths or device changes. Not an activation tool.
"""
from pathlib import Path,PurePosixPath
from zipfile import ZipFile,ZIP_STORED
from urllib.request import urlopen
import argparse,hashlib,json,tempfile,os
ROOT=Path(__file__).resolve().parents[1]
CATALOG=json.loads((ROOT/'third_party/rising-bootanimations/catalog.json').read_text())
def inspect(path,entry):
 if path.stat().st_size!=entry['bytes']:raise ValueError('Archive size mismatch')
 data=path.read_bytes()
 if len(data)!=entry['bytes'] or hashlib.sha256(data).hexdigest()!=entry['sha256']:raise ValueError('Archive identity mismatch')
 with ZipFile(path) as z:
  names=set();total=0;frames=0
  for item in z.infolist():
   name=PurePosixPath(item.filename)
   if name.is_absolute() or '..' in name.parts or '\\' in item.filename or item.filename in names:raise ValueError('Unsafe/duplicate member')
   names.add(item.filename)
   if item.flag_bits&1 or item.compress_type!=ZIP_STORED or item.file_size>8*1024*1024:raise ValueError('Unsupported archive member')
   if (item.external_attr>>16)&0o170000==0o120000:raise ValueError('Symlink member')
   total+=item.file_size
   if not item.is_dir():
    if item.filename!='desc.txt' and not (len(name.parts)==2 and name.parts[0].startswith('part') and (name.suffix.lower() in ('.png','.jpg','.jpeg') or name.name=='trim.txt')):raise ValueError('Unexpected member')
    if name.suffix.lower() in ('.png','.jpg','.jpeg'):frames+=1
  if len(names)>2000 or total>64*1024*1024 or not frames:raise ValueError('Archive budget exceeded')
  if z.getinfo('desc.txt').file_size>4096:raise ValueError('Oversized descriptor')
  desc=z.read('desc.txt').decode('utf-8');lines=[line.strip() for line in desc.splitlines() if line.strip()]
  width,height,fps=map(int,lines[0].split())
  if (width,height,fps)!=(entry['width'],entry['height'],entry['fps']) or width*height>10_000_000 or not 1<=fps<=60:raise ValueError('Unexpected descriptor')
  parts=[]
  for line in lines[1:]:
   fields=line.split()
   if fields[0]=='dynamic_colors':continue
   if fields[0] not in ('p','c') or len(fields)<4 or int(fields[1])<0 or int(fields[2])<0:raise ValueError('Unsupported sequence')
   part=fields[3]
   if not part.startswith('part') or '/' in part or not any(n.startswith(part+'/') for n in names):raise ValueError('Missing part')
   parts.append(dict(type=fields[0],repeat=int(fields[1]),pause=int(fields[2]),directory=part))
  if not parts:raise ValueError('No animation sequence')
  return dict(id=entry['id'],width=width,height=height,fps=fps,frames=frames,bytes=len(data),dynamic_colors=entry['dynamic_colors'],parts=parts)
def main():
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('cache',type=Path);parser.add_argument('--download',action='store_true');args=parser.parse_args()
 if args.download:args.cache.mkdir(parents=True,exist_ok=True)
 results=[]
 for entry in CATALOG['animations']:
  path=args.cache/entry['file']
  if args.download and not path.exists():
   url=f"https://raw.githubusercontent.com/{CATALOG['repository']}/{CATALOG['commit']}/{CATALOG['directory']}/{entry['file']}"
   with urlopen(url,timeout=30) as response:data=response.read(entry['bytes']+1)
   if len(data)!=entry['bytes'] or hashlib.sha256(data).hexdigest()!=entry['sha256']:raise ValueError('Downloaded identity mismatch')
   fd,name=tempfile.mkstemp(dir=args.cache,prefix='.boot-')
   try:
    with os.fdopen(fd,'wb') as stream:stream.write(data)
    os.replace(name,path)
   finally:
    if os.path.exists(name):os.unlink(name)
  results.append(inspect(path,entry))
 print(json.dumps(results,indent=2))
if __name__=='__main__':main()
