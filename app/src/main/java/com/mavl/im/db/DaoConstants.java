package com.mavl.im.db;

import android.provider.BaseColumns;

/**
 * Created by panwenjuan on 17-8-1.
 */
public class DaoConstants {
    protected static final String DB_NAME = "messageDB";
    protected static final int DB_VERSION = 1;

    public static class MessageEntry implements BaseColumns {
        public static final String DATABASE_TABLE_MESSAGES = "Messages";

        public static final String COLUMNS_MESSAGE_ID = "msg_id";
        public static final String COLUMNS_MESSAGE_CLIENT_ID = "msg_client_id";
        public static final String COLUMNS_MESSAGE_PAYLOAD = "msg_payload";
        public static final String COLUMNS_MESSAGE_TIMESTAMP = "msg_timeStamp";
        public static final String COLUMNS_MESSAGE_STATUS = "msg_status";
        public static final String COLUMNS_MESSAGE_IS_RECEIVED = "msg_isReceived";
        public static final String COLUMNS_MESSAGE_FROM_UID = "msg_fromUid";
        public static final String COLUMNS_MESSAGE_TO_UID = "msg_toUid";
    }

    public static class MessageEntry2 implements BaseColumns {
        public static final String DATABASE_TABLE_MESSAGES = "Messages2";

        public static final String COLUMNS_MESSAGE_ID = "msg_id";
        public static final String COLUMNS_MESSAGE_CLIENT_ID = "msg_client_id";
        public static final String COLUMNS_MESSAGE_PAYLOAD = "msg_payload";
        public static final String COLUMNS_MESSAGE_RETAINED = "msg_retained";
        public static final String COLUMNS_MESSAGE_TIMESTAMP = "msg_timeStamp";
        public static final String COLUMNS_MESSAGE_STATUS = "msg_status";
        public static final String COLUMNS_MESSAGE_IS_RECEIVED = "msg_isReceived";
        public static final String COLUMNS_MESSAGE_FROM_UID = "msg_fromUid";
        public static final String COLUMNS_MESSAGE_TO_UID = "msg_toUid";
    }

    public static final String CREATE_MESSAGES_TABLE = "CREATE TABLE "
            + MessageEntry.DATABASE_TABLE_MESSAGES + " ("
            + MessageEntry._ID + " INTEGER PRIMARY KEY,"
            + MessageEntry.COLUMNS_MESSAGE_ID + " TEXT,"
            + MessageEntry.COLUMNS_MESSAGE_CLIENT_ID + " INTEGER,"
            + MessageEntry.COLUMNS_MESSAGE_PAYLOAD + " TEXT,"
            + MessageEntry.COLUMNS_MESSAGE_TIMESTAMP + " INTEGER,"
            + MessageEntry.COLUMNS_MESSAGE_STATUS + " INTEGER,"
            + MessageEntry.COLUMNS_MESSAGE_IS_RECEIVED + " INTEGER,"
            + MessageEntry.COLUMNS_MESSAGE_FROM_UID + " TEXT,"
            + MessageEntry.COLUMNS_MESSAGE_TO_UID + " TEXT "
            + ");";

    public static final String CREATE_MESSAGES2_TABLE = "CREATE TABLE "
            + MessageEntry2.DATABASE_TABLE_MESSAGES + " ("
            + MessageEntry2._ID + " INTEGER PRIMARY KEY,"
            + MessageEntry2.COLUMNS_MESSAGE_ID + " TEXT,"
            + MessageEntry2.COLUMNS_MESSAGE_CLIENT_ID + " INTEGER,"
            + MessageEntry2.COLUMNS_MESSAGE_PAYLOAD + " TEXT,"
            + MessageEntry2.COLUMNS_MESSAGE_RETAINED + " INTEGER,"
            + MessageEntry2.COLUMNS_MESSAGE_TIMESTAMP + " INTEGER,"
            + MessageEntry2.COLUMNS_MESSAGE_STATUS + " INTEGER,"
            + MessageEntry2.COLUMNS_MESSAGE_IS_RECEIVED + " INTEGER,"
            + MessageEntry2.COLUMNS_MESSAGE_FROM_UID + " TEXT,"
            + MessageEntry2.COLUMNS_MESSAGE_TO_UID + " TEXT "
            + ");";
}
