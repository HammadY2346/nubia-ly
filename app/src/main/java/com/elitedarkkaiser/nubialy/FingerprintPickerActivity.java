package com.elitedarkkaiser.nubialy;

import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.elitedarkkaiser.nubialy.xposed.FingerprintGlyphDrawable;

/** A full-screen, recycled gallery. Selection edits a draft; it cannot grant cosmetic authority. */
public final class FingerprintPickerActivity extends AppCompatActivity {
    private int selected;
    private int dp(float value){return Math.round(value*getResources().getDisplayMetrics().density);}
    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        if(!CompatibilityPolicy.isNx809j(Build.MODEL,Build.DEVICE)||Build.VERSION.SDK_INT!=36){finish();return;}
        selected=getIntent().getIntExtra("selection",0);if(!FingerprintStyles.valid(selected))selected=0;
        FingerprintGlyphDrawable.artworkResources(getResources());
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getColor(R.color.background));
        MaterialToolbar toolbar=new MaterialToolbar(this);toolbar.setTitle("UDFPS icon picker");toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);toolbar.setNavigationContentDescription("Back");toolbar.setNavigationOnClickListener(v->finish());toolbar.getMenu().add("Artwork credits").setOnMenuItemClickListener(item->{showCredits();return true;});root.addView(toolbar,new LinearLayout.LayoutParams(-1,dp(64)));
        TextView caption=new TextView(this);caption.setText("Choose an icon · "+FingerprintStyles.NAMES[selected]);caption.setTextSize(14);caption.setPadding(dp(24),dp(4),dp(24),dp(16));root.addView(caption);
        GridView grid=new GridView(this);grid.setNumColumns(3);grid.setHorizontalSpacing(dp(16));grid.setVerticalSpacing(dp(16));grid.setPadding(dp(20),dp(4),dp(20),dp(24));grid.setClipToPadding(false);grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);grid.setSelector(android.R.color.transparent);grid.setAdapter(new Icons());
        grid.setOnItemClickListener((parent,view,position,id)->{setResult(RESULT_OK,new Intent().putExtra("selection",position));finish();});root.addView(grid,new LinearLayout.LayoutParams(-1,0,1));
        // Keep the toolbar and last row clear of system bars, including edge-to-edge enforcement.
        root.setOnApplyWindowInsetsListener((view,insets)->{android.graphics.Insets bars=insets.getInsets(android.view.WindowInsets.Type.systemBars()|android.view.WindowInsets.Type.displayCutout());view.setPadding(bars.left,bars.top,bars.right,bars.bottom);return insets;});
        setContentView(root);root.requestApplyInsets();grid.setSelection(selected);
    }
    private void showCredits(){try(java.io.InputStream input=getAssets().open("licenses/rising-udfps.txt")){String notice=new String(input.readAllBytes(),java.nio.charset.StandardCharsets.UTF_8);new com.google.android.material.dialog.MaterialAlertDialogBuilder(this).setTitle("Artwork credits").setMessage(notice).setPositiveButton("Close",null).show();}catch(java.io.IOException unavailable){android.widget.Toast.makeText(this,"Artwork credits unavailable",android.widget.Toast.LENGTH_SHORT).show();}}
    private final class Icons extends BaseAdapter {
        @Override public int getCount(){return FingerprintStyles.NAMES.length;}
        @Override public Object getItem(int position){return FingerprintStyles.NAMES[position];}
        @Override public long getItemId(int position){return position;}
        @Override public boolean hasStableIds(){return true;}
        @Override public View getView(int position,View convert,ViewGroup parent){
            MaterialCardView card;
            if(convert instanceof MaterialCardView existing)card=existing;
            else{
                card=new MaterialCardView(FingerprintPickerActivity.this);card.setRadius(dp(28));card.setCardElevation(0);card.setCardBackgroundColor(getColor(R.color.surface));card.setUseCompatPadding(false);
                ImageView image=new ImageView(FingerprintPickerActivity.this);image.setScaleType(ImageView.ScaleType.FIT_CENTER);image.setPadding(dp(19),dp(19),dp(19),dp(19));card.addView(image,new android.widget.FrameLayout.LayoutParams(-1,-1,Gravity.CENTER));
                // GridView owns click/focus so screen readers and keyboard navigation select one item.
                card.setFocusable(false);card.setClickable(false);
                card.addOnLayoutChangeListener((v,l,t,r,b,ol,ot,or,ob)->{int width=r-l;if(width>0&&v.getLayoutParams().height!=width){ViewGroup.LayoutParams params=v.getLayoutParams();params.height=width;v.setLayoutParams(params);}});
            }
            int width=Math.max(dp(80),(getResources().getDisplayMetrics().widthPixels-dp(72))/3);card.setLayoutParams(new AbsListView.LayoutParams(-1,width));
            boolean active=position==selected;card.setStrokeWidth(dp(active?3:2));card.setStrokeColor(active?getColor(R.color.primary):0xff454454);card.setSelected(active);
            card.setContentDescription(FingerprintStyles.NAMES[position]+(active?", selected":""));card.setTooltipText(FingerprintStyles.NAMES[position]);
            ImageView image=(ImageView)card.getChildAt(0);image.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            image.setImageDrawable(position==0?getDrawable(R.drawable.rising_fp_udfps_icon_default):new FingerprintGlyphDrawable(position,getColor(R.color.primary)));
            return card;
        }
    }
}
