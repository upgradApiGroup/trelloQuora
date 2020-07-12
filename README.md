# trelloQuora
###### Restful Web APIs
###### PGDSD (Blockchain) Course 5, Module 3 Assignment
###### https://github.com/upgradApiGroup/trelloQuora | Nitash Anklesaria, Sourav Mishra, Anwar S

### Objective
The objective of the assignment was to build a Quora-like website REST API endpoints using Java,
Spring MVC, JPA, PostgreSQL and SwaggerUI.

### For DB Configuration
Please note that the passwords used for database access in this project are different from those
mentioned in the video tutorial. The version of the PostgreSQL driver used in this project is 
42.2.5, and the relevant .jar files are in the project folder itself.

Name of the database used: quora

Username: quora_admin

Password: quora_admin

### Version Control
The standard practice of creating different branches to compartmentalize the various features and
milestones of development have been used. The order of the branches is as follows:

* master: Stub file was pushed to this branch. And all subsequent branches were created from this 
and later merged back into this 'master' branch. **This is the FINAL branch for submission.**

* 01UserController: All touchpoints related to a user (SignUp, SignIn, SignOut) were developed 
in this branch and later merged into the 'master' branch.
 
* 02CommonController: The 'userprofile' touchpoint was developed in this branch and later merged 
into the 'master' branch.

* 03AdminController: The 'userDelete' touchpoint was developed in this branch and later merged into 
the 'master' branch.

* 04QuestionController: All touchpoints related to Questions (createQuestion, getAllQuestions, 
editQuestionContent, deleteQuestion, getAllQuestionsByUser) were developed in this branch and later 
merged into the 'master' branch.

* 05AnswerController: All touchpoints related to Answers (createAnswer, editAnswerContent, 
deleteAnswer, getAllAnswersToQuestion) were developed in this branch and later merged into 
the 'master' branch.

* 06BugFixes: After all preceding branches were developed, tested and merged in to the 'master' 
branch, this new branch was created to test the project as a whole. A few minor bugs were fixed.
Comments were added in all Controller, BusinessService and Dao classes. When done, this branch was
also merged into the 'master branch.

### Formatting Style
The recommended IntelliJ Java Google Style Guide was imported in the IDE, and the formatting was
applied across the entire code. The link to the XML is below for reference. 
https://raw.githubusercontent.com/google/styleguide/gh-pages/intellij-java-google-style.xml

##### **[END OF FILE]**
