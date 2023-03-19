package utils;

import com.mindsmiths.armory.Screen;
import com.mindsmiths.armory.component.BaseComponent;
import com.mindsmiths.armory.component.SubmitButton;

import java.util.List;

public class ScreenUtils {

    public static List<Screen> removeScreens(List<Screen> screens, List<String> screensToRemove) {

        for (int i = 0; i < screens.size(); i++) {
            if (screensToRemove.contains(screens.get(i).getId())) {
                if (i > 0)
                    for (BaseComponent component : screens.get(i - 1).getComponents())
                        if (component instanceof SubmitButton button && button.getNextScreen().equals(screens.get(i).getId()))
                            button.setNextScreen(i == screens.size() - 1 ? screens.get(0).getId() : screens.get(i + 1).getId());
                screens.remove(i);
                i--;
            }
        }
        return screens;
    }
}
