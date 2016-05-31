package com.tablewindows.updowncounter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


public class UpDownCounterDatabaseHelper extends SQLiteOpenHelper{

	    private static final String TBL_CTR = "ctr"; 

		
		
		private static final String[] TBL_NAMES = {TBL_CTR};
		
		public static final String TYPE_INTEGERPKAUTO = "INTEGER PRIMARY KEY AUTOINCREMENT";
		public static final String TYPE_INTEGER = "INTEGER";
		public static final String TYPE_DATETIME = "DATETIME";
		public static final String TYPE_TEXT = "TEXT";
		public static final String TYPE_REAL = "REAL";
		public static final String TYPE_LONG = "LONG";
		
	    public static final  String CTRID = "_id";
	    public static final  String CTRNAME = "ctrname";
	    public static final  String CTRCOUNT = "ctrcount";
		public static final  String CTRLUPDATE = "ctrlupdate";
	    

		
	
		public static final String[] TBL_ctr_CREATE_FIELDS_NAMES = {CTRID, CTRNAME, CTRCOUNT, CTRLUPDATE};
		public static final String[] TBL_ctr_CREATE_FIELDS_TYPES = {TYPE_INTEGERPKAUTO, TYPE_TEXT, TYPE_INTEGER, TYPE_LONG};

	
		
		private static String DB_PATH = "/data/data/com.tablewindows.updowncounter/databases/";
	    private static String DB_NAME = "updowncounter";
	    private SQLiteDatabase myDataBase;
	    private final Context myContext;
     
	    public UpDownCounterDatabaseHelper(Context context) {
		    super(context, DB_NAME, null, 1);
		    this.myContext = context;
	    }	

	    public void createDataBase() throws IOException{
	    	boolean dbExist = checkDataBase();
//		    if(dbExist) {
//		    } 
//		    else {
//		    	this.getReadableDatabase();
//		    	try {
//		    		//copyDataBase();
//		    		} 
//		    	catch (IOException e) {
//		    		//throw new Error("Error copying database");
//		    	}
//		    }
	    }
     
	    private boolean checkDataBase(){
	    	SQLiteDatabase checkDB = null;
		    try{
		    		String myPath = DB_PATH + DB_NAME;
		    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		    	}catch(SQLiteException e){
		    }
		    if(checkDB != null){
		    	checkDB.close();
		    }
		    return checkDB != null ? true : false;
	    }
     
	    private void copyDataBase() throws IOException{
	    	InputStream myInput = myContext.getAssets().open(DB_NAME);
	    	String outFileName = DB_PATH + DB_NAME;
	    	OutputStream myOutput = new FileOutputStream(outFileName);
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = myInput.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
		    myOutput.flush();
		    myOutput.close();
		    myInput.close();
	    }
     
    public void openDataBase() throws SQLException{
    	String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }
     
    @Override
    public synchronized void close() {
	    if(myDataBase != null)
	    myDataBase.close();
	    super.close();
    }
     
    @Override
    public void onCreate(SQLiteDatabase db) {
     		for (int j = 0; j < TBL_NAMES.length; ++j)     	{
			db.execSQL(getCreateTableString(TBL_NAMES[j]));
    	}
    }
     
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 		for (int i = 0; i < TBL_NAMES.length; ++i)
    	{
			db.execSQL("DROP TABLE IF EXISTS " +TBL_NAMES[i]);
    	}
        onCreate(db);
    }

	private static String getCreateTableString(String tableName)
    {
		String[] names = getCreateTableColumnsAttributes(tableName, "NAMES");
		String[] types = getCreateTableColumnsAttributes(tableName, "TYPES");

    	StringBuilder builder = new StringBuilder();
    	builder.append("CREATE TABLE " + tableName + " (" );
    	for (int i = 0; i < names.length; ++i)     	{
    		if (i == (names.length-1))
    		builder.append((names[i])).append(" ").append(types[i]);
    		else
    		builder.append((names[i])).append(" ").append(types[i]).append(",");	
    	}
    	builder.append(")");
    	return builder.toString();
    }
    
    private static String[] getCreateTableColumnsAttributes(String tableName, String columnsAttributeType)
    {
    	try 	{
			return (String[]) UpDownCounterDatabaseHelper.class.getDeclaredField("TBL_" + tableName + "_CREATE_FIELDS_" + columnsAttributeType).get(null);
		} 
		catch (Exception e) 		{
			e.printStackTrace();
			return new String[0];
		} 
    }
        
    public void addCounter(String ctrname, int ctrcount, Long dt)
    {
 	   SQLiteDatabase db = this.getWritableDatabase();
 	   int tot =0;
 	   int id;
		ContentValues ctrValues = new ContentValues();
 	  ctrValues.put(CTRNAME,ctrname);
       Cursor lvlCursor = db.rawQuery("select * from ctr where ctrname = \'" + ctrname +"\'", null);
       
       if (lvlCursor.getCount() >0) 
       {
    	   lvlCursor.moveToFirst();
    	   tot = ctrcount + lvlCursor.getInt(2);
           ctrValues.put(CTRCOUNT,tot);
           ctrValues.put(CTRLUPDATE, dt);
     	   db.update(TBL_CTR, ctrValues, CTRNAME + "='" + ctrname+"'", null);
       }
       else
       {	  ;
    	   ctrValues.put(CTRCOUNT,ctrcount);
    	   ctrValues.put(CTRLUPDATE, dt);
    	   db.insert(TBL_CTR, null, ctrValues);
       }	
       db.close();	
    }
    
	public  int getCount()
	{
        int i =0;
		SQLiteDatabase db = this.getReadableDatabase();
        String lvlQry = "select ctrcount from ctr";
        Cursor lvlCursor = db.rawQuery(lvlQry, null);
        if (lvlCursor != null) {
        	   lvlCursor.moveToFirst();
        	   i= lvlCursor.getInt(0);
        	  }
        return i;
	}
	
    public List<UDCCount> getAllCounts() {
        List<UDCCount> cntList = new ArrayList<UDCCount>();
        Date date;
        String selectQuery = "SELECT  * FROM " + TBL_CTR + " ORDER BY " + CTRLUPDATE + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
       
        if (cursor.moveToFirst()) {
            do {
                UDCCount cnt = new UDCCount();
                cnt.setID(0);
                cnt.setName(cursor.getString(1));
                cnt.setCount(cursor.getInt(2));
                cnt.setDate(cursor.getLong(3));
                cntList.add(cnt);
            } while (cursor.moveToNext());
        }
        return cntList;
    }


    
}