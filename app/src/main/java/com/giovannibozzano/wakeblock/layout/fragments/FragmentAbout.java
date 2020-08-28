package com.giovannibozzano.wakeblock.layout.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.giovannibozzano.wakeblock.BuildConfig;
import com.giovannibozzano.wakeblock.R;
import com.giovannibozzano.wakeblock.tasks.ResourceUpdate;

import java.util.Objects;

public class FragmentAbout extends FragmentInitializable
{
	private TextView aboutView;
	private TextView patronView;

	@Override
	public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState)
	{
		View view = layoutInflater.inflate(R.layout.fragment_about, viewGroup, false);
		SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refreshFaq);
		this.aboutView = view.findViewById(R.id.aboutView);
		this.aboutView.setMovementMethod(LinkMovementMethod.getInstance());
		this.patronView = view.findViewById(R.id.patronView);
		this.patronView.setMovementMethod(LinkMovementMethod.getInstance());
		swipeRefreshLayout.setColorSchemeResources(R.color.md_green_600);
		swipeRefreshLayout.setOnRefreshListener(() -> {
			new ResourceUpdate(this).execute();
			swipeRefreshLayout.setRefreshing(false);
		});
		initialize();
		return view;
	}

	public void initialize()
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(this.getContext()));
		String text = sharedPreferences.getString("patrons", String.valueOf(R.string.process_wait));
		this.aboutView.setText(Html.fromHtml("<p><strong>Version " + BuildConfig.VERSION_NAME + "</strong></p><br />" + "<p>Giovanni Bozzano - Main Developer & Project Manager</p><p>Emanuele Giunta - Developer, Tester and Support</p><p>Luke Nolet - Official Tester</p>", Html.FROM_HTML_MODE_COMPACT));
		this.patronView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
		this.aboutView.invalidate();
		this.patronView.invalidate();
	}
}
