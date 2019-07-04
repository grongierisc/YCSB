package com.yahoo.ycsb.db;

import com.intersystems.xep.annotations.Index;
import com.intersystems.xep.annotations.IndexType;

/**
 * A class that wraps a JDBC compliant database to allow it to be interfaced
 * with YCSB. This class extends {@link DB} and implements the database
 * interface used by YCSB client.
 *
 * <br>
 * Each client will have its own instance of this class. This client is not
 * thread safe.
 *
 * <br>
 * This interface expects a schema <key> <field1> <field2> <field3> ... All
 * attributes are of type TEXT. All accesses are through the primary key.
 * Therefore, only one index on the primary key is needed.
 */
@Index(name="indexOne", fields={"key"}, type=IndexType.bitmap)
public class Usertable {
  
  private String key;
  private String field0;
  private String field1;
  private String field2;
  private String field3;
  private String field4;
  private String field5;
  private String field6;
  private String field7;
  private String field8;
  private String field9;

  public Usertable() {}

}
