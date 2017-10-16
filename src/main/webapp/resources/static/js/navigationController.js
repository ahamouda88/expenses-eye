(function() {
	var application = angular.module('application'),
		currentUserCookie = 'CURRENT_USER';

	// Navigation Controller
	function NavigationController($scope, $rootScope, $location, $cookies, UserService, $templateCache, $auth) {
		
		var authenticate = function(credentials, callback) {
			if($rootScope.authenticated) return;
			
			$templateCache.removeAll();
			// If new credentials are added!
			if(credentials){
				var response = UserService.loginUser(credentials),
					tmpData;
				response.success(function(data) {	
					tmpData = data;
					tmpData.password = null;
					tmpData.image = null;
			    		$cookies.put(currentUserCookie, JSON.stringify(tmpData));
			    		$rootScope.authenticated = true;
			    		$rootScope.currentUser = data;
			    		$scope.loginErrorMessage = '';
				    	callback && callback();
			    });
				response.error(function(data) {
					$scope.loginErrorMessage = data.error;
				    	$cookies.remove(currentUserCookie);
				    	$rootScope.authenticated = false;
				    	$rootScope.currentUser = undefined;
				    	callback && callback();
			    });
			}else{
				// Else try to get information about current user, in case of page reload!
				var currentUser = $cookies.get(currentUserCookie);
				if(currentUser) {
					$rootScope.currentUser = JSON.parse(currentUser);
					var data = {
						email : $rootScope.currentUser.email,
						password : $rootScope.currentUser.password
					};
					UserService.loginUser(data);
					$rootScope.authenticated = true;
				}
			}
		}
		authenticate();
		
		$scope.credentials = {};
		// A login function
		$scope.loginUser = function() {
			authenticate($scope.credentials, function() {
				if ($rootScope.authenticated) {
					$location.path("/");
					$scope.credentials = {};
					$scope.loginError = false;
		        } else {
		        		$scope.credentials.password = null;
		        		$scope.loginError = true;
		        }
			});
		};
		
		// A logout function
		$scope.logoutUser = function() {
			UserService.logoutUser().success(function() {
            		$rootScope.authenticated = false;
            		$rootScope.currentUser = undefined;
                $location.path("/login");
            }).error(function(data) {
                console.log("Logout failed");
            });
        };
        
        // Authenticate function given the provider such as (facebook, google plus, ..)
        // If successful it will login the user
        $scope.authenticateProvider = function(provider) {
            $auth.authenticate(provider).then(function(result){
            		if(result.data){
	            		$scope.credentials.email = result.data.email;
	            		$scope.credentials.password = result.data.password;
	            		$scope.loginUser();
	            	}
            }).catch(function(result){
            		$scope.credentials.response = result.data;
            });
        };
        
		// Check if current user has the give role
		$scope.hasRole = function(role){
			if($rootScope.currentUser === undefined) return false;
			for(var i in $rootScope.currentUser.roles){
				if($rootScope.currentUser.roles[i] === role) return true;
			}
			return false;
		};
	}
	
	application.controller('NavigationController', NavigationController);
})();