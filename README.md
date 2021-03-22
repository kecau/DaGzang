# DaGzang
A Synthetic Data Generator for Cross-domain Recommendation Services

### Introduction
This study aims to propose a platform, called DaGzang, to generate synthetic datasets provide for cross-domain recommendation systems. DaGzang platform working according to the complete loop and it has three major roles as follows (i) detect the overlap association (data distribution pattern) between the real-world datasets, (ii) generate synthetic datasets based on these real-world datasets, and (iii) evaluate the quality of the generated synthetic datasets. Additionally, DaGzang is deployed into DakGalBi, our cross-domain recommender system, in order to validate the usefulness of the synthetic datasets generated.

### Requirements
The codebase is implemented in Java (develop kit 8.0) | Apache Tomcat  (64-bit). Package versions used for development are just below.
```
IDE Netbean 12
MySQL 8.0 
Java 8.0
Apache Tomcat 8
Spring Framework
Maven
```

### How to run the system with Apache Tomcat?
1. Start web sever
```
$mvnw tomcat8:run
```
2. Create WAR file
```
$mvnw clean package
```
3. Start MySQL
```
docker container run -d -p 3306:3306  \
-e MYSQL_ROOT_PASSWORD=password \
-e MYSQL_DATABASE=dagzang \
-e MYSQL_USER=root \
-e MYSQL_PASSWORD=ke@cau \
--name dagzang mysql:8.0.21
```
4. Run in browser
* http://localhost:8084/dakgalbi
* Try online version: http://recsys.cau.ac.kr:8084/dakgalbi

## Contact ##
In case of queries, please email: vuongnguyen@cau.ac.kr




