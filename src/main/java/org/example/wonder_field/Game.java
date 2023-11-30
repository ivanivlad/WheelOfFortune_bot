package org.example.wonder_field;

import org.example.MyBot;
import org.example.wonder_field.player.Player;
import org.example.wonder_field.player.PlayerAnswer;

import java.util.Scanner;
import java.util.StringJoiner;

public class Game {
    private static final int PLAYERS_TOTAL = 1;
    private static final int ROUNDS_TOTAL = 2;
    private static final int ROUNDS_OF_GROUP = 1;
    private static final int INDEX_OF_FINAL_ROUND = 1;
    public Scanner scanner;
    private final String[] questionList;
    private final String[] answerList;
    private final Tableau tableau;
    private final Yakubovich yakubovich;
    private final Player[] winners;
    private Player player;
    private final long chatID;
    private final MyBot bot;
    public enum ACTIONS{
        START,
        JOIN_PLAYER, WAIT_ANSWER, WAIT_LETTER, WAIT_WORD,

    }
    private ACTIONS currentAction;

    public Game(MyBot bot, long chatID){
        questionList = new String[ROUNDS_TOTAL];
        answerList = new String[ROUNDS_TOTAL];
        yakubovich = new Yakubovich();
        tableau = new Tableau();
        winners = new Player[ROUNDS_OF_GROUP];
        scanner = new Scanner(System.in);
        this.chatID = chatID;
        this.bot = bot;
    }

    public static boolean isFinalRound(int round){
        return round == INDEX_OF_FINAL_ROUND;
    }

    public void setNewQuestion(int numOfQuestion, String question, String answer){
        questionList[numOfQuestion - 1] = question;
        answerList[numOfQuestion - 1] = answer.toUpperCase();
    }

    public void printAllQuestionForMock(){
        for (int i = 0; i < Game.ROUNDS_TOTAL; i++) {
            System.out.println(questionList[i]);
            System.out.println(answerList[i]);
        }
    }

     //Мок-объект. Заполняет список вопросов и ответов
    public void initMock() {

        setNewQuestion(1, "Что не губит людей, в отличии от воды?", "Пиво");
        setNewQuestion(2, "Что мешает бетон, пока бригадир жрёт самогон?", "Бетономешалка");
        /*setNewQuestion(3, "Птица семейства Ястребиные?", "Орел");
        setNewQuestion(4, "Имя первого косманавта?", "Юра");*/

        say("Запуск игры \"Поле Чудес\"");

    }

    private void joinPlayer(){
        say(String.format("Игрок представьтесь: имя,город. Например: Иван,Москва"));
        waitAction(ACTIONS.JOIN_PLAYER);
    }

    public boolean isRoundFinished(){
        return !tableau.hasUnknownLetters();
    }

    public void nextAttempt(){
        say(String.format("Ход игрока %s, %s\n", player.name, player.city));
        say("Чтобы назвать букву нажмите /letter, готовы назвать слово - /word");
        waitAction(ACTIONS.WAIT_ANSWER);
    }

    public void playRound(int roundIndex){
        boolean isFinalRound = isFinalRound(roundIndex);
        String question = questionList[roundIndex];
        tableau.init(answerList[roundIndex]);
        invitePlayers(isFinalRound);
        askQuestion(question);
        say(tableau.printTableau());
        nextAttempt();
    }

    public void start(){
        beforeStartGame();
        currentAction = ACTIONS.START;
        nextStep();

    }

