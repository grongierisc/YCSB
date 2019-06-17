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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


/**
 * This is a client implementation for IrisXEP.
 */
public class IrisXEPClient extends DB {

  private static final String COM_YAHOO_YCSB_DB_USERTABLE_TABLE = "com_yahoo_ycsb_db.Usertable";
  private static final String COM_YAHOO_YCSB_DB_USERTABLE_CLASS = "com.yahoo.ycsb.db.Usertable";
  private EventQuery<Usertable> myQuery;
  private EventQuery<Usertable> myQueryScan;
  private EventPersister xepPersister;
  private Event event;
  private UsertableMaker um;
  private Class<?> c;

  private boolean initialized = false;

  public IrisXEPClient() {}

  public void init() throws DBException {
    try {
      if (initialized) {
        System.err.println("Client connection already initialized.");
        return;
      }
      
      um = new UsertableMaker("table", "key", 12);
      c = um.make();
      
      xepPersister = PersisterFactory.createPersister();
      xepPersister.connect("127.0.0.1", 51773, "User", "_SYSTEM", "password"); // connect to localhost

      String cano = c.getName().toString();
      //if (!xepPersister.isSchemaUpToDate(c)) {
      xepPersister.importSchema(cano);
      //}
      event = xepPersister.getEvent(cano);
      
      System.out.println("Init");

      initialized=true;

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

      EventQueryIterator<Usertable> myIter = getIteratorByKey(table, key);
      
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
      EventQueryIterator<Usertable> myIter = getIteratorScan(table, startkey, recordcount);
      
      for (int i = 0; i < recordcount && myIter.hasNext(); i++) {
        if (result != null && fields != null) {
          HashMap<String, ByteIterator> values = new HashMap<String, ByteIterator>();
          for (String field : fields) {
            String value = "todo";
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

      this.insert(table, key, values);

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
      
      EventQueryIterator<Usertable> myIter = getIteratorByKey(table, key);
      
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

      String sql = " SELECT * FROM "+table+" WHERE "+key+" >= ? ";

      
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

      String sql = " SELECT top ? * FROM "+table+" WHERE "+key+" >= ? ";

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


