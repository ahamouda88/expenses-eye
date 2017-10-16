package com.expensesEye.service.impl;

import static com.expensesEye.constants.OAuthConstants.AUTH_CODE;
import static com.expensesEye.constants.OAuthConstants.CLIENT_ID_KEY;
import static com.expensesEye.constants.OAuthConstants.CLIENT_SECRET;
import static com.expensesEye.constants.OAuthConstants.CODE_KEY;
import static com.expensesEye.constants.OAuthConstants.GRANT_TYPE_KEY;
import static com.expensesEye.constants.OAuthConstants.REDIRECT_URI_KEY;

import java.util.Arrays;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.expensesEye.model.ErrorMessages;
import com.expensesEye.persist.entity.Payload;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;
import com.expensesEye.service.ProviderService;
import com.expensesEye.utils.JSONUtils;

@Service(value = "googleProvider")
@Transactional
public class GoogleProviderService implements ProviderService {
	private static final Logger LOGGER = Logger.getLogger(GoogleProviderService.class);

	private Client restClient;

	private final String accessTokenUrl = "https://accounts.google.com/o/oauth2/token";
	private final String peopleApiUrl = "https://www.googleapis.com/plus/v1/people/me/openIdConnect";

	@Value("${google.secret}")
	private String googleSecret;

	public GoogleProviderService() {
		restClient = ClientBuilder.newClient();
	}

	@Override
	public String getAccessToken(Payload payload) {
		if (payload == null) {
			LOGGER.debug("Given payload is null");
			return null;
		}

		MultivaluedMap<String, String> accessData = new MultivaluedHashMap<>();
		accessData.add(CLIENT_ID_KEY, payload.getClientId());
		accessData.add(REDIRECT_URI_KEY, payload.getRedirectUri());
		accessData.add(CLIENT_SECRET, googleSecret);
		accessData.add(CODE_KEY, payload.getCode());
		accessData.add(GRANT_TYPE_KEY, AUTH_CODE);

		Response response = restClient.target(accessTokenUrl).request().post(Entity.form(accessData));
		accessData.clear();

		try {
			Map<String, Object> responseMap = JSONUtils.getMapFromResponse(response);
			return responseMap.get("access_token") == null ? null : responseMap.get("access_token").toString();
		} catch (Exception e) {
			LOGGER.error(ErrorMessages.FAILDED_PARSE_RESPONSE);
			return null;
		}
	}

	@Override
	public User getUser(String accessToken, boolean withImage) {
		if (StringUtils.isEmpty(accessToken)) {
			LOGGER.debug("Given accessToken is null");
			return null;
		}

		Response response = restClient.target(peopleApiUrl).request("text/plain")
				.header("Authorization", String.format("Bearer %s", accessToken)).get();
		try {
			Map<String, Object> userInfo = JSONUtils.getMapFromResponse(response);
			return createUser(userInfo);
		} catch (Exception ex) {
			LOGGER.error(ErrorMessages.FAILDED_PARSE_RESPONSE);
			return null;
		}
	}

	private User createUser(Map<String, Object> userMap) {
		if (userMap == null)
			return null;

		String password = userMap.get("sub") == null ? null : userMap.get("sub").toString();
		String email = userMap.get("email") == null ? null : userMap.get("email").toString();
		String firstName = userMap.get("given_name") == null ? null : userMap.get("given_name").toString();
		String lastName = userMap.get("family_name") == null ? null : userMap.get("family_name").toString();
		String imageUrl = userMap.get("picture") == null ? null : userMap.get("picture").toString();

		User user = new User(firstName, lastName, email, password, Arrays.asList(UserRole.REGULAR_USER), true);
		try {
			user.setImage(getImage(imageUrl));
		} catch (Exception e) {
			LOGGER.error(ErrorMessages.FAILED_PROVIDER_GET_IMAGE);
		}
		return user;
	}

}
