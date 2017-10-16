package com.expensesEye.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component("loginAuthenticationEntryPoint")
public class LoginAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		// Customize error messages being returned
		String message;
		switch (authException.getMessage()) {
		case "UserDetailsService returned null, which is an interface contract violation":
			message = "User with given email is not found";
			break;

		case "Bad credentials":
			message = "Wrong email or password please try again!";
			break;
		default:
			message = authException.getMessage();
			break;
		}
		response.getOutputStream().println("{ \"error\": \"" + message + "\" }");
	}

}
