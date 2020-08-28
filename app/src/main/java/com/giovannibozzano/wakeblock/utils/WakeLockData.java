package com.giovannibozzano.wakeblock.utils;

import android.os.IBinder;

import com.giovannibozzano.wakeblock.enums.WakeLockStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WakeLockData extends WakeLockJSONData
{
	private final List<IBinder> locks = Collections.synchronizedList(new ArrayList<>());
	private int occurrencesTotal;
	private int occurrencesBoot;
	private int timesBlockedTotal;
	private int timesBlockedBoot;
	private long runTimeTotal;
	private long runTimeBoot;
	private long lastRunTimeRefresh;
	private Long lastAcquisition;
	private WakeLockStatus status;

	public WakeLockData(int occurrencesTotal, int timesBlockedTotal, long runTimeTotal, Long lastAcquisition, List<String> acquiringPackages, boolean isBlocked, long blockTime, WakeLockStatus status)
	{
		super(acquiringPackages, blockTime, isBlocked);
		this.occurrencesTotal = occurrencesTotal;
		this.timesBlockedTotal = timesBlockedTotal;
		this.runTimeTotal = runTimeTotal;
		this.lastRunTimeRefresh = lastAcquisition != null ? lastAcquisition : 0;
		this.lastAcquisition = lastAcquisition;
		this.status = status;
	}

	int compareTo(WakeLockData wakeLockData, int orderType, boolean bootList)
	{
		if (bootList) {
			switch (orderType) {
				case Utils.ORDER_BY_OCCURRENCES:
					if (this.occurrencesBoot > wakeLockData.getOccurrencesBoot()) {
						return -1;
					} else if (this.occurrencesBoot == wakeLockData.getOccurrencesBoot()) {
						return 0;
					}
					return 1;
				case Utils.ORDER_BY_TIMES_BLOCKED:
					if (this.timesBlockedBoot > wakeLockData.getTimesBlockedBoot()) {
						return -1;
					} else if (this.timesBlockedBoot == wakeLockData.getTimesBlockedBoot()) {
						return 0;
					}
					return 1;
				case Utils.ORDER_BY_RUN_TIME:
					if (this.runTimeBoot > wakeLockData.getRunTimeBoot()) {
						return -1;
					} else if (this.runTimeBoot == wakeLockData.getRunTimeBoot()) {
						return 0;
					}
					return 1;
			}
		} else {
			switch (orderType) {
				case Utils.ORDER_BY_OCCURRENCES:
					if (this.occurrencesTotal > wakeLockData.getOccurrencesTotal()) {
						return -1;
					} else if (this.occurrencesTotal == wakeLockData.getOccurrencesTotal()) {
						return 0;
					}
					return 1;
				case Utils.ORDER_BY_TIMES_BLOCKED:
					if (this.timesBlockedTotal > wakeLockData.getTimesBlockedTotal()) {
						return -1;
					} else if (this.timesBlockedTotal == wakeLockData.getTimesBlockedTotal()) {
						return 0;
					}
					return 1;
				case Utils.ORDER_BY_RUN_TIME:
					if (this.runTimeTotal > wakeLockData.getRunTimeTotal()) {
						return -1;
					} else if (this.runTimeTotal == wakeLockData.getRunTimeTotal()) {
						return 0;
					}
					return 1;
			}
		}
		return 0;
	}

	public void addToLocks(IBinder binder)
	{
		this.locks.add(binder);
	}

	public List<IBinder> getLocks()
	{
		return this.locks;
	}

	public int getOccurrencesTotal()
	{
		return this.occurrencesTotal;
	}

	public void setOccurrencesTotal(int occurrencesTotal)
	{
		this.occurrencesTotal = occurrencesTotal;
	}

	public int getOccurrencesBoot()
	{
		return this.occurrencesBoot;
	}

	public void setOccurrencesBoot(int occurrencesBoot)
	{
		this.occurrencesBoot = occurrencesBoot;
	}

	public int getTimesBlockedTotal()
	{
		return this.timesBlockedTotal;
	}

	public void setTimesBlockedTotal(int timesBlockedTotal)
	{
		this.timesBlockedTotal = timesBlockedTotal;
	}

	public int getTimesBlockedBoot()
	{
		return this.timesBlockedBoot;
	}

	public void setTimesBlockedBoot(int timesBlockedBoot)
	{
		this.timesBlockedBoot = timesBlockedBoot;
	}

	public long getRunTimeTotal()
	{
		return this.runTimeTotal;
	}

	public void setRunTimeTotal(long runTimeTotal)
	{
		this.runTimeTotal = runTimeTotal;
	}

	public long getRunTimeBoot()
	{
		return this.runTimeBoot;
	}

	public void setRunTimeBoot(long runTimeBoot)
	{
		this.runTimeBoot = runTimeBoot;
	}

	public long getLastRunTimeRefresh()
	{
		return this.lastRunTimeRefresh;
	}

	public void setLastRunTimeRefresh(long lastRunTimeRefresh)
	{
		this.lastRunTimeRefresh = lastRunTimeRefresh;
	}

	public Long getLastAcquisition()
	{
		return this.lastAcquisition;
	}

	public void setLastAcquisition(Long lastAcquisition)
	{
		this.lastAcquisition = lastAcquisition;
	}

	public WakeLockStatus getStatus()
	{
		return this.status;
	}

	public void setStatus(WakeLockStatus status)
	{
		this.status = status;
	}
}
