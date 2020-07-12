package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
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
public class AnswerController {

  @Autowired
  private AnswerBusinessService answerBusinessService;

  /**
   * Create Answer Controller
   * @param answerRequest - accepts AnswerRequest object
   * @param accessToken   - accepts access token of signed in user
   * @param questionUuid  - accepts the question ID for which answer has to be created
   *
   * Creates an AnswerEntity object. Calls AnswerBusinessService, passes the object
   *
   * @return The UUID of the answer created and the HTTP Status, as part of AnswerResponse object
   * @exception AuthorizationFailedException if invalid credentials are used by the requester
   * @exception InvalidQuestionException if invalid question ID is passed
   */

  @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

  public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest,
      @PathVariable("questionId") final String questionUuid,
      @RequestHeader("authorization") final String accessToken)
      throws InvalidQuestionException, AuthorizationFailedException {

    final AnswerEntity answerEntity = new AnswerEntity();

    answerEntity.setUuid(UUID.randomUUID().toString());
    answerEntity.setDate(ZonedDateTime.now());
    answerEntity.setAns(answerRequest.getAnswer());

    final AnswerEntity createdAnswerEntity = answerBusinessService
        .createAnswer(answerEntity, questionUuid, accessToken);
    AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid())
        .status("ANSWER CREATED");

    return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
  }

  /**
   * Edit Answer Controller
   * @param answerEditRequest - accepts AnswerEditRequest Object
   * @param accessToken       - accepts access token of signed in user
   * @param answerId          - accepts answer ID from path passed on as a variable
   *
   * Updates the content of the answer with the specified answer ID, with the
   * content passed on in the EditAnswerRequest object
   *
   * @return The UUID of the answer updated and the HTTP Status, as part of AnswerEditResponse object
   * @exception AuthorizationFailedException if invalid credentials are used by the requester
   * @exception AnswerNotFoundException if invalid answer ID is used by the requester
   */

  @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

  public ResponseEntity<AnswerEditResponse> editAnswerContent(
      final AnswerEditRequest answerEditRequest,
      @PathVariable("answerId") final String answerId,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, AnswerNotFoundException {

    AnswerEntity answerEntity = new AnswerEntity();
    answerEntity.setUuid(answerId);
    answerEntity.setAns(answerEditRequest.getContent());

    AnswerEntity editedAnswerEntity = answerBusinessService.editAnswer(answerEntity, accessToken);
    AnswerEditResponse answerEditResponse = new AnswerEditResponse()
        .id(editedAnswerEntity.getUuid()).status("ANSWER EDITED");

    return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
  }

  /**
   * Delete Answer Controller
   * @param accessToken - accepts access token of signed in user
   * @param answerId    - accepts answer ID from path passed on as a variable
   *
   * Calls the AnswerBusinessService and passes the answerId to be deleted
   *
   * @return The UUID of the answer deleted and the HTTP Status as part of AnswerDeleteResponse object
   * @exception AuthorizationFailedException if invalid credentials are used by the requester
   * @exception AnswerNotFoundException if invalid answer Id is used by the requester
   */

  @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

  public ResponseEntity<AnswerDeleteResponse> deleteAnswer(
      @PathVariable("answerId") final String answerId,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, AnswerNotFoundException {

    AnswerEntity deletedAnswer = answerBusinessService.deleteAnswer(answerId, accessToken);
    AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse()
        .id(deletedAnswer.getUuid()).status("ANSWER DELETED");

    return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
  }

  /**
   * Get All Answers to a question
   * @param questionUuid  - accepts the question ID from the path passed on as a variable
   * @param accessToken   - accepts access token of signed in user
   *
   * Calls the AnswerBusinessService and passes the question ID whose answers are to be fetched
   *
   * @return Returns all answers of specific question from the DB along with the HTTP Status
   * as a List of AnswerDetailsResponse objects, after validating the user request
   * @throws AuthorizationFailedException if invalid credentials are used by the requester
   * @throws InvalidQuestionException if invalid question ID is passed
   */

  @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

  public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(
      @PathVariable("questionId") String questionUuid,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, InvalidQuestionException {

    List<AnswerDetailsResponse> answerDetailsResponseList = new ArrayList<>();

    List<AnswerEntity> allAnswersByQuestion = answerBusinessService
        .getAllAnswersByQuestion(questionUuid, accessToken);

    for (AnswerEntity answer : allAnswersByQuestion) {
      String answerUuid = answer.getUuid();
      String answerContent = answer.getAns();
      String questionContent = answer.getQuestion().getContent();

      AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse().id(answerUuid)
          .answerContent(answerContent).questionContent(questionContent);

      answerDetailsResponseList.add(answerDetailsResponse);
    }

    return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponseList,
        HttpStatus.OK);
  }
}
