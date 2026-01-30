package org.hhu.chatgpt.domain.model;

import jakarta.validation.constraints.*;
import org.hhu.chatgpt.domain.model.files.File;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    @Pattern(regexp = "^[0-9+\\-\\s]{6,20}$",
            message = "Ungültiges Telefonnummernformat")
    private  String phone;

    private  Set<String> tags;
    //private  Set<File> files;

    private  Set<Thema> themenSet;




    public Betreuer(Integer fachId,
                    String titel,
                    String firstName,
                    String lastName,
                    String email,
                    String phone,
                    Set<String> tags,
                    //List<File> files,
                    Set<Thema> themenSet

    ) {
        this.titel = titel;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.fachId = fachId;
        this.tags = tags != null ? Set.copyOf(tags) : Set.of();
        //this.files = files != null ? List.copyOf(files) : List.of();
        this.themenSet = themenSet;
    }

    public Betreuer(){}

    public String getTitel() { return titel; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Integer getFachId() { return fachId; }
    public Set<String> getTags() { return tags; }
    //public Set<File> getFiles() { return files; }
    public Set<Thema> getThemenSet() {return themenSet;}

    public void setThemenSet(Set<Thema> themenSet) { this.themenSet = (themenSet == null) ? new HashSet<>() : new HashSet<>(themenSet);}
    public void setTitel(String titel) { this.titel = titel; }
    public void setFachId(Integer gitHubId) {this.fachId = gitHubId;}
    public void setTags(Set<String> tags) { this.tags = tags; }
    public void setPhone(String phone){ this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

}
