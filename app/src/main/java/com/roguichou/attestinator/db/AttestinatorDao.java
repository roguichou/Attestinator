package com.roguichou.attestinator.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.roguichou.attestinator.Constants;
import com.roguichou.attestinator.Profil;
import com.roguichou.attestinator.attestation.AttestationPermanente;

import java.util.List;

@Dao
public interface AttestinatorDao {
    @Query("SELECT * FROM "+ Constants.ATT_TABLE_NAME)
    List<AttestationPermanente> getAttestationsPermanentes();

    @Query("SELECT * FROM "+ Constants.PROFIL_TABLE_NAME)
    List<Profil> getProfils();

    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AttestationPermanente attestation);

    /*
     * update the object in database
     * @param note, object to be updated
     */
    @Update
    void update(AttestationPermanente attestation);

    /*
     * delete the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(AttestationPermanente attestation);


    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Profil profil);

    /*
     * update the object in database
     * @param note, object to be updated
     */
    @Update
    void update(Profil profil);

    /*
     * delete the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(Profil profil);

}
