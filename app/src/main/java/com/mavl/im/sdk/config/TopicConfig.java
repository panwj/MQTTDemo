package com.mavl.im.sdk.config;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.mavl.im.sdk.IMConstants;
import com.mavl.im.sdk.entity.IMTopic;
import com.mavl.im.sdk.util.Logger;

public class TopicConfig {

    private static final String SEPARATOR = "/";

    public static IMTopic analysisTopic(String topic) {
        if (TextUtils.isEmpty(topic)) return null;
        String[] fields = topic.split(SEPARATOR);
        if (fields == null || fields.length <= 0) return null;
        int length = fields.length;
        IMTopic imTopic = new IMTopic();
        for (int i = 0; i < fields.length; i++) {
        }
        imTopic.appId = 0 < length ? fields[0] : "";
        imTopic.operation = 1 < length ? Integer.parseInt(fields[1]) : IMConstants.Operation.OPERATION_UNKNOWN;
        imTopic.messageLocalId = 2 < length ? Integer.valueOf(fields[2]) : 0;
        imTopic.toUid = 3 < length ? fields[3] : "";
        imTopic.messageId = 4 < length ? fields[4] : "";
        imTopic.fromUid = 5 < length ? fields[5] : "";
        if (!TextUtils.isEmpty(imTopic.messageId)) {
            imTopic.timeStamp = Long.parseLong(imTopic.messageId);
        } else {
            imTopic.timeStamp = System.currentTimeMillis();
        }

        return imTopic;
    }

    public static String createTopic(IMTopic topic) {
        if (topic == null) return "";
        String topicStr = IMGlobalConfig.getAppId() + SEPARATOR + topic.operation
                + SEPARATOR + topic.messageLocalId + SEPARATOR + topic.toUid;
        Logger.e("createTopic() : " + topicStr);
        return topicStr;
    }

    /**
     * 发送1v1消息
     * @param localId 本地消息id（为区分发送消息与后台返回消息体一致，messageId是后台返回的消息体id，是唯一id）
     * @param toUid
     * @return
     */
    public static String createOneToOneTopic(String localId, @NonNull String toUid) {
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMConstants.Operation.OPERATION_SEND_MSG_ONE_TO_ONE
                + SEPARATOR + localId + SEPARATOR + toUid;
        return topic;
    }

    /**
     * 创建群组
     * @param localId 本地群组id（为区分本地创建群组时与后台返回的消息对应）
     * @return
     */
    public static String createGroupTopic(String localId) {
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMConstants.Operation.OPERATION_CREATE_GROUP
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
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMConstants.Operation.OPERATION_SEND_MSG_GROUP
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
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMConstants.Operation.OPERATION_SEND_MSG_VIRTUAL
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
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMConstants.Operation.OPERATION_JOIN_GROUP
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
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMConstants.Operation.OPERATION_QUIT_GROUP
                + SEPARATOR + localId + SEPARATOR + gid;
        return topic;
    }

    /**
     * 上传设备deviceToken
     * @param localId
     * @return
     */
    public static String createUploadDeviceTopic(String localId) {
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMConstants.Operation.OPERATION_UPLOAD_DEVICE_TOKEN
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
        String topic = IMGlobalConfig.getAppId() + SEPARATOR + IMConstants.Operation.OPERATION_GET_HISTORY_MSG
                + SEPARATOR + localId + SEPARATOR + fromUid + SEPARATOR + type + SEPARATOR
                + (all ? "" : cursorTime) + SEPARATOR + offset;
        return topic;
    }
}
