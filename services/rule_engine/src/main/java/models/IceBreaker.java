package models;

import com.mindsmiths.armory.components.ActionGroupComponent;
import com.mindsmiths.armory.components.BaseSubmitButtonComponent;
import com.mindsmiths.armory.components.ImageComponent;
import com.mindsmiths.armory.components.PrimarySubmitButtonComponent;
import com.mindsmiths.armory.components.TitleComponent;
import com.mindsmiths.armory.templates.BaseTemplate;
import com.mindsmiths.armory.templates.TemplateGenerator;

import com.mindsmiths.sdk.utils.templating.Templating;
import com.mindsmiths.sdk.core.db.PrimaryKey;
import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IceBreaker {
    @PrimaryKey
    public final int QUESTIONS_COUNT = 3;
    public final int OPTIONS_COUNT = 3;
    Map<String, String> matchCorrectAnswers = new HashMap<>();
    Map<String, List<String>> questionOptions = new HashMap<>();
    Map<String, String> availableQuestions = new HashMap<>();
    List<String> shownQuestions = new ArrayList<>();
    String matchFirstName;
    int numOfCorrectAnswers;

    public IceBreaker(Map<String, String> matchAnsweredQuestions, List<EmployeeProfile> otherEmployees,
            String matchFirstName) {
        this.matchFirstName = matchFirstName;
        ArrayList<String> questions = new ArrayList<>(matchAnsweredQuestions.keySet());
        Collections.shuffle(questions);
        for (int i = 0; i < Math.min(questions.size(), QUESTIONS_COUNT); i++) {
            String questionId = questions.get(i);
            matchCorrectAnswers.put(questionId, matchAnsweredQuestions.get(questionId));
            questionOptions.put(questionId, new ArrayList<String>(List.of(matchAnsweredQuestions.get(questionId))));
            availableQuestions.put(questionId, Mitems.getText("questions.guessing-quiz." + questionId));
        }
        Collections.shuffle(otherEmployees);
        for (String questionId : questions)
            for (EmployeeProfile employee : otherEmployees) {
                String employeeAnswer = employee.getPersonalAnswers().getOrDefault(questionId, "");

                if (employeeAnswer.equals(""))
                    continue;

                if (!questionOptions.get(questionId).contains(employeeAnswer))
                    questionOptions.get(questionId).add(employeeAnswer);

                if (questionOptions.get(questionId).size() >= OPTIONS_COUNT)
                    break;
            }

        for (Map.Entry<String, List<String>> options : questionOptions.entrySet())
            if (options.getValue().size() < OPTIONS_COUNT)
                for (Option answers : Mitems.getOptions("questions.guessing-quiz.fake-answers"))
                    if (answers.getId().equals(options.getKey())) {
                        List<String> fakeAnswers = new ArrayList<>(List.of(answers.getText().split("\\|")));
                        Collections.shuffle(fakeAnswers);
                        for (String answer : fakeAnswers) {
                            if (!options.getValue().contains(answer))
                                options.getValue().add(answer);
                            if (options.getValue().size() >= OPTIONS_COUNT)
                                break;
                        }
                        break;
                    }
    }

    public String nextQuestionId() {
        for (String questionId : questionOptions.keySet())
            if (!shownQuestions.contains(questionId))
                return questionId;
        return "";
    }

    public void updateShownScreens(String questionId) {
        if (!shownQuestions.contains(questionId))
            shownQuestions.add(questionId);
    }

    public void updateNumOfCorrectAnswers(String answerValue) {
        if (answerValue.equals("correct-answer"))
            this.numOfCorrectAnswers++;
    }

    private List<BaseSubmitButtonComponent> buildAnswerButtons(String questionId) {
        List<PrimarySubmitButtonComponent> buttons = new ArrayList<>();
        List<BaseSubmitButtonComponent> castedButtons = new ArrayList<>();
        Collections.shuffle(questionOptions.get(questionId));
        for (String answer : questionOptions.get(questionId)) {
            if (answer.equals(matchCorrectAnswers.get(questionId)))
                buttons.add(new PrimarySubmitButtonComponent(questionId, answer, "correct-answer"));
            else
                buttons.add(new PrimarySubmitButtonComponent(questionId, answer, "wrong-answer"));
        }

        for (PrimarySubmitButtonComponent button : buttons)
            castedButtons.add((BaseSubmitButtonComponent) button);
        return castedButtons;
    }

    public BaseTemplate buildQuestionScreen(String questionId) {
        String question = Templating.recursiveRender(availableQuestions.get(questionId), Map.of(
                "firstName", matchFirstName));

        BaseTemplate screen = new TemplateGenerator(questionId)
                .addComponent("title", new TitleComponent(question))
                .addComponent(questionId, new ActionGroupComponent(buildAnswerButtons(questionId)));
        return screen;
    }

    public BaseTemplate buildResultScreen(String result) {
        Map<String, BaseTemplate> resultScreens = new HashMap<>();
        BaseTemplate correctAnswerScreen = new TemplateGenerator("correct-answer")
                .addComponent("title", new TitleComponent(Mitems.getText("ice-breaker.correct-answer.text")))
                .addComponent("image", new ImageComponent(Mitems.getText("ice-breaker.correct-answer.image-path")))
                .addComponent("submit", new PrimarySubmitButtonComponent("submit",
                        Mitems.getOptions("ice-breaker.correct-answer.button")[0].getText(),
                        Mitems.getOptions("ice-breaker.correct-answer.button")[0].getId()));
        resultScreens.put("correct-answer", correctAnswerScreen);

        BaseTemplate wrongAnswerScreen = new TemplateGenerator("wrong-answer")
                .addComponent("title", new TitleComponent(Mitems.getText("ice-breaker.wrong-answer.text")))
                .addComponent("image", new ImageComponent(Mitems.getText("ice-breaker.wrong-answer.image-path")))
                .addComponent("submit", new PrimarySubmitButtonComponent("submit",
                        Mitems.getOptions("ice-breaker.wrong-answer.button")[0].getText(),
                        Mitems.getOptions("ice-breaker.wrong-answer.button")[0].getId()));
        resultScreens.put("wrong-answer", wrongAnswerScreen);
        return resultScreens.get(result);
    }

    public String getScoreFirstScreenId() {
        return "info-screen";
    }

    public Map<String, BaseTemplate> buildScoreScreens() {
        String firstScreenId = getScoreFirstScreenId();
        Map<String, BaseTemplate> scoreScreens = Map.of(
                firstScreenId, new TemplateGenerator(firstScreenId)
                        .addComponent("title", new TitleComponent(Mitems.getText("ice-breaker.message-screen.text")))
                        .addComponent("submit", new PrimarySubmitButtonComponent(
                                "submit",
                                Mitems.getOptions("ice-breaker.message-screen.button")[0].getText(),
                                "score-screen")),
                "score-screen", new TemplateGenerator("score-screen")
                        .addComponent("title", new TitleComponent(
                                Templating.recursiveRender(Mitems.getText("ice-breaker.score-screen.my-result"),
                                        Map.of("numOfCorrectAnswers", numOfCorrectAnswers))))
                        .addComponent("answers", new TitleComponent(
                                Templating.recursiveRender(Mitems.getText("ice-breaker.score-screen.match-results"),
                                        Map.of("firstName", matchFirstName,
                                                "answerOne", matchCorrectAnswers.get(shownQuestions.get(0)),
                                                "answerTwo", matchCorrectAnswers.get(shownQuestions.get(1)),
                                                "answerThree", matchCorrectAnswers.get(shownQuestions.get(2))))))
                        .addComponent("submit", new PrimarySubmitButtonComponent(
                                "submit",
                                Mitems.getOptions("ice-breaker.score-screen.button")[0].getText(),
                                "final-screen")),
                "final-screen", new TemplateGenerator("final-screen")
                        .addComponent("title", new TitleComponent(Mitems.getText("ice-breaker.final-screen.text"))));
        return scoreScreens;
    }
}