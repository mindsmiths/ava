package models;

import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GuessingQuizQuestions {
    public final int LUNCH_QUIZ_QUESTIONS_COUNT = 3;
    public final int LUNCH_QUIZ_OPTIONS_COUNT = 3;
    Map<String, String> correctAnswers = new HashMap<>();
    Map<String, List<String>> questionOptions = new HashMap<>();
    Map<String, String> availableQuestions = new HashMap<>();


    public GuessingQuizQuestions(Map<String, String> matchAnsweredQuestions, List<EmployeeProfile> otherEmployees) {
        ArrayList<String> questions = new ArrayList<>(matchAnsweredQuestions.keySet());
        Collections.shuffle(questions);
        for (int i = 0; i < Math.min(questions.size(), LUNCH_QUIZ_QUESTIONS_COUNT); i++) {
            String questionId = questions.get(i);
            correctAnswers.put(questionId, matchAnsweredQuestions.get(questionId));
            questionOptions.put(questionId, List.of(matchAnsweredQuestions.get(questionId)));
            availableQuestions.put(questionId, Mitems.getText("questions.guessing-quiz." + questionId));
        }
        Collections.shuffle(otherEmployees);
        for (String questionId : questions)
            for (EmployeeProfile employee : otherEmployees) {
                String employeeAnswer = employee.getPersonalAnswers().getOrDefault(questionId, "");

                if (employeeAnswer.equals("")) continue;

                if (!questionOptions.get(questionId).contains(employeeAnswer))
                    questionOptions.get(questionId).add(employeeAnswer);

                if (questionOptions.get(questionId).size() >= LUNCH_QUIZ_OPTIONS_COUNT) break;
            }

        for (Map.Entry<String, List<String>> options : questionOptions.entrySet()) {
            if (options.getValue().size() < LUNCH_QUIZ_OPTIONS_COUNT) {
                for (Option answers : Mitems.getOptions("onboarding.guessing-quiz.fake-answers")) {
                    if (answers.getId().equals(options.getKey())) {
                        List<String> fakeAnswers = new ArrayList<>(List.of(answers.getText().split("\\|")));
                        Collections.shuffle(fakeAnswers);
                        for (String answer : fakeAnswers) {
                            if (!options.getValue().contains(answer))
                                options.getValue().add(answer);
                            if (options.getValue().size() >= LUNCH_QUIZ_OPTIONS_COUNT)
                                break;
                        }
                        break;
                    }
                }
            }
        }


    }
}
