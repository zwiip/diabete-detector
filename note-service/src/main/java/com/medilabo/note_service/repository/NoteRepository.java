package com.medilabo.note_service.repository;

import com.medilabo.note_service.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

    List<Note> findNotesByPatientId(Integer patientId);
}
