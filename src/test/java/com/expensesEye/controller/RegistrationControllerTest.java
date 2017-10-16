package com.expensesEye.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.expensesEye.common.UserTestMethods;
import com.expensesEye.config.SpringBootConfig;
import com.expensesEye.constants.PathConstants;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootConfig.class })
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class RegistrationControllerTest implements UserTestMethods {

	@Autowired
	protected WebApplicationContext webApplicationContext;

	protected MockMvc mockMvc;
	protected ObjectMapper mapper;

	private User user1;
	private User user2;

	private String FULL_REGISTER_PATH = PathConstants.USERS_PATH + PathConstants.REGISTER_PATH;

	@Before
	public void testRegisterUser() throws Exception {
		MockitoAnnotations.initMocks(this);

		mapper = new ObjectMapper();
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

		// Test registering a user
		List<UserRole> roles = new ArrayList<>();
		roles.add(UserRole.ADMIN);
		User user = new User("Adam", "Sandler", "ahmed@hotmail.com", "password", roles);
		MockMultipartFile userFile = new MockMultipartFile("user", "", "application/json",
				mapper.writeValueAsString(user).getBytes());
		mockMvc.perform(fileUpload(FULL_REGISTER_PATH).file(userFile)).andExpect(status().isCreated());

		user = new User("Steven", "Gerrad", "liver@hotmail.com", "password", roles);
		userFile = new MockMultipartFile("user", "", "application/json", mapper.writeValueAsString(user).getBytes());
		mockMvc.perform(fileUpload(FULL_REGISTER_PATH).file(userFile)).andExpect(status().isCreated());
	}

	@Test
	public void testVerifyUser() throws Exception {
		user1 = getJavaObject(PathConstants.USERS_PATH + "/1", User.class);
		Assert.assertEquals(false, user1.isVerified());
		
		user2 = getJavaObject(PathConstants.USERS_PATH + "/2", User.class);
		Assert.assertEquals(false, user2.isVerified());
	}

	@Override
	public MockMvc getMockMvc() {
		return mockMvc;
	}

}
