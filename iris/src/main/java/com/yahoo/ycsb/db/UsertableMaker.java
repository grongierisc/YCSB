package com.yahoo.ycsb.db;

import java.lang.reflect.Field;
import java.util.Map;

import com.yahoo.ycsb.ByteIterator;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;


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
    
    // import needed package
    pool.importPackage("java.lang");
    pool.importPackage("com.intersystems.xep.annotations.Id");
    
    try {
      
      //prepare the type string field
      CtClass string = pool.getCtClass("java.lang.String");
      
      String newClass = "com.yahoo.ycsb.db."+table;
      
      CtClass clazz = null;
      // get or create the class 
      if (pool.getOrNull(newClass) != null) {
      	clazz = pool.get(newClass);
      }
      else {
        clazz = pool.makeClass(newClass);
      }
      
      //create fieldscount fields name fieldX of type string
      for(int i=0; i < fieldscount; i++) {
        try {
        	clazz.getField("field"+i);
        } catch (NotFoundException e) { 
      	  CtField f = new CtField(string, "field"+i, clazz);
          clazz.addField(f);
        }
      }

      //get the ConstPool for the key field
      ClassFile cfile = clazz.getClassFile();
      ConstPool cpool = cfile.getConstPool();

      //create the annotation Id for the key field
      //this is to make key the primary key of the event to persist in Iris
      AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
      Annotation annot = new Annotation("com.intersystems.xep.annotations.Id", cpool);
      annot.addMemberValue("generated", new BooleanMemberValue(cpool));
      attr.addAnnotation(annot);
      
      //add key field to class
      CtField fk = new CtField(string, keyname, clazz); 
      fk.getFieldInfo().addAttribute(attr);
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
