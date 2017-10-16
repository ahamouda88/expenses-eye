(function() {
	var application = angular.module("application");
	
	// Create a date format filter
	application.filter('dateFormat', function($filter) {
		return function(date) {
			return date === undefined ? '' : $filter('date') (date, 'MM-dd-yyyy');
		}
	});
})();