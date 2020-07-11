package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity editAnswer(AnswerEntity answerEntity){
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    public AnswerEntity deleteAnswer(AnswerEntity answerEntity){
        entityManager.remove(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswerByUuid(final String answerUuid) {
        try{
            return entityManager.createNamedQuery("getAnswerByUuid", AnswerEntity.class)
                    .setParameter("answerUuid", answerUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<AnswerEntity> getAllAnswersByQuestion(final String questionUuid) {
        try{
            return entityManager.createNamedQuery("getAnswersByQuestion", AnswerEntity.class)
                    .setParameter("questionUuid", questionUuid).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
