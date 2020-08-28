package com.giovannibozzano.wakeblock.patcher.classes;

import java.util.List;

@FunctionalInterface interface IMethodPatcher
{
	String patch(List<String> lines, String text);
}
