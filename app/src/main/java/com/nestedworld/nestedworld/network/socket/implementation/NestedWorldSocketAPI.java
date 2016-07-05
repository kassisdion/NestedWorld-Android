package com.nestedworld.nestedworld.network.socket.implementation;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionManager;
import com.nestedworld.nestedworld.models.Session;
import com.nestedworld.nestedworld.network.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.network.socket.listener.SocketListener;
import com.nestedworld.nestedworld.network.socket.models.DefaultModel;

import org.msgpack.value.ImmutableValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public final class NestedWorldSocketAPI implements SocketListener {

    //Singleton
    private static NestedWorldSocketAPI mSingleton;

    //Static field
    private final static int TIME_OUT = 10000;
    private final static String HOST = "eip.kokakiwi.net";
    private final static int PORT = 6464;

    //Private field
    private final String TAG = getClass().getSimpleName();
    private final SocketManager mSocketManager;
    private final ConnectionListener mConnectionListener;

    /*
    ** Constructor
     */
    private NestedWorldSocketAPI(@NonNull final ConnectionListener connectionListener) {
        //Init private field
        mConnectionListener = connectionListener;

        //Init the socket
        mSocketManager = new SocketManager(HOST, PORT);
        mSocketManager.setTimeOut(TIME_OUT);
        mSocketManager.addSocketListener(this);

        LogHelper.d(TAG, "Waiting for connection...");

        //Connect() require networking so we call it inside a thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                mSocketManager.connect();
            }
        }).start();
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
    ** Public method
     */
    public void sendRequest(@NonNull final DefaultModel data, @NonNull final SocketMessageType.MessageKind messageKind) {
        //Send a message with the corresponding key
        sendMessage(data.serialise(), SocketMessageType.messageType.getMap().get(messageKind));
    }

    /*
    ** Private method
     */
    private void authRequest(@NonNull String requestId) {
        Session session = SessionManager.get().getSession();
        if (session != null) {
            String token = session.authToken;

            ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
            mapBuilder.put(ValueFactory.newString("type"), ValueFactory.newString("authenticate"));
            mapBuilder.put(ValueFactory.newString("token"), ValueFactory.newString(token));

            sendMessage(mapBuilder, requestId);
        }
    }

    private void sendMessage(@NonNull final ValueFactory.MapBuilder mapBuilder, @NonNull final String requestId) {
        mapBuilder.put(ValueFactory.newString("id"), ValueFactory.newString(requestId));
        mSocketManager.send(mapBuilder.build());
    }

    private void parseSocketMessage(@NonNull final Map<Value, Value> message) {
        //Check if the message is a response
        if (message.containsKey(ValueFactory.newString("id"))) {
            //get the messageId
            final String messageId = message.get(ValueFactory.newString("id")).asStringValue().asString();

            //check if we know this id
            if (SocketMessageType.messageType.getMap().containsValue(messageId)) {
                final SocketMessageType.MessageKind kind = SocketMessageType.messageType.getInvertedMap().get(messageId);
                //Check it it's an auth response
                if (kind == SocketMessageType.MessageKind.TYPE_AUTHENTICATE) {
                    parseAuthMessage(message);
                    return;
                } else {
                    notifyMessageReceive(kind, message);
                    return;
                }
            }
        }

        //It's a spontaneous message, try to found the type
        if (message.containsKey(ValueFactory.newString("type"))) {
            final String type = message.get(ValueFactory.newString("type")).asStringValue().asString();

            //check if we know the type
            if (SocketMessageType.messageType.getMap().containsValue(type)) {
                final SocketMessageType.MessageKind kind = SocketMessageType.messageType.getInvertedMap().get(type);
                notifyMessageReceive(kind, message);
                return;
            }
        }
        LogHelper.d(TAG, "Can't parse message");
    }

    private void parseAuthMessage(@NonNull final Map<Value, Value> message) {
        if (message.get(ValueFactory.newString("result")).asStringValue().asString().equals("success")) {
            //Call connectionListener.onConnectionReady() inside the main thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mConnectionListener.onConnectionReady(mSingleton);
                }
            });
        } else {
            onSocketDisconnected();
            mSocketManager.disconnect();
        }
    }

    private void notifyMessageReceive(@NonNull SocketMessageType.MessageKind messageKind, @NonNull final Map<Value, Value> message) {
        LogHelper.d(TAG, "Notify: " + SocketMessageType.messageType.getMap().get(messageKind));
        mConnectionListener.onMessageReceived(messageKind, message);
    }

    /*
    ** SocketListener implementation
     */
    @Override
    public void onSocketConnected() {
        LogHelper.d(TAG, "Successfully got a connection");

        //Connection success, we can init the singleton
        mSingleton = NestedWorldSocketAPI.this;

        //Auth the connection
        authRequest("AUTH_REQUEST");
    }

    @Override
    public void onSocketDisconnected() {
        mSingleton = null;

        //Call connectionListener.onConnectionLost() inside the main thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mConnectionListener.onConnectionLost();
            }
        });
    }

    @Override
    public void onMessageReceived(ImmutableValue message) {
        switch (message.getValueType()) {
            case MAP:
                final Map<Value, Value> map = message.asMapValue().map();
                parseSocketMessage(map);
                break;
            default:
                //Cannot parse the message, undefined in the protocol
                LogHelper.d(TAG, "Can't parse message");
                break;
        }
    }
}

