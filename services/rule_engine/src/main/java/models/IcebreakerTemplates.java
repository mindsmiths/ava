package models;

import com.mindsmiths.armory.Screen;
import com.mindsmiths.armory.component.*;
import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;

import java.util.*;

public class IcebreakerTemplates {
    public static final int NUMBER_OF_QUIZ_ANSWERS = 3;

    public static List<Screen> icebreakerQuizScreens(List<IcebreakerQuestion> questions, List<String> otherAnswers) {

        List<Screen> screens = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            String nextScreenId = i == questions.size() - 1 ? "quiz-end" : questions.get(i + 1).getQuestionId();
            screens.add(new Screen(questions.get(i).getQuestionId())
                    .add(new Title(questions.get(i).getQuizQuestionText())));

            for (QuizOption quizOption : getAnswerOptions(questions.get(i), otherAnswers)) {
                String nextScreen = quizOption.isCorrect() ? "correct" : "wrong";
                screens.get(i).add(new SubmitButton("quiz-option", quizOption.getAnswer(), nextScreen));
            }
            screens.add(new Screen("correct")
                    .add(new Title(Mitems.getText("ice-breaker.quiz-question.correct-answer")))
                    .add(new Image(Mitems.getText("ice-breaker.quiz-question.correct-photo")))
                    .add(new SubmitButton("next-question", Mitems.getText("ice-breaker.quiz-question.button-text"), nextScreenId)));
            screens.add(new Screen("wrong")
                    .add(new Title(Mitems.getText("ice-breaker.quiz-question.wrong-answer")))
                    .add(new Image(Mitems.getText("ice-breaker.quiz-question.wrong-photo")))
                    .add(new SubmitButton("next-question", Mitems.getText("ice-breaker.quiz-question.button-text"), nextScreenId)));
        }
        screens.add(new Screen("quiz-end")
                .add(new Title(Mitems.getText("ice-breaker.quiz-end.title")))
                .add(new Description(Mitems.getText("ice-breaker.quiz-end.description")))
                .add(new SubmitButton("finish-personal-quiz", Mitems.getText("ice-breaker.personal-questions-end.button-text"), "quiz-results")));

        screens.add(new Screen("quiz-results")
                .add(new Title(Mitems.getText("ice-breaker.quiz-results.title")))
                .add(new Description(Mitems.getText("ice-breaker.quiz-results.description")))
                .add(new SubmitButton("finish-personal-quiz", Mitems.getText("ice-breaker.quiz-results.button-text"), "final-screen")));

        return screens;
    }

    public static List<Screen> icebreakerQuestionScreens() {
        List<Screen> screens = new ArrayList<>();

        List<IcebreakerQuestion> icebreakerQuestions = new ArrayList<>(Arrays.stream(Mitems.getOptions("ice-breaker.questions.questions"))
                .map(option -> new IcebreakerQuestion(option.getId(), option.getText())).toList());

        Collections.shuffle(icebreakerQuestions);

        Option startButton = Mitems.getOptions("ice-breaker.personal-questions-start.buttons")[0];
        screens.add(new Screen("personal-quiz-start")
                .add(new Title(Mitems.getText("ice-breaker.personal-questions-start.title")))
                .add(new Description(Mitems.getText("ice-breaker.personal-questions-start.description")))
                .add(new SubmitButton(startButton.getId(), startButton.getText(), icebreakerQuestions.get(0).getQuestionId())));

        for (int i = 0; i < icebreakerQuestions.size(); i++) {
            String nextScreen = i == icebreakerQuestions.size() - 1 ?
                    icebreakerQuestions.get(0).getQuestionId() : icebreakerQuestions.get(i + 1).getQuestionId();
            screens.add(new Screen(icebreakerQuestions.get(i).getQuestionId())
                    .add(new Title(icebreakerQuestions.get(i).getQuestionText()))
                    .add(new TextArea("icebreaker-answer-" + icebreakerQuestions.get(i).getQuestionId())));


            for (Option button : Mitems.getOptions("ice-breaker.personal-question-screen.buttons")) {
                screens.get(screens.size() - 1).add(new SubmitButton(button.getId(), button.getText(), nextScreen));
            }
        }
        screens.add(new Screen("personal-questions-end")
                .add(new Title(Mitems.getText("ice-breaker.personal-questions-end.title")))
                .add(new SubmitButton("personal-questions-end", Mitems.getText("ice-breaker.personal-questions-end.button-text"), "final-screen")));
        return screens;
    }

    private static List<QuizOption> getAnswerOptions(IcebreakerQuestion icebreakerQuestion, List<String> otherAnswers) {  // TODO: check for duplicates
        List<QuizOption> answerOptions = new ArrayList<>();
        List<String> otherOptions = otherAnswers.size() >= NUMBER_OF_QUIZ_ANSWERS ?
                selectNRandom(otherAnswers) : fillWithDummyAnswers(otherAnswers, icebreakerQuestion.getDummyAnswers());
        for (String otherOption : otherOptions)
            answerOptions.add(new QuizOption(otherOption, false));

        answerOptions.add(new QuizOption(icebreakerQuestion.getAnswer(), true));
        Collections.shuffle(answerOptions);
        return answerOptions;
    }

    private static List<String> selectNRandom(List<String> list) {
        Random rand = new Random();

        if (list.size() == NUMBER_OF_QUIZ_ANSWERS) return list;
        List<String> result = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_QUIZ_ANSWERS; i++) {
            int randomIndex = rand.nextInt(list.size());
            result.add(list.get(randomIndex));
            list.remove(randomIndex);
        }
        return result;
    }

    private static List<String> fillWithDummyAnswers(List<String> answers, List<String> dummyAnswers) {
        Collections.shuffle(dummyAnswers);
        for (int i = 0; i < NUMBER_OF_QUIZ_ANSWERS; i++) {
            if (answers.size() >= NUMBER_OF_QUIZ_ANSWERS) break;
            answers.add(dummyAnswers.get(i));
        }
        return answers;
    }
}
