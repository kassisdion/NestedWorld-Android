package com.nestedworld.nestedworld.data.network.socket.implementation;

import com.nestedworld.nestedworld.BuildConfig;
import com.nestedworld.nestedworld.data.network.NetworkConstant;

public final class SocketEndPoint {
    private final static String DEV_URL = "realtime-dev.kokakiwi.net";
    private final static String PROD_URL = "realtime.kokakiwi.net";

    public final static String SOCKET_END_POINT =
            (BuildConfig.ENVIRONMENT == NetworkConstant.Environement.DEV) ? DEV_URL
                    : (BuildConfig.ENVIRONMENT == NetworkConstant.Environement.PROD) ? PROD_URL
                    : DEV_URL;

    private final static int DEV_PORT = 6466;
    private final static int PROD_PORT = 6466;
    public final static int SOCKET_PORT =
            (BuildConfig.ENVIRONMENT == NetworkConstant.Environement.DEV) ? DEV_PORT
                    : (BuildConfig.ENVIRONMENT == NetworkConstant.Environement.PROD) ? PROD_PORT
                    : DEV_PORT;

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    private SocketEndPoint() {
        //Private constructor for avoiding this class to be construct
    }
}
