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

import com.intersystems.xep.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * This is a client implementation for IrisXEP.
 */
public class IrisXEPClient extends DB {

  private EventPersister xepPersister;
  private Event event;
  private boolean initialized = false;

  public IrisXEPClient() {}

  public void init() throws DBException {
    try {
      if (initialized) {
        System.err.println("Client connection already initialized.");
        return;
      }
      xepPersister = PersisterFactory.createPersister();
      xepPersister.connect("127.0.0.1", 51773, "User", "_SYSTEM", "password"); // connect to localhost

      xepPersister.importSchema("com.yahoo.ycsb.db.Usertable");   // import flat schema

      event = this.xepPersister.getEvent("com.yahoo.ycsb.db.Usertable");
      
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

      EventQueryIterator<Usertable> myIter = returnOneByKey(table, key);
      
      if (!myIter.hasNext()) {
        return Status.NOT_FOUND;
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
      System.out.println("Scan");
      return Status.OK;
    } catch (Exception e) {
      System.err.println(e);
      return Status.ERROR;
    }
  }

  public Status update(String table, String key, Map<String, ByteIterator> values) {
    try {

      EventQueryIterator<Usertable> myIter = returnOneByKey(table, key);
      
      if (myIter.hasNext()) {
        Usertable eventItems = new Usertable(key, values);
        myIter.set(eventItems);
      }

      return Status.OK;
    } catch (Exception e) {
      System.err.println(e);
      return Status.ERROR;
    }
  }

  public Status insert(String table, String key, Map<String, ByteIterator> values) {
    try {

      Usertable eventItems = new Usertable(key, values);
      
      event.store(eventItems);

      return Status.OK;
    } catch (Exception e) {
      System.err.println(e);
      return Status.ERROR;
    }
  }

  public Status delete(String table, String key) {
    try {
      
      EventQueryIterator<Usertable> myIter = returnOneByKey(table, key);
      
      if (myIter.hasNext()) {
        myIter.remove();
      }
      return Status.OK;
    } catch (Exception e) {
      System.err.println(e);
      return Status.ERROR;
    }
  }
  
  private EventQueryIterator<Usertable> returnOneByKey(String table, String key){

    StringBuilder read = new StringBuilder("SELECT * FROM ");
    read.append("com_yahoo_ycsb_db.Usertable ");
    read.append(" WHERE ");
    read.append(" key ");
    read.append(" = ");
    read.append("?");

    try {

      event = this.xepPersister.getEvent("com.yahoo.ycsb.db.Usertable");
      
      EventQuery<Usertable> myQuery = event.createQuery(" SELECT * FROM com_yahoo_ycsb_db.Usertable WHERE key = ? ");
     
      myQuery.setParameter(1, key); 
      myQuery.execute();
      
      return myQuery.getIterator();
    
    } catch (Exception e) {
      System.err.println(e);
      return null;
    }
    
    

  }

}


