package models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@AllArgsConstructor
@Data
public class AvaiablePersonalQuestions {
    private ArrayList<String> personalQuestions = new ArrayList<String>();
    private int numOfPersonalQuestions = 6;
    private String currentQuestion;
    private int currentQuestionIndex;
    
    AvaiablePersonalQuestions(){
        for(int questionNum = 1; questionNum <= numOfPersonalQuestions; questionNum++){
            this.personalQuestions.add("question" + questionNum);
        }
        this.currentQuestion = personalQuestions.get(0);
        this.currentQuestionIndex = 0;
    }

    public void popAnsweredQuestion(){
        this.personalQuestions.remove(this.currentQuestion);
    }

    public void nextQuestion(){
        if (currentQuestionIndex == personalQuestions.size() - 1){
            this.currentQuestion = personalQuestions.get(0);
            this.currentQuestionIndex = 0;
        }
        else{
            this.currentQuestion = personalQuestions.get(currentQuestionIndex + 1);
            this.currentQuestionIndex += 1;
        }
    }
}