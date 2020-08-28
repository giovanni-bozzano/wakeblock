package com.giovannibozzano.wakeblock.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;

import com.giovannibozzano.wakeblock.R;
import com.giovannibozzano.wakeblock.exceptions.PatchException;
import com.giovannibozzano.wakeblock.layout.fragments.FragmentHome;
import com.giovannibozzano.wakeblock.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RenderFragmentHome extends AsyncTask<Void, Void, Void>
{
	private final WeakReference<FragmentHome> fragmentWeakReference;
	private final String coreModPath;

	public RenderFragmentHome(FragmentHome fragmentHome, String coreModPath)
	{
		this.fragmentWeakReference = new WeakReference<>(fragmentHome);
		this.coreModPath = coreModPath;
	}

	@Override
	protected Void doInBackground(Void... voids)
	{
		FragmentHome fragment = this.fragmentWeakReference.get();
		if (fragment == null) {
			return null;
		}
		Activity activity = fragment.getActivity();
		if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
			return null;
		}
		File backupFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/WakeBlock/Backups/");
		Date latestBackupDate = null;
		long currentCRC32;
		try {
			currentCRC32 = Utils.crc32(this.coreModPath);
			long alternateCurrentCRC32 = currentCRC32;
			if (!this.coreModPath.equals("/system/framework/services.jar")) {
				alternateCurrentCRC32 = Utils.crc32("/system/framework/services.jar");
			}
			if (backupFolder.exists() && backupFolder.isDirectory() && backupFolder.listFiles().length > 0) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
				for (File backup : backupFolder.listFiles()) {
					boolean containsFile = false;
					boolean alternate = false;
					for (File file : backup.listFiles()) {
						if (file.getName().equals(currentCRC32 + ".zip")) {
							containsFile = true;
							break;
						} else if (file.getName().equals(alternateCurrentCRC32 + ".zip")) {
							containsFile = true;
							alternate = true;
						}
					}
					if (!containsFile) {
						continue;
					}
					try {
						Date date = simpleDateFormat.parse(backup.getName());
						if (latestBackupDate == null || latestBackupDate.before(date)) {
							latestBackupDate = date;
							fragment.setLatestBackup(backup.getAbsoluteFile() + "/" + (alternate ? alternateCurrentCRC32 : currentCRC32) + ".zip");
						}
					} catch (ParseException exception) {
						exception.printStackTrace();
					}
				}
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
		FragmentHome fragment = this.fragmentWeakReference.get();
		if (fragment == null) {
			return;
		}
		Activity activity = fragment.getActivity();
		if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
			return;
		}
		if (fragment.getLatestBackup() == null) {
			return;
		}
		fragment.getPatcherContainer().setOnClickListener(null);
		fragment.getPatcherText().setText(activity.getString(R.string.core_mod_already_installed));
		fragment.getUninstallCard().setVisibility(View.VISIBLE);
		fragment.getUninstallContainer().setOnClickListener(currentView -> {
			new MaterialAlertDialogBuilder(fragment.getActivity()).setTitle(this.fragmentWeakReference.get().getResources().getString(R.string.dialog_uninstall_title)).setMessage(this.fragmentWeakReference.get().getResources().getString(R.string.dialog_uninstall_message)).setPositiveButton(this.fragmentWeakReference.get().getResources().getString(R.string.confirm), (alertDialog, which) -> {
				try {
					Utils.rebootAndFlash(activity, fragment.getLatestBackup());
				} catch (PatchException exception) {
					exception.printStackTrace();
				}
			}).setNegativeButton(this.fragmentWeakReference.get().getResources().getString(R.string.cancel), (alertDialog, which) -> {
			}).create().show();
		});
	}
}
