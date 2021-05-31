package com.giovannibozzano.wakeblock.patcher;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.giovannibozzano.wakeblock.R;
import com.giovannibozzano.wakeblock.enums.DeviceArchitecture;
import com.giovannibozzano.wakeblock.enums.InstallMode;
import com.giovannibozzano.wakeblock.enums.PatchType;
import com.giovannibozzano.wakeblock.exceptions.PatchException;
import com.giovannibozzano.wakeblock.exceptions.SystemAlreadyPatchedException;
import com.giovannibozzano.wakeblock.exceptions.UnsupportedFrameworkException;
import com.giovannibozzano.wakeblock.patcher.classes.PatcherPowerManagerService;
import com.giovannibozzano.wakeblock.utils.ExecuteAsRoot;
import com.giovannibozzano.wakeblock.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.nio.file.FileSystem;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.ObservableEmitter;

class PatcherProcess implements Runnable
{
	private final WeakReference<PatcherService> service;
	private final ObservableEmitter<PatcherStatus> statusObserver;
	private final InstallMode installMode;
	private PatchType patchType;
	private String coreModPath;

	PatcherProcess(PatcherService service, ObservableEmitter<PatcherStatus> statusObserver, String coreModPath, InstallMode installMode)
	{
		this.service = new WeakReference<>(service);
		this.statusObserver = statusObserver;
		this.coreModPath = coreModPath;
		this.installMode = installMode;
	}

	@Override
	public void run()
	{
		Log.i(this.service.get().getPackageName(), "Core mod path: " + this.coreModPath);
		try {
            /*
            PREPARATION PHASE
            */
			String userId = Utils.getUserId(this.service.get(), this.service.get().getApplicationInfo().dataDir);
			Log.i(this.service.get().getPackageName(), "User ID: " + userId);
			this.service.get().sendNotification("Cleaning previous temporary files...");
			this.cleanFiles();
			this.service.get().sendNotification("Creating /patcher folder...");
			this.createFolder();
			this.getCoremodPath(userId);
			Log.i(this.service.get().getPackageName(), this.patchType.name());
			this.service.get().sendNotification("Copying baksmali/smali from assets...");
			boolean vDexExists = this.checkVdex();
			this.copyLibrariesFromAssets(vDexExists);

			/*
            PATCHING PHASE
			*/
			this.service.get().sendNotification("Extracting...");
			this.runBaksmali(userId, vDexExists);
			this.service.get().sendNotification("Patching smali code...");
			this.patchSmali();
			this.service.get().sendNotification("Patching...");
			this.runSmali(userId);
			this.service.get().sendNotification("Zipping services.jar...");
			this.createJar();
			if (this.patchType != PatchType.DEODEXED) {
				this.service.get().sendNotification("Odexing...");
				this.createOdex(vDexExists, userId);
			}

			/*
            FLASHING PHASE
			*/
			String path = this.service.get().getCacheDir().getAbsolutePath() + (this.installMode == InstallMode.MAGISK ? "/magisktemp" : "/patcher");
			this.service.get().sendNotification("Creating flashable zip folder...");
			this.createFlashableZipFolder(path);
			this.service.get().sendNotification("Creating backup...");
			this.createBackup(vDexExists);
			this.service.get().sendNotification("Printing the custom update scripts...");
			this.createUpdaterScript(vDexExists, path);
			this.service.get().sendNotification("Creating flashable zip...");
			this.createFlashableZip(vDexExists);
			switch (this.installMode) {
				case MAGISK:
					this.service.get().sendNotification("Copying Magisk Module to /sdcard/WakeBlock...");
					try {
						Utils.copyFilesFromLocation(this.service.get().getCacheDir().getAbsolutePath() + "/wakeblock-magisk.zip", Environment.getExternalStorageDirectory().getAbsolutePath() + "/WakeBlock/wakeblock-magisk.zip");
					} catch (IOException exception) {
						throw new PatchException("Storing zip file failed", exception);
					}
					this.service.get().sendNotification(this.service.get().getResources().getString(R.string.choice_noreboot_info));
					break;
				case MANUAL:
					this.service.get().sendNotification("Copying flashabe zip to /sdcard/WakeBlock...");
					try {
						Utils.copyFilesFromLocation(this.service.get().getCacheDir().getAbsolutePath() + "/flash.zip", Environment.getExternalStorageDirectory().getAbsolutePath() + "/WakeBlock/flash-me.zip");
					} catch (IOException exception) {
						throw new PatchException("Storing zip file failed", exception);
					}
					this.service.get().sendNotification(this.service.get().getResources().getString(R.string.choice_noreboot_info));
					break;
				case SCHEDULED:
					this.service.get().sendNotification("Copying flashabe zip to /sdcard/WakeBlock...");
					try {
						Utils.copyFilesFromLocation(this.service.get().getCacheDir().getAbsolutePath() + "/flash.zip", Environment.getExternalStorageDirectory().getAbsolutePath() + "/WakeBlock/flash-me.zip");
					} catch (IOException exception) {
						throw new PatchException("Storing zip file failed", exception);
					}
					this.service.get().sendNotification("Flash scheduling...");
					Utils.rebootAndFlash(this.service.get(), this.service.get().getCacheDir().getAbsolutePath() + "/flash.zip");
					break;
			}
			this.statusObserver.onNext(new PatcherStatus());
		} catch (PatchException exception) {
			exception.printStackTrace();
			this.handleError(exception.getMessage());
		} catch (UnsupportedFrameworkException exception) {
			exception.printStackTrace();
			this.handleError(this.service.get().getResources().getString(R.string.unsupported_framework) + ": " + this.service.get().getResources().getString(R.string.error_6));
		} catch (SystemAlreadyPatchedException exception) {
			exception.printStackTrace();
			this.handleError(this.service.get().getResources().getString(R.string.core_mod_already_installed));
		}
	}

