(function() {
	var application = angular.module('application', [ 'ngRoute',  'ngCookies', 'satellizer' ]);

	// Handle routes
	application.config([ '$routeProvider', '$authProvider', function($routeProvider, $authProvider) {
		
		$routeProvider.when('/', {
			templateUrl : '/all/view/home',
			resolve : {
				factory : checkRouting
			}
		}).when('/login', {
			templateUrl : '/all/view/login'
		}).when('/user', {
			templateUrl : '/all/view/user'
		}).when('/users', {
			templateUrl : '/usrmgr/view/users',
			resolve : {
				factory : checkRouting
			}
		}).when('/expense', {
			templateUrl : '/usr/view/expense',
			resolve : {
				factory : checkRouting
			}
		}).when('/expenses', {
			templateUrl : '/usr/view/expenses',
			resolve : {
				factory : checkRouting
			}
		}).when('/userDetails', {
			templateUrl : '/usr/view/userDetails',
			resolve : {
				factory : checkRouting
			}
		}).when('/expenses/week-expenses', {
			templateUrl : '/usr/view/expenses/week-expenses',
			resolve : {
				factory : checkRouting
			}
		}).otherwise({
			redirectTo : '/'
		});

	    $authProvider.facebook({
	    		clientId: 1976536855960421
	    });

	    $authProvider.google({
	    		clientId: '508386496077-od6ctsv6bmqrnhiuts4vvqmbhn4lv3pt.apps.googleusercontent.com'
	    });
	      
	} ]);
	
	// Check first if currentUser is authenticated, before doing the route
	var checkRouting = function($rootScope, $location) {
		if ($rootScope.authenticated) {
			return true;
		} else {
			$location.path("/login");
			return false;
		}
	};
})();
