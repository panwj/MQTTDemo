package com.mavl.im.sdk.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class IMMessage implements Parcelable {

    public int messageId;
    public int messageClientId;
    public String payload;
    public int qos = 1;
    public boolean retained = true;
    public boolean dup = false;
    public long timeStamp;

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
