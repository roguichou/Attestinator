package com.roguichou.attestinator.attestation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.View;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.WriterException;
import com.roguichou.attestinator.Constants;
import com.roguichou.attestinator.Profil;
import com.roguichou.attestinator.R;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

@Entity(tableName = Constants.TMP_TABLE_NAME)
public class AttestationTemporaire extends Attestation {

    //taille du texte
    private static final float PDF_FONT_SZ = 11f;
    private static final float PDF_REASON_SZ = 12f;

    //position Nom Prénom
    private static final Point PDF_IDENTITY_POS = new Point (92, 702);

    //Position date de naissance
    private static final Point PDF_BIRTHDAY_POS = new Point (92, 684);

    //Position lieu de naissance
    private static final Point PDF_BIRTHPLACE_POS = new Point (214, 684);

    //adresse
    private static final Point PDF_ADDRESS_POS = new Point (104, 665);

    //position en X des croix
    private static final int PDF_REASON_X = 47;

    //fait à
    private static final Point PDF_LOCATION_POS = new Point (83, 76);

    //date de sortie
    private static final Point PDF_DATE_POS = new Point (63, 58);

    //heure de sortie
    private static final Point PDF_HEURE_POS = new Point (227, 58);


    private Calendar heureSortie = null;

    private Raison raison = null;

    @Embedded
    private Profil profil;

    @Ignore
    private Bitmap qrBitmap = null;

    @Ignore
    private Context context =null;

    @Ignore
    private int qr_size=0;



