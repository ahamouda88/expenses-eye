package com.expensesEye.model.request;

import java.io.Serializable;

import com.expensesEye.persist.entity.Expense;

/**
 * A class that contains all the parameters needed to perform search on an
 * {@link Expense} model/entity
 */
public class ExpenseSearchRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Double minAmount;
	private final Double maxAmount;
	private final Long startTime;
	private final Long endTime;
	private final Long userId;

	private ExpenseSearchRequest(Builder builder) {
		this.minAmount = builder.minAmount;
		this.maxAmount = builder.maxAmount;
		this.startTime = builder.startTime;
		this.endTime = builder.endTime;
		this.userId = builder.userId;
	}

	public Double getMinAmount() {
		return minAmount;
	}

	public Double getMaxAmount() {
		return maxAmount;
	}

	public Long getStartTime() {
		return startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public Long getUserId() {
		return userId;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Double minAmount;
		private Double maxAmount;
		private Long startTime;
		private Long endTime;
		private Long userId;

		public Builder minAmount(Double minAmount) {
			this.minAmount = minAmount;
			return this;
		}

		public Builder maxAmount(Double maxAmount) {
			this.maxAmount = maxAmount;
			return this;
		}

		public Builder startTime(Long startTime) {
			this.startTime = startTime;
			return this;
		}

		public Builder endTime(Long endTime) {
			this.endTime = endTime;
			return this;
		}

		public Builder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public ExpenseSearchRequest build() {
			return new ExpenseSearchRequest(this);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((maxAmount == null) ? 0 : maxAmount.hashCode());
		result = prime * result + ((minAmount == null) ? 0 : minAmount.hashCode());
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		ExpenseSearchRequest other = (ExpenseSearchRequest) obj;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (maxAmount == null) {
			if (other.maxAmount != null)
				return false;
		} else if (!maxAmount.equals(other.maxAmount))
			return false;
		if (minAmount == null) {
			if (other.minAmount != null)
				return false;
		} else if (!minAmount.equals(other.minAmount))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExpenseSearchRequest [minAmount=" + minAmount + ", maxAmount=" + maxAmount + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", userId=" + userId + "]";
	}

}
