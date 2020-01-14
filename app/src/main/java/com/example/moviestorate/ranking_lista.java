package com.example.moviestorate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.moviestorate.db.TaskContract;
import com.example.moviestorate.db.TaskDbHelper;

import java.util.ArrayList;

public class ranking_lista extends AppCompatActivity {

    private ListView mTaskListView;
    private TaskDbHelper mHelper;
    private ArrayAdapter<SomeList> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_lista);


        mHelper = new TaskDbHelper(this);
        mTaskListView = (ListView) findViewById(R.id.ranking);

        updateUI();

        }

    private void updateUI() {
        final ArrayList<SomeList> filteredList = new ArrayList<>();

        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format("SELECT " + TaskContract.TaskEntry.COL_MOVIE_TITLE +","+ TaskContract.TaskEntry.COL_MOVIE_RATE  +
                           " FROM " + TaskContract.TaskEntry.TABLE + " WHERE " + TaskContract.TaskEntry.COL_MOVIE_WATCHED + " = 1"
                        + " ORDER BY " + TaskContract.TaskEntry.COL_MOVIE_RATE  + " DESC"),
                null);



        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_MOVIE_TITLE);
            int idx2 = cursor.getColumnIndex(TaskContract.TaskEntry.COL_MOVIE_RATE);
            float ocenaSQL = cursor.getFloat(idx2);
                        System.out.println(ocenaSQL);
            filteredList.add(new SomeList(cursor.getString(idx),ocenaSQL, ""));
        }

        if (mAdapter == null) {

            SomeCustomAdapter adapter = new SomeCustomAdapter(this, filteredList);

            mTaskListView.setAdapter(adapter);

        } else {
            mAdapter.clear();
            mAdapter.addAll(filteredList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();


    }
    public class SomeList {
        private String mFirstValue;
        private float mSecondValue;
        private String mThirdValue;


        public SomeList(String firstvalue, float secondvalue, String thirdvalue) {
            mFirstValue = firstvalue;
            mSecondValue = secondvalue;
            mThirdValue = thirdvalue;
        }

        public String getFirstValue() {
            return mFirstValue;
        }

        public float getSecondValue() {
            return mSecondValue;
        }

        public String getThirdValue() {
            return mThirdValue;
        }
    }

    class SomeCustomAdapter extends ArrayAdapter<SomeList> {

        SomeCustomAdapter(Context context, ArrayList<SomeList> filteredList) {
            super(context, 0, filteredList);

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //initialize view
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.ranking, parent, false);
            }
            //get current object from SomeList class
            SomeList currentSomeList = getItem(position);

            //Placeholder for first value
            TextView mFirstTextView = (TextView) listItemView.findViewById(R.id.task_title);
            mFirstTextView.setText(String.valueOf(currentSomeList.getFirstValue()));

            //Placeholder for second value
            RatingBar mSecondTextView = (RatingBar) listItemView.findViewById(R.id.ocena);
            mSecondTextView.setRating(currentSomeList.getSecondValue());

            //Placeholder for first value
//            EditText mThirdTextView = (EditText) listItemView.findViewById(R.id.opis);
//            mThirdTextView.setText(String.valueOf(currentSomeList.getThirdValue()));

            return listItemView;
        }
    }
    }

