package tk.ellviss.prog.my_budget;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private static final String[] FIELDS = {"_id", DbOpenHelper.COLUMN_NOTE,};
    private static final String[] FROM = {DbOpenHelper.COLUMN_NOTE,};
    private static final int[] TO = {android.R.id.text1,};
    private static final java.lang.String ORDER = "_id DESC";

    EditText mInputField;
    EditText mMoneyField;
    ListView mNotesList;

    DbOpenHelper mHelper = new DbOpenHelper(this);
    SQLiteDatabase mDb;

    SimpleCursorAdapter mAdapter;
    long mNoteId = -1;
    private String mOldNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInputField = (EditText) findViewById(R.id.inputField);
        mMoneyField = (EditText) findViewById(R.id.money);
        mNotesList = (ListView) findViewById(R.id.notesList);
        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2,
                null, FROM, TO, 0);
        mNotesList.setAdapter(mAdapter);
        registerForContextMenu(mNotesList);
        registerForContextMenu(mInputField);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        switch (v.getId()) {
            case R.id.notesList:
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.my_menu, menu);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_edit: {
                AdapterView.AdapterContextMenuInfo info =
                        (AdapterView.AdapterContextMenuInfo) item
                                .getMenuInfo();
                mOldNote = getNoteById(info.id); // save for future use
                mInputField.setText(mOldNote);   // fill in input field
                mNoteId = info.id;               // save for future use
                return true;
            }
            case R.id.item_delete: {
                AdapterView.AdapterContextMenuInfo info =
                        (AdapterView.AdapterContextMenuInfo) item
                                .getMenuInfo();
                deleteNote(info.id);
                showNotes();
                return true;
            }
            default:
                return super.onContextItemSelected(item);
        }
    }

    private String getNoteById(long id) {
        mDb = (mDb == null) ? mHelper.getWritableDatabase() : mDb;
        Cursor c = mDb.query(DbOpenHelper.DB_TABLE, FIELDS, "_id = " + id,
                null, null, null, null);

        String note = null;
        String Money = null;
        if (c != null) {
            c.moveToFirst();
            note = c.getString(c.getColumnIndexOrThrow(
                    DbOpenHelper.COLUMN_NOTE));
            Money = c.getString(c.getColumnIndexOrThrow(DbOpenHelper.COLUMN_MONEY));
            c.close();
        }
        return note;
    }

    private void deleteNote(long id) {
        mDb = (mDb == null) ? mHelper.getWritableDatabase() : mDb;
        mDb.delete(DbOpenHelper.DB_TABLE, "_id = " + id, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDb != null) {
            mDb.close();
            mDb = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNotes();
    }

    private void showNotes() {
        mDb = (mDb == null) ? mHelper.getWritableDatabase() : mDb;
        Cursor c = mDb.query(DbOpenHelper.DB_TABLE, FIELDS,
                null, null, null, null, ORDER);
        mAdapter.swapCursor(c);
    }

    public void onOkButtonClick(View view) {
        String note = mInputField.getText().toString().trim();
        String money = mMoneyField.getText().toString().trim();
        if (note.length() > 0) {
            ContentValues values = new ContentValues(1);
            values.put(DbOpenHelper.COLUMN_NOTE, note);
            values.put(DbOpenHelper.COLUMN_MONEY,money);
            mDb = (mDb == null) ? mHelper.getWritableDatabase() : mDb;

            if (mNoteId >= 0) {
                mDb.update(DbOpenHelper.DB_TABLE, values,
                        "_id = " + mNoteId, null);
            } else {
                mDb.insert(DbOpenHelper.DB_TABLE, null, values);
            }
            showNotes();
        }

        mNoteId = -1;
        mOldNote = null;
        mInputField.setText(null);
        mMoneyField.setText(null);
    }
}
