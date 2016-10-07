package com.nestedworld.nestedworld.network.socket.models.message.message;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;

import java.util.Map;

public class MessageReceivedMessage extends DefaultMessage {

    /*
    ** Constructor
     */
    public MessageReceivedMessage(@NonNull Map<Value, Value> message) {
        super(message);
    }

    /*
    ** Life cycle
     */
    @Override
    protected void unSerialise(@NonNull Map<Value, Value> message) {

    }
}
