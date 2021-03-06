package com.javainis.data_export_import.interfaces;

import com.javainis.survey.entities.Survey;
import com.javainis.survey.entities.SurveyResult;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

public interface DataImporter {
    Future<Survey> importSurvey(File selectedFile, Survey survey);
    Future<List<SurveyResult>> importAnswers(File selectedFile, Survey survey, List<SurveyResult> surveyResultList);
}
