package com.camerinfolks.controller;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SimpleCORSFilter implements Filter{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
	     HttpServletResponse response = (HttpServletResponse) res;
	     response.setHeader("Access-Control-Allow-Private-Network", "true");
	        response.setHeader("Access-Control-Allow-Origin", "*");
	        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	        response.setHeader("Access-Control-Max-Age", "3600");
	        response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers,*");
	        chain.doFilter(req, res);
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