    //constructeur via IHM
    public AttestationTemporaire(Context context, View view, int qr_size, Profil profil, Raison raison, int h, int min)
    {
        this.qr_size = qr_size;
        this.context = context;
        this.profil = profil;
        this.raison= raison;
        heureSortie = Calendar.getInstance();
        heureSortie.set(Calendar.HOUR_OF_DAY, h);
        heureSortie.set(Calendar.MINUTE, min);
        fileType = FILE_TYPE_PDF;

        String date = String.format(Locale.FRENCH, "%04d_%02d_%02d_%02d_%02d",
                heureSortie.get(Calendar.YEAR), heureSortie.get(Calendar.MONTH),
                heureSortie.get(Calendar.DAY_OF_MONTH),
                heureSortie.get(Calendar.HOUR_OF_DAY), heureSortie.get(Calendar.MINUTE));

        filename = "att_"+profil.getLabel()+"_"+date+".pdf";



        try {
            genererPDF();
            qrBitmap = genererQRcode(qr_size);
            if(null!= view) {
                Snackbar mySnackbar = Snackbar.make(view, "Attestation générée", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        }
        catch (Exception e)
        {
            if (null != view) {
                Snackbar mySnackbar = Snackbar.make(view, "Erreur à la génération d'attestation", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        }
    }


    //constructeur via BDD
    public AttestationTemporaire() {
        fileType = FILE_TYPE_PDF;
    }


    public void setSettings(Context context, int qr_size)
    {
        this.context = context;
        this.qr_size = qr_size;
        regenererQR();
    }

    public void setHeureSortie(Calendar heureSortie) {
        this.heureSortie = heureSortie;
        regenererQR();
    }

    public void setProfil(Profil profil) {
        this.profil = profil;
        regenererQR();
    }

    public void setRaison(Raison raison) {
        this.raison = raison;
        regenererQR();
    }

    private void regenererQR()
    {
        boolean isValid =
                heureSortie != null &&
                profil != null &&
                raison != null &&
                context != null &&
                qr_size != 0;
        if(isValid)
        {
            try {
                qrBitmap = genererQRcode(qr_size);
            }
            catch (Exception e)
            {
                Log.d("Attestinator","Erreur QR Code");
            }
        }

    }

    public Bitmap getQrBitmap() {
        return qrBitmap;
    }

    public Calendar getHeureSortie() {
        return heureSortie;
    }

    public Raison getRaison() { return raison; }

    public Profil getProfil() { return profil; }










    private String getTimeAsString()
    {
        return String.format(Locale.FRENCH, "%02d:%02d",  heureSortie.get(Calendar.HOUR_OF_DAY), heureSortie.get(Calendar.MINUTE));
    }

    private Bitmap genererQRcode(int sz) throws WriterException {
        Bitmap bmp;
        SimpleDateFormat frmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
        String today = frmt.format(heureSortie.getTime());
        String data = "Cree le: " + today + " a " + getTimeAsString() + ";\n" +
                "Nom: " + profil.getName() + "\n" + "Prenom: " + profil.getFirstName() + ";\n" +
                "Naissance: " + profil.getBirthDate() + " a " + profil.getBirthLocation() + ";\n" +
                "Adresse: " + profil.getAddress() + " " + profil.getPostCode() + " " + profil.getCity() + ";\n" +
                "Sortie: " + today + " a " + getTimeAsString() + ";\n" +
                "Motifs: " + raison.toString()+";\n";


        QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, sz);

        bmp = qrgEncoder.encodeAsBitmap();

        return bmp;
    }

    private void genererPDF() throws IOException, WriterException {
        InputStream input;
        PDDocument doc;

        input = context.getResources().openRawResource(R.raw.certificate);
        RandomAccessBufferedFileInputStream istream = new RandomAccessBufferedFileInputStream(input);
        PDFParser parser = new PDFParser(istream);
        parser.parse();
        doc = parser.getPDDocument();

        PDFont font = PDType1Font.HELVETICA;

        PDPage page = doc.getPage(0);

        PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, true, true);
        contentStream.beginText();
        contentStream.setFont(font, PDF_FONT_SZ);
        contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_IDENTITY_POS.x, PDF_IDENTITY_POS.y));
        contentStream.showText(profil.getName() + " " + profil.getFirstName());

        contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_BIRTHDAY_POS.x, PDF_BIRTHDAY_POS.y));
        contentStream.showText(profil.getBirthDate());

        contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_BIRTHPLACE_POS.x, PDF_BIRTHPLACE_POS.y));
        contentStream.showText(profil.getBirthLocation());

        contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_ADDRESS_POS.x, PDF_ADDRESS_POS.y));
        contentStream.showText(profil.getAddress() + " " + profil.getPostCode() + " " + profil.getCity());

        contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_LOCATION_POS.x, PDF_LOCATION_POS.y));
        contentStream.showText(profil.getCity());

        contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_DATE_POS.x, PDF_DATE_POS.y));
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat frmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
        String today = frmt.format(calendar.getTime());
        contentStream.showText(today);

        contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_HEURE_POS.x, PDF_HEURE_POS.y));
        contentStream.showText(getTimeAsString());


        contentStream.setFont(font, PDF_REASON_SZ);
        contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_REASON_X, raison.getPosition_y()));
        contentStream.showText("x");
        contentStream.endText();

        Bitmap bitmap = genererQRcode(150);
        PDImageXObject image = LosslessFactory.createFromImage(doc, bitmap);
        PDRectangle pageSize = page.getMediaBox();
        contentStream.drawImage(image, pageSize.getWidth() - 156, 25);
        contentStream.close();

        Bitmap bitmap2  = genererQRcode(300);
        PDImageXObject image2 = LosslessFactory.createFromImage(doc, bitmap2);

        doc.addPage(new PDPage(PDRectangle.A4));
        PDPage page2 = doc.getPage(1);
        PDPageContentStream contentStream2 = new PDPageContentStream(doc, page2, false, true, true);
        pageSize = page2.getMediaBox();
        contentStream2.drawImage(image2, 50, pageSize.getHeight() - 350);
        contentStream2.close();


        FileOutputStream fos = context.getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
        doc.save(fos);
        doc.close();
        input.close();
    }

}
