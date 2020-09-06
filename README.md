**Disclaimer:** This project is very much work in progress, but I believe it's still a good sample of how I write and structure my code.
# spring-boot-rest-api
Backend api serving JSON for a website with functionality similar to a group on Facebook.
## Prerequisites to compile and run
- JDK 14
- MongoDB
## Current Features
- User registration possible only with a one use token generated by someone with admin privileges
- Calendar in which users can set their availability on a given day
## Planned Features for v1 / TODO
- Email verification
- Deleting accounts
- Creating and editing posts and comments
- Creating events integrated with the calendar
- Polls
- Categorization of posts(discussion, info, event, poll)
- Refactor test code to reduce duplication
## Technologies/Libraries used
- Java 14
- Spring Boot 2.3
- Spring MVC
- Spring Security
- Spring Data MongoDB
- JUnit 5 and AssertJ
- Mockito
