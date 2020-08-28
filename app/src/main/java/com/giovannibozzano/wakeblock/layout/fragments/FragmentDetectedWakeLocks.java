package com.giovannibozzano.wakeblock.layout.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.giovannibozzano.wakeblock.R;
import com.giovannibozzano.wakeblock.WakeBlock;
import com.giovannibozzano.wakeblock.database.DatabaseHelper;
import com.giovannibozzano.wakeblock.database.WakeLocksTable;
import com.giovannibozzano.wakeblock.enums.WakeLockStatus;
import com.giovannibozzano.wakeblock.layout.recyclerview.AdapterWakeLocks;
import com.giovannibozzano.wakeblock.tasks.RenderFragmentDetectedWakeLocks;
import com.giovannibozzano.wakeblock.utils.Utils;
import com.giovannibozzano.wakeblock.utils.WakeLockData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentDetectedWakeLocks extends Fragment
{
	private Map<String, WakeLockData> wakeLockList = new LinkedHashMap<>();
	private DatabaseHelper databaseHelper;
	@BindView(R.id.logs_refresh) SwipeRefreshLayout swipeRefreshLayout;
	@BindView(R.id.recycler_view) RecyclerView recyclerView;
	private String searchQuery = "";
	private SearchView searchView;
	private MenuItem bootList;
	private MenuItem hideSystemWakeLocks;
	private MenuItem showOnlyBlockedWakeLocks;
	private MenuItem orderByName;
	private MenuItem orderByOccurrences;
	private MenuItem orderByTimesBlocked;
	private MenuItem orderByRunTime;

	@Override
	public void setMenuVisibility(boolean visible)
	{
		super.setMenuVisibility(visible);
		if (!visible && this.recyclerView != null) {
			this.searchQuery = "";
			this.recyclerView.setAdapter(null);
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState)
	{
		View view = layoutInflater.inflate(R.layout.fragment_detected_wakelocks, viewGroup, false);
		ButterKnife.bind(this, view);
		if (this.getContext() == null) {
			return view;
		}
		this.setHasOptionsMenu(true);
		this.databaseHelper = new DatabaseHelper(this.getContext());
		this.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
		this.recyclerView.setAdapter(new AdapterWakeLocks(this.databaseHelper, this.wakeLockList));
		this.recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));
		return view;
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.menu_wakelock_list, menu);
		this.searchView = ((SearchView) menu.findItem(R.id.action_search).getActionView());
		this.bootList = menu.findItem(R.id.boot_list);
		this.hideSystemWakeLocks = menu.findItem(R.id.hide_system_wakelocks);
		this.showOnlyBlockedWakeLocks = menu.findItem(R.id.show_only_blocked_wakelocks);
		this.orderByName = menu.findItem(R.id.order_by_name);
		this.orderByOccurrences = menu.findItem(R.id.order_by_occurrences);
		this.orderByTimesBlocked = menu.findItem(R.id.order_by_times_blocked);
		this.orderByRunTime = menu.findItem(R.id.order_by_run_time);
		new RenderFragmentDetectedWakeLocks(this).execute();
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem menuItem)
	{
		if (this.getContext() == null) {
			return false;
		}
		Drawable checkedDrawable = ContextCompat.getDrawable(this.getContext(), R.drawable.ic_checked_checkbox);
		Drawable uncheckedDrawable = ContextCompat.getDrawable(this.getContext(), R.drawable.ic_unchecked_checkbox);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
		SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
		switch (menuItem.getItemId()) {
			case R.id.boot_list:
				if (sharedPreferences.getBoolean("boot_list", false)) {
					this.bootList.setIcon(uncheckedDrawable);
					this.bootList.setChecked(false);
					sharedPreferencesEditor.putBoolean("boot_list", false);
				} else {
					this.bootList.setIcon(checkedDrawable);
					this.bootList.setChecked(true);
					sharedPreferencesEditor.putBoolean("boot_list", true);
				}
				break;
			case R.id.hide_system_wakelocks:
				if (sharedPreferences.getBoolean("hide_system_wakelocks", false)) {
					this.hideSystemWakeLocks.setIcon(uncheckedDrawable);
					this.hideSystemWakeLocks.setChecked(false);
					sharedPreferencesEditor.putBoolean("hide_system_wakelocks", false);
				} else {
					this.hideSystemWakeLocks.setIcon(checkedDrawable);
					this.hideSystemWakeLocks.setChecked(true);
					sharedPreferencesEditor.putBoolean("hide_system_wakelocks", true);
				}
				break;
			case R.id.show_only_blocked_wakelocks:
				if (sharedPreferences.getBoolean("show_only_blocked_wakelocks", false)) {
					this.showOnlyBlockedWakeLocks.setIcon(uncheckedDrawable);
					this.showOnlyBlockedWakeLocks.setChecked(false);
					sharedPreferencesEditor.putBoolean("show_only_blocked_wakelocks", false);
				} else {
					this.showOnlyBlockedWakeLocks.setIcon(checkedDrawable);
					this.showOnlyBlockedWakeLocks.setChecked(true);
					sharedPreferencesEditor.putBoolean("show_only_blocked_wakelocks", true);
				}
				break;
			case R.id.order_by_name:
				this.orderByName.setIcon(checkedDrawable);
				this.orderByOccurrences.setIcon(uncheckedDrawable);
				this.orderByTimesBlocked.setIcon(uncheckedDrawable);
				this.orderByRunTime.setIcon(uncheckedDrawable);
				this.orderByName.setChecked(true);
				this.orderByOccurrences.setChecked(false);
				this.orderByTimesBlocked.setChecked(false);
				this.orderByRunTime.setChecked(false);
				sharedPreferencesEditor.putInt("order_wakelocks_by", 0);
				break;
			case R.id.order_by_occurrences:
				this.orderByName.setIcon(uncheckedDrawable);
				this.orderByOccurrences.setIcon(checkedDrawable);
				this.orderByTimesBlocked.setIcon(uncheckedDrawable);
				this.orderByRunTime.setIcon(uncheckedDrawable);
				this.orderByName.setChecked(false);
				this.orderByOccurrences.setChecked(true);
				this.orderByTimesBlocked.setChecked(false);
				this.orderByRunTime.setChecked(false);
				sharedPreferencesEditor.putInt("order_wakelocks_by", 1);
				break;
			case R.id.order_by_times_blocked:
				this.orderByName.setIcon(uncheckedDrawable);
				this.orderByOccurrences.setIcon(uncheckedDrawable);
				this.orderByTimesBlocked.setIcon(checkedDrawable);
				this.orderByRunTime.setIcon(uncheckedDrawable);
				this.orderByName.setChecked(false);
				this.orderByOccurrences.setChecked(false);
				this.orderByTimesBlocked.setChecked(true);
				this.orderByRunTime.setChecked(false);
				sharedPreferencesEditor.putInt("order_wakelocks_by", 2);
				break;
			case R.id.order_by_run_time:
				this.orderByName.setIcon(uncheckedDrawable);
				this.orderByOccurrences.setIcon(uncheckedDrawable);
				this.orderByTimesBlocked.setIcon(uncheckedDrawable);
				this.orderByRunTime.setIcon(checkedDrawable);
				this.orderByName.setChecked(false);
				this.orderByOccurrences.setChecked(false);
				this.orderByTimesBlocked.setChecked(false);
				this.orderByRunTime.setChecked(true);
				sharedPreferencesEditor.putInt("order_wakelocks_by", 3);
				break;
			default:
				return super.onOptionsItemSelected(menuItem);
		}
		sharedPreferencesEditor.apply();
		this.initializeList(sharedPreferences.getBoolean("boot_list", false), sharedPreferences.getBoolean("hide_system_wakelocks", false), sharedPreferences.getBoolean("show_only_blocked_wakelocks", false));
		this.notifyDataSetChanged();
		return false;
	}

	public void initializeList(boolean bootList, boolean hideSystemWakeLocks, boolean showOnlyBlockedWakeLocks)
	{
		this.wakeLockList.clear();
		Map<String, WakeLockData> unorderedHashMap = new HashMap<>();
		if (WakeBlock.getWakeBlockService().getLoadedWakeLocks() != null && WakeBlock.getWakeBlockService().getLoadedWakeLocks().size() > 0) {
			Map<String, WakeLockData> temporaryWakeLockList = new HashMap<>(WakeBlock.getWakeBlockService().getLoadedWakeLocks());
			for (String key : temporaryWakeLockList.keySet()) {
				WakeLockData wakeLockData = temporaryWakeLockList.get(key);
				if ((!hideSystemWakeLocks || wakeLockData.getAcquiringPackages().size() > 1 || !wakeLockData.getAcquiringPackages().contains("android")) && (!showOnlyBlockedWakeLocks || wakeLockData.getIsBlocked()) && (!bootList || wakeLockData.getOccurrencesBoot() > 0)) {
					unorderedHashMap.put(key, wakeLockData);
				}
			}
		} else {
			if (bootList) {
				return;
			}
			SQLiteDatabase database = this.databaseHelper.getReadableDatabase();
			String[] projection = { WakeLocksTable.WakeLockEntry.COLUMN_TAG, WakeLocksTable.WakeLockEntry.COLUMN_OCCURRENCES, WakeLocksTable.WakeLockEntry.COLUMN_TIMES_BLOCKED, WakeLocksTable.WakeLockEntry.COLUMN_RUN_TIME, WakeLocksTable.WakeLockEntry.COLUMN_LAST_ACQUISITION, WakeLocksTable.WakeLockEntry.COLUMN_ACQUIRING_PACKAGES, WakeLocksTable.WakeLockEntry.COLUMN_IS_BLOCKED, WakeLocksTable.WakeLockEntry.COLUMN_BLOCK_TIME };
			Cursor cursor = database.query(WakeLocksTable.WakeLockEntry.TABLE_NAME, projection, null, null, null, null, null);
			while (cursor.moveToNext()) {
				List<String> acquiringPackages = new Gson().fromJson(cursor.getString(5), new TypeToken<List<String>>()
				{
				}.getType());
				if ((!hideSystemWakeLocks || acquiringPackages.size() > 1 || !acquiringPackages.contains("android")) && (!showOnlyBlockedWakeLocks || cursor.getInt(6) == 1)) {
					unorderedHashMap.put(cursor.getString(0), new WakeLockData(cursor.getInt(1), cursor.getInt(2), cursor.getLong(3), cursor.getLong(4), acquiringPackages, cursor.getInt(6) == 1, cursor.getLong(7), WakeLockStatus.NOT_PENDING));
				}
			}
			cursor.close();
			database.close();
		}
		Map<String, WakeLockData> nameOrderedHashMap = new LinkedHashMap<>();
		SortedSet<String> keys = new TreeSet<>(unorderedHashMap.keySet());
		for (String key : keys) {
			nameOrderedHashMap.put(key, unorderedHashMap.get(key));
		}
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
		switch (sharedPreferences.getInt("order_wakelocks_by", 0)) {
			case 0:
				this.wakeLockList.putAll(nameOrderedHashMap);
				break;
			case 1:
				this.wakeLockList = Utils.sortWakeLocks(nameOrderedHashMap, Utils.ORDER_BY_OCCURRENCES, bootList);
				break;
			case 2:
				this.wakeLockList = Utils.sortWakeLocks(nameOrderedHashMap, Utils.ORDER_BY_TIMES_BLOCKED, bootList);
				break;
			default:
				this.wakeLockList = Utils.sortWakeLocks(nameOrderedHashMap, Utils.ORDER_BY_RUN_TIME, bootList);
		}
	}

	public void notifyDataSetChanged()
	{
		((AdapterWakeLocks) this.recyclerView.getAdapter()).setLastSelected(null);
		if (((AdapterWakeLocks) this.recyclerView.getAdapter()).getActionMode() != null) {
			((AdapterWakeLocks) this.recyclerView.getAdapter()).getActionMode().finish();
		}
		((AdapterWakeLocks) this.recyclerView.getAdapter()).setWakeLockList(Utils.filterWakeLocks(this.wakeLockList, this.searchQuery));
		this.recyclerView.getAdapter().notifyDataSetChanged();
	}

	public SwipeRefreshLayout getSwipeRefreshLayout()
	{
		return this.swipeRefreshLayout;
	}

	public SearchView getSearchView()
	{
		return searchView;
	}

	public MenuItem getBootList()
	{
		return bootList;
	}

	public MenuItem getHideSystemWakeLocks()
	{
		return hideSystemWakeLocks;
	}

	public MenuItem getShowOnlyBlockedWakeLocks()
	{
		return this.showOnlyBlockedWakeLocks;
	}

	public MenuItem getOrderByName()
	{
		return orderByName;
	}

	public MenuItem getOrderByOccurrences()
	{
		return orderByOccurrences;
	}

	public MenuItem getOrderByTimesBlocked()
	{
		return orderByTimesBlocked;
	}

	public MenuItem getOrderByRunTime()
	{
		return orderByRunTime;
	}

	public void setSearchQuery(String searchQuery)
	{
		this.searchQuery = searchQuery;
	}
}
