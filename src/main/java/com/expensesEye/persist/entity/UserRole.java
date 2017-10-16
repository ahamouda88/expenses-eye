package com.expensesEye.persist.entity;

/**
 * An Enum that represents the different roles of a user for-example: Admin, User Manager, and Regular User.
 */
public enum UserRole {

	ADMIN("Admin", 1),
	USER_MANAGER("User Manager", 2),
	REGULAR_USER("Regular User", 3);

	private String title;
	private int rank;

	private UserRole(String title, int rank) {
		this.title = title;
		this.rank = rank;
	}

	public String title() {
		return title;
	}

	public int rank() {
		return rank;
	}

	public static UserRole getFromRank(int rank) {
		for (UserRole role : UserRole.values()) {
			if (role.rank() == rank) return role;
		}
		return null;
	}

	public static UserRole getUserRole(String role) {
		return UserRole.valueOf(role);
	}
}
