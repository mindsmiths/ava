package models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

import com.mindsmiths.sdk.core.db.PrimaryKey;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AvailablePersonalQuestions {
    @PrimaryKey
    private String avaId;
    private ArrayList<String> personalQuestions = new ArrayList<String>();
    private int numOfPersonalQuestions = 14;
    private String currentQuestion;

    public AvailablePersonalQuestions(String avaId) {
        for (int questionNum = 1; questionNum <= numOfPersonalQuestions; questionNum++) {
            this.personalQuestions.add("question" + questionNum);
        }
        this.currentQuestion = personalQuestions.get(0);
        this.avaId = avaId;
    }

    public void popQuestion() {
        this.personalQuestions.remove(0);
        this.currentQuestion = personalQuestions.get(0);
    }

    public void nextQuestion() {
        String temp = personalQuestions.get(0);
        this.personalQuestions.remove(0);
        this.personalQuestions.add(temp);
        this.currentQuestion = personalQuestions.get(0);
    }
}