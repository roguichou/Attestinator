package com.roguichou.attestinator;

import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

public class NewAttFragment extends Fragment {

    private Raison raison = null;
    private View fragmentView = null;
    TimePicker picker = null;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_att, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        picker=(TimePicker)view.findViewById(R.id.timePicker1);
        picker.setIs24HourView(true);

        fragmentView = view;

        view.findViewById(R.id.button_generer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1. vérifier qu'on a bien une raison
                int chip_id = ((ChipGroup)fragmentView.findViewById(R.id.raison_group)).getCheckedChipId();

                if (ChipGroup.NO_ID == chip_id )
                {
                    String msg = "Pas de raison sélectionnée";
                    Snackbar mySnackbar = Snackbar.make(fragmentView, msg, Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
                else
                {
                    String msg = "raison sélectionnée : "+chip_id;
                    Snackbar mySnackbar = Snackbar.make(fragmentView, msg, Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }


            }
        });



        ((Chip)view.findViewById(R.id.raison_work)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    raison = Raison.TRAVAIL;
                }
            }
        });

        ((Chip)view.findViewById(R.id.raison_courses)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    raison = Raison.ACHATS;
                }
            }
        });
        ((Chip)view.findViewById(R.id.raison_sante)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    raison = Raison.SANTE;
                }
            }
        });
        ((Chip)view.findViewById(R.id.raison_prommenade)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    raison = Raison.SPORT_ANIMAUX;
                }
            }
        });
        ((Chip)view.findViewById(R.id.raison_enfants)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    raison = Raison.ENFANTS;
                }
            }
        });


        view.findViewById(R.id.button_generer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1. vérifier qu'on a bien une raison
                int chip_id = ((ChipGroup)fragmentView.findViewById(R.id.raison_group)).getCheckedChipId();

                if (ChipGroup.NO_ID == chip_id  || null == raison)
                {
                    String msg = "Pas de raison sélectionnée";
                    Snackbar mySnackbar = Snackbar.make(fragmentView, msg, Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
                else
                {

                    if (raison == Raison.SPORT_ANIMAUX) {
                        ((MainActivity)getActivity()).debuterSortie (fragmentView, picker.getHour(), picker.getMinute());
                    }
                    ((MainActivity)getActivity()).genererAttestation(fragmentView, raison, picker.getHour(), picker.getMinute());

                    NavHostFragment.findNavController(NewAttFragment.this)
                            .navigate(R.id.action_newAttFragment_to_FirstFragment);

                    //String msg = "raison sélectionnée : "+ raison.toString()+ " H départ:"+picker.getHour()+":"+picker.getMinute();
                    //Snackbar mySnackbar = Snackbar.make(fragmentView, msg, Snackbar.LENGTH_SHORT);
                    //mySnackbar.show();
                }


            }
        });


    }





    /*
     "code": "travail",
     "code": "achats",
     "code": "sante",
  //   "code": "famille",
  //   "code": "handicap",
     "code": "sport_animaux",
  //   "code": "convocation",
  //   "code": "missions",
     "code": "enfants",
*/
}
