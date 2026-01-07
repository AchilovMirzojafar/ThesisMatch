package org.hhu.chatgpt.domain.model.betreuer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hhu.chatgpt.domain.model.files.File;
import org.springframework.format.annotation.NumberFormat;

import java.util.List;

public class Betreuer {

    //@NotBlank(message = "Darf nicth leer sein!")
    private final String firstName;
    //@NotBlank(message = "Darf nicth leer sein!")
    private final String lastName;
    //@Email(message = "Muss eine gültige Email Adresse sein!")
    private final String email;
    //@NumberFormat
    private final String phone;
    //@NotBlank(message = "Sie müssen mindestens ein Tag eingeben!")
    private final List<String> tags;
    private final List<File> files;
    private final String gitHubName;

    public Betreuer(String firstName,
                    String lastName,
                    String email,
                    String phone,
                    String gitHubName,
                    List<String> tags,
                    List<File> files

                   ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.gitHubName = gitHubName;
        this.tags = tags != null ? List.copyOf(tags) : List.of();
        this.files = files != null ? List.copyOf(files) : List.of();
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getGitHubName() { return gitHubName; }
    public List<String> getTags() { return tags; }
    public List<File> getFiles() { return files; }
    }
