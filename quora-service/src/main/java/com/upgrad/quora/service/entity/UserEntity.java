package com.upgrad.quora.service.entity;

// id                   SERIAL,
// uuid                 VARCHAR(200) NOT NULL,
// firstName            VARCHAR(30) NOT NULL,
// lastName             VARCHAR(30) NOT NULL,
// userName             VARCHAR(30) UNIQUE NOT NULL,
// email                VARCHAR(50) UNIQUE NOT NULL,
// password             VARCHAR(255) NOT NULL,
// salt                 VARCHAR(200) NOT NULL,
// country              VARCHAR(30),
// aboutMe              VARCHAR(50),
// dob                  VARCHAR(30),
// role                 VARCHAR(30),
// contactNumber        VARCHAR(30),
// PRIMARY KEY (id));

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "USERS")
@NamedQueries({
    @NamedQuery(name = "userByUsername", query = "select u from UserEntity u where u.userName = :username"),
    @NamedQuery(name = "userByUuid", query = "select u from UserEntity u where u.uuid = :uuid"),
    @NamedQuery(name = "userByEmail", query = "select u from UserEntity u where u.email = :email"),
    @NamedQuery(name = "deleteUserById", query = "delete UserEntity u where u.uuid = :uuid")
})

public class UserEntity implements Serializable {

  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "UUID")
  @NotNull
  @Size(max = 200)
  private String uuid;

  @Column(name = "FIRSTNAME ")
  @NotNull
  @Size(max = 30)
  private String firstName;

  @Column(name = "LASTNAME")
  @NotNull
  @Size(max = 30)
  private String lastName;

  @Column(name = "USERNAME")
  @NotNull
  @Size(max = 30)
  private String userName;

  @Column(name = "EMAIL")
  @NotNull
  @Size(max = 50)
  private String email;

  @Column(name = "PASSWORD")
  @NotNull
  @Size(max = 255)
  private String password;

  @Column(name = "SALT")
  @NotNull
  @Size(max = 200)
  private String salt;

  @Column(name = "COUNTRY")
  @Size(max = 30)
  private String country;

  @Column(name = "ABOUTME")
  @Size(max = 50)
  private String aboutme;

  @Column(name = "DOB")
  @Size(max = 30)
  private String dob;

  @Column(name = "ROLE")
  @Size(max = 30)
  private String role;

  @Column(name = "CONTACTNUMBER")
  @Size(max = 30)
  private String contactNumber;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
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

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getAboutme() {
    return aboutme;
  }

  public void setAboutme(String aboutme) {
    this.aboutme = aboutme;
  }

  public String getDob() {
    return dob;
  }

  public void setDob(String dob) {
    this.dob = dob;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }
}
