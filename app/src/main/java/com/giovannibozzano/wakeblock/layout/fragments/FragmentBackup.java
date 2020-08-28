package com.giovannibozzano.wakeblock.layout.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.giovannibozzano.wakeblock.R;
import com.giovannibozzano.wakeblock.tasks.BBWakelock;
import com.giovannibozzano.wakeblock.tasks.RBWakeLock;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentBackup extends Fragment
{
	@BindView(R.id.backup) CardView backupButton;
	@BindView(R.id.restore) CardView restoreButton;

	@Override
	public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState)
	{
		View view = layoutInflater.inflate(R.layout.fragment_backup, viewGroup, false);
		ButterKnife.bind(this, view);
		FragmentBackup.this.backupButton.findViewById(R.id.backup);
		FragmentBackup.this.restoreButton.findViewById(R.id.restore);
		initialize();
		return view;
	}

	private void initialize()
	{
		this.backupButton.setOnClickListener(v -> new BBWakelock(this.getActivity()).execute());
		this.restoreButton.setOnClickListener(v -> new RBWakeLock(this.getActivity()).execute());
	}
}
