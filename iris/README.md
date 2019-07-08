<!--
Copyright (c) 2015 YCSB contributors. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You
may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License. See accompanying
LICENSE file.
-->

## Quick Start

This section describes how to run YCSB on iris. 

### 1. Install Java and Maven

#### 1.1 Import Iris Jar to Maven
For JBDC
```
mvn install:install-file -Dfile="jdbc/src/main/resources/intersystems-jdbc-3.0.0.jar" -DgroupId="com.intersystems" -DartifactId="intersystems-jdbc" -Dversion="3.0.0" -Dpackaging=jar
```
For XEP
```
mvn install:install-file -Dfile="jdbc/src/main/resources/intersystems-xep-3.0.0.jar" -DgroupId="com.intersystems" -DartifactId="intersystems-xep" -Dversion="3.0.0" -Dpackaging=jar
```
### 2. Set Up YCSB
Git clone YCSB and compile:
  ```
git clone https://github.com/grongierisc/YCSB
cd YCSB
mvn clean package
  ```

### 3. Load data and run tests
####3.1 with JDBC
Load the data:
```
./bin/ycsb load jdbc -P  workloads/workloada -p recordcount=100000 -P  jdbc/src/main/conf/iris_local.properties -cp  jdbc/src/main/resources/intersystems-jdbc-3.0.0.jar -threads 4
```
Run the workload test:
```
./bin/ycsb run jdbc -P  workloads/workloada -p operationcount=100000 -P jdbc/src/main/conf/iris_local.properties -cp  jdbc/src/main/resources/intersystems-jdbc-3.0.0.jar -threads 4
```
####3.2 with XEP
    
1. Load the data :
  ```
./bin/ycsb load iris-xep -P workloads/workloada -p recordcount=100000 -P iris/src/main/conf/iris_local.properties -threads 4
  ```

2. Run the workload :
  ```
./bin/ycsb run iris-xep -P workloads/workloada -p operationcount=100000 -P iris/src/main/conf/iris_local.properties -threads 4
  ```

####3.3 with Java Native API

1. Load the data :
  ```
./bin/ycsb load iris-native -P workloads/workloada -p recordcount=100000 -P iris/src/main/conf/iris_local.properties -threads 4
  ```

2. Run the workload :
  ```
./bin/ycsb run iris-native -P workloads/workloada -p operationcount=100000 -P iris/src/main/conf/iris_local.properties -threads 4
  ```