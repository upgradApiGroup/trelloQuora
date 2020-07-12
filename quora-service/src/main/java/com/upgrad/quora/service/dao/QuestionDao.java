package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

  @PersistenceContext
  private EntityManager entityManager;

  /** Creates a question
   * @param questionEntity - accepts QuestionEntity object
   * @description persists the new object in the database
   * @return newly created QuestionEntity object
   * */
  public QuestionEntity createQuestion(QuestionEntity questionEntity) {
    entityManager.persist(questionEntity);
    return questionEntity;
  }

  /** Get all questions
   * @description fetches all questions from the database
   * @return List object of type QuestionEntity
   */
  public List<QuestionEntity> getAllQuestions() {
    try {
      return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class)
          .getResultList();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /** Update/edit a question
   * @param questionEntity - accepts QuestionEntity object
   * @description merges the updated object in the database
   * @return newly updated QuestionEntity object
   */
  public QuestionEntity editQuestionContent(final QuestionEntity questionEntity) {
    return entityManager.merge(questionEntity);
  }

  /** Get question by Id
   * @param uuid - accepts question UUID as a String
   * @description fetches the QuestionEntity with provided UUID from the database
   * @return QuestionEntity object
   */
  public QuestionEntity getQuestionById(final String uuid) {
    try {
      return entityManager.createNamedQuery("getQuestionByUuid", QuestionEntity.class)
          .setParameter("uuid", uuid).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /** Delete a question by Id
   * @param existingQuestion - accepts QuestionEntity object
   * @description Deleted a question as per the provided QuestionEntity
   * @return UUID String of the deleted QuestionEntity
   */
  public String deleteQuestion(final QuestionEntity existingQuestion) {
    entityManager.remove(existingQuestion);
    return existingQuestion.getUuid();
  }

  /** Get questions by User
   * @param userEntity - accepts UserEntity object
   * @description Fetches all the questions from the DB that were created by the provided UserEntity
   * @return List object of type QuestionEntity
   */
  public List<QuestionEntity> getAllQuestionsByUser(UserEntity userEntity) {
    try {
      return entityManager.createNamedQuery("QuestionByUserId", QuestionEntity.class)
          .setParameter("user", userEntity).getResultList();
    } catch (NoResultException nre) {
      return null;
    }
  }
}
