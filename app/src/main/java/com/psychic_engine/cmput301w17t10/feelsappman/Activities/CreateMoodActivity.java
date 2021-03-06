package com.psychic_engine.cmput301w17t10.feelsappman.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.psychic_engine.cmput301w17t10.feelsappman.Controllers.CreateMoodController;
import com.psychic_engine.cmput301w17t10.feelsappman.Controllers.FileManager;
import com.psychic_engine.cmput301w17t10.feelsappman.Exceptions.EmptyMoodException;
import com.psychic_engine.cmput301w17t10.feelsappman.Exceptions.TriggerTooLongException;
import com.psychic_engine.cmput301w17t10.feelsappman.Models.MoodLocation;
import com.psychic_engine.cmput301w17t10.feelsappman.Enums.MoodState;
import com.psychic_engine.cmput301w17t10.feelsappman.Models.Photograph;
import com.psychic_engine.cmput301w17t10.feelsappman.R;
import com.psychic_engine.cmput301w17t10.feelsappman.Enums.SocialSetting;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import static com.psychic_engine.cmput301w17t10.feelsappman.R.id.imageView;
import static java.lang.Boolean.TRUE;

/**
 * Created by jyuen1 on 3/6/17.
 * Comments by Alex Dong on 3/12/17.
 * Location and photo by Pierre Lin on 3/28/2017
 */

/**
 * CreateMoodActivity will be similar to the EditMoodActivity in such a way that the format will be
 * the same. However, one will be able to edit previously created events and one will only be able
 * to createMoodEvent new ones. The participant will be able to enter a variety of options where the mood
 * state is mandatory for entry, while others are optional.
 * @see EditMoodActivity
 */
