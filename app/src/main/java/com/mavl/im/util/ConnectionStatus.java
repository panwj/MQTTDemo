package com.mavl.im.util;

/**
 * Connections status for  a connection
 */
public enum ConnectionStatus {
    /**
     * Client is Connected
     **/
    CONNECTED,

    /**
     * Client is Disconnected
     **/
    DISCONNECTED,

    /**
     * Client has encountered an Error
     **/
    ERROR,

    /**
     * Status is unknown
     **/
    NONE
}
