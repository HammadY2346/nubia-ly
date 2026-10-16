package com.elitedarkkaiser.nubialy;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.android.material.card.MaterialCardView;
import com.elitedarkkaiser.nubialy.xposed.FingerprintGlyphDrawable;

/** Recycled gallery cards own their activation; the image cannot swallow selection taps. */
final class FingerprintIconAdapter extends BaseAdapter {
    private final android.content.Context context;
    private final int selected;
    private final java.util.function.IntConsumer choose;
    FingerprintIconAdapter(android.content.Context context,int selected,java.util.function.IntConsumer choose){this.context=context;this.selected=selected;this.choose=choose;FingerprintGlyphDrawable.artworkResources(context.getResources());}
    private int dp(float value){return Math.round(value*context.getResources().getDisplayMetrics().density);}
    private int getColor(int id){return context.getColor(id);}
    private android.content.res.Resources getResources(){return context.getResources();}
    private android.graphics.drawable.Drawable getDrawable(int id){return context.getDrawable(id);}
    @Override public int getCount(){return FingerprintStyles.NAMES.length;}
    @Override public Object getItem(int position){return FingerprintStyles.NAMES[position];}
    @Override public long getItemId(int position){return position;}
    @Override public boolean hasStableIds(){return true;}
    @Override public View getView(int position,View convert,ViewGroup parent){
        MaterialCardView card;
        if(convert instanceof MaterialCardView existing)card=existing;
        else{
            card=new MaterialCardView(context);card.setRadius(dp(28));card.setCardElevation(0);card.setCardBackgroundColor(getColor(R.color.surface));card.setUseCompatPadding(false);
            ImageView image=new ImageView(context);image.setScaleType(ImageView.ScaleType.FIT_CENTER);image.setPadding(dp(19),dp(19),dp(19),dp(19));card.addView(image,new android.widget.FrameLayout.LayoutParams(-1,-1,Gravity.CENTER));
            // The card owns touch, keyboard and accessibility activation. Its preview is decorative.
            card.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            card.setFocusable(true);card.setClickable(true);
            card.addOnLayoutChangeListener((v,l,t,r,b,ol,ot,or,ob)->{int width=r-l;if(width>0&&v.getLayoutParams().height!=width){ViewGroup.LayoutParams params=v.getLayoutParams();params.height=width;v.setLayoutParams(params);}});
        }
        int width=Math.max(dp(80),(getResources().getDisplayMetrics().widthPixels-dp(72))/3);card.setLayoutParams(new AbsListView.LayoutParams(-1,width));
        boolean active=position==selected;card.setStrokeWidth(dp(active?3:2));card.setStrokeColor(active?getColor(R.color.primary):0xff454454);card.setSelected(active);
        card.setContentDescription(FingerprintStyles.NAMES[position]+(active?", selected":""));card.setTooltipText(FingerprintStyles.NAMES[position]);
        // Rebind the callback too: recycled cards must select their current icon, not the old ID.
        card.setOnClickListener(v->choose.accept(position));
        ImageView image=(ImageView)card.getChildAt(0);image.setClickable(false);image.setLongClickable(false);image.setFocusable(false);image.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        image.setImageDrawable(position==0?getDrawable(R.drawable.rising_fp_udfps_icon_default):new FingerprintGlyphDrawable(position,getColor(R.color.primary)));
        return card;
    }
}
