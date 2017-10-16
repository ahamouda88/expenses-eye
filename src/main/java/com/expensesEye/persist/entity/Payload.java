package com.expensesEye.persist.entity;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

/**
 * A model that represents the payload when logging in using a social media
 * provider
 */
public class Payload implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank
	private String clientId;

	@NotBlank
	private String redirectUri;

	@NotBlank
	private String code;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((redirectUri == null) ? 0 : redirectUri.hashCode());
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
		Payload other = (Payload) obj;
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (redirectUri == null) {
			if (other.redirectUri != null)
				return false;
		} else if (!redirectUri.equals(other.redirectUri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Payload [clientId=" + clientId + ", redirectUri=" + redirectUri + ", code=" + code + "]";
	}

}