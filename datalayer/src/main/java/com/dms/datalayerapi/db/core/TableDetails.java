package com.dms.datalayerapi.db.core;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * @author Raja.p
 */
public class TableDetails {

    private String tableName;
    private HashMap<String, String> tblFieldsNTypes;

    /**
     * @param tableName <p> Send Table as String</p>
     * @param object    <p>Send a object of the Class Type which you need to create Object. Pleas implements with the Cloneable interface.</p>
     * @return
     */
    public static <T> TableDetails getTableDetails(String tableName, Class<T> object) {
        TableDetails details = new TableDetails();
        details.setTableName(tableName);
        details.setTblFieldsNTypes(getTableFields(object));
        return details;
    }


    /**
     * @param classType <p> It take <b>Class Name</b> as the table Name</p>
     *                  <p>Send a object of the Class Type which you need to create Object. Pleas implements with the Cloneable interface.</p>
     * @return
     */
    public static <T> TableDetails getTableDetails(Class<T> classType) {
        TableDetails details = new TableDetails();
        details.setTableName(classType.getSimpleName());
        details.setTblFieldsNTypes(getTableFields(classType));
        return details;
    }

    private TableDetails() {

    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    private static <T> HashMap<String, String> getTableFields(Class<T> clazz) {


        HashMap<String, String> allTblFieldsNTypes = new HashMap();

        for (Field field : clazz.getDeclaredFields()) {


            String key = null, value = null;
            try {

                field.setAccessible(true);
                // value.getClass().isPrimitive() ||

                if (field.getName().equals("_id") || field.isSynthetic())
                    continue;

                key = field.getName();

                if (field.getType().isPrimitive()) {
                    if (field.getType().getName().equals("long")
                            || field.getType().getName().equals("int")
                            || field.getType().getName().equals("boolean")
                            || field.getType().getName().equals("short")
                            || field.getType().getName().equals("byte")) {
                        value = "INTEGER";
                    } else if (field.getType().getName().equals("double") || field.getType().getName().equals("float")) {
                        value = "REAL";
                    }
                } else {
                    if (field.getType() == Long.class
                            || field.getType() == Integer.class
                            || field.getType() == Boolean.class
                            || field.getType() == Short.class
                            || field.getType() == Byte.class) {
                        value = "INTEGER";
                    } else if (field.getType() == String.class) {
                        value = "TEXT";
                    } else if (field.getType() == Float.class || field.getType() == Double.class) {
                        value = "REAL";
                    } else if (field.getType() == Byte[].class
                            || field.getType().getName().equals("[B")) {
                        value = "BLOB";
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            allTblFieldsNTypes.put(key, value);
        }

        return allTblFieldsNTypes;
    }

    public HashMap<String, String> getTblFieldsNTypes() {
        return tblFieldsNTypes;
    }

    public void setTblFieldsNTypes(HashMap<String, String> tblFieldsNTypes) {
        this.tblFieldsNTypes = tblFieldsNTypes;
    }
}
