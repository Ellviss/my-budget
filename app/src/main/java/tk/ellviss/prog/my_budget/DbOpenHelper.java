package tk.ellviss.prog.my_budget;

/**
 * Created by krasnoshtanov on 21.07.2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {

    static final String DB_NAME = "notes.db";
    static final String DB_TABLE = "notes";
    static final String COLUMN_NOTE = "note";
    static final String COLUMN_MONEY = "money";
    static final int DB_VERSION = 1;

    private static final String DB_CREATE = "CREATE TABLE "
            + DB_TABLE + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NOTE + " TEXT NOT NULL, "
            + COLUMN_MONEY + " integer);";


    Context mContext;

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
        // TODO DELETE AFTER DEBUGGING
        ContentValues values = new ContentValues(1);
        for (int i = 0; i < 3; i++) {
            values.put(COLUMN_NOTE, "Note #" + i);
            values.put(COLUMN_MONEY, "Spend =" + i);
            db.insert(DB_TABLE, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
