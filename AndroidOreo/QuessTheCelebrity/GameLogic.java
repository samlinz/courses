package com.samlinz.androidcourse.quesstheceleb;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Class which encloses game instance's inner state and game logic.
 */
public class GameLogic {
    // Constants.
    private static final String LogName = GameLogic.class.getSimpleName();
    private static final int QUEUE_LENGTH = 5;

    // Keep count of celebrities.
    private Queue<String> pastCelebrities;
    private Celebrity currentCelebrity;
    private List<Celebrity> celebrities;

    // Bound UI.
    private NotifiableUserInterface ui;

    // Game configuration.
    private int choiceCount;

    // Keep track of score.
    private int correctCount;
    private int totalCount;

    private GameLogic() {
        pastCelebrities = new LinkedList<>();
    }

    /**
     * Get {@link this#choiceCount} random names of which one is correct.
     *
     * @return List of celebrity names of which one is correct and the rest are wrong.
     */
    public List<String> getChoices() {
        List<String> choices = new LinkedList<>();
        choices.add(currentCelebrity.getName());

        Random rng = new Random();

        while (choices.size() < choiceCount) {
            final int randomChoice = rng.nextInt(celebrities.size());
            Celebrity wrongCeleb = celebrities.get(randomChoice);
            final String wrongCelebName = wrongCeleb.getName();

            // Do not include same name twice.
            if (pastCelebrities.contains(wrongCelebName) || choices.contains(wrongCelebName))
                continue;

            choices.add(wrongCelebName);
        }

        Collections.shuffle(choices);
        return choices;
    }

    /**
     * Commit an answer to current question.
     *
     * @param name Name of the quessed celebrity as a string.
     */
    public void answer(String name) {
        totalCount++;

        String correctChoice = currentCelebrity.getName();

        if (name.toLowerCase().equals(currentCelebrity.getName().toLowerCase())) {
            correctCount++;
            correctChoice = null;
        }

        getNewState(correctChoice);
    }

    /**
     * Build a new state and pass it to the bound UI instance.
     *
     * @param previousCorrectChoice Correct choice from previous question, non-null if answer was wrong!
     */
    public void getNewState(String previousCorrectChoice) {
        // Choose new celeb.
        Celebrity celebrity = getNew();
        currentCelebrity = celebrity;

        // Get choices.
        List<String> choices = getChoices();

        // Fetch bitmap for new celeb.
        getCurrentBitmap(bitmap -> {
            // Bind new state to UI.

            CelebrityGameState newState = new CelebrityGameState();
            newState.bitmap = bitmap;
            newState.choices = choices;
            newState.correct = correctCount;
            newState.total = totalCount;
            newState.correctChoice = previousCorrectChoice;
            newState.logic = this;

            ui.pushState(newState);
        });
    }

    /**
     * Get or lazily download the bitmap for current celebrity and pass it to the callback.
     *
     * @param imageLoadCallback Callback which receives the downloaded or cached Bitmap as an argument.
     */
    public void getCurrentBitmap(Celebrity.ImageLoadTaskCallback imageLoadCallback) {
        currentCelebrity.getImage(imageLoadCallback);
    }

    /**
     * Bind user interface to game logic. New game states will be passed to the instance.
     * @param activity
     */
    public void bindUI(QuessCelebrityGameActivity activity) {
        this.ui = activity;
        getNewState(null);
    }

    /**
     * Get a new celebrity pseudo-randomly.
     *
     * @return New instance of {@link Celebrity} who is not in past queue.
     */
    public Celebrity getNew() {
        Random rng = new Random();
        boolean chosen = false;

        Celebrity newCelebrity = null;

        while (!chosen) {
            final int randomIndex = rng.nextInt(celebrities.size());
            newCelebrity = celebrities.get(randomIndex);
            if (pastCelebrities.contains(newCelebrity.getName()))
                continue;

            chosen = true;
        }

        // Shorten the queue.
        pastCelebrities.add(newCelebrity.getName());
        while (pastCelebrities.size() > QUEUE_LENGTH)
            pastCelebrities.poll();

        return newCelebrity;
    }

    /**
     * Build a new game instance.
     *
     * @param celebrities List of all celebrities.
     * @return
     */
    public static GameLogic getGame(List<Celebrity> celebrities) {
        GameLogic gameLogic = new GameLogic();
        gameLogic.celebrities = celebrities;
        gameLogic.choiceCount = 4;

        return gameLogic;
    }
}
