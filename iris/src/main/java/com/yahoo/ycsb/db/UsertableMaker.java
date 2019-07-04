package com.yahoo.ycsb.db;

import java.lang.reflect.Field;
import java.util.Map;

import com.yahoo.ycsb.ByteIterator;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;


/**
 * UsertableMaker is an javassist implementation.
 * This is an helper to generated dynamically 
 * an class that can be persist in the XEP event process
 */
public final class UsertableMaker {

  private String keyname;
  private String table;
  private int fieldscount;
  private static UsertableMaker singleton = null;
  private Class<?> c = null;
  private Usertable ut = new Usertable();
  
  private UsertableMaker() {}

  private UsertableMaker(String tablename, String keyname, int fieldscount) {
    this.setKeyname(keyname);
    this.setTable(tablename);
    this.setFieldscount(fieldscount);
  }
  
  /** Point d'accès pour l'instance unique du singleton. */
  public static UsertableMaker getInstance(String tablename, String keyname, int fieldscount)
  {           
    if (singleton == null) {
      singleton = new UsertableMaker(tablename, keyname, fieldscount); 
    }
    return singleton;
  }
  
  public Class<?> get(boolean dynaclass) throws CannotCompileException {
    if (c == null && dynaclass) {
      return this.make();
    }
    if (c != null && dynaclass) {
      return c;
    }
    return ut.getClass();
  }
  
  public Class<?> make() throws CannotCompileException {
    
    // get default pool to build the new class
    ClassPool pool = ClassPool.getDefault();
    
    String dynaClass = "com.yahoo.ycsb.db."+table;
    String staticClass = "com.yahoo.ycsb.db.Usertable";
    
    CtClass clazz = null;

    
    try {

      // get or rename the class 
      if (staticClass.equals(dynaClass)) {
        throw new CannotCompileException("Dynamic class can't have the same name as static class");
      } else {
        clazz = pool.getAndRename(staticClass, dynaClass);
      }
      
      //prepare the type string field
      CtClass string = pool.getCtClass("java.lang.String");

      // Purge fields from Static UserTable
      for (int i=0; i < 10; i++) {
        clazz.removeField(clazz.getField("field"+i));
      }
      clazz.removeField(clazz.getField("key"));
      
      //create fieldscount fields name fieldX of type string
      for(int i=0; i < fieldscount; i++) {
        CtField f = new CtField(string, "field"+i, clazz);
        clazz.addField(f);
      }
      
      //add key field to class
      CtField fk = new CtField(string, keyname, clazz); 
      clazz.addField(fk);
      

      //generate class
      c = clazz.toClass();
     
    } catch (Exception e) {
      e.printStackTrace();
    }
    return c;
  }
  
  //populate the class with the benchdata
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
