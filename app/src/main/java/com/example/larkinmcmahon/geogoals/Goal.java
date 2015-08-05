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
    //TODO: need to add start & end date/time - put here or in DB?

    public Goal() {

    }

    public Goal(int id, String title, List<LatLng> locations, List<Integer> radii, int occurrences, int timeFrame, String comments) {
        mTitle = title;
        mLocations = locations;
        mRadii = radii;
        mOccurrences = occurrences;
        mTimeFrame = timeFrame;
        mComments = comments;
        mId = id;
    }
    public Goal(String title, List<LatLng> locations, List<Integer> radii, int occurrences, int timeFrame, String comments) {
        mTitle = title;
        mLocations = locations;
        mRadii = radii;
        mOccurrences = occurrences;
        mTimeFrame = timeFrame;
        mComments = comments;
        mId = id;
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
        id++;
    }

    private Goal(Parcel in){
        mLocations = new ArrayList<LatLng>();
        mRadii = new ArrayList<Integer>();
        mTitle = in.readString();
        in.readTypedList(mLocations, LatLng.CREATOR);
        in.readList(mRadii, Integer.class.getClassLoader() );
        mOccurrences = in.readInt();
        mTimeFrame = in.readInt();
        mId = in.readInt();
        mComments = in.readString();


    }

    public static final Parcelable.Creator<Goal> CREATOR
            = new Parcelable.Creator<Goal>() {
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
    //unsure what to put here
    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags){
        out.writeString(mTitle);
        out.writeTypedList(mLocations);
        out.writeList(mRadii);
        out.writeInt(mOccurrences);
        out.writeInt(mTimeFrame);
        out.writeInt(mId);
        out.writeString(mComments);


    }

    public int getOverallID() {
        return id;
    }

}
