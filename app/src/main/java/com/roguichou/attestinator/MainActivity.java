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
import com.roguichou.attestinator.attestation.Raison;
import com.roguichou.attestinator.db.AttestinatorDatabase;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //clé requête de permission
    private static final int REQUEST_CODE_PERM = 0xC00;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location home = null;

    private AttestinatorDatabase attestinatorDatabase;

    private List<Profil> profils;
    private List<AttestationPermanente> attestations;
    private List<AttestationTemporaire> attTemps;

    private Logger log;

    protected MyApp mMyApp;

    private int screenWidth;

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
            screenWidth = ecran.width();
        }
        else
        {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            screenWidth = metrics.widthPixels;
        }

        attestinatorDatabase = AttestinatorDatabase.getInstance(MainActivity.this);

        profils = attestinatorDatabase.getAttestinatorDao().getProfils();

        attTemps = attestinatorDatabase.getAttestinatorDao().getAttestationsTemporaires();
        Collections.reverse(attTemps);

        //clear old attestations
        clearOldTempAtt();
        //handle legacy
        handleLegacy();

        attestations = attestinatorDatabase.getAttestinatorDao().getAttestationsPermanentes();

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


    private void demarrerService(Profil profil, int h, int min)
    {
        Intent StartServiceIntent = new Intent(this, SortieService.class);
        Calendar heureSortie = Calendar.getInstance();
        heureSortie.set(Calendar.HOUR_OF_DAY,h);
        heureSortie.set(Calendar.MINUTE, min);
        StartServiceIntent.putExtra("heureSortie", heureSortie);
        StartServiceIntent.putExtra("Profil", profil);
        ContextCompat.startForegroundService(this, StartServiceIntent);
    }

    public void debuterSortie(View view, Profil profil, int h, int min) {

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
                        demarrerService(profil, h, min);
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

    public void prolongerSortie (Profil profil, Calendar heureSortie)
    {
        if(null == profil)
        {
            Iterator<Profil> profilIt = profils.iterator();
            while (profilIt.hasNext()) {
                profil = profilIt.next();
                AttestationTemporaire att = new AttestationTemporaire(this, null,
                        screenWidth, profil, Raison.SPORT_ANIMAUX,
                        heureSortie.get(Calendar.HOUR_OF_DAY), heureSortie.get(Calendar.MINUTE));

                addTempAtt(att);
            }
        }
        else {
            AttestationTemporaire att = new AttestationTemporaire(this, null,
                    screenWidth, profil, Raison.SPORT_ANIMAUX,
                    heureSortie.get(Calendar.HOUR_OF_DAY), heureSortie.get(Calendar.MINUTE));

            addTempAtt(att);
        }
        demarrerService(profil, heureSortie.get(Calendar.HOUR_OF_DAY), heureSortie.get(Calendar.MINUTE));
    }


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

    public Location getHome(){
        return home;
    }

    public int getScreenWidth() {return screenWidth; }

    public List<Profil> getProfils() {return profils;}

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

    public void addTempAtt(AttestationTemporaire att) {
        attTemps.add(0,att);
        attestinatorDatabase.getAttestinatorDao().insert(att);
    }

    public List<AttestationTemporaire> getTemporaireAttestations() {
        return attTemps;
    }

     //clear old attestations
    private void clearOldTempAtt()
    {
        Calendar maintenant = Calendar.getInstance();
        long ts = maintenant.getTimeInMillis();
        Iterator<AttestationTemporaire> attIterator = attTemps.iterator();
        while (attIterator.hasNext()) {
            AttestationTemporaire att = attIterator.next();
            long attTs = att.getHeureSortie().getTimeInMillis();
            if (ts-attTs > Constants.OBSO_ATT)
            {
                attIterator.remove();

                File f = new File(getFilesDir()+"/"+att.getFilename());
                f.delete();

                attestinatorDatabase.getAttestinatorDao().delete(att);
            }
            else
            {
                att.setSettings(this, screenWidth);
            }
        }
    }

    //handle legacy
    private void handleLegacy()
    {
        //Préférences
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);

        //clé de Préférences
        //attestations permanentes
        final String KEY_NB_ATT_PERM = "nb_att";
        final String KEY_ROOT_ATT_TYPE = "att_type_";
        final String KEY_ROOT_FILE_TYPE = "file_type_";
        final String KEY_ROOT_LABEL = "label_";

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

        final String KEY_NAME = "nom";
        final String KEY_FIRST_NAME = "prenom";
        final String KEY_BIRTH_DATE = "date_naiss";
        final String KEY_BIRTH_LOCATION = "lieu_naiss";
        final String KEY_ADD = "addresse";
        final String KEY_POST_CODE = "code_postal";
        final String KEY_CITY = "ville";

        String legacyProfilFirstName = settings.getString(KEY_FIRST_NAME, null);
        if(null != legacyProfilFirstName) {
            Profil legacyProfil = new Profil();
            legacyProfil.setLabel(legacyProfilFirstName);
            legacyProfil.setName(settings.getString(KEY_NAME, null));
            legacyProfil.setFirstName(legacyProfilFirstName);
            legacyProfil.setBirthDate(settings.getString(KEY_BIRTH_DATE, null));
            legacyProfil.setBirthLocation(settings.getString(KEY_BIRTH_LOCATION, null));
            legacyProfil.setAddress(settings.getString(KEY_ADD, null));
            legacyProfil.setPostCode(settings.getString(KEY_POST_CODE, null));
            legacyProfil.setCity(settings.getString(KEY_CITY, null));
            addProfil(legacyProfil);

            SharedPreferences.Editor editor;
            editor = settings.edit();
            editor.remove(KEY_NAME);
            editor.remove(KEY_FIRST_NAME);
            editor.remove(KEY_BIRTH_DATE);
            editor.remove(KEY_BIRTH_LOCATION);
            editor.remove(KEY_ADD);
            editor.remove(KEY_POST_CODE);
            editor.remove(KEY_CITY);
            editor.apply();
        }
    }
}

