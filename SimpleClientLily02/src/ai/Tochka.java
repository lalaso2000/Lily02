/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import gameElements.Board;
import gameElements.Game;
import gameElements.GameResources;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * <h1> Tochka </h1>
 *
 * <br> ニューラルネットワークを使うらしい
 *
 * @author niwatakumi
 */
public class Tochka extends TajimaLabAI {

    private int handCount = 0;

    private double[][] middle1Weight;
    private double[][] middle2Weight;
    private double[][] outWeight;

    private String middle1Out;
    private String middle2Out;
    private double[] output;

    public static Action[] ACTIONS = {
        new Action("S", "1-1", "F"),
        new Action("S", "1-1", "G"),
        new Action("S", "1-1", "M"),
        new Action("S", "2-1", "F"),
        new Action("S", "2-1", "G"),
        new Action("S", "2-2"),
        new Action("S", "3-1"),
        new Action("S", "3-2"),
        new Action("S", "3-3"),
        new Action("S", "3-4", "F"),
        new Action("S", "3-4", "G"),
        new Action("S", "4-1"),
        new Action("S", "4-2"),
        new Action("S", "4-3"),
        new Action("S", "4-4"),
        new Action("S", "4-5"),
        new Action("S", "5-1"),
        new Action("S", "5-2"),
        new Action("S", "5-3"),
        new Action("S", "5-4"),
        new Action("S", "5-5"),
        new Action("P", "6-1"),
        new Action("P", "6-2", "FF"),
        new Action("P", "6-2", "FG"),
        new Action("P", "6-2", "GG"),
        new Action("P", "6-3"),
        new Action("P", "7-1", "T0"),
        new Action("P", "7-1", "T1"),
        new Action("P", "7-1", "T2"),
        new Action("P", "7-1", "T3"),
        new Action("P", "7-1", "T4"),
        new Action("P", "7-1", "T5")
    };

//    private static final int PREFETCH_MAX_LEVEL = 8;    // 先読みの最高階数
//
//    private double[][] flaskWeight = new double[12][3];
//    private double[][] gearWeight = new double[12][3];
//    private double[][][] moneyWeight = new double[12][11][3];
//    private double[][] trendWeight = new double[12][3];
//    private double[][] scoreWeight = new double[12][3];
//    private double[][] machineWeight = new double[12][22];
//    private double[][] startPlayerWeight = new double[12][3];
//
//    private final List<String> PLACE_FOR_PROFESSOR = Arrays.asList("6-1", "6-2", "6-3", "7-1");
    /**
     * コンストラクタ for client
     *
     * @param game
     * @param weightFilePath 重み付け係数のファイル
     */
    public Tochka(Game game, String weightFilePath) {
        super(game);
        this.myName = "Tochka";
        loadWeights(weightFilePath);
    }

