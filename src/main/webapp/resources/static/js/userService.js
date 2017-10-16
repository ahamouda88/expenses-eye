(function() {
	var application = angular.module("application"),
		usersPath = '/api/users',
		registerPath = usersPath + '/register';

	// User Service
	function UserService($http) {
		 
		// A login function
		this.loginUser = function(credentials) {
			// The header 'X-Requested-With' is added to prevent the browser security popup
			var headers = { 
				authorization : "Basic " + btoa(credentials.email + ":" + credentials.password),
				'X-Requested-With' : 'XMLHttpRequest'
			}
			return $http.get(usersPath + '/currentuser', {headers : headers});
		};
		
		// A logout function
		this.logoutUser = function() {
			return $http.post('/logout');
		};

		// Add or Update User
		this.addUpdateUser = function(data, image) {
			// Sending both user, and image data
			var fd = new FormData(),
				reqMethod = data.id === undefined || data.id === null ? 'POST' : 'PUT';
			
			fd.append('user', JSON.stringify(data));
			if(image !== undefined) fd.append('image', image);
			
			return $http({
				url: registerPath,
				method: reqMethod,
				headers: {'Content-Type': undefined},
				data: fd,
				transformRequest: function (data, headersGetterFunction) {
					return data;
				}
			});
		};
		
		// Get User By Id
		this.getById = function(userId) {
			var path = usersPath + '/' + userId;
			return $http.get(path);
		};
		
		// Send a registration request given from and to email addresses
		this.sendRegistrationRequest = function(fromEmail, toEmail){
			var path = usersPath +"/requestregister?from=" + fromEmail + "&to=" + toEmail;
			return $http.get(path);
		};
		
		// Remove User
		this.deleteUser = function(data) {
			var path = usersPath + '?id=' + data.id;
			return $http.delete(path);
		};

		// Get All Users
		this.getAllUsers = function() {
			return $http.get(usersPath);
		};
		
		// Set target User, when row is selected
		var targetUser = {};
		this.setTargetUser = function(user){
			targetUser = user;
		};
		
		// Get target User, to be displayed on the add/update page
		this.getTargetUser = function(){
			return targetUser;
		};
	}

	application.service("UserService", UserService);
})();