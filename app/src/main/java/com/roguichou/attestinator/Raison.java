package com.roguichou.attestinator;

public enum Raison {

    TRAVAIL ("travail", 578),
    ACHATS ("achats", 533),
    SANTE ("sante", 477),
    FAMILLE ("famille", 435),
    HANDICAP ("handicap", 396),
    SPORT_ANIMAUX ("sport_animaux", 358),
    CONVOCATION ("convocation", 295),
    MISSIONS  ("missions", 255),
    ENFANTS ("enfants", 211);

        private final String code;
        private final int position_y;

    Raison(String _code, int _position_y) {
        code = _code;
        position_y = _position_y;
    }

    @Override
    public String toString()
    {
        return code;
    }

    public int getPosition_y()
    {
        return position_y;
    }
}