public class CreateMoodActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    private LocationManager lm;
    private LocationListener locationListener;

    private Spinner moodSpinner;
    private Spinner socialSettingSpinner;
    private EditText triggerEditText;
    private CheckBox locationCheckBox; // TODO: change type
    private Button browseButton;
    private ImageView photoImageView;
    private Button createButton;
    private Button cancelButton;
    private CreateMoodController createMoodController = new CreateMoodController();
    /**
     * Calls upon the methods to initialize the UI needed.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoImageView = (ImageView) findViewById(R.id.imageView);
        setContentView(R.layout.activity_create_mood);

        //List of permissions required and Requestcode for the permssions needed
        //in ActivityCompat.requestPermissions
        int permission_code = 1;
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        if(!hasPermissions(this, permissions)){
            ActivityCompat.requestPermissions(this, permissions, permission_code);
        }

        // set up mood and social setting spinners (drop downs)
        setUpSpinners();

        // set up events that happen when user clicks in trigger and outside trigger
        setUpTrigger();

        // set up events that happen when user clicks browse button
        setUpBrowse();

        // set up events that happen when user clicks location button
        setUpLocation();

        // set up events that happen when user clicks createMoodEvent button
        setUpCreate();

        // set up events that happen when user clicks cancel button
        setUpCancel();
    }
    //Taken from http://stackoverflow.com/questions/34342816/android-6-0-multiple-permissions
    //March 26, 2017

    /**
     * Method to detect whether or not permissions required for the app to run are granted. Upon
     * earlier versions of the SDK, permission is automatically granted
     * @return true if SDK < 23 or participant permits
     * @return false if participant denies and SDK > 23
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= 23 && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){

            //resume tasks needing this permission
        }
    }

    /**
     * Main method to call whenever the participant was to createMoodEvent their mood event after setting
     * their options. The system will obtain all of the information (null or not), and then save
     * the mood event into the participants own arrays of mood events. Depending on the options,
     * the participant will be able to save it as a picture, which will have required a prompt to
     * access the external storage.
     */
    public void createMoodEvent() {
        Log.d("Clicked on Create", "MoodEvent should be added here");
        Boolean isChecked = locationCheckBox.isChecked();
        String moodString = moodSpinner.getSelectedItem().toString();
        String socialSettingString = socialSettingSpinner.getSelectedItem().toString();
        String trigger = triggerEditText.getText().toString();

        //optional features that require a model initially set to null
        Photograph photo = null;
        MoodLocation location = null;

        boolean photoSizeUnder = TRUE;

        if (isChecked) {
            //TODO DO LOC STUFF, get current loc and make it location
            Location coords = new Location("GPS");
            coords = getCurrentLocation(coords);
            //set location as new MoodLocation as a Geopoint
            try {
                double lat = coords.getLatitude();
                double lon = coords.getLongitude();
                location = new MoodLocation(new GeoPoint(lat, lon));
            } catch (Exception e) {
                //pass
            }

        }


        //Taken from http://stackoverflow.com/questions/26865787/get-bitmap-from-imageview-in-android-l
        //March 10, 2017
        //gets drawable from imageview and converts drawable to bitmap
        try {
            Bitmap bitmap = ((BitmapDrawable) photoImageView.getDrawable()).getBitmap();
            photo = new Photograph(bitmap);
            photoSizeUnder = photo.getLimitSize();
        } catch (Exception e) {
            // pass
        }


        if (photoSizeUnder) {   // TODO photo size limit exception
            boolean thrown = false;
            try {
                CreateMoodController.createMoodEvent(moodString, socialSettingString, trigger, photo
                        , location, getApplicationContext());
            } catch (EmptyMoodException e) {
                thrown = true;
                Toast.makeText(CreateMoodActivity.this,
                        "Please specify a mood.",
                        Toast.LENGTH_LONG).show();
            } catch (TriggerTooLongException e) {
                thrown = true;
                Toast.makeText(CreateMoodActivity.this,
                        "Trigger has to be 3 words.",
                        Toast.LENGTH_LONG).show();
            }

            if (!thrown) {
                Intent intent = new Intent(CreateMoodActivity.this, MyProfileActivity.class);
                startActivity(intent);
            }

        } else {
            Toast.makeText(CreateMoodActivity.this,
                    "Photo size is too large! (Max 65536 bytes)",
                    Toast.LENGTH_LONG).show();
        }
    }

    public Location getCurrentLocation(Location coords) {
        //Taken from http://stackoverflow.com/questions/17584374/check-if-gps-and-or-mobile-network-location-is-enabled
        //March 27, 2017
        lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        //Create new Location object using provider
        Boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Boolean network = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        //GPS service gets FINE location
        //Network provider gets COARSE location
        if (gps) {
            //Ignore warnings, permissions checked when activity starts
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            if (lm != null) {
                coords = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }

        }
        if (!gps && network) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            if (lm!=null) {
                coords = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

        }
        //no GPS or network provider
        if (!gps && !network) {
            Toast.makeText(CreateMoodActivity.this,
                    "You are not connected to GPS or a network provider",
                    Toast.LENGTH_LONG).show();
        }
        return coords;
    }
    /**
     * Setup method to createMoodEvent the spinners in the UI. This includes creating the adapters necessary
     * for the spinners as well as the necessary settings and moods that will be contained in the
     * spinner.
     */
    void setUpSpinners() {
        // Spinner elements
        moodSpinner = (Spinner) findViewById(R.id.moodDropDown);
        socialSettingSpinner = (Spinner) findViewById(R.id.socialSettingDropDown);
        // Spinner drop down elements
        List<String> moodCategories = new ArrayList<String>();
        moodCategories.add("Select a mood");     // default option
        MoodState[] moodStates = MoodState.values();
        for (MoodState moodState : moodStates) {
            moodCategories.add(moodState.toString());
        }

        List<String> socialSettingCategories = new ArrayList<String>();
        socialSettingCategories.add("Select a social setting");    // default option
        SocialSetting[] socialSettings = SocialSetting.values();
        for (SocialSetting socialSetting : socialSettings) {
            socialSettingCategories.add(socialSetting.toString());
        }

        // Creating adapter for spinners
        ArrayAdapter<String> moodSpinnerAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, moodCategories);
        ArrayAdapter<String> socialSettingSpinnerAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, socialSettingCategories);

        // Drop down layout style - list view with radio button
        moodSpinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        socialSettingSpinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        // Attaching adapter to spinner
        moodSpinner.setAdapter(moodSpinnerAdapter);
        socialSettingSpinner.setAdapter(socialSettingSpinnerAdapter);
    }

    /**
     * Setup method for the trigger EditText category
     */
    void setUpTrigger() {

        triggerEditText = (EditText) findViewById(R.id.trigger);
        triggerEditText.setText("");
    }

    /**
     * Setup method for the location EditText (TEMPORARY) category
     */
    void setUpLocation() {
        locationCheckBox = (CheckBox) findViewById(R.id.includeLocation);

    }

    /**
     * Setup method the browse button, being able to select pictures from the phone storage.
     */
    void setUpBrowse() {
        // Taken from http://stackoverflow.com/questions/21072034/image-browse-button-in-android-activity
        // on 03-06-17
        browseButton = (Button) findViewById(R.id.browse);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    /**
     * Upon execution, the activity will be able to display the photo that the participant selected
     * so long the size is within limit.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    // displayed the browsed image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            photoImageView = (ImageView) findViewById(imageView);
            photoImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    /**
     * Setup method for the createMoodEvent button, which will issue a command to createMoodEvent the mood event on
     * click.
     */
    void setUpCreate() {
        createButton = (Button) findViewById(R.id.create);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMoodEvent();
            }
        });
    }

    /**
     * Setup method for the cancel button, which will issue a command to close the addition of a
     * mood event if the paticipant ever changes their mind.
     */
    void setUpCancel() {
        cancelButton = (Button) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    /**
     * Attempt to save the instance when the activity pauses
     */
    @Override
    protected void onPause() {
        super.onPause();
        FileManager.saveInFile(this);
    }

    /**
     * Attempt to save the instance when the activity stops running
     */
    @Override
    public void onStop() {
        super.onStop();
        FileManager.saveInFile(this);
    }

}
