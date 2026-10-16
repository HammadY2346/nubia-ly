package com.elitedarkkaiser.nubialy;
/** Pinned Rising source archives. IDs never contain paths or shell text. */
public final class BootAnimations {
 public record Entry(String id,String name,long bytes,String sha256,int width,int height,boolean dynamicColors){}
 public static final Entry[] ENTRIES={
  new Entry("rising","Rising",10068709,"79e5c5ec55373224dd935ca77102e3d474f4f93fb8b194c48915f18c0ffb2e92",1080,2400,false),
  new Entry("cyberpunk","Cyberpunk",23773485,"7187c3ac7d8319ab160084a73d247f69daf59d49ab230eba7cec405882191514",1080,1920,false),
  new Entry("valorant","Valorant",7223104,"24ac9b2cb306a3946f84af28fcd848313b127152d6e4cbf36432d00225d83104",1080,840,false),
  new Entry("google","Google",8404191,"c8a96eac5fb156784bcdf53e981bcd1074b38a8ac85396b6082980a12e6a4b28",960,2142,false),
  new Entry("google_monet","Google Monet",3725631,"051ba247fbbf9d152edb6ff9f0ef19f3af6a231739cb7f461e5363a7fa12f8d7",1080,2338,true)};
 public static Entry find(String id){for(Entry e:ENTRIES)if(e.id().equals(id))return e;return null;}
 private BootAnimations(){}
}
