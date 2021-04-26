package com.giovannibozzano.wakeblock.layout.recyclerview;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.giovannibozzano.wakeblock.R;
import com.giovannibozzano.wakeblock.WakeBlock;
import com.giovannibozzano.wakeblock.database.DatabaseHelper;
import com.giovannibozzano.wakeblock.database.WakeLocksTable.WakeLockEntry;
import com.giovannibozzano.wakeblock.enums.WakeLockStatus;
import com.giovannibozzano.wakeblock.utils.Utils;
import com.giovannibozzano.wakeblock.utils.WakeLockData;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AdapterWakeLocks extends Adapter<HolderWakeLock>
{
	private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss, dd/MM/yy");
	private final DatabaseHelper databaseHelper;
	private List<String> wakelockTags;
	private List<WakeLockData> wakeLockData;
	private ActionMode actionMode;
	private ToolbarWakeLock actionModeCallback;
	private Integer lastSelected;

	public AdapterWakeLocks(DatabaseHelper databaseHelper, @NonNull Map<String, WakeLockData> wakeLockList)
	{
		this.databaseHelper = databaseHelper;
		this.wakelockTags = new ArrayList<>(wakeLockList.keySet());
		this.wakeLockData = new ArrayList<>(wakeLockList.values());
	}

	@Override
	@NonNull
	public HolderWakeLock onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
	{
		return new HolderWakeLock(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wakelock_entry, viewGroup, false));
	}

	@Override
	public void onBindViewHolder(@NonNull HolderWakeLock viewHolder, int position)
	{
		Context context = viewHolder.itemView.getContext();
		viewHolder.getTag().setText(this.wakelockTags.get(position));
		StringBuilder stringBuilder = new StringBuilder();
		for (int counter = 0; counter < this.wakeLockData.get(position).getAcquiringPackages().size(); counter++) {
			stringBuilder.append("\n- ");
			stringBuilder.append(this.wakeLockData.get(position).getAcquiringPackages().get(counter));
		}
		viewHolder.getAcquiringPackages().setText(viewHolder.itemView.getResources().getString(R.string.acquiring_packages) + ": " + stringBuilder.toString());
		if (this.wakeLockData.get(position).getIsBlocked()) {
			if (this.lastSelected != null && this.lastSelected == position) {
				viewHolder.getHolder().setBackgroundColor(Utils.themeAttributeToColor(R.attr.colorPrimaryDark, context));
			} else {
				viewHolder.getHolder().setBackgroundColor(Utils.themeAttributeToColor(R.attr.colorPrimary, context));
			}
			viewHolder.getTag().setTextColor(ContextCompat.getColor(context, R.color.md_grey_50));
			viewHolder.getAcquiringPackages().setTextColor(ContextCompat.getColor(context, R.color.md_grey_50));
		} else {
			if (this.lastSelected != null && this.lastSelected == position) {
				viewHolder.getHolder().setBackgroundColor(Utils.themeAttributeToColor(android.R.attr.colorControlHighlight, context));
			} else {
				viewHolder.getHolder().setBackgroundColor(Utils.themeAttributeToColor(android.R.attr.colorBackground, context));
			}
			viewHolder.getTag().setTextColor(Utils.themeAttributeToColor(android.R.attr.textColorSecondary, context));
			viewHolder.getAcquiringPackages().setTextColor(Utils.themeAttributeToColor(android.R.attr.textColorSecondary, context));
		}
		View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_wakelock_information, null);
		TextView tag = dialogView.findViewById(R.id.wakelock_tag);
		TextView occurrences = dialogView.findViewById(R.id.wakelock_occurrences);
		TextView timesBlocked = dialogView.findViewById(R.id.wakelock_times_blocked);
		TextView runTime = dialogView.findViewById(R.id.wakelock_runtime);
		TextView blockTime = dialogView.findViewById(R.id.wakelock_block_time);
		TextView lastAcquisition = dialogView.findViewById(R.id.wakelock_last_acquisition);
		tag.setText(this.wakelockTags.get(position));
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (sharedPreferences.getBoolean("boot_list", false)) {
			int seconds = (int) (this.wakeLockData.get(position).getRunTimeBoot() / 1000) % 60;
			int minutes = (int) ((this.wakeLockData.get(position).getRunTimeBoot() / 60000) % 60);
			int hours = (int) ((this.wakeLockData.get(position).getRunTimeBoot() / 3600000));
			occurrences.setText(viewHolder.itemView.getResources().getString(R.string.occurrences_since_boot) + ": " + this.wakeLockData.get(position).getOccurrencesBoot());
			timesBlocked.setText(viewHolder.itemView.getResources().getString(R.string.times_blocked_since_boot) + ": " + this.wakeLockData.get(position).getTimesBlockedBoot());
			runTime.setText(viewHolder.itemView.getResources().getString(R.string.run_time_since_boot) + ": " + hours + " " + viewHolder.itemView.getResources().getString(R.string.hours) + " " + minutes + " " + viewHolder.itemView.getResources().getString(R.string.minutes) + " " + seconds + " " + viewHolder.itemView.getResources().getString(R.string.seconds) + " " + (this.wakeLockData.get(position).getRunTimeBoot() % 1000) + " ms");
		} else {
			int seconds = (int) (this.wakeLockData.get(position).getRunTimeTotal() / 1000) % 60;
			int minutes = (int) ((this.wakeLockData.get(position).getRunTimeTotal() / 60000) % 60);
			int hours = (int) ((this.wakeLockData.get(position).getRunTimeTotal() / 3600000));
			occurrences.setText(viewHolder.itemView.getResources().getString(R.string.total_occurrences) + ": " + this.wakeLockData.get(position).getOccurrencesTotal());
			timesBlocked.setText(viewHolder.itemView.getResources().getString(R.string.total_times_blocked) + ": " + this.wakeLockData.get(position).getTimesBlockedTotal());
			runTime.setText(viewHolder.itemView.getResources().getString(R.string.total_run_time) + ": " + hours + " " + viewHolder.itemView.getResources().getString(R.string.hours) + " " + minutes + " " + viewHolder.itemView.getResources().getString(R.string.minutes) + " " + seconds + " " + viewHolder.itemView.getResources().getString(R.string.seconds) + " " + (this.wakeLockData.get(position).getRunTimeTotal() % 1000) + " ms");
		}
		int seconds = (int) (this.wakeLockData.get(position).getBlockTime() / 1000) % 60;
		int minutes = (int) ((this.wakeLockData.get(position).getBlockTime() / 60000) % 60);
		int hours = (int) ((this.wakeLockData.get(position).getBlockTime() / 3600000));
		blockTime.setText(viewHolder.itemView.getResources().getString(R.string.block_time) + ": " + hours + " " + viewHolder.itemView.getResources().getString(R.string.hours) + " " + minutes + " " + viewHolder.itemView.getResources().getString(R.string.minutes) + " " + seconds + " " + viewHolder.itemView.getResources().getString(R.string.seconds) + " " + (this.wakeLockData.get(position).getBlockTime() % 1000) + " ms");
		if (this.wakeLockData.get(position).getLastAcquisition() != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(this.wakeLockData.get(position).getLastAcquisition());
			lastAcquisition.setText(viewHolder.itemView.getResources().getString(R.string.last_acquisition) + ": " + formatter.format(calendar.getTime()));
		} else {
			lastAcquisition.setText(viewHolder.itemView.getResources().getString(R.string.last_acquisition) + ": N/A");
		}
		AlertDialog alertDialog = new MaterialAlertDialogBuilder(context).setView(dialogView).create();
		viewHolder.itemView.setOnLongClickListener(new WakeLockLongClickListener(alertDialog));
		viewHolder.getHolder().setOnClickListener(view -> {
			if (this.lastSelected == null || this.lastSelected != viewHolder.getAdapterPosition()) {
				if (this.lastSelected != null) {
					this.notifyItemChanged(this.lastSelected);
					this.actionModeCallback.reinitialize(viewHolder.getAdapterPosition(), alertDialog);
				} else {
					this.actionModeCallback = new ToolbarWakeLock(context, this, viewHolder.getAdapterPosition(), alertDialog);
					this.actionMode = ((AppCompatActivity) context).startSupportActionMode(this.actionModeCallback);
				}
				this.actionMode.setTitle(this.wakelockTags.get(viewHolder.getAdapterPosition()));
				if (this.wakeLockData.get(viewHolder.getAdapterPosition()).getIsBlocked()) {
					viewHolder.getHolder().setBackgroundColor(Utils.themeAttributeToColor(R.attr.colorPrimaryDark, context));
				} else {
					viewHolder.getHolder().setBackgroundColor(Utils.themeAttributeToColor(android.R.attr.colorControlHighlight, context));
				}
				this.lastSelected = viewHolder.getAdapterPosition();
			} else {
				this.lastSelected = null;
				this.actionMode.finish();
				if (this.wakeLockData.get(viewHolder.getAdapterPosition()).getIsBlocked()) {
					viewHolder.getHolder().setBackgroundColor(Utils.themeAttributeToColor(R.attr.colorPrimary, context));
				} else {
					viewHolder.getHolder().setBackgroundColor(Utils.themeAttributeToColor(android.R.attr.colorBackground, context));
				}
			}
		});
		viewHolder.getHolder().startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
	}

	@Override
	public int getItemCount()
	{
		return this.wakelockTags == null ? 0 : this.wakelockTags.size();
	}

	@Override
	public void onViewDetachedFromWindow(@NonNull HolderWakeLock viewHolder)
	{
		viewHolder.getHolder().clearAnimation();
	}

	public void blockWakeLock(int position, boolean block, long blockTime)
	{
		SQLiteDatabase database = this.databaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(WakeLockEntry.COLUMN_IS_BLOCKED, block ? 1 : 0);
		values.put(WakeLockEntry.COLUMN_BLOCK_TIME, blockTime);
		if (WakeBlock.getWakeBlockService().getLoadedWakeLocks() != null && WakeBlock.getWakeBlockService().getLoadedWakeLocks().size() > 0) {
			WakeLockData wakeLockData = WakeBlock.getWakeBlockService().getLoadedWakeLocks().get(this.wakelockTags.get(position));
			wakeLockData.setIsBlocked(block);
			wakeLockData.setBlockTime(blockTime);
			if (wakeLockData.getStatus() == WakeLockStatus.NOT_PENDING) {
				wakeLockData.setStatus(WakeLockStatus.PENDING_UPDATE);
			}
			if (wakeLockData.getStatus() == WakeLockStatus.PENDING_CREATION) {
				long now = System.currentTimeMillis();
				values.put(WakeLockEntry.COLUMN_TAG, this.wakelockTags.get(position));
				values.put(WakeLockEntry.COLUMN_OCCURRENCES, wakeLockData.getOccurrencesTotal());
				values.put(WakeLockEntry.COLUMN_TIMES_BLOCKED, wakeLockData.getTimesBlockedTotal());
				if (!wakeLockData.getLocks().isEmpty() && now >= wakeLockData.getLastAcquisition()) {
					wakeLockData.setRunTimeTotal(wakeLockData.getRunTimeTotal() + (now - wakeLockData.getLastRunTimeRefresh()));
					wakeLockData.setRunTimeBoot(wakeLockData.getRunTimeBoot() + (now - wakeLockData.getLastRunTimeRefresh()));
					wakeLockData.setLastRunTimeRefresh(now);
				}
				values.put(WakeLockEntry.COLUMN_RUN_TIME, wakeLockData.getRunTimeTotal());
				values.put(WakeLockEntry.COLUMN_ACQUIRING_PACKAGES, new Gson().toJson(wakeLockData.getAcquiringPackages()));
				database.insert(WakeLockEntry.TABLE_NAME, null, values);
			} else {
				database.update(WakeLockEntry.TABLE_NAME, values, WakeLockEntry.COLUMN_TAG + " LIKE ?", new String[] { this.wakelockTags.get(position) });
			}
			database.close();
			wakeLockData.setStatus(WakeLockStatus.NOT_PENDING);
		} else {
			database.update(WakeLockEntry.TABLE_NAME, values, WakeLockEntry.COLUMN_TAG + " LIKE ?", new String[] { this.wakelockTags.get(position) });
			database.close();
		}
		this.wakeLockData.get(position).setIsBlocked(block);
		this.wakeLockData.get(position).setBlockTime(blockTime);
		this.actionMode.finish();
		this.lastSelected = null;
		this.notifyItemChanged(position);
	}

	void deleteWakeLock(int position)
	{
		if (WakeBlock.getWakeBlockService().getLoadedWakeLocks() != null && WakeBlock.getWakeBlockService().getLoadedWakeLocks().size() > 0) {
			WakeBlock.getWakeBlockService().getLoadedWakeLocks().remove(this.wakelockTags.get(position));
		}
		SQLiteDatabase database = this.databaseHelper.getWritableDatabase();
		database.delete(WakeLockEntry.TABLE_NAME, WakeLockEntry.COLUMN_TAG + " LIKE ?", new String[] { this.wakelockTags.get(position) });
		database.close();
		this.wakelockTags.remove(position);
		this.wakeLockData.remove(position);
		this.actionMode.finish();
		this.lastSelected = null;
		this.notifyItemRemoved(position);
	}

	public void setWakeLockList(Map<String, WakeLockData> wakeLockList)
	{
		this.wakelockTags = new ArrayList<>(wakeLockList.keySet());
		this.wakeLockData = new ArrayList<>(wakeLockList.values());
	}

	List<WakeLockData> getWakeLockData()
	{
		return this.wakeLockData;
	}

	public ActionMode getActionMode()
	{
		return this.actionMode;
	}

	public void setLastSelected(Integer lastSelected)
	{
		this.lastSelected = lastSelected;
	}

	public Integer getLastSelected()
	{
		return this.lastSelected;
	}

	class WakeLockLongClickListener implements OnLongClickListener
	{
		private final AlertDialog alertDialog;

		WakeLockLongClickListener(AlertDialog alertDialog)
		{
			this.alertDialog = alertDialog;
		}

		@Override
		public boolean onLongClick(View view)
		{
			this.alertDialog.show();
			return true;
		}
	}
}
