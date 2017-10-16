package com.expensesEye.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class ErrorResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private int errorCode;
	private List<String> messages;

	public ErrorResponse() {
	}

	public ErrorResponse(int errorCode, String... messages) {
		this(errorCode, Arrays.asList(messages));
	}

	public ErrorResponse(int errorCode, List<String> messages) {
		this.errorCode = errorCode;
		this.messages = messages;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + errorCode;
		result = prime * result + ((messages == null) ? 0 : messages.hashCode());
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
		ErrorResponse other = (ErrorResponse) obj;
		if (errorCode != other.errorCode)
			return false;
		if (messages == null) {
			if (other.messages != null)
				return false;
		} else if (!messages.equals(other.messages))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ErrorResponse [errorCode=" + errorCode + ", message=" + messages + "]";
	}
}
