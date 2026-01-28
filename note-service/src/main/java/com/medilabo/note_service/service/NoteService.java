package com.medilabo.note_service.service;

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

    public List<Note> getNotesByPatientId(Integer patientId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "date");
        return noteRepository.findNotesByPatientId(patientId, sort);
    }

    public Note addNote(Integer patientId, String noteText) {
        Note note = new Note(patientId, noteText);
        note.setDate(LocalDateTime.now());
        return noteRepository.save(note);
    }
}
