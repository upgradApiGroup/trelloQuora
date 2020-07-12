package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "answer")

 /**
  * The Named Queries for extracting data from the DB
  */
@NamedQueries(
    {
        @NamedQuery(name = "getAnswerByUuid", query = "select a from AnswerEntity a where a.uuid = :answerUuid"),
        @NamedQuery(name = "getAnswersByQuestion", query = "select a from AnswerEntity a where a.question = :question")
    }
)
public class AnswerEntity implements Serializable {

  /**
   * Various class members which have to be mapped as table columns in the DB
   */
  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "UUID")
  @NotNull
  @Size(max = 200)
  private String uuid;

  @Column(name = "ANS")
  @NotNull
  @Size(max = 255)
  private String ans;

  @Column(name = "DATE")
  @NotNull
  private ZonedDateTime date;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private UserEntity user;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "QUESTION_ID")
  private QuestionEntity question;

  /**
   * getter and setter methods
   */
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getAns() {
    return ans;
  }

  public void setAns(String ans) {
    this.ans = ans;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public QuestionEntity getQuestion() {
    return question;
  }

  public void setQuestion(QuestionEntity question) {
    this.question = question;
  }
}
