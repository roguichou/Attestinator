package com.roguichou.attestinator.attestation;

import androidx.annotation.NonNull;

import com.roguichou.attestinator.Constants;

public enum Raison {

/*
  travail: ,
  achats_culturel_cultuel: ,
  sante: 434,
  famille: 410,
  handicap: 373,
  sport_animaux: ,
  convocation: ,
  missions: ,
  enfants: ,
}


 */
   // TRAVAIL ("travail", 553),
    ACHATS (Constants.CODE_RAISON_ACHATS, 482),
    SANTE (Constants.CODE_RAISON_SANTE, 434),
 //   FAMILLE ("famille", 410),
 //   HANDICAP ("handicap", 373),
    SPORT_ANIMAUX (Constants.CODE_RAISON_SPORT_ANIMAUX, 349),
 //   CONVOCATION ("convocation", 276),
 //   MISSIONS  ("missions", 252),
    ENFANTS (Constants.CODE_RAISON_ENFANTS, 228);



        private final String code;
        private final int position_y;

    Raison(String _code, int _position_y) {
        code = _code;
        position_y = _position_y;
    }

    @Override
    @NonNull
    public String toString()
    {
        return code;
    }


    public static Raison fromString(String code)
    {
        switch (String.valueOf(code)) {
            case Constants.CODE_RAISON_ACHATS:
                return Raison.ACHATS;
            case Constants.CODE_RAISON_SANTE:
                return Raison.SANTE;
            case Constants.CODE_RAISON_SPORT_ANIMAUX:
                return Raison.SPORT_ANIMAUX;
            case Constants.CODE_RAISON_ENFANTS:
                return Raison.ENFANTS;
        }
        return null;
    }

    public int getPosition_y()
    {
        return position_y;
    }
}
