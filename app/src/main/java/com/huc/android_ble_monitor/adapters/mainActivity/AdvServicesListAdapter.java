package com.huc.android_ble_monitor.adapters.mainActivity;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.huc.android_ble_monitor.R;
import java.util.List;

public class AdvServicesListAdapter extends ArrayAdapter<Pair<String,String>> {
        private TextView mName;
        private TextView mUuid;

        public AdvServicesListAdapter(@NonNull Context context, @NonNull List<Pair<String, String>> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final Pair<String, String> p = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main_adv_service_list_item, parent, false);
            }

            initializeViews(convertView);

            mName.setText(p.first);
            mUuid.setText(p.second);

            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        private void initializeViews(View view) {
            mName = view.findViewById(R.id.name);
            mUuid = view.findViewById(R.id.uuid);
        }
}
