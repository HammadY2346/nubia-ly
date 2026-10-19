package com.hammad.rmtweaks.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.rmtweaks.Features;
import com.hammad.rmtweaks.LogStore;
import com.hammad.rmtweaks.ModuleStatus;
import com.hammad.rmtweaks.Prefs;
import com.hammad.rmtweaks.R;

public class MainActivity extends AppCompatActivity {
    private RecyclerView list;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new HomeAdapter());
    }

    @Override protected void onResume() {
        super.onResume();
        if (list.getAdapter() != null) list.getAdapter().notifyDataSetChanged();
    }

    /** Row 0 = status card, rows 1..n = sections, last row = logs. */
    private final class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int T_STATUS = 0, T_SECTION = 1, T_LOGS = 2;

        @Override public int getItemCount() { return Features.SECTIONS.length + 2; }

        @Override public int getItemViewType(int pos) {
            if (pos == 0) return T_STATUS;
            if (pos == getItemCount() - 1) return T_LOGS;
            return T_SECTION;
        }

        @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            LayoutInflater inf = LayoutInflater.from(parent.getContext());
            View v = inf.inflate(type == T_STATUS ? R.layout.item_status : R.layout.item_section, parent, false);
            return new RecyclerView.ViewHolder(v) {};
        }

        @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
            View v = h.itemView;
            int type = getItemViewType(pos);
            if (type == T_STATUS) {
                boolean active = ModuleStatus.isActive();
                boolean readable = Prefs.isWorldReadable(MainActivity.this);
                v.findViewById(R.id.status_dot).setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(getColor(active ? R.color.ok : R.color.err)));
                ((TextView) v.findViewById(R.id.status_title)).setText(active ? R.string.status_active : R.string.status_inactive);
                ((TextView) v.findViewById(R.id.status_sub)).setText(active
                        ? getString(R.string.status_scope_hint) + (readable ? "" : "\nSettings file is not shared with apps yet — reboot once.")
                        : "RedMagic 10S Pro · NX789J · fw 11.0.11");
                return;
            }
            if (type == T_LOGS) {
                ((ImageView) v.findViewById(R.id.icon)).setImageResource(R.drawable.ic_log);
                ((TextView) v.findViewById(R.id.title)).setText(R.string.logs);
                ((TextView) v.findViewById(R.id.subtitle)).setText("What every hook did, per app — for debugging");
                TextView count = v.findViewById(R.id.count);
                int n = LogStore.read(MainActivity.this).size();
                count.setText(n + " entries");
                v.findViewById(R.id.card).setOnClickListener(x -> startActivity(new Intent(MainActivity.this, LogActivity.class)));
                return;
            }
            final Features.Section s = Features.SECTIONS[pos - 1];
            ((ImageView) v.findViewById(R.id.icon)).setImageResource(s.icon);
            ((TextView) v.findViewById(R.id.title)).setText(s.title);
            ((TextView) v.findViewById(R.id.subtitle)).setText(s.subtitle);
            ((TextView) v.findViewById(R.id.count)).setText(getString(R.string.enabled_of, Prefs.enabledCount(MainActivity.this, s), s.features.length));
            v.findViewById(R.id.card).setOnClickListener(x ->
                    startActivity(new Intent(MainActivity.this, SectionActivity.class).putExtra("section", s.id)));
        }
    }
}
