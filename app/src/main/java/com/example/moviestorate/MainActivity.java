package com.example.moviestorate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView mTaskListView; //obiekt listy która będzie aktualizowana
    private TaskDbHelper mHelper; //obiekt do obslugi bazy danych
    private ArrayAdapter<String> mAdapter; //adapter do łączenia danych z widokami, potrzebny do obsługi listview

    @Override
    protected void onCreate(Bundle savedInstanceState) { //co ma sie robic podczas otwarcia aplikacji
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelper = new TaskDbHelper(this);
        mTaskListView = (ListView) findViewById(R.id.movies); //wybiera liste ktra ma obslugiwac
        updateUI();


    }

    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>(); //nowa lista rzeczy ktore adapter ma włożyć do listview
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format("SELECT " + TaskContract.TaskEntry.COL_MOVIE_TITLE + " FROM " +
                TaskContract.TaskEntry.TABLE +" WHERE " + TaskContract.TaskEntry.COL_MOVIE_WATCHED + " = 0 ;"), null); //wykonaj komende na bazie sql
        while (cursor.moveToNext()) { //przechodzi do kolejnej wzroconej wartosci
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_MOVIE_TITLE); //sprawdzenie ktora kolumna zawiera tytul
            taskList.add(cursor.getString(idx));//dodaje zawartosc komorki tej kolumny do listy
        }

        if (mAdapter == null) { //jezeli nie bylo przypisanej zadnej wartosci do adaptera
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.movie_towatch, //w ktorym layoucie
                    R.id.task_title, //pod ktory element
                    taskList);//co ma wsadzic
            mTaskListView.setAdapter(mAdapter); //ten adapter ma sluzyc do aktualizacji tej listy
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged(); //refresh widoku
        }

        cursor.close(); //zamkniecie zapytania do sql
        db.close();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //tworzenie menu
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task: //dodawanie filmu
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Dodaj film")
                        .setMessage("Jaki film chcesz obejrzeć?")
                        .setView(taskEditText)
                        .setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String film = String.valueOf(taskEditText.getText());
                                SQLiteDatabase db = mHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(TaskContract.TaskEntry.COL_MOVIE_TITLE, film);
                                db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE); //nadpisac
                                db.close();
                                updateUI();
                            }
                        })
                        .setNegativeButton("Anuluj", null)
                        .create();
                dialog.show();
                return true;

            case R.id.obejrzane:
                Intent intent2 = new Intent(this, obejrzane.class); //uruchamianie activity
                this.startActivity(intent2);
                return true;

            case R.id.ranking_lista:
                Intent intent3 = new Intent(this, ranking_lista.class);
                this.startActivity(intent3);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void watchedTask(View view) { //metoda do ustawiania jako obejrzane
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COL_MOVIE_WATCHED, 1); //co ma insertowac

        db.update(TaskContract.TaskEntry.TABLE,
                values,
                TaskContract.TaskEntry.COL_MOVIE_TITLE + " = ?",new String[]{task}); //w db zmienia wartosc 0 na 1
        db.close();
        updateUI();
    }



}
