package com.javainis.survey.dao;

import com.javainis.survey.entities.Question;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

public abstract class QuestionDAO {
    @Inject
    protected EntityManager manager;

    public void create(Question question) {
        manager.persist(question);
    }

    public List<Question> getAll(){
        return manager.createNamedQuery("Question.findAll", Question.class).getResultList();
    }

    public Question findById(Long id){
        return manager.createNamedQuery("Question.findById", Question.class).getSingleResult();
    }
    public List<Question> findBySurveyId(Long surveyId){
        return manager.createNamedQuery("Question.findBySurveyId", Question.class).setParameter("surveyId", surveyId).getResultList();
    }
}
