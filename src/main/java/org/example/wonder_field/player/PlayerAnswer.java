package org.example.wonder_field.player;

public class PlayerAnswer {

    public enum TypeAnswer{
        LETTER,
        WORD
    }
    public TypeAnswer typeAnswer;
    public String answer;

    public PlayerAnswer(TypeAnswer type, String newAnswer)
    {
        typeAnswer = type;
        answer = newAnswer;
    }

}
