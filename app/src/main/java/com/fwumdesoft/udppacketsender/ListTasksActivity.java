package com.fwumdesoft.udppacketsender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ListTasksActivity extends AppCompatActivity {
    public static final String TASK_LIST_ITEM = "taskListItem#";
    public static final String TASK_LIST_ITEM_COUNT = "taskListItemCount";

    private List<UDPTask> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tasks);

        //Recreate task list
        int size = 1;
        if(savedInstanceState != null) {
            size = savedInstanceState.getInt(TASK_LIST_ITEM_COUNT);
        }
        taskList = new ArrayList<>(size);
        if(savedInstanceState != null) {
            for (int i = 0; i < size; i++) {
                UDPTask task = getUDPTask(savedInstanceState, TASK_LIST_ITEM + String.valueOf(i));
                taskList.add(task);
                Log.i("ListTasksActivity", task.toString());
            }
        }

        //Get newly created task
        UDPTask newTask = getIntentUDPTask();
        if(newTask != null) {
            taskList.add(newTask);
            newTask.start();
            //TODO: Add a callback to trigger task removal when the task is complete
        }

        //Update ListView
        ListView tasksListView = (ListView)findViewById(R.id.taskList);
        ArrayAdapter<UDPTask> adapter = new ArrayAdapter<UDPTask>(this, R.layout.list_item_task, R.id.lblTaskName, taskList) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) { //Add functionality to stop button for each task
                View view = super.getView(position, convertView, parent);
                Button btnStopTask = (Button) view.findViewById(R.id.btnStopTask);
                btnStopTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UDPTask task = getItem(position);
                        task.interrupt();
                    }
                });
                return view;
            }
        };
        if(tasksListView != null) tasksListView.setAdapter(adapter);
    }

    @SuppressWarnings("unchecked")
    private UDPTask getIntentUDPTask() {
        return (UDPTask) getIntent().getSerializableExtra(CreateTaskActivity.EXTRA_NEW_TASK);
    }

    private UDPTask getUDPTask(Bundle savedInstanceState, String key) {
        return (UDPTask) savedInstanceState.getSerializable(key);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(TASK_LIST_ITEM_COUNT, taskList.size());
        for(int i = 0; i < taskList.size(); i++) {
            outState.putSerializable(TASK_LIST_ITEM + String.valueOf(i), taskList.get(i));
            Log.i("ListTasksActivity", taskList.get(i).toString());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_list_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.newTask:
                Intent intent = new Intent(this, CreateTaskActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
