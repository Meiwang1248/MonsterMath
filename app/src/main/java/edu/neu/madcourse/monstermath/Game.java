package edu.neu.madcourse.monstermath;

import java.util.HashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Game {
    String operation; // + - * / mix
    String difficultyLevel; // easy medium hard
    Boolean singleMode; // single player is true, else false
    int curStage; // single problem, like 2+3
    int score; // current score in this round

    int curNumber1; // randomly generate the first number
    int curNumber2; // randomly generate the second number
    String curOperator; // + - * / mix in this round
    HashSet options; // multiple choice
    int bonus;  // 2 seconds will give 5 extra points; 5 seconds will give 2 extra points; over 5s no bonus
    int curAnswer; // right answer for the current question

    Random rand = new Random();

    int curPlayerAnswer;

    public Game(String operation, String difficultyLevel, Boolean singleMode, int curStage, int score) {
        this.operation = operation;
        this.difficultyLevel = difficultyLevel;
        this.singleMode = singleMode;
        this.curStage = curStage;
        this.score = score;
    }

    public String getOperation() {
        return operation;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public Boolean getSingleMode() {
        return singleMode;
    }

    public int getCurStage() {
        return curStage;
    }

    public int getCurScore() {
        return score;
    }

    public void gameStart() {

        for(int i=0; i<10; i++){
            generateOneStage();
            curStage++;
            // after finish each question, we will reset the following:
            curNumber1 = 0;
            curNumber2 = 0;
            curAnswer = 0;
            options.clear();
        }
        // 游戏结束， display your total score is ***
    }


    // Yihui part 4
    public void generateOneStage(){
        // bounds 还没算，还没加上时间
        // https://blog.csdn.net/lintianlin/article/details/40540831
        generateOptions();
        bonus = 5;
        Timer timer = new Timer();
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                //if时间过去2秒， bonus -3=2

                //if时间又过去3秒，bonus -2=0
                bonus -= 3;
            }
        };

        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                //if时间过去2秒， bonus -3=2

                //if时间又过去3秒，bonus -2=0
                bonus -= 2;
            }
        };

        timer.schedule(task1,2000);

        timer.schedule(task2,5000);



        if(curPlayerAnswer == curAnswer){
            score += 10;
            score += bonus;
        } else {
            options.remove(curPlayerAnswer);
        }
    }

    public void generateNumbers() {
        if (operation == "+" || operation == "-") {
            if (difficultyLevel == "easy") {
                int upperBound = 11;
                curNumber1 = rand.nextInt(upperBound);
                curNumber2 = rand.nextInt(upperBound);
            } else if (difficultyLevel == "medium") {
                int upperBound = 21;
                curNumber1 = rand.nextInt(upperBound);
                curNumber2 = rand.nextInt(upperBound);
            } else if (difficultyLevel == "hard") {
                int upperBound = 101;
                curNumber1 = rand.nextInt(upperBound);
                curNumber2 = rand.nextInt(upperBound);
            }

        }

        if (operation == "*" || operation == "/") {
            if (difficultyLevel == "easy") {
                int upperBound = 6;
                curNumber1 = rand.nextInt(upperBound);
                curNumber2 = rand.nextInt(upperBound);
            } else if (difficultyLevel == "medium") {
                int upperBound = 11;
                curNumber1 = rand.nextInt(upperBound);
                curNumber2 = rand.nextInt(upperBound);
            } else if (difficultyLevel == "hard") {
                int upperBound = 16;
                curNumber1 = rand.nextInt(upperBound);
                curNumber2 = rand.nextInt(upperBound);
            }

        }
    }

    public void generateOptions(){
        generateNumbers();
        options = new HashSet();
        if (operation == "+") {
            curAnswer = curNumber1 + curNumber2;
        } else if (operation == "-") {
            curAnswer = curNumber1 - curNumber2;
        } else if (operation == "*") {
            curAnswer = curNumber1 * curNumber2;
        } else if (operation == "/") {
            curAnswer = curNumber1 / curNumber2;
        }

        options.add(curAnswer);
        for(int i = 0; i < 4; i ++){
            int option = rand.nextInt(101);
            options.add(option);
        }
    }
}





