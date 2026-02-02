package com.medilabo.assessment_service.dto;

public class NoteDTO {
    private String noteText;

    public NoteDTO(String noteText) {
        this.noteText = noteText;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}
