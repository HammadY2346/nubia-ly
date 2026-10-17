package com.elitedarkkaiser.nubialy;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import java.util.HashMap;
import java.util.Map;

/** App-bundled Rising artwork; never installs overlays or edits system resources. */
public final class SystemIconPacks {
    public static final String[] NAMES={"Stock","Acherus","Aurora","Circular","Filled","Gradicon","Kai","Lorn","Nostalgic","OxygenOS","Outline","Plumpy","Pavlova UI","Rounded","Sam","Victor","Xperia"};
    private static Resources resources;
    private static final Map<String,Integer> ids=new HashMap<>();
    private SystemIconPacks(){}
    public static synchronized void resources(Resources value){resources=value;ids.clear();}
    public static boolean valid(int value){return value>=0&&value<NAMES.length;}
    public static synchronized Drawable artwork(int pack,String key){
        if(resources==null||pack<=0||!valid(pack)||key==null)return null;
        String name="iconpack_"+pack+"_"+key;
        Integer id=ids.get(name);if(id==null){id=resources.getIdentifier(name,"drawable","com.elitedarkkaiser.nubialy");ids.put(name,id);}
        return id==0?null:resources.getDrawable(id,null).mutate();
    }
}
