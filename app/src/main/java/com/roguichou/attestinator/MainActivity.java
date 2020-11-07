package com.roguichou.attestinator;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.zxing.WriterException;
import com.tom_roush.pdfbox.io.RandomAccessBufferedFileInputStream;
import com.tom_roush.pdfbox.pdfparser.PDFParser;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.util.Matrix;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Display;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;
import android.view.WindowManager;
import android.view.WindowMetrics;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_NAME = "nom";
    private static final String KEY_FIRST_NAME = "prenom";
    private static final String KEY_BIRTH_DATE = "date_naiss";
    private static final String KEY_BIRTH_LOCATION = "lieu_naiss";
    private static final String KEY_ADD = "addresse";
    private static final String KEY_POST_CODE = "code_postal";
    private static final String KEY_CITY = "ville";
    private static final String KEY_RAISON = "raison";
    private static final String KEY_H = "heure";
    private static final String KEY_MIN = "min";

    private static final int REQUEST_CODE_PERM_LOC = 0x753;

    private String profil_name = null;
    private String profil_first_name = null;
    private String profil_birth_date = null;
    private String profil_birth_location = null;
    private String profil_address = null;
    private String profil_post_code = null;
    private String profil_city = null;


    private SharedPreferences settings;

    protected MyApp mMyApp;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private Calendar heureSortie = null;
    private Location home = null;

    private Raison raison = null;
    Bitmap qrBitmap = null;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyApp = (MyApp)this.getApplicationContext();
        mMyApp.setCurrentActivity(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        settings = getPreferences(Context.MODE_PRIVATE);
        profil_name = settings.getString(KEY_NAME, null);
        profil_first_name = settings.getString(KEY_FIRST_NAME, null);
        profil_birth_date = settings.getString(KEY_BIRTH_DATE, null);
        profil_birth_location = settings.getString(KEY_BIRTH_LOCATION, null);
        profil_address = settings.getString(KEY_ADD, null);
        profil_post_code = settings.getString(KEY_POST_CODE, null);
        profil_city = settings.getString(KEY_CITY, null);


        String raison_s = settings.getString(KEY_RAISON, null);
        if(null!= raison_s) {
            raison = Raison.fromString(raison_s);

            int h = settings.getInt(KEY_H, 0);
            int min = settings.getInt(KEY_MIN, 0);
            heureSortie = Calendar.getInstance();
            heureSortie.set(Calendar.HOUR_OF_DAY, h);
            heureSortie.set(Calendar.MINUTE, min);


            genererQRcode(null);
        }
        PDFBoxResourceLoader.init(getApplicationContext());

        createNotificationChannel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isProfilFull()
    {
        return (null!=profil_name &&
                null!=profil_first_name &&
                null!=profil_birth_date &&
                null!=profil_birth_location &&
                null!=profil_address &&
                null!=profil_post_code &&
                null!=profil_city );

    }


    public FusedLocationProviderClient getFusedLocationClient(){
        return fusedLocationClient;
    }

    public String getProfilName() {
        return profil_name;
    }

    public String getProfilFirstName() {
        return profil_first_name;
    }

    public String getProfilBirthDate() {
        return profil_birth_date;
    }

    public String getProfilBirthLocation() {
        return profil_birth_location;
    }

    public String getProfilAddress() {
        return profil_address;
    }

    public String getProfilPostCode() {
        return profil_post_code;
    }

    public String getProfilCity() {
        return profil_city;
    }


    public Calendar getHeureSortie() {
        return heureSortie;
    }
    public Location getHome(){
        return home;
    }

    public void setProfilName(String _val) {
        profil_name = _val;
    }

    public void setProfilFirstName(String _val) {
        profil_first_name = _val;
    }

    public void setProfilBirthDate(String _val) {
        profil_birth_date = _val;
    }

    public void setProfilBirthLocation(String _val) {
        profil_birth_location = _val;
    }

    public void setProfilAddress(String _val) {
        profil_address = _val;
    }

    public void setProfilPostCode(String _val) {
        profil_post_code = _val;
    }

    public void setProfilCity(String _val) {
        profil_city = _val;
    }

    public Bitmap getQrBitmap() {
        return qrBitmap;
    }

    public void saveProfil() {
        SharedPreferences.Editor editor;

        editor = settings.edit();
        editor.putString(KEY_NAME, profil_name);
        editor.putString(KEY_FIRST_NAME, profil_first_name);
        editor.putString(KEY_BIRTH_DATE, profil_birth_date);
        editor.putString(KEY_BIRTH_LOCATION, profil_birth_location);
        editor.putString(KEY_ADD, profil_address);
        editor.putString(KEY_POST_CODE, profil_post_code);
        editor.putString(KEY_CITY, profil_city);

        editor.apply();
    }

    private String getTimeAsString()
    {
        return String.format(Locale.FRENCH, "%02d:%02d",  heureSortie.get(Calendar.HOUR_OF_DAY), heureSortie.get(Calendar.MINUTE));
    }


    private void genererQRcode(View view)
    {
        SimpleDateFormat frmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
        String today = frmt.format(heureSortie.getTime());
        String data = "Cree le: " + today + " a " + getTimeAsString() + ";\n" +
                "Nom: " + profil_name + "\n" + "Prenom: " + profil_first_name + ";\n" +
                "Naissance: " + profil_birth_date + " a " + profil_birth_location + ";\n" +
                "Adresse: " + profil_address + " " + profil_post_code + " " + profil_city + ";\n" +
                "Sortie: " + today + " a " + getTimeAsString() + ";\n" +
                "Motifs: " + raison.toString()+";\n";

        WindowManager mgr= getWindowManager();
        WindowMetrics metrics = mgr.getCurrentWindowMetrics();
        Rect ecran = metrics.getBounds();
        QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, ecran.width());

        try {
            qrBitmap = qrgEncoder.encodeAsBitmap();
        } catch (WriterException e) {
            if(view!= null) {
                Snackbar mySnackbar = Snackbar.make(view, "Erreur à la génération de l'attestation", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
            e.printStackTrace();
        }


        if(view!= null)
        {
            Snackbar mySnackbar = Snackbar.make(view, "Attestation générée", Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }
    }

    private void genererPDF(View view) {
        InputStream input;
        PDDocument doc;
        try {
            input = getResources().openRawResource(R.raw.certificate);
            RandomAccessBufferedFileInputStream istream = new RandomAccessBufferedFileInputStream(input);
            PDFParser parser = new PDFParser(istream);
            parser.parse();
            doc = parser.getPDDocument();
        } catch (Exception e) {
            Snackbar mySnackbar = Snackbar.make(view, "Erreur à l'ouverture du modèle " + e.toString(), Snackbar.LENGTH_SHORT);
            mySnackbar.show();
            return;
        }

        PDFont font = PDType1Font.HELVETICA;
        float fontSize = 11.0f;

        PDPage page = doc.getPage(0);

        try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, true, true)) {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.setTextMatrix(Matrix.getTranslateInstance(119, 696));
            contentStream.showText(profil_name + " " + profil_first_name);

            contentStream.setTextMatrix(Matrix.getTranslateInstance(119, 674));
            contentStream.showText(profil_birth_date);

            contentStream.setTextMatrix(Matrix.getTranslateInstance(297, 674));
            contentStream.showText(profil_birth_location);

            contentStream.setTextMatrix(Matrix.getTranslateInstance(133, 652));
            contentStream.showText(profil_address + " " + profil_post_code + " " + profil_city);

            contentStream.setTextMatrix(Matrix.getTranslateInstance(105, 177));
            contentStream.showText(profil_city);

            contentStream.setTextMatrix(Matrix.getTranslateInstance(91, 153));
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat frmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
            String today = frmt.format(calendar.getTime());
            contentStream.showText(today);

            contentStream.setTextMatrix(Matrix.getTranslateInstance(264, 153));
            contentStream.showText(getTimeAsString());


            contentStream.setFont(font, 18.0f);
            contentStream.setTextMatrix(Matrix.getTranslateInstance(78, raison.getPosition_y()));
            contentStream.showText("x");
            contentStream.endText();

            String data = "Cree le: " + today + " a " + getTimeAsString() + "\n" +
                    "Nom: " + profil_name + "\n" + "Prenom: " + profil_first_name + "\n" +
                    "Naissance: " + profil_birth_date + " a " + profil_birth_location + "\n" +
                    "Adresse: " + profil_address + " " + profil_post_code + " " + profil_city + "\n" +
                    "Sortie: " + today + " a " + getTimeAsString() + "\n" +
                    "Motifs: " + raison.toString();

            QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, 96);
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            PDImageXObject image = LosslessFactory.createFromImage(doc, bitmap);
            PDRectangle pageSize = page.getMediaBox();
            contentStream.drawImage(image, pageSize.getWidth() - 156, 100);
            contentStream.close();

            QRGEncoder qrgEncoder2 = new QRGEncoder(data, null, QRGContents.Type.TEXT, 300);
            Bitmap bitmap2 = qrgEncoder2.encodeAsBitmap();
            PDImageXObject image2 = LosslessFactory.createFromImage(doc, bitmap2);

            doc.addPage(new PDPage(PDRectangle.A4));
            PDPage page2 = doc.getPage(1);
            PDPageContentStream contentStream2 = new PDPageContentStream(doc, page2, false, true, true);
            pageSize = page2.getMediaBox();
            contentStream2.drawImage(image2, 50, pageSize.getHeight() - 350);
            contentStream2.close();
        } catch (Exception e) {
            Snackbar mySnackbar = Snackbar.make(view, "Erreur à l'édition du PDF " + e.toString(), Snackbar.LENGTH_SHORT);
            Log.e("Atestation-inator", e.toString());
            mySnackbar.show();
            return;
        }


        try (FileOutputStream fos = getApplicationContext().openFileOutput("attestation.pdf", Context.MODE_PRIVATE)) {
            doc.save(fos);
        } catch (Exception e) {
            Snackbar mySnackbar = Snackbar.make(view, "Erreur à l'écriture du PDF " + e.toString(), Snackbar.LENGTH_SHORT);
            mySnackbar.show();
            return;
        }

        try {
            if (null != input) {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Snackbar mySnackbar = Snackbar.make(view, "Attestation générée", Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }

    public void genererAttestation(View view, Raison raison, int h, int min) {
        this.raison= raison;
        heureSortie = Calendar.getInstance();
        heureSortie.set(Calendar.HOUR_OF_DAY, h);
        heureSortie.set(Calendar.MINUTE, min);

        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString(KEY_RAISON, raison.toString());
        editor.putInt(KEY_H, h);
        editor.putInt(KEY_MIN, min);
        editor.apply();

        genererQRcode(view);
        genererPDF(view);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        /*@param requestCode The request code passed in {@link #requestPermissions(
         * android.app.Activity, String[], int)}
         * @param permissions The requested permissions. Never null.
         * @param grantResults The grant results for the corresponding permissions
         *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
         *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            home = location;
                            fusedLocationClient.removeLocationUpdates(locationCallback);
                            demarrerService();
                        }
                    }
                }
            };
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void demarrerService()
    {
        Intent serviceIntent = new Intent(this, SortieService.class);
 //       serviceIntent.putExtra("heure", heureSortie);
 //       serviceIntent.putExtra("home", home);
  //      serviceIntent.putExtra("activty", this);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void debuterSortie(int h, int min) {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERM_LOC);
        }
        else
        {

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
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

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }
}
