package com.nestedworld.nestedworld.network.socket.models.message.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.database.models.Combat;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public class AvailableMessage extends DefaultMessage {

    private String type;
    private String combat_id;
    private String origin;
    private Integer monsterId;
    private String opponentPseudo;

    /*
    ** Constructor
     */
    public AvailableMessage(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @NonNull SocketMessageType.MessageKind idKind) {
        super(message, messageKind, idKind);
    }

    /*
    ** Life cycle
     */
    @Override
    protected void unSerialise(@NonNull Map<Value, Value> message) {
        Combat combat = new Combat();

        if (message.containsKey(ValueFactory.newString("type"))) {
            this.type = combat.type = message.get(ValueFactory.newString("type")).asStringValue().asString();
        }
        if (message.containsKey(ValueFactory.newString("id"))) {
            this.combat_id = combat.combat_id = message.get(ValueFactory.newString("id")).asStringValue().asString();
        }
        if (message.containsKey(ValueFactory.newString("origin"))) {
            this.origin = combat.origin = message.get(ValueFactory.newString("origin")).asStringValue().asString();
        }
        if (message.containsKey(ValueFactory.newString("monster_id"))) {
            this.monsterId = combat.monsterId = message.get(ValueFactory.newString("monster_id")).asIntegerValue().asInt();
        }
        if (message.containsKey(ValueFactory.newString("user"))) {
            Map<Value, Value> userInfo = message.get(ValueFactory.newString("user")).asMapValue().map();
            this.opponentPseudo = combat.opponent_pseudo = userInfo.get(ValueFactory.newString("pseudo")).asStringValue().asString();
        }
        combat.save();
    }

    /*
    ** Utils
     */
    public Combat saveAsCombat() {
        Combat combat = new Combat();
        combat.type = this.type;
        combat.combat_id = this.combat_id;
        combat.origin = this.origin;
        combat.monsterId = this.monsterId;
        combat.opponent_pseudo = this.opponentPseudo;

        combat.save();

        return combat;
    }

    /*
    ** Getter (generated)
     */
    public String getMessageId() {
        return combat_id;
    }
}
