package com.example.moviestorate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Rating;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviestorate.db.TaskContract;
import com.example.moviestorate.db.TaskDbHelper;

import java.util.ArrayList;

public class obejrzane extends AppCompatActivity {

    private ListView mTaskListView;
    private TaskDbHelper mHelper;
    private ArrayAdapter<SomeList> mAdapter;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obejrzane);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        System.out.println("Jestem w Obejrzane");

        mHelper = new TaskDbHelper(this);
        mTaskListView = (ListView) findViewById(R.id.movies);
        updateUI();

    }


    private void updateUI() {
        final ArrayList<SomeList> filteredList = new ArrayList<>(); // Stworzenie listy dla adaptera uzywajac obiektu SomeList jako inputu

        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format("SELECT " + TaskContract.TaskEntry.COL_MOVIE_TITLE +","+ TaskContract.TaskEntry.COL_MOVIE_RATE  +
                        ","+ TaskContract.TaskEntry.COL_MOVIE_DESCREPTION +
                " FROM " + TaskContract.TaskEntry.TABLE + " WHERE " + TaskContract.TaskEntry.COL_MOVIE_WATCHED + " = 1 ;"), //wybranie obejrzanych filmow
                null);

        while (cursor.moveToNext()) { //idzie po wynikach cursora
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_MOVIE_TITLE); //z kazdej pozycji jakiej jest sczytuje inex kolum
            int idx2 = cursor.getColumnIndex(TaskContract.TaskEntry.COL_MOVIE_RATE);
            int idx3 = cursor.getColumnIndex(TaskContract.TaskEntry.COL_MOVIE_DESCREPTION);
            String tytulSQL = cursor.getString(idx);
            float ocenaSQL = cursor.getFloat(idx2);
            String opisSQL = cursor.getString(idx3);
            if (opisSQL == null)
                opisSQL = "";
            System.out.println(ocenaSQL);
            filteredList.add(new SomeList(tytulSQL,ocenaSQL, opisSQL)); //dodaje do listy typu arraylist te wartosci
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
    public void deleteTask(View view) { //usuwanie z bazy
            View parent = (View) view.getParent();
            TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
            String task = String.valueOf(taskTextView.getText());
            SQLiteDatabase db = mHelper.getWritableDatabase();

            db.delete(TaskContract.TaskEntry.TABLE,
                    TaskContract.TaskEntry.COL_MOVIE_TITLE + " = ?",new String[]{task});
            db.close();
            updateUI();
    }


    public void showEdytuj(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        System.out.println(task);


        RatingBar ocena = (RatingBar) parent.findViewById(R.id.ocena);  //pobiera wartosci ustawione przez uzytkownika
        float ocenaLiczbowa = ocena.getRating();

        EditText taskEditText = (EditText) parent.findViewById(R.id.opis);
        String opis = String.valueOf(taskEditText.getText());

        System.out.println(ocenaLiczbowa);
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COL_MOVIE_RATE, ocenaLiczbowa);

        ContentValues values2 = new ContentValues();
        values2.put(TaskContract.TaskEntry.COL_MOVIE_DESCREPTION, opis);

        db.update(TaskContract.TaskEntry.TABLE,
                values,
                TaskContract.TaskEntry.COL_MOVIE_TITLE + " = ?",new String[]{task}); //zapisuje do bazy nowo ustawione wartosci
        db.update(TaskContract.TaskEntry.TABLE,
                values2,
                TaskContract.TaskEntry.COL_MOVIE_TITLE + " = ?",new String[]{task});
        db.close();
        updateUI();

    }

    public class SomeList { //obiekt do przechowywania danych ktore maja byc przekazane do adpatera
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
        }  //zwraca tytul

        public float getSecondValue() {
            return mSecondValue;
        } //zwraca ocene

        public String getThirdValue() {
            return mThirdValue;
        } //zwraca opis
    }

    class SomeCustomAdapter extends ArrayAdapter<SomeList> { //rozszerzam arrayadpater zeby mogl przekazywac wiecej zmiennnych do widomu

        SomeCustomAdapter(Context context, ArrayList<SomeList> filteredList) {
            super(context, 0, filteredList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { //metoda obiektu somelist
            View listItemView = convertView; //inicjalizuje widok
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.movie_watched, parent, false); //wklada layout do widoku???
            }
            SomeList currentSomeList = getItem(position); //bierze aktualny obiekt z klasy somelist

            TextView mFirstTextView = (TextView) listItemView.findViewById(R.id.task_title);//Placeholder for first value
            mFirstTextView.setText(String.valueOf(currentSomeList.getFirstValue()));

            RatingBar mSecondTextView = (RatingBar) listItemView.findViewById(R.id.ocena);//Placeholder for second value
            mSecondTextView.setRating(currentSomeList.getSecondValue());

            EditText mThirdTextView = (EditText) listItemView.findViewById(R.id.opis);  //Placeholder for first value
            mThirdTextView.setText(String.valueOf(currentSomeList.getThirdValue()));

            return listItemView;
        }
    }
    }

