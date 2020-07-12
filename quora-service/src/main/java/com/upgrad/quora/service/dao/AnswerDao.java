package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

  @PersistenceContext
  private EntityManager entityManager;

  /** Create an answer
   * @param answerEntity - accepts AnswerEntity object
   *
   * persists the newly created answer object in the database
   *
   * @return newly created AnswerEntity object
   * */

  public AnswerEntity createAnswer(AnswerEntity answerEntity) {
    entityManager.persist(answerEntity);
    return answerEntity;
  }

  /** Edit an answer
   * @param answerEntity - accepts AnswerEntity object
   *
   * merges the updated answer object in the database
   *
   * @return updated AnswerEntity object
   * */

  public AnswerEntity editAnswer(AnswerEntity answerEntity) {
    entityManager.merge(answerEntity);
    return answerEntity;
  }

  /** Delete an answer
   * @param answerEntity - accepts AnswerEntity object
   *
   * Deletes an answer as per the provided AnswerEntity
   *
   * @return the AnswerEntity object which was deleted from the DB
   */

  public AnswerEntity deleteAnswer(AnswerEntity answerEntity) {
    entityManager.remove(answerEntity);
    return answerEntity;
  }

  /** Get question by Id
   * @param answerUuid - accepts answer UUID as a String
   *
   * fetches the AnswerEntity with provided UUID from the database
   *
   * @return AnswerEntity object
   */

  public AnswerEntity getAnswerByUuid(final String answerUuid) {
    try {
      return entityManager.createNamedQuery("getAnswerByUuid", AnswerEntity.class)
          .setParameter("answerUuid", answerUuid).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /** Get all answers to a question
   * @param question - accepts QuestionEntity object
   *
   * Fetches all the answers for the provided QuestionEntity from the DB
   *
   * @return List of AnswerEntity objects
   */

  public List<AnswerEntity> getAllAnswersByQuestion(QuestionEntity question) {
    try {
      return entityManager.createNamedQuery("getAnswersByQuestion", AnswerEntity.class)
          .setParameter("question", question).getResultList();
    } catch (NoResultException nre) {
      return null;
    }
  }
}