	private void createFolder() throws PatchException
	{
		File patcherFolder = new File(this.service.get().getCacheDir().getAbsolutePath() + "/patcher");
		if ((!patcherFolder.exists() && !patcherFolder.mkdirs()) || (!patcherFolder.isDirectory() && !patcherFolder.mkdirs())) {
			throw new PatchException(this.service.get().getResources().getString(R.string.error_0));
		}
	}

	private void cleanFiles() throws PatchException
	{
		List<String> commandList = new ArrayList<>();
		commandList.add("rm -rf " + this.service.get().getCacheDir().getAbsolutePath() + "/*");
		if (!ExecuteAsRoot.execute(commandList)) {
			throw new PatchException(this.service.get().getResources().getString(R.string.error_1));
		}
	}

	private void getCoremodPath(String userId) throws PatchException
	{
		if (!this.isJarUsed(userId)) {
			switch (this.coreModPath) {
				case "/system/framework/oat/arm/services.odex":
					this.patchType = PatchType.ODEXED_ARM;
					break;
				case "/system/framework/oat/arm64/services.odex":
					this.patchType = PatchType.ODEXED_ARM64;
					break;
				default:
					this.patchType = PatchType.DEODEXED;
					break;
			}
		} else {
			this.patchType = PatchType.DEODEXED;
			this.coreModPath = "/system/framework/services.jar";
		}
	}

