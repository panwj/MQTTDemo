package com.mavl.im.sdk.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class IMTopic implements Parcelable {

    public String appId = "";
    public int operation;
    public int messageClientId;
    public String messageId = "";
    public String toUid = "";
    public String fromUid = "";
    public long timeStamp = System.currentTimeMillis();

    public IMTopic() {

    }

    public static final Creator<IMTopic> CREATOR = new Creator<IMTopic>() {

        public IMTopic createFromParcel(Parcel in) {
            return new IMTopic(in);
        }

        public IMTopic[] newArray(int size) {
            return new IMTopic[size];
        }
    };

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(appId);
        out.writeInt(operation);
        out.writeInt(messageClientId);
        out.writeString(messageId);
        out.writeString(toUid);
        out.writeString(fromUid);
        out.writeLong(timeStamp);
    }

    private IMTopic(Parcel in) {
        appId = in.readString();
        operation = in.readInt();
        messageClientId = in.readInt();
        messageId = in.readString();
        toUid = in.readString();
        fromUid = in.readString();
        timeStamp = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "IMTopic{" +
                "appId='" + appId + '\'' +
                ", operation=" + operation +
                ", messageClientId=" + messageClientId +
                ", messageId='" + messageId + '\'' +
                ", toUid='" + toUid + '\'' +
                ", fromUid='" + fromUid + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
