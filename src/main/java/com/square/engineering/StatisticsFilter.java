package com.square.engineering;

import java.io.IOException;

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
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		final ApiCallStats stats = new ApiCallStats();
		stats.method = getMethod(request);
		chain.doFilter(request, new HttpServletResponseWrapper((HttpServletResponse) response){

			@Override
			public void setStatus(int sc) {
				super.setStatus(sc);
				stats.statusCode = sc;
			}

		});
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	private String getMethod(ServletRequest request){
		return ((HttpServletRequest)request).getMethod();
	}
	
	public class ApiCallStats {
		
		public String method; 
		public int statusCode;
		public long time;
		
		public ApiCallStats(){
			time = System.currentTimeMillis();
		}
		
		public String toString(){
			return "";
		}
	}

}
