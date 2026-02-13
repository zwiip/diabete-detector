package com.medilabo.note_service.service;

import com.medilabo.note_service.dto.NoteCreateDTO;
import com.medilabo.note_service.model.Note;
import com.medilabo.note_service.repository.NoteRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /**
     * Retrieves all the notes for a specific patient identifier
     * @param patientId Integer of the patient unique identifier.
     * @return a list of Note which can be empty.
     */
    public List<Note> getNotesByPatientId(Integer patientId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "date");
        return noteRepository.findNotesByPatientId(patientId, sort);
    }

    /**
     * Save a new note to database.
     * @param newNote a NoteCreateDTO object with the information for the new note.
     * @return the saved Note object.
     */
    public Note addNote(NoteCreateDTO newNote) {
        Note note = new Note(newNote.getPatientId(), newNote.getNoteText());
        note.setDate(LocalDateTime.now());
        return noteRepository.save(note);
    }
}
