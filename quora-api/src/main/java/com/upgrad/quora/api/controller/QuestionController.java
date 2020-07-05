package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    /* Create Question Controller*/
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        final QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDateCreated(ZonedDateTime.now());

        final QuestionEntity createdQuestion = questionBusinessService.createQuestion(questionEntity, authorization);
        QuestionResponse createQuestionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(createQuestionResponse, HttpStatus.CREATED);
    }

    /* Get All Questions */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, JSONException {
        final List<QuestionEntity> allQuestionsList = questionBusinessService.getAllQuestions(authorization);

        ArrayList<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<>();

        for (QuestionEntity question: allQuestionsList) {
            QuestionDetailsResponse questionDetails = new QuestionDetailsResponse();
            questionDetails.setId(String.valueOf(question.getId()));
            questionDetails.setContent(question.getContent());
            questionDetailsResponses.add(questionDetails);
        }
        return new ResponseEntity<ArrayList>(questionDetailsResponses, HttpStatus.OK);
    }
}
