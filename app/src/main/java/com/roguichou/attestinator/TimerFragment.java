package com.roguichou.attestinator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;

public class TimerFragment extends Fragment {

    private View fragmentView = null;
    private Calendar heure_sortie = null;
    private Location home = null;

    private Handler handler = null;
    private Runnable runnableCode = null;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public TimerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentView = view;
        heure_sortie = ((MainActivity) getActivity()).getHeureSortie();
        home = ((MainActivity) getActivity()).getHome();

        view.findViewById(R.id.button_prolonger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heure_sortie.add(Calendar.MINUTE, 30);
                ((MainActivity) getActivity()).genererAttestation(fragmentView, Raison.SPORT_ANIMAUX, heure_sortie.get(Calendar.HOUR_OF_DAY), heure_sortie.get(Calendar.MINUTE));
            }
        });

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        if (null== home)
                        {
                            home = ((MainActivity) getActivity()).getHome();
                        }
                        int dist = (int) location.distanceTo(home);
                          Log.d("ATTESTINATOR", "home:"+home.toString()+"loc:" + location.toString() + " dist=" + dist);
                          ((TextView) fragmentView.findViewById(R.id.dist_txt)).setText(String.format("%d m", dist));

                        if (dist > 1000) {
                            ((TextView) fragmentView.findViewById(R.id.dist_txt)).setTextColor(Color.RED);
                        } else {
                            ((TextView) fragmentView.findViewById(R.id.dist_txt)).setTextColor(Color.LTGRAY);
                        }
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {

            ((MainActivity) getActivity()).getFusedLocationClient().requestLocationUpdates(locationRequest, locationCallback, null);
        }



        // Create the Handler object (on the main thread by default)
        handler = new Handler(Looper.getMainLooper());

        // Define the code block to be executed
        runnableCode = new Runnable() {
            @Override
            public void run() {

                // Do something here on the main thread
                Calendar maintenant = Calendar.getInstance();

                long delta = (maintenant.getTimeInMillis() - heure_sortie.getTimeInMillis()) / 1000;
                delta = 60 * 60 - delta;
                int min = (int) (delta / 60);
                int sec = (int) (delta - min * 60);

                ((TextView) fragmentView.findViewById(R.id.timer_txt)).setText(String.format("%02d:%02d", min, sec));

                if (delta < 5 * 60) {
                    ((TextView) fragmentView.findViewById(R.id.timer_txt)).setTextColor(Color.RED);
                } else {
                    ((TextView) fragmentView.findViewById(R.id.timer_txt)).setTextColor(Color.LTGRAY);
                }

                // Repeat this the same runnable code block again another 1 seconds
                handler.postDelayed(runnableCode, 1000);
            }
        };
        // Start the initial runnable task by posting through the handler
        handler.post(runnableCode);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnableCode);
    }
}