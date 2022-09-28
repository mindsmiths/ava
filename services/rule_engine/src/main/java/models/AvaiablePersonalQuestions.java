package models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

import com.mindsmiths.sdk.core.db.PrimaryKey;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AvaiablePersonalQuestions {
    @PrimaryKey
    private String avaId;
    private ArrayList<String> personalQuestions = new ArrayList<String>();
    private int numOfPersonalQuestions = 6;
    private String currentQuestion;
    private int currentQuestionIndex;
    
    public AvaiablePersonalQuestions(String avaId){
        for(int questionNum = 1; questionNum <= numOfPersonalQuestions; questionNum++){
            this.personalQuestions.add("question" + questionNum);
        }
        this.currentQuestion = personalQuestions.get(0);
        this.currentQuestionIndex = 0;
        this.avaId = avaId;
    }

    public void popAnsweredQuestion(){
        this.personalQuestions.remove(currentQuestion);
        this.currentQuestion = personalQuestions.get(currentQuestionIndex);
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