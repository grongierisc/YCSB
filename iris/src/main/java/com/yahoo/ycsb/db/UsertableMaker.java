package com.yahoo.ycsb.db;

import java.lang.reflect.Field;
import java.util.Map;

import com.yahoo.ycsb.ByteIterator;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;


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
public class UsertableMaker {

  private String keyname;
  private String table;
  private int fieldscount;
  
  public UsertableMaker() {}

  public UsertableMaker(String tablename, String keyname, int fieldscount) {
    this.setKeyname(keyname);
    this.setTable(tablename);
    this.setFieldscount(fieldscount);
  }
  
  public Class<?> make() {
    
    ClassPool pool = ClassPool.getDefault();
   
    pool.importPackage("java.lang");
    pool.importPackage("com.yahoo.ycsb.db");
    
    // create the class 
    CtClass clazz = pool.makeClass(table);
    Class<?> c = null;
    try {
      CtClass string = pool.getCtClass("java.lang.String");
      for(int i=0; i < fieldscount; i++) {
        CtField f = new CtField(string, "field"+i, clazz);
        clazz.addField(f);
      }

      CtField fk = new CtField(string, keyname, clazz);

      clazz.addField(fk);
      c = clazz.toClass();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    return c;
  }
  
  public Object populate(Class<?> classs, String key, Map<String, ByteIterator> values) throws Exception {

    Object instance = classs.newInstance();

    Field fieldkey = instance.getClass().getDeclaredField(keyname);
    fieldkey.setAccessible(true);
    fieldkey.set(instance, key);
    
    for (Map.Entry<String, ByteIterator> value : values.entrySet()) {
      try {
        Field field = instance.getClass().getDeclaredField(value.getKey());
        String tValue = value.getValue().toString();
        field.setAccessible(true);
        field.set(instance, tValue);

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return instance;
  }

  public String getKeyname() {
    return keyname;
  }

  public void setKeyname(String tkeyname) {
    this.keyname = tkeyname;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String tablename) {
    this.table = tablename;
  }

  public int getFieldscount() {
    return fieldscount;
  }

  public void setFieldscount(int tfieldscount) {
    this.fieldscount = tfieldscount;
  }
}
