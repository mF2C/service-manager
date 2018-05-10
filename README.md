# Service Management
The current development of Service Management module is divided into two different components: the Categorization and QoS providing blocks.

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This development is part of the European Project mF2C.

## Running

Type the following command from the root project directory to start the application:

```
$ mvn spring-boot:run
```

After waiting some time to start, check `http://localhost:46200/api/service-management/` to see if the Service Management is running properly. 


## Interfaces

#### Categorizer
-	URI: `http://localhost:46200/api/service-management/categorizer/`

Submit a new service: 
-	Method: POST.
-   Body: JSON object representing a service.

Retrieve or delete an existing service: 
-	Method: GET, DELETE.
-   Params: `<service_id>` of the service.

#### QoS Provider
-	URI: `http://localhost:46200/api/service-management/qos/`
-	Method: GET
-   Params: `<service_instance_id>` of the service instance.
-   Returns a service instance.
  

