package com.roguichou.attestinator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class FirstFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity)getActivity()).setActionBarTitle("Attestation-inator");

        view.findViewById(R.id.button_profil).setOnClickListener(view1 -> NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment));

        view.findViewById(R.id.button_new).setOnClickListener(view12 -> {

            if (((MainActivity)getActivity()).isProfilFull()) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_newAttFragment);
            }
            else
            {
                Snackbar mySnackbar = Snackbar.make(view, "Veuillez compléter votre profil", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });

        view.findViewById(R.id.button_show_qr).setOnClickListener(view13 -> {
            File attestation = new File(getActivity().getFilesDir()+"/attestation.pdf");
            if (attestation.exists()) {
                Bundle bundle = new Bundle();
                bundle.putString("fichier", null);
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_afficherFragment, bundle);
            }
            else
            {
                Snackbar mySnackbar = Snackbar.make(view13, "Aucune attestation à afficher.", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });

        view.findViewById(R.id.button_show_pdf).setOnClickListener(view14 -> {
            File attestation = new File(getActivity().getFilesDir()+"/attestation.pdf");
            if (attestation.exists()) {
                Bundle bundle = new Bundle();
                bundle.putString("fichier", "attestation.pdf");
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_afficherFragment, bundle);
            }
            else
            {
                Snackbar mySnackbar = Snackbar.make(view, "Aucune attestation à afficher.", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });

        view.findViewById(R.id.button_show_home).setOnClickListener(view15 -> {
            File attestation = new File(getActivity().getFilesDir()+"/att_dom.pdf");
            if (attestation.exists()) {
                Bundle bundle = new Bundle();
                bundle.putString("fichier", "att_dom.pdf");
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_afficherFragment, bundle);
            }
            else
            {
                Snackbar mySnackbar = Snackbar.make(view, "Aucune attestation à afficher.", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });


        view.findViewById(R.id.button_show_work).setOnClickListener(view16 -> {
            File attestation = new File(getActivity().getFilesDir()+"/att_work.pdf");
            if (attestation.exists()) {
                Bundle bundle = new Bundle();
                bundle.putString("fichier", "att_work.pdf");
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_afficherFragment, bundle);
            }
            else
            {
                Snackbar mySnackbar = Snackbar.make(view, "Aucune attestation à afficher.", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });


        view.findViewById(R.id.button_show_creche).setOnClickListener(view17 -> {
            File attestation = new File(getActivity().getFilesDir()+"/att_creche.pdf");
            if (attestation.exists()) {
                Bundle bundle = new Bundle();
                bundle.putString("fichier", "att_creche.pdf");
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_afficherFragment, bundle);
            }
            else
            {
                Snackbar mySnackbar = Snackbar.make(view, "Aucune attestation à afficher.", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });

        view.findViewById(R.id.button_show_school).setOnClickListener(view18 -> {
            File attestation = new File(getActivity().getFilesDir()+"/att_ecole.pdf");
            if (attestation.exists()) {
                Bundle bundle = new Bundle();
                bundle.putString("fichier", "att_ecole.pdf");
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_afficherFragment, bundle);
            }
            else
            {
                Snackbar mySnackbar = Snackbar.make(view, "Aucune attestation à afficher.", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });

    }
}