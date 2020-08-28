package com.giovannibozzano.wakeblock.patcher.classes;

import com.giovannibozzano.wakeblock.exceptions.SystemAlreadyPatchedException;
import com.giovannibozzano.wakeblock.exceptions.UnsupportedFrameworkException;

@FunctionalInterface interface IPatcher
{
	void patch() throws UnsupportedFrameworkException, SystemAlreadyPatchedException;
}
