package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataBaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database";
    private static final String LOCATION_TABLE = "location";
    private static final String ID = "id";
    private static final String NEARBY_TABLE = "nearby";
    private static final String LATITUDE = "latitude";
    private static final String ZIP = "zip";
    private static final String LONGITUDE   = "longitude";
    private static final String ADDRESS = "address";
    private static final String TIME = "time";

    public DataBaseManager(Context context) {
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOCATION_TABLE = "CREATE TABLE "+LOCATION_TABLE+" ("+
                ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                LATITUDE+" TEXT,"+
                LONGITUDE+" TEXT,"+
                ADDRESS+" TEXT,"+
                ZIP+" TEXT,"+
                TIME+" TEXT)";

        String CREATE_NEARBY_TABLE = "CREATE TABLE "+NEARBY_TABLE+" ("+
                ID + " INTEGER," +
                LATITUDE + " TEXT," +
                LONGITUDE + " TEXT," +
                ADDRESS+" TEXT,"+
                TIME+" TEXT,"+
                "PRIMARY KEY ("+ID+","+LATITUDE+","+LONGITUDE+"), "+
                "FOREIGN KEY ("+ID+") REFERENCES "+LOCATION_TABLE+"("+ID+") "+
                "ON DELETE CASCADE)";
        // create contacts table
        db.execSQL(CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_NEARBY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addLocation(String latitude, String longitude, String address, String zip, String time){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        // 3. ADD THE VALUES
        values.put(LATITUDE, latitude);
        values.put(LONGITUDE, longitude);
        values.put(ADDRESS, address);
        values.put(ZIP,zip);
        values.put(TIME, time);
        // 4. INSERT VALUES TO TABLE
        db.insert(LOCATION_TABLE,null,values);
        // 5. CLOSE DB
        db.close();
    }

    public void addNearLocation(String key, String latitude, String longitude, String address, String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, Integer.parseInt(key));
        values.put(LATITUDE, latitude);
        values.put(LONGITUDE, longitude);
        values.put(ADDRESS, address);
        values.put(TIME, time);
        // 4. INSERT VALUES TO TABLE
        db.insert(NEARBY_TABLE,null,values);
        // 5. CLOSE DB
        db.close();
    }
    /*
    public void deleteLocation(List<Contact> contacts){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. delete item from DB
        for (int i = 0; i < contacts.size(); i++){
            db.delete(CONTACT_TABLE,CONTACT_NAME+" = ?", new String[] {contacts.get(i).getName()});
            db.delete(FRIEND_TABLE,FRIEND_NAME+" = ?", new String[] {contacts.get(i).getName()});
        }
        db.close();
    }
    */
    public List<String> getLocations(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+LOCATION_TABLE;
        Cursor cursor = db.rawQuery(query,null);
        List<String> locations = new ArrayList<>();
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                Integer key = cursor.getInt(cursor.getColumnIndex(ID));
                String latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
                String longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
                String address = cursor.getString(cursor.getColumnIndex(ADDRESS));
                String zip = cursor.getString(cursor.getColumnIndex(ZIP));
                String time = cursor.getString(cursor.getColumnIndex(TIME));
                locations.add(key+","+address+","+latitude+","+longitude+","+zip+","+time);
                cursor.moveToNext();
            }
        }
        for (String loc: locations){
            System.out.println("getLocations: "+loc);
        }
        return locations;
    }

    public List<String> getLocationsByZip(String zip){
        System.out.println("getLocationsByZip");
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+ID+", "+LATITUDE+", "+LONGITUDE+", "+ADDRESS+" FROM "+LOCATION_TABLE+" WHERE "+ZIP+" = '"+zip+"'";
        Cursor cursor = db.rawQuery(query,null);
        List<String> locations = new ArrayList<>();
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                Integer key = cursor.getInt(cursor.getColumnIndex(ID));
                String latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
                String longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
                String address = cursor.getString(cursor.getColumnIndex(ADDRESS));
                locations.add(key+","+latitude+","+longitude+","+address);
                cursor.moveToNext();
            }
        }
        for (String loc: locations){
            System.out.println("getLocationsByZip: "+loc);
        }
        return locations;
    }

    public List<String> getNearLocations(String key){
        System.out.println("getLocationsByZip");
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+LATITUDE+", "+LONGITUDE+", "+ADDRESS+" FROM "+NEARBY_TABLE+" WHERE "+ID+" = '"+key+"'";
        Cursor cursor = db.rawQuery(query,null);
        List<String> locations = new ArrayList<>();
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                String latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
                String longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
                String address = cursor.getString(cursor.getColumnIndex(ADDRESS));
                locations.add(address+": "+latitude+","+longitude);
                cursor.moveToNext();
            }
        }
        for (String loc: locations){
            System.out.println("getLocationsByZip: "+loc);
        }
        return locations;
    }
}
