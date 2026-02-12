package com.medilabo.note_service.repository;

import com.medilabo.note_service.model.Note;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

    /**
     * Custom method to find the notes matching a specific patient identifier.
     * @param patientId Integer of the patient
     * @param sort sort options for queries
     * @return a List of Note, which can be empty.
     */
    List<Note> findNotesByPatientId(Integer patientId, Sort sort);
}
