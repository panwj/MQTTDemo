package com.mavl.im.entity;

import android.os.Parcel;
import android.os.Parcelable;


public class Message implements Parcelable {

    public String messageId;//消息的真实ID，由后台返回
    public int messageLocalId = hashCode();//消息的本地ID，用于与后台返回的消息体对应
    public String payload = "";//消息内容
    public long timeStamp;//生成消息的时间戳，毫秒
    public int status;//0: 正在发送, 1: 发送成功, 2:发送失败
    public String fromUid = "";
    public String toUid = "";
    public boolean isReceived = true;

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
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
        out.writeInt(isReceived ? 1 : 0);
    }

    public Message() {

    }

    private Message(Parcel in) {
        messageId = in.readString();
        messageLocalId = in.readInt();
        payload = in.readString();
        timeStamp = in.readLong();
        status = in.readInt();
        fromUid = in.readString();
        toUid = in.readString();
        isReceived = in.readInt() == 1 ? true : false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", messageLocalId=" + messageLocalId +
                ", payload='" + payload + '\'' +
                ", timeStamp=" + timeStamp +
                ", status=" + status +
                ", fromUid='" + fromUid + '\'' +
                ", toUid='" + toUid + '\'' +
                ", isReceived=" + isReceived +
                '}';
    }
}
