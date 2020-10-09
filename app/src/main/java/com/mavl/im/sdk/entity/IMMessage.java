package com.mavl.im.sdk.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class IMMessage implements Parcelable {

    public int messageId;//消息的真实ID，由后台返回
    public int messageClientId = hashCode();//消息的本地ID，用于与后台返回的消息体对应
    public String payload;//消息内容
    public int qos = 1;//operation, 0: 消息最多分发一次； 1：消息最少分发一次； 2：消息只分发一次
    public boolean retained = true;
    public boolean dup = false;
    public long timeStamp;//生成消息的时间戳，纳秒

    public static final Creator<IMMessage> CREATOR = new Creator<IMMessage>() {

        public IMMessage createFromParcel(Parcel in) {
            return new IMMessage(in);
        }

        public IMMessage[] newArray(int size) {
            return new IMMessage[size];
        }
    };

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(messageId);
        out.writeInt(messageClientId);
        out.writeString(payload);
        out.writeInt(qos);
        out.writeBoolean(retained);
        out.writeBoolean(dup);
        out.writeLong(timeStamp);

    }

    public IMMessage() {

    }

    private IMMessage(Parcel in) {
        messageId = in.readInt();
        messageClientId = in.readInt();
        payload = in.readString();
        qos = in.readInt();
        retained = in .readBoolean();
        dup = in.readBoolean();
        timeStamp = in.readLong();
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
                '}';
    }
}
