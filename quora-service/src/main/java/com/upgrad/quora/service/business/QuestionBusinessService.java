package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class QuestionBusinessService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private QuestionDao questionDao;

  /**
   * Create Question Business Service
   * @param questionEntity     - accepts QuestionEntity object passed from QuestionController
   * @param authorizationToken - accepts String containing requester's authorization code
   * @description Validates if the requester with the passed authorizationToken has signed in
   * and not signed out. Prepares the questionEntity with the userId from the UserAuthEntity.
   * Calls the questionDao with the questionEntity as a parameter.
   * @return QuestionEntity object
   * @throws AuthorizationFailedException if invalid/expired authorizationToken is used
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionEntity createQuestion(QuestionEntity questionEntity,
      final String authorizationToken) throws AuthorizationFailedException {

    // Login Validations
    UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(authorizationToken);
    isUserSignedIn(userAuthEntity);
    if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to post a question");
    }

    questionEntity.setUserId(userAuthEntity.getUserId());
    return questionDao.createQuestion(questionEntity);
  }

  /**
   * Get All Questions Business Service
   * @param authorizationToken - accepts String containing requester's authorization code
   * @description Validates if the requester with the passed authorizationToken has signed in
   * and not signed out. Calls the questionDao with the questionEntity as a parameter.
   * @return List of type QuestionEntity
   * @throws AuthorizationFailedException if invalid/expired authorizationToken is used
   */
  public List<QuestionEntity> getAllQuestions(final String authorizationToken)
      throws AuthorizationFailedException {

    // Login Validations
    UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(authorizationToken);
    isUserSignedIn(userAuthEntity);
    if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to get all questions");
    }

    return questionDao.getAllQuestions();
  }

  /**
   * Edit Question Content Business Service
   * @param questionEntity     - accepts QuestionEntity object passed from QuestionController
   * @param authorizationToken - accepts String containing requester's authorization code
   * @description Validates if the requester with the passed authorizationToken has signed in
   * and not signed out. Validates the questionId and it's ownership with the requester's
   * userId. Prepares the questionEntity with other setter methods. Calls the questionDao with the
   * questionEntity as a parameter.
   * @return QuestionEntity object
   * @throws AuthorizationFailedException if invalid/expired authorizationToken is used
   * @throws InvalidQuestionException if invalid Question ID is used
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionEntity editQuestionContent(QuestionEntity questionEntity,
      String authorizationToken)
      throws AuthorizationFailedException, InvalidQuestionException {

    // Login Validations
    UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(authorizationToken);
    isUserSignedIn(userAuthEntity);
    if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to edit the question");
    }

    // Question Validations
    QuestionEntity existingQuestion = questionDao.getQuestionById(questionEntity.getUuid());
    isValidQuestion(existingQuestion);
    isValidOwner(userAuthEntity, existingQuestion);

    questionEntity.setId(existingQuestion.getId());
    questionEntity.setDateCreated(existingQuestion.getDateCreated());
    questionEntity.setUserId(existingQuestion.getUserId());

    return questionDao.editQuestionContent(questionEntity);
  }

  /**
   * Delete Question Business Service
   * @param uuid     - accepts String containing the UUID of the question to be deleted
   * @param authorizationToken - accepts String containing requester's authorization code
   * @description Validates if the requester with the passed authorizationToken has signed in
   * and not signed out. Validates the questionId and it's ownership with the requester's userId,
   * Or the role of the requester. Prepares the questionEntity with other setter methods.
   * Calls the questionDao with the questionEntity as a parameter.
   * @return QuestionEntity object
   * @throws AuthorizationFailedException if invalid/expired authorizationToken is used
   * @throws InvalidQuestionException if invalid Question ID is used
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public String deleteQuestion(String uuid, String authorizationToken)
      throws AuthorizationFailedException, InvalidQuestionException {

    // Login Validations
    UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(authorizationToken);
    isUserSignedIn(userAuthEntity);
    if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to delete a question");
    }

    // Question Validations
    QuestionEntity existingQuestion = questionDao.getQuestionById(uuid);
    isValidQuestion(existingQuestion);
    isOwnerOrAdmin(userAuthEntity, existingQuestion);

    return questionDao.deleteQuestion(existingQuestion);
  }

  /**
   * Get Question By Id Business Service
   * @param userId     - accepts String containing the userId of whose questions must be fetched
   * @param authorizationToken - accepts String containing requester's authorization code
   * @description Validates if the requester with the passed authorizationToken has signed in
   * and not signed out. Validates the userId. Calls the questionDao with the
   * userEntity as a parameter.
   * @return QuestionEntity object
   * @throws AuthorizationFailedException if invalid/expired authorizationToken is used
   * @throws UserNotFoundException if invalid User ID is used
   */
  public List<QuestionEntity> getAllQuestionsByUser(final String userId,
      final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {

    // Login Validations
    UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(authorizationToken);
    isUserSignedIn(userAuthEntity);
    if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to get all questions posted by a specific user");
    }

    // User Validation
    UserEntity existingUser = userDao.getUserById(userId);
    if (existingUser == null) {
      throw new UserNotFoundException("USR-001",
          "User with entered uuid whose question details are to be seen does not exist");
    }

    return questionDao.getAllQuestionsByUser(existingUser);
  }

  /** Auxiliary Method: SignIn validation
   * @param userAuthToken - accepts UserAuthEntity object from multiple methods in
   *                      QuestionBusinessService Class
   * @throws AuthorizationFailedException if userAuthToken is null
   */
  private void isUserSignedIn(UserAuthEntity userAuthToken) throws AuthorizationFailedException {
    if (userAuthToken == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
  }

  /** Auxiliary Method: Question validation
   * @param existingQuestion - accepts QuestionEntity from multiple methods in
   *                         QuestionBusinessService Class
   * @throws InvalidQuestionException if existingQuestion is null
   */
  private void isValidQuestion(QuestionEntity existingQuestion) throws InvalidQuestionException {
    if (existingQuestion == null) {
      throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
    }
  }

  /** Auxiliary Method: Question ownership validation
   * @param userAuthEntity - accepts UserAuthEntity object
   * @param existingQuestion - accepts QuestionEntity object
   * @throws AuthorizationFailedException if userId of both the requester and
   * the question do not match.
   */
  private void isValidOwner(UserAuthEntity userAuthEntity, QuestionEntity existingQuestion)
      throws AuthorizationFailedException {
    if (!userAuthEntity.getUserId().equals(existingQuestion.getUserId())) {
      throw new AuthorizationFailedException("ATHR-003",
          "Only the question owner can edit the question");
    }
  }

  /** Auxiliary Method: Question ownership validation
   * @param userAuthEntity - accepts UserAuthEntity object
   * @param existingQuestion - accepts QuestionEntity object
   * @throws AuthorizationFailedException if userId of both the requester and
   * the question do not match, AND if the requester is not the 'admin'
   */
  private void isOwnerOrAdmin(UserAuthEntity userAuthEntity, QuestionEntity existingQuestion)
      throws AuthorizationFailedException {
    boolean isOwner = userAuthEntity.getUserId().equals(existingQuestion.getUserId());
    boolean isAdmin = userAuthEntity.getUserId().getRole().equals("admin");
    if (!isOwner && !isAdmin) {
      throw new AuthorizationFailedException("ATHR-003",
          "Only the question owner or admin can delete the question");
    }
  }
}
