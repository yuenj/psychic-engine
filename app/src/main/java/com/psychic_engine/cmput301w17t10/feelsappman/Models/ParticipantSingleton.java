package com.psychic_engine.cmput301w17t10.feelsappman.Models;

import android.util.Log;

import com.psychic_engine.cmput301w17t10.feelsappman.Activities.LoginActivity;

import java.util.ArrayList;

/**
 * Created by jyuen1 on 3/8/17.
 * Commented by adong
 */

/**
 * ParticipantSingleton class is used to initialize only one object for this class. The class will
 * contain the participant that is using the app, as well as the ArrayList of participants that
 * stores all of the participants for the app.
 */
public class ParticipantSingleton {
    /**
     * Instance is a one ParticipantSingleton
     * ArrayList<Participants> will be containing all of the participants in the program
     * selfParticipant will be the current participant using the app
     * participantcount will be the amount of participants that are currently stored.
     **/
    private static ParticipantSingleton instance = null;
    private ArrayList<Participant> participantList;
    private ArrayList<MoodEvent> offlineCreatedList = new ArrayList<>();
    private ArrayList<MoodEvent> offlineDeleteList = new ArrayList<>();
    private ArrayList<MoodEvent> offlineEditList = new ArrayList<>();
    private Participant selfParticipant;

    /**
     * Constructor ParticipantSingleton will initialize a new ArrayList<Participants>
     * Set participantCount to 0
     * The 'instance' is not initialized because we wont make the instance until we call getInstance()
     * This method is not accessed unless it is accessed by getInstance()
     * getInstance() will be the point where we will make a reference to the an ArrayList
     **/
    private ParticipantSingleton() {
        participantList = new ArrayList<>();
    }

    /**
    * Method required to access ParticipantSingleton() constructor
    * Will not make a new reference unless there is no instance (initially)
    * If there is an instance, we will just return the instance (containing the new ArrayList<Participant>)
    * The participant count will be 0
    **/
     public static ParticipantSingleton getInstance() {
        if (instance == null) {
            instance = new ParticipantSingleton();
        }
        return instance;
    }

    /** Method to get the main participant of the program
     * The initialization will not set a participant until you log in
     * @return true if successful in assigning selfParticipant
     * @return false if unsuccessful in assigning selfParticipant
     **/
     public Boolean setSelfParticipant(Participant participant) {
         try {
             for (Participant storedParticipant : instance.getParticipantList()) {
                 if (participant.getLogin().equals(storedParticipant.getLogin())) {
                     this.selfParticipant = participant;
                     return true;
                 }
             }
             return false;
         } catch (Throwable e) {
             this.selfParticipant = null;
             return false;
         }
    }

    /**
     * Getter method to obtain the ArrayList of participants stored
     * @return participantList
     */
    public ArrayList<Participant> getParticipantList() {
        return participantList;
    }

    /**
     * Getter method to obtain the main participant of the app
     * @return selfParticipant
     */
    public Participant getSelfParticipant() {
        return selfParticipant;
    }


    /**
     * Method to add a new participant into storage. It is only called in the login page
     * when you sign up for a new account.
     * @see LoginActivity
     * @param participant
     * @return true if successful
     * @return false if unsuccessful
     */
    public Boolean addParticipant(Participant participant) {
        try {
            if (participant.getLogin().equals("")){
                Log.i("Blank", "Blank login textbox");
                return false;
            }
            participantList.add(participant);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * Method to determine whether or not there is an instance or not. Used in the Singleton class
     * to determine whether or not it should initialize another ParticipantSingleton class
     * @see ParticipantSingleton
     * @return null if instance does not exist
     * @return instance if exists
     */
    public static ParticipantSingleton isLoaded() {
        return instance;
    }

    /**
     * Setter method to set the instance to be the newly created instance when you call getInstance()
     * method.
     * @param instance
     */
    public void setInstance(ParticipantSingleton instance) {
        this.instance = instance;
    }


    /**
     * Method searches for the participant object using their login name and returns the objectID
     * affiliated with that login to be used for either setting the selfParticipant value or
     * finding the object in the storage of participants
     * @param participantName
     * @return
     */
    public Participant searchParticipant(String participantName) {
        Log.d("StartSearch", "Starting the search");
        for(Participant participant : participantList) {
            Log.d("Stored", participant.getLogin());
            if (participant.getLogin().equals(participantName)) {
                return participant;
            }
        }
        return null;
    }

    /**
     * Getter method to return the mood list that was populated when there is no data connection.
     * @return list of moods to be added upon reconnection
     */
    public ArrayList<MoodEvent> getOfflineCreatedList() {
        return this.offlineCreatedList;
    }

    /**
     * Getter method to return the mood that was populated when there is no data connection. These
     * mood events in the list would be deleted in the server upon reconnection.
     * @return list of moods to be deleted upon reconnection
     */
    public ArrayList<MoodEvent> getOfflineDeleteList() {
        return this.offlineDeleteList;
    }

    /**
     * Adds the mood event into a singleton array to preserve the details for later use when there
     * is data connection. Upon connectivity, the mood events would eventually be synced to the servers
     * @param moodEvent
     */
    public void addNewOfflineMood(MoodEvent moodEvent) {
        this.offlineCreatedList.add(moodEvent);
    }

    /**
     * Adds the mood event that is to be deleted upon connection.  Acts as a carrier to keep track
     * of mood events that were deleted during offline mode
     * @param moodEvent
     */
    public void addDeleteOfflineMood(MoodEvent moodEvent) {
        this.offlineDeleteList.add(moodEvent);
    }

    /**
     * Adds the mood event that needs to be updated upon connection.
     */
    public void addEditOfflineMood(MoodEvent moodEvent) {
        this.offlineEditList.add(moodEvent);
    }

    /**
     * Getter method for edit mood list
     */
    public ArrayList<MoodEvent> getOfflineEditList() {
        return this.offlineEditList;
    }
}
