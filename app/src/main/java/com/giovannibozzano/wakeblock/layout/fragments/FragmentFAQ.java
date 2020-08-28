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

import com.giovannibozzano.wakeblock.R;
import com.giovannibozzano.wakeblock.tasks.ResourceUpdate;

public class FragmentFAQ extends FragmentInitializable
{
	private TextView textView;

	@Override
	public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState)
	{
		View view = layoutInflater.inflate(R.layout.fragment_faq, viewGroup, false);
		SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refreshFaq);
		this.textView = view.findViewById(R.id.faqView);
		this.textView.setMovementMethod(LinkMovementMethod.getInstance());
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
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
		String text = sharedPreferences.getString("faqs", String.valueOf(R.string.process_wait));
		this.textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
		this.textView.invalidate();
	}
}