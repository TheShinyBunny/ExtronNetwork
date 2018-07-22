package com.extron.network.api.utils;

public abstract class AnimatedText {

    private String baseText;

    public AnimatedText(String base) {
        this.baseText = base;
    }

    public String getBaseText() {
        return baseText;
    }

    public abstract String getHighlightColor();

    public abstract String getBaseColor();

    public abstract int getLetterSkip();

    public abstract int speed();

    public abstract boolean stepLetters();

    /** -1 = left, 0 = none, 1 = right, -11 both directions */
    public abstract int stepLettersDir();

    public String getFrame(int frame) {
        String f = "";
        f+= getBaseColor();
        char[] arr = baseText.toCharArray();
        if (stepLetters()) {
            int dir = stepLettersDir();
            frame %= arr.length;
            for (int i = 0; i < arr.length; i+=dir) {
                if (i >= frame && i < frame + getLetterSkip()) {
                    f += getHighlightColor() + arr[i] + getBaseColor();
                } else {
                    f += arr[i];
                }
            }
        }
        return f;
    }

    public int frameCount() {
        return baseText.length();
    }
}
