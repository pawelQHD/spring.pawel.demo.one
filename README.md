# Project development documentation

### Document Description

This document serves as a project journal. It is there to help me understand and learn.

This project lets me practice everything I have learned about Spring Boot so far.

For this reason, it's not focused on functionality as much as a starting point for future project.

In the future I can go back to this project and review all the things I've learned but might have forgotten

That's why this project is more of a documentation of my work rather than a project that's usable.

### Project Description

The idea behind this project is to create a very simple to-do task tracker. 

You will be able to create, update, delete and complete the tasks and set deadlines for them.

Additional features include creating priorities and set categories for the tasks.

This project takes function over design approach, for this reason I will not make anything that looks good, that will be reserved for future projects.

### Beginning

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
Save the file in a directory (99-pawels-self-testing-projects) and launch IntelliJ.

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
### Database setup

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

### Creating Users.java and Task.java

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

### Authorities table

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

### @Configuration class
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

### Custom login page
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

### Logout button

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

### Adding buttons to other pages

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
### User Registration page

There is a lot of groundwork needed in order to register new users inside the database.

This section will focus on the first part that will just display the registration form, without pushing it to the database.

```java
@NotNull(message = "is required")
@Size(min=1, message = "is required")
@Column(name="password", nullable = false)
private String password;
```

The above code was added to both userName and password fields.

This will be used later for validating and I should probably include the very same validation when signing users in.

```java
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUserName(String userName);
}
```
The above code uses JpaRepository. It's used for common database queries.

All you need to do is plug in the Entity type as well as the id (most commonly Integer).

I believe that the id is read from the Entity type using the @Id tag, but have not confirmed it.

```java
public interface UserService {

    public User findByUserName(String userName);
}
```

The above code is an interface that I have created. This is part of the guide that I followed.

The guide is a simple PDF without much information so there is not as much detail in it.

Later on the findByUserName method will be needed and this is why we had to implement it.

```java
@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }
}
```

I really like how the above code is implemented. We have the interface and the implementation class.

This way we can always change our implementation class without many changes to the code. This makes it more maintainable.

We also take advantage of our UserRepository from earlier.

```java
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private UserService userService;

    @Autowired
    public CustomAuthenticationSuccessHandler(UserService theUserService){
        userService = theUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        System.out.println("In customAuthenticationSuccessHandler");
        String userName = authentication.getName();
        System.out.println("userName=" + userName);
        User theUser = userService.findByUserName(userName);

        HttpSession session = request.getSession();
        session.setAttribute("user", theUser);

        response.sendRedirect(request.getContextPath() + "/");
    }
}
```

Above code is part of SpringSecurity. To my knowledge, this code runs when user successfully authenticates.

I will verify it once I manage to get the user logged it, as we are moving to bcrypt encryption soon.

```java

@Configuration
public class SecurityConfig {

    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    public SecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }


    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {

        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
                configurer -> configurer
                        .requestMatchers("/register/**").permitAll()
                        .anyRequest()
                        .authenticated()
        ).formLogin(
                form -> form
                        .loginPage("/myLoginPage")
                        .loginProcessingUrl("/authenticateTheUser")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
        ).logout(
                logout -> logout
                        .permitAll()
        );

        return http.build();
    }

    /*
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService){
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }
    */

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
```

The above code caused me the most issues. There are few reasons for it.

It contains commented out code which is not included inside this project. I left it here for reference.

The reason I had to remove this code was because one of the methods is now deprecated.

I then found out that JdbcUserDetailsManager is a better way to handle it, and it's something I already had.

Another reason I had a lot of issues with it was because of ```.requestMatchers("/register/**").permitAll()```

I missed it from the guide I was using and I could not click on the new user registration link.

Turns out it's important, and it was not highlighted in the guide I used. Another reason why this journal is useful.

```java
@Controller
@RequestMapping("/register")
public class RegistrationController {

    private UserService userService;

    @Autowired
    public RegistrationController(UserService userService){
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder){

        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showRegistrationForm")
    public String showMyLoginPage(Model theModel){

        theModel.addAttribute("user", new User());

        return "register/userRegistrationForm";
    }
}
```

The last piece of code is not finished, but I decided it was a good place for a commit.

This code handles all the relevant mappings that are used for the registration form and creating new users.

All that's missing now is adding users to the database and testing it all out.

```html
    <a th:href="@{/register/showRegistrationForm}">
        Register new user
    </a>
```
The above HTML code was added to the login-page. It's a simple link a user can click if they are not registered yet.

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>New User Registration</title>
</head>
<body>

<h3>New User Registration</h3>
<hr>
<form action="#" th:action="@{/register/processRegistrationForm}"
    th:object="${user}"
    method="Post">

    <div th:if="${param.registrationError}">
        <span th:text="${param.registrationError}"></span>
    </div>

    <p>
        Username* <input type="text" th:field="*{userName}">
    </p>

    <p>
        Password* <input type="text" th:field="*{password}">
    </p>

    <p>
        <button type="submit">Register</button>
    </p>

    <a th:href="@{/}">
        <button type="button">Back</button>
    </a>
    
</form>
</body>
</html>
```
The last piece of code for this section was the actual registration form.

It's still not finished as we need to add email field, this will be done in the next section.

### User Registration page - part 2

This next part focuses on finishing the last part and actually adding the user to the database.

For this I have realised my database setup wasn't set up correctly, and I had to change it.

The first mistake was that id needed to be included inside the authority table.

This was needed because I had to create Authority class which is the code below:

```java
package com.example.pawel.demo.one.entity;

