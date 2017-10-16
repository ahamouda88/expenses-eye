# Expenses Eye
This simple web-site is used for tracking your expenses, by adding/updating/deleting your expense information (date, time, description, amount, comment), and you will be able to filter your expenses as well.

## Implementation:
- This project is implemented using AngularJS and Bootstrap as the front-end technology, while for the back-end and the restful API are implemented using Java 8, Spring Boot, and Spring MVC. 
- Maven is used as the building tool. 
- For unit and integration testing, JUnit, and Mockito frameworks are used. 
- The database being used is MySQL.

## Running the application:
- Must have MYSQL database, and a schema named 'expensesEyeDb'
- Database username, and password for now are root, and no password
- For schema, username, password or database config changes, you can update the application.properties under src/main/resources
- Then build the application using Maven using the following command: mvn clean install
- Then simply you can either run the application by executing SpringBootConfig.java in the com.expensesEye.config package or using the following command: mvn spring-boot:run

## Pages End Points 
|              URI                   |                               Description                               |   Method   |
|------------------------------------|-------------------------------------------------------------------------|------------|
| /                                  | Home/Welcome Page                                                       |     GET    |
| /login                             | Navigates to the Login Page                                             |     GET    |
| /user                              | Navigates to the Sign-up Page, where you can add a user                 |     GET    |
| /userDetails                              | Navigates to the current user's information details                |     GET    |
| /users                             | Navigates to all Users Page, where you can manage all users         	   |     GET    |
| /expense                              | Navigates to the Expense Page, where you can add a Expense                    |     GET    |
| /expenses                             | Navigates to all Expenses Page, where you can manage all expenses             |     GET    |
| /expenses/week-expenses             | Navigates to current week's Expenses Page, where you can see this week's expenses   |     GET    |

## RESTful Web Service
### Register End Points:
|              URI                   |                  Description                     		              |    Method   |
|------------------------------------|------------------------------------------------------------------------|-------------|
| /api/users/register     | Registers a new user, a given user string, and image file (optional) 			  |     POST     |
| /api/users/register     | Updates an existing user, given a user string, and image file (optional)     		  |     POST     |
| /api/users/verify/{email}?token={token}     | Verifies a user given the a user's email, token, and generate a new token flag     |     GET     |
| /api/users/requestregister?from={from}&to={to}    | Sends a registration request to a given email     |     GET     |
| /api/users/currentuser             | To login and return current authenticated user                                     |     GET     |

> Note: For login you should provide a basic authorization header with the email and password, and the below header as well
- 'X-Requested-With' : 'XMLHttpRequest'


## RESTful Web Service
### Users End Points:
|              URI                   |                  Description                     		              |    Method   |
|------------------------------------|------------------------------------------------------------------------|-------------|
| /api/users                         | Returns the list of all users           								  |     GET     |
| /api/users                         | Adds a new user to the database, given a user object                   |     POST    |
| /api/users                         | Updates a user, given a user object                                    |     PUT     |
| /api/users?id={id}                 | Deletes a user, given a user's id       								  |    DELETE   |
| /api/users/{id}                    | Returns a user by user's id            							      |     GET     |
| /api/users/email/{email}     		| Returns a user by user's email                                      |     GET     | 


### Body Request Examples:
#### Add/Register User
```json
{
  	"firstName": "Ahmed",
  	"lastName": "Hamouda",
  	"email": "ahmed@msn.com",
  	"password": "1234",
  	"confirmPassword": "1234",
  	"roles": [ "ADMIN" ]
}
```
#### Update User's Last Name
```json
{
	"id": "1",
	"firstName": "Ahmed",
  	"lastName": "updatedField",
  	"email": "ahmed@msn.com",
  	"password": "1234",
  	"confirmPassword": "1234",
  	"roles": ["ADMIN"]
}
```

### Expenses End Points:
|              URI                   |                  Description                     					  |    Method   |
|------------------------------------|------------------------------------------------------------------------|-------------|
| /api/expenses                         | Returns the list of all expenses                                          |     GET     |
| /api/expenses                         | Adds a new expense to the database given a expense object                    |     POST    |
| /api/expenses                         | Updates a expense given a expense object                                     |     PUT     |
| /api/expenses?id={id}                 | Deletes a expense, given the expense's id                                    |    DELETE   |
| /api/expenses/{id}                    | Returns a expense by id                                                   |     GET     |
| /api/expenses/user/{userId}      | Returns all expense of the given user                                                 |     GET     |

### Body Request Examples:
#### Add Expense
```json
{
    "amount": "10", 
    "time": "1507026940", 
    "description": "Taxes",
    "comment": "No Comment",
    "user" : {
        "id": "1"
    }
}
```
#### Update Expense's Amount
```json
{
	 "version": 0,
    "id": 1,
    "amount": "10", 
    "time": "1507026940", 
    "description": "Taxes",
    "comment": "No Comment",
    "user": {
        "id": 1
    }
}
```

#### Expenses Search End Point
* URI: /api/expenses/search?userId={userId}&minAmount={minAmount}&maxAmount={maxAmount}&startTime={startTime}&endTime={endTime}
* Description: Filters expenses based on to the given minAmount, maxAmount, startTime, endTime, and userId
* Request Params: (minAmount, maxAmount, startTime, endTime, and userId) all params are OPTIONAL
* Method: GET