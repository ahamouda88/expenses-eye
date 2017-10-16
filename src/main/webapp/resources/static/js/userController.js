(function() {
	var application = angular.module("application"),
		currentUserCookie = 'CURRENT_USER';

	// User Controller
	function UserController($scope, $rootScope, $cookies, UserService, PagerService, ExpenseService) {
		$scope.allRoles = ['ADMIN', 'USER_MANAGER', 'REGULAR_USER'];
		
		// Add or Update User
		$scope.addUpdateUser = function() {
			var dataObj = {
				id : $scope.user.id,
				firstName : $scope.user.firstName,
				lastName : $scope.user.lastName,
				email : $scope.user.email,
				password : $scope.user.password,
				confirmPassword : $scope.user.confirmPassword,
				roles : $scope.selectedRoles && $scope.selectedRoles.length > 0 ? $scope.selectedRoles : $scope.user.roles ?  $scope.user.roles : [ 'REGULAR_USER' ],
				version : $scope.user.version,
				verified : $scope.user.verified,
				locked : $scope.user.locked,
				numOfAttempts : $scope.user.numOfAttempts 
			};
			// Verify if it is an insert or update request
			$scope.insert = $scope.user.id === undefined ? true : false;

			// Password and confirm password validation
			if ($scope.user.confirmPassword !== $scope.user.password) {
				$scope.user.response = {
					errorCode : -1,
					messages : [ 'Password and Confirm password should match!' ]
				};
				clearPassword();
			} else {
				dataObj.image = $scope.imageUrl ? null : $scope.user.image;
				var response = UserService.addUpdateUser(dataObj, $scope.imageFile);
				
				response.success(function(data) {
					if($scope.insert) {
						$scope.user = {};
						$scope.imageUrl = '';
						$scope.imageFile = '';
						$scope.selectedRoles = [];
					// If current user is being updated then update current user on the root scope
					} else if($scope.user.id === $rootScope.currentUser.id){
						tmpData = data;
						tmpData.password = null;
						tmpData.image = null;
				    		$cookies.put(currentUserCookie, JSON.stringify(tmpData));
				    		$rootScope.currentUser = data;
				    		$scope.user = $rootScope.currentUser;
				    		$scope.user.confirmPassword = $scope.user.password;
					}
					$scope.user.response = data;
				});
				response.error(function(data) {
					$scope.user.response = data;
					if($scope.insert) {
						clearPassword();
					}
				});
			}
		};

		// Delete User
		$scope.deleteUser = function(user) {
			if (confirm('Are you sure you want to delete this user: ' + user.email + ' ?')) {
				var response = UserService.deleteUser(user).then(function() {
					$scope.users = $scope.getAllUsers();
					$scope.deletedUser = user;
				});
			}
		};

		// Set target User, when row is selected
		$scope.setTargetUser = function(user) {
			user.confirmPassword = user.password;
			UserService.setTargetUser(user);
		};

		// Get target User, to be displayed on the add/update page
		$scope.getTargetUser = function() {
			var emptyUser = {};
			$scope.user = UserService.getTargetUser();
			$scope.imageFile = $scope.user.image;
			$scope.selectedRoles = $scope.user ? $scope.user.roles ? $scope.user.roles : [] : [];
			UserService.setTargetUser(emptyUser);
		};

		// Get All Users
		$scope.getAllUsers = function() {
			var response = UserService.getAllUsers();
			response.success(function(data) {
				$scope.users = data;
				setUpPagination(data);
			});
		};

		$scope.setNext = function() {
			$scope.currentPage = $scope.tablePagination.setNext();
		};

		$scope.setPrev = function() {
			$scope.currentPage = $scope.tablePagination.setPrev();
		}

		$scope.initUserDetails = function() {
			if ($rootScope.currentUser !== undefined) {
				var response = UserService.getById($rootScope.currentUser.id);
				response.success(function(data) {
					// Load image
					$scope.imageFile = data.image;
					$scope.user = data;
					$scope.user.confirmPassword = $scope.user.password;
				});
			}
		};
		
		$scope.sendRequest = function(){
			var fromEmail = $rootScope.currentUser.email,
				toEmail = $scope.request.email,
				response = UserService.sendRegistrationRequest(fromEmail, toEmail);
			
			response.success(function(data) {
				$scope.request.success = true;
				$scope.request.email = '';
			});
			
			response.error(function(data) {
				$scope.request.success = false;
				$scope.request = data;
			});
		};
		
		// Function to switch the role checkboxs on the user page
		$scope.switchRole = function(role){
			var index = $scope.selectedRoles.indexOf(role);
			
			if(index > -1){
				// Remove role if it exists
				$scope.selectedRoles.splice(index, 1);
			}else{
				// Add role if doesn't exist
				$scope.selectedRoles.push(role);
			}
		};
		
		// Function to load chart data
		$scope.loadChartData = function() {
			$scope.chart = {};
			$scope.chart.expenses = ['data1'];
			$scope.chart.times = ['x'];
			var currentUserId = $rootScope.currentUser.id;
			if(currentUserId){
				ExpenseService.getFilteredExpenses(null, null, null, null, currentUserId)
					.success(function(data) {
						for(var i=0 ; i<data.length ; i++){
							var expense = data[i];
							$scope.chart.expenses.push(expense.amount);
							$scope.chart.times.push(new Date(expense.time));
						}
					});
			}
		};
		
		// Setting up pagination
		function setUpPagination(data) {
			$scope.tablePagination = PagerService.tablePagination(data, 6);
			$scope.currentPage = $scope.tablePagination.currentPage;
		}

		function clearPassword() {
			$scope.user.password = '';
			$scope.user.confirmPassword = '';
		}
	}

	application.controller('UserController', UserController);
})();