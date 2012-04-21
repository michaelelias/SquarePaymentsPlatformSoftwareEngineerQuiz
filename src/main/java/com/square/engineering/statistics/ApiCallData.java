package com.square.engineering.statistics;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class ApiCallData implements Delayed {

	public long time;
	public String method;
	public int statusCode;

	public ApiCallData() {
	}

	public ApiCallData(long time, String method, int statusCode) {
		super();
		this.time = time;
		this.method = method;
		this.statusCode = statusCode;
	}

	@Override
	public String toString() {
		return time + " " + method + " " + statusCode;
	}

	public static ApiCallData fromLog(String logLine) {
		String[] data = logLine.split(" ");
		return new ApiCallData(Long.parseLong(data[0]), data[1], Integer.parseInt(data[2]));
	}

	@Override
	public int compareTo(Delayed arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		// TODO Auto-generated method stub
		return 0;
	}
}
