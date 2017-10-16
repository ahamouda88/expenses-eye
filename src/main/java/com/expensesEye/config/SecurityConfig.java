package com.expensesEye.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;

import com.expensesEye.security.CsrfHeaderFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private LogoutSuccessHandler successHandler;

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	@Qualifier("loginAuthenticationEntryPoint")
	private AuthenticationEntryPoint loginAuthenticationEntryPoint;

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**").antMatchers("/webjars/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/", "/all/view/user").permitAll()
				.antMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
				// RESTful API Requests
				.antMatchers(HttpMethod.POST, "/api/users").hasAnyRole("USER_MANAGER", "ADMIN")
				.antMatchers(HttpMethod.PUT, "/api/users").hasAnyRole("USER_MANAGER", "ADMIN")
				.antMatchers(HttpMethod.GET, "/api/users/verify/**").permitAll()
				.antMatchers(HttpMethod.DELETE, "/api/users").hasAnyRole("USER_MANAGER", "ADMIN")
				.antMatchers(HttpMethod.GET, "/api/users").hasAnyRole("USER_MANAGER", "ADMIN")
				// For User registration and verification
				.antMatchers(HttpMethod.POST, "/api/users/register").permitAll()
				.antMatchers(HttpMethod.PUT, "/api/users/register").permitAll()
				.antMatchers(HttpMethod.GET, "/api/users/verify/**").permitAll()
				// For social media providers authentication
				.antMatchers(HttpMethod.POST, "/auth/**").permitAll()
				// AngularJS routes
				.antMatchers("/usr/view/userDetails").permitAll()
				.antMatchers("/usr/**").hasAnyRole("REGULAR_USER", "ADMIN")
				.antMatchers("/usrmgr/**").hasAnyRole("USER_MANAGER", "ADMIN")
				.anyRequest()
					.authenticated()
				.and()
					.formLogin()
					.loginPage("/all/view/login")
					.permitAll()
				.and()
					.httpBasic()
					.authenticationEntryPoint(loginAuthenticationEntryPoint)
				.and()
					.logout()
					.logoutUrl("/logout")
					.logoutSuccessHandler(successHandler)
					.deleteCookies("JSESSIONID", "CURRENT_USER")
					.invalidateHttpSession(false)
					.permitAll()
				.and()
					.csrf()
					.disable()
				.addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
				.exceptionHandling()
					.authenticationEntryPoint(loginAuthenticationEntryPoint);
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}
}
