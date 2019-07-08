#! /bin/bash

BLUE='\033[0;36m'
NC='\033[0m'


echo -e "${BLUE}Testing with Maria LOCAL${NC}"

java  -classpath jdbc/src/main/conf:jdbc/target/jdbc-binding-0.17.0-SNAPSHOT.jar:jdbc/src/resources/mariadb-java-client-2.4.1.jar \
com.yahoo.ycsb.db.JdbcDBCli \
-P  jdbc/src/main/conf/maria_local.properties -c "truncate table usertable"

 bin/ycsb load jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc/src/main/conf/maria_local.properties \
-cp  jdbc/src/resources/mariadb-java-client-2.4.1.jar \
-threads 4

 bin/ycsb run jdbc \
-P  workloads/workloada -p operationcount=100000 \
-P  jdbc/src/main/conf/maria_local.properties \
-cp  jdbc/src/resources/mariadb-java-client-2.4.1.jar \
-threads 4


echo -e "${BLUE}Testing with Maria DOCKER${NC}"

java  -classpath jdbc/src/main/conf:jdbc/target/jdbc-binding-0.17.0-SNAPSHOT.jar:jdbc/src/resources/mariadb-java-client-2.4.1.jar \
com.yahoo.ycsb.db.JdbcDBCli \
-P  jdbc/src/main/conf/maria_docker.properties -c "truncate table usertable"

 bin/ycsb load jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc/src/main/conf/maria_docker.properties \
-cp  jdbc/src/resources/mariadb-java-client-2.4.1.jar \
-threads 4

 bin/ycsb run jdbc \
-P  workloads/workloada -p operationcount=100000 \
-P  jdbc/src/main/conf/maria_docker.properties \
-cp  jdbc/src/resources/mariadb-java-client-2.4.1.jar \
-threads 4

echo -e "${BLUE}Testing with Intersystems IRIS LOCAL${NC}"

java  -classpath jdbc/src/main/conf:jdbc/target/jdbc-binding-0.17.0-SNAPSHOT.jar:jdbc/src/main/resources/intersystems-jdbc-3.0.0.jar \
com.yahoo.ycsb.db.JdbcDBCli \
-P  jdbc/src/main/conf/iris_local.properties -c "truncate table usertable"

 bin/ycsb load jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc/src/main/conf/iris_local.properties \
-cp  jdbc/src/main/resources/intersystems-jdbc-3.0.0.jar \
-threads 4

 bin/ycsb run jdbc \
-P  workloads/workloada -p operationcount=100000 \
-P  jdbc/src/main/conf/iris_local.properties \
-cp  jdbc/src/main/resources/intersystems-jdbc-3.0.0.jar \
-threads 4

echo -e "${BLUE}Testing with Intersystems IRIS DOCKER${NC}"
java  -classpath jdbc/src/main/conf:jdbc/target/jdbc-binding-0.17.0-SNAPSHOT.jar:jdbc/src/main/resources/intersystems-jdbc-3.0.0.jar \
com.yahoo.ycsb.db.JdbcDBCli \
-P  jdbc/src/main/conf/iris_docker.properties -c "truncate table usertable"

 bin/ycsb load jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc/src/main/conf/iris_docker.properties \
-cp  jdbc/src/main/resources/intersystems-jdbc-3.0.0.jar \
-threads 4

 bin/ycsb run jdbc \
-P  workloads/workloada -p operationcount=100000 \
-P  jdbc/src/main/conf/iris_docker.properties \
-cp  jdbc/src/main/resources/intersystems-jdbc-3.0.0.jar \
-threads 4

echo -e "${BLUE}Testing with PostgreSQL LOCAL${NC}"
java  -classpath jdbc/src/main/conf:jdbc/target/jdbc-binding-0.17.0-SNAPSHOT.jar:jdbc/src/resources/postgresql-42.2.5.jar \
com.yahoo.ycsb.db.JdbcDBCli \
-P  jdbc/src/main/conf/postgres_local.properties -c "truncate table usertable"

 bin/ycsb load jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc/src/main/conf/postgres_local.properties \
-cp  jdbc/src/resources/postgresql-42.2.5.jar \
-threads 4

 bin/ycsb run jdbc \
-P  workloads/workloada -p operationcount=100000 \
-P  jdbc/src/main/conf/postgres_local.properties \
-cp  jdbc/src/resources/postgresql-42.2.5.jar \
-threads 4

echo -e "${BLUE}Testing with PostgreSQL DOCKER${NC}"
java  -classpath jdbc/src/main/conf:jdbc/target/jdbc-binding-0.17.0-SNAPSHOT.jar:jdbc/src/resources/postgresql-42.2.5.jar \
com.yahoo.ycsb.db.JdbcDBCli \
-P  jdbc/src/main/conf/postgres_docker.properties -c "truncate table usertable"

 bin/ycsb load jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc/src/main/conf/postgres_docker.properties \
-cp  jdbc/src/resources/postgresql-42.2.5.jar \
-threads 4

 bin/ycsb run jdbc \
-P  workloads/workloada -p operationcount=100000 \
-P  jdbc/src/main/conf/postgres_docker.properties \
-cp  jdbc/src/resources/postgresql-42.2.5.jar \
-threads 4