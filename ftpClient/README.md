# Reference Documentation

## How to run the application

### Integrated FTP Server using Apache Mina project

Reference - https://mina.apache.org/ftpserver-project/embedding_ftpserver.html

Generate your own certificate using this command:
```shell

openssl req -x509 -nodes -days 7300 -newkey rsa:2048 -keyout vsftpd.pem -out vsftpd.pem
```

#### CURL commands for the embedded FTP server

```shell
# list the remote directory
curl -v ftp://demo:secret1234@localhost:2221/

# upload a new file to the remote directory
curl -v -T README.md ftp://demo:secret1234@localhost:2221/
```

### Local development

1. Run the containers
   1. This command will run two containers:
      1. postgresSQL DB
      2. FTP server 
```shell
docker-compose up -d
```

2. Run the application
   1. This command will start the Spring boot application and the embedded FTP server 
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
