package com.gdevelopers.movies.helpers;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.ModelService;

public class OfflineLayout {

    public static void init(final Context context, final View emptyLayout, final KFragment kFragment, final ModelService service) {
        Button reloadBt = emptyLayout.findViewById(R.id.reload);
        Button settingsBt = emptyLayout.findViewById(R.id.settings);
        reloadBt.setOnClickListener(view -> kFragment.update(service, true));

        settingsBt.setOnClickListener(view -> context.startActivity(new Intent(Settings.ACTION_SETTINGS)));
    }
}
