package com.giovannibozzano.wakeblock.layout.activities;

import android.Manifest.permission;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.giovannibozzano.wakeblock.R;
import com.giovannibozzano.wakeblock.utils.ExecuteAsRoot;
import com.giovannibozzano.wakeblock.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ActivitySplash extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (sharedPreferences.getBoolean("dark_mode", false)) {
			this.setTheme(R.style.AppTheme_Dark);
		}
		super.onCreate(savedInstanceState);
		Utils.bindService(this);
		if (this.checkSelfPermission(permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this).setCancelable(false);
			alertDialogBuilder.setTitle(this.getResources().getString(R.string.storage_permissions_denied)).setMessage(this.getResources().getString(R.string.storage_permissions_denied_explanation)).setNeutralButton(this.getResources().getString(R.string.storage_permissions_denied_ok), (alertDialog, which) -> {
				this.requestPermissions(new String[] { permission.WRITE_EXTERNAL_STORAGE }, 1);
				if (this.checkSelfPermission(permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
					Toast.makeText(this.getApplicationContext(), "Stop fooling around! Grant me dat permission!", Toast.LENGTH_SHORT);
					alertDialogBuilder.setNegativeButton(this.getResources().getString(R.string.ok), (alertDialog1, which1) -> Toast.makeText(this.getApplicationContext(), "It is done.", Toast.LENGTH_SHORT).show());
				} else {
					Toast.makeText(this.getApplicationContext(), "Thanks!", Toast.LENGTH_SHORT).show();
				}
			}).create().show();
		} else {
			if (!ExecuteAsRoot.canRunRootCommands()) {
				new MaterialAlertDialogBuilder(this).setCancelable(false).setTitle("Root access required").setMessage("WakeBlock requires root to run.\nThe application will now close.").setNeutralButton("OK", (alertDialog, which) -> this.finish()).create().show();
				return;
			}
			this.startActivity(new Intent(this, ActivityMain.class));
			this.finish();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
			this.finish();
			return;
		}
		if (!ExecuteAsRoot.canRunRootCommands()) {
			new MaterialAlertDialogBuilder(this).setCancelable(false).setTitle("Root access required").setMessage("WakeBlock requires root to run.\nThe application will now close.").setNeutralButton("OK", (alertDialog, which) -> this.finish()).create().show();
			return;
		}
		this.startActivity(new Intent(this, ActivityMain.class));
		this.finish();
	}
}
