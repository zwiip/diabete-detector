package com.medilabo.patientservice.entities;

@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @NotBlank(message = "Name must not be null")
    @Size(max = 50)
    @Column(length = 50, nullable = false)
    private String name;

    @NotBlank(message = "First Name must not be null")
    @Size(max = 50)
    @Column(name="first_name", length = 50, nullable = false)
    private String firstName;

    @NotNull(message = "The birthdate must not be null")
    @Column(name="birth_date", nullable = false)
    private LocalDate birthDate;

    @NotNull(message = "Gender must not be null")
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Gender gender;

    @Size(max = 255)
    @Column(length = 255)
    private String address;

    @Size(max = 15)
    @Column(length = 15)
    private String phone;

    public Patient() {}

    public Patient(String name, String firstName, LocalDate birthDate, com.medilabo.patientservice.entities.Patient.Gender gender, String address, String phone) {
        this.name = name;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
    }

    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}