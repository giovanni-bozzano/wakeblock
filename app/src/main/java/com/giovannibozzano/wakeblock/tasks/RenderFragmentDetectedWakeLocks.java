package com.giovannibozzano.wakeblock.tasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import com.giovannibozzano.wakeblock.R;
import com.giovannibozzano.wakeblock.layout.fragments.FragmentDetectedWakeLocks;

import java.lang.ref.WeakReference;

public class RenderFragmentDetectedWakeLocks extends AsyncTask<Void, Void, Void>
{
	private final WeakReference<FragmentDetectedWakeLocks> fragmentWeakReference;

	public RenderFragmentDetectedWakeLocks(FragmentDetectedWakeLocks fragmentDetectedWakeLocks)
	{
		this.fragmentWeakReference = new WeakReference<>(fragmentDetectedWakeLocks);
	}

	@Override
	protected Void doInBackground(Void... voids)
	{
		FragmentDetectedWakeLocks fragment = this.fragmentWeakReference.get();
		if (fragment == null) {
			return null;
		}
		Activity activity = fragment.getActivity();
		if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
			return null;
		}
		activity.runOnUiThread(() -> fragment.getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextSubmit(String searchQuery)
			{
				return false;
			}

			@Override
			public boolean onQueryTextChange(String query)
			{
				fragment.setSearchQuery(query);
				fragment.notifyDataSetChanged();
				return false;
			}
		}));
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		fragment.initializeList(sharedPreferences.getBoolean("boot_list", false), sharedPreferences.getBoolean("hide_system_wakelocks", false), sharedPreferences.getBoolean("show_only_blocked_wakelocks", false));
		return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
		FragmentDetectedWakeLocks fragment = this.fragmentWeakReference.get();
		if (fragment == null) {
			return;
		}
		Activity activity = fragment.getActivity();
		if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
			return;
		}
		fragment.getSwipeRefreshLayout().setColorSchemeResources(R.color.md_green_600);
		fragment.getOrderByName().setChecked(true);
		fragment.getSearchView().setMaxWidth((int) (256 * fragment.getResources().getDisplayMetrics().density + 0.5f));
		Drawable checkedDrawable = ContextCompat.getDrawable(activity, R.drawable.ic_checked_checkbox);
		Drawable uncheckedDrawable = ContextCompat.getDrawable(activity, R.drawable.ic_unchecked_checkbox);
		checkedDrawable.setColorFilter(ContextCompat.getColor(activity, R.color.md_grey_800), Mode.SRC_ATOP);
		uncheckedDrawable.setColorFilter(ContextCompat.getColor(activity, R.color.md_grey_800), Mode.SRC_ATOP);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		if (sharedPreferences.getBoolean("boot_list", false)) {
			fragment.getBootList().setChecked(true);
			fragment.getBootList().setIcon(checkedDrawable);
		} else {
			fragment.getBootList().setIcon(uncheckedDrawable);
		}
		if (sharedPreferences.getBoolean("hide_system_wakelocks", false)) {
			fragment.getHideSystemWakeLocks().setChecked(true);
			fragment.getHideSystemWakeLocks().setIcon(checkedDrawable);
		} else {
			fragment.getHideSystemWakeLocks().setIcon(uncheckedDrawable);
		}
		if (sharedPreferences.getBoolean("show_only_blocked_wakelocks", false)) {
			fragment.getShowOnlyBlockedWakeLocks().setChecked(true);
			fragment.getShowOnlyBlockedWakeLocks().setIcon(checkedDrawable);
		} else {
			fragment.getShowOnlyBlockedWakeLocks().setIcon(uncheckedDrawable);
		}
		switch (sharedPreferences.getInt("order_wakelocks_by", 0)) {
			case 0:
				fragment.getOrderByName().setChecked(true);
				fragment.getOrderByName().setIcon(checkedDrawable);
				fragment.getOrderByOccurrences().setIcon(uncheckedDrawable);
				fragment.getOrderByTimesBlocked().setIcon(uncheckedDrawable);
				fragment.getOrderByRunTime().setIcon(uncheckedDrawable);
				break;
			case 1:
				fragment.getOrderByOccurrences().setChecked(true);
				fragment.getOrderByOccurrences().setIcon(checkedDrawable);
				fragment.getOrderByName().setIcon(uncheckedDrawable);
				fragment.getOrderByTimesBlocked().setIcon(uncheckedDrawable);
				fragment.getOrderByRunTime().setIcon(uncheckedDrawable);
				break;
			case 2:
				fragment.getOrderByTimesBlocked().setChecked(true);
				fragment.getOrderByTimesBlocked().setIcon(checkedDrawable);
				fragment.getOrderByName().setIcon(uncheckedDrawable);
				fragment.getOrderByOccurrences().setIcon(uncheckedDrawable);
				fragment.getOrderByRunTime().setIcon(uncheckedDrawable);
				break;
			default:
				fragment.getOrderByRunTime().setChecked(true);
				fragment.getOrderByRunTime().setIcon(checkedDrawable);
				fragment.getOrderByName().setIcon(uncheckedDrawable);
				fragment.getOrderByOccurrences().setIcon(uncheckedDrawable);
				fragment.getOrderByTimesBlocked().setIcon(uncheckedDrawable);
		}
		fragment.getSwipeRefreshLayout().setOnRefreshListener(() -> {
			fragment.initializeList(sharedPreferences.getBoolean("boot_list", false), sharedPreferences.getBoolean("hide_system_wakelocks", false), sharedPreferences.getBoolean("show_only_blocked_wakelocks", false));
			fragment.notifyDataSetChanged();
			fragment.getSwipeRefreshLayout().setRefreshing(false);
		});
		fragment.notifyDataSetChanged();
	}
}
