(function() {
	var application = angular.module("application");

	// Expense Controller
	function ExpenseController($scope, $rootScope, $location, ExpenseService, UserService, PagerService) {
		// Check if we need to request all expenses or only user's expenses
		var currentUserId = ($rootScope.currentUser && $rootScope.currentUser.role !== 'ADMIN') ? $rootScope.currentUser.id : null;
		
		// Add or Update Expense
		$scope.addUpdateExpense = function() {
			var dataObj = {
				version : $scope.expense.version,
				id : $scope.expense.id,
				amount : $scope.expense.amount,
				time : $scope.expense.time.getTime(),
				description : $scope.expense.description,
				comment : $scope.expense.comment,
				user : $scope.expense.user
			};
			
			// Verify if it is an insert or update request
			if ($scope.expense.id === undefined) {
				$scope.insert = true;
				if(!dataObj.user){
					dataObj.user = $rootScope.currentUser;
				}
			} else {
				$scope.insert = false;
			}

			if(dataObj.user === undefined){
				$scope.expense.response = {
					errorCode : -1,
					messages : [ "User can't be null!" ]
				};
			}else{
				var response = ExpenseService.addUpdateExpense(dataObj);
				response.success(function(data) {
					$scope.expense = {};
					$scope.expense.response = data;
					$location.path("/expenses");
				});
				response.error(function(data) {
					$scope.expense.response = data;
					$scope.insert = false;
				});
			}
		};

		// Delete Expense
		$scope.deleteExpense = function(expense) {
			if (confirm('Are you sure you want to delete expense with Id: ' + expense.id + ' ?')) {
				var response = ExpenseService.deleteExpense(expense).then(function() {
					$scope.expenses = $scope.getAllExpenses();
					$scope.deletedExpense = expense;
				});
			}
		};

		// Set target Expense, when row is selected
		$scope.setTargetExpense = function(expense) {
			expense.time = new Date(expense.time);
			ExpenseService.setTargetExpense(expense);
		};

		// Get target Expense, to be displayed on the add/update page
		$scope.getTargetExpense = function() {
			$scope.expense = ExpenseService.getTargetExpense();
			var emptyExpense = {};
			ExpenseService.setTargetExpense(emptyExpense);
		};

		// Get All Expenses
		$scope.getAllExpenses = function() {
			var response;
			if(currentUserId){
				response = ExpenseService.getFilteredExpenses(null, null, null, null, currentUserId);
			}else{
				response = ExpenseService.getAllExpenses();
			}
			response.success(function(data) {
				$scope.expenses = data;
				setUpPagination(data);
			});
		};

		/*
		 * Method to process filter and contains logic for which service method
		 * is being called
		 */
		$scope.filterExpenses = function() {
			var minAmount = $scope.filter.expense.minAmount,
				maxAmount = $scope.filter.expense.maxAmount,
				startTime = $scope.filter.expense.startTime ? $scope.filter.expense.startTime.getTime() : null,
				endTime = $scope.filter.expense.endTime ? $scope.filter.expense.endTime.getTime() : null,
				response = ExpenseService.getFilteredExpenses(minAmount, maxAmount, startTime, endTime, currentUserId);
			
			response.success(function(data) {
				$scope.expenses = data;
				setUpPagination(data);
			});
		};
		
		// This function loads all this week's expenses
		$scope.getThisWeekExpenses = function() {
			var curr = new Date(),
				response;
			
			curr.setHours(0, 0, 0, 0);
			$scope.startDay = curr.setDate(curr.getDate() - curr.getDay() + 1),
			$scope.endDay = curr.setDate(curr.getDate() - curr.getDay() + 7);

			response = ExpenseService.getFilteredExpenses(null, null, $scope.startDay, $scope.endDay, currentUserId);
			response.success(function(data) {
				var totalAmount = 0;
				
				for(var i=0 ; i<data.length ; i++){
					totalAmount += data[i].amount;
				}
				$scope.expenses = data;
				$scope.expenses.averageAmount = (totalAmount / 7).toFixed(2)
				$scope.expenses.totalAmount = totalAmount.toFixed(2)
			});
		};
		
		// This function is used to print an html element based on it's Id
		$scope.print = function(elementId){
			var printContents = document.getElementById(elementId).innerHTML,
				popupWin = window.open('', '_blank', 'width=300,height=300');
			popupWin.document.open();
			popupWin.document.write('<html><head><link rel="stylesheet" type="text/css" href="style.css" /></head><body onload="window.print()">' + printContents + '</body></html>');
			popupWin.document.close();
		};

		$scope.initExpensePage = function() {
			$scope.getTargetExpense();
		};
		
		$scope.setNext = function() {
			$scope.currentPage = $scope.tablePagination.setNext();
		};
		
		$scope.setPrev = function() {
			$scope.currentPage = $scope.tablePagination.setPrev();
		};
		
		// Setting up pagination
		function setUpPagination(data){
			$scope.tablePagination = PagerService.tablePagination(data, 5);
			$scope.currentPage = $scope.tablePagination.currentPage;
		}
	}

	application.controller('ExpenseController', ExpenseController);
})();