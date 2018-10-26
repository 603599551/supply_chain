package com.utils;

import java.util.UUID;

public class UUIDTool {

	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
