package com.aleynasahin.todolist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import com.aleynasahin.todolist.databinding.DialogEditBinding;

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
        listAdapter.setOnItemLongClickListener(position -> {
            showOptionsDialog(position);
        });


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
        String task = binding.editText.getText().toString();

        if (task.isEmpty()) {
            binding.editText.setError("Please enter a task");
            return;
        }
        try {

            SQLiteDatabase database = this.openOrCreateDatabase("ToDoList", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS todolist (id INTEGER PRIMARY KEY,task VARCHAR)");
            String sqlString = "INSERT INTO todolist (task) VALUES(?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1, task);
            sqLiteStatement.execute();

            toDoListArrayList.clear();
            getData();

            
            if (binding.editText.getText().toString().isEmpty()) {
                binding.editText.setError("Please enter a task");
                return;
            }

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
    private void showOptionsDialog(int position) {

        String[] options = {"Edit", "Delete"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Choose an option")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        showEditDialog(position);
                    } else if (which == 1) {
                        deleteItem(position);
                    }
                })
                .show();
    }
    @SuppressLint("NotifyDataSetChanged")
    private void deleteItem(int position) {

        try {
            SQLiteDatabase database =
                    this.openOrCreateDatabase("ToDoList", MODE_PRIVATE, null);

            int id = toDoListArrayList.get(position).id;
            database.execSQL("DELETE FROM todolist WHERE id = " + id);

            toDoListArrayList.remove(position);
            listAdapter.notifyDataSetChanged();

            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showEditDialog(int position) {

        DialogEditBinding dialogBinding =
                DialogEditBinding.inflate(LayoutInflater.from(this));

        dialogBinding.editTask
                .setText(toDoListArrayList.get(position).todo);

        new AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Save", (dialog, which) -> {

                    String newTask =
                            dialogBinding.editTask.getText().toString();

                    if (!newTask.isEmpty()) {
                        updateTask(position, newTask);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateTask(int position, String newTask) {

        try {
            SQLiteDatabase database =
                    this.openOrCreateDatabase("ToDoList", MODE_PRIVATE, null);

            int id = toDoListArrayList.get(position).id;

            SQLiteStatement statement =
                    database.compileStatement("UPDATE todolist SET task = ? WHERE id = ?");
            statement.bindString(1, newTask);
            statement.bindLong(2, id);
            statement.execute();

            toDoListArrayList.get(position).todo = newTask;
            listAdapter.notifyDataSetChanged();

            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
