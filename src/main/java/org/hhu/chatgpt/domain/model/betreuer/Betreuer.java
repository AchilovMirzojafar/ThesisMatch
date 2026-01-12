package org.hhu.chatgpt.domain.model.betreuer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hhu.chatgpt.domain.model.files.File;

import java.util.List;

public class Betreuer {
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
    private  List<String> tags;
    private  List<File> files;


    @NotBlank(message = "Darf nicht leer sein!")
    private  String gitHubName;

    public Betreuer(String titel,
                    String firstName,
                    String lastName,
                    String email,
                    String phone,
                    String gitHubName,
                    List<String> tags,
                    List<File> files

                   ) {
        this.titel = titel;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.gitHubName = gitHubName;
        this.tags = tags != null ? List.copyOf(tags) : List.of();
        this.files = files != null ? List.copyOf(files) : List.of();
    }

    public Betreuer(){}

    public String getTitel() { return titel; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getGitHubName() { return gitHubName; }
    public List<String> getTags() { return tags; }
    public List<File> getFiles() { return files; }

    public void setTitel(String titel) { this.titel = titel; }
    public void setGitHubName(String gitHubName) {this.gitHubName = gitHubName;}
    public void setFiles(List<File> files) { this.files = files; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public void setPhone(String phone){ this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

}
