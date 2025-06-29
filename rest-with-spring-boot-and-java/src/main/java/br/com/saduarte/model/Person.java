package br.com.saduarte.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name= "person")
public class Person  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(nullable = false, length = 6)
    private String gender;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(name = "wikipedia_profile_url", length = 255)
    private String profileUrl;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "person_books", joinColumns = @JoinColumn(name = "person_id"), inverseJoinColumns = @JoinColumn(name = "book_id"))
    private List<Book> books;

    public Person() {}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(getId(), person.getId()) && Objects.equals(getFirstName(), person.getFirstName()) && Objects.equals(getLastName(), person.getLastName()) && Objects.equals(getAddress(), person.getAddress()) && Objects.equals(getGender(), person.getGender()) && Objects.equals(getEnabled(), person.getEnabled()) && Objects.equals(getProfileUrl(), person.getProfileUrl()) && Objects.equals(getPhotoUrl(), person.getPhotoUrl()) && Objects.equals(getBooks(), person.getBooks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstName(), getLastName(), getAddress(), getGender(), getEnabled(), getProfileUrl(), getPhotoUrl(), getBooks());
    }
}
