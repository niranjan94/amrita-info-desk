package com.njlabs.amrita.aid.push;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	// Database version
	private static final int DATABASE_VERSION = 1;
	
	// Database Name
	private static final String DATABASE_NAME = "PushManager";
	// Announcement table name
	private static final String TABLE_ANNOUNCEMENTS = "announcements";
	
	// Announcement Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_TITLE = "title";
	private static final String KEY_ALERT = "alert";
	private static final String KEY_DATETIME = "datetime";
	private static final String KEY_STATUS = "status";
	
	public DatabaseHandler(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	////
	//// Creating Tables
	////
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String CREATE_ANNOUNCEMENTS_TABLE = "CREATE TABLE " + TABLE_ANNOUNCEMENTS + "("+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT," + KEY_ALERT + " TEXT," + KEY_DATETIME + " TEXT," + KEY_STATUS + " TEXT )";
		db.execSQL(CREATE_ANNOUNCEMENTS_TABLE);
	}
	////
	//// Upgrading database
	////
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANNOUNCEMENTS);
		// Create tables again
		onCreate(db);
	}
	////
    //// Adding new announcement
	////
	public void addAnnouncement(Announcement announcement) 
	{
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(KEY_TITLE, announcement.getTitle()); // Title
	    values.put(KEY_ALERT, announcement.getAlert()); // Alert
	    values.put(KEY_DATETIME, announcement.getDatetime()); // Datetime
	    values.put(KEY_STATUS, announcement.getStatus()); // Status
	    
	 
	    // Inserting Row
	    db.insert(TABLE_ANNOUNCEMENTS, null, values);
	    db.close(); // Closing database connection
	}
	////
	//// Getting a single announcement
	///
	public Announcement getAnnouncement(int id) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    Cursor cursor = db.query(TABLE_ANNOUNCEMENTS, new String[] { KEY_ID,
	            KEY_TITLE, KEY_ALERT , KEY_DATETIME, KEY_STATUS}, KEY_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    Announcement announcement = new Announcement(Integer.parseInt(cursor.getString(0)),
	            cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4));
	    // return Announcement
	    return announcement;
	}
    ////
	//// Getting All normal Announcements
	////
	public List<Announcement> getAllAnnouncements() {
	    List<Announcement> announcementList = new ArrayList<Announcement>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_ANNOUNCEMENTS+" WHERE status<>'ace_club' ORDER BY id DESC";
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	Announcement announcement = new Announcement();
	        	announcement.setID(Integer.parseInt(cursor.getString(0)));
	        	announcement.setTitle(cursor.getString(1));
	        	announcement.setAlert(cursor.getString(2));
	        	announcement.setDatetime(cursor.getString(3));
	        	announcement.setStatus(cursor.getString(4));
	            // Adding Announcement to list
	            announcementList.add(announcement);
	        } while (cursor.moveToNext());
	    }
	    // return announcementList
	    return announcementList;
	}
    ////
	//// Getting All ACE Club Announcements
	////
	public List<Announcement> getAllAceAnnouncements() {
	    List<Announcement> announcementList = new ArrayList<Announcement>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_ANNOUNCEMENTS+" WHERE status='ace_club' ORDER BY id DESC";
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	Announcement announcement = new Announcement();
	        	announcement.setID(Integer.parseInt(cursor.getString(0)));
	        	announcement.setTitle(cursor.getString(1));
	        	announcement.setAlert(cursor.getString(2));
	        	announcement.setDatetime(cursor.getString(3));
	        	announcement.setStatus(cursor.getString(4));
	            // Adding Announcement to list
	            announcementList.add(announcement);
	        } while (cursor.moveToNext());
	    }
	    // return announcementList
	    return announcementList;
	}
	////
	//// Get Number of Announcements
	////
	public int getAnnouncementsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ANNOUNCEMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
	////
	//// Update a single Announcement
	////
	public int updateAnnouncement(Announcement announcement) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(KEY_TITLE, announcement.getTitle());
	    values.put(KEY_ALERT, announcement.getAlert());
	    values.put(KEY_DATETIME, announcement.getDatetime());
	    values.put(KEY_STATUS, announcement.getStatus());
	 
	    // updating row
	    return db.update(TABLE_ANNOUNCEMENTS, values, KEY_ID + " = ?",
	            new String[] { String.valueOf(announcement.getID()) });
	}
	////
	//// Delete an announcement
	////
	public void deleteAnnouncement(Announcement announcement) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_ANNOUNCEMENTS, KEY_ID + " = ?",
	            new String[] { String.valueOf(announcement.getID()) });
	    db.close();
	}
	
}
