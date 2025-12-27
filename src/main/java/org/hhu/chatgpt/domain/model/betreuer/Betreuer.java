package org.hhu.chatgpt.domain.model.betreuer;

import org.hhu.chatgpt.domain.model.files.File;

import java.util.List;

public class Betreuer {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    // Fachgebiete als Tags
    private List<String> tags;

    // Hochgeladene Dateien
    private List<File> files;

    public Betreuer(String vorName,
                    String nachName,
                    String email,
                    String phone,
                    List<String> tags,
                    List<File> files) {
        this.firstName = vorName;
        this.lastName = nachName;
        this.email = email;
        this.phone = phone;
        this.tags = tags;
        this.files = files;
    }

    // Getter
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<File> getFiles() {
        return files;
    }
}
