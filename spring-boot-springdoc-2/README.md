# Read Me First

Reference: https://www.baeldung.com/spring-rest-openapi-documentation

Documentation is an essential part of building REST APIs. In this tutorial, we’ll look at SpringDoc, which simplifies the generation and maintenance of API docs based on the OpenAPI 3 specification for Spring Boot 3.x applications.

## Setting up springdoc-openapi

Spring Boot 3.x requires to use version 2 of springdoc-openapi:

### OpenAPI Description Path
After setting up the dependency correctly, we can run our application and find the OpenAPI descriptions at /v3/api-docs, which is the default path:

http://localhost:8080/v3/api-docs

## Integration with Swagger UI
Besides generating the OpenAPI 3 specification, we can integrate springdoc-openapi with Swagger UI to interact with our API specification and exercise the endpoints.

The springdoc-openapi dependency already includes Swagger UI, so we’re all set to access the API documentation at:

http://localhost:8080/swagger-ui/index.html

### Support for swagger-ui Properties
The springdoc-openapi library also supports swagger-ui properties. These can be used as Spring Boot properties with the prefix springdoc.swagger-ui.

For example, we can customize the path of our API documentation by changing the springdoc.swagger-ui.path property inside our application.properties file:

`
springdoc.swagger-ui.path=/swagger-ui-custom.html
`

So now our API documentation will be available at http://localhost:8080/swagger-ui-custom.html.

As another example, we can sort the API paths according to their HTTP methods with the springdoc.swagger-ui.operationsSorter property:

`springdoc.swagger-ui.operationsSorter=method`