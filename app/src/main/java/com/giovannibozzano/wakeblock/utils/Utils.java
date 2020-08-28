package com.giovannibozzano.wakeblock.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.giovannibozzano.wakeblock.R;
import com.giovannibozzano.wakeblock.WakeBlockService;
import com.giovannibozzano.wakeblock.enums.DeviceArchitecture;
import com.giovannibozzano.wakeblock.enums.PatchType;
import com.giovannibozzano.wakeblock.exceptions.PatchException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Utils
{
	public static final short CORE_MOD_VERSION = 1;
	public static final boolean CHANGELOG = false;
	public static final String TAG = "WakeBlock";
	public static final int ORDER_BY_OCCURRENCES = 0;
	public static final int ORDER_BY_TIMES_BLOCKED = 1;
	public static final int ORDER_BY_RUN_TIME = 2;

	private Utils()
	{
	}

	public static void bindService(Context context)
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (sharedPreferences.getBoolean("foreground_service", false)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				context.startForegroundService(new Intent(context, WakeBlockService.class));
			} else {
				context.startService(new Intent(context, WakeBlockService.class));
			}
		} else {
			PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			if (powerManager != null) {
				WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "service_bind");
				wakeLock.acquire(0L);
				wakeLock.release();
				Log.i(Utils.TAG, "+++++++++++++++++++++++++ UTILS WAKEBLOCK SERVICE SENT THE WAKELOCK TO THE SYSTEM +++++++++++++++++++++++++");
			} else {
				Log.i(Utils.TAG, "+++++++++++++++++++++++++ WAKEBLOCK SERVICE FAILED TO SEND THE WAKELOCK TO THE SYSTEM +++++++++++++++++++++++++");
			}
		}
	}

	public static Map<String, WakeLockData> filterWakeLocks(Map<String, WakeLockData> wakeLockList, String searchQuery)
	{
		String lowerCaseQuery = searchQuery.toLowerCase();
		Map<String, WakeLockData> filteredWakeLockList = new LinkedHashMap<>();
		for (String key : wakeLockList.keySet()) {
			if (key.toLowerCase().contains(lowerCaseQuery)) {
				filteredWakeLockList.put(key, wakeLockList.get(key));
			}
		}
		return filteredWakeLockList;
	}

	public static Map<String, WakeLockData> sortWakeLocks(Map<String, WakeLockData> map, int orderType, boolean bootList)
	{
		List<Map.Entry<String, WakeLockData>> list = new LinkedList<>(map.entrySet());
		list.sort((object1, object2) -> object1.getValue().compareTo(object2.getValue(), orderType, bootList));
		Map<String, WakeLockData> result = new LinkedHashMap<>();
		for (Map.Entry<String, WakeLockData> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static String getCurrentDate(String format)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
		simpleDateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
		return simpleDateFormat.format(new Date());
	}

	public static String generateRandomHexString(int length)
	{
		Random random = new Random();
		StringBuilder stringBuilder = new StringBuilder();
		while (stringBuilder.length() < length) {
			stringBuilder.append(Integer.toHexString(random.nextInt()));
		}
		return stringBuilder.toString().substring(0, length);
	}

	public static String getUserId(Context context, String path) throws PatchException
	{
		List<String> commandList = new ArrayList<>();
		commandList.add("cd " + path);
		commandList.add("ls -l | grep u0_ | cut -d \" \" -f3 | head -1");
		String userId = ExecuteAsRoot.read(commandList);
		if (userId == null) {
			throw new PatchException(context.getResources().getString(R.string.error_3));
		}
		return userId;
	}

	@SuppressWarnings("unused")
	public static String getSelinuxContext(Context context, String path) throws PatchException
	{
		List<String> commandList = new ArrayList<>();
		commandList.add("cd " + path);
		commandList.add("ls -dZ files | cut -d \":\" -f5 | cut -d \" \" -f0");
		String selinuxContext = ExecuteAsRoot.read(commandList);
		if (selinuxContext == null) {
			throw new PatchException(context.getResources().getString(R.string.error_4));
		}
		return selinuxContext;
	}

	public static void rebootAndFlash(Context context, String path) throws PatchException
	{
		List<String> commandList = new ArrayList<>();
		commandList.add("echo 'boot-recovery ' > /cache/recovery/openrecoveryscript");
		commandList.add("echo '--update_package=" + path + "' >> /cache/recovery/openrecoveryscript");
		commandList.add("reboot recovery");
		if (!ExecuteAsRoot.execute(commandList)) {
			throw new PatchException(context.getResources().getString(R.string.error_18));
		}
	}

	public static void zip(String outputPath, List<String> files, String removedParentPath) throws IOException
	{
		File outputFile = new File(outputPath);
		File outputDirectory = outputFile.getParentFile();
		if (outputDirectory != null && (!outputDirectory.exists() || !outputDirectory.isDirectory()) && !outputDirectory.mkdirs()) {
			throw new IOException();
		}
		if (outputFile.exists() && !outputFile.delete()) {
			throw new IOException();
		}
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outputPath))) {
			for (String filePath : files) {
				File file = new File(filePath);
				if (file.isDirectory()) {
					Utils.addDirectoryToZip(file, zipOutputStream, removedParentPath);
				} else {
					Utils.addFileToZip(file, zipOutputStream, removedParentPath);
				}
			}
		}
	}

	private static void addDirectoryToZip(File directory, ZipOutputStream zipOutputStream, String removedParentPath) throws IOException
	{
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				Utils.addDirectoryToZip(file, zipOutputStream, removedParentPath);
			} else {
				Utils.addFileToZip(file, zipOutputStream, removedParentPath);
			}
		}
	}

	private static void addFileToZip(File file, ZipOutputStream zipOutputStream, String removedParentPath) throws IOException
	{
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			zipOutputStream.putNextEntry(new ZipEntry(file.getAbsolutePath().replace(removedParentPath, "")));
			byte[] buffer = new byte[4096];
			int read;
			while ((read = fileInputStream.read(buffer)) > 0) {
				zipOutputStream.write(buffer, 0, read);
			}
			zipOutputStream.closeEntry();
		}
	}

	public static void unzip(String zipFilePath, String outputPath) throws IOException
	{
		File pathDirectory = new File(outputPath);
		if (!pathDirectory.exists() && !pathDirectory.mkdirs()) {
			throw new IOException();
		}
		try (ZipFile zipFile = new ZipFile(zipFilePath)) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				File file = new File(outputPath + File.separator + zipEntry.getName());
				if (zipEntry.isDirectory()) {
					if (!file.mkdirs()) {
						throw new IOException();
					}
					continue;
				}
				if ((!file.getParentFile().exists() && !file.getParentFile().mkdirs()) || !file.createNewFile()) {
					throw new IOException();
				}
				try (InputStream inputStream = zipFile.getInputStream(zipEntry); BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))) {
					byte[] buffer = new byte[4096];
					int read;
					while ((read = inputStream.read(buffer)) > 0) {
						bufferedOutputStream.write(buffer, 0, read);
					}
				}
			}
		}
	}

	public static void copyFileFromAssets(AssetManager assetManager, String fileName, String outputPath) throws IOException
	{
		File outputFile = new File(outputPath);
		File outputDirectory = outputFile.getParentFile();
		if (outputDirectory != null && (!outputDirectory.exists() || !outputDirectory.isDirectory()) && !outputDirectory.mkdirs()) {
			throw new IOException();
		}
		if (outputFile.exists() && !outputFile.delete()) {
			throw new IOException();
		}
		try (InputStream inputStream = assetManager.open(fileName); OutputStream outputStream = new FileOutputStream(new File(outputPath))) {
			byte[] buffer = new byte[4096];
			int read;
			while ((read = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, read);
			}
			outputStream.flush();
		}
	}

	public static void copyFilesFromLocation(String sourcePath, String outputPath) throws IOException
	{
		File outputFile = new File(outputPath);
		File outputDirectory = outputFile.getParentFile();
		if (outputDirectory != null && (!outputDirectory.exists() || !outputDirectory.isDirectory()) && !outputDirectory.mkdirs()) {
			throw new IOException();
		}
		if (outputFile.exists() && !outputFile.delete()) {
			throw new IOException();
		}
		try (InputStream inputStream = new FileInputStream(new File(sourcePath)); OutputStream outputStream = new FileOutputStream(outputFile)) {
			byte[] buffer = new byte[4096];
			int read;
			while ((read = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, read);
			}
		}
	}

	public static long crc32(String fileName) throws IOException
	{
		try (InputStream inputStream = new FileInputStream(fileName)) {
			CRC32 crc = new CRC32();
			byte[] buffer = new byte[4096];
			int read;
			while ((read = inputStream.read(buffer)) > 0) {
				crc.update(buffer, 0, read);
			}
			return crc.getValue();
		}
	}

	@RequiresApi(Build.VERSION_CODES.O)
	public static String createNotificationChannel(Context context, String channelId, String channelName, int importance, int lockScreenVisibility, boolean showBadge)
	{
		NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
		notificationChannel.setLockscreenVisibility(lockScreenVisibility);
		notificationChannel.setShowBadge(showBadge);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (notificationManager != null) {
			notificationManager.createNotificationChannel(notificationChannel);
		}
		return channelId;
	}

	public static DeviceArchitecture getDeviceArchitecture()
	{
		for (String androidArch : Build.SUPPORTED_ABIS) {
			switch (androidArch) {
				case "arm64-v8a":
					return DeviceArchitecture.ARM64_V8A;
				case "armeabi-v7a":
					return DeviceArchitecture.ARMEABI_V7A;
				case "x86_64":
				case "x86":
					throw new RuntimeException("Unsupported architecture");
				default:
					break;
			}
		}
		throw new RuntimeException("Unable to determine arch from Build.SUPPORTED_ABIS = " + Arrays.toString(Build.SUPPORTED_ABIS));
	}

	public static String getMd5(String path)
	{
		List<String> commandList = new ArrayList<>();
		commandList.add("md5sum " + path + " | cut -d \" \" -f 1");
		return ExecuteAsRoot.read(commandList);
	}

	public static int themeAttributeToColor(int themeAttributeId, Context context)
	{
		TypedValue outValue = new TypedValue();
		Resources.Theme theme = context.getTheme();
		boolean wasResolved = theme.resolveAttribute(themeAttributeId, outValue, true);
		if (wasResolved) {
			return outValue.resourceId == 0 ? outValue.data : ContextCompat.getColor(context, outValue.resourceId);
		} else {
			return 0;
		}
	}

	public static String buildUpdaterBinary(PatchType patchType, boolean vDexExists)
	{
		return "#!/sbin/sh\n" +
		       "\n" +
		       "OUTFD=/proc/self/fd/$2;\n" +
		       "ZIPFILE=\"$3\";\n" +
		       "\n" +
		       "# ui_print \"<message>\" [\"<message 2>\" ...]\n" +
		       "ui_print() {\n" + "\twhile [ \"$1\" ]; do\n" +
		       "\t\techo \"ui_print $1\" >> \"$OUTFD\";\n" +
		       "\t\tshift;\n" +
		       "\tdone\n" +
		       "}\n" +
		       "\n" +
		       "# package_extract_file <file> <destination_file>\n" +
		       "package_extract_file() { mkdir -p \"$(dirname \"$2\")\"; unzip -o \"$ZIPFILE\" \"$1\" -p > \"$2\"; }\n" +
		       "\n" + "# set_perm <owner> <group> <mode> <file> [<file2> ...]\n" +
		       "set_perm() {\n" +
		       "\tuid=$1\n" +
		       "\tgid=$2\n" +
		       "\tmod=$3\n" +
		       "\tshift 3;\n" +
		       "\tchown \"$uid\":\"$gid\" \"$@\" || chown \"$uid\".\"$gid\" \"$@\";\n" +
		       "\tchmod \"$mod\" \"$@\";\n" +
		       "}\n" +
		       "\n" +
		       "ui_print \"========================\"\n" +
		       "ui_print \"WakeBlock Install Script\"\n" +
		       "ui_print \"========================\"\n" +
		       "ui_print \"Mounting system partition...\"\n" +
		       "umount \"/system\"\n" +
		       "mount -o rw /system\n" +
		       "ui_print \"Copying new file...\"\n" +
		       "if [[ -d /system/system ]]\n" +
		       "then\n" +
		       "\tui_print \"Detecting /system/system...\"\n" +
		       "\tpackage_extract_file \"" + (patchType == PatchType.DEODEXED ? "services.jar" : "services.odex") + "\" \"/system/system/framework/" + (patchType == PatchType.DEODEXED ? "services.jar" : "oat/arm" + (patchType == PatchType.ODEXED_ARM ? "" : "64") + "/services.odex") + "\"\n" +
		       (vDexExists ? "\tpackage_extract_file \"services.vdex\" \"/system/system/framework/oat/arm" + (patchType == PatchType.ODEXED_ARM ? "" : "64") + "/services.vdex\")\n" : "") +
		       "\tui_print \"Setting permissions...\"\n" +
		       "\tset_perm \"0\" \"0\" \"0644\" \"/system/system/framework/" + (patchType == PatchType.DEODEXED ? "services.jar" : "oat/arm" + (patchType == PatchType.ODEXED_ARM ? "" : "64") + "/services.odex") + "\"\n" +
		       (vDexExists ? "\tset_perm \"0\" \"0\" \"0644\" \"/system/system/framework/oat/arm" + (patchType == PatchType.ODEXED_ARM ? "" : "64") + "/services.vdex\")\n" : "") +
		       "else\n" + "\tui_print \"Detecting /system...\"\n" +
		       "\tpackage_extract_file \"" + (patchType == PatchType.DEODEXED ? "services.jar" : "services.odex") + "\" \"/system/framework/" + (patchType == PatchType.DEODEXED ? "services.jar" : "oat/arm" + (patchType == PatchType.ODEXED_ARM ? "" : "64") + "/services.odex") + "\"\n" +
		       (vDexExists ? "\tpackage_extract_file \"services.vdex\" \"/system/framework/oat/arm" + (patchType == PatchType.ODEXED_ARM ? "" : "64") + "/services.vdex\")\n" : "") +
		       "\tui_print \"Setting permissions...\"\n" +
		       "\tset_perm \"0\" \"0\" \"0644\" \"/system/framework/" + (patchType == PatchType.DEODEXED ? "services.jar" : "oat/arm" + (patchType == PatchType.ODEXED_ARM ? "" : "64") + "/services.odex") + "\"\n" +
		       (vDexExists ? "\tset_perm \"0\" \"0\" \"0644\" \"/system/framework/oat/arm" + (patchType == PatchType.ODEXED_ARM ? "" : "64") + "/services.vdex\")\n" : "") +
		       "fi\n" +
		       "ui_print \"Unmounting system partition...\"\n" +
		       "umount \"/system\"\n" +
		       "ui_print \"========\"\n" +
		       "ui_print \"Finished\"\n" +
		       "ui_print \"========\"\n";
	}
}
