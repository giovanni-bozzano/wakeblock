package com.giovannibozzano.wakeblock.layout.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.giovannibozzano.wakeblock.R;
import com.giovannibozzano.wakeblock.WakeBlock;
import com.giovannibozzano.wakeblock.layout.activities.ActivityMain;
import com.giovannibozzano.wakeblock.utils.Utils;

public class FragmentSettings extends PreferenceFragmentCompat
{
	@Override
	public void onCreatePreferences(Bundle bundle, String rootKey)
	{
		this.setPreferencesFromResource(R.xml.settings_preferences, rootKey);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
		CheckBoxPreference darkMode = this.findPreference("dark_mode");
		darkMode.setChecked(sharedPreferences.getBoolean("dark_mode", false));
		darkMode.setOnPreferenceClickListener((preference) -> {
			SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
			sharedPreferencesEditor.putBoolean("dark_mode", ((CheckBoxPreference) preference).isChecked());
			sharedPreferencesEditor.apply();
			Intent intent = new Intent(this.getContext(), ActivityMain.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			this.startActivity(intent);
			this.getActivity().finish();
			return true;
		});
		CheckBoxPreference debugLogs = this.findPreference("debug_logs");
		debugLogs.setChecked(sharedPreferences.getBoolean("debug_logs", false));
		debugLogs.setOnPreferenceClickListener((preference) -> {
			SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
			sharedPreferencesEditor.putBoolean("debug_logs", ((CheckBoxPreference) preference).isChecked());
			sharedPreferencesEditor.apply();
			return true;
		});
		CheckBoxPreference foregroundService = this.findPreference("foreground_service");
		foregroundService.setChecked(sharedPreferences.getBoolean("foreground_service", false));
		this.findPreference("foreground_service").setOnPreferenceClickListener((preference) -> {
			SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
			sharedPreferencesEditor.putBoolean("foreground_service", ((CheckBoxPreference) preference).isChecked());
			sharedPreferencesEditor.apply();
			if (((CheckBoxPreference) preference).isChecked()) {
				Utils.bindService(this.getContext());
			} else {
				if (WakeBlock.getWakeBlockService() != null) {
					WakeBlock.getWakeBlockService().stopForeground(true);
				}
			}
			return true;
		});
		this.findPreference("manual_service_binder").setOnPreferenceClickListener((preference) -> {
			Utils.bindService(this.getContext());
			return true;
		});
	}
}
