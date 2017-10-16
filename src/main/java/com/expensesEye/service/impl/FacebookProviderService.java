package com.expensesEye.service.impl;

import static com.expensesEye.constants.OAuthConstants.CLIENT_ID_KEY;
import static com.expensesEye.constants.OAuthConstants.CLIENT_SECRET;
import static com.expensesEye.constants.OAuthConstants.CODE_KEY;
import static com.expensesEye.constants.OAuthConstants.REDIRECT_URI_KEY;

import java.util.Arrays;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
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

@Service(value = "facebookProvider")
@Transactional
public class FacebookProviderService implements ProviderService {
	private static final Logger LOGGER = Logger.getLogger(FacebookProviderService.class);

	private Client restClient;
	private WebTarget webTarget;

	@Value("${facebook.secret}")
	private String facebookSecret;

	private final String endPointUrl = "https://graph.facebook.com";
	private final String accessTokenUrl = "/oauth/access_token";
	private final String graphApiUrl = "/me";
	private final String pictureUrl = "/picture";

	public FacebookProviderService() {
		restClient = ClientBuilder.newClient();
		webTarget = restClient.target(endPointUrl);
	}

	@Override
	public String getAccessToken(Payload payload) {
		if (payload == null) {
			LOGGER.debug("Given payload is null");
			return null;
		}

		WebTarget target = webTarget.path(accessTokenUrl).queryParam(CLIENT_ID_KEY, payload.getClientId())
				.queryParam(REDIRECT_URI_KEY, payload.getRedirectUri()).queryParam(CLIENT_SECRET, facebookSecret)
				.queryParam(CODE_KEY, payload.getCode());

		try {
			Response response = target.request("text/plain").accept(MediaType.TEXT_PLAIN).get();
			Map<String, Object> responseMap = JSONUtils.getMapFromResponse(response);
			return responseMap.get("access_token") == null ? null : responseMap.get("access_token").toString();
		} catch (Exception ex) {
			LOGGER.error(ErrorMessages.FAILDED_PARSE_RESPONSE);
			return null;
		}
	}

	@Override
	public User getUser(String accessToken, boolean withImage) {
		Map<String, Object> userMap = this.getUserMap(accessToken);
		User user = createUser(userMap);
		if (user != null && withImage) {
			String userId = userMap.get("id") == null ? null : userMap.get("id").toString();
			user.setImage(getImage(accessToken, userId));
		}
		return user;
	}

	private byte[] getImage(String accessToken, String userId) {
		if (StringUtils.isEmpty(accessToken)) {
			LOGGER.debug("Given accessToken is null");
			return null;
		}

		StringBuilder path = new StringBuilder().append(endPointUrl).append("/").append(userId).append(pictureUrl)
				.append("?access_token=").append(accessToken);
		try {
			return getImage(path.toString());
		} catch (Exception ex) {
			LOGGER.error("Failed to get image");
			return null;
		}
	}

	private Map<String, Object> getUserMap(String accessToken) {
		if (StringUtils.isEmpty(accessToken)) {
			LOGGER.debug("Given accessToken is null");
			return null;
		}

		WebTarget target = webTarget.path(graphApiUrl).queryParam("access_token", accessToken).queryParam("fields",
				"id,email,first_name,last_name");
		try {
			return JSONUtils.getMapFromResponse(target.request("text/plain").get());
		} catch (Exception ex) {
			LOGGER.error(ErrorMessages.FAILDED_PARSE_RESPONSE);
			return null;
		}
	}

	private User createUser(Map<String, Object> userMap) {
		if (userMap == null)
			return null;

		String password = userMap.get("id") == null ? null : userMap.get("id").toString();
		String email = userMap.get("email") == null ? null : userMap.get("email").toString();
		String firstName = userMap.get("first_name") == null ? null : userMap.get("first_name").toString();
		String lastName = userMap.get("last_name") == null ? null : userMap.get("last_name").toString();

		return new User(firstName, lastName, email, password, Arrays.asList(UserRole.REGULAR_USER), true);
	}
}
