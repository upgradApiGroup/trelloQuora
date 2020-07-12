package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

  @Autowired
  private QuestionBusinessService questionBusinessService;

  /**
   * Create Question Controller
   * @param questionRequest - accepts QuestionRequest object
   * @param authorization   - accepts authorization code of signed in user
   * @description Creates a QuestionEntity object. Calls QuestionBusinessService, passes the object
   * @return The UUID of the question created as part of QuestionResponse object
   * @exception AuthorizationFailedException if invalid credentials are used by the requester
   */
  @RequestMapping(method = RequestMethod.POST, path = "/question/create",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException {

    final QuestionEntity questionEntity = new QuestionEntity();

    questionEntity.setUuid(UUID.randomUUID().toString());
    questionEntity.setContent(questionRequest.getContent());
    questionEntity.setDateCreated(ZonedDateTime.now());

    final QuestionEntity createdQuestion = questionBusinessService
        .createQuestion(questionEntity, authorization);
    QuestionResponse createQuestionResponse = new QuestionResponse().id(createdQuestion.getUuid())
        .status("QUESTION CREATED");
    return new ResponseEntity<QuestionResponse>(createQuestionResponse, HttpStatus.CREATED);
  }

  /**
   * Get All Questions Controller
   * @param authorization - accepts authorization code of signed in user.
   * @description Calls the QuestionBusinessService, passes authorization as a parameter
   * @return Returns all questions from the database after validating the user request
   * @throws AuthorizationFailedException if invalid credentials are used by the requester
   */
  @RequestMapping(method = RequestMethod.GET, path = "/question/all",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<ArrayList> getAllQuestions(
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException {

    final List<QuestionEntity> allQuestionsList = questionBusinessService
        .getAllQuestions(authorization);
    ArrayList<QuestionDetailsResponse> questionDetailsResponses = convertToQuestionDetailsResponseArray(
        allQuestionsList);
    return new ResponseEntity<>(questionDetailsResponses, HttpStatus.OK);
  }

  /**
   * Edit Question Controller
   * @param questionEditRequest - accepts QuestionEditRequest Object
   * @param authorization       - accepts authorization code of signed in user
   * @param questionId          - accepts questionId from path passed on as a variable
   * @description Updates the content of the question with the specified questionId, with the
   * content passed on in the EditQuestionRequest object
   * @return The UUID of the question updated as part of QuestionResponse object
   * @throws AuthorizationFailedException if invalid credentials are used by the requester
   * @throws InvalidQuestionException if invalid question Id is used by the requester
   */
  @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionEditResponse> editQuestionContent(
      final QuestionEditRequest questionEditRequest,
      @PathVariable("questionId") final String questionId,
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException, InvalidQuestionException {
    QuestionEntity questionEntity = new QuestionEntity();
    questionEntity.setUuid(questionId);
    questionEntity.setContent(questionEditRequest.getContent());

    QuestionEntity updatedQuestion = questionBusinessService
        .editQuestionContent(questionEntity, authorization);
    QuestionEditResponse questionEditResponse = new QuestionEditResponse()
        .id(updatedQuestion.getUuid())
        .status("QUESTION EDITED");
    return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
  }

  /**
   * Delete Question Controller
   * @param authorization - accepts authorization code of signed in user
   * @param questionId    - accepts questionId from path passed on as a variable
   * @description Calls the QuestionBusinessService and passes the questionId to be deleted
   * @return The UUID of the question updated as part of QuestionResponse object
   * @throws AuthorizationFailedException if invalid credentials are used by the requester
   * @throws InvalidQuestionException if invalid question Id is used by the requester
   */
  @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}")
  public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
      @PathVariable("questionId") final String questionId,
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException, InvalidQuestionException {
    String deletedQuestionId = questionBusinessService.deleteQuestion(questionId, authorization);
    QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse()
        .id(deletedQuestionId).status("QUESTION DELETED");
    return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
  }

  /**
   * Get All Questions of a user
   * @param userId        - accepts the userId from the path passed on as a variable
   * @param authorization - accepts authorization code of signed in user
   * @description Calls the QuestionBusinessService and passed the userId whose questions
   * are to be fetched
   * @return Returns all questions of specific user from the DB after validating the user request
   * @throws AuthorizationFailedException if invalid credentials are used by the requester
   * @throws UserNotFoundException is invalid userId is passed
   */
  @RequestMapping(method = RequestMethod.GET, path = "question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<ArrayList> getAllQuestionsByUser(
      @PathVariable("userId") final String userId,
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException, UserNotFoundException {

    final List<QuestionEntity> allQuestionsList = questionBusinessService
        .getAllQuestionsByUser(userId, authorization);

    ArrayList<QuestionDetailsResponse> questionDetailsResponses = convertToQuestionDetailsResponseArray(
        allQuestionsList);
    return new ResponseEntity<>(questionDetailsResponses, HttpStatus.OK);
  }

  /**
   * Auxiliary (private) Methods
   * @param allQuestionsList - accepts the List object of type QuestionEntity Converts into an
   * @description ArrayList of type QuestionDetailsResponse so that it can form the ResponseEntity
   * of getAllQuestions() and getAllQuestionsByUser() Controller methods
   * @return ArrayList of type QuestionDetailsResponse
   */
  private ArrayList<QuestionDetailsResponse> convertToQuestionDetailsResponseArray(
      List<QuestionEntity> allQuestionsList) {

    ArrayList<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<>();
    for (QuestionEntity question : allQuestionsList) {
      QuestionDetailsResponse questionDetails = new QuestionDetailsResponse();
      questionDetails.setId(question.getUuid());
      questionDetails.setContent(question.getContent());
      questionDetailsResponses.add(questionDetails);
    }

    return questionDetailsResponses;
  }
}
