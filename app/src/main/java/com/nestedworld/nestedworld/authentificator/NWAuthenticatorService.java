package com.nestedworld.nestedworld.authentificator;

import android.accounts.AbstractAccountAuthenticator;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class NWAuthenticatorService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getAction().equals(
                android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
            return null;

        AbstractAccountAuthenticator authenticator = new NWAuthenticator(this);
        return authenticator.getIBinder();
    }
}