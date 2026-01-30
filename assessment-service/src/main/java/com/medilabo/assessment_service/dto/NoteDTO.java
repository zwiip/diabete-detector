package com.medilabo.assessment_service.dto;

public class NoteDTO {
    private String note;

    public NoteDTO(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
