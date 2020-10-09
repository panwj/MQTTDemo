package com.mavl.im.sdk.config;

public class IMOperationConfig {

    /**
     * 创建群组
     */
    public static final int OPERATION_0 = 0;
    /**
     * 发送1v1消息
     */
    public static final int OPERATION_1 = 1;
    /**
     * 发送群消息，1vN
     */
    public static final int OPERATION_2 = 2;
    /**
     * 发送群消息到虚拟群，消息体需要传递uidList
     */
    public static final int OPERATION_3 = 3;
    /**
     * 加入圈子
     */
    public static final int OPERATION_201 = 201;
    /**
     * 退出群
     */
    public static final int OPERATION_202 = 202;
    /**
     * 上传deviceToken
     */
    public static final int OPERATION_300 = 300;
    /**
     * 拉取历史消息
     */
    public static final int OPERATION_401 = 401;
}
