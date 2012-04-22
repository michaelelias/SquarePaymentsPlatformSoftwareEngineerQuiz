package com.square.engineering.statistics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;

public class StatisticsFilter implements Filter {

	private static final Logger LOG = Logger.getLogger(StatisticsFilter.class);

	private static final int AVERAGE_MAX_SAMPLE_SIZE = 10;

	private Map<String, List<Integer>> statusCodes;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		statusCodes = new HashMap<String, List<Integer>>();
		statusCodes.put("2xx", new ArrayList<Integer>());
		statusCodes.put("3xx", new ArrayList<Integer>());
		statusCodes.put("4xx", new ArrayList<Integer>());
		statusCodes.put("5xx", new ArrayList<Integer>());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		long time = System.currentTimeMillis();
		final ApiCallData stats = new ApiCallData();
		stats.method = getMethod(request);
		stats.time = time;
		chain.doFilter(request, new HttpServletResponseWrapper((HttpServletResponse) response) {

			@Override
			public void setStatus(int sc) {
				super.setStatus(sc);
				stats.statusCode = sc;
			}

			@Override
			public void setStatus(int sc, String sm) {
				super.setStatus(sc, sm);
				stats.statusCode = sc;
			}

		});
		storeStatusCode(stats.statusCode);
		logStats();
	}

	private void storeStatusCode(int statusCode) {
		List<Integer> statusCodeClassValues = null;
		if (statusCode >= 200 && statusCode < 300) {
			statusCodeClassValues = statusCodes.get("2xx");
		} else if (statusCode >= 300 && statusCode < 400) {
			statusCodeClassValues = statusCodes.get("3xx");
		} else if (statusCode >= 400 && statusCode < 500) {
			statusCodeClassValues = statusCodes.get("4xx");
		} else if (statusCode >= 500 && statusCode < 600) {
			statusCodeClassValues = statusCodes.get("5xx");
		}

		if(statusCodeClassValues != null){
			if (statusCodeClassValues.size() > AVERAGE_MAX_SAMPLE_SIZE) {
				statusCodeClassValues.remove(0);
			}
			statusCodeClassValues.add(statusCode);
		}
	}

	private void logStats() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, List<Integer>> statusCode : statusCodes.entrySet()) {
			sb.append(statusCode.getKey() + "[avg=" + calculateAverage(statusCode.getValue()) + ",variance=" + calculateVariance(statusCode.getValue()) + "], ");
		}
		LOG.info(sb.toString());
	}

	private int calculateVariance(List<Integer> values) {
		if (values.size() == 0) {
			return 0;
		}
		int sum = 0;
		int sumSqr = 0;
		for (int value : values) {
			sum += value;
			sumSqr += Math.pow(value, 2);
		}
		int mean = sum/values.size();
		return (sumSqr - sum*mean) / values.size();
	}

	private int calculateAverage(List<Integer> values) {
		if (values.size() == 0) {
			return 0;
		}
		int sum = 0;
		for (int value : values) {
			sum += value;
		}
		return sum / values.size();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	private String getMethod(ServletRequest request) {
		return ((HttpServletRequest) request).getMethod();
	}

}
