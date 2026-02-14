package org.hhu.chatgpt.domain.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class Betreuer {
    @NotNull(message = "Darf nicht leer sein!")
    private  Integer fachId;
    @NotBlank(message = "Titel muss ausgewählt werden!")
    private String titel;
    @NotBlank(message = "Darf nicht leer sein!")
    private  String firstName;
    @NotBlank(message = "Darf nicht leer sein!")
    private  String lastName;
    @NotBlank(message = "E-Mail darf nicht leer sein")
    @Size(max = 254, message = "E-Mail ist zu lang")
    @Email(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Bitte eine gültige E-Mail-Adresse eingeben"
    )
    private String email;
    @Pattern(regexp = "^[0-9+\\-\\s]{6,12}$",
            message = "Ungültiges Telefonnummernformat")
    private  String phone;

    private  Set<String> tags;

    private  Set<Thema> themenSet;


    public Betreuer(Integer fachId,
                    String titel,
                    String firstName,
                    String lastName,
                    String email,
                    String phone,
                    Set<String> tags,
                    Set<Thema> themenSet

    ) {
        this.titel = titel;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.fachId = fachId;
        this.tags = tags != null ? Set.copyOf(tags) : Set.of();
        this.themenSet = themenSet != null ? new HashSet<>(themenSet) : new HashSet<>();

    }

    public Betreuer(){}

    public String getTitel() { return titel; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Integer getFachId() { return fachId; }
    public Set<String> getTags() { return tags; }
    public Set<Thema> getThemenSet() {return themenSet;}

    public void setTags(Set<String> tags) {this.tags = (tags == null) ? Set.of() : Set.copyOf(tags);}

    public void setThemenSet(Set<Thema> themenSet) {this.themenSet = (themenSet == null) ? new HashSet<>() : new HashSet<>(themenSet);}
    public void setTitel(String titel) { this.titel = titel; }
    public void setFachId(Integer gitHubId) {this.fachId = gitHubId;}
    public void setPhone(String phone){ this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

}
