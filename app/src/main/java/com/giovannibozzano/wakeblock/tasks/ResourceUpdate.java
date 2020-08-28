package com.giovannibozzano.wakeblock.tasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.giovannibozzano.wakeblock.layout.fragments.FragmentInitializable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResourceUpdate extends AsyncTask<Void, Void, Void>
{
	private final WeakReference<Activity> activityWeakReference;
	private final WeakReference<FragmentInitializable> fragmentWeakReference;

	public ResourceUpdate(Activity activity)
	{
		this(activity, null);
	}

	public ResourceUpdate(FragmentInitializable fragmentFAQ)
	{
		this(fragmentFAQ.getActivity(), fragmentFAQ);
	}

	private ResourceUpdate(Activity activity, FragmentInitializable fragment)
	{
		this.activityWeakReference = new WeakReference<>(activity);
		this.fragmentWeakReference = new WeakReference<>(fragment);
	}

	@Override
	protected Void doInBackground(Void... voids)
	{
		Activity activity = this.activityWeakReference.get();
		if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
			return null;
		}
		Log.i(activity.getPackageName(), "Acquiring latest Text...");
		HashMap<String, String> WakeBlockUrls = new HashMap<>();
		WakeBlockUrls.put("FAQs", "https://raw.githubusercontent.com/MrLast98/WakeBlock/master/faq");
		WakeBlockUrls.put("Patrons", "https://raw.githubusercontent.com/MrLast98/WakeBlock/master/patrons");
		for (Map.Entry entry : WakeBlockUrls.entrySet()) {
			Log.i(activity.getPackageName(), "Downloading updated" + entry.getKey().toString() + "...");
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new URL(entry.getValue().toString()).openConnection().getInputStream()), Charset.forName("UTF-8")))) {
				Log.i(activity.getPackageName(), "Writing file down...");
				SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
				sharedPreferencesEditor.putString(entry.getKey().toString().toLowerCase().replace(" ", "_"), bufferedReader.lines().collect(Collectors.joining("\n"))).apply();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		Log.i(activity.getPackageName(), "Resources updated correctly!");
		return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
		Activity activity = this.activityWeakReference.get();
		if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
			return;
		}
		FragmentInitializable fragment = this.fragmentWeakReference.get();
		if (fragment == null) {
			return;
		}
		fragment.initialize();
	}
}
