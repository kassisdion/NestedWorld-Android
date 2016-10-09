package com.nestedworld.nestedworld.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.events.socket.chat.OnMessageReceivedEvent;
import com.nestedworld.nestedworld.events.socket.chat.OnUserJoinedEvent;
import com.nestedworld.nestedworld.events.socket.chat.OnUserPartedEvent;
import com.nestedworld.nestedworld.events.socket.combat.OnAskMessageEvent;
import com.nestedworld.nestedworld.events.socket.combat.OnAttackReceiveEvent;
import com.nestedworld.nestedworld.events.socket.combat.OnAvailableMessageEvent;
import com.nestedworld.nestedworld.events.socket.combat.OnCombatEndEvent;
import com.nestedworld.nestedworld.events.socket.combat.OnCombatStartMessageEvent;
import com.nestedworld.nestedworld.events.socket.combat.OnMonsterKoEvent;
import com.nestedworld.nestedworld.gcm.NestedWorldGcm;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AskMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AvailableMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.CombatEndMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.MonsterKoMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.network.socket.models.message.message.MessageReceivedMessage;
import com.nestedworld.nestedworld.network.socket.models.message.message.UserJoinedMessage;
import com.nestedworld.nestedworld.network.socket.models.message.message.UserPartedMessage;

import org.greenrobot.eventbus.EventBus;
import org.msgpack.value.Value;

import java.util.Map;

public class SocketService extends Service {

    public final static String TAG = SocketService.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder();
    private NestedWorldSocketAPI mNestedWorldSocketAPI = null;

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
        NestedWorldSocketAPI.getInstance().addListener(new ConnectionListener() {
            @Override
            public void onConnectionReady(@NonNull NestedWorldSocketAPI nestedWorldSocketAPI) {
                mNestedWorldSocketAPI = nestedWorldSocketAPI;
            }

            @Override
            public void onConnectionLost() {
                mNestedWorldSocketAPI = null;

                //Clean API
                NestedWorldSocketAPI.reset();

                //Re-init API
                onStartCommand(intent, flags, startId);
            }

            @Override
            public void onMessageReceived(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
                //Do internal job
                parseMessage(message, messageKind, idKind);
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        NestedWorldSocketAPI.reset();
    }

    /*
    ** Public method (for client)
     */
    @Nullable
    public NestedWorldSocketAPI getApiInstance() {
        LogHelper.d(TAG, "returning instance (isNull=" + (mNestedWorldSocketAPI == null) + ")");
        return mNestedWorldSocketAPI;
    }

    /*
    ** Internal method
     */
    private void parseMessage(@NonNull final Map<Value, Value> message, @NonNull final SocketMessageType.MessageKind messageKind, @Nullable final SocketMessageType.MessageKind idKind) {
        //Handle notification
        NestedWorldGcm.onMessageReceived(this, message, messageKind, idKind);

        //Do internal job
        switch (messageKind) {
            case TYPE_CHAT_USER_JOINED:
                //Parse message
                UserJoinedMessage userJoinedMessage = new UserJoinedMessage(message, messageKind, idKind);

                //Send event
                EventBus.getDefault().post(new OnUserJoinedEvent(userJoinedMessage));
                break;
            case TYPE_CHAT_USER_PARTED:
                //Parse message
                UserPartedMessage userPartedMessage = new UserPartedMessage(message, messageKind, idKind);

                //Send event
                EventBus.getDefault().post(new OnUserPartedEvent(userPartedMessage));
                break;
            case TYPE_CHAT_MESSAGE_RECEIVED:
                //Parse message
                MessageReceivedMessage messageReceivedMessage = new MessageReceivedMessage(message, messageKind, idKind);

                //Send event
                EventBus.getDefault().post(new OnMessageReceivedEvent(messageReceivedMessage));
                break;
            case TYPE_COMBAT_START:
                //Parse response
                StartMessage startMessage = new StartMessage(message, messageKind, idKind);

                //Send notification
                EventBus.getDefault().post(new OnCombatStartMessageEvent(startMessage));
                break;
            case TYPE_COMBAT_AVAILABLE:
                //Parse response
                AvailableMessage availableMessage = new AvailableMessage(message, messageKind, idKind);
                availableMessage.saveAsCombat();

                //Send event
                EventBus.getDefault().post(new OnAvailableMessageEvent(availableMessage));
                break;
            case TYPE_COMBAT_MONSTER_KO:
                //Parse response
                MonsterKoMessage monsterKoMessage = new MonsterKoMessage(message, messageKind, idKind);

                //Send Event
                EventBus.getDefault().post(new OnMonsterKoEvent(monsterKoMessage));
                break;
            case TYPE_COMBAT_ATTACK_RECEIVED:
                //Parse response
                AttackReceiveMessage attackReveiveMessage = new AttackReceiveMessage(message, messageKind, idKind);

                //Send Event
                EventBus.getDefault().post(new OnAttackReceiveEvent(attackReveiveMessage));
                break;
            case TYPE_COMBAT_END:
                //Parse message
                CombatEndMessage combatEndMessage = new CombatEndMessage(message, messageKind, idKind);

                //Send event
                EventBus.getDefault().post(new OnCombatEndEvent(combatEndMessage));
                break;
            case TYPE_GEO_PLACES_CAPTURED:
                //Who know ?
                break;
            case TYPE_AUTHENTICATE:
                //Shouldn't use it (handle by socketManager)
                break;
            case TYPE_COMBAT_MONSTER_REPLACED:
                //It's a response (it's probably a result for chat:join:chanel)
                break;
            case TYPE_CHAT_JOIN_CHANNEL:
                //It's a response (it's probably a result for chat:join:chanel)
                break;
            case TYPE_CHAT_PART_CHANNEL:
                //It's a response (it's probably a result for chat:part:chanel)
                break;
            case TYPE_CHAT_SEND_MESSAGE:
                //It's a response (it's probably a result for chat:send:message)
                break;
            case TYPE_COMBAT_SEND_ATTACK:
                //It's a response (it's probably a result for combat:send:atk)
                break;
            case TYPE_COMBAT_MONSTER_KO_CAPTURE:
                //It's a response (it's probably a result for monster:ko:capture)
                break;
            case TYPE_COMBAT_MONSTER_KO_REPLACE:
                //It's a response (it's probably a result for monster:ko:replace)
                break;
            case TYPE_COMBAT_FLEE:
                //It's a response (it's probably a result for combat:flee)
                break;
            case TYPE_COMBAT_ASK:
                //Parse response (it's a result for combat:ask)
                AskMessage askMessage = new AskMessage(message, messageKind, idKind);

                //Send Event
                EventBus.getDefault().post(new OnAskMessageEvent(askMessage));
                break;
            case TYPE_RESULT:
                //It's a response, shouldn't use it
                break;
            default:
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
