package com.psychic_engine.cmput301w17t10.feelsappman;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by adong on 2/27/17.
 */

/**
 * Participant model to describe the attributes of the participant. Each participant upon signup
 * will hold their own personal arrays of mood events, followers, following, and pending requests.
 * Currently the last three are out of service until later development.
 * @see LoginActivity
 */
public class Participant extends ModelFrame{
    public String login;
    public MoodEvent mostRecentMoodEvent;
    public int mostRecentMoodEventIndex;
    public ArrayList<MoodEvent> moodEvents;
    public ArrayList<Participant> followers;
    public ArrayList<Participant> following;
    public ArrayList<Participant> pendingRequests;
    public String id;

    /**
     * Everytime the participant is initialized, we will save their name into the system. Since
     * the account is new, they will have no mood events, no followers, no following, and no
     * pending requests.
     * @param loginName
     */
    public Participant(String loginName) {
        this.login = loginName;
        this.moodEvents = new ArrayList<MoodEvent>();
        this.followers = new ArrayList<Participant>();
        this.following = new ArrayList<Participant>();
        this.pendingRequests = new ArrayList<Participant>();
    }

    //TODO: Potentially unique ID ???
    /**
     * Getter method to get the ID that was set by the elastic search server
     */
    public String getID() {
        return this.id;
    }

    //TODO: Potentially unique ID ???
    /**
     * Setter method to set the ID that was given in the elastic search server
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Getter method to get the login
     * @return
     */
    public String getLogin() {return this.login;}

    /**
     * Getter method to get the mood list of the participant. Will return NULL pointer
     * if called when empty.
     * @return
     */
    public ArrayList<MoodEvent> getMoodList() {
        return this.moodEvents;
    }

    /**
     * Getter method to get the followers list. Will return NULL poiinter if called when empty.
     * @return
     */
    public ArrayList<Participant> getFollowers() {
        return this.followers;
    }

    /**
     * Getter method to get the following list, Will return NULL pointer if called when empty.
     * @return
     */
    public ArrayList<Participant> getFollowing() {
        return this.following;
    }

    /**
     * Getter method to get the pending requests list. Will return NULL pointer if called empty.
     * @return
     */
    public ArrayList<Participant> getPendingRequests() {
        return this.pendingRequests;
    }

    /**
     * Getter for mostRecentMoodEvent
     * @return
     */
    public MoodEvent getMostRecentMoodEvent() { return this.mostRecentMoodEvent; }

    /**
     * Getter for mostRecentMoodEventIndex
     * @return
     */
    public int getMostRecentMoodEventIndex() { return this.mostRecentMoodEventIndex; }

    /**
     * replace old mood event with new mood event at index. Currently out of service until further
     * notice. Most likely be issued into the controller class instead if any.
     * @param index
     * @param moodEvent
     */
    public void setMoodEvent(int index, MoodEvent moodEvent) {
        if (mostRecentMoodEvent == null) {
            mostRecentMoodEvent = moodEvent;
            mostRecentMoodEventIndex = index;
        }
        else if (moodEvent.getDate().after(mostRecentMoodEvent.getDate())) {
            mostRecentMoodEvent = moodEvent;
            mostRecentMoodEventIndex = index;
        }

        moodEvents.set(index, this.mostRecentMoodEvent);
    }

    public void addMoodEvent(MoodEvent moodEvent) {
        if (moodEvents.isEmpty()) {
            Log.d("Empty", "MoodEvents is empty for "+ login);
        }

        if (mostRecentMoodEvent == null) {
            mostRecentMoodEvent = moodEvent;
            mostRecentMoodEventIndex = moodEvents.size();
        }
        else if (moodEvent.getDate().after(mostRecentMoodEvent.getDate())) {
            mostRecentMoodEvent = moodEvent;
            mostRecentMoodEventIndex = moodEvents.size();
        }

        moodEvents.add(moodEvent);
        Log.d("Success", "Successful addition of mood event");
        Log.d("Added", this.moodEvents.get(0).getMood().getMood().toString());
    }

    public void removeMoodEvent(int index) {
        moodEvents.remove(index);

        if (moodEvents.size() == 0) {
            mostRecentMoodEvent = null;
            mostRecentMoodEventIndex = -1;
        }
        else {
            Date earliestDate = moodEvents.get(0).getDate();
            for (int i = 0; i < moodEvents.size(); i++) {
                if (moodEvents.get(i).getDate().after(earliestDate)) {
                    mostRecentMoodEventIndex = i;
                    mostRecentMoodEvent = moodEvents.get(mostRecentMoodEventIndex);
                }

            }
        }
    }

    public void setMoodList(ArrayList<MoodEvent> moodList)
    {
        this.moodEvents = moodList;
    }
}


