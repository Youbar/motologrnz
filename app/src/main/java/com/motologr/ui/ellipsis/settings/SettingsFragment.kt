package com.motologr.ui.ellipsis.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.motologr.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}