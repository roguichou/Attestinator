package com.roguichou.attestinator.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.roguichou.attestinator.Constants;
import com.roguichou.attestinator.attestation.AttestationPermanente;

import java.util.List;

@Dao
public interface AttestationPermanenteDao {
    @Query("SELECT * FROM "+ Constants.ATT_TABLE_NAME)
    List<AttestationPermanente> getAll();


    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
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
     * delete list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(AttestationPermanente... attestation);      // Note... is varargs, here note is an array

}
