package com.giovannibozzano.wakeblock.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ExecuteAsRoot
{
	public static boolean canRunRootCommands()
	{
		try {
			Process suProcess = Runtime.getRuntime().exec("su");
			DataOutputStream dataOutputStream = new DataOutputStream(suProcess.getOutputStream());
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(suProcess.getInputStream()));
			dataOutputStream.writeBytes("id\n");
			dataOutputStream.flush();
			String currentUID = bufferedReader.readLine();
			dataOutputStream.writeBytes("exit\n");
			dataOutputStream.flush();
			return currentUID != null && currentUID.contains("uid=0");
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		return false;
	}

	public static boolean execute(List<String> commandList)
	{
		try {
			Process suProcess = Runtime.getRuntime().exec("su");
			try (DataOutputStream dataOutputStream = new DataOutputStream(suProcess.getOutputStream()); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(suProcess.getInputStream()))) {
				dataOutputStream.writeBytes("id\n");
				dataOutputStream.flush();
				String currentUID = bufferedReader.readLine();
				if (currentUID == null || !currentUID.contains("uid=0")) {
					dataOutputStream.writeBytes("exit\n");
					dataOutputStream.flush();
					return false;
				}
				for (String currentCommand : commandList) {
					dataOutputStream.writeBytes(currentCommand + "\n");
					dataOutputStream.flush();
				}
				dataOutputStream.writeBytes("exit\n");
				dataOutputStream.flush();
				return suProcess.waitFor() == 0;
			}
		} catch (IOException | InterruptedException exception) {
			exception.printStackTrace();
		}
		return false;
	}

	public static String read(List<String> commandList)
	{
		try {
			Process suProcess = Runtime.getRuntime().exec("su");
			try (DataOutputStream dataOutputStream = new DataOutputStream(suProcess.getOutputStream()); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(suProcess.getInputStream()))) {
				dataOutputStream.writeBytes("id\n");
				dataOutputStream.flush();
				String currentUID = bufferedReader.readLine();
				if (currentUID == null || !currentUID.contains("uid=0")) {
					dataOutputStream.writeBytes("exit\n");
					dataOutputStream.flush();
					return null;
				}
				String result = null;
				for (int index = 0; index < commandList.size(); index++) {
					String currentCommand = commandList.get(index);
					dataOutputStream.writeBytes(currentCommand + "\n");
					dataOutputStream.flush();
					if (index == commandList.size() - 1) {
						Thread thread = new Thread()
						{
							@Override
							public void run()
							{
								try {
									sleep(5000L);
								} catch (InterruptedException exception) {
								}
								try {
									suProcess.getInputStream().close();
								} catch (IOException exception) {
									exception.printStackTrace();
								}
							}
						};
						thread.start();
						result = bufferedReader.readLine();
						if (thread.isAlive()) {
							thread.interrupt();
						}
					}
				}
				dataOutputStream.writeBytes("exit\n");
				dataOutputStream.flush();
				return result;
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		return null;
	}
}
