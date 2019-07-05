/**
 * Copyright (c) 2012-2016 YCSB contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */

package com.yahoo.ycsb.db;

import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.DBException;
import com.yahoo.ycsb.Status;

import com.intersystems.jdbc.IRISConnection;
import com.intersystems.jdbc.IRIS;
import com.intersystems.jdbc.IRISDataSource;
import com.intersystems.jdbc.IRISIterator;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * This is a client implementation for IrisXEP.
 */
public class IrisNativeApiClient extends DB {

  private static final String PARAM_KEYNAME = "key";

  private static final String PARAM_TABLE = "table";

  private static final String PARAM_FIELDCOUNT = "fieldcount";

  private static final Lock INIT_LOCK = new ReentrantLock();

  /** The URL to connect to the database. */
  public static final String CONNECTION_URL = "db.url";
  
  /** The port to connect to the database. */
  public static final String CONNECTION_PORT = "db.port";

  /** The namespace to connect to the database. */
  public static final String CONNECTION_NAMESPACE = "db.namespace";
  
  /** The user name to use to connect to the database. */
  public static final String CONNECTION_USER = "db.user";

  /** The password to use for establishing the connection. */
  public static final String CONNECTION_PASSWD = "db.passwd";
  
  public static final String DEFAULT_PROP = "";

  private Properties props;
  private IRIS irisNative;
  
  /** Returns parsed int value from the properties if set, otherwise returns defaultvalue. */
  private static int getIntProperty(Properties props, String key, int defaultvalue) throws DBException {
    String valueStr = props.getProperty(key);
    if (valueStr != null) {
      try {
        return Integer.parseInt(valueStr);
      } catch (NumberFormatException nfe) {
        System.err.println("Invalid " + key + " specified: " + valueStr);
        throw new DBException(nfe);
      }
    }
    return defaultvalue;
  }
  
  /** Returns parsed boolean value from the properties if set, otherwise returns defaultVal. */
  private static boolean getBoolProperty(Properties props, String key, boolean defaultVal) {
    String valueStr = props.getProperty(key);
    if (valueStr != null) {
      return Boolean.parseBoolean(valueStr);
    }
    return defaultVal;
  }

  public IrisNativeApiClient() {}

  public void init() throws DBException {
    try {
      
      //Get properties
      props = getProperties();
      String urls = props.getProperty(CONNECTION_URL, "127.0.0.1");
      String user = props.getProperty(CONNECTION_USER, "_system");
      String passwd = props.getProperty(CONNECTION_PASSWD, "password");
      String namespace = props.getProperty(CONNECTION_NAMESPACE, "USER");
      int port = getIntProperty(props, CONNECTION_PORT, 51773);
   
      INIT_LOCK.lock();
      
      // Using IRISDataSource to connect
      IRISDataSource ds = new IRISDataSource();

      // Create connection string
      String dbUrl = "jdbc:IRIS://" + urls + ":" + port + "/" + namespace;
      ds.setURL(dbUrl);
      ds.setUser(user);
      ds.setPassword(passwd);

      // Making connection
      IRISConnection dbconnection = (IRISConnection) ds.getConnection();
      irisNative = IRIS.createIRIS(dbconnection);

      INIT_LOCK.unlock();
     
      
      System.out.println("Init");

    } catch (Exception e) {
      System.out.println("Exception Init : " + e);
    }
  }

  public void cleanup() {
    try {
      irisNative.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("CleanUp");
  }

  public Status read(String table, String key, Set<String> fields, Map<String, ByteIterator> result) {
    try {

      IRISIterator iter = irisNative.getIRISIterator("^"+table, key);
      while (iter.hasNext()) {
        iter.next();
        //System.out.println(" \"" + iter.getSubscriptValue() + "\"=" + iter.getValue()); 
      }

      return Status.OK;
    } catch (Exception e) {
      System.err.println(e);
      return Status.ERROR;
    }
  }

  public Status scan(String table, String startkey, int recordcount,
      Set<String> fields, Vector<HashMap<String, ByteIterator>> result) {
    try {

      return Status.OK;
    } catch (Exception e) {
      System.err.println(e);
      return Status.ERROR;
    }
  }

  public Status update(String table, String key, Map<String, ByteIterator> values) {
    try {
      insert(table, key, values);
      return Status.OK;
    } catch (Exception e) {
      System.err.println(e);
      return Status.ERROR;
    }
  }

  public Status insert(String table, String key, Map<String, ByteIterator> values) {
    try {

      String field = "";
      String tValue = "";

      for (Map.Entry<String, ByteIterator> value : values.entrySet()) {
        try {
          field = value.getKey();
          tValue = value.getValue().toString();
          irisNative.set(tValue, "^"+table, key, field);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      return Status.OK;
    } catch (Exception e) {
      System.err.println(e);
      return Status.ERROR;
    }
  }

  public Status delete(String table, String key) {
    try {
      irisNative.kill("^"+table, key);
      return Status.OK;
    } catch (Exception e) {
      System.err.println(e);
      return Status.ERROR;
    }
  }

}


