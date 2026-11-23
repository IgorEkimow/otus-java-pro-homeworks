package ru.otus.java.pro.homeworks.homework13;

import ru.otus.java.pro.homeworks.homework13.appcontainer.AppComponentsContainerImpl;
import ru.otus.java.pro.homeworks.homework13.appcontainer.api.AppComponentsContainer;
import ru.otus.java.pro.homeworks.homework13.config.AppConfig;
import ru.otus.java.pro.homeworks.homework13.services.GameProcessor;

@SuppressWarnings({"squid:S125", "squid:S106"})
public class App {
    public static void main(String[] args) {
        AppComponentsContainer container = new AppComponentsContainerImpl(AppConfig.class);
        GameProcessor gameProcessor = container.getAppComponent(GameProcessor.class);

        gameProcessor.startGame();
    }
}