package com.expensesEye.security;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.expensesEye.security.permission.ExpensePermissionProvider;
import com.expensesEye.security.permission.RegistrationPermissionProvider;
import com.expensesEye.security.permission.UserPermissionProvider;

@Component
public class ApplicationPermissionEvaluator implements PermissionEvaluator {

	@Autowired
	private RegistrationPermissionProvider registartionPermissionProvider;

	@Autowired
	private ExpensePermissionProvider expensePermissionProvider;

	@Autowired
	private UserPermissionProvider userPermissionProvider;

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		if (registartionPermissionProvider.getSupportedPermissions().contains(permission)) {
			return registartionPermissionProvider.isAuthorized(authentication, permission, targetDomainObject);
		} else if (expensePermissionProvider.getSupportedPermissions().contains(permission)) {
			return expensePermissionProvider.isAuthorized(authentication, permission, targetDomainObject);
		} else if (userPermissionProvider.getSupportedPermissions().contains(permission)) {
			return userPermissionProvider.isAuthorized(authentication, permission, targetDomainObject);
		}
		return false;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		return false;
	}
}
