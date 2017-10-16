package com.expensesEye.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutSuccess implements LogoutSuccessHandler {

	private static Logger logger = LoggerFactory.getLogger(CustomLogoutSuccess.class);

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		if (authentication != null && authentication.getDetails() != null) {
			try {
				request.getSession().invalidate();
			} catch (Exception e) {
				logger.error("Unable to invalidate the user's session!", e);
			}
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

}