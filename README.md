# Service Management
Agent Controller - Service Management module

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

The Service Management module is a component of the European Project mF2C.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

#### Step 1: Install Java

Update your system to the latest stable version, add the Oracle Java PPA to Apt, update your Apt package database and install the latest stable version of Oracle Java 8:
```
$ sudo apt-get update
$ sudo add-apt-repository ppa:webupd8team/java
$ sudo apt-get update
$ sudo apt-get install java-common oracle-java8-installer
```
Verify the Java version by running the following command:
```
$ java -version
```
Output:
```
java version "1.8.0_65"
Java(TM) SE Runtime Environment (build 1.8.0_65-b17)
Java HotSpot(TM) 64-Bit Server VM (build 25.65-b01, mixed mode)
```

#### Step 2: Install Apache Maven

Install Apache Maven 3.5.2:
```
$ sudo apt-get install maven
```
Verify the maven version using the following command:
```
$ mvn -version
```

#### Step 3: Clone the repository
Clone the git repository using:
```
$ git clone https://github.com/mF2C/ServiceManagement.git
```

### Run using Spring Boot

Type the following command from the root project directory to start the application:

```
$ mvn spring-boot:run
```

Output:
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::  (v1.5.8.RELEASE)
....... . . .
....... . . . (log output here)
....... . . .
```

If you open a web browser to localhost:8080 you should see the following output:
```
Welcome to the Service Manager!
```

## Deployment using Docker

Package using Maven:
```
$ mvn package
```
Output:
```
[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building service-manager 1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
.......
.......
.......
[INFO] --- spring-boot-maven-plugin:1.5.8.RELEASE:repackage (default) @ service-manager ---
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 3.901 s
[INFO] Finished at: 2017-11-05T10:29:28+01:00
[INFO] Final Memory: 27M/269M
[INFO] ------------------------------------------------------------------------
```
Build a tagged docker image using the following command line:
```
$ mvn install dockerfile:build
```
Run the local tagged image using:
```
$ docker run -p 8080:8080 -t mf2c/service-manager
```
Finally, if the service is running correctly, typing localhost:8080 on your web browser, the output should be:
```
Welcome to the Service Manager!
```
## Interfaces

### Get available methods
Returns the list of available methods to call on the mapping interface:
-	URI: `localhost:8080/api/v1/mapping/`
-	Method: GET
-	Params: none

### Submit a task
Entry point to compute a task on the Service Management block:
-	URI: `localhost:8080/api/v1/mapping/submit`
-	Method: POST
-	Params: none
-	Body: task - JSON object representing a task.

### Task operation
Start, stop, restart or delete a task: 
-	URI: `localhost:8080/api/v1/mapping/<task_id>/<options>` 
-	Method: PUT
- Params
  - `<task_id>`: id of the task
  - `<options>`: type of operation {start, stop, restart, delete}


