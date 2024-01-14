# Reference Documentation

## How to run the application

Generate your own certificate using this command:
```shell

openssl req -x509 -nodes -days 7300 -newkey rsa:2048 -keyout vsftpd.pem -out vsftpd.pem
```


1. Run the containers
```shell
docker-compose up -d
```
2. Run the application
```shell
mvn clean spring-boot:run
```

3. CURL command to create a CSV file and upload it to the FTP server
```shell
curl -H 'Content-Type: application/json' -s -XPOST http://localhost:8080/api/ftp/file -d '{"key1":"value1", "key2":"value2"}'

```
4. CURL command to list file from the remote server
```shell
curl -XGET http://localhost:8080/api/ftp/list
```
