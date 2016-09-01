package com.fwumdesoft.udppacketsender;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.Serializable;

public class UDPTask extends Service implements Serializable {
    private static final long serialVersionUID = 2;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
