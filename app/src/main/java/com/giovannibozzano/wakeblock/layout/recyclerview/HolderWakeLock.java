package com.giovannibozzano.wakeblock.layout.recyclerview;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.giovannibozzano.wakeblock.R;

public class HolderWakeLock extends ViewHolder
{
	private final LinearLayout holder;
	private final TextView tag;
	private final TextView footerAcquiringPackages;

	HolderWakeLock(View view)
	{
		super(view);
		this.holder = view.findViewById(R.id.wakelock_holder);
		this.tag = view.findViewById(R.id.wakelock_tag);
		this.footerAcquiringPackages = view.findViewById(R.id.wakelock_footer_acquiring_packages);
	}

	public LinearLayout getHolder()
	{
		return this.holder;
	}

	public TextView getTag()
	{
		return this.tag;
	}

	public TextView getAcquiringPackages()
	{
		return this.footerAcquiringPackages;
	}
}
