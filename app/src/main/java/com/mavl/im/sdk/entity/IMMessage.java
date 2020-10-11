package com.mavl.im.sdk.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.mavl.im.sdk.IMConstants;

public class IMMessage implements Parcelable {

    public String messageId;//消息的真实ID，由后台返回
    public int messageClientId = hashCode();//消息的本地ID，用于与后台返回的消息体对应
    public String payload;//消息内容
    public int qos = 1;//operation, 0: 消息最多分发一次； 1：消息最少分发一次； 2：消息只分发一次
    public boolean retained = true;
    public boolean dup = false;
    public long timeStamp;//生成消息的时间戳，毫秒

    public int status = IMConstants.MSG_SEND_STATUS_SENDING;//message status,  0：消息发送中； 1：消息发送成功； 2：消息发送失败
    public boolean isReceived = true;//true:是收到的消息； false:是发送到的消息
    public String fromUid = "";
    public String toUid = "";

    public static final Creator<IMMessage> CREATOR = new Creator<IMMessage>() {

        public IMMessage createFromParcel(Parcel in) {
            return new IMMessage(in);
        }

        public IMMessage[] newArray(int size) {
            return new IMMessage[size];
        }
    };

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(messageId);
        out.writeInt(messageClientId);
        out.writeString(payload);
        out.writeInt(qos);
        out.writeBoolean(retained);
        out.writeBoolean(dup);
        out.writeLong(timeStamp);
        out.writeInt(status);
        out.writeBoolean(isReceived);
        out.writeString(fromUid);
        out.writeString(toUid);
    }

    public IMMessage() {

    }

    private IMMessage(Parcel in) {
        messageId = in.readString();
        messageClientId = in.readInt();
        payload = in.readString();
        qos = in.readInt();
        retained = in.readBoolean();
        dup = in.readBoolean();
        timeStamp = in.readLong();
        status = in.readInt();
        isReceived = in.readBoolean();
        fromUid = in.readString();
        toUid = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "IMMessage{" +
                "messageId=" + messageId +
                ", messageClientId=" + messageClientId +
                ", payload='" + payload + '\'' +
                ", qos=" + qos +
                ", retained=" + retained +
                ", dup=" + dup +
                ", timeStamp=" + timeStamp +
                ", status=" + status +
                ", isReceived=" + isReceived +
                ", fromUid=" + fromUid +
                ", toUid=" + toUid +
                '}';
    }
}
