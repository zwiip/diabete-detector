package com.medilabo.note_service.controller;

import com.medilabo.note_service.dto.NoteCreateDTO;
import com.medilabo.note_service.model.Note;
import com.medilabo.note_service.service.NoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Retrieves all notes associated to a specific patient identifier.
     * @param patientId Integer representing the patient's identifier.
     * @return a List of all notes for this identifier. It can be empty.
     */
    @GetMapping("/{patientId}")
    public List<Note> getNotesByPatientId(@PathVariable Integer patientId) {
        return noteService.getNotesByPatientId(patientId);
    }

    /**
     * Creates a new note.
     * @param newNote a Note to create
     * @return the created note.
     */
    @PostMapping
    public Note addNote(@RequestBody NoteCreateDTO newNote) {
        return noteService.addNote(newNote);
    }
}
