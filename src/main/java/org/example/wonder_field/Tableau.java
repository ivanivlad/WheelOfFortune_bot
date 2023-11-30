package org.example.wonder_field;

import java.util.Arrays;
import java.util.StringJoiner;

public class Tableau {
    private static final Character UNKNOWN_SYMBOL = '▬';
    String correctAnswer;
    char[] letters;

    public void init(String Answer){
        correctAnswer = Answer;
        letters = UNKNOWN_SYMBOL.toString().repeat(Answer.length()).toCharArray();
    }

    public String printTableau(){
        if (attributesIsEmpty()){
            return "";
        }
        StringJoiner joiner = new StringJoiner(" ");
        for (char letter: letters) {
            joiner.add(Character.toString(letter));
        }
        joiner.add("\n");
        return  joiner.toString();
    }

    public void openLetter(String letter){
        if (attributesIsEmpty()){
            return;
        }
        int indexOf = -1;
        while (correctAnswer.indexOf(letter, indexOf + 1) != -1)
        {
            indexOf = correctAnswer.indexOf(letter, indexOf + 1);
            letters[indexOf] = correctAnswer.charAt(indexOf);
        }
    }

    public void openWord(){
        letters = correctAnswer.toCharArray();
    }

    public boolean hasUnknownLetters(){
        return Arrays.binarySearch(letters, UNKNOWN_SYMBOL) > 0 ;
    }

    public boolean attributesIsEmpty(){
        return letters.length == 0 || correctAnswer.isEmpty();
    }
}
