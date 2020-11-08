package com.roguichou.attestinator;

import android.graphics.Bitmap;
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
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AfficherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AfficherFragment extends Fragment {

    ImageView image;
    View fragmentView = null;
    //PDDocument doc = null;
    public AfficherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AfficherFragment.
     */
    public static AfficherFragment newInstance() {
        return new AfficherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_afficher, container, false);

        fragmentView = view;

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity)getActivity()).setActionBarTitle("Attestation");

        image = view.findViewById(R.id.pdf_renderer);

        String fichier = getArguments().getString("fichier");

        if( null == fichier)
        {
            view.setBackgroundColor(Color.BLACK);
            image.setImageBitmap(((MainActivity)getActivity()).getQrBitmap());
        }
        else {
            view.setBackgroundColor(Color.WHITE);
            InputStream input;

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
        }



    }

}