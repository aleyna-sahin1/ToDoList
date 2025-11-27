package com.aleynasahin.todolist;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aleynasahin.todolist.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    ArrayList<List> toDoListArrayList;
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        View view = binding.getRoot();
        setContentView(view);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toDoListArrayList = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listAdapter = new ListAdapter(toDoListArrayList);
        binding.recyclerView.setAdapter(listAdapter);

        getData();

    }

    @SuppressLint("NotifyDataSetChanged")
    public void getData() {

        try {
            SQLiteDatabase database = this.openOrCreateDatabase("ToDoList", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS todolist (id INTEGER PRIMARY KEY,task VARCHAR)");

            Cursor cursor = database.rawQuery("SELECT * FROM todolist", null);

            int idIx = cursor.getColumnIndex("id");
            int taskIx = cursor.getColumnIndex("task");

            while (cursor.moveToNext()) {

                int id = cursor.getInt(idIx);
                String todo = cursor.getString(taskIx);
                List list = new List(id, todo);
                toDoListArrayList.add(list);

            }
            listAdapter.notifyDataSetChanged();
            cursor.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public void add(View view) {

        try {
            String task = binding.editText.getText().toString();

            SQLiteDatabase database = this.openOrCreateDatabase("ToDoList", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS todolist (id INTEGER PRIMARY KEY,task VARCHAR)");
            String sqlString = "INSERT INTO todolist (task) VALUES(?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1, task);
            sqLiteStatement.execute();

            toDoListArrayList.clear();
            getData();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Toast.makeText(this, "Task added to your list!", Toast.LENGTH_LONG).show();

        binding.editText.setText("");


    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear(View view) {

        try {
            SQLiteDatabase database = this.openOrCreateDatabase("ToDoList", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS todolist (id INTEGER PRIMARY KEY,task VARCHAR)");
            database.execSQL("DELETE FROM todolist");

            toDoListArrayList.clear();
            listAdapter.notifyDataSetChanged();

            Toast.makeText(this, "All tasks removed!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
