package com.nestedworld.nestedworld.ui.fight.battle.player.base;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.nestedworld.nestedworld.database.models.Attack;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonsterAttackResponse;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.fight.battle.BattleMonsterAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;

public abstract class PlayerManager {
    protected final View mViewContainer;
    protected StartMessage.StartMessagePlayerMonster mCurrentMonster = null;
    protected ArrayList<MonsterAttackResponse.MonsterAttack> mCurrentMonsterAttacks = null;
    protected final BattleMonsterAdapter mAdapter = new BattleMonsterAdapter();
    protected int mRemaningMonster;

    /*
    ** Constructor
     */
    protected PlayerManager(@NonNull final View container, final int teamSize) {
        mViewContainer = container;
        mRemaningMonster = teamSize;
        ButterKnife.bind(this, container);
    }

    /*
    ** Method for child
     */
    public abstract void updateCurrentMonsterLife(@NonNull final AttackReceiveMessage.AttackReceiveMessageMonster monster);

    public abstract void displayAttackReceive();

    public abstract void displayAttackSend();

    public abstract void displayMonsterKo();

    protected abstract void displayCurrentMonster();

    /*
    ** Utils
     */
    @CallSuper
    public void onMonsterKo() {
        mRemaningMonster -= 1;
        displayMonsterKo();
    }

    public boolean hasRemainingMonster() {
        return mRemaningMonster > 0;
    }

    @CallSuper
    public void setCurrentMonster(@NonNull final StartMessage.StartMessagePlayerMonster monster, @NonNull final ArrayList<MonsterAttackResponse.MonsterAttack> attacks) {
        mCurrentMonster = monster;
        mCurrentMonsterAttacks = attacks;

        ((BaseAppCompatActivity) mViewContainer.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayCurrentMonster();
            }
        });
    }

    public StartMessage.StartMessagePlayerMonster getCurrentMonster() {
        return mCurrentMonster;
    }

    public boolean hasMonster(final long id) {
        return mCurrentMonster.id == id;
    }

    @Nullable
    public MonsterAttackResponse.MonsterAttack getMonsterAttackByType(@NonNull Attack.AttackType attackTypeWanted) {
        //Loop over current monster attack for finding an attack of the given type
        for (MonsterAttackResponse.MonsterAttack monsterAttack : mCurrentMonsterAttacks) {
            if (monsterAttack.infos.getType() == attackTypeWanted) {
                return monsterAttack;
            }
        }
        return null;
    }
}