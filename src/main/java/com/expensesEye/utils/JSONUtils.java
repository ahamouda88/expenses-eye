package com.expensesEye.utils;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A Utility class that perform operations on JSON objects
 */
public final class JSONUtils {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * This methods creates a Map representation of a given {@link Response} which
	 * is being read as a String
	 * 
	 * @param response
	 *            a {@link Response} String
	 * @return a Map representation of the given input String or <b>null</b> if
	 *         failed to parse the given String
	 * @throws JsonParseException
	 *             if failed to parse the given JSON String
	 * @throws JsonMappingException
	 *             if failed to map the given JSON String to a Map object
	 * @throws IOException
	 *             if an I/O issue occured
	 */
	public static Map<String, Object> getMapFromResponse(Response response)
			throws JsonParseException, JsonMappingException, IOException {
		if (response == null)
			return null;

		String result = response.readEntity(String.class);

		return JSONUtils.getJsonMap(result);
	}

	/**
	 * This methods creates a Map representation of a given JSON String
	 * 
	 * @param input
	 *            a JSON String
	 * @return a Map representation of the given input String or <b>null</b> if
	 *         failed to parse the given String
	 * @throws JsonParseException
	 *             if failed to parse the given JSON String
	 * @throws JsonMappingException
	 *             if failed to map the given JSON String to a Map object
	 * @throws IOException
	 *             if an I/O issue occured
	 */
	public static Map<String, Object> getJsonMap(String input)
			throws JsonParseException, JsonMappingException, IOException {
		if (input == null)
			return null;

		return MAPPER.readValue(input, new TypeReference<Map<String, Object>>() {
		});
	}
}
