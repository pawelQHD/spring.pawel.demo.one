# Project development documentation

### **Description**

The idea behind this project is to create a very simple to-do task tracker. 
You will be able to complete the tasks and set deadlines for them as well as create priorities and set categories for the tasks.

### **Beginning**

Go to start.spring.io and set up the project as following:
```
Project - Maven
Language - Java
Spring Boot 3.5.7
Artifact - pawel.demo.one
Packaging - Jar
Configuration - Properties
Java - 21
Dependencies - Spring Web, Thymeleaf, Spring Security, Spring Boot DevTools
I also missed adding: Spring Data JPA and MySQL Driver and had to add the manually later.
```
Save the file in a directory (99-pawels-self-testing-projects) and launch InteliJ.

Go into Settings -> Build, Execution, Deployment -> Build project automatically. This allows DevTools to work and automatically re-build projects.

Create index.html file inside: main/resources/templates directory.

Create a controller package inside the main/java/com.example/pawel/demo/one directory. Inside this directory create a @controller class:

```java
@Controller
public class SelfTestController {


@GetMapping("/")
public String selfTestHomepage(){


       return "index.html";
}
}
```
### **Database setup**

The below code uses an .sql file to create a database as well as the table structure:
```sql
CREATE DATABASE IF NOT EXISTS `to_do_tracker`;
USE `to_do_tracker`;


--
-- Table structure for table `users`
--
DROP TABLE IF EXISTS `tasks`;
DROP TABLE IF EXISTS `users`;


CREATE TABLE `users` (
`id` INT NOT NULL AUTO_INCREMENT,
`username` VARCHAR(50) NOT NULL UNIQUE,
`password` VARCHAR(255) NOT NULL,
`email` VARCHAR(100) UNIQUE,
`enabled` TINYINT(1) DEFAULT 1,
`created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


--
-- Table structure for table `tasks`
--
CREATE TABLE `tasks` (
`id` INT NOT NULL AUTO_INCREMENT,
`title` VARCHAR(100) NOT NULL,
`description` TEXT,
`completed` TINYINT(1) DEFAULT 0,
`priority` ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
`category` VARCHAR(50),
`due_date` DATE,
`created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
`updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
`user_id` INT NOT NULL,
PRIMARY KEY (`id`),
FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
INDEX `idx_user_id` (`user_id`),
INDEX `idx_completed` (`completed`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
```

### **Creating Users.java and Task.java**

```java
@Entity
@Table(name = "users")
public class User {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name="id")
private int id;


@Column(name="username", nullable = false, unique = true)
private String username;


@Column(name="password", nullable = false)
private String password;


@Column(name="email")
private String email;


@Column(name="enabled")
private boolean enabled = true;


@Column(name="created_at")
private LocalDateTime createdAt;


@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
private List<Task> tasks;


// Constructors, getters and setters
}
```
The above code is what Users.java looks like. There are few notable mentions in this file.

```@Column(name="username", nullable = false, unique = true)``` this part of the code tells Spring that username cannot be left as null, and it has to be unique. We can’t have users with the same username as that would cause issues with identifying them.

``` @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)``` This means that one user can have many different tasks assigned to it. 

This also creates a list of tasks, which will be very handy when displaying them. ```mappedBy=”user”``` tells JPA who owns this relationship. 

In this case the user owns the list of tasks. cascade = CascadeType. ALL tells Spring what to do with the tasks when the user performs an action. 

The ``` .ALL``` means that the tasks get updated with any action that the user performs.
```java
@Entity
@Table(name="tasks")
public class Task {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;


    @Column(name = "title", nullable = false)
    private String title;


    @Column(name = "description", columnDefinition = "TEXT")
    private String description;


    @Column(name = "completed")
    private boolean completed;


    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority;


    @Column(name = "category")
    private String category;


    @Column(name = "due_date")
    private LocalDate dueDate;


    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


// Constructions, getters and setters
}
```
```@Column(name="description", columnDefinition = "TEXT")``` Is a way to go around the 255-character limit inside the database. This way we know we can go for a much longer String without any issues when we are trying to save it inside the database.

```@ManyToOne``` means that many tasks can be owned by a single user. Every task must be linked to a single user. No two users can have the same task.

```@JoinColumn(name="user_id", nullable = false)``` The Foreign key is the user_id, and we specify it here. This field inside the database will tell us which user owns a task. This also means that we cannot leave this field blank.

### **Authorities table**

Below code is what I used to generate authorities and insert two sample users into the database:
```sql
USE `to_do_tracker`;


