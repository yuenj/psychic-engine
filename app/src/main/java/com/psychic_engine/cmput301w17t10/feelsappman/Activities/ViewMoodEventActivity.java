package com.psychic_engine.cmput301w17t10.feelsappman.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.psychic_engine.cmput301w17t10.feelsappman.Controllers.FileManager;
import com.psychic_engine.cmput301w17t10.feelsappman.Models.MoodEvent;
import com.psychic_engine.cmput301w17t10.feelsappman.Models.Participant;
import com.psychic_engine.cmput301w17t10.feelsappman.Models.ParticipantSingleton;
import com.psychic_engine.cmput301w17t10.feelsappman.R;

import java.util.ArrayList;

/**
 * Created by jyuen1 on 3/12/2017.
 */

/**
 * This is an activity/view class that displays information about a mood.
 */
public class ViewMoodEventActivity extends AppCompatActivity{
    private TextView name;
    private TextView dateTime;
    private ImageView photo;
    private TextView location;
    private TextView icon;          // Temporary for now to meet requirements until we have icons for mood
    private TextView trigger;
    private TextView socialIcon;    // Temporary for now to meet requirements until we have icon for social setting
    private ImageButton returnButton;

    ParticipantSingleton instance;
    int moodEventPosition;
    MoodEvent moodEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mood_event);

        name = (TextView) findViewById(R.id.me_name);
        dateTime = (TextView) findViewById(R.id.me_date_time);
        photo = (ImageView) findViewById(R.id.me_photo);
        location = (TextView) findViewById(R.id.me_location);
        icon = (TextView) findViewById(R.id.me_icon);
        trigger = (TextView) findViewById(R.id.me_trigger);
        socialIcon = (TextView) findViewById(R.id.me_social);
        returnButton = (ImageButton) findViewById(R.id.me_return);

        moodEventPosition = getIntent().getExtras().getInt("moodEventPosition");
        instance = ParticipantSingleton.getInstance();
        Participant self = instance.getSelfParticipant();

        MoodEvent moodEvent = null;
        try {
            ArrayList<MoodEvent> moodEventList = self.getMoodList();
            moodEvent = moodEventList.get(moodEventPosition);    // TODO: get appropriate mood later, right now gets the most recent image
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        name.setText(self.getLogin());
        dateTime.setText(moodEvent.getDate().toString());
        if (moodEvent.getPicture() != null)
            photo.setImageBitmap(moodEvent.getPicture().getImage());
        location.setText(moodEvent.getLocation());
        icon.setText(moodEvent.getMood().getColor().toString());
        trigger.setText(moodEvent.getTrigger());
        if (moodEvent.getSocialSetting() != null)
            socialIcon.setText(moodEvent.getSocialSetting().toString());

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        FileManager.saveInFile(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        FileManager.saveInFile(this);
    }

}