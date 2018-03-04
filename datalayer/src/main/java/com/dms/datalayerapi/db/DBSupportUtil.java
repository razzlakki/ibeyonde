package com.dms.datalayerapi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dms.datalayerapi.db.core.TableDetails;
import com.dms.datalayerapi.util.ContentHelper;
import com.dms.datalayerapi.util.Helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Raja.p on 13-05-2016.
 */
public abstract class DBSupportUtil {
    private static ArrayList<TableDetails> allTableDefinitions;
    private Context context;
    protected SQLiteDatabase writableDB;
    protected SQLiteDatabase readableDB;

    protected DBSupportUtil(Context context) {
        this.context = context;
        allTableDefinitions = new ArrayList<>();
        checkTablesNAdd(getAllTableDetails(new ArrayList<TableDetails>()));
        try {
            OpenHelper openHelper = new OpenHelper(context);
            this.writableDB = openHelper.getWritableDatabase();
            this.readableDB = openHelper.getReadableDatabase();
            openHelper.onCreate(writableDB);
        } catch (SQLiteException e) {
        }
    }


    private ArrayList<TableDetails> checkTablesNAdd(ArrayList<TableDetails> allTableDetails) {
        for (TableDetails tableDetails :
                allTableDetails) {
            if (!isTableAvailable(tableDetails.getTableName(), allTableDefinitions)) {
                allTableDefinitions.add(tableDetails);
            }
        }
        return null;
    }

    public static void setTableData(SQLiteDatabase db) {
        for (TableDetails tableDetails : allTableDefinitions) {
            alterRCreateTable(db, tableDetails);
        }
    }

    private static void alterRCreateTable(SQLiteDatabase db,
                                          TableDetails tableDetails) {
        // If Table exists
        HashMap<String, String> allFieldsnTypes = tableDetails
                .getTblFieldsNTypes();

        Set<Map.Entry<String, String>> set = allFieldsnTypes.entrySet();
        if (isTableExists(db, tableDetails.getTableName())) {
            for (Map.Entry<String, String> entry : set) {
                try {
                    db.execSQL("ALTER TABLE " + tableDetails.getTableName()
                            + " ADD COLUMN " + entry.getKey() + " "
                            + entry.getValue() + ";");
                } catch (Exception e) {

                }
            }
        } else {
            // If Table Not exists
            StringBuilder createtable = new StringBuilder();
            createtable.append("CREATE TABLE IF NOT EXISTS "
                    + tableDetails.getTableName()
                    + "(_id INTEGER PRIMARY KEY AUTOINCREMENT");

            for (Map.Entry<String, String> entry : set) {
                createtable.append("," + entry.getKey() + " "
                        + entry.getValue());
            }
            createtable.append(")");
            try {
                db.execSQL(createtable.toString());
            } catch (Exception e) {
            }

        }
    }

    private static boolean isTableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery(
                "select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                        + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public static boolean isTableAvailable(String tableName,
                                           ArrayList<TableDetails> allTableDefinitions) {
        for (TableDetails tableDetails : allTableDefinitions) {
            if (tableDetails.getTableName().equalsIgnoreCase(tableName))
                return true;
        }
        return false;
    }

    protected ArrayList<TableDetails> getAllTableDetails(ArrayList<TableDetails> allTableDefinitions) {
        ArrayList<Class> allClass = getAllTables(new ArrayList<Class>());
        for (Class aClass : allClass) {
            allTableDefinitions.add(TableDetails.getTableDetails(aClass));
        }
        return allTableDefinitions;
    }

    protected abstract ArrayList<Class> getAllTables(ArrayList<Class> classes);

    protected abstract String getDatabaseFileName();

    public abstract int getDatabaseVersion();

    public void dropTable(String tableName) {
        writableDB.execSQL("DROP TABLE IF EXISTS " + tableName);
        updateOneTable(writableDB, tableName);
    }

    private void updateOneTable(SQLiteDatabase db, String tableName) {
        allTableDefinitions = getAllTableDetails(allTableDefinitions);
        for (TableDetails tableDetails : allTableDefinitions) {
            if (tableDetails.getTableName().equals(tableName)) {
                alterRCreateTable(db, tableDetails);
                break;
            }
        }
    }

    public void excuteDbSQL(String sqlQuery, String tableName) {
        writableDB.execSQL(sqlQuery);
        setTableData(writableDB);
    }


    public void excuteDbSQL(String sqlQuery) {
        writableDB.execSQL(sqlQuery);
        setTableData(writableDB);
    }


