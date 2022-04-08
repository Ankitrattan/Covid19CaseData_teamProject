package com.cst2335.covid19casedata_teamproject.data;

import android.os.Parcel;
import android.os.Parcelable;

public class CaseData implements Parcelable {

    private String country,province,date;
    private int id,count;

    public CaseData(){

    }

    protected CaseData(Parcel in) {
        country = in.readString();
        province = in.readString();
        date = in.readString();
        id = in.readInt();
        count = in.readInt();
    }

    public static final Creator<CaseData> CREATOR = new Creator<CaseData>() {
        @Override
        public CaseData createFromParcel(Parcel in) {
            return new CaseData(in);
        }

        @Override
        public CaseData[] newArray(int size) {
            return new CaseData[size];
        }
    };

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(country);
        parcel.writeString(province);
        parcel.writeString(date);
        parcel.writeInt(id);
        parcel.writeInt(count);
    }
}
