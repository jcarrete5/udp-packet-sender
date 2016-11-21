package com.fwumdesoft.udppacketsender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Posts a UDP message with the given data to the
 * target address on the target port.
 */
class UdpPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_post);
    }

    public void sendUdpMessage(View view) {
        //Get input
        String targetHost = ((EditText)findViewById(R.id.txtAddress)).getText().toString();
        String strTargetPort = ((EditText)findViewById(R.id.txtPort)).getText().toString();
        String strData = ((EditText)findViewById(R.id.txtData)).getText().toString();

        //Check and sanitize input
        if(targetHost.equals("") || strTargetPort.equals("") || strData.equals("")) {
            Toast.makeText(this, R.string.toastFillOutFields, Toast.LENGTH_SHORT).show();
            return;
        }
        if(strData.length() % 2 == 1) strData += "0";
        byte[] data = new byte[strData.length() / 2];
        for(int i = 0; i < data.length; i++) {
            try {
                data[i] = (byte) Integer.parseInt(strData.substring(i * 2, i * 2 + 2), 16);
            } catch(NumberFormatException e) {
                Log.i("UdpPostActivity", "Invalid HEX Data");
                Toast.makeText(this, R.string.toastInvalidHexData, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        int targetPort;
        try {
            targetPort = Integer.parseInt(strTargetPort);
            if(targetPort < 0 || targetPort > 65535) throw new NumberFormatException();
        } catch(NumberFormatException e) {
            Log.i("UdpPostActivity", "Invalid target port");
            Toast.makeText(this, R.string.toastInvalidPort, Toast.LENGTH_SHORT).show();
            return;
        }

        //Post UDP message
        postUdpMessage(targetHost, targetPort, data);
    }

    private void postUdpMessage(final String targetHost, final int targetPort, final byte[] data) {
        final Runnable messageFailed = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UdpPostActivity.this, R.string.toastFailedToSend, Toast.LENGTH_SHORT).show();
            }
        };

        new Thread() {
            @Override
            public void run() {
                try(DatagramSocket socket = new DatagramSocket()) {
                    DatagramPacket packet = new DatagramPacket(data, data.length, new InetSocketAddress(targetHost, targetPort));
                    socket.setBroadcast(true);
                    socket.send(packet);
                    Log.d("UdpPostActivity", "Message sent");
                } catch(IOException e) {
                    Log.w("UdpPostActivity", "Failed to send UDP message", e);
                    UdpPostActivity.this.runOnUiThread(messageFailed);
                }
            }
        }.start();
    }
}
