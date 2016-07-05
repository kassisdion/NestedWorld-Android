package com.nestedworld.nestedworld.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.listener.ConnectionListener;

import org.msgpack.value.Value;

import java.util.Map;

public class SocketService extends Service {

    public final static String TAG = SocketService.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder();
    @Nullable private NestedWorldSocketAPI mNestedWorldSocketAPI = null;

    /*
    ** Binder
     */
    public class LocalBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }

    /*
    ** Life cycle
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogHelper.d(TAG, "onBind()");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LogHelper.d(TAG, "onStartCommand()");


        NestedWorldSocketAPI.getInstance(new ConnectionListener() {
            @Override
            public void onConnectionReady(@NonNull NestedWorldSocketAPI nestedWorldSocketAPI) {
                mNestedWorldSocketAPI = nestedWorldSocketAPI;
            }

            @Override
            public void onConnectionLost() {
                NestedWorldSocketAPI.reset();
            }

            @Override
            public void onMessageReceived(@NonNull SocketMessageType.MessageKind kind, @NonNull Map<Value, Value> content) {
                //TODO parse kind
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        LogHelper.d(TAG, "onCreate()");
    }

    @Override
    public void onDestroy() {
        NestedWorldSocketAPI.reset();
    }
}