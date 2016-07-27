# ECommerce - Maven
```
mvn install
mvn test
```

```
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=formation --restart=always --name mysql_solo mysql

mkdir ~/.m2

docker run -it --rm -v "$PWD":/usr/src/mymaven -v ~/.m2:/root/.m2 -w /usr/src/mymaven maven:3.3.9-jdk-8 mvn clean install

docker run -it --rm -v "$PWD":/usr/src/mymaven -v ~/.m2:/root/.m2 -w /usr/src/mymaven maven:3.3.9-jdk-8 mvn test
```