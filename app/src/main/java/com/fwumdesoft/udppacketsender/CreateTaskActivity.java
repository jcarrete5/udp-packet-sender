package com.fwumdesoft.udppacketsender;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateTaskActivity extends AppCompatActivity {
    public static final String EXTRA_NEW_TASK = "newTask";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
    }

    public void createTask(View view) {
        String hostname = ((EditText)findViewById(R.id.txtAddress)).getText().toString();
        String port = ((EditText)findViewById(R.id.txtPort)).getText().toString();
        String strData = ((EditText)findViewById(R.id.txtData)).getText().toString();
        String taskName = ((EditText)findViewById(R.id.txtTaskName)).getText().toString();
        String strRepetitions = ((EditText)findViewById(R.id.txtRepetitions)).getText().toString();
        String strInterval = ((EditText)findViewById(R.id.txtInterval)).getText().toString();
        if(hostname.equals("") || port.equals("") || strData.equals("")) {
            Toast.makeText(this, R.string.toastFillOutFields, Toast.LENGTH_SHORT).show();
            return;
        }

        //Sanitize inputs
        if(strData.length() % 2 == 1) strData += "0";
        byte[] data = new byte[strData.length() / 2];
        for(int i = 0; i < data.length; i++) {
            try {
                data[i] = (byte) Integer.parseInt(strData.substring(i * 2, i * 2 + 2), 16);
            } catch(NumberFormatException e) {
                Toast.makeText(this, R.string.toastInvalidHexData, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        UDPTask task = null;
        try {
            task = new UDPTask(hostname, Integer.parseInt(port), data);
        } catch(NumberFormatException e) {
            Toast.makeText(this, R.string.toastInvalidPort, Toast.LENGTH_SHORT).show();
            return;
        }
        if(!strRepetitions.equals("")) {
            task.setRepetitions(Integer.parseInt(strRepetitions));
        }
        if(!strInterval.equals("")) {
            task.setRepeatInterval(Integer.parseInt(strInterval));
        }
        if(!taskName.equals("")) {
            task.updateName(taskName);
        }

        Intent intent = new Intent(this, ListTasksActivity.class);
        intent.putExtra(EXTRA_NEW_TASK, task);
        startActivity(intent);
    }
}
