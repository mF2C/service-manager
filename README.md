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

-	Base URI: `http://localhost:46200/api/service-management/`

Retrieve all services: 
-	Method: GET.
-   Returns: a list of all services.

Retrieve a service: 
-	Method: GET.
-   Params: `<service_id>` of the service.
-   Returns: the requested service.

Submit a new service: 
-	Method: POST.
-   Body: JSON object representing a [Service](https://github.com/mF2C/cimi/tree/master/_demo)

Update an existing service: 
-	Method: PUT.
-   Body: JSON object representing a [Service](https://github.com/mF2C/cimi/tree/master/_demo).

Delete an existing service: 
-	Method: DELETE.
-   Params: `<service_id>` of the service.

Check QoS provider:
-	Method: GET
-   Params: `<service_instance_id>` from an existing service instance.
-   Returns: the same service instance.