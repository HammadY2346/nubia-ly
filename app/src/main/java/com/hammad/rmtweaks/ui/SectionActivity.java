package com.hammad.rmtweaks.ui;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.hammad.rmtweaks.Features;
import com.hammad.rmtweaks.Frames;
import com.hammad.rmtweaks.Prefs;
import com.hammad.rmtweaks.R;

public class SectionActivity extends AppCompatActivity {
    private Features.Section section;
    private final ActivityResultLauncher<Uri> pickFolder = registerForActivityResult(
            new ActivityResultContracts.OpenDocumentTree(), uri -> {
                if (uri == null) return;
                int n = Frames.importFolder(this, uri);
                Toast.makeText(this, n == 0 ? "No image frames found in that folder" : n + " frames imported", Toast.LENGTH_LONG).show();
                RecyclerView list = findViewById(R.id.list);
                if (list.getAdapter() != null) list.getAdapter().notifyDataSetChanged();
            });

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        section = Features.section(getIntent().getStringExtra("section"));
        if (section == null) { finish(); return; }
        setContentView(R.layout.activity_section);
        MaterialToolbar tb = findViewById(R.id.toolbar);
        tb.setTitle(section.title);
        tb.setNavigationOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.hint)).setText(getString(R.string.restart_hint) + "\nApps: " + String.join(", ", section.packages));
        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new FeatureAdapter());
    }

    private final class FeatureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Override public int getItemCount() { return section.features.length; }

        @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feature, parent, false);
            return new RecyclerView.ViewHolder(v) {};
        }

        @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
            final Features.Feature f = section.features[pos];
            View v = h.itemView;
            ((TextView) v.findViewById(R.id.title)).setText(f.title);
            TextView desc = v.findViewById(R.id.desc);
            desc.setText(f.desc);
            desc.setVisibility(f.desc == null || f.desc.isEmpty() ? View.GONE : View.VISIBLE);
            final MaterialSwitch sw = v.findViewById(R.id.toggle);
            final TextView value = v.findViewById(R.id.value);
            View card = v.findViewById(R.id.card);
            if (f.type == Features.Type.ACTION) {
                sw.setVisibility(View.GONE);
                value.setVisibility(View.VISIBLE);
                int n = Frames.count(SectionActivity.this);
                value.setText(n == 0 ? "Pick folder" : n + " frames");
                card.setOnClickListener(x -> pickFolder.launch(null));
            } else if (f.type == Features.Type.SWITCH) {
                sw.setVisibility(View.VISIBLE);
                value.setVisibility(View.GONE);
                sw.setOnCheckedChangeListener(null);
                sw.setChecked(Prefs.on(SectionActivity.this, f.key));
                sw.setOnCheckedChangeListener((b, checked) -> Prefs.set(SectionActivity.this, f.key, checked));
                card.setOnClickListener(x -> sw.toggle());
            } else {
                sw.setVisibility(View.GONE);
                value.setVisibility(View.VISIBLE);
                int idx = Prefs.choice(SectionActivity.this, f.key);
                if (idx < 0 || idx >= f.choices.length) idx = 0;
                value.setText(f.choices[idx]);
                final int current = idx;
                card.setOnClickListener(x -> new MaterialAlertDialogBuilder(SectionActivity.this)
                        .setTitle(f.title)
                        .setSingleChoiceItems(f.choices, current, (d, which) -> {
                            Prefs.set(SectionActivity.this, f.key, which);
                            notifyItemChanged(h.getBindingAdapterPosition());
                            d.dismiss();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show());
            }
        }
    }
}
