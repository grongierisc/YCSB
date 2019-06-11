#! /bin/bash

BLUE='\033[0;36m'
NC='\033[0m'


echo -e "${BLUE}Testing with Maria LOCAL${NC}"

java  -classpath jdbc/src/main/conf:jdbc/target/jdbc-binding-0.17.0-SNAPSHOT.jar:jdbc-binding/lib/mariadb-java-client-2.4.1.jar \
com.yahoo.ycsb.db.JdbcDBCli \
-P  jdbc-binding/conf/maria_local.properties -c "truncate table usertable"

 bin/ycsb load jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc-binding/conf/maria_local.properties \
-cp  jdbc-binding/lib/mariadb-java-client-2.4.1.jar \
-threads 4

 bin/ycsb run jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc-binding/conf/maria_local.properties \
-cp  jdbc-binding/lib/mariadb-java-client-2.4.1.jar \
-threads 4


echo -e "${BLUE}Testing with Maria DOCKER${NC}"

java  -classpath jdbc/src/main/conf:jdbc/target/jdbc-binding-0.17.0-SNAPSHOT.jar:jdbc-binding/lib/mariadb-java-client-2.4.1.jar \
com.yahoo.ycsb.db.JdbcDBCli \
-P  jdbc-binding/conf/maria_docker.properties -c "truncate table usertable"

 bin/ycsb load jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc-binding/conf/maria_docker.properties \
-cp  jdbc-binding/lib/mariadb-java-client-2.4.1.jar \
-threads 4

 bin/ycsb run jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc-binding/conf/maria_docker.properties \
-cp  jdbc-binding/lib/mariadb-java-client-2.4.1.jar \
-threads 4

echo -e "${BLUE}Testing with Intersystems IRIS LOCAL${NC}"

java  -classpath jdbc/src/main/conf:jdbc/target/jdbc-binding-0.17.0-SNAPSHOT.jar:jdbc-binding/lib/intersystems-jdbc-3.0.0.jar \
com.yahoo.ycsb.db.JdbcDBCli \
-P  jdbc-binding/conf/iris_local.properties -c "truncate table usertable"

 bin/ycsb load jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc-binding/conf/iris_local.properties \
-cp  jdbc-binding/lib/intersystems-jdbc-3.0.0.jar \
-threads 4

 bin/ycsb run jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc-binding/conf/iris_local.properties \
-cp  jdbc-binding/lib/intersystems-jdbc-3.0.0.jar \
-threads 4

echo -e "${BLUE}Testing with Intersystems IRIS DOCKER${NC}"
java  -classpath jdbc/src/main/conf:jdbc/target/jdbc-binding-0.17.0-SNAPSHOT.jar:jdbc-binding/lib/intersystems-jdbc-3.0.0.jar \
com.yahoo.ycsb.db.JdbcDBCli \
-P  jdbc-binding/conf/iris_docker.properties -c "truncate table usertable"

 bin/ycsb load jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc-binding/conf/iris_docker.properties \
-cp  jdbc-binding/lib/intersystems-jdbc-3.0.0.jar \
-threads 4

 bin/ycsb run jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc-binding/conf/iris_docker.properties \
-cp  jdbc-binding/lib/intersystems-jdbc-3.0.0.jar \
-threads 4

echo -e "${BLUE}Testing with PostgreSQL LOCAL${NC}"
java  -classpath jdbc/src/main/conf:jdbc/target/jdbc-binding-0.17.0-SNAPSHOT.jar:jdbc-binding/lib/postgresql-42.2.5.jar \
com.yahoo.ycsb.db.JdbcDBCli \
-P  jdbc-binding/conf/postgres_local.properties -c "truncate table usertable"

 bin/ycsb load jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc-binding/conf/postgres_local.properties \
-cp  jdbc-binding/lib/postgresql-42.2.5.jar \
-threads 4

 bin/ycsb run jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc-binding/conf/postgres_local.properties \
-cp  jdbc-binding/lib/postgresql-42.2.5.jar \
-threads 4

echo -e "${BLUE}Testing with PostgreSQL DOCKER${NC}"
java  -classpath jdbc/src/main/conf:jdbc/target/jdbc-binding-0.17.0-SNAPSHOT.jar:jdbc-binding/lib/postgresql-42.2.5.jar \
com.yahoo.ycsb.db.JdbcDBCli \
-P  jdbc-binding/conf/postgres_docker.properties -c "truncate table usertable"

 bin/ycsb load jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc-binding/conf/postgres_docker.properties \
-cp  jdbc-binding/lib/postgresql-42.2.5.jar \
-threads 4

 bin/ycsb run jdbc \
-P  workloads/workloada -p recordcount=100000 \
-P  jdbc-binding/conf/postgres_docker.properties \
-cp  jdbc-binding/lib/postgresql-42.2.5.jar \
-threads 4