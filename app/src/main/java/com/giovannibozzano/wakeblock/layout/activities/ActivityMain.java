package com.giovannibozzano.wakeblock.layout.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.giovannibozzano.wakeblock.R;
import com.giovannibozzano.wakeblock.WakeBlock;
import com.giovannibozzano.wakeblock.layout.fragments.FragmentAbout;
import com.giovannibozzano.wakeblock.layout.fragments.FragmentBackup;
import com.giovannibozzano.wakeblock.layout.fragments.FragmentDetectedWakeLocks;
import com.giovannibozzano.wakeblock.layout.fragments.FragmentFAQ;
import com.giovannibozzano.wakeblock.layout.fragments.FragmentHome;
import com.giovannibozzano.wakeblock.layout.fragments.FragmentSettings;
import com.giovannibozzano.wakeblock.tasks.ResourceUpdate;
import com.giovannibozzano.wakeblock.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

public class ActivityMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
	private final Handler drawerHandler = new Handler();
	private int drawerItem;
	private boolean fromShortcut = true;
	private DrawerLayout drawerLayout;
	private NavigationView navigationView;
	private Runnable pendingRunnable;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
		if (sharedPreferences.getBoolean("dark_mode", false)) {
			this.setTheme(R.style.AppTheme_Dark);
		}
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		this.setSupportActionBar(toolbar);
		this.drawerLayout = findViewById(R.id.drawer_layout);
		this.navigationView = findViewById(R.id.navigation_view);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, this.drawerLayout, toolbar, R.string.app_name, R.string.app_name)
		{
			@Override
			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
				super.onDrawerSlide(drawerView, 0);
			}

			@Override
			public void onDrawerClosed(@NonNull View view)
			{
				if (ActivityMain.this.pendingRunnable != null) {
					ActivityMain.this.invalidateOptionsMenu();
					ActivityMain.this.drawerHandler.post(ActivityMain.this.pendingRunnable);
					ActivityMain.this.pendingRunnable = null;
				}
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset)
			{
				super.onDrawerSlide(drawerView, 0);
			}
		};
		this.drawerLayout.addDrawerListener(toggle);
		toggle.syncState();
		this.navigate(R.id.fragment_home, false, false);
		this.navigationView.setNavigationItemSelectedListener(this);
		if (!WakeBlock.isInitialized()) {
			WakeBlock.setInitialized(true);
		} else {
			return;
		}
		try {
			Utils.getDeviceArchitecture();
		} catch (RuntimeException exception) {
			new MaterialAlertDialogBuilder(this.getApplicationContext()).setTitle(this.getResources().getString(R.string.dialog_architecture_title)).setMessage(this.getResources().getString(R.string.dialog_architecture_message)).setNeutralButton(this.getResources().getString(R.string.ok), null).create().show();
		}
		if (Utils.CHANGELOG) {
			new MaterialAlertDialogBuilder(this.getApplicationContext()).setTitle(this.getResources().getString(R.string.dialog_changelog_title)).setMessage(this.getResources().getString(R.string.dialog_changelog_message)).setPositiveButton(this.getResources().getString(R.string.dialog_changelog_do_not_ask_again), (alertDialog, which) -> {
				SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit();
				sharedPreferencesEditor.putBoolean("changelog_viewed", true);
				sharedPreferencesEditor.apply();
			}).setNegativeButton(this.getResources().getString(R.string.ok), (alertDialog, which) -> this.finish()).create().show();
		}
		new ResourceUpdate(this).execute();
	}

	@Override
	public void onBackPressed()
	{
		if (this.drawerLayout.isDrawerOpen(this.navigationView)) {
			this.drawerLayout.closeDrawer(this.navigationView);
		} else if (this.drawerItem != R.id.fragment_home && !fromShortcut) {
			this.navigate(R.id.fragment_home, true, false);
		} else {
			this.finish();
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem)
	{
		this.drawerHandler.removeCallbacksAndMessages(null);
		if (menuItem.getItemId() != this.drawerItem) {
			this.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.placeholder, R.anim.slide_out_right).hide(this.getSupportFragmentManager().findFragmentByTag("latest_fragment")).commit();
			this.pendingRunnable = () -> this.navigate(menuItem.getItemId(), true, true);
			this.drawerLayout.closeDrawer(this.navigationView);
		}
		return true;
	}

	private void navigate(int itemId, boolean animation, boolean backPressed)
	{
		this.navigationView.setCheckedItem(itemId);
		switch (itemId) {
			case R.id.fragment_home:
				this.fromShortcut = false;
				this.drawerItem = itemId;
				this.displayFragment(new FragmentHome(), animation, backPressed);
				break;
			case R.id.fragment_detected_wakelocks:
				this.drawerItem = itemId;
				this.displayFragment(new FragmentDetectedWakeLocks(), animation, backPressed);
				break;
			case R.id.fragment_backup:
				this.drawerItem = itemId;
				this.displayFragment(new FragmentBackup(), animation, backPressed);
				break;
			case R.id.fragment_faq:
				this.drawerItem = itemId;
				this.displayFragment(new FragmentFAQ(), animation, backPressed);
				break;
			case R.id.fragment_about:
				this.drawerItem = itemId;
				this.displayFragment(new FragmentAbout(), animation, backPressed);
				break;
			case R.id.fragment_settings:
				this.drawerItem = itemId;
				this.displayFragment(new FragmentSettings(), animation, backPressed);
				break;
			default:
				break;
		}
	}

	private void displayFragment(@NonNull Fragment fragment, boolean animation, boolean backPressed)
	{
		this.supportInvalidateOptionsMenu();
		FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
		if (animation) {
			if (backPressed) {
				fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.placeholder);
			} else {
				fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
			}
		}
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.content, fragment, "latest_fragment").commit();
	}
}
