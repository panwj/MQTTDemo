package com.mavl.im.sdk;


public class IMConstants {

    /**
     * The default keep alive interval in seconds if one is not specified
     */
    public static final int KEEP_ALIVE_INTERVAL_DEFAULT = 60;
    /**
     * The default connection timeout in seconds if one is not specified
     */
    public static final int CONNECTION_TIMEOUT_DEFAULT = 30;


    public static class Operation {
        /**
         * 未知操作
         */
        public static final int OPERATION_UNKNOWN = -1;

        /**
         * 发送1v1消息
         */
        public static final int OPERATION_SEND_MSG_ONE_TO_ONE = 1;

        /**
         * 发送群消息，1vN
         */
        public static final int OPERATION_SEND_MSG_GROUP = 2;

        /**
         * 发送群消息到虚拟群，消息体需要传递uidList
         */
        public static final int OPERATION_SEND_MSG_VIRTUAL = 3;

        /**
         * 创建群组
         */
        public static final int OPERATION_CREATE_GROUP = 0;

        /**
         * 加入組
         */
        public static final int OPERATION_JOIN_GROUP = 201;

        /**
         * 退出群
         */
        public static final int OPERATION_QUIT_GROUP = 202;

        /**
         * 上传deviceToken
         */
        public static final int OPERATION_UPLOAD_DEVICE_TOKEN = 300;

        /**
         * 拉取历史消息
         */
        public static final int OPERATION_GET_HISTORY_MSG = 401;
    }

    public static class MessageQos {
        public static final int QOS_0 = 0;
        public static final int QOS_1 = 1;
        public static final int QOS_2 = 2;
    }

}
