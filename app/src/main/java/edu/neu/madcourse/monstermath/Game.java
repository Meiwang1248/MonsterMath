package edu.neu.madcourse.monstermath;

import java.util.HashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Game {
    String operation; // + - * / mix
    String difficultyLevel; // easy medium hard
    boolean singleMode; // single player is true, else false
    int curStage; // single problem, like 2+3
    int score; // current score in this round

    int curNumber1; // randomly generate the first number
    int curNumber2; // randomly generate the second number
    String curOperator; // + - * / in this round
    HashSet<Integer> options; // generate multiple choices
    int bonus;  // 2 seconds will give 5 extra points; 5 seconds will give 2 extra points; over 5s no bonus
    int curAnswer; // right answer for the current question

    Random rand = new Random();

    int curPlayerAnswer;

    public Game(String operation, String difficultyLevel, boolean singleMode, int curStage, int score) {
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

    /**
     * Generates one game stage.
     */
    public void generateOneStage(){
        // @https://blog.csdn.net/lintianlin/article/details/40540831
        generateOptions();
        bonus = 5;
        Timer timer = new Timer();
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                //if时间过去2秒， bonus -3=2
                bonus -= 3;
            }
        };

        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                //if时间又过去3秒，bonus -2=0
                bonus -= 2;
            }
        };

        // 这里已经有时间了？
        timer.schedule(task1,2000);

        timer.schedule(task2,5000);

        if(curPlayerAnswer == curAnswer){
            score += 10;
            score += bonus;
        } else {
            options.remove(curPlayerAnswer);
        }
    }

    /**
     * Generates two numbers for the math operation based on difficulty level.
     */
    public void generateNumbers() {
        int upperBound = 0;
        if (operation.equals("add") || operation.equals("subtract")) {
            if (difficultyLevel.equals("easy")) {
                upperBound = 10;
            } else if (difficultyLevel.equals("medium")) {
                upperBound = 20;
            } else if (difficultyLevel.equals("hard")) {
                upperBound = 100;
            }
        } else if (operation.equals("multiply") || operation.equals("divide")) {
            if (difficultyLevel.equals("easy")) {
                upperBound = 5;
            } else if (difficultyLevel.equals("medium")) {
                upperBound = 10;
            } else if (difficultyLevel.equals("hard")) {
                upperBound = 15;
            }
        }

        // 区别对待divide，因为可能出现随机数无法整除的现象，必须保证两个数能够整除
        if (operation.equals("divide")) {
            // plus 1 to ensure 0 not included
            curNumber2 = rand.nextInt(upperBound) + 1;
            // find the quotient
            int quotient = 1;
            if (difficultyLevel.equals("easy")) {
                quotient = rand.nextInt(3) + 1;
            } else if (difficultyLevel.equals("medium")) {
                quotient = rand.nextInt(5) + 1;
            } else if (difficultyLevel.equals("hard")) {
                quotient = rand.nextInt(10) + 1;
            }
            // make sure curNumber2 can evenly divide curNumber1
            curNumber1 = curNumber2 * quotient;
        } else {
            curNumber1 = rand.nextInt(upperBound) + 1;
            curNumber2 = rand.nextInt(upperBound) + 1;
        }
    }

    /**
     * Generates five options for each question.
     */
    public void generateOptions(){
        generateNumbers();
        options = new HashSet();
        if (operation.equals("add")) {
            curAnswer = curNumber1 + curNumber2;
        } else if (operation.equals("subtract")) {
            curAnswer = curNumber1 - curNumber2;
        } else if (operation.equals("multiply")) {
            curAnswer = curNumber1 * curNumber2;
        } else if (operation.equals("divide")) {
            curAnswer = curNumber1 / curNumber2;
        }

        // add the correct answer to options
        options.add(curAnswer);

        // ensure there are 5 options
        while (options.size() < 5) {
            int option = curAnswer + (rand.nextInt(11) - 5) ;
            options.add(option);
        }
    }
}





