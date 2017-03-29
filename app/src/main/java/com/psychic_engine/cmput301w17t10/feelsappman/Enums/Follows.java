package com.psychic_engine.cmput301w17t10.feelsappman.Enums;

/**
 * Created by jordi on 2017-03-29.
 */

public enum Follows {

    FOLLOWERS,
    FOLLOWING,
    FOLLOWREQUEST;

    @Override
    public String toString() {
        switch (this) {
            case FOLLOWERS: return "Followers";
            case FOLLOWING: return "Following";
            case FOLLOWREQUEST: return "Follow Requests";

            default:
                throw new IllegalArgumentException();

        }
    }
    }



