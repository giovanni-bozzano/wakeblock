package com.giovannibozzano.wakeblock.database;

import android.provider.BaseColumns;

public final class WakeLocksTable
{
	private WakeLocksTable()
	{
	}

	public static class WakeLockEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "wakelocks";
		public static final String COLUMN_TAG = "tag";
		public static final String COLUMN_OCCURRENCES = "occurrences";
		public static final String COLUMN_TIMES_BLOCKED = "times_blocked";
		public static final String COLUMN_RUN_TIME = "run_time";
		public static final String COLUMN_LAST_ACQUISITION = "last_acquisition";
		public static final String COLUMN_ACQUIRING_PACKAGES = "acquiring_packages";
		public static final String COLUMN_IS_BLOCKED = "is_blocked";
		public static final String COLUMN_BLOCK_TIME = "block_time";
	}
}
