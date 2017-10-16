package com.expensesEye.model.request;

import java.io.Serializable;

import com.expensesEye.persist.entity.User;

/**
 * A class that contains all the parameters needed to perform an update on a
 * {@link User} model/entity
 */
public class UserUpdateRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private final byte[] image;
	private final String firstName;
	private final String lastName;
	private final String password;
	private final String email;
	private final Boolean verified;
	private final Boolean locked;
	private final Integer numOfAttempts;

	private UserUpdateRequest(Builder builder) {
		this.image = builder.image;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.password = builder.password;
		this.email = builder.email;
		this.verified = builder.verified;
		this.locked = builder.locked;
		this.numOfAttempts = builder.numOfAttempts;
	}

	public byte[] getImage() {
		return image;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public Boolean getVerified() {
		return verified;
	}

	public Boolean getLocked() {
		return locked;
	}

	public Integer getNumOfAttempts() {
		return numOfAttempts;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private byte[] image;
		private String firstName;
		private String lastName;
		private String password;
		private String email;
		private Boolean verified;
		private Boolean locked;
		private Integer numOfAttempts;

		public Builder image(byte[] image) {
			this.image = image;
			return this;
		}

		public Builder firstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		public Builder lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder verified(Boolean verified) {
			this.verified = verified;
			return this;
		}

		public Builder locked(Boolean locked) {
			this.locked = locked;
			return this;
		}

		public Builder numOfAttempts(Integer numOfAttempts) {
			this.numOfAttempts = numOfAttempts;
			return this;
		}

		public UserUpdateRequest build() {
			return new UserUpdateRequest(this);
		}

	}
}
