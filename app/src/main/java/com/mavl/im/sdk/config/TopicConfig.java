package com.mavl.im.sdk.config;


import androidx.annotation.NonNull;

public class TopicConfig {

    private static final String SEPARATOR = "/";

    /**
     * 发送1v1消息
     * @param localId 本地消息id（为区分发送消息与后台返回消息体一致，messageId是后台返回的消息体id，是唯一id）
     * @param toUid
     * @return
     */
    public static String createOneToOneTopic(String localId, @NonNull String toUid) {
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMOperationConfig.OPERATION_1
                + SEPARATOR + localId + SEPARATOR + toUid;
        return topic;
    }

    /**
     * 创建群组
     * @param localId 本地群组id（为区分本地创建群组时与后台返回的消息对应）
     * @return
     */
    public static String createGroupTopic(String localId) {
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMOperationConfig.OPERATION_0
                + SEPARATOR + localId + SEPARATOR;
        return topic;
    }

    /**
     * 发送1vN消息
      * @param localId
     * @param toGid
     * @return
     */
    public static String createOneToGroupTopic(String localId, @NonNull String toGid) {
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMOperationConfig.OPERATION_2
                + SEPARATOR + localId + SEPARATOR + toGid;
        return topic;
    }

    /**
     * 发送1vN消息，IM后台不参与管理群组，在发送消息时消息体需要传递 uidList(uid1, uid2#消息体)
     * @param localId
     * @param toGid
     * @return
     */
    public static String createVirtualToGroupTopic(String localId, String toGid) {
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMOperationConfig.OPERATION_3
                + SEPARATOR + localId + SEPARATOR + toGid;
        return topic;
    }

    /**
     * 加入群
     * @param localId
     * @param gid ：所要加入群的gid
     * @return
     */
    public static String createAddGroupTopic(String localId, @NonNull String gid) {
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMOperationConfig.OPERATION_201
                + SEPARATOR + localId + SEPARATOR + gid;
        return topic;
    }

    /**
     * 退去群
     * @param localId
     * @param gid ：所要退出群的gid
     * @return
     */
    public static String createQuitGroupTopic(String localId, @NonNull String gid) {
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMOperationConfig.OPERATION_202
                + SEPARATOR + localId + SEPARATOR + gid;
        return topic;
    }

    /**
     * 上传设备deviceToken
     * @param localId
     * @return
     */
    public static String createUploadDeviceTopic(String localId) {
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMOperationConfig.OPERATION_300
                + SEPARATOR + localId + SEPARATOR;
        return topic;
    }

    /**
     * 拉取历史消息
     * @param localId
     * @param fromUid ：gid/uid
     * @param type : 群聊2；单聊1；
     * @param cursorTime : 从那个时间点开始拉取
     * @param offset : 拉取记录的条数
     * @param all : 是否拉取全部消息
     * @return
     */
    public static String createHistoryMsgTopic(String localId, String fromUid, int type, long cursorTime, int offset, boolean all) {
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMOperationConfig.OPERATION_401
                + SEPARATOR + localId + SEPARATOR + fromUid + SEPARATOR + type + SEPARATOR
                + (all ? "" : cursorTime) + SEPARATOR + offset;
        return topic;
    }
}
