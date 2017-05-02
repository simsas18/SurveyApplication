package com.javainis.reports.controllers;

import com.javainis.reports.api.MultiChoiceQuestionReport;
import com.javainis.reports.mybatis.model.Choice;
import com.javainis.reports.mybatis.model.MultipleChoiceAnswer;
import com.javainis.reports.mybatis.model.MultipleChoiceQuestion;
import com.javainis.reports.mybatis.model.Question;
import lombok.Getter;
import org.apache.deltaspike.core.api.future.Futureable;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;

import javax.ejb.AsyncResult;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

@Named
@Dependent
@Alternative
public class MultiChoiceChartController implements MultiChoiceQuestionReport, Serializable {
    @Getter
    MultipleChoiceQuestion multipleChoiceQuestion;
    @Getter
    List<MultipleChoiceAnswer> multipleChoiceAnswers;
    @Getter
    private HorizontalBarChartModel model;

    public void init() {
        createBarModel();
    }

    private void createBarModel() {
        model = new HorizontalBarChartModel();

        ChartSeries answers = new ChartSeries();
        answers.setLabel("Answers");
        for(Choice q: multipleChoiceQuestion.getChoices())
        {
            int count = 0;
            for(MultipleChoiceAnswer a: multipleChoiceAnswers)
                    if(a.getId() == q.getId())
                        count++;

            answers.set(q.getText(), count);
        }

        model.addSeries(answers);

        model.setTitle(multipleChoiceQuestion.getText());
        model.setShowPointLabels(false);

        Axis xAxis = model.getAxis(AxisType.X);
        xAxis.setLabel("Count");
        xAxis.setMin(0);

        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setLabel("Choices");
    }

    @Override
    public String getTemplateName() {
        return "multi-choice-show.xhtml";
    }

    @Override
    public void setQuestion(Question question) {
        if(question instanceof MultipleChoiceQuestion) {
            multipleChoiceQuestion = (MultipleChoiceQuestion) question;
            multipleChoiceAnswers = (List<MultipleChoiceAnswer>) (List<?>) multipleChoiceQuestion.getAnswers();
        }
        else {
            System.out.println("MultipleChoiceQuestion was not set successfully");
        }
    }

    @Override
    @Futureable
    public Future<Void> generateReportAsync() {
        init();
        return new AsyncResult<>(null);
    }
}
