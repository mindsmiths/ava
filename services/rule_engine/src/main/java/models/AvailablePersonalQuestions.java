package models;

import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;
import com.mindsmiths.sdk.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;

@AllArgsConstructor
@Data
public class AvailablePersonalQuestions {
    private String id = Utils.randomGenerator();
    private ArrayList<String> personalQuestions = new ArrayList<>();
    private int numOfPersonalQuestions = 14;
    private String currentQuestion;

    public AvailablePersonalQuestions() {
        for (Option option : Mitems.getOptions("questions.personal-questions.questions"))
            personalQuestions.add(option.getId());
        Collections.shuffle(personalQuestions);
        this.currentQuestion = personalQuestions.get(0);
    }

    public void popQuestion() {
        this.personalQuestions.remove(0);
        this.currentQuestion = personalQuestions.get(0);
    }

    public void nextQuestion() {
        String tmp = personalQuestions.get(0);
        this.personalQuestions.remove(0);
        this.personalQuestions.add(tmp);
        this.currentQuestion = personalQuestions.get(0);
    }
}