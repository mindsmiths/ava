package models;

import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.sdk.core.db.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IcebreakerQuestion {
    @PrimaryKey
    public String questionId;
    public String questionText;

    public String answer;
    public List<String> dummyAnswers = new ArrayList<>();
    public String quizQuestionText;

    public IcebreakerQuestion(String mitemsSlug, String dummyAnswers){
        questionId = mitemsSlug;
        questionText = Mitems.getText("ice-breaker.questions." + mitemsSlug);
        this.dummyAnswers = Arrays.asList(dummyAnswers.split(   ","));
    }

    public String getQuizQuestionFromMitems() {
        quizQuestionText = Mitems.getText("ice-breaker.questions." + questionId + "-quiz");
        return quizQuestionText;
    }
}
