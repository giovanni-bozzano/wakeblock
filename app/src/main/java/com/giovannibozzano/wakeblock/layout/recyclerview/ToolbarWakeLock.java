package com.giovannibozzano.wakeblock.layout.recyclerview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.ActionMode.Callback;

import com.giovannibozzano.wakeblock.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

class ToolbarWakeLock implements Callback
{
	private final Context context;
	private final AdapterWakeLocks adapterWakeLocks;
	private int position;
	private AlertDialog alertDialog;
	private ActionMode actionMode;
	private Menu menu;

	ToolbarWakeLock(Context context, AdapterWakeLocks adapterWakeLocks, int position, AlertDialog alertDialog)
	{
		this.context = context;
		this.adapterWakeLocks = adapterWakeLocks;
		this.position = position;
		this.alertDialog = alertDialog;
	}

	@Override
	public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
	{
		this.actionMode = actionMode;
		this.menu = menu;
		actionMode.getMenuInflater().inflate(R.menu.menu_wakelock_selected, menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
	{
		if (!this.adapterWakeLocks.getWakeLockData().get(this.position).getIsBlocked()) {
			menu.findItem(R.id.wakelock_edit_time).setVisible(false);
			menu.findItem(R.id.wakelock_block_unblock).setIcon(this.context.getDrawable(R.drawable.ic_wakelock_block));
			menu.findItem(R.id.wakelock_block_unblock).setTitle("Block");
		} else {
			menu.findItem(R.id.wakelock_edit_time).setVisible(true);
			menu.findItem(R.id.wakelock_block_unblock).setIcon(this.context.getDrawable(R.drawable.ic_wakelock_unblock));
			menu.findItem(R.id.wakelock_block_unblock).setTitle("Unblock");
		}
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
	{
		switch (menuItem.getItemId()) {
			case R.id.wakelock_edit_time:
				if (!this.adapterWakeLocks.getWakeLockData().get(this.position).getIsBlocked()) {
					break;
				}
				AlertDialog alertDialog = new MaterialAlertDialogBuilder(this.context).setView(LayoutInflater.from(this.context).inflate(R.layout.dialog_wakelock_block, null)).setPositiveButton(context.getResources().getString(R.string.confirm), (alertDialogEditTime, which) -> {
					long blockTime = 0;
					try {
						EditText blockTimeText = ((AlertDialog) alertDialogEditTime).findViewById(R.id.block_time);
						if (blockTimeText != null) {
							blockTime = Long.parseLong(blockTimeText.getText().toString());
						}
					} catch (NumberFormatException exception) {
						Log.e(this.context.getPackageName(), "Wrong number format");
					}
					this.adapterWakeLocks.blockWakeLock(this.position, true, blockTime);
				}).setNegativeButton(context.getResources().getString(R.string.cancel), null).create();
				alertDialog.show();
				EditText blockTimeTextEditTime = alertDialog.findViewById(R.id.block_time);
				if (blockTimeTextEditTime != null) {
					blockTimeTextEditTime.setText(String.format(Long.toString(this.adapterWakeLocks.getWakeLockData().get(this.position).getBlockTime()), Locale.getDefault()));
					blockTimeTextEditTime.setSelection(blockTimeTextEditTime.getText().length());
				}
				break;
			case R.id.wakelock_block_unblock:
				if (!this.adapterWakeLocks.getWakeLockData().get(this.position).getIsBlocked()) {
					new MaterialAlertDialogBuilder(this.context).setView(LayoutInflater.from(this.context).inflate(R.layout.dialog_wakelock_block, null)).setTitle("Block WakeLock").setPositiveButton(context.getResources().getString(R.string.confirm), (alertDialogBlock, which) -> {
						long blockTime = 0;
						try {
							EditText blockTimeText = ((AlertDialog) alertDialogBlock).findViewById(R.id.block_time);
							if (blockTimeText != null) {
								blockTime = Long.parseLong(blockTimeText.getText().toString());
							}
						} catch (NumberFormatException exception) {
							Log.e(this.context.getPackageName(), "Wrong number format");
						}
						this.adapterWakeLocks.blockWakeLock(this.position, true, blockTime);
					}).setNegativeButton(context.getResources().getString(R.string.cancel), null).create().show();
				} else {
					new MaterialAlertDialogBuilder(this.context).setTitle(context.getResources().getString(R.string.unblock_wakelock)).setPositiveButton(context.getResources().getString(R.string.confirm), (alertDialogUnblock, which) -> this.adapterWakeLocks.blockWakeLock(this.position, false, 0)).setNegativeButton(context.getResources().getString(R.string.cancel), null).create().show();
				}
				break;
			case R.id.wakelock_information:
				this.alertDialog.show();
				break;
			case R.id.wakelock_delete:
				new MaterialAlertDialogBuilder(this.context).setTitle("Delete WakeLock").setPositiveButton(this.context.getResources().getString(R.string.confirm), (alertDialogDelete, which) -> this.adapterWakeLocks.deleteWakeLock(this.position)).setNegativeButton(this.context.getResources().getString(R.string.cancel), null).create().show();
				break;
			default:
				break;
		}
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode actionMode)
	{
		if (this.adapterWakeLocks.getLastSelected() != null) {
			this.adapterWakeLocks.notifyItemChanged(this.adapterWakeLocks.getLastSelected());
			this.adapterWakeLocks.setLastSelected(null);
		}
	}

	void reinitialize(int position, AlertDialog alertDialog)
	{
		this.position = position;
		this.alertDialog = alertDialog;
		if (this.actionMode != null && this.menu != null) {
			this.onPrepareActionMode(this.actionMode, this.menu);
		}
	}
}
