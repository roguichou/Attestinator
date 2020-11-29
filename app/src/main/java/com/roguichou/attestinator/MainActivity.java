package com.roguichou.attestinator;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.roguichou.attestinator.attestation.AttestationPermanente;
import com.roguichou.attestinator.attestation.AttestationTemporaire;
import com.roguichou.attestinator.db.AttestinatorDatabase;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //clé requête de permission
    private static final int REQUEST_CODE_PERM = 0xC00;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location home = null;

    //clé de Préférences
    //attestations permanentes
    private static final String KEY_NB_ATT_PERM = "nb_att";
    private static final String KEY_ROOT_ATT_TYPE = "att_type_";
    private static final String KEY_ROOT_FILE_TYPE = "file_type_";
    private static final String KEY_ROOT_LABEL = "label_";


    private AttestinatorDatabase attestinatorDatabase;

    private AttestationTemporaire att_temp;

    private List<Profil> profils;
    private List<AttestationPermanente> attestations;

    private Logger log;

    protected MyApp mMyApp;

    private int screen_width;

    public static final String CHANNEL_ID = "SortieServiceChannel";


    private void createNotificationChannel() {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sortie Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navigationController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return navigationController.navigateUp();
    }

    public void setActionBarTitle(String title) {
        ActionBar app = getSupportActionBar();
        if(null != app) {
            app.setTitle(title);
        }
    }

    public FusedLocationProviderClient getFusedLocationClient(){
        return fusedLocationClient;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyApp = (MyApp)this.getApplicationContext();
        mMyApp.setCurrentActivity(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavController navigationController  = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navigationController);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        log = new Logger(this, Logger.LOG_LEVEL_INFO);


        WindowManager mgr= getWindowManager();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowMetrics metrics = mgr.getCurrentWindowMetrics();
            Rect ecran = metrics.getBounds();
            screen_width = ecran.width();
        }
        else
        {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            screen_width = metrics.widthPixels;
        }

        initiateSettings();

        PDFBoxResourceLoader.init(getApplicationContext());

        createNotificationChannel();


        //permissions
        ArrayList<String> params = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            params.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            params.add(Manifest.permission.CAMERA);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            params.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }

        if(params.size()>0) {
            String [] perms = new String[params.size()];
            for(int i=0;i<params.size();i++)
            {
                perms[i] = params.get(i);
            }

            ActivityCompat.requestPermissions(this, perms, REQUEST_CODE_PERM);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }





    private void demarrerService()
    {
        ContextCompat.startForegroundService(this,
                new Intent(this, SortieService.class));
    }

    public void debuterSortie(View view) {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);


        locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        home = location;
                        Log.d("ATTESTINATOR", "home:"+home.toString());
                        demarrerService();
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                    }
                }
            }
        };

        try
        {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
        catch (SecurityException e)
        {
            Snackbar mySnackbar = Snackbar.make(view, "Erreur: pas d'autorisation de localisation", Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }
    }

    //settings
    private void initiateSettings()
    {
        //Préférences
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);

        attestinatorDatabase = AttestinatorDatabase.getInstance(MainActivity.this);

        profils = attestinatorDatabase.getAttestinatorDao().getProfils();

        att_temp = new AttestationTemporaire(this,
                (profils.size()>0)?profils.get(0):null,
                screen_width);
        att_temp.parseSettings(settings);

        //legacy
        int nb_att = settings.getInt(KEY_NB_ATT_PERM, 0);

        //migration legacy
        if (nb_att >0) {
            SharedPreferences.Editor editor;
            editor = settings.edit();
            editor.remove(KEY_NB_ATT_PERM);
            editor.apply();

            for (int i = 0; i < nb_att; i++) {
                int att_type = settings.getInt(KEY_ROOT_ATT_TYPE + i, 0);
                int file_type = settings.getInt(KEY_ROOT_FILE_TYPE + i, 0);
                String label = settings.getString(KEY_ROOT_LABEL + i, null);
                AttestationPermanente attestation = new AttestationPermanente(att_type, file_type, label);
                attestinatorDatabase.getAttestinatorDao().insert(attestation);
            }
        }

        attestations = attestinatorDatabase.getAttestinatorDao().getAttestationsPermanentes();

    }

    public AttestationTemporaire getAttestationTemporaire(){return att_temp;}

    public List<AttestationPermanente> getPermanentAttestations() {
        return attestations;
    }

    void addAttestation(AttestationPermanente att)
    {
        attestations.add(att);
        attestinatorDatabase.getAttestinatorDao().insert(att);
    }

    void removeAttestation(AttestationPermanente att)
    {
        attestations.remove(att);

        File f = new File(getFilesDir()+"/"+att.getFilename());
        f.delete();

        attestinatorDatabase.getAttestinatorDao().delete(att);
    }

    public Logger getLog(){return log;}

    public List<Profil> getProfils() {return profils;}

    public Location getHome(){
        return home;
    }


    public void deleteProfil(Profil profil) {
        profils.remove(profil);
        attestinatorDatabase.getAttestinatorDao().delete(profil);
    }

    public void updateProfil(Profil profil) {
        attestinatorDatabase.getAttestinatorDao().update(profil);
    }

    public void addProfil(Profil profil) {
        profils.add(profil);
        attestinatorDatabase.getAttestinatorDao().insert(profil);
    }
}