    public Tochka(Game game) {
        super(game);
        this.myName = "Tochka";

        // とりあえずランダムで生成
        this.middle1Weight = new double[60][16];
        Random rand = new Random();
        for (int i = 0; i < 60; i++) {
            for (int j = 0; j < 16; j++) {
                this.middle1Weight[i][j] = 2 * rand.nextDouble() - 1.0;
            }
        }
        this.middle2Weight = new double[16][10];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 10; j++) {
                this.middle2Weight[i][j] = 2 * rand.nextDouble() - 1.0;
            }
        }
        this.outWeight = new double[10][32];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 32; j++) {
                this.outWeight[i][j] = 2 * rand.nextDouble() - 1.0;
            }
        }
    }

    /**
     * 係数をファイルから読み込む ファイル形式は、1-60行目が1層目、61-76行目が2層目、77-86行目が出口層の係数
     */
    private void loadWeights(String filePath) {
        // 係数を初期化
        this.middle1Weight = new double[60][16];
        this.middle2Weight = new double[16][10];
        this.outWeight = new double[10][32];
        ArrayList<String> lines = new ArrayList<>();

        // ファイルを読み込む
        File file = new File(filePath);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                // 空行はスキップ
                if (line.isEmpty()) {
                    continue;
                }
                // "#"から始まる行はコメント
                if (line.startsWith("#")) {
                    continue;
                }
                // 空行でない場合、保存
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            System.err.println("ファイルが存在しません。");
        } catch (IOException e) {
            System.err.println("エラーが発生しました。");
        }

        // 行数が異なる
        if (lines.size() != 86) {
            System.err.println("csvの行数が不正です。");
        }

        // 数値を読み込む
        int lineCount = 0;
        for (String line : lines) {
            String[] nums = line.split(",");
            int numCount = 0;
            // 各行の数字の数を確認
            if (0 <= lineCount && lineCount < 60) {
                if (nums.length != 16) {
                    System.err.println((lineCount + 1) + "行目の列数が異なります");
                }
            }
            if (60 <= lineCount && lineCount < 76) {
                if (nums.length != 10) {
                    System.err.println((lineCount + 1) + "行目の列数が異なります");
                }
            }
            if (76 <= lineCount && lineCount < 86) {
                if (nums.length != 32) {
                    System.err.println((lineCount + 1) + "行目の列数が異なります");
                }
            }
            for (String num : nums) {
                try {
                    double n = Double.parseDouble(num);
                    if (0 <= lineCount && lineCount < 60) {
                        this.middle1Weight[lineCount][numCount] = n;
                    }
                    if (60 <= lineCount && lineCount < 76) {
                        this.middle2Weight[lineCount-60][numCount] = n;
                    }
                    if (76 <= lineCount && lineCount < 86) {
                        this.outWeight[lineCount-76][numCount] = n;
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("数値じゃないものが含まれています。");
                }
                numCount++;
            }
            lineCount++;
        }
    }

    private void printWeight(Game game) {
//        Integer season = this.convertSeasonStrToSeasonNum(game.getSeason());
//        if (season == null) {
//            return;
//        }
//
//        this.addMessage("==========================");
//        this.addMessage("========== weights ==========");
//        this.addMessage("==========================");
//        String line = "Flask {\n\t";
//        line += this.flaskWeight[season][0] + ", ";
//        line += this.flaskWeight[season][1] + ", ";
//        line += this.flaskWeight[season][2] + "\n},\n";
//
//        line += "Gear {\n\t";
//        line += this.gearWeight[season][0] + ",";
//        line += this.gearWeight[season][1] + ",";
//        line += this.gearWeight[season][2] + "\n},\n";
//
//        line += "Money {\n";
//        for (int i = 0; i < 11; i++) {
//            line += "\t" + i + " {\n";
//            line += "\t\t";
//            line += this.moneyWeight[season][i][0] + ",";
//            line += this.moneyWeight[season][i][1] + ",";
//            line += this.moneyWeight[season][i][2] + "\n\t},\n";
//        }
//        line += "},\n";
//
//        line += "Trend {\n\t";
//        line += this.trendWeight[season][0] + ",";
//        line += this.trendWeight[season][1] + ",";
//        line += this.trendWeight[season][2] + "\n},\n";
//
//        line += "Score {\n\t";
//        line += this.scoreWeight[season][0] + ",";
//        line += this.scoreWeight[season][1] + ",";
//        line += this.scoreWeight[season][2] + "\n},\n";
//
//        line += "Machine {\n\t";
//        line += this.machineWeight[season][0] + ",";
//        line += this.machineWeight[season][1] + ",";
//        line += this.machineWeight[season][2] + ",";
//        line += this.machineWeight[season][3] + ",";
//        line += this.machineWeight[season][4] + ",";
//        line += this.machineWeight[season][5] + ",";
//        line += this.machineWeight[season][6] + ",";
//        line += this.machineWeight[season][7] + ",";
//        line += this.machineWeight[season][8] + ",";
//        line += this.machineWeight[season][9] + ",";
//        line += this.machineWeight[season][10] + ",";
//        line += this.machineWeight[season][11] + ",";
//        line += this.machineWeight[season][12] + ",";
//        line += this.machineWeight[season][13] + ",";
//        line += this.machineWeight[season][14] + ",";
//        line += this.machineWeight[season][15] + ",";
//        line += this.machineWeight[season][16] + ",";
//        line += this.machineWeight[season][17] + ",";
//        line += this.machineWeight[season][18] + ",";
//        line += this.machineWeight[season][19] + ",";
//        line += this.machineWeight[season][20] + ",";
//        line += this.machineWeight[season][21] + "\n},\n";
//
//        line += "StartPlayer {\n\t";
//        line += this.startPlayerWeight[season][0] + ",";
//        line += this.startPlayerWeight[season][1] + ",";
//        line += this.startPlayerWeight[season][2] + "\n},\n";
//
//        addMessage(line);
    }

    @Override
    protected void enemyPlay(String text) {
        super.enemyPlay(text);
        this.handCount += 1;
    }

    /**
     * 仮想で打つ（季節の更新をするかどうか変更可）
     *
     * @param game ゲームボード
     * @param playerNum 次のプレイヤー
     * @param action アクション
     * @param seasonChangeable 季節を更新するかどうか
     * @return 打った盤面
     */
    private Game clonePlay(Game game, int playerNum, Action action, boolean seasonChangeable) {
        Game cloneGame = game.clone();
        // 配置可能でなければnullを返す
        if (cloneGame.canPutWorker(playerNum, action.place, action.worker, action.option) == false) {
            return null;
        }

        // アクションしてみる
        cloneGame.play(playerNum, action.place, action.worker, action.option);
        // 季節が変わるなら更新
        if (seasonChangeable == true && cloneGame.getGameState() == Game.STATE_SEASON_END) {
            cloneGame.changeNewSeason();
        }
        return cloneGame;
    }

    private Action neuralNet(NeuralInput input) {

        // 1層目 畳み込む
        String in = input.getRowVal();
        for (int j = 0; j < 16; j++) {
            double sum = 0.0;
            for (int i = 0; i < 60; i++) {
                sum += Integer.parseInt(in.substring(i, i + 1)) * middle1Weight[i][j];
            }
            if (sum >= 0) {
                this.middle1Out += "1";
            } else {
                this.middle1Out += "0";
            }
        }

        addMessage("[1st]: " + middle1Out);

        // 2層目 畳み込む
        for (int j = 0; j < 10; j++) {
            double sum = 0.0;
            for (int i = 0; i < 16; i++) {
                sum += Integer.parseInt(middle1Out.substring(i, i + 1)) * middle2Weight[i][j];
            }
            if (sum >= 0) {
                this.middle2Out += "1";
            } else {
                this.middle2Out += "0";
            }
        }

        addMessage("[1st]: " + middle2Out);

        // 出口 畳み込む
        output = new double[32];
        double total = 0;
        for (int j = 0; j < 32; j++) {
            double sum = 0.0;
            for (int i = 0; i < 10; i++) {
                sum += Integer.parseInt(middle2Out.substring(i, i + 1)) * outWeight[i][j];
            }
            output[j] = sum;
        }

        // 順位づけする
        Map<Action, Double> map = new HashMap<>();
        for (int i = 0; i < 32; i++) {
            map.put(ACTIONS[i], output[i]);
        }
        List<Map.Entry<Action, Double>> listEntrys = new ArrayList<Entry<Action, Double>>(map.entrySet());
        Collections.sort(listEntrys, (Entry<Action, Double> o1, Entry<Action, Double> o2) -> o2.getValue().compareTo(o1.getValue()));
        addMessage("====output====");
        for (Entry<Action, Double> entry : listEntrys) {
            addMessage(entry.getKey() + ":" + entry.getValue());
        }

        // 上から順に打てるものを確認する
        for (Entry<Action, Double> entry : listEntrys) {
            Action a = entry.getKey();
            String w = a.worker;
            String p = a.place;
            String o = a.option;
            if (this.gameBoard.canPutWorker(myNumber, p, w, o)) {
                return a;
            } else if (this.gameBoard.canPutWorker(myNumber, p, "P", o)) {
                return new Action("P", p, o);
            }
        }
        if (this.gameBoard.canPutWorker(myNumber, "S", "1-1", "M")) {
            return new Action("S", "1-1", "M");
        } else {
            return new Action("P", "1-1", "M");
        }
    }

    /**
     * 考えるフェーズ 手を打つところまで実装
     */
    @Override
    protected void think() {

        this.addMessage("==========================");
        this.addMessage("========== thinking ==========");
        this.addMessage("==========================");

        this.handCount += 1;

        Action bestAction = null;

        NeuralInput input = new NeuralInput(gameBoard, myNumber, handCount);

        addMessage("inputLength: " + input.getLength());
        addMessage("rowInput: " + input.getRowVal());

        middle1Out = "";
        middle2Out = "";

        bestAction = neuralNet(input);

        this.addMessage("===========================");
        this.addMessage("========== think end ==========");
        this.addMessage("===========================");

        this.addMessage("* Best Action is " + bestAction);

        // 最適解を打つ
        this.putWorker(bestAction);
    }

    /**
     * 季節が変わった時に呼び出される
     */
    @Override
    protected void seasonChanged() {
        printWeight(gameBoard);
    }

    /**
     * プレイヤー番号が通知された時呼び出される
     */
    @Override
    protected void playerNumDecided() {

    }

}
