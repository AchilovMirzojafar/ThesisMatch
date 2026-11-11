package org.hhu.thesismatch.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Table("betreuer")
public class BetreuerDTO {

    @Id
    private Integer id;

    @Version
    private Integer version;

    private Integer fachId;
    private String titel;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    private String email;
    private String phone;

    @MappedCollection(idColumn = "betreuer")
    private Set<BetreuerTagDTO> tags = new HashSet<>();

    @MappedCollection(idColumn = "betreuer")
    private Set<ThemaDTO> themenSet = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getFachId() {
        return fachId;
    }

    public void setFachId(Integer fachId) {
        this.fachId = fachId;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<BetreuerTagDTO> getTags() {
        return tags;
    }

    public void setTags(Set<BetreuerTagDTO> tags) {
        this.tags = tags;
    }

    public Set<ThemaDTO> getThemenSet() {
        return themenSet;
    }

    public void setThemenSet(Set<ThemaDTO> themenSet) {
        this.themenSet = themenSet;
    }
}