    public <T> void bulkInsertion(List<T> objects) {
        try {
            writableDB.beginTransaction();
            for (T t : objects) {
                insertOrUpdateTable(t, DBAction.INSERT, null);
            }
            writableDB.setTransactionSuccessful();
        } finally {
            writableDB.endTransaction();
        }
    }

    public <T> int updateRecordIfExist(Class<T> classType, T model, String[] keys, String... values) {
        String query = null;
        for (int i = 0; i < keys.length; i++) {
            if (i == 0) {
                query = keys[i] + "='" + values[0] + "'";
            } else {
                query = query + " AND " + keys[i] + "='" + values[0] + "'";
            }
        }
        ArrayList<T> response = getAllValuesFromTable(classType.getSimpleName(), query, classType, null);
        if (response != null && response.size() > 0) {
            return insertOrUpdateTable(model, DBAction.UPDATE, query);
        } else {
            return insertOrUpdateTable(model, DBAction.INSERT, null);
        }
    }

    public int insertOrUpdateTable(Object object,
                                   DBAction insertFlag, @Nullable String where) {
        return insertOrUpdateTable(object.getClass().getSimpleName(), object, insertFlag, where);
    }

    public int insertOrUpdateTable(Object object,
                                   DBAction insertFlag, @Nullable String where, HashMap<String, String> inputs) {
        Class<?> clazz = object.getClass();

        ContentValues cv = new ContentValues();
        for (String col : inputs.keySet()) {
            try {
                Field field = clazz.getField(col);
                if (field != null && field.getName().equals(col)) {
                    field.set(object, inputs.get(col));
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return insertOrUpdateTable(object.getClass().getSimpleName(), object, insertFlag, where);
    }

    /**
     * @param classType
     * @param where
     * @param <T>
     * @return
     */
    public <T> int deleteRow(Class<T> classType, String where) {
        return deleteRow(classType, where, null);
    }


    /**
     * Delete All Records of the table
     *
     * @param classType
     * @param <T>
     * @return
     */
    public <T> void dropTable(Class<T> classType) {
        writableDB.execSQL("drop table if exists " + classType.getSimpleName());
        updateOneTable(writableDB, classType.getSimpleName());
    }

    public <T> int deleteRow(Class<T> classType, String where, String[] whereArgs) {
        return writableDB.delete(classType.getSimpleName(), where, whereArgs);
    }

    /**
     * ]
     *
     * @param TableName
     * @param object
     * @param insertFlag <p/>
     *                   if it is insert send  DBAction.ActionType.INSERT/UPDATE
     * @param where      <p>
     *                   it can be null also
     *                   </p>
     * @return
     */
    public int insertOrUpdateTable(String TableName, Object object,
                                   DBAction insertFlag, @Nullable String where) {

        Class<?> clazz = object.getClass();

        ContentValues cv = new ContentValues();
        for (Field field : clazz.getDeclaredFields()) {

            try {
                field.setAccessible(true);

                if (field.getName().equals("_id"))
                    continue;

                if (field.getType().isPrimitive()) {

                    if (field.getType().getName().equals("long")) {
                        cv.put(field.getName(), (Long) field.get(object));
                    } else if (field.getType().getName().equals("int")) {
                        cv.put(field.getName(), (Integer) field.get(object));
                    } else if (field.getType().getName().equals("boolean")) {
                        cv.put(field.getName(), ((Boolean) field.get(object)) ? 1 : 0);
                    } else if (field.getType().getName().equals("double")) {
                        cv.put(field.getName(), (Double) field.get(object));
                    } else if (field.getType().getName().equals("short")) {
                        cv.put(field.getName(), (Short) field.get(object));
                    } else if (field.getType().getName().equals("byte")) {
                        cv.put(field.getName(), (Byte) field.get(object));
                    } else if (field.getType().getName().equals("float")) {
                        cv.put(field.getName(), (Float) field.get(object));
                    }
                }

                if (field.get(object) == null)
                    continue;

                if (field.getType() == Long.class) {
                    cv.put(field.getName(), (Long) field.get(object));
                } else if (field.getType() == Integer.class) {
                    cv.put(field.getName(), (Integer) field.get(object));
                } else if (field.getType() == Boolean.class) {
                    cv.put(field.getName(), ((Boolean) field.get(object)) ? 1 : 0);
                } else if (field.getType() == String.class) {
                    cv.put(field.getName(), ((String) field.get(object)).trim());
                } else if (field.getType() == Double.class) {
                    cv.put(field.getName(), (Double) field.get(object));
                } else if (field.getType() == Short.class) {
                    cv.put(field.getName(), (Short) field.get(object));
                } else if (field.getType() == Byte.class) {
                    cv.put(field.getName(), (Byte) field.get(object));
                } else if (field.getType().getName().equals("[B")) {
                    cv.put(field.getName(), (byte[]) field.get(object));
                } else if (field.getType() == Float.class) {
                    cv.put(field.getName(), (Float) field.get(object));
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
            }

        }

        switch (insertFlag) {
            case INSERT:
                return (int) writableDB.insert(TableName, null, cv);
            case UPDATE:
                return writableDB.update(TableName, cv, where, null);
            default:
                return 0;// Nothing will happen
        }
    }

    /**
     * @param classType
     * @param contentValues
     * @param where
     * @param whereCause
     * @param <T>
     * @return
     */
    public <T> int updateTable(Class<T> classType, ContentHelper contentValues, String where, String... whereCause) {
        int cursor = 0;
        try {
            cursor = writableDB.update(classType.getSimpleName(), contentValues.get(), where, whereCause);
        } catch (Exception e) {

        }
        return cursor;
    }

    public <T> T getValuesFromTable(String where, Class<T> classType) {
        return getValuesFromTable(where, classType, null);
    }

    public <T> T getValuesFromTable(String where, Class<T> classType, String orderBy) {
        ArrayList<T> responce = getAllValuesFromTable(classType.getSimpleName(), where, classType, orderBy);
        if (responce != null && responce.size() > 0)
            return responce.get(0);
        return null;
    }


    public <T> ArrayList<T> getAllValuesFromTable(String where, Class<T> classType, String orderBy) {
        return getAllValuesFromTable(classType.getSimpleName(), where, classType, orderBy);
    }

    /**
     * Return all values of the table.
     *
     * @param classType
     * @param <T>
     * @return
     */
    public <T> ArrayList<T> getAllValuesFromTable(Class<T> classType, String orderBy) {
        return getAllValuesFromTable(classType.getSimpleName(), null, classType, orderBy);
    }

    public <T> long getCountFromTable(Class<T> classType) {
        return getCountFromTable(classType, null);
    }

    public <T> long getCountFromTable(Class<T> classType, String where) {
        String baseQuery = "select count(*) from " + classType.getSimpleName();
        String query = where != null ? baseQuery + " where " + where : baseQuery;
        Cursor cursor = writableDB.rawQuery(query, null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }


    public <T> long getTotalOfColumns(Class<T> classType, String columName, String where) {
        String baseQuery = "select sum(" + columName + ") as count from " + classType.getSimpleName();
        String query = where != null ? baseQuery + " where " + where : baseQuery;
        Cursor cursor = writableDB.rawQuery(query, null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }

    public <T> String getTotalOfColumnsAsString(Class<T> classType, String columName, String where) {
        String baseQuery = "select sum(" + columName + ") as count from " + classType.getSimpleName();
        String query = where != null ? baseQuery + " where " + where : baseQuery;
        Cursor cursor = writableDB.rawQuery(query, null);
        cursor.moveToFirst();
        String count = cursor.getString(0);
        cursor.close();
        return count;
    }

    public <T> ArrayList<T> getAllValuesFromTable(String TableName,
                                                  String where, Class<T> clazz, String orderBy) {
        return getAllValuesFromTable(TableName, where, clazz, orderBy, null);
    }

    public <T> ArrayList<T> getAllValuesFromTable(String where, Class<T> clazz, String orderBy, String groupBy) {
        return getAllValuesFromTable(clazz.getSimpleName(), where, clazz, orderBy, groupBy);
    }

    public QueryBuilders districtBuilder = null;
    public QueryBuilders columnsByBuilder = null;
    public QueryBuilders selectionArgsByBuilder = null;
    public QueryBuilders isHavingBuilder = null;
    public QueryBuilders limitBuilder = null;
    public QueryBuilders groupByBuilder = null;
    public QueryBuilders customQueryBuilder = null;

    public DBSupportUtil setDistrictBuilder(QueryBuilders districtBuilder) {
        this.districtBuilder = districtBuilder;
        return this;
    }

    public DBSupportUtil setColumnsByBuilder(QueryBuilders columnsByBuilder) {
        this.columnsByBuilder = columnsByBuilder;
        return this;
    }

    public DBSupportUtil setSelectionArgsByBuilder(QueryBuilders selectionArgsByBuilder) {
        this.selectionArgsByBuilder = selectionArgsByBuilder;
        return this;
    }

    public DBSupportUtil setIsHavingBuilder(QueryBuilders isHavingBuilder) {
        this.isHavingBuilder = isHavingBuilder;
        return this;
    }

    public DBSupportUtil setLimitBuilder(QueryBuilders limitBuilder) {
        this.limitBuilder = limitBuilder;
        return this;
    }

    public DBSupportUtil setGroupBy(QueryBuilders groupBy) {
        this.groupByBuilder = groupBy;
        return this;
    }

    public DBSupportUtil setCustomQueryBuilder(QueryBuilders customQueryBuilder) {
        this.customQueryBuilder = customQueryBuilder;
        return this;
    }

    public static class QueryBuilders {
        private String[] columns = null;
        private String[] selectionArgs = null;
        private String havingString = null;
        private String limitString = null;
        private String groupBy = null;
        private String customQuery = null;


        public static QueryBuilders getDistrictBuilder() {
            return new QueryBuilders();
        }

        public static QueryBuilders getColumnsByBuilder(String... columns) {
            QueryBuilders queryBuilders = new QueryBuilders();
            queryBuilders = queryBuilders.setColumns(columns);
            return queryBuilders;
        }

        public static QueryBuilders getSelectionArgsByBuilder(String... columns) {
            QueryBuilders queryBuilders = new QueryBuilders();
            return queryBuilders.setSelectionArgs(columns);
        }

        public static QueryBuilders getHavingBuilder(String havingString) {
            QueryBuilders queryBuilders = new QueryBuilders();
            return queryBuilders.setHavingString(havingString);
        }

        public static QueryBuilders getGroupBuilder(String havingString) {
            QueryBuilders queryBuilders = new QueryBuilders();
            return queryBuilders.setGroupByString(havingString);
        }

        public static QueryBuilders getLimitBuilder(String limit) {
            QueryBuilders queryBuilders = new QueryBuilders();
            return queryBuilders.setLimitString(limit);
        }

        public static QueryBuilders getCustomQueryBuilder(String customQuery) {
            QueryBuilders queryBuilders = new QueryBuilders();
            return queryBuilders.setCustomQueryBuilder(customQuery);
        }

        public QueryBuilders setColumns(String[] columns) {
            this.columns = columns;
            return this;
        }

        public String[] getColumns() {
            return columns;
        }

        public QueryBuilders setSelectionArgs(String[] selectionArgs) {
            this.selectionArgs = selectionArgs;
            return this;
        }

        public String[] getSelectionArgs() {
            return selectionArgs;
        }

        public QueryBuilders setHavingString(String havingString) {
            this.havingString = havingString;
            return this;
        }

        public QueryBuilders setLimitString(String limitString) {
            this.limitString = limitString;
            return this;
        }

        public QueryBuilders setGroupByString(String groupBy) {
            this.groupBy = groupBy;
            return this;
        }

        /**
         * Use Select * for exclude the Cursor exceptions:
         *
         * @param customQuery
         * @return
         */
        public QueryBuilders setCustomQueryBuilder(String customQuery) {
            this.customQuery = customQuery;
            return this;
        }

        public String getCustomQuery() {
            return customQuery;
        }
    }


    public synchronized <T> ArrayList<T> getAllValuesFromTable(String TableName,
                                                               String where, Class<T> clazz, String orderBy, String groupBy) {
        ArrayList<T> allValues = new ArrayList();
        try {

            String orderby = orderBy;


            Cursor cursor = null;
            if (groupBy == null) {
                groupBy = (groupByBuilder == null) ? null : groupByBuilder.groupBy;
            }
            if (customQueryBuilder != null && customQueryBuilder.getCustomQuery() != null)
                cursor = this.readableDB.rawQuery(customQueryBuilder.getCustomQuery(), null);
            else
                cursor = this.readableDB.query(((this.districtBuilder == null) ? false : true), TableName,
                        (columnsByBuilder != null) ? columnsByBuilder.getColumns() : null,
                        where, (selectionArgsByBuilder != null) ? selectionArgsByBuilder.getSelectionArgs() : null,
                        groupBy, (isHavingBuilder != null) ? isHavingBuilder.havingString : null,
                        orderby, (limitBuilder != null) ? limitBuilder.limitString : null);

            resetQueryBuilders();

            if (cursor.moveToFirst()) {
                do {
                    Object obj = null;
                    try {
                        obj = clazz.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    for (Field field : clazz.getDeclaredFields()) {
                        try {
                            field.setAccessible(true);

                            if (!Helper.hasInArray(cursor.getColumnNames(), field.getName()))
                                continue;

                            if (field.getType().isPrimitive()) {
                                if (field.getType().getName().equals("long")) {

                                    field.set(obj, cursor.getLong((cursor
                                            .getColumnIndex(field.getName()))));

                                } else if (field.getType().getName().equals("int")) {
                                    field.set(obj, cursor.getInt(cursor
                                            .getColumnIndex(field.getName())));
                                } else if (field.getType().getName()
                                        .equals("boolean")) {
                                    if (cursor.getInt(cursor.getColumnIndex(field
                                            .getName())) == 1) {
                                        field.set(obj, true);
                                    } else {
                                        field.set(obj, false);
                                    }
                                } else if (field.getType().getName()
                                        .equals("double")) {
                                    field.set(obj, Double.parseDouble(cursor
                                            .getString(cursor.getColumnIndex(field
                                                    .getName()))));
                                } else if (field.getType().getName()
                                        .equals("short")) {
                                    field.set(obj, cursor.getInt(cursor
                                            .getColumnIndex(field.getName())));
                                } else if (field.getType().getName().equals("byte")) {
                                    field.set(obj, cursor.getInt(cursor
                                            .getColumnIndex(field.getName())));
                                } else if (field.getType().getName().equals("float")) {
                                    field.set(obj, cursor.getFloat(cursor
                                            .getColumnIndex(field.getName())));
                                }
                            }
                            if (field.getType() == Long.class) {

                                field.set(obj, cursor.getLong(cursor
                                        .getColumnIndex(field.getName())));

                            } else if (field.getType() == Integer.class) {
                                field.set(obj, cursor.getInt(cursor
                                        .getColumnIndex(field.getName())));
                            } else if (field.getType() == Boolean.class) {
                                if (cursor.getInt(cursor.getColumnIndex(field
                                        .getName())) == 1) {
                                    field.set(obj, true);
                                } else {
                                    field.set(obj, false);
                                }
                            } else if (field.getType() == String.class) {
                                field.set(obj, cursor.getString(cursor
                                        .getColumnIndex(field.getName())));
                            } else if (field.getType() == Double.class) {
                                field.set(obj, Double.parseDouble(cursor
                                        .getString(cursor.getColumnIndex(field
                                                .getName()))));
                            } else if (field.getType() == Short.class) {
                                field.set(obj, cursor.getInt(cursor
                                        .getColumnIndex(field.getName())));
                            } else if (field.getType() == Byte.class) {
                                field.set(obj, cursor.getInt(cursor
                                        .getColumnIndex(field.getName())));
                            } else if (field.getType() == Byte[].class) {
                                field.set(obj, cursor.getBlob(cursor
                                        .getColumnIndex(field.getName())));
                            } else if (field.getType().getName().equals("[B")) {
                                field.set(obj, cursor.getBlob(cursor
                                        .getColumnIndex(field.getName())));
                            } else if (field.getType() == Float.class) {
                                field.set(obj, Float.parseFloat(cursor
                                        .getString(cursor.getColumnIndex(field
                                                .getName()))));
                            }

                        } catch (Exception e) {
                            // e.printStackTrace();
//                            Log.e("", "");
                        }

                    }

                    allValues.add((T) obj);

                } while (cursor.moveToNext());

            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();

            }
        } catch (Exception e) {
        } finally {
            resetQueryBuilders();
        }
        return allValues;
    }

    private void resetQueryBuilders() {
        this.districtBuilder = null;
        this.columnsByBuilder = null;
        this.selectionArgsByBuilder = null;
        this.isHavingBuilder = null;
        this.limitBuilder = null;
        this.groupByBuilder = null;
        this.customQueryBuilder = null;
    }


    public <T> void removeAndInsert(T obj) {
        if (obj == null)
            return;
        if (getCountFromTable(obj.getClass()) > 0) {
            dropTable(obj.getClass());
        }
        insertOrUpdateTable(obj, DBAction.INSERT, null);
    }

    public <T> void removeAndInsertBulk(List<T> objs) {
        if (objs == null && objs.size() == 0)
            return;
        if (getCountFromTable(objs.get(0).getClass()) > 0) {
            dropTable(objs.get(0).getClass());
        }
        bulkInsertion(objs);
    }

    public <T> void insertBulk(List<T> objs) {
        if (objs == null && objs.size() == 0)
            return;
        bulkInsertion(objs);
    }


    private class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, getDatabaseFileName(), null,
                    getDatabaseVersion());
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            setTableData(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            setTableData(db);
        }
    }


    public File copyFileToSdCard() {
        File filePath = null;
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + context.getPackageName() + "//databases//" + getDatabaseFileName();
                String backupDBPath = System.currentTimeMillis() + getDatabaseFileName();
                File currentDB = new File(data, currentDBPath);
                filePath = new File(sd, backupDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(filePath).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                }
            }
        } catch (Exception e) {
            Log.w("Settings Backup", e);
        }
        return filePath;
    }

}