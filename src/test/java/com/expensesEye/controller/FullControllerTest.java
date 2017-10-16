package com.expensesEye.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.expensesEye.config.SpringBootConfig;
import com.expensesEye.persist.entity.Expense;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootConfig.class)
@ActiveProfiles("test")
public class FullControllerTest {

	private final static String USERS_PATH = "/api/users";
	private final static String EXPENSES_PATH = "/api/expenses";

	@Autowired
	protected WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;
	private ObjectMapper mapper;

	private Long userId1;
	private Long userId2;

	@Before
	public void setUp() throws Exception {
		mapper = new ObjectMapper();
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

		// Test creating valid users
		User user1 = new User("Ahmed", "Hamouda", "ahmed@hotmail.com", "free", Arrays.asList(UserRole.ADMIN), true);
		testCreatedObjectId(USERS_PATH, user1);
		// Test get user, to return the user with the Id
		user1 = getJavaObject(USERS_PATH + "/1", User.class);

		User user2 = new User("Quynh", "To Tuan", "quynh@hotmail.com", "Nice", Arrays.asList(UserRole.ADMIN), true);
		// Test get user, to return the user with the Id
		testCreatedObjectId(USERS_PATH, user2);
		user2 = getJavaObject(USERS_PATH + "/2", User.class);

		// Test creating invalid user with an existing email
		User user3 = new User("Adam", "Sandler", "ahmed@hotmail.com", "Invalid", Arrays.asList(UserRole.USER_MANAGER));
		mockMvc.perform(MockMvcRequestBuilders.post(USERS_PATH, user3).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(user3))).andExpect(MockMvcResultMatchers.status().isBadRequest());

		userId1 = getJavaObject(USERS_PATH + "/email/ahmed@hotmail.com", User.class).getId();
		userId2 = getJavaObject(USERS_PATH + "/email/quynh@hotmail.com", User.class).getId();

		// Creating Expenses
		Expense expense1 = new Expense(100.0, 10L, "Meal", "No comment");
		expense1.setUser(user1);
		testCreatedObjectId(EXPENSES_PATH, expense1);

		Expense expense2 = new Expense(1400.0, 20L, "Travel", "Visting the Camp Nou");
		expense2.setUser(user1);
		testCreatedObjectId(EXPENSES_PATH, expense2);

		Expense expense3 = new Expense(500.0, 30L, "Taxes", "State taxes");
		expense3.setUser(user1);
		testCreatedObjectId(EXPENSES_PATH, expense3);

		Expense expense4 = new Expense(3000.0, 45L, "Travel", "Visiting Berlin");
		expense4.setUser(user2);
		testCreatedObjectId(EXPENSES_PATH, expense4);

		Expense expense5 = new Expense(50.0, 60L, "Meal", "Fast food");
		expense5.setUser(user2);
		testCreatedObjectId(EXPENSES_PATH, expense5);

		// Test creating invalid Expense
		Expense expense6 = new Expense(50.0, 60L, null, "Fast food");
		expense6.setUser(user2);
		mockMvc.perform(MockMvcRequestBuilders.post(EXPENSES_PATH, expense6).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(expense6))).andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.messages[0]", Matchers.is("Failed to create expense")))
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void testFunctionality() throws Exception {
		// Test Update User
		List<UserRole> roles = new ArrayList<>();
		roles.add(UserRole.ADMIN);
		User targetUser = new User("Ahmed", "Hamouda", "newEmail@msn.com", "free", roles);
		targetUser.setId(userId1);
		mockMvc.perform(MockMvcRequestBuilders.put(USERS_PATH, targetUser).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(targetUser))).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("newEmail@msn.com")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.verified", Matchers.is(false)));

		// Test get user by Id
		testReturnedFieldMatch(USERS_PATH + "/" + userId2, "email", "quynh@hotmail.com");

		// Test get user by email
		testReturnedFieldMatch(USERS_PATH + "/email/quynh@hotmail.com", "lastName", "To Tuan");

		// Test get user by invalid email
		mockMvc.perform(MockMvcRequestBuilders.get(USERS_PATH + "/email/invalidEmail@m")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.messages[0]",
						Matchers.is("Email param [invalidEmail@m] is invalid")));

		// Test Update Expense
		Expense expense = getJavaObject(EXPENSES_PATH + "/2", Expense.class);
		expense.setAmount(1000.0);

		mockMvc.perform(MockMvcRequestBuilders.put(EXPENSES_PATH, expense).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(expense))).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.amount", Matchers.is(1000.0)));

		// Test Remove Expense
		mockMvc.perform(MockMvcRequestBuilders.delete(EXPENSES_PATH + "?id=4"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.amount", Matchers.is(3000.0)));

		// Verify size after expense removal
		int expectedSize = 1;
		User user = getJavaObject(USERS_PATH + "/2", User.class);
		Assert.assertEquals(expectedSize, user.getExpenses().size());

		// Test Remove Invalid Expense Id
		mockMvc.perform(MockMvcRequestBuilders.delete(EXPENSES_PATH + "?id=7"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@After
	public void verifySize() throws Exception {
		// Test get all expenses BEFORE removing a user
		testReturnedListSize(EXPENSES_PATH, 4);

		// Test get by min and max amount
		testReturnedListSize(EXPENSES_PATH + "/search?minAmount=1000&maxAmount=1000", 1);

		// Test get user expenses
		testReturnedListSize(EXPENSES_PATH + "/search?userId=1", 3);

		// Test get user with specific max amount
		testReturnedListSize(EXPENSES_PATH + "/search?maxAmount=100&userId=1", 1);

		// Test get with start and end times
		testReturnedListSize(EXPENSES_PATH + "/search?startTime=20&endTime=45", 2);

		// Test get user with start and end times
		testReturnedListSize(EXPENSES_PATH + "/search?startTime=15&endTime=25&userId=1", 1);

		// Test get user with invalid min amount
		testReturnedListSize(EXPENSES_PATH + "/search?minAmount=5000&userId=1", 0);

		// Test Get all users
		testReturnedListSize(USERS_PATH, 2);

		// Test remove user
		mockMvc.perform(MockMvcRequestBuilders.delete(USERS_PATH + "?id=" + userId1))
				.andExpect(MockMvcResultMatchers.status().isOk());

		// Test remaining number of users
		testReturnedListSize(USERS_PATH, 1);

		// Test get all expenses AFTER removing a user
		testReturnedListSize(EXPENSES_PATH, 1);
	}

	/*
	 * Generic test methods
	 */
	public <T> void testCreatedObjectId(String path, T object) throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post(path, object).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(object))).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.notNullValue()));
	}

	public <T> void testReturnedFieldMatch(String path, String fieldName, String expectedValue) throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(path)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$." + fieldName, Matchers.is(expectedValue)));
	}

	public <T> void testReturnedListSize(String path, int size) throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(path)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(size)));
	}

	public <T> T getJavaObject(String path, Class<T> classType) throws Exception {
		MvcResult result = mockMvc.perform(get(path)).andReturn();
		return mapper.readValue(result.getResponse().getContentAsString(), classType);
	}
}
