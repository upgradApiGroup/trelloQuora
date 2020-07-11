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


@Service
public class AnswerBusinessService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity, final String questionId, final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthToken = userDao.getUserAuthByToken(accessToken);
        QuestionEntity question = questionDao.getQuestionById(questionId);

        if(userAuthToken == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }
        if (question == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        answerEntity.setUserId(userAuthToken.getUserId());
        answerEntity.setQuestionId(question);

        return answerDao.createAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(AnswerEntity answerEntity, final String accessToken) throws AnswerNotFoundException, AuthorizationFailedException {

        UserAuthEntity userAuthToken = userDao.getUserAuthByToken(accessToken);
        AnswerEntity existingAnswer = answerDao.getAnswerByUuid(answerEntity.getUuid());

        if(userAuthToken == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }
        if (existingAnswer == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        if(!isUserOwner(userAuthToken, existingAnswer)){
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        answerEntity.setUserId(userAuthToken.getUserId());
        answerEntity.setDate(existingAnswer.getDate());
        answerEntity.setQuestionId(existingAnswer.getQuestionId());

        return answerDao.editAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(final String answerId, final String accessToken) throws AnswerNotFoundException, AuthorizationFailedException {

        UserAuthEntity userAuthToken = userDao.getUserAuthByToken(accessToken);
        AnswerEntity existingAnswer = answerDao.getAnswerByUuid(answerId);

        if(userAuthToken == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }
        if (existingAnswer == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        if(!isUserOwner(userAuthToken, existingAnswer) && !isUserAdmin(userAuthToken)){
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }

        return answerDao.deleteAnswer(existingAnswer);
    }

    public boolean isUserAdmin(UserAuthEntity userAuthToken){
        UserEntity loggedInUser = userAuthToken.getUserId();
        return loggedInUser.getRole().equals("admin");
    }

    public boolean isUserOwner(UserAuthEntity userAuthToken, AnswerEntity existingAnswer){
        UserEntity loggedInUser = userAuthToken.getUserId();
        return loggedInUser.equals(existingAnswer.getUserId());
    }
}
