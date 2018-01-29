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

If you open a web browser to `http://localhost:8080/api/v1/service-management/` you should see the following output:
```
Info - Welcome to the mF2C Service Manager!
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
Finally, if the service is running correctly, typing `http://localhost:8080/api/v1/service-management/` on your web browser, the output should be:
```
Info - Welcome to the mF2C Service Manager!
```
## Resources

Representation of the used resources by the Service Manager

#### Service
  ```
{
    "id":"1",
    "name":"GPS",
    "description":"get the GPS location",
    "created" : "15.01.18",
    "updated" : "18.01.18",
    "resourceURI" : "...",
        "category": {
            "timeLimit":"10",
            "location":"cloud",
            "priority":"1",
            "cpu":"1",
            "memory":"200",
            "storage":"20",
            "network":"100"
            }
}
  ```
## Interfaces

### Get endpoints
Returns the list of available endpoints in the Service Manager:
-	URI: `http://localhost:8080/api/v1/service-management/endpoints/`
-	Method: GET
-	Params: none

### Submit a service
Interface to submit a service to the Service Manager:
-	URI: `http://localhost:8080/api/v1/service-management/mapping/submit/`
-	Method: POST
-	Params: none
- Body: service - JSON object representing a service.

### Service operation
Start, stop, restart or delete a service from the Service Manager: 
-	URI: `http://localhost:8080/api/v1/service-management/mapping/<service_id>/<options>` 
-	Method: PUT
- Params
  - `<service_id>`: id of the service
  - `<options>`: type of operation {START, STOP, RESTART, DELETE}

### Check QoS 
Interface to check the QoS: 
-	URI: `http://localhost:8080/api/v1/service-management/qos/check/<service_id>` 
-	Method: PUT
- Params
  - `<service_id>`: id of the service
