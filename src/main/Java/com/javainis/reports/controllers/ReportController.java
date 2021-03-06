package com.javainis.reports.controllers;

import com.javainis.reports.api.*;
import com.javainis.reports.mybatis.dao.SurveyMapper;
import com.javainis.reports.mybatis.model.*;
import com.javainis.user_management.controllers.UserController;
import com.javainis.user_management.dao.UserTypeDAO;
import lombok.Getter;
import org.omnifaces.cdi.Param;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Named
@ViewScoped
@Getter
public class ReportController implements Serializable {
    private static final int TIMEOUT_LIMIT = 60;

    @Inject
    private UserController userController;

    @Inject
    private SurveyMapper surveyMapper;

    @Inject
    @Param(pathIndex = 0)
    private String surveyUrl;

    private Survey survey;

    private int refreshCount = 0;

    private boolean canAccess = false;

    private boolean timeout = false;

    private Map<Question, QuestionReport> questionReports;

    private Map<Question, Future<Void>> reports;

    @PostConstruct
    private void init() {
        survey = surveyMapper.selectByUrl(surveyUrl);
        if (survey == null) {
            canAccess = false;
            return;
        }
        /* Check if user is logged in */
        if(userController.getUser().getUserID() == null){
            canAccess = false;
            return;
        }

        if( (userController.getUser().getUserID().equals(survey.getAuthorId())) || //author
                (userController.getUser().getUserType().getId() == UserTypeDAO.USER_TYPE_ADMIN) || //admin
                (survey.getIsPublic() && userController.getUser() != null)){ //public survey and logged in user
            canAccess = true;
        }

        /* Link questions and controllers */
        questionReports = new HashMap<>();
        reports = new HashMap<>();
        for (Question question : survey.getQuestions()) {
            if(!question.getAnswers().isEmpty()){
                QuestionReport report = null;
                Future<Void> future;
                if (question instanceof FreeTextQuestion) {
                    report = javax.enterprise.inject.spi.CDI.current().select(TextQuestionReport.class).get();
                } else if (question instanceof IntervalQuestion) {
                    report = javax.enterprise.inject.spi.CDI.current().select(IntervalQuestionReport.class).get();
                } else if (question instanceof SingleChoiceQuestion) {
                    report = javax.enterprise.inject.spi.CDI.current().select(SingleChoiceQuestionReport.class).get();
                } else if (question instanceof MultipleChoiceQuestion) {
                    report = javax.enterprise.inject.spi.CDI.current().select(MultiChoiceQuestionReport.class).get();
                }
                report.setQuestion(question);
                future = report.generateReportAsync();
                reports.put(question, future);
                questionReports.put(question, report);
            }
        }
    }

    public void checkProgress() {
        timeout = true;
        refreshCount++;
        if(refreshCount >= TIMEOUT_LIMIT){
            timeout = true;
            return;
        }
        for (Future future : reports.values()) {
            if (!future.isDone()) {
                timeout = false;
            }
        }
    }
}
