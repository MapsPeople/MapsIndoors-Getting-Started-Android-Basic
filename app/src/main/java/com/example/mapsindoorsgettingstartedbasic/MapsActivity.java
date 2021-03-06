package com.example.mapsindoorsgettingstartedbasic;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.mapsindoors.mapssdk.MPDirectionsRenderer;
import com.mapsindoors.mapssdk.MPRoutingProvider;
import com.mapsindoors.mapssdk.MapControl;
import com.mapsindoors.mapssdk.Point;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapControl mMapControl;
    private View mMapView;
    private ImageButton mSearchBtn;
    private TextInputEditText mSearchTxtField;
    private MPRoutingProvider mpRoutingProvider;
    private MPDirectionsRenderer mpDirectionsRenderer;
    private Point mUserLocation = new Point(38.897389429704695, -77.03740973527613,0);
    private NavigationFragment mNavigationFragment;
    private SearchFragment mSearchFragment;
    private Fragment mCurrentFragment;
    private BottomSheetBehavior<FrameLayout> mBtmnSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSearchBtn = findViewById(R.id.search_btn);
        mSearchTxtField = findViewById(R.id.search_edit_txt);
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);


        //ClickListener to start a search, when the user clicks the search button
        mSearchBtn.setOnClickListener(view -> {
            if (mSearchTxtField.getText().length() != 0) {
                //TODO: Call the search method when you have created it following the tutorial
                //Making sure keyboard is closed.
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });


        //Listener for when the user searches through the keyboard
        mSearchTxtField.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_SEARCH) {
                if (textView.getText().length() != 0) {
                    //There is text inside the search field. So lets do the search.
                    //TODO: Call the search method when you have created it following the tutorial
                }
                //Making sure keyboard is closed.
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        FrameLayout bottomSheet = findViewById(R.id.standardBottomSheet);
        mBtmnSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBtmnSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    if (mCurrentFragment != null) {
                        if (mCurrentFragment instanceof NavigationFragment) {
                            //Clears the direction view if the navigation fragment is closed.
                            if (mpDirectionsRenderer != null) {
                                mpDirectionsRenderer.clear();
                            }
                        }
                        //Clears the map if any searches has been done.
                        if (mMapControl != null) {
                            mMapControl.clearMap();
                        }
                        //Removes the current fragment from the BottomSheet.
                        removeFragmentFromBottomSheet(mCurrentFragment);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMapView != null) {
            //TODO: Init MapControl here
        }
    }

    //TODO: Implement methods when described in the tutorial.

    void addFragmentToBottomSheet(Fragment newFragment) {
        if (mCurrentFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(mCurrentFragment).commit();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.standardBottomSheet, newFragment).commit();
        mCurrentFragment = newFragment;
        //Set the map padding to the height of the bottom sheets peek height. To not obfuscate the google logo.
        runOnUiThread(()-> {
            mMapControl.setMapPadding(0, 0,0, mBtmnSheetBehavior.getPeekHeight());
            if (mBtmnSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                mBtmnSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    void removeFragmentFromBottomSheet(Fragment fragment) {
        if (mCurrentFragment.equals(fragment)) {
            mCurrentFragment = null;
        }
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        runOnUiThread(()-> {
            mMapControl.setMapPadding(0,0,0,0);
        });
    }
}