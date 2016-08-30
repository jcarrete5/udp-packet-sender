package com.fwumdesoft.udppacketsender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;

public class CreateTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
    }

    public void createTask(View view) {
        String hostname = ((EditText)findViewById(R.id.txtAddress)).getText().toString();
        String port = ((EditText)findViewById(R.id.txtPort)).getText().toString();
        String strData = ((EditText)findViewById(R.id.txtData)).getText().toString();
        if(hostname.equals("") || port.equals("") || strData.equals("")) {
            Toast.makeText(this, R.string.toastFillOutFields, Toast.LENGTH_SHORT).show();
            return;
        }

        if(strData.length() % 2 == 1) strData += "0";
        byte[] data = new byte[strData.length() / 2];
        for(int i = 0; i < data.length; i++) {
            data[i] = (byte)Integer.parseInt(strData.substring(i * 2, i * 2 + 2), 16);
        }

        UDPTask task = new UDPTask(hostname, Integer.parseInt(port), data);
        task.start();
    }
}
