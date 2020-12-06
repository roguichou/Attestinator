package com.roguichou.attestinator.db;

import androidx.room.TypeConverter;

import com.roguichou.attestinator.attestation.Raison;

public class RaisonConverter {

    /**
     * Raison <=> String
     */
    @TypeConverter
    public static String fromRaisonToString(Raison value) {
        return value.toString();
    }

    @TypeConverter
    public static Raison fromStringToRaison(String value) {
        return (Raison.fromString(value));
    }


}