    public void listen(String action){

        switch (currentAction) {
            case JOIN_PLAYER: {
                String[] newAnswerSplit = action.split(",");
                if (newAnswerSplit.length == 2){
                    player = new Player(newAnswerSplit[0].strip(), newAnswerSplit[1].strip());
                    nextStep();
                }
                break;
            }
            case WAIT_ANSWER: {
                if (action.equals("/letter")) {
                    say("Якубович: Назовите букву");
                    waitAction(ACTIONS.WAIT_LETTER);
                } else if (action.equals("/word")) {
                    say("Якубович: Назовите слово");
                    waitAction(ACTIONS.WAIT_WORD);
                } else {
                    say("Некорректное значение, введите /letter или /word");
                }
                break;
            }
            case WAIT_LETTER: {
                PlayerAnswer newAnswer = sayLetter(action);
                if (isCorrectLetter(newAnswer.answer, tableau.correctAnswer)) {
                    tableau.openLetter(newAnswer.answer);
                    say(tableau.printTableau());
                    if (isRoundFinished()){
                        sayWin(true);
                    }else {
                        nextAttempt();
                    }
                } else {
                    nextAttempt();}

                break;
            }
            case WAIT_WORD: {
                PlayerAnswer newAnswer = sayWord(action);
                if (isCorrectWord(newAnswer.answer, tableau.correctAnswer)) {
                    tableau.openWord();
                    say(tableau.printTableau());
                    if (isRoundFinished()){
                        sayWin(true);
                    }else {
                        nextAttempt();
                    }
                }else {
                    nextAttempt();}
                break;
            }
        }
    }

    private void nextStep(){
        //do something
        //текущая команда
        switch (currentAction){
            case START:
            {
                joinPlayer();
                break;
            }
            case JOIN_PLAYER:
            {
                say("Начнем!");
                playRound(0);
                break;
            }
        }
    }

    private void say(String text){
        bot.sendMessage(text, chatID);
    }

    private void waitAction(ACTIONS action){
        currentAction = action;
    }

    public void beforeStartGame(){
        say("Якубович: Здравствуйте, уважаемые дамы и господа! Пятница! В эфире капитал-шоу «Поле чудес»!");
    }

    public void beforeEndGame(){
        say("Мы прощаемся с вами ровно на одну неделю! Здоровья вам, до встречи!");
    }

    public void invitePlayers(boolean isFinalRound){
        if (isFinalRound) {
           say(String.format("Якубович: приглашаю победителей групповых этапов: %s\n", player.name));
        } else{
            say(String.format("Якубович: приглашаю игрока: %s\n", player.name));
        }
    }

    public void askQuestion(String question){
        say(String.format("Якубович: Внимание вопрос!\n \"%s\"\n", question));
    }

    public void sayWin(boolean isFinalRound){
        if (isFinalRound) {
            say(String.format("Якубович: И перед нами победитель Капитал шоу поле чудес! Это %s из %s\n", player.name, player.city));
            say(String.format("Для начала новой игры напиши /play"));
        } else{
            say(String.format("Якубович: Молодец! %s из %s проходит в финал!\n", player.name, player.city));
        }
    }

    public boolean isCorrectLetter(String letter, String answer){
        boolean isCorrect = answer.contains(letter);
        if (isCorrect) {
            say("Якубович: Есть такая буква, откройте ее!");
        } else{
            say("Якубович: Нет такой буквы! Следующий игрок, крутите барабан!");
        }
        say("__________________________________");
        return isCorrect;
    }

    public boolean isCorrectWord(String playerAnswer, String answer){
        boolean isCorrect = answer.equals(playerAnswer);
        if (isCorrect) {
            say(String.format("Якубович: %s! Абсолютно верно!\n", answer));
        } else{
            say(String.format("Якубович: Неверно! Следующий игрок!"));
        }
        say("__________________________________");
        return isCorrect;
    }

    public PlayerAnswer sayLetter(String newLetter){
        say(String.format("Игрок %s: буква %s \n", player.name, newLetter));
        return new PlayerAnswer(PlayerAnswer.TypeAnswer.LETTER, newLetter.toUpperCase());
    }

    public PlayerAnswer sayWord(String newWord){
        say(String.format("Игрок %s: слово %s \n", player.name, newWord));
        return new PlayerAnswer(PlayerAnswer.TypeAnswer.WORD, newWord.toUpperCase());
    }

}