import jakarta.persistence.*;

@Entity
@Table(name="authorities")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="username", nullable = false)
    private String userName;

    @Column(name="authority", nullable = false)
    private String authority;
    
    // constructors, getters and setters
}
```

I had to delete all the database tables and start from scratch. I'm very glad I kept my .sql scripts with the project.

The other mistake I made was with the length of password. I think initially it was set up as 100, we don't need it this long.

ByCrypt uses 60 characters for its password and the prefix is 8 characters that covers {noop} and {bcrypt}.

Turns out I didn't need to have the prefix. I have to figure out why, but from my research I can see that the guide I followed expects BCrypt as default.

So there is no need to specify it. I think in the course I followed they used default implementations and the guide I followed for this section is a bit more custom.

```java
    @PostMapping("/processRegistrationForm")
    public String processRegistrationForm(
            @Valid @ModelAttribute("user") User theUser,
            BindingResult theBindingResult,
            HttpSession session,
            Model theModel
    ){

        String userName = theUser.getUserName();

        if(theBindingResult.hasErrors()){
            return "register/registration-form";
        }

        User existing = userService.findByUserName(userName);
        if(existing != null){
            theModel.addAttribute("user", new User());
            theModel.addAttribute("registrationError",
                    "User name already exists.");
            return "register/registration-form";
        }

        userService.save(theUser);
        session.setAttribute("user", theUser);

        return "register/registration-confirmation";
    }
```

The above code was added to the RegistrationController. This code checks if there are any errors, then checks if the user is already in the database.

Then it actually saves the user using our brand-new method inside the UserServiceImpl.

```java
public interface UserService {

    public User findByUserName(String userName);

    void save(User user);
}
```

The above code is the updated UserService interface. The below code is the actual implementation of the save method:

```java
    @Override
    public void save(User theUser) {

        User user = new User();
        Authority authority = new Authority();

        user.setUserName(theUser.getUserName());
        user.setPassword(passwordEncoder.encode(theUser.getPassword()));
        user.setEmail(theUser.getEmail());
        user.setCreatedAt(LocalDateTime.now());

        authority.setUserName(theUser.getUserName());
        authority.setAuthority("ROLE_USER");

        userRepository.save(user);
        authorityRepository.save(authority);
    }
```

This save method is what caused me to change the database structure I mentioned earlier.

We have to assign new users with a role in order for them to log in. Adding Id made it easier as I was able to create Authority Repository:

```java
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    List<Authority> findByUserName(String username);
}
```

There was a small change inside SecurityConfig. I had to change the way one of the beans was injected to fix circular dependency.

```java
@Configuration
public class SecurityConfig {

//    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
//
//    @Autowired
//    public SecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler){
//        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
//    }


    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource){

        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) throws Exception{

        http.authorizeHttpRequests(
                configurer -> configurer
                        .requestMatchers("/register/**").permitAll()
                        .anyRequest()
                        .authenticated()
        ).formLogin(
                form -> form
                        .loginPage("/myLoginPage")
                        .loginProcessingUrl("/authenticateTheUser")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
        ).logout(
                logout -> logout
                        .permitAll()
        );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();
    }
}
```

The easiest fix was to simply add is as a parameter to the method instead. Not sure if it's the best way to handle it, but it makes the software work.

I also don't know any better for now and I hope I can learn how to handle circular dependencies in the future.

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>New User Registration</title>
</head>
<body>

<h3>New User Registration</h3>
<hr>
<form action="#" th:action="@{/register/processRegistrationForm}"
    th:object="${user}"
    method="Post">

    <div th:if="${param.registrationError}">
        <span th:text="${param.registrationError}"></span>
    </div>

    <p>
        Username* <input type="text" th:field="*{userName}">
    </p>
    <div th:if="${#fields.hasErrors('userName')}">
        <p th:each="err : ${#fields.errors('userName')}" th:text="'User name ' + ${err}"></p>
    </div>

    <p>
        Password* <input type="text" th:field="*{password}">
    </p>
    <div th:if="${#fields.hasErrors('password')}">
        <p th:each="err : ${#fields.errors('password')}" th:text="'Password ' + ${err}"></p>
    </div>

    <p>
        Email* <input type="text" th:field="*{email}">
    </p>
    <div th:if="${#fields.hasErrors('email')}">
        <p th:each="err : ${#fields.errors('email')}" th:text="'Email ' + ${err}"></p>
    </div>

    <p>
        <button type="submit">Register</button>
    </p>

    <a th:href="@{/}">
        <button type="button">Back</button>
    </a>

</form>
</body>
</html>
```

registration-form had to be changed a little bit to handle when the errors arise. There is still one bug remaining with it.

When I create a user that already exists, it simply returns the registration page without giving any reason.

I'm focusing more on the function over form, and it's a bug that is low on my priority list for the moment.

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>User Registered Successfully!</title>
</head>
<body>

<h2>User registered successfully!</h2>

<ul>
    <li th:text="'User name: ' + ${user.userName}"></li>
    <li th:text="'Email address: ' + ${user.email}"></li>
</ul>

<hr>

<a th:href="@{/login-page}">Back to login page</a>

</body>
</html>
```

We also have very simple confirmation page when the user gets created, shown above.

The last piece of information that I was missing to get it all to work is that the password needed to be all in bcrypt format without any prefix.

I initially thought that I can use both {noop} and {bcrypt} at the same time but that is not the case.

Also, the code I'm using does not support the prefix for the password, so I had to modify my database in order to get it working.

Right now all it has is the 60 character bcrypt password and this works flawlessly.