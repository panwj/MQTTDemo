package com.mavl.im.sdk.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class IMSubscribe implements Parcelable {

    public String topic;
    public int qos;

    public static final Creator<IMSubscribe> CREATOR = new Creator<IMSubscribe>() {

        public IMSubscribe createFromParcel(Parcel in) {
            return new IMSubscribe(in);
        }

        public IMSubscribe[] newArray(int size) {
            return new IMSubscribe[size];
        }
    };

    public void writeToParcel(Parcel out, int flags) {
         out.writeString(topic);
         out.writeInt(qos);
    }

    public IMSubscribe() {

    }

    private IMSubscribe(Parcel in) {
        topic = in.readString();
        qos = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "IMSubscribe{" +
                "topic='" + topic + '\'' +
                ", qos=" + qos +
                '}';
    }
}