DROP TABLE IF EXISTS `authorities`;


--
-- Inserting data for table `users`
--


INSERT INTO `users` (username, password, email)
VALUES
('john','{noop}test123','john@example.com'),
('pawel','{noop}test123','pawel@example.com');




--
-- Table structure for table `authorities`
--


CREATE TABLE `authorities` (
`username` varchar(50) NOT NULL,
`authority` varchar(50) NOT NULL,
UNIQUE KEY `authorities_idx_1` (`username`,`authority`),
CONSTRAINT `authorities_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


--
-- Inserting data for table `authorities`
--


INSERT INTO `authorities`
VALUES
('john','ROLE_USER'),
('pawel','ROLE_USER'),
('pawel','ROLE_ADMIN');
```

### **@Configuration class**
```java
@Configuration
public class SecurityConfig {


@Bean
public UserDetailsManager userDetailsManager(DataSource dataSource){


       return new JdbcUserDetailsManager(dataSource);
}


@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{


       http.authorizeHttpRequests(
               configurer -> configurer
                       .anyRequest()
                       .authenticated()
       ).formLogin(
               form -> form
                       .loginPage("/myLoginPage")
                       .loginProcessingUrl("/authenticateTheUser")
                       .permitAll()
       );


       return http.build();
}
}
```
The first bean is used for connecting with the database and use the users and authorities table.

DataSource is automatically injected by Spring. This is possible because of the database details inside the application.properties file.

```JdbcUserDetailsManager``` looks for users and authorities tables and handles authentication for users. This includes retrieving users and roles from the database.

The second bean is the main security configuration. This tells spring how to secure your application.

```authorizeHttpRequests()``` starts the chain and tells which URLs need authentication

```anyRequests()``` tells Spring that any URL in the app needs authentication

```authenticated()``` means that user must be logged in

The second part of this bean defines how login is handled

```loginPage(“/myLoginPage”)``` tells Spring that we will use a custom login page. This is where the user will be redirected if they are not logged in.

```loginProcessingUrl("/authenticateTheUser")``` is where Spring intercepts the login and checks if the credentials are accurate.

```permitAll()``` means that everyone can access the login page. Without this, users that are not authenticated, will not be able to log in as they wouldn’t have access to log in page.

### **Custom login page**
```java
@GetMapping("/myLoginPage")
public String myLoginPage(){


return "login-page.html";
}
```
The above code had to be added inside the SelfTestController class. This allows us to use our very own custom login page.
```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
   <meta charset="UTF-8">
   <title>Log In Page</title>
</head>
<body>


<h3>Log in to use To-Do Master</h3>


<form action="#" th:action="@{/authenticateTheUser}" method="POST">


   <p>
       Username:<input type="text" name="username"/>
   </p>


   <p>
       Password:<input type="password" name="password">
   </p>


   <input type="submit" value="Login"/>


</form>


</body>
</html>
```
Above code is the login-page.html which completes the custom login form.

### **Logout button**

We need to add the following code inside our HTML to insert the login button:
```html
<form action="#" th:action="@{/logout}" method="POST">
   <input type="submit" value="Logout">
</form>
```
We also need to update our SecurityFilterChain method in our Security Configuration to support login:
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{


http.authorizeHttpRequests(
configurer -> configurer
.anyRequest()
.authenticated()
).formLogin(
form -> form
.loginPage("/myLoginPage")
.loginProcessingUrl("/authenticateTheUser")
.permitAll()
).logout(
logout -> logout
.permitAll()
);


return http.build();
}
```
In the above code we only need to add the .logout section of the code.

### **Adding buttons to other pages**

The following code is added to support two buttons. These buttons have two different permission levels.

The Add task button is available for any user. The Users button is only available for the admins.
```html
<div style="display: flex; gap: 10px;">
   <a th:href="@{/add-task}" >
       <button type="button">Add task</button>
   </a><a sec:authorize="hasRole('ADMIN')" th:href="@{/user_list}" >
       <button type="button">Users</button>
   </a>
</div>
```
```sec:authorize="hasRole('ADMIN')" ``` is what allows this button to hide for users that do not have the AMIN role.

```style="display: flex; gap: 10px;"``` is to fix small visual bug where a line between the two buttons appeared.

To make this work we also need to create a mapping for the html pages just like in the following code:
```html
@GetMapping("/user_list")
public String userList(){


return "user-list.html";
}
```