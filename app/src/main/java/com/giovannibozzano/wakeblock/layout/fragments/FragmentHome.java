package com.giovannibozzano.wakeblock.layout.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.giovannibozzano.wakeblock.R;
import com.giovannibozzano.wakeblock.WakeBlock;
import com.giovannibozzano.wakeblock.enums.InstallMode;
import com.giovannibozzano.wakeblock.patcher.PatcherError;
import com.giovannibozzano.wakeblock.patcher.PatcherService;
import com.giovannibozzano.wakeblock.tasks.RenderFragmentHome;
import com.giovannibozzano.wakeblock.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class FragmentHome extends Fragment
{
	@BindView(R.id.core_mod_version_text) TextView coreModVersionText;
	@BindView(R.id.service_status_image) ImageView serviceStatusImage;
	@BindView(R.id.patcher_image) ImageView servicePatchImage;
	@BindView(R.id.service_status_text) TextView serviceStatusText;
	@BindView(R.id.service_status_container) FrameLayout serviceStatusContainer;
	@BindView(R.id.patcher_text) TextView patcherText;
	@BindView(R.id.patcher_container) FrameLayout patcherContainer;
	@BindView(R.id.uninstall_container) FrameLayout uninstallContainer;
	@BindView(R.id.uninstall_card) CardView uninstallCard;
	private String coreModPath;
	private String latestBackup;
	private boolean serviceBound = false;
	private ServiceConnection patcherServiceConnection;
	private PatcherService patcherService;
	private Disposable wakeBlockServiceDisposable;
	private Disposable patcherServiceDisposable;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (new File("/system/framework/oat/arm/services.odex").exists()) {
			this.coreModPath = "/system/framework/oat/arm/services.odex";
		} else if (new File("/system/framework/oat/arm64/services.odex").exists()) {
			this.coreModPath = "/system/framework/oat/arm64/services.odex";
		} else if (new File("/system/framework/services.jar").exists()) {
			this.coreModPath = "/system/framework/services.jar";
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle)
	{
		View view = layoutInflater.inflate(R.layout.fragment_home, viewGroup, false);
		ButterKnife.bind(this, view);
		if (this.getContext() == null) {
			return view;
		}
		this.serviceStatusContainer.setBackgroundColor(this.getContext().getColor(R.color.md_red_500));
		this.serviceStatusImage.setImageResource(R.drawable.ic_service_unbound);
		this.servicePatchImage.findViewById(R.id.patcher_image);
		this.serviceStatusText.setText(this.getResources().getString(R.string.service_unbound));
		this.patcherText.setText(this.getResources().getString(R.string.install_core_mod));
		if (this.coreModPath != null) {
			switch (this.coreModPath) {
				case "/system/framework/oat/arm/services.odex":
					this.coreModVersionText.setText(this.getResources().getString(R.string.arm_device));
					break;
				case "/system/framework/oat/arm64/services.odex":
					this.coreModVersionText.setText(this.getResources().getString(R.string.arm64_device));
					break;
				case "/system/framework/services.jar":
					this.coreModVersionText.setText(this.getResources().getString(R.string.deodexed_rom));
					break;
				default:
					break;
			}
			this.patcherContainer.setBackgroundColor(this.getContext().getColor(R.color.md_grey_500));
		} else {
			this.coreModVersionText.setText(this.getResources().getString(R.string.core_file_not_found));
			this.patcherContainer.setBackgroundColor(this.getContext().getColor(R.color.md_red_500));
		}
		if (this.coreModPath == null) {
			this.patcherText.setText(this.getResources().getString(R.string.unsupported_framework));
		}
		// connect to patcher service to know if it is working
		this.patcherServiceConnection = new ServiceConnection()
		{
			@Override
			public void onServiceConnected(ComponentName componentName, IBinder binder)
			{
				if (FragmentHome.this.getContext() == null) {
					return;
				}
				FragmentHome.this.patcherService = ((PatcherService.LocalBinder) binder).getService();
				// keep watching for patching errors to display
				FragmentHome.this.patcherServiceDisposable = FragmentHome.this.patcherService.observeStatus().observeOn(AndroidSchedulers.mainThread()).subscribe(status -> {
					if (status instanceof PatcherError) {
						FragmentHome.this.patcherContainer.setBackgroundColor(FragmentHome.this.patcherService.getColor(R.color.md_red_500));
						FragmentHome.this.patcherText.setText(((PatcherError) status).getMessage());
					} else {
						// Service exited normally
						FragmentHome.this.patcherContainer.setBackgroundColor(FragmentHome.this.patcherService.getColor(R.color.md_green_500));
						FragmentHome.this.patcherText.setText("Done! Check the WakeBlock folder!");
					}
					FragmentHome.this.servicePatchImage.clearAnimation();
				});
				// check if WakeBlock main service is bound to the system
				if (WakeBlock.getWakeBlockService() != null && WakeBlock.getWakeBlockService().fetchBinding()) {
					// if it is, update UI
					FragmentHome.this.serviceBound = true;
					FragmentHome.this.serviceStatusContainer.setBackgroundColor(ContextCompat.getColor(FragmentHome.this.getContext(), R.color.md_green_500));
					FragmentHome.this.serviceStatusImage.setImageResource(R.drawable.ic_service_bound);
					FragmentHome.this.serviceStatusText.setText(FragmentHome.this.getResources().getString(R.string.service_bound));
					FragmentHome.this.patcherContainer.setOnClickListener(null);
					FragmentHome.this.patcherText.setText(FragmentHome.this.getResources().getString(R.string.core_mod_already_installed));
					if (WakeBlock.isCoreModVersionChecked() && WakeBlock.getCoreModVersion() != Utils.CORE_MOD_VERSION) {
						new MaterialAlertDialogBuilder(FragmentHome.this.getContext()).setTitle("Outdated Core Mod").setMessage("The WakeBlock core mod is outdated. Please uninstall and reinstall the mod to update it.\n\nNote that until the core mod is updated WakeBlock will not block or register any wakelock.\n\nInstalled core mod version: " + WakeBlock.getCoreModVersion() + "\nAvailable core mod version: " + Utils.CORE_MOD_VERSION).setNeutralButton("OK", null).create().show();
					}
				} else {
					// if it is not, allow patching...
					FragmentHome.this.patcherContainer.setOnClickListener(currentView -> {
						if (FragmentHome.this.patcherService.isProcessing()) {
							return;
						}
						new MaterialAlertDialogBuilder(FragmentHome.this.getContext()).setTitle(R.string.choice_title).setItems(new CharSequence[] { FragmentHome.this.getString(R.string.choice_reboot), FragmentHome.this.getString(R.string.choice_noreboot), FragmentHome.this.getString(R.string.choice_magisk), FragmentHome.this.getString(R.string.choice_error) }, (dialog, which) -> {
							if (which == 3) {
								Toast.makeText(FragmentHome.this.getContext(), FragmentHome.this.getString(R.string.choice_cancel), Toast.LENGTH_SHORT).show();
								return;
							}
							if (FragmentHome.this.patcherService == null) {
								return;
							}
							IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
							Intent batteryStatus = FragmentHome.this.getContext().registerReceiver(null, filter);
							if (which == 2) {
								new MaterialAlertDialogBuilder(FragmentHome.this.getContext()).setTitle("WARNING").setMessage(R.string.magisk_warning).setNeutralButton(R.string.magisk_warning_button, null).create().show();
							}
							if (batteryStatus != null && batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1) != BatteryManager.BATTERY_STATUS_CHARGING && batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) / (float) batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) < 0.25) {
								new MaterialAlertDialogBuilder(FragmentHome.this.getContext()).setTitle("WARNING").setMessage("We detected that your battery is lower than 25%. The patching process needs quite a bit of resources and it could take up to 30 minutes. We highly suggest that you plug in your device to the charger before patching! But anyway, the choice is yours: ").setPositiveButton("I won't blame you if everything explodes.", (batteryDialog, batteryWhich) -> FragmentHome.this.patcherService.execute(FragmentHome.this.coreModPath, InstallMode.getFromId(which))).setPositiveButton("Oh. Wait, I'll fix this.", null).create().show();
							} else {
								FragmentHome.this.getContext().startService(new Intent(FragmentHome.this.getContext(), PatcherService.class));
								FragmentHome.this.patcherService.execute(FragmentHome.this.coreModPath, InstallMode.getFromId(which));
								Toast.makeText(FragmentHome.this.getContext(), "Patching process is starting...", Toast.LENGTH_LONG).show();
								FragmentHome.this.patcherContainer.setBackgroundColor(FragmentHome.this.patcherService.getColor(R.color.md_blue_500));
								Animation animation = AnimationUtils.loadAnimation(FragmentHome.this.getContext(), R.anim.rotation);
								FragmentHome.this.servicePatchImage.startAnimation(animation);
								FragmentHome.this.patcherText.setText("Working...");
							}
						}).create().show();
					});
					// ...and keep watching for WakeBlock main service system binding
					if (WakeBlock.getWakeBlockService() == null) {
						new MaterialAlertDialogBuilder(FragmentHome.this.getContext()).setTitle("WARNING").setMessage("The WakeBlock main service is being killed by the system. Please report this to the developers.").setNeutralButton("OK", null).create().show();
					} else {
						FragmentHome.this.wakeBlockServiceDisposable = WakeBlock.getWakeBlockService().observeBinding().observeOn(AndroidSchedulers.mainThread()).subscribe(bound -> {
							if (!FragmentHome.this.serviceBound && bound) {
								FragmentHome.this.serviceBound = true;
								FragmentHome.this.serviceStatusContainer.setBackgroundColor(ContextCompat.getColor(FragmentHome.this.getContext(), R.color.md_green_500));
								FragmentHome.this.serviceStatusImage.setImageResource(R.drawable.ic_service_bound);
								FragmentHome.this.serviceStatusText.setText(FragmentHome.this.getString(R.string.service_bound));
								FragmentHome.this.patcherContainer.setOnClickListener(null);
								FragmentHome.this.patcherText.setText(FragmentHome.this.getString(R.string.core_mod_already_installed));
							}
						});
						FragmentHome.this.wakeBlockServiceDisposable = WakeBlock.getWakeBlockService().observeOutdatedCoreMod().observeOn(AndroidSchedulers.mainThread()).subscribe(outdated -> {
							if (outdated) {
								new MaterialAlertDialogBuilder(FragmentHome.this.getContext()).setTitle("Outdated Core Mod").setMessage("The WakeBlock core mod is outdated. Please uninstall and reinstall the mod to update it.\n\nNote that until the core mod is updated WakeBlock will not block or register any wakelock.\n\nInstalled core mod version: " + WakeBlock.getCoreModVersion() + "\nAvailable core mod version: " + Utils.CORE_MOD_VERSION).setNeutralButton("OK", null).create().show();
							}
						});
					}
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName componentName)
			{
				if (FragmentHome.this.patcherServiceDisposable != null) {
					FragmentHome.this.patcherServiceDisposable.dispose();
				}
			}
		};
		viewGroup.getContext().bindService(new Intent(FragmentHome.this.getContext(), PatcherService.class), this.patcherServiceConnection, Context.BIND_AUTO_CREATE);
		new RenderFragmentHome(this, this.coreModPath).execute();
		return view;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (this.getContext() == null) {
			return;
		}
		if (this.wakeBlockServiceDisposable != null) {
			this.wakeBlockServiceDisposable.dispose();
		}
		if (this.patcherServiceDisposable != null) {
			this.patcherServiceDisposable.dispose();
		}
		if (this.patcherServiceConnection != null) {
			this.getContext().unbindService(this.patcherServiceConnection);
		}
	}

	public TextView getPatcherText()
	{
		return this.patcherText;
	}

	public FrameLayout getPatcherContainer()
	{
		return this.patcherContainer;
	}

	public FrameLayout getUninstallContainer()
	{
		return this.uninstallContainer;
	}

	public CardView getUninstallCard()
	{
		return this.uninstallCard;
	}

	public String getLatestBackup()
	{
		return this.latestBackup;
	}

	public void setLatestBackup(String latestBackup)
	{
		this.latestBackup = latestBackup;
	}
}
