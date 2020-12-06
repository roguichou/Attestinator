package com.roguichou.attestinator;

public class Constants {

   public static final String ATT_DB_NAME = "attestationsdb.db";

   public static final String ATT_TABLE_NAME = "attestations";
   public static final String PROFIL_TABLE_NAME = "profils";
   public static final String TMP_TABLE_NAME = "att_sortie";

   public static final int DISTANCE_MAX = 20000; //en mètres
   public static final int DUREE_SORTIE = 3*60; //en minutes

   //durée d'obsolescence des attestations (en milisecondes)
   public static final long OBSO_ATT = 12*60*60*1000; //12h

   public static final String CODE_RAISON_ACHATS ="achats_culturel_cultuel";
   public static final String CODE_RAISON_SANTE ="sante";
   public static final String CODE_RAISON_SPORT_ANIMAUX ="sport_animaux";
   public static final String CODE_RAISON_ENFANTS ="enfants";


}
