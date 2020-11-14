package com.roguichou.attestinator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import android.graphics.pdf.PdfRenderer;

import java.io.File;


public class AfficherFragment extends Fragment {

    ImageView image;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_afficher, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity)getActivity()).setActionBarTitle("Attestation");

        image = view.findViewById(R.id.pdf_renderer);

        String fichier = getArguments().getString("fichier");
        int type = getArguments().getInt("type");

        switch (type)
        {
            case 0xFF:
                view.setBackgroundColor(Color.BLACK);
                image.setImageBitmap(((MainActivity)getActivity()).getQrBitmap());
                break;

            case AttestationPermanente.FILE_TYPE_PDF:
                view.setBackgroundColor(Color.WHITE);
                try {
                    File attestation = new File(getActivity().getFilesDir() + "/" + fichier);
                    ParcelFileDescriptor fd =	ParcelFileDescriptor.open(attestation,ParcelFileDescriptor.MODE_READ_ONLY);
                    PdfRenderer renderer =new PdfRenderer(fd);
                    PdfRenderer.Page page = renderer.openPage(0);
                    Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    image.setImageBitmap(bitmap);
                    page.close();
                    fd.close();
                } catch (Exception e) {
                    Snackbar mySnackbar = Snackbar.make(view, "Erreur à l'ouverture du fichier " + e.toString(), Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
                break;

            case AttestationPermanente.FILE_TYPE_JPG:
                view.setBackgroundColor(Color.BLACK);

                File attestation = new File(getActivity().getFilesDir() + "/" + fichier);

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(attestation.getAbsolutePath(),bmOptions);
                //bitmap = Bitmap.createScaledBitmap(bitmap, view.getWidth(),view.getHeight(),true);

                image.setImageBitmap(bitmap);
                break;

            default:
                Snackbar mySnackbar = Snackbar.make(view, "Type iconnu ???", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
                break;
        }


    }

}