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
    private static int geofenceId = 0;

    private List<LatLng> mLocations;
    private List<Integer> mRadii;
    private List<Integer> mGeofenceIDs;
    private String mTitle;
    private int mOccurrences;
    private int mTimeFrame;
    private String mComments;
    private int mId;
    private String mStartDate;
    private String mStartTime;
    private String mEndDate;
    private String mEndTime;
    private int mCurrentOccurrences;


    public Goal() {

    }

    public Goal(int id, String title, List<LatLng> locations, List<Integer> radii, List<Integer> geofenceIDs,
                int occurrences, int timeFrame, String comments, String startD, String endD, String startT, String endT, int currentOccurences) {
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
        mGeofenceIDs = geofenceIDs;
        mCurrentOccurrences = currentOccurences;

    }
    public Goal(String title, List<LatLng> locations, List<Integer> radii, List<Integer> geofenceIDs,
                int occurrences, int timeFrame, String comments, String startD, String endD, String startT, String endT) {
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
        mGeofenceIDs = geofenceIDs;
        mCurrentOccurrences = 0;
        id++;
    }

    public Goal(String title){
        mTitle = title;
        mLocations = new ArrayList<LatLng>();
        mRadii = new ArrayList<Integer>();
        mGeofenceIDs = new ArrayList<Integer>();
        mOccurrences = 0;
        mTimeFrame = 0;
        mComments = "";
        mId = id;
        mStartDate = "";
        mEndDate = "";
        mStartTime = "";
        mEndTime = "";
        mCurrentOccurrences = 0;
        id++;
    }

    private Goal(Parcel in){
        mLocations = new ArrayList<LatLng>();
        mRadii = new ArrayList<Integer>();
        mGeofenceIDs = new ArrayList<Integer>();
        mTitle = in.readString();
        mOccurrences = in.readInt();
        mTimeFrame = in.readInt();
        mComments = in.readString();
        mId = in.readInt();
        mStartDate = in.readString();
        mEndDate = in.readString();
        mStartTime = in.readString();
        mEndTime = in.readString();
        mCurrentOccurrences = in.readInt();
        //id++;
        in.readTypedList(mLocations, LatLng.CREATOR);
        in.readList(mRadii, Integer.class.getClassLoader());
        in.readList(mGeofenceIDs, Integer.class.getClassLoader());
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
        mGeofenceIDs.add(geofenceId++);
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


    public void setIds(List<Integer> ids){
        mGeofenceIDs = ids;
    }

    public int getCurrentOccurences(){
        return mCurrentOccurrences;
    }

    public void incrementOccurrences() {
        mCurrentOccurrences++;
    }

    public void setCurrentOccurrences(int occurrences) {
        mCurrentOccurrences = occurrences;
    }

    public List<Integer> getIds() {
        return mGeofenceIDs;
    }

    //unsure what to put here
    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags){
        out.writeString(mTitle);
        out.writeInt(mOccurrences);
        out.writeInt(mTimeFrame);
        out.writeString(mComments);
        out.writeInt(mId);
        out.writeString(mStartDate);
        out.writeString(mEndDate);
        out.writeString(mStartTime);
        out.writeString(mEndTime);
        out.writeInt(mCurrentOccurrences);
        out.writeTypedList(mLocations);
        out.writeList(mRadii);
        out.writeList(mGeofenceIDs);
    }

    public int getOverallID() {
        return id;
    }

    public static void setCurrentID(int previousId){
        id = previousId;
    }

    public static void setGeofenceId(int previousId){
        geofenceId = previousId;
    }


}
