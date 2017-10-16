package com.expensesEye.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.expensesEye.common.UserTestMethods;
import com.expensesEye.config.SecurityConfig;
import com.expensesEye.config.SpringBootConfig;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootConfig.class, SecurityConfig.class })
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class UserApiTest implements UserTestMethods {

	private final static String USERS_PATH = "/api/users";

	@Autowired
	protected WebApplicationContext webApplicationContext;

	protected MockMvc mockMvc;

	private User user1;
	private User user2;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Test
	@WithMockUser(roles = { "ADMIN" })
	public void testUpdateUserWitAdminAuthorizedUser() throws Exception {
		createUsers();
		testUpdateUser(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@WithMockUser(roles = { "USER_MANAGER" })
	public void testUpdateUserWithManagerAuthorizedUser() throws Exception {
		createUsers();
		testUpdateUser(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@WithMockUser(roles = { "ADMIN" })
	public void testDeleteUserWitAdminAuthorizedUser() throws Exception {
		createUsers();
		testDeleteUser(MockMvcResultMatchers.status().isOk(), USERS_PATH, user2.getId());
	}

	@Test
	@WithMockUser(roles = { "USER_MANAGER" })
	public void testDeleteUserWithManagerAuthorizedUser() throws Exception {
		createUsers();
		testDeleteUser(MockMvcResultMatchers.status().isOk(), USERS_PATH, user2.getId());
	}

	@Test
	@WithMockUser(roles = { "REGULAR_USER" })
	public void testDeleteUserWithUnAuthorizedUser() throws Exception {
		testDeleteUser(MockMvcResultMatchers.status().isForbidden(), USERS_PATH, 1);
	}

	@Test
	@WithMockUser(roles = { "ADMIN" })
	public void testGetAllUsersWithAdminAuthorizedUser() throws Exception {
		createUsers();
		testReturnedListSize(MockMvcResultMatchers.status().isOk(), USERS_PATH, 2);
	}

	@Test
	@WithMockUser(roles = { "USER_MANAGER" })
	public void testGetAllUsersWithManagerAuthorizedUser() throws Exception {
		testReturnedListSize(MockMvcResultMatchers.status().isOk(), USERS_PATH, 0);
	}

	@Test
	@WithMockUser(roles = { "REGULAR_USER" })
	public void testGetAllUsersWithUnAuthorizedUser() throws Exception {
		testReturnedListSize(MockMvcResultMatchers.status().isForbidden(), USERS_PATH, null);
	}

	private void testUpdateUser(ResultMatcher expectedResult) throws Exception {
		User targetUser = new User("Ahmed", "Hamouda", "ahmed@msn.com", "free", Arrays.asList(UserRole.ADMIN));
		targetUser.setId(user1.getId());

		mockMvc.perform(put(USERS_PATH, targetUser).contentType(MediaType.APPLICATION_JSON)
				.content(MAPPER.writeValueAsString(targetUser))).andExpect(expectedResult);
	}

	private void createUsers() throws Exception {
		User tempUser1 = new User("Ahmed", "Hamouda", "ahmed@hotmail.com", "free", Arrays.asList(UserRole.ADMIN), true);
		testCreatedObjectId(USERS_PATH, tempUser1);
		user1 = getJavaObject(USERS_PATH + "/1", User.class);

		User tempUser2 = new User("Quynh", "To Tuan", "quynh@hotmail.com", "Nice", Arrays.asList(UserRole.ADMIN), true);
		testCreatedObjectId(USERS_PATH, tempUser2);
		user2 = getJavaObject(USERS_PATH + "/2", User.class);
	}

	@Override
	public MockMvc getMockMvc() {
		return mockMvc;
	}
}
