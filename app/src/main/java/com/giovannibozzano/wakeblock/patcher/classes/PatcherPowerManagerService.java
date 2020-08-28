package com.giovannibozzano.wakeblock.patcher.classes;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.Log;
import android.util.SparseArray;

import com.giovannibozzano.wakeblock.exceptions.SystemAlreadyPatchedException;
import com.giovannibozzano.wakeblock.exceptions.UnsupportedFrameworkException;
import com.giovannibozzano.wakeblock.patcher.SmaliParser;
import com.giovannibozzano.wakeblock.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PatcherPowerManagerService implements IPatcher
{
	private static final SparseArray<IMethodPatcher> PATCH_ACQUIRE_WAKELOCK_INTERNAL = new SparseArray<>();
	private static final SparseArray<IMethodPatcher> PATCH_REMOVE_WAKELOCK_LOCKED = new SparseArray<>();
	private final Context context;
	private final String filePath;

	public PatcherPowerManagerService(Context context, String filePath)
	{
		this.context = context;
		this.filePath = filePath;
	}

	private static String patchAcquireWakeLockInternalDefault(List<String> lines, String text) throws UnsupportedFrameworkException
	{
		Log.i("giovannibozzano.PatcherPowerManagerService", "patchAcquireWakeLockInternalDefault");
		String methodText = SmaliParser.getMethodText(lines, Collections.singletonList("acquireWakeLockInternal(Landroid/os/IBinder;ILjava/lang/String;Ljava/lang/String;Landroid/os/WorkSource;Ljava/lang/String;II)V"));
		if (methodText.length() <= 0) {
			throw new UnsupportedFrameworkException();
		}
		List<String> methodLines = new ArrayList<>(Arrays.asList(methodText.split("\n")));
		// Save the original method length.
		int originalMethodLineSize = methodLines.size();
		// Get the already existing labels.
		List<String> methodLabels = SmaliParser.getMethodLabels(methodText);
		// Generate a label and avoid already existing ones.
		String label1;
		do {
			label1 = Utils.generateRandomHexString(2);
		} while (methodLabels.contains(label1));
		int semaphoreRegister = -1;
		List<Integer> usedRegisters = new ArrayList<>();
		// Go through all the method lines.
		for (int index = 0; index < methodLines.size(); index++) {
			// Add current line registers as used.
			usedRegisters.addAll(SmaliParser.getLineRegisters(methodLines.get(index), false));
			if (methodLines.get(index).contains("monitor-enter")) {
				// Get the semaphore register in order to avoid overwriting it.
				semaphoreRegister = SmaliParser.getLineRegisters(methodLines.get(index), false).get(0);
			} else if (methodLines.get(index).contains(":try_start_")) {
				// Make sure we found the semaphore register.
				if (semaphoreRegister == -1) {
					throw new UnsupportedFrameworkException();
				}
				// Get at least 6 available registers.
				int neededRegisters = 6;
				List<Integer> availableRegisters = SmaliParser.getMethodRegisters(methodText);
				availableRegisters.removeAll(usedRegisters);
				if (availableRegisters.size() < neededRegisters) {
					Log.i("giovannibozzano.PatcherPowerManagerService", "Insufficient available registers: " + availableRegisters.size());
					int missingRegisters = neededRegisters - availableRegisters.size();
					// Go back looking for the used registers declaration line.
					for (int currentIndex = 0; currentIndex < methodLines.size(); currentIndex++) {
						if (methodLines.get(currentIndex).contains(".registers")) {
							// Push the used registers number by the correct amount (previous number plus the needed registers amount minus the already available registers amount).
							methodLines.remove(currentIndex);
							methodLines.add(currentIndex, "    .registers " + (SmaliParser.getMethodRegistersNumber(methodText) + missingRegisters));
							// Retrieve a sufficient number of unused registers.
							List<Integer> unusedRegisters = new ArrayList<>(SmaliParser.REGISTERS_NUMBERS);
							unusedRegisters.removeAll(availableRegisters);
							// This should not happen, because it means we require more registers than available in smali.
							if (unusedRegisters.size() < missingRegisters) {
								throw new UnsupportedFrameworkException();
							}
							// Add the unused registers to the available ones.
							for (int counter = 0; counter < missingRegisters; counter++) {
								availableRegisters.add(unusedRegisters.get(counter));
							}
							break;
						}
					}
				}
				Log.i("giovannibozzano.PatcherPowerManagerService", "Available registers: " + availableRegisters.size());
				int register1 = availableRegisters.get(0); // this instance
				int register2 = availableRegisters.get(1); // WakeBlock instance
				int register3 = availableRegisters.get(2); // mContext
				int register4 = availableRegisters.get(3); // lock
				int register5 = availableRegisters.get(4); // tag
				int register6 = availableRegisters.get(5); // packageName
				// Start injecting code.
				index++;
				methodLines.add(index++, "    # @ WAKEBLOCK");
				methodLines.add(index++, "");
				methodLines.add(index++, "    move-object/from16 v" + register1 + ", p0");
				methodLines.add(index++, "");
				methodLines.add(index++, "    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->getInstance()Lcom/giovannibozzano/wakeblock/WakeBlockService;");
				methodLines.add(index++, "");
				methodLines.add(index++, "    move-result-object v" + register2);
				methodLines.add(index++, "");
				methodLines.add(index++, "    iget-object v" + register3 + ", v" + register1 + ", Lcom/android/server/power/PowerManagerService;->mContext:Landroid/content/Context;");
				methodLines.add(index++, "");
				methodLines.add(index++, "    move-object/from16 v" + register4 + ", p1");
				methodLines.add(index++, "");
				methodLines.add(index++, "    move-object/from16 v" + register5 + ", p3");
				methodLines.add(index++, "");
				methodLines.add(index++, "    move-object/from16 v" + register6 + ", p4");
				methodLines.add(index++, "");
				methodLines.add(index++, "    invoke-virtual {v" + register2 + ", v" + register3 + ", v" + register4 + ", v" + register5 + ", v" + register6 + "}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->acquireWakeLockInternal(Landroid/content/Context;Landroid/os/IBinder;Ljava/lang/String;Ljava/lang/String;)Z");
				methodLines.add(index++, "");
				methodLines.add(index++, "    move-result v" + register2);
				methodLines.add(index++, "");
				methodLines.add(index++, "    if-nez v" + register2 + ", :cond_" + label1);
				methodLines.add(index++, "");
				methodLines.add(index++, "    monitor-exit v" + semaphoreRegister);
				methodLines.add(index++, "");
				methodLines.add(index++, "    return-void");
				methodLines.add(index++, "");
				methodLines.add(index++, "    :cond_" + label1);
				methodLines.add(index++, "");
				methodLines.add(index, "    # # WAKEBLOCK");
				// This is the last injection, break the loop to avoid duplicates.
				break;
			}
		}
		// If we did not inject any code, we failed.
		if (methodLines.size() <= originalMethodLineSize) {
			throw new UnsupportedFrameworkException();
		}
		// Create the new method.
		StringBuilder newMethod = new StringBuilder();
		for (int index = 0; index < methodLines.size(); index++) {
			newMethod.append(methodLines.get(index));
			if (index < methodLines.size() - 1) {
				newMethod.append('\n');
			}
		}
		// Replace the old method with the new one.
		return text.replace(methodText, newMethod.toString());
	}

	private static String patchRemoveWakeLockLockedDefault(List<String> lines, String text) throws UnsupportedFrameworkException
	{
		Log.i("giovannibozzano.PatcherPowerManagerService", "patchRemoveWakeLockLockedDefault");
		String methodText = SmaliParser.getMethodText(lines, Collections.singletonList("removeWakeLockLocked(Lcom/android/server/power/PowerManagerService$WakeLock;I)V"));
		if (methodText.length() <= 0) {
			throw new UnsupportedFrameworkException();
		}
		List<String> methodLines = new ArrayList<>(Arrays.asList(methodText.split("\n")));
		// Save the original method length.
		int originalMethodLineSize = methodLines.size();
		// Go through all the method lines.
		for (int index = 0; index < methodLines.size(); index++) {
			if (methodLines.get(index).contains(".registers")) {
				// Get at least 3 available registers.
				int neededRegisters = 3;
				List<Integer> availableRegisters = SmaliParser.getMethodRegisters(methodText);
				if (availableRegisters.size() < neededRegisters) {
					Log.i("giovannibozzano.PatcherPowerManagerService", "Insufficient available registers: " + availableRegisters.size());
					int missingRegisters = neededRegisters - availableRegisters.size();
					// Push the used registers number by the correct amount (previous number plus the needed registers amount minus the already available registers amount).
					methodLines.remove(index);
					methodLines.add(index, "    .registers " + (SmaliParser.getMethodRegistersNumber(methodText) + missingRegisters));
					// Retrieve a sufficient number of unused registers.
					List<Integer> unusedRegisters = new ArrayList<>(SmaliParser.REGISTERS_NUMBERS);
					unusedRegisters.removeAll(availableRegisters);
					// This should not happen, because it means we require more registers than available in smali.
					if (unusedRegisters.size() < missingRegisters) {
						throw new UnsupportedFrameworkException();
					}
					// Add the unused registers to the available ones.
					for (int counter = 0; counter < missingRegisters; counter++) {
						availableRegisters.add(unusedRegisters.get(counter));
					}
				}
				Log.i("giovannibozzano.PatcherPowerManagerService", "Available registers: " + availableRegisters.size());
				int register1 = availableRegisters.get(0);
				int register2 = availableRegisters.get(1);
				int register3 = availableRegisters.get(2);
				// Start injecting code.
				index++;
				methodLines.add(index++, "");
				methodLines.add(index++, "    # @ WAKEBLOCK");
				methodLines.add(index++, "");
				methodLines.add(index++, "    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->getInstance()Lcom/giovannibozzano/wakeblock/WakeBlockService;");
				methodLines.add(index++, "");
				methodLines.add(index++, "    move-result-object v" + register1);
				methodLines.add(index++, "");
				methodLines.add(index++, "    iget-object v" + register2 + ", p1, Lcom/android/server/power/PowerManagerService$WakeLock;->mLock:Landroid/os/IBinder;");
				methodLines.add(index++, "");
				methodLines.add(index++, "    iget-object v" + register3 + ", p1, Lcom/android/server/power/PowerManagerService$WakeLock;->mTag:Ljava/lang/String;");
				methodLines.add(index++, "");
				methodLines.add(index++, "    invoke-virtual {v" + register1 + ", v" + register2 + ", v" + register3 + "}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->removeWakeLockLocked(Landroid/os/IBinder;Ljava/lang/String;)V");
				methodLines.add(index++, "");
				methodLines.add(index, "    # # WAKEBLOCK");
				// This is the last injection, break the loop to avoid duplicates.
				break;
			}
		}
		// If we did not inject any code, we failed.
		if (methodLines.size() <= originalMethodLineSize) {
			throw new UnsupportedFrameworkException();
		}
		// Create the new method.
		StringBuilder newMethod = new StringBuilder();
		for (int index = 0; index < methodLines.size(); index++) {
			newMethod.append(methodLines.get(index));
			if (index < methodLines.size() - 1) {
				newMethod.append('\n');
			}
		}
		// Replace the old method with the new one.
		return text.replace(methodText, newMethod.toString());
	}

	@Override
	public void patch() throws UnsupportedFrameworkException, SystemAlreadyPatchedException
	{
		List<String> lines;
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(this.context.getCacheDir(), this.filePath)))) {
			lines = bufferedReader.lines().collect(Collectors.toList());
		} catch (IOException exception) {
			exception.printStackTrace();
			throw new UnsupportedFrameworkException();
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (String line : lines) {
			stringBuilder.append(line);
			stringBuilder.append('\n');
		}
		String text = stringBuilder.toString();
		if (text.contains("wakeblock")) {
			throw new SystemAlreadyPatchedException();
		}
		IMethodPatcher apiDependantMethodPatch = PatcherPowerManagerService.PATCH_ACQUIRE_WAKELOCK_INTERNAL.get(VERSION.SDK_INT);
		if (apiDependantMethodPatch != null) {
			text = apiDependantMethodPatch.patch(lines, text);
		} else {
			text = PatcherPowerManagerService.patchAcquireWakeLockInternalDefault(lines, text);
		}
		apiDependantMethodPatch = PatcherPowerManagerService.PATCH_REMOVE_WAKELOCK_LOCKED.get(VERSION.SDK_INT);
		if (apiDependantMethodPatch != null) {
			text = apiDependantMethodPatch.patch(lines, text);
		} else {
			text = PatcherPowerManagerService.patchRemoveWakeLockLockedDefault(lines, text);
		}
		try (PrintWriter printWriter = new PrintWriter(new File(this.context.getCacheDir(), "/patcher/PowerManagerService.smali"), "UTF-8")) {
			printWriter.print(text);
		} catch (IOException exception) {
			exception.printStackTrace();
			throw new UnsupportedFrameworkException();
		}
	}
}
