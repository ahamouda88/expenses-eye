package com.expensesEye.service;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

import com.expensesEye.persist.entity.Payload;
import com.expensesEye.persist.entity.User;

public interface ProviderService {

	/**
	 * This method takes a {@link Payload} that contains the client Id, code, and
	 * redirect url and these are being used for creating the URI for retrieving an
	 * access token
	 * 
	 * @param payload
	 *            a {@link Payload} object
	 * @return the access token for accessing the provider's API
	 */
	public String getAccessToken(Payload payload);

	/**
	 * This method returns a {@link User} object given the acess token
	 * 
	 * @param accessToken
	 *            an access token for retrieving the user info
	 * @param withImage
	 *            a boolean to either retrieve user's image or not
	 * @return a {@link User} object
	 */
	public User getUser(String accessToken, boolean withImage);

	/**
	 * This method returns a byte array representing an image given the image's url
	 * 
	 * @param imageUrl
	 *            the image's url
	 * @return a byte array representing the user's image
	 * @throws Exception
	 *             throws exception when failed to retrieve image
	 */
	public default byte[] getImage(String imageUrl) throws Exception {
		if (StringUtils.isEmpty(imageUrl))
			return null;
		String tempImage = "../expensesEye/tmpImage.jpg";
		File file = new File(tempImage);
		FileUtils.copyURLToFile(new URL(imageUrl), file);

		Path path = Paths.get(tempImage);
		byte[] result = Files.readAllBytes(path);
		Files.delete(path);

		return result;
	}

}
