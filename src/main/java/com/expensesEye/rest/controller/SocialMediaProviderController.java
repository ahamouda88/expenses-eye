package com.expensesEye.rest.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.expensesEye.constants.PathConstants;
import com.expensesEye.model.CurrentUserDetails;
import com.expensesEye.model.ErrorMessages;
import com.expensesEye.persist.entity.Payload;
import com.expensesEye.persist.entity.User;
import com.expensesEye.service.ProviderService;
import com.expensesEye.service.RegistrationService;
import com.expensesEye.utils.ResponseFactoryUtils;

@RestController
@RequestMapping(value = PathConstants.AUTH_PATH)
public class SocialMediaProviderController {

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	@Qualifier("facebookProvider")
	private ProviderService facebookProvider;

	@Autowired
	@Qualifier("googleProvider")
	private ProviderService googleProvider;

	@RequestMapping(value = PathConstants.FACEBOOK_PATH, method = RequestMethod.POST)
	public ResponseEntity<?> loginFacebook(@RequestBody Payload payload, @Context HttpServletRequest request) {
		return loginProvider(payload, facebookProvider);
	}

	@RequestMapping(value = PathConstants.GOOGLE_PATH, method = RequestMethod.POST)
	public ResponseEntity<?> loginGoogle(@RequestBody Payload payload, @Context HttpServletRequest request) {
		return loginProvider(payload, googleProvider);
	}

	private ResponseEntity<?> loginProvider(Payload payload, ProviderService providerService) {
		// 1) Get Access token
		String accessToken = providerService.getAccessToken(payload);

		if (accessToken == null) {
			return ResponseFactoryUtils.createFailResponse(HttpStatus.BAD_REQUEST,
					ErrorMessages.FAILED_PROVIDER_GET_TOKEN);
		}

		// 2) Get User using the access token
		User user = providerService.getUser(accessToken, true);
		if (user == null) {
			return ResponseFactoryUtils.createFailResponse(HttpStatus.BAD_REQUEST,
					ErrorMessages.FAILED_PROVIDER_GET_USER);
		}

		// 3) Register user
		user = registrationService.providerLogin(user);
		if (user == null) {
			return ResponseFactoryUtils.createFailResponse(HttpStatus.BAD_REQUEST, ErrorMessages.FAILED_PROVIDER_LOGIN);
		}

		// 4) Authenticate user
		CurrentUserDetails currentUser = new CurrentUserDetails(user);
		Authentication auth = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);
		return ResponseFactoryUtils.createSuccessResponse(user, HttpStatus.OK);
	}
}
