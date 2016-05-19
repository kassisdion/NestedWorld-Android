package com.nestedworld.nestedworld.network.socket.implementation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.network.socket.listener.SocketListener;
import com.nestedworld.nestedworld.network.socket.models.DefaultModel;
import com.nestedworld.nestedworld.helpers.user.UserManager;
import com.nestedworld.nestedworld.helpers.log.LogHelper;

import org.msgpack.value.ValueFactory;

public final class NestedWorldSocketAPI {

    private static NestedWorldSocketAPI mSingleton;
    private final String TAG = getClass().getSimpleName();
    private final SocketManager mSocketManager;

    /*
    ** Constructor
     */
    private NestedWorldSocketAPI(@NonNull final ConnectionListener connectionListener) {
        mSocketManager = new SocketManager("eip.kokakiwi.net", 6464);
        mSocketManager.setTimeOut(10000);
        LogHelper.d(TAG, "Waiting for a new connection...");
        mSocketManager.addSocketListener(new SocketListener() {
            @Override
            public void onSocketConnected() {
                LogHelper.d(TAG, "Successfully Fully got a connection");
                mSingleton = NestedWorldSocketAPI.this;
                connectionListener.onConnectionReady(mSingleton);
            }

            @Override
            public void onSocketDisconnected() {
                LogHelper.e(TAG, "Connection failed");
                mSingleton = null;
                connectionListener.onConnectionLost();
            }

            @Override
            public void onMessageSent() {
                //A message has been send, should call some listener
            }

            @Override
            public void onMessageReceived(String message) {
                //TODO parse content and call listener
            }
        });
        mSocketManager.connect();
    }

    /*
    ** Singleton
     */
    public static void getInstance(@NonNull ConnectionListener connectionListener) {
        if (mSingleton == null) {
            new NestedWorldSocketAPI(connectionListener);
        } else {
            connectionListener.onConnectionReady(mSingleton);
        }
    }

    /*
    ** Avoid leek when log out
     */
    public static void reset() {
        mSingleton = null;
    }

    /*
    ** Private method
     */
    private void addAuthStateToMapValue(@NonNull Context context, @NonNull ValueFactory.MapBuilder mapBuilder) {
        String token = UserManager.get().getCurrentAuthToken(context);
        mapBuilder.put(ValueFactory.newString("token"), ValueFactory.newString(token == null ? "" : token));
    }

    public void combatRequest(@NonNull Context context, @NonNull DefaultModel data) {
        ValueFactory.MapBuilder mapBuilder = data.serialise();
        addAuthStateToMapValue(context, mapBuilder);
        mSocketManager.send(mapBuilder.build());
    }

    public void chatRequest(@NonNull Context context, @NonNull DefaultModel data) {
        ValueFactory.MapBuilder mapBuilder = data.serialise();
        addAuthStateToMapValue(context, mapBuilder);
        mSocketManager.send(mapBuilder.build());
    }
}
