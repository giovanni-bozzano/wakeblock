package com.giovannibozzano.wakeblock.patcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmaliParser
{
	public static final List<Integer> REGISTERS_NUMBERS = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
	private static final String[] LABEL_TYPES = new String[] { ":cond_", ":try_start_", ":try_end_", ":catch_", ":catchall_" };

	public static String getMethodText(List<String> lines, List<String> matches)
	{
		StringBuilder methodLines = new StringBuilder();
		boolean methodStarted = false;
		for (String line : lines) {
			if (line.contains(".method")) {
				boolean rightMethod = true;
				for (String match : matches) {
					if (!line.contains(match)) {
						rightMethod = false;
						break;
					}
				}
				if (rightMethod) {
					methodStarted = true;
				}
			}
			if (methodStarted) {
				methodLines.append('\n');
				methodLines.append(line);
				if (line.contains(".end method")) {
					return methodLines.toString();
				}
			}
		}
		return methodLines.toString();
	}

	public static int getMethodRegistersNumber(String methodText)
	{
		String[] lines = methodText.split("\n");
		for (String line : lines) {
			if (line.contains(".registers ")) {
				return Integer.parseInt(line.split(".registers ")[1]);
			}
		}
		return 0;
	}

	public static List<Integer> getMethodRegisters(String methodText)
	{
		String[] lines = methodText.split("\n");
		List<Integer> methodRegisters = new ArrayList<>();
		for (String line : lines) {
			SmaliParser.getLineRegisters(line, true).forEach(register -> {
				if (!methodRegisters.contains(register)) {
					methodRegisters.add(register);
				}
			});
		}
		return methodRegisters;
	}

	public static List<String> getMethodLabels(String methodText)
	{
		String[] lines = methodText.split("\n");
		List<String> methodLabels = new ArrayList<>();
		for (String line : lines) {
			SmaliParser.getLineLabels(line).forEach(label -> {
				if (!methodLabels.contains(label)) {
					methodLabels.add(label);
				}
			});
		}
		return methodLabels;
	}

	public static List<Integer> getLineRegisters(String line, boolean fourBitsOnly)
	{
		List<Integer> lineRegisters = new ArrayList<>();
		int index = line.indexOf('v');
		while (index >= 0) {
			StringBuilder registerNumberString = new StringBuilder();
			int nextIndex = index + 1;
			while (line.length() > nextIndex && Character.isDigit(line.charAt(nextIndex))) {
				registerNumberString.append(line.charAt(nextIndex));
				nextIndex++;
			}
			if (registerNumberString.length() > 0) {
				int registerNumber = Integer.parseInt(registerNumberString.toString());
				if (!lineRegisters.contains(registerNumber)) {
					lineRegisters.add(registerNumber);
				}
			}
			index = line.indexOf('v', index + 1);
		}
		if (lineRegisters.size() == 2) {
			Pattern pattern = Pattern.compile(Pattern.quote("v" + lineRegisters.get(0)) + "(.*?)" + Pattern.quote("v" + lineRegisters.get(1)));
			Matcher matcher = pattern.matcher(line);
			if (matcher.find() && matcher.group(1).contains(" .. ")) {
				int maximumRegister = lineRegisters.get(1);
				lineRegisters.remove(1);
				for (int register = lineRegisters.get(0) + 1; register < maximumRegister; register++) {
					lineRegisters.add(register);
				}
				lineRegisters.add(maximumRegister);
			}
		}
		if (fourBitsOnly) {
			lineRegisters.removeIf(register -> !SmaliParser.REGISTERS_NUMBERS.contains(register));
		}
		return lineRegisters;
	}

	private static List<String> getLineLabels(String line)
	{
		List<String> lineLabels = new ArrayList<>();
		for (String labelType : SmaliParser.LABEL_TYPES) {
			String temporaryLine = line;
			while (temporaryLine.contains(labelType)) {
				temporaryLine = temporaryLine.split(labelType)[1];
				String label;
				Pattern pattern = Pattern.compile("^[0-9a-fA-F]+$");
				if (temporaryLine.length() >= 3 && pattern.matcher(temporaryLine.substring(0, 3)).matches()) {
					label = temporaryLine.substring(0, 3);
				} else if (temporaryLine.length() >= 2 && pattern.matcher(temporaryLine.substring(0, 2)).matches()) {
					label = temporaryLine.substring(0, 2);
				} else {
					label = temporaryLine.substring(0, 1);
				}
				if (!lineLabels.contains(label)) {
					lineLabels.add(label);
				}
			}
		}
		return lineLabels;
	}
}
