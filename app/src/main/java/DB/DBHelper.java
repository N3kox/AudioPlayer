package DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private Context myContext;

    private static final String CREATE_MUSIC_LIST = "create table musicList(listId INTEGER PRIMARY KEY AUTOINCREMENT, listName TEXT NOT NULL)";
    private static final String CREATE_MUSIC_LIST_CONTENT = "create table musicDetail(listId INTEGER,musicId INTEGER, musicName TEXT,artistName TEXT,albumName TEXT)";


    public static final String INSERT_MUSIC_LIST = "insert into musicList (listName) values ";
    public static final String INSERT_MUSIC_LIST_DETAIL = "insert into musicDetail values ";
    public static final String SELECT_FROM_LIST = "select * from musicList";
    public static final String SELECT_FROM_DETAIL = "select * from musicDetail";
    public static final String DELETE_FROM_MUSIC_LIST = "delete from musicList where listId = ";
    public static final String DELETE_FROM_MUSIC_DETAIL_ALL = "delete from musicDetail where listId = ";
    public static final String DELETE_FROM_MUSIC_DETAIL_ONE = "delete from musicDetail where musicId = ";


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MUSIC_LIST);
        db.execSQL(CREATE_MUSIC_LIST_CONTENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
