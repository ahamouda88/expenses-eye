package com.expensesEye.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.expensesEye.common.UserTestMethods;
import com.expensesEye.config.MethodSecurityConfig;
import com.expensesEye.config.SecurityConfig;
import com.expensesEye.config.SpringBootConfig;
import com.expensesEye.constants.PathConstants;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootConfig.class, SecurityConfig.class, MethodSecurityConfig.class })
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class AuthenticationTest implements UserTestMethods {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;
	private ObjectMapper mapper = new ObjectMapper();

	private final static String USERS_PATH = "/api/users";
	private final static String FULL_REGISTER_PATH = PathConstants.USERS_PATH + PathConstants.REGISTER_PATH;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(SecurityMockMvcConfigurers.springSecurity()).build();

		User user = new User("Ahmed", "Hamouda", "ahmed@hotmail.com", "free", Arrays.asList(UserRole.ADMIN), true);
		MockMultipartFile userFile = new MockMultipartFile("user", "", "application/json",
				mapper.writeValueAsString(user).getBytes());
		mockMvc.perform(fileUpload(FULL_REGISTER_PATH).file(userFile)).andExpect(status().isCreated());
	}

	@Test
	public void testSuccessfulAuthentication() throws Exception {
		mockMvc.perform(get(USERS_PATH + "/currentuser").with(httpBasic("ahmed@hotmail.com", "free")))
				.andExpect(status().isOk()).andExpect(authenticated().withUsername("ahmed@hotmail.com"));
	}

	@Test
	public void testFailedAuthentication() throws Exception {
		mockMvc.perform(get(USERS_PATH + "/currentuser").with(httpBasic("ahmed@hotmail.com", "invalid")))
				.andExpect(status().isUnauthorized());
	}

	@Override
	public MockMvc getMockMvc() {
		return mockMvc;
	}
}
