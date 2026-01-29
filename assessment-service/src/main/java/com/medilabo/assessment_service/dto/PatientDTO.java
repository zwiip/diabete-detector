package com.medilabo.assessment_service.dto;

import java.util.List;

public class PatientDTO {
    private Integer id;
    private Integer age;
    private Gender gender;
    List<String> notes; // patient notes.notetext

    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }
}