	private boolean isJarUsed(String userId) throws PatchException
	{
		List<String> commandList = new ArrayList<>();
		commandList.add("cp /system/framework/services.jar " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.original.jar");
		commandList.add("chcon u:object_r:app_data_file:s0:c512,c768 " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.original.jar");
		commandList.add("chown " + userId + ":" + userId + " " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.original.jar");
		if (!ExecuteAsRoot.execute(commandList)) {
			throw new PatchException("Checking jar file failed");
		}
		try {
			Utils.unzip(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.original.jar", this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services-original");
			File file = new File(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services-original/classes.dex");
			if (file.exists()) {
				org.jf.baksmali.Main.main(new String[] { "d", "-o", this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services", "--di", "false", this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.original.jar" });
				File classe = new File (this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services/com/android/server/power/PowerManagerService.smali");
				if(classe.exists()) {
					File pulizia = new File(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services");
					if (!file.delete() || !classe.delete() || !pulizia.delete()) {
						throw new PatchException("Checking jar file failed");
					}
					return true;
				}
			}
		} catch (IOException exception) {
			throw new PatchException("Checking jar file failed", exception);
		}
		return false;
	}

	private void copyLibrariesFromAssets(boolean vDexExists) throws PatchException
	{
		try {
			if (vDexExists) {
				Utils.copyFileFromAssets(this.service.get().getAssets(), (Utils.getDeviceArchitecture() == DeviceArchitecture.ARMEABI_V7A ? "vdexExtractor-armeabi-v7a" : "vdexExtractor-arm64-v8a"), this.service.get().getCacheDir().getAbsolutePath() + "/patcher/vdexExtractor");
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
				Utils.copyFileFromAssets(this.service.get().getAssets(), (Utils.getDeviceArchitecture() == DeviceArchitecture.ARMEABI_V7A ? "compact_dex_converter-armeabi-v7a" : "compact_dex_converter-arm64-v8a"), this.service.get().getCacheDir().getAbsolutePath() + "/patcher/compact_dex_converter");
			}
			if (this.installMode == InstallMode.MAGISK) {
				Utils.copyFileFromAssets(this.service.get().getAssets(), "fraccagnoricci.zip", this.service.get().getCacheDir().getAbsolutePath() + "/patcher/fraccagnoricci.zip");
				Utils.unzip(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/fraccagnoricci.zip", this.service.get().getCacheDir().getAbsolutePath() + "/magisktemp");
			}
			Utils.copyFileFromAssets(this.service.get().getAssets(), "giovannibozzano.zip", this.service.get().getCacheDir().getAbsolutePath() + "/patcher/giovannibozzano.zip");
			Utils.unzip(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/giovannibozzano.zip", this.service.get().getCacheDir().getAbsolutePath() + "/patcher");
		} catch (IOException exception) {
			throw new PatchException(this.service.get().getResources().getString(R.string.error_2), exception);
		}
	}

	private boolean checkVdex()
	{
		return this.patchType != PatchType.DEODEXED && new File(this.coreModPath.replace("odex", "vdex")).exists();
	}

	private void runBaksmali(String userId, boolean vDexExists) throws PatchException
	{
		List<String> commandList = new ArrayList<>();
		if (this.patchType != PatchType.DEODEXED) {
			if (vDexExists) {
				commandList.add("cd " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher");
				commandList.add("chmod 755 vdexExtractor");
				commandList.add("nice -n -20 ./vdexExtractor -i " + this.coreModPath.replace("odex", "vdex") + " -o " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher --ignore-crc-error");
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
					commandList.add("chmod 755 compact_dex_converter");
					commandList.add("nice -n -20 ./compact_dex_converter " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services_classes.cdex");
					commandList.add("mv " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services_classes.cdex.new " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services_classes.dex");
				}
				commandList.add("chcon u:object_r:app_data_file:s0:c512,c768 " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services_classes.dex");
				commandList.add("chown " + userId + ":" + userId + " " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services_classes.dex");
				commandList.add("sync");
				commandList.add("echo 3 > /proc/sys/vm/drop_caches");
				if (!ExecuteAsRoot.execute(commandList)) {
					throw new PatchException(this.service.get().getResources().getString(R.string.error_5));
				}
				org.jf.baksmali.Main.main(new String[] { "d", "-o", this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services", "--di", "false", this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services_classes.dex" });
			} else {
				commandList.add("sync");
				commandList.add("echo 3 > /proc/sys/vm/drop_caches");
				if (!ExecuteAsRoot.execute(commandList)) {
					throw new PatchException(this.service.get().getResources().getString(R.string.error_5));
				}
				org.jf.baksmali.Main.main(new String[] { "x", "-d", "/system/framework/arm" + (this.patchType == PatchType.ODEXED_ARM ? "" : "64"), "-o", this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services", "--di", "false", this.coreModPath });
			}
		} else {
			commandList.add("sync");
			commandList.add("echo 3 > /proc/sys/vm/drop_caches");
			if (!ExecuteAsRoot.execute(commandList)) {
				throw new PatchException(this.service.get().getResources().getString(R.string.error_5));
			}
			org.jf.baksmali.Main.main(new String[] { "d", "-o", this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services", "--di", "false", this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.original.jar" });
		}
		commandList.clear();
		commandList.add("chcon -R u:object_r:app_data_file:s0:c512,c768 " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services");
		commandList.add("chown -R " + userId + ":" + userId + " " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services");
		if (!ExecuteAsRoot.execute(commandList)) {
			throw new PatchException(this.service.get().getResources().getString(R.string.error_5));
		}
	}

	private void patchSmali() throws UnsupportedFrameworkException, SystemAlreadyPatchedException
	{
		new PatcherPowerManagerService(this.service.get(), "patcher/services/com/android/server/power/PowerManagerService.smali").patch();
	}

	private void runSmali(String userId) throws PatchException
	{
		List<String> commandList = new ArrayList<>();
		commandList.add("cp -r " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/PowerManagerService.smali " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services/com/android/server/power/PowerManagerService.smali");
		commandList.add("mv " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/giovannibozzano " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services/com/");
		commandList.add("sync");
		commandList.add("echo 3 > /proc/sys/vm/drop_caches");
		if (!ExecuteAsRoot.execute(commandList)) {
			throw new PatchException(this.service.get().getResources().getString(R.string.error_9));
		}
		org.jf.smali.Main.main(new String[] { "a", "-o", this.service.get().getCacheDir().getAbsolutePath() + "/patcher/" + (this.patchType != PatchType.DEODEXED ? "" : "services-original/") + "classes.dex", this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services" });
		commandList.clear();
		commandList.add("chcon u:object_r:app_data_file:s0:c512,c768 " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/" + (this.patchType != PatchType.DEODEXED ? "" : "services-original/") + "classes.dex");
		commandList.add("chown " + userId + ":" + userId + " " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/" + (this.patchType != PatchType.DEODEXED ? "" : "services-original/") + "classes.dex");
		if (!ExecuteAsRoot.execute(commandList)) {
			throw new PatchException(this.service.get().getResources().getString(R.string.error_9));
		}
	}

	private void createJar() throws PatchException
	{
		try {
			if (this.patchType != PatchType.DEODEXED) {
				Utils.zip(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.jar", Collections.singletonList(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/classes.dex"), this.service.get().getCacheDir().getAbsolutePath() + "/patcher/");
			} else {
				List<String> filePaths = new ArrayList<>();
				for (File file : new File(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services-original").listFiles()) {
					filePaths.add(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services-original/" + file.getName());
				}
				Utils.zip(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.jar", filePaths, this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services-original/");
			}
		} catch (IOException exception) {
			throw new PatchException(this.service.get().getResources().getString(R.string.error_10), exception);
		}
	}

	private void createOdex(boolean vDexExists, String userId) throws PatchException
	{
		List<String> commandList = new ArrayList<>();
		commandList.add("sync");
		commandList.add("echo 3 > /proc/sys/vm/drop_caches");
		commandList.add("nice -n -20 dex2oat --runtime-arg -Xms64M --runtime-arg -Xmx256M --runtime-arg -classpath --runtime-arg \"&\" --boot-image=/system/framework/boot.art --dex-file=" + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.jar --dex-location=/system/framework/services.jar --oat-file=" + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.odex --android-root=/system --instruction-set=arm" + (this.patchType == PatchType.ODEXED_ARM ? "" : "64") + " --instruction-set-variant=generic --instruction-set-features=default --runtime-arg -Xnorelocate --no-generate-debug-info " + (vDexExists ? "--generate-build-id " : " ") + "--abort-on-hard-verifier-error --compile-pic");
		commandList.add("chcon u:object_r:app_data_file:s0:c512,c768 " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.odex");
		commandList.add("chown " + userId + ":" + userId + " " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.odex");
		if (vDexExists) {
			commandList.add("chcon u:object_r:app_data_file:s0:c512,c768 " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.vdex");
			commandList.add("chown " + userId + ":" + userId + " " + this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.vdex");
		}
		if (!ExecuteAsRoot.execute(commandList)) {
			throw new PatchException(this.service.get().getResources().getString(R.string.error_11));
		}
	}

	private void createFlashableZipFolder(String path) throws PatchException
	{
		File updateBinaryPath = new File(path + (this.installMode != InstallMode.MAGISK ? "/META-INF/com/google/android" : ""));
		if ((!updateBinaryPath.exists() && !updateBinaryPath.mkdirs()) || (!updateBinaryPath.isDirectory() && !updateBinaryPath.mkdirs())) {
			throw new PatchException(this.service.get().getResources().getString(R.string.error_12));
		}
	}

	private void createBackup(boolean vDexExists) throws PatchException
	{
		if (this.installMode != InstallMode.MAGISK) {
			File backupPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/WakeBlock/Backups/" + Utils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
			if ((!backupPath.exists() && !new File(backupPath.getAbsolutePath() + "/temp").mkdirs()) || (!backupPath.isDirectory() && !new File(backupPath.getAbsolutePath() + "/temp").mkdirs())) {
				throw new PatchException(this.service.get().getResources().getString(R.string.error_17));
			}
			List<String> commandList = new ArrayList<>();
			commandList.add("cp \"" + this.coreModPath + "\" \"" + backupPath.getAbsolutePath() + "/temp/\"");
			if (vDexExists) {
				commandList.add("cp \"" + this.coreModPath.replace("odex", "vdex") + "\" \"" + backupPath + "/temp/\"");
			}
			if (!ExecuteAsRoot.execute(commandList)) {
				throw new PatchException(this.service.get().getResources().getString(R.string.error_17));
			}
			this.createFlashableZipFolder(backupPath.getAbsolutePath() + "/temp");
			this.createUpdaterScript(vDexExists, backupPath.getAbsolutePath() + "/temp");
			List<String> filePaths = new ArrayList<>();
			for (File file : new File(backupPath.getAbsolutePath() + "/temp").listFiles()) {
				filePaths.add(backupPath.getAbsolutePath() + "/temp/" + file.getName());
			}
			try {
				Utils.zip(backupPath.getAbsolutePath() + "/" + (this.patchType != PatchType.DEODEXED ? Utils.crc32(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.odex") : Utils.crc32(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.jar")) + ".zip", filePaths, backupPath.getAbsolutePath() + "/temp/");
			} catch (IOException exception) {
				throw new PatchException(this.service.get().getResources().getString(R.string.error_17), exception);
			}
			commandList.clear();
			commandList.add("rm -rf \"" + backupPath.getAbsolutePath() + "/temp\"");
			if (!ExecuteAsRoot.execute(commandList)) {
				throw new PatchException(this.service.get().getResources().getString(R.string.error_17));
			}
		}
	}

	private void createUpdaterScript(boolean vDexExists, String path) throws PatchException
	{
		if (this.installMode != InstallMode.MAGISK) {
			try (PrintWriter printWriter = new PrintWriter(new File(path + "/META-INF/com/google/android/update-binary"), "UTF-8")) {
				Utils.copyFileFromAssets(this.service.get().getAssets(), "updater-script", path + "/META-INF/com/google/android/updater-script");
				printWriter.print(Utils.buildUpdaterBinary(this.patchType, vDexExists));
			} catch (IOException exception) {
				throw new PatchException(this.service.get().getResources().getString(R.string.error_15), exception);
			}
		} else {
			String postfsdata = "MODDIR=${0%/*}\n" + "PATHDISABLE=\"/system/disable.module\"\n" + (this.patchType == PatchType.DEODEXED ? "PATHJAR=\"" : "PATHODEX=\"") + this.coreModPath + "\"\n" + (this.patchType == PatchType.DEODEXED ? "MD5JAR=\"" : "MD5ODEX=\"") + Utils.getMd5(this.coreModPath) + "\"\n" + (vDexExists ? "PATHVDEX=\"" + this.coreModPath.replace("odex", "vdex") + "\"\n" + "MD5VDEX=\"" + Utils.getMd5(this.coreModPath.replace("odex", "vdex")) + "\"\n" : "") + "if" + (this.patchType != PatchType.DEODEXED ? " [ \"$(md5sum $PATHODEX | cut -d \" \" -f 1)\" != \"$MD5ODEX\" ] ||" : "") + (vDexExists ? " [ \"$(md5sum $PATHVDEX | cut -d \" \" -f 1)\" != \"$MD5VDEX\" ] ||" : "") + (this.patchType == PatchType.DEODEXED ? " [ \"$(md5sum $PATHJAR | cut -d \" \" -f 1)\" != \"$MD5JAR\" ] ||" : "") + " [ -e $PATHDISABLE ]\n" + "then\n" + "  echo \"[WAKEBLOCK WATCHER] You've updated your ROM! Disabling Magisk module...\"\n" + "  rm -rf $MODDIR\n" + "  rm -rf $PATHDISABLE\nfi\n";
			try (PrintWriter printWriter = new PrintWriter(new File(path + "/common", "post-fs-data.sh"), "UTF-8")) {
				printWriter.print(postfsdata);
			} catch (IOException exception) {
				throw new PatchException(this.service.get().getResources().getString(R.string.error_15), exception);
			}
		}
	}

	private void createFlashableZip(boolean vDexExists) throws PatchException
	{
		if (this.installMode != InstallMode.MAGISK) {
			List<String> filePaths = new ArrayList<>();
			filePaths.add(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/META-INF");
			filePaths.add(this.patchType != PatchType.DEODEXED ? this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.odex" : this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.jar");
			if (vDexExists) {
				filePaths.add(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.vdex");
			}
			try {
				Utils.zip(this.service.get().getCacheDir().getAbsolutePath() + "/flash.zip", filePaths, this.service.get().getCacheDir().getAbsolutePath() + "/patcher/");
			} catch (IOException exception) {
				throw new PatchException(this.service.get().getResources().getString(R.string.error_16), exception);
			}
		} else {
			try {
				File magiskModule = new File(this.service.get().getCacheDir().getAbsolutePath() + "/magisktemp");
				if ((!magiskModule.exists() && !magiskModule.mkdirs()) || (!magiskModule.isDirectory() && !magiskModule.mkdirs())) {
					throw new PatchException(this.service.get().getResources().getString(R.string.error_12));
				}
				if (this.patchType != PatchType.DEODEXED) {
					Utils.copyFilesFromLocation(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.odex", this.service.get().getCacheDir().getAbsolutePath() + "/magisktemp/system/framework/oat/arm" + (this.patchType == PatchType.ODEXED_ARM ? "/" : "64/") + "services.odex");
				}
				if (vDexExists) {
					Utils.copyFilesFromLocation(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.vdex", this.service.get().getCacheDir().getAbsolutePath() + "/magisktemp/system/framework/oat/arm" + (this.patchType == PatchType.ODEXED_ARM ? "/" : "64/") + "services.vdex");
				}
				if (this.patchType == PatchType.DEODEXED) {
					Utils.copyFilesFromLocation(this.service.get().getCacheDir().getAbsolutePath() + "/patcher/services.jar", this.service.get().getCacheDir().getAbsolutePath() + "/magisktemp/system/framework/services.jar");
				}
				List<String> filePaths = new ArrayList<>();
				for (File file : new File(this.service.get().getCacheDir().getAbsolutePath() + "/magisktemp").listFiles()) {
					filePaths.add(this.service.get().getCacheDir().getAbsolutePath() + "/magisktemp/" + file.getName());
				}
				Utils.zip(this.service.get().getCacheDir().getAbsolutePath() + "/wakeblock-magisk.zip", filePaths, this.service.get().getCacheDir().getAbsolutePath() + "/magisktemp/");
			} catch (IOException exception) {
				throw new PatchException(this.service.get().getResources().getString(R.string.error_16), exception);
			}
		}
	}

	private void handleError(String message)
	{
		this.statusObserver.onNext(new PatcherError(this.service.get().getResources().getString(R.string.patching_error) + (message != null ? " (" + message.toUpperCase() + ")" : "")));
	}
}
