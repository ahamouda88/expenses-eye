package com.expensesEye.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.hamcrest.Matchers;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface UserTestMethods {

	public static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * A method to test the creation of an object, and test if the returned object
	 * id is not null
	 * 
	 * @param path
	 *            the url of the endpoint
	 * @param object
	 *            the entity/object that need to be added
	 * @throws Exception
	 *             if the test fails, and creation didn't happen
	 */
	public default <T> void testCreatedObjectId(String path, T object) throws Exception {
		getMockMvc()
				.perform(MockMvcRequestBuilders.post(path, object).contentType(MediaType.APPLICATION_JSON)
						.content(MAPPER.writeValueAsString(object)))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.notNullValue()));
	}

	/**
	 * A method that tests the number of returned list
	 * 
	 * @param expectedResult
	 *            expected result status
	 * @param path
	 *            the url of the endpoint
	 * @param size
	 *            the expected size of the returned list
	 * @throws Exception
	 *             if the test fails, and list is not returned
	 */
	public default <T> void testReturnedListSize(ResultMatcher expectedResult, String path, Integer size)
			throws Exception {
		ResultActions action = getMockMvc().perform(MockMvcRequestBuilders.get(path)).andExpect(expectedResult);
		if (size != null && size >= 0) {
			action.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(size)));
		}
	}

	/**
	 * A method that returns makes a get request, and parse the returned json string
	 * into a java object
	 * 
	 * @param path
	 *            the url of the endpoint
	 * @param classType
	 *            the type wanted for the created java object
	 * @return the java object
	 * @throws Exception
	 *             if wasn't able to parse the returned json object to a java object
	 */
	public default <T> T getJavaObject(String path, Class<T> classType) throws Exception {
		MvcResult result = getMockMvc().perform(get(path)).andReturn();
		return MAPPER.readValue(result.getResponse().getContentAsString(), classType);
	}

	/**
	 * A method used to test deleting a user
	 * 
	 * @param expectedResult
	 *            expected result status
	 * @param path
	 *            the url of the endpoint
	 * @param userId
	 *            the id of the user to be deleted
	 * @throws Exception
	 *             if deletion was failed
	 */
	public default void testDeleteUser(ResultMatcher expectedResult, String path, long userId) throws Exception {
		getMockMvc().perform(MockMvcRequestBuilders.delete(path + "?id=" + userId)).andExpect(expectedResult);
	}

	public MockMvc getMockMvc();
}
