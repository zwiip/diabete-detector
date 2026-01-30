package com.medilabo.assessment_service.dto;

import java.time.LocalDate;

public class PatientDTO {
    private LocalDate birthDate;
    private Gender gender;

    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
