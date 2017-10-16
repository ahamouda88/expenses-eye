(function() {
	var application = angular.module("application"),
		expensesPath = '/api/expenses';

	// Expense Service
	function ExpenseService($http, $filter) {
		// Add or Update Expense
		this.addUpdateExpense = function(data) {
			/*
			 * Check if expense's id is null then make a post request, otherwise
			 * make a put request
			 */
			if (data.id === undefined) {
				return $http.post(expensesPath, data);
			} else {
				return $http.put(expensesPath, data);
			}
		};
		
		// Remove Expense
		this.deleteExpense = function(data) {
			var path = expensesPath + '?id=' + data.id;
			return $http.delete(path);
		};

		// Get All Expenses
		this.getAllExpenses = function() {
			return $http.get(expensesPath);
		};
		
		// Returns expenses filtered by the given data
		this.getFilteredExpenses = function(minAmount, maxAmount, startTime, endTime, userId){
			var path = expensesPath + '/search?',
				params = [];
			if(minAmount) params.push('minAmount=' + minAmount);
			
			if(maxAmount) params.push('maxAmount=' + maxAmount);
			
			if(startTime) params.push('startTime=' + startTime);
			
			if(endTime) params.push('endTime=' + endTime);
			
			if(userId) params.push('userId=' + userId);
			
			return $http.get(path + params.join('&'));
		};
		
		// Set target Expense, when row is selected
		var targetExpense = {};
		this.setTargetExpense = function(expense){
			targetExpense = expense;
		};
		
		// Get target Expense, to be displayed on the add/update page
		this.getTargetExpense = function(){
			return targetExpense;
		};
	}

	application.service("ExpenseService", ExpenseService);
})();