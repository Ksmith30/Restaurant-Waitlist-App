package com.example.android.waitlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.example.android.waitlist.data.TestUtil;
import com.example.android.waitlist.data.WaitlistContract;
import com.example.android.waitlist.data.WaitlistDbHelper;


public class MainActivity extends AppCompatActivity {

    private GuestListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private EditText mNewGuestNameEditDataBase;
    private EditText mNewPartySizedEditText;

    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView waitlistRecyclerView;

        mNewGuestNameEditDataBase = (EditText) findViewById(R.id.person_name_edit_text);
        mNewPartySizedEditText = (EditText) findViewById(R.id.party_count_edit_text);
        waitlistRecyclerView = (RecyclerView) this.findViewById(R.id.all_guests_list_view);

        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        WaitlistDbHelper waitlistDbHelper = new WaitlistDbHelper(this);
        mDb = waitlistDbHelper.getWritableDatabase();

        Cursor cursor = getAllGuests();

        mAdapter = new GuestListAdapter(this, cursor);

        waitlistRecyclerView.setAdapter(mAdapter);

    }

    public void addToWaitlist(View view) {

        if (mNewPartySizedEditText.getText().length() == 0 ||
                mNewGuestNameEditDataBase.getText().length() == 0) {
            return;
        }

        String guestName = mNewGuestNameEditDataBase.getText().toString();
        int partySize = 1;

        try {
            partySize = Integer.parseInt(mNewPartySizedEditText.getText().toString());
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }

        addGuest(guestName, partySize);

        mAdapter.swapCursor(getAllGuests());

        mNewGuestNameEditDataBase.getText().clear();
        mNewPartySizedEditText.getText().clear();

        mNewPartySizedEditText.clearFocus();

    }

    private Cursor getAllGuests() {
        return mDb.query(
                WaitlistContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP
        );

    }

    private long addGuest(String name, int partySize) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name);
        contentValues.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, partySize);

        return mDb.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, contentValues);
    }

}
