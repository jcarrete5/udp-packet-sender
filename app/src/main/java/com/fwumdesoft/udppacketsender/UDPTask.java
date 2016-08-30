package com.fwumdesoft.udppacketsender;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class UDPTask extends Thread {
    private static long tasksCreated = 0;

    private String hostname;
    private int port;
    private byte[] data;

    /** Time in milliseconds between UDP requests. Must not be negative. */
    private int repeatInterval;
    /** Number of times this task is repeated. Negative for indefinite repetition. */
    private int repetitions;
    private int numFailedPackets = 0;

    public UDPTask(String hostname, int port, byte[] data) {
        this(hostname, port, data, 1);
    }

    public UDPTask(String hostname, int port, byte[] data, int repetitions) {
        this(hostname, port, data, repetitions, 0);
    }

    public UDPTask(String hostname, int port, byte[] data, int repetitions, int interval) {
        this(hostname, port, data, repetitions, interval, "task" + tasksCreated++);
    }
    public UDPTask(String hostname, int port, byte[] data, int repetitions, int interval, String name) {
        super(name);
        if(data == null) throw new IllegalArgumentException("data byte array must be non-null");
        if(interval < 0) throw new IllegalArgumentException("repeat interval must be positive");
        this.hostname = hostname;
        this.port = port;
        this.data = data;
        repeatInterval = interval;
        this.repetitions = repetitions;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        DatagramPacket packet = null;
        try {
            socket = new DatagramSocket();
            socket.setSendBufferSize(data.length);
            socket.setBroadcast(true);
        } catch(SocketException e) {
            Log.w("UDPTask::" + getName(), "Failed to bind the socket", e);
            interrupt();
        }
        try {
            packet = new DatagramPacket(data, data.length, new InetSocketAddress(hostname, port));
        } catch(SocketException e) {
            Log.w("UDPTask::" + getName(), "Failed to identify remote host", e);
            interrupt();
        }

        while(!isInterrupted() && repetitions != 0) {
            try {
                socket.send(packet);
            } catch(IOException e) {
                Log.i("UDPTask::" + getName(), "Failed to send a packet");
                numFailedPackets++;
            }
            try {
                Thread.sleep(repeatInterval);
            } catch(InterruptedException e) {
                Log.i("UDPTask::" + getName(), "Cancelled");
                interrupt();
            }

            if(repetitions > 0) {
                repetitions--;
            }
        }

        if(socket != null) socket.close();
        Log.i("UDPTask::" + getName(), "Done");
    }

    public void setRepeatInterval(int interval) {
        if(interval < 0) throw new IllegalArgumentException("repeat interval must be positive");
        repeatInterval = interval;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public int getNumFailedPackets() {
        return numFailedPackets;
    }
}
