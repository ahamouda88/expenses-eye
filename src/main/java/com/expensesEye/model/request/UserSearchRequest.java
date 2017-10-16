package com.expensesEye.model.request;

import java.io.Serializable;
import java.util.List;

import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;

/**
 * A class that contains all the parameters needed to perform search on an
 * {@link User} model/entity
 */
public class UserSearchRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Long id;
	private final String firstName;
	private final String lastName;
	private final String email;
	private final Boolean verified;
	private final Boolean locked;
	private final Integer numOfAttempts;
	private final List<UserRole> roles;

	private UserSearchRequest(Builder builder) {
		this.id = builder.id;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.email = builder.email;
		this.verified = builder.verified;
		this.locked = builder.locked;
		this.numOfAttempts = builder.numOfAttempts;
		this.roles = builder.roles;
	}

	public Long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public Boolean isVerified() {
		return verified;
	}

	public Boolean isLocked() {
		return locked;
	}

	public Integer getNumOfAttempts() {
		return numOfAttempts;
	}

	public List<UserRole> getRoles() {
		return roles;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Long id;
		private String firstName;
		private String lastName;
		private String email;
		private Boolean verified;
		private Boolean locked;
		private Integer numOfAttempts;
		private List<UserRole> roles;

		public Builder id(Long id) {
			this.id = id;
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

		public Builder roles(List<UserRole> roles) {
			this.roles = roles;
			return this;
		}

		public UserSearchRequest build() {
			return new UserSearchRequest(this);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((locked == null) ? 0 : locked.hashCode());
		result = prime * result + ((numOfAttempts == null) ? 0 : numOfAttempts.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((verified == null) ? 0 : verified.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserSearchRequest other = (UserSearchRequest) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (locked == null) {
			if (other.locked != null)
				return false;
		} else if (!locked.equals(other.locked))
			return false;
		if (numOfAttempts == null) {
			if (other.numOfAttempts != null)
				return false;
		} else if (!numOfAttempts.equals(other.numOfAttempts))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		if (verified == null) {
			if (other.verified != null)
				return false;
		} else if (!verified.equals(other.verified))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserSearchRequest [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email="
				+ email + ", verified=" + verified + ", locked=" + locked + ", numOfAttempts=" + numOfAttempts
				+ ", roles=" + roles + "]";
	}

}
