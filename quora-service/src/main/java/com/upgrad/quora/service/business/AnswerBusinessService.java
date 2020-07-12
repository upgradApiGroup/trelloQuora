package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class AnswerBusinessService {

  @Autowired
  private AnswerDao answerDao;

  @Autowired
  private QuestionDao questionDao;

  @Autowired
  private UserDao userDao;

  /**
   * This method takes the following inputs:
   * @param accessToken   - accepts requester's access token as String value
   * @param answerEntity  - accepts AnswerEntity object passed from AnswerController
   * @param questionUuid  - accepts questionUuid String value passed from AnswerController
   *
   * and then validates the following:
   * 1. User should be signed in
   * 2. User should not have signed out after signing in
   * 3. Question to be answered, must exist in database
   *
   * After the validations, it sets the user and question for the answer and calls the AnswerDao
   * class method with the AnswerEntity object
   *
   * @return AnswerEntity object containing newly created answer
   * @exception AuthorizationFailedException if invalid/expired access token is used
   * @exception InvalidQuestionException if invalid/non-existing question ID is used
  */

  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity createAnswer(AnswerEntity answerEntity, final String questionUuid,
      final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {

    UserAuthEntity userAuthToken = userDao.getUserAuthByToken(accessToken);
    QuestionEntity question = questionDao.getQuestionById(questionUuid);

    if (userAuthToken == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    if (userAuthToken.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to post an answer");
    }
    if (question == null) {
      throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
    }

    answerEntity.setUser(userAuthToken.getUserId());
    answerEntity.setQuestion(question);

    return answerDao.createAnswer(answerEntity);
  }

  /**
   * This method takes the following inputs:
   * @param accessToken   - accepts requester's access token as String value
   * @param answerEntity  - accepts AnswerEntity object passed from AnswerController
   *
   * and then validates the following:
   * 1. User should be signed in
   * 2. User should not have signed out after signing in
   * 3. Answer to be edited, must exist in database
   * 4. Answer can be edited either by admin or answer owner
   *
   * After the validations, it sets the user, id, date and question for the answer and calls the AnswerDao
   * class method with the AnswerEntity object
   *
   * @return AnswerEntity object containing updated answer
   * @exception AuthorizationFailedException if invalid/expired access token is used
   * @exception AnswerNotFoundException if invalid/non-existing answer ID is used
   */

  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity editAnswer(AnswerEntity answerEntity, final String accessToken)
      throws AnswerNotFoundException, AuthorizationFailedException {

    UserAuthEntity userAuthToken = userDao.getUserAuthByToken(accessToken);
    AnswerEntity existingAnswer = answerDao.getAnswerByUuid(answerEntity.getUuid());

    if (userAuthToken == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    if (userAuthToken.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to edit an answer");
    }
    if (existingAnswer == null) {
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
    }
    if (!isUserOwner(userAuthToken, existingAnswer)) {
      throw new AuthorizationFailedException("ATHR-003",
          "Only the answer owner can edit the answer");
    }

    answerEntity.setUser(userAuthToken.getUserId());
    answerEntity.setDate(existingAnswer.getDate());
    answerEntity.setQuestion(existingAnswer.getQuestion());
    answerEntity.setId(existingAnswer.getId());

    return answerDao.editAnswer(answerEntity);
  }

  /**
   * This method takes the following inputs:
   * @param accessToken   - accepts requester's access token as String value
   * @param answerEntity  - accepts AnswerEntity object passed from AnswerController
   *
   * and then validates the following:
   * 1. User should be signed in
   * 2. User should not have signed out after signing in
   * 3. Answer to be edited, must exist in database
   * 4. Answer can be deleted either by admin or answer owner
   *
   * After the validations, it calls the AnswerDao class method with the existing AnswerEntity
   * object to be deleted from the DB
   *
   * @return AnswerEntity object of the deleted answer
   * @exception AuthorizationFailedException if invalid/expired access token is used
   * @exception AnswerNotFoundException if invalid/non-existing answer ID is used
   */

  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity deleteAnswer(final String answerId, final String accessToken)
      throws AnswerNotFoundException, AuthorizationFailedException {

    UserAuthEntity userAuthToken = userDao.getUserAuthByToken(accessToken);
    AnswerEntity existingAnswer = answerDao.getAnswerByUuid(answerId);

    if (userAuthToken == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    if (userAuthToken.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to delete an answer");
    }
    if (existingAnswer == null) {
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
    }
    if (!isUserOwner(userAuthToken, existingAnswer) && !isUserAdmin(userAuthToken)) {
      throw new AuthorizationFailedException("ATHR-003",
          "Only the answer owner or admin can delete the answer");
    }

    return answerDao.deleteAnswer(existingAnswer);
  }

  /**
   * This method takes the following inputs:
   * @param accessToken   - accepts requester's access token as String value
   * @param questionUuid  - accepts questionUuid String value passed from AnswerController
   *
   * and then validates the following:
   * 1. User should be signed in
   * 2. User should not have signed out after signing in
   * 3. Question to be answered, must exist in database
   *
   * After the validations, it calls the AnswerDao class method with the AnswerEntity object
   *
   * @return List of AnswerEntity objects
   * @exception AuthorizationFailedException if invalid/expired access token is used
   * @exception InvalidQuestionException if invalid/non-existing question ID is used
   */

  public List<AnswerEntity> getAllAnswersByQuestion(final String questionUuid,
      final String accessToken) throws InvalidQuestionException, AuthorizationFailedException {
    
    UserAuthEntity userAuthToken = userDao.getUserAuthByToken(accessToken);
    QuestionEntity question = questionDao.getQuestionById(questionUuid);

    if (userAuthToken == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    if (userAuthToken.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to get the answers");
    }
    if (question == null) {
      throw new InvalidQuestionException("QUES-001",
          "The question with entered uuid whose details are to be seen does not exist");
    }
    return answerDao.getAllAnswersByQuestion(question);
  }

  /**
   * This method takes the user authentication token object and verifies if the user is an admin
   *
   * @param userAuthToken - accepts the UserAuthEntity object
   * @return boolean value after the required validation
   */

  public boolean isUserAdmin(UserAuthEntity userAuthToken) {
    UserEntity loggedInUser = userAuthToken.getUserId();
    return loggedInUser.getRole().equals("admin");
  }

    /**
     * This method takes the user authentication token object and an answer object and verifies,
     * if the logged-in user is the same as the owner of the answer
     *
     * @param userAuthToken - accepts the UserAuthEntity object
     * @param existingAnswer - accepts the AnswerEntity object
     * @return boolean value after the required validation
     */

  public boolean isUserOwner(UserAuthEntity userAuthToken, AnswerEntity existingAnswer) {
    UserEntity loggedInUser = userAuthToken.getUserId();
    return loggedInUser.equals(existingAnswer.getUser());
  }
}
