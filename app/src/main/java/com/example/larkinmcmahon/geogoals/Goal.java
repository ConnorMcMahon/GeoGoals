package com.example.larkinmcmahon.geogoals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by connor on 7/28/15.
 */
public class Goal implements Parcelable {

    private static int id = 0;

    private List<LatLng> mLocations;
    private List<Integer> mRadii;
    private String mTitle;
    private int mOccurrences;
    private int mTimeFrame;
    private String mComments;
    private int mId;
    private String mStartDate;
    private String mStartTime;
    private String mEndDate;
    private String mEndTime;


    public Goal() {

    }

    public Goal(int id, String title, List<LatLng> locations, List<Integer> radii, int occurrences, int timeFrame, String comments, String startD, String endD, String startT, String endT) {
        mTitle = title;
        mLocations = locations;
        mRadii = radii;
        mOccurrences = occurrences;
        mTimeFrame = timeFrame;
        mComments = comments;
        mId = id;
        mStartDate = startD;
        mEndDate = endD;
        mStartTime = startT;
        mEndTime = endT;
    }
    public Goal(String title, List<LatLng> locations, List<Integer> radii, int occurrences, int timeFrame, String comments, String startD, String endD, String startT, String endT) {
        mTitle = title;
        mLocations = locations;
        mRadii = radii;
        mOccurrences = occurrences;
        mTimeFrame = timeFrame;
        mComments = comments;
        mId = id;
        mStartDate = startD;
        mEndDate = endD;
        mStartTime = startT;
        mEndTime = endT;
        id++;
    }

    public Goal(String title){
        mTitle = title;
        mLocations = new ArrayList<LatLng>();
        mRadii = new ArrayList<Integer>();
        mOccurrences = 0;
        mTimeFrame = 0;
        mComments = "";
        mId = id;
        mStartDate = "";
        mEndDate = "";
        mStartTime = "";
        mEndTime = "";
        id++;
    }

    private Goal(Parcel in){
        mLocations = new ArrayList<LatLng>();
        mRadii = new ArrayList<Integer>();
        mTitle = in.readString();
        mOccurrences = in.readInt();
        mTimeFrame = in.readInt();
        mComments = in.readString();
        mId = id;
        mStartDate = in.readString();
        mEndDate = in.readString();
        mStartTime = in.readString();
        mEndTime = in.readString();
        //id++;
        in.readTypedList(mLocations, LatLng.CREATOR);
        in.readList(mRadii, Integer.class.getClassLoader() );
    }

    public static final Creator<Goal> CREATOR
            = new Creator<Goal>() {
        public Goal createFromParcel(Parcel in) {
            return new Goal(in);
        }

        public Goal[] newArray(int size){
            return new Goal[size];
        }
    };

    public void addGeofence(LatLng coord, int radius){
        mLocations.add(coord);
        mRadii.add(radius);
    }

    public int getID() { return mId; }
    public void setTitle(String title){
        mTitle = title;
    }

    public String getTitle(){
        return mTitle;
    }

    public List<LatLng> getLocations(){
        return mLocations;
    }

    public List<Integer> getRadii(){
        return mRadii;
    }

    public int getOccurance(){
        return mOccurrences;
    }

    public void setOccurance(int occurrences){
        mOccurrences = occurrences;
    }

    public int getTimeFrame(){
        return mTimeFrame;
    }

    public void setTimeFrame(int timeFrame){
        mTimeFrame = timeFrame;
    }

    public String getComments(){
        return mComments;
    }

    public void setComments(String comments){
        mComments = comments;
    }

    public void setStartDate(String start) {mStartDate = start;}

    public String getStartDate() {return mStartDate;}

    public void setEndDate(String end) {mEndDate = end;}

    public String getEndDate() {return mEndDate;}

    public void setStartTime(String start) {mStartTime = start;}

    public String getStartTime() {return mStartTime;}

    public void setEndTime(String end) {mEndTime = end;}

    public String getEndTime() {return mEndTime;}

    //unsure what to put here
    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags){
        out.writeString(mTitle);
        out.writeTypedList(mLocations);
        out.writeList(mRadii);
    }

    public int getOverallID() {
        return id;
    }

}
