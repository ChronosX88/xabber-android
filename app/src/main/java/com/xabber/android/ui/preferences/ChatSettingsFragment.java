package com.xabber.android.ui.preferences;

import android.os.Bundle;

import com.xabber.android.ui.helper.PreferenceSummaryHelper;
import com.xabber.androiddev.R;

public class ChatSettingsFragment  extends android.preference.PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference_chat);

        PreferenceSummaryHelper.updateSummary(getPreferenceScreen());
    }
}
