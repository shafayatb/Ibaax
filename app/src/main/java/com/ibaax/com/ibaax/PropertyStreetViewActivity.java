package com.ibaax.com.ibaax;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

public class PropertyStreetViewActivity extends AppCompatActivity {

    private static LatLng LOCATION;
    double Latitude, Longitude;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_street_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Street View");
        Latitude = getIntent().getDoubleExtra("Latitude", 0);
        Longitude = getIntent().getDoubleExtra("Longitude", 0);

        LOCATION = new LatLng(Latitude, Longitude);
        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(final StreetViewPanorama panorama) {

                        if (savedInstanceState == null) {
                           // if (loc != null && loc.links != null) {
                                //panorama.setPosition(LOCATION);
                            /*} else {
                                Toast.makeText(PropertyStreetViewActivity.this, "No Street View Available For This Property", Toast.LENGTH_LONG)
                                        .show();
                                finish();
                            }*/
                            panorama.setPosition(LOCATION);
                            panorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
                                @Override
                                public void onStreetViewPanoramaChange(StreetViewPanoramaLocation loc) {
                                    if (loc != null && loc.links != null) {
                                        //panorama.setPosition(LOCATION);
                                    }else{
                                        Toast.makeText(PropertyStreetViewActivity.this, "No Street View Available For This Property", Toast.LENGTH_LONG)
                                                .show();
                                        finish();
                                    }
                                }
                            });

                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
