package it.unibo.mvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {
    //private static final int MIN = 0;
    //private static final int MAX = 100;
    //private static final int ATTEMPTS = 10;
    private int min, max, attempts;

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    private void inputFile(){
        final File input = new File("src/main/resources/config.yml");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(input), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e1) {
            throw new IllegalStateException("Config file not found");
        }
        String line = "";
        int index;
        for (int i = 0; i < 3; i++){
            try {
                line = reader.readLine();
                index = line.indexOf(" ") + 1;
                if (i == 0) {
                    min = Integer.parseInt(line.substring(index));
                } else if (i == 1) {
                    max = Integer.parseInt(line.substring(index));
                } else if (i == 2) {
                    attempts = Integer.parseInt(line.substring(index));
                }
            } catch (IOException e) {
            System.out.println("reading error"); // NOPMD
            }
        }
        try {
                reader.close();
        } catch (IOException e){
            System.out.println("closing error"); //NOPMD
        }
    }

    /**
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        inputFile();
        this.model = new DrawNumberImpl(min, max, attempts);
        assert model != null;
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * @param args
     *            ignored
     * @throws FileNotFoundException 
     */
    public static void main(final String... args) throws FileNotFoundException {
        new DrawNumberApp(new DrawNumberViewImpl(), new DrawNumberViewImpl());
    }

}
