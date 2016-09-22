package com.nestedworld.nestedworld.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.event.socket.OnAvailableMessageEvent;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AvailableMessage;

import org.greenrobot.eventbus.EventBus;
import org.msgpack.value.Value;

import java.util.Map;

public class SocketService extends Service {

    public final static String TAG = SocketService.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder();

    /*
    ** Life cycle
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //Display some log
        LogHelper.d(TAG, "onBind()");

        return mBinder;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        //Display some log
        LogHelper.d(TAG, "onStartCommand()");

        //Instantiate a socketConnection listener
        NestedWorldSocketAPI.getInstance(new ConnectionListener() {
            @Override
            public void onConnectionReady(@NonNull NestedWorldSocketAPI nestedWorldSocketAPI) {
                //Do what you want (can send message)
            }

            @Override
            public void onConnectionLost() {
                //Clean API
                NestedWorldSocketAPI.reset();

                //Re-init API
                onStartCommand(intent, flags, startId);
            }

            @Override
            public void onMessageReceived(@NonNull SocketMessageType.MessageKind kind, @NonNull Map<Value, Value> content) {
                //Do internal job
                parseMessage(kind, content);
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        NestedWorldSocketAPI.reset();
    }

    /*
    ** Internal method
     */
    private void parseMessage(@NonNull SocketMessageType.MessageKind kind, @NonNull Map<Value, Value> content) {
        //Do internal job
        switch (kind) {
            case TYPE_CHAT_USER_JOINED:
                break;
            case TYPE_CHAT_USER_PARTED:
                break;
            case TYPE_CHAT_MESSAGE_RECEIVED:
                break;
            case TYPE_COMBAT_START:
                break;
            case TYPE_COMBAT_AVAILABLE:
                AvailableMessage availableMessage = new AvailableMessage(content);
                availableMessage.saveAsCombat();
                EventBus.getDefault().post(new OnAvailableMessageEvent(availableMessage));
                break;
            case TYPE_COMBAT_MONSTER_KO:
                break;
            case TYPE_COMBAT_ATTACK_RECEIVED:
                break;
            case TYPE_COMBAT_MONSTER_REPLACED:
                break;
            case TYPE_COMBAT_END:
                break;
            case TYPE_GEO_PLACES_CAPTURED:
                break;
            case TYPE_AUTHENTICATE:
                break;
            case TYPE_CHAT_JOIN_CHANNEL:
                break;
            case TYPE_CHAT_PART_CHANNEL:
                break;
            case TYPE_CHAT_SEND_MESSAGE:
                break;
            case TYPE_COMBAT_SEND_ATTACK:
                break;
            case TYPE_COMBAT_MONSTER_KO_CAPTURE:
                break;
            case TYPE_COMBAT_MONSTER_KO_REPLACE:
                break;
            case TYPE_COMBAT_FLEE:
                break;
            case TYPE_COMBAT_ASK:
                break;
            case TYPE_RESULT:
                break;
        }
    }

    /*
    ** Binder
     */
    public class LocalBinder extends Binder {
        public SocketService getService() {
            // Return this instance of SocketService so clients can call public methods
            return SocketService.this;
        }
    }
}
