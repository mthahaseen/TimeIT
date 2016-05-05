package com.overclocked.timeit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Thahaseen on 4/22/2016.
 */
public class SwipeData implements Parcelable{

    public SwipeData(){}

    String swipeDate;
    Long swipeInTime;
    Long swipeOutTime;
    int holiday;

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(swipeDate);
        dest.writeLong(swipeInTime);
        dest.writeLong(swipeOutTime);
        dest.writeInt(holiday);
    }

    private SwipeData(Parcel in) {
        this.swipeDate = in.readString();
        this.swipeInTime = in.readLong();
        this.swipeOutTime = in.readLong();
        this.holiday = in.readInt();
    }
    public static final Parcelable.Creator<SwipeData> CREATOR = new Parcelable.Creator<SwipeData>() {

        @Override
        public SwipeData createFromParcel(Parcel source) {
            return new SwipeData(source);
        }

        @Override
        public SwipeData[] newArray(int size) {
            return new SwipeData[size];
        }
    };

    public String getSwipeDate() {
        return swipeDate;
    }

    public void setSwipeDate(String swipeDate) {
        this.swipeDate = swipeDate;
    }

    public Long getSwipeInTime() {
        return swipeInTime;
    }

    public void setSwipeInTime(Long swipeInTime) {
        this.swipeInTime = swipeInTime;
    }

    public Long getSwipeOutTime() {
        return swipeOutTime;
    }

    public void setSwipeOutTime(Long swipeOutTime) {
        this.swipeOutTime = swipeOutTime;
    }

    public int getHoliday() {
        return holiday;
    }

    public void setHoliday(int holiday) {
        this.holiday = holiday;
    }
}
