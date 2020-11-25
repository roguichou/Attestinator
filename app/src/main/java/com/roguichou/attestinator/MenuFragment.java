package com.roguichou.attestinator;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.roguichou.attestinator.attestation.Attestation;
import com.roguichou.attestinator.attestation.AttestationPermanente;
import com.roguichou.attestinator.attestation.AttestationTemporaire;

import java.io.File;
import java.util.List;

public class MenuFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity)getActivity()).setActionBarTitle("Attestation-inator");
        Profil profil = ((MainActivity)getActivity()).getProfil();

        view.findViewById(R.id.info_but).setOnClickListener(view1 -> NavHostFragment.findNavController(MenuFragment.this)
                .navigate(R.id.action_MenuFragment_to_aproposFragment));

        view.findViewById(R.id.button_profil).setOnClickListener(view1 -> NavHostFragment.findNavController(MenuFragment.this)
                .navigate(R.id.action_MenuFragment_to_SecondFragment));

        view.findViewById(R.id.button_new).setOnClickListener(view0 -> {

            if (profil.isValid()) {
                NavHostFragment.findNavController(MenuFragment.this)
                        .navigate(R.id.action_MenuFragment_to_newAttFragment);
            }
            else
            {
                Snackbar mySnackbar = Snackbar.make(view, "Veuillez compléter votre profil", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });

        view.findViewById(R.id.button_new).setOnClickListener(view12 -> {

            if (profil.isValid()) {
                NavHostFragment.findNavController(MenuFragment.this)
                        .navigate(R.id.action_MenuFragment_to_newAttFragment);
            }
            else
            {
                Snackbar mySnackbar = Snackbar.make(view, "Veuillez compléter votre profil", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });

        view.findViewById(R.id.button_show_qr).setOnClickListener(view13 -> {
            AttestationTemporaire attestation = ((MainActivity) getActivity()).getAttestationTemporaire();
            attestation.setFileType(Attestation.FILE_TYPE_NONE);
            if(attestation.isValid())
            {
                Bundle bundle = new Bundle();
                bundle.putSerializable("attestation",attestation);
                NavHostFragment.findNavController(MenuFragment.this)
                        .navigate(R.id.action_MenuFragment_to_afficherFragment, bundle);
            }
            else
            {
                Snackbar mySnackbar = Snackbar.make(view13, "Aucune attestation à afficher.", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });

        view.findViewById(R.id.button_show_pdf).setOnClickListener(view14 -> {
            AttestationTemporaire attestation = ((MainActivity) getActivity()).getAttestationTemporaire();
            attestation.setFileType(Attestation.FILE_TYPE_PDF);
            if(attestation.isValid())
            {
                Bundle bundle = new Bundle();
                bundle.putSerializable("attestation",attestation);
                NavHostFragment.findNavController(MenuFragment.this)
                        .navigate(R.id.action_MenuFragment_to_afficherFragment, bundle);
            }
            else
            {
                Snackbar mySnackbar = Snackbar.make(view, "Aucune attestation à afficher.", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });

        /* **********  Dynamically create buttons  ********** */
        buildTableShowAttestationPermanente(view);


    }


    private void buildTableShowAttestationPermanente(@NonNull View view)
    {
        TableLayout table = (view.findViewById(R.id.table_show_att));

        //1. clear all rows
        table.removeAllViews();

        //2. create rows
        Resources res = getResources();
        List<AttestationPermanente> attestations = ((MainActivity)getActivity()).getPermanentAttestations();
        TableRow row = null;
        int col=0;

        for (int i=0; i<attestations.size();i++)
        {
            TextView txt = null;
            ImageButton but = null;
            AttestationPermanente attestation = attestations.get(i);

            switch (col)
            {
                case 0:
                case 3:
                    row = (TableRow)LayoutInflater.from(getContext()).inflate(R.layout.show_att_row, null);
                    table.addView(row);
                    txt = row.findViewById(R.id.label_att_1);
                    but = row.findViewById(R.id.show_att_1);
                    col = 0;
                    break;
                case 1:
                    txt = row.findViewById(R.id.label_att_2);
                    but = row.findViewById(R.id.show_att_2);
                    break;
                case 2:
                    txt = row.findViewById(R.id.label_att_3);
                    but = row.findViewById(R.id.show_att_3);
                    break;
            }

            //Label
            txt.setText(attestation.getLabel());

            //button
            int type = attestation.getAttestationType();
            String img = "ic_profile";
            switch(type)
            {
                case AttestationPermanente.ATTESTATION_TYPE_HOME :
                    img = "ic_noun_attestation_824051";
                    break;
                case AttestationPermanente.ATTESTATION_TYPE_WORK :
                    img = "ic_att_travail";
                    break;
                case AttestationPermanente.ATTESTATION_TYPE_ECOLE :
                    img = "ic_att_ecole";
                    break;
            }

            but.setImageResource(res.getIdentifier("com.roguichou.attestinator:drawable/"+ img,null,null));
            but.setOnClickListener(view0 -> {
                File att_fn = new File(getActivity().getFilesDir()+"/"+attestation.getFilename());
                if (att_fn.exists()) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("attestation",attestation);
                    NavHostFragment.findNavController(MenuFragment.this)
                            .navigate(R.id.action_MenuFragment_to_afficherFragment, bundle);
                }
                else
                {
                    Snackbar mySnackbar = Snackbar.make(view, "Aucune attestation à afficher.", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
            });

            col++;
        }

        switch (col)
        {
            case 1:
                row.findViewById(R.id.show_att_2).setVisibility(View.INVISIBLE);
                row.findViewById(R.id.label_att_2).setVisibility(View.INVISIBLE);
            case 2:
                row.findViewById(R.id.show_att_3).setVisibility(View.INVISIBLE);
                row.findViewById(R.id.label_att_3).setVisibility(View.INVISIBLE);
        }
    }

}