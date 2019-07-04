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
import com.yahoo.ycsb.StringByteIterator;


import com.intersystems.xep.*;

import java.lang.reflect.Field;
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
public class IrisXEPClient extends DB {

  private static final String PARAM_KEYNAME = "key";

  private static final String PARAM_TABLE = "table";

  private static final String PARAM_FIELDCOUNT = "fieldcount";
  
  private static final String PARAM_DB_DYNACLASS = "db.dynaclass";

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
  private String keyname = "key";
  private String tablename = "usertable";
  private int fieldscount = 10;
  private EventQuery<Usertable> myQuery;
  private EventQuery<Usertable> myQueryScan;
  private EventPersister xepPersister;
  private Event event;
  private UsertableMaker um;
  private Class<?> c;

  
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

  public IrisXEPClient() {}

  public void init() throws DBException {
    try {
      
      //Get properties
      props = getProperties();
      String urls = props.getProperty(CONNECTION_URL, "127.0.0.1");
      String user = props.getProperty(CONNECTION_USER, "_system");
      String passwd = props.getProperty(CONNECTION_PASSWD, "password");
      String namespace = props.getProperty(CONNECTION_NAMESPACE, "USER");
      int port = getIntProperty(props, CONNECTION_PORT, 51773);
      
      boolean dynaclass = getBoolProperty(props, PARAM_DB_DYNACLASS, true);
      tablename = props.getProperty(PARAM_TABLE, "Usertable");
      keyname = props.getProperty(PARAM_KEYNAME, PARAM_KEYNAME);
      fieldscount = getIntProperty(props, PARAM_FIELDCOUNT, 10);



      
      INIT_LOCK.lock();
      
      um = UsertableMaker.getInstance(tablename, keyname, fieldscount);

      c = um.get(dynaclass);

      xepPersister = PersisterFactory.createPersister();
      xepPersister.connect(urls, port, namespace, user, passwd); 
      
      
      String cano = c.getName().toString();
      if (!xepPersister.isSchemaUpToDate(c)) {
        xepPersister.importSchema(cano);
      }
      event = xepPersister.getEvent(cano);
      
      INIT_LOCK.unlock();
     
      
      System.out.println("Init");

    } catch (Exception e) {
      System.out.println("Exception Init : " + e);
    }
  }

  public void cleanup() {
    try {
      xepPersister.close();
      event.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("CleanUp");
  }

  public Status read(String table, String key, Set<String> fields, Map<String, ByteIterator> result) {
    try {

      EventQueryIterator<Usertable> myIter = getIteratorByKey(tablename, key);
      
      if (!myIter.hasNext()) {
        return Status.ERROR;
      }
      
      myQuery.close();
      
      return Status.OK;
    } catch (Exception e) {
      System.err.println(e);
      return Status.ERROR;
    }
  }

  public Status scan(String table, String startkey, int recordcount,
      Set<String> fields, Vector<HashMap<String, ByteIterator>> result) {
    try {
      EventQueryIterator<Usertable> myIter = getIteratorScan(tablename, startkey, recordcount);
      
      for (int i = 0; i < recordcount && myIter.hasNext(); i++) {
        if (result != null && fields != null) {
          HashMap<String, ByteIterator> values = new HashMap<String, ByteIterator>();
          for (String field : fields) {
            Object usertable = myIter.next();
            Field objectfield = usertable.getClass().getDeclaredField(field);
            String value = (String) objectfield.get(usertable);
            values.put(field, new StringByteIterator(value));
          }
          result.add(values);
        }
      }
      
      return Status.OK;
    } catch (Exception e) {
      System.err.println(e);
      return Status.ERROR;
    }
  }

  public Status update(String table, String key, Map<String, ByteIterator> values) {
    try {

      this.insert(tablename, key, values);

      return Status.OK;
    } catch (Exception e) {
      System.err.println(e);
      return Status.ERROR;
    }
  }

  public Status insert(String table, String key, Map<String, ByteIterator> values) {
    try {

      Object eventItems = um.populate(c, key, values);
      event.store(eventItems);

      return Status.OK;
    } catch (Exception e) {
      System.err.println(e);
      return Status.ERROR;
    }
  }

  public Status delete(String table, String key) {
    try {
      
      EventQueryIterator<Usertable> myIter = getIteratorByKey(tablename, key);
      
      if (myIter.hasNext()) {
        myIter.remove();
      }
      
      return Status.OK;
    } catch (Exception e) {
      System.err.println(e);
      return Status.ERROR;
    }
  }
  
  private EventQueryIterator<Usertable> getIteratorByKey(String table, String key){

    try {

      String sql = " SELECT * FROM com_yahoo_ycsb_db."+table+" WHERE "+keyname+"= ?";

      
      myQuery = event.createQuery(sql);


      myQuery.setParameter(1, key); 
      myQuery.execute();
     
      
      return myQuery.getIterator();
    
    } catch (Exception e) {
      System.err.println(e);
      return null;
    }
  }
  
  private EventQueryIterator<Usertable> getIteratorScan(String table, String key, int recordcount){

    try {

      String sql = " SELECT top ? * FROM com_yahoo_ycsb_db."+table+" WHERE "+keyname+"= ?";

      if (myQueryScan == null) {
        myQueryScan = event.createQuery(sql);
      }

      myQueryScan.setParameter(1, recordcount);
      myQueryScan.setParameter(2, key); 
      myQueryScan.execute();
      
      EventQueryIterator<Usertable> tmp = myQueryScan.getIterator();
      
      myQueryScan.close();
      
      return tmp;
    
    } catch (Exception e) {
      System.err.println(e);
      return null;
    }
  }

}


