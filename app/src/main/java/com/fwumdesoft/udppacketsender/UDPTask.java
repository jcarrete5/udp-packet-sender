package com.fwumdesoft.udppacketsender;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class UDPTask extends Thread {
    private SocketAddress address;
    private byte[] data;
    /** Time in milliseconds between UDP requests. Must not be negative. */
    private double repeatInterval;
    /** Number of times this task is repeated. Negative for indefinite repetition. */
    private int repetitions;

    private DatagramSocket socket;
    private DatagramPacket packet;
    private int numFailedPackets = 0;

    public UDPTask(@NonNull SocketAddress address, @NonNull byte[] data) throws IOException {
        this(address, data, 1);
    }

    public UDPTask(@NonNull SocketAddress address, @NonNull byte[] data, int repetitions) throws IOException {
        this(address, data, repetitions, 0);
    }

    public UDPTask(@NonNull SocketAddress address, @NonNull byte[] data, int repetitions, double interval) throws IOException {
        super("");
        if(address == null) throw new IllegalArgumentException("socket address must be non-null");
        if(data == null) throw new IllegalArgumentException("data byte array must be non-null");
        if(interval < 0) throw new IllegalArgumentException("repeat interval must be positive");
        this.address = address;
        this.data = data;
        repeatInterval = interval;
        this.repetitions = repetitions;

        socket = new DatagramSocket();
        socket.setSendBufferSize(data.length);
        socket.setBroadcast(true);
        packet = new DatagramPacket(data, data.length, address);
    }

    @Override
    public void run() {
        while(!isInterrupted() && repetitions != 0) {
            try {
                socket.send(packet);
            } catch(IOException e) {
                Log.e("UDPTask::" + getName(), "Failed to send a packet");
                numFailedPackets++;
            }

            if(repetitions > 0) {
                repetitions--;
            }
        }
        socket.close();
        Log.i("UDPTask::" + getName(), "Done");
    }

    public void setAddress(@NonNull SocketAddress address) {
        if(address == null) throw new IllegalArgumentException("socket address must be non-null");
        this.address = address;
        packet.setSocketAddress(address);
    }

    public void setData(@NonNull byte[] data) {
        if(data == null) throw new IllegalArgumentException("data byte array must be non-null");
        this.data = data;
        packet.setData(data);
    }

    public void setRepeatInterval(double interval) {
        if(interval < 0) throw new IllegalArgumentException("repeat interval must be positive");
        repeatInterval = interval;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public byte[] getData() {
        return data;
    }

    public double getRepeatInterval() {
        return repeatInterval;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public int getNumFailedPackets() {
        return numFailedPackets;
    }
}
