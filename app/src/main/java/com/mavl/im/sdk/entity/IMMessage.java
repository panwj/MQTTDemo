package com.mavl.im.sdk.entity;

import android.os.Parcel;
import android.os.Parcelable;


public class IMMessage implements Parcelable {

    public String messageId;//消息的真实ID，由后台返回
    public int messageLocalId = (int) System.currentTimeMillis();//消息的本地ID，用于与后台返回的消息体对应
    public String payload = "";//消息内容
    public long timeStamp;//生成消息的时间戳，毫秒
    public int status;
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
        out.writeInt(messageLocalId);
        out.writeString(payload);
        out.writeLong(timeStamp);
        out.writeInt(status);
        out.writeString(fromUid);
        out.writeString(toUid);
    }

    public IMMessage() {

    }

    private IMMessage(Parcel in) {
        messageId = in.readString();
        messageLocalId = in.readInt();
        payload = in.readString();
        timeStamp = in.readLong();
        status = in.readInt();
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
                ", messageLocalId=" + messageLocalId +
                ", payload='" + payload + '\'' +
                ", timeStamp=" + timeStamp +
                ", status=" + status +
                ", fromUid=" + fromUid +
                ", toUid=" + toUid +
                '}';
    }
}
