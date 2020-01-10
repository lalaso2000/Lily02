/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h1>648の変数を保存したりしなかったりするクラス</h1>
 *
 * @author niwatakumi
 */
public class GeneticIndividual implements Comparable<GeneticIndividual> {

    public static final double GAUSSIAN_SIGMA = 0.01;
    // 各層の重み
    private double[][] middle1Weight = new double[INPUT_LENGTH][MIDDLE_1_LENGTH];;
    private double[][] middle2Weight = new double[MIDDLE_1_LENGTH][MIDDLE_2_LENGTH];
    private double[][] outWeight = new double[MIDDLE_2_LENGTH][OUTPUT_LENGTH];
    // 各層の長さ
    public static final int INPUT_LENGTH = 60;
    public static final int MIDDLE_1_LENGTH = 16;
    public static final int MIDDLE_2_LENGTH = 10;
    public static final int OUTPUT_LENGTH = 32;
    // 重みを一列にしたもの：遺伝子
    public static final int CHROMOSOME_LENGTH = INPUT_LENGTH * MIDDLE_1_LENGTH + MIDDLE_1_LENGTH * MIDDLE_2_LENGTH + MIDDLE_2_LENGTH * OUTPUT_LENGTH;
    private double[] genes = new double[CHROMOSOME_LENGTH];
    private int win = 0;
    private int totalScore = 0;
    private boolean newcomer;

    /**
     * middle1Weight / middle2Weight / outputWeightを一列にする
     */
    private void serialize() {
        for (int i = 0; i < INPUT_LENGTH; i++) {
            for (int j = 0; j < MIDDLE_1_LENGTH; j++) {
                this.genes[i * MIDDLE_1_LENGTH + j] = this.middle1Weight[i][j];
            }
        }
        for (int i = 0; i < MIDDLE_1_LENGTH; i++) {
            for (int j = 0; j < MIDDLE_2_LENGTH; j++) {
                this.genes[i * MIDDLE_2_LENGTH + j + INPUT_LENGTH * MIDDLE_1_LENGTH] = this.middle2Weight[i][j];
            }
        }
        for (int i = 0; i < MIDDLE_2_LENGTH; i++) {
            for (int j = 0; j < OUTPUT_LENGTH; j++) {
                this.genes[i * OUTPUT_LENGTH + j + INPUT_LENGTH * MIDDLE_1_LENGTH + MIDDLE_1_LENGTH * MIDDLE_2_LENGTH] = this.outWeight[i][j];
            }
        }
    }

    /**
     * ランダムに生成する
     */
    public GeneticIndividual() {
        // とりあえずランダムで生成
        this.middle1Weight = new double[INPUT_LENGTH][MIDDLE_1_LENGTH];
        Random rand = new Random();
        for (int i = 0; i < INPUT_LENGTH; i++) {
            for (int j = 0; j < MIDDLE_1_LENGTH; j++) {
                this.middle1Weight[i][j] = 2 * rand.nextDouble() - 1.0;
            }
        }
        this.middle2Weight = new double[MIDDLE_1_LENGTH][MIDDLE_2_LENGTH];
        for (int i = 0; i < MIDDLE_1_LENGTH; i++) {
            for (int j = 0; j < MIDDLE_2_LENGTH; j++) {
                this.middle2Weight[i][j] = 2 * rand.nextDouble() - 1.0;
            }
        }
        this.outWeight = new double[MIDDLE_2_LENGTH][OUTPUT_LENGTH];
        for (int i = 0; i < MIDDLE_2_LENGTH; i++) {
            for (int j = 0; j < OUTPUT_LENGTH; j++) {
                this.outWeight[i][j] = 2 * rand.nextDouble() - 1.0;
            }
        }
        this.newcomer = true;
        // 係数を一列にする
        this.serialize();
    }

    /**
     * 配列を受け取って生成する
     *
     * @param genes 染色体になる配列
     */
    public GeneticIndividual(double[] genes) {
        if (genes.length != CHROMOSOME_LENGTH) {
            return;
        }
        this.genes = genes;
        // 各種重みに振り分ける
        for (int i = 0; i < INPUT_LENGTH * MIDDLE_1_LENGTH; i++) {
            this.middle1Weight[i / MIDDLE_1_LENGTH][i % MIDDLE_1_LENGTH] = genes[i];
        }
        for (int i = 0; i < MIDDLE_1_LENGTH * MIDDLE_2_LENGTH; i++) {
            this.middle2Weight[i / MIDDLE_2_LENGTH][i % MIDDLE_2_LENGTH] = genes[i + INPUT_LENGTH * MIDDLE_1_LENGTH];
        }
        for (int i = 0; i < MIDDLE_2_LENGTH * OUTPUT_LENGTH; i++) {
            this.outWeight[i / OUTPUT_LENGTH][i % OUTPUT_LENGTH] = genes[i + INPUT_LENGTH * MIDDLE_1_LENGTH + MIDDLE_1_LENGTH * MIDDLE_2_LENGTH];
        }
        this.newcomer = false;
    }

    /**
     * csvファイルから読み込んで生成
     *
     * @param filePath
     */
    public GeneticIndividual(String filePath) {
        this.loadWeights(filePath);
        this.newcomer = false;
    }

    /**
     * コピーコンストラクタ
     *
     * @param g
     */
    public GeneticIndividual(GeneticIndividual g) {
        System.arraycopy(g.genes, 0, this.genes, 0, CHROMOSOME_LENGTH);
    }

    /**
     * 係数をファイルから読み込む ファイル形式は、1行に各季節、フラスコ→ギア→…→スタプレの順
     */
    private void loadWeights(String filePath) {
        ArrayList<String> lines = new ArrayList<>();

        // ファイルを読み込む
        File file = new File(filePath);
        if (!file.exists()){
            String basename = filePath.substring(0,filePath.lastIndexOf('.'));
            String rFilePath = basename + "(r).csv";
            file = new File(rFilePath);
        }
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
            System.err.println("ファイル" + file.getAbsolutePath() + "が存在しません。");
        } catch (IOException e) {
            System.err.println("エラーが発生しました。");
        }

        // 行数が異なる
        if (lines.size() != INPUT_LENGTH + MIDDLE_1_LENGTH + MIDDLE_2_LENGTH) {
            System.err.println("csvの行数が不正です。");
        }

        // 数値を読み込む
        int lineCount = 0;
        for (String line : lines) {
            String[] nums = line.split(",");
            int numCount = 0;
            // 各行の数字の数を確認
            if (0 <= lineCount && lineCount < INPUT_LENGTH) {
                if (nums.length != MIDDLE_1_LENGTH) {
                    System.err.println((lineCount + 1) + "行目の列数が異なります");
                }
            }
            if (INPUT_LENGTH <= lineCount && lineCount < INPUT_LENGTH + MIDDLE_1_LENGTH) {
                if (nums.length != MIDDLE_2_LENGTH) {
                    System.err.println((lineCount + 1) + "行目の列数が異なります");
                }
            }
            if (76 <= lineCount && lineCount < INPUT_LENGTH + MIDDLE_1_LENGTH + MIDDLE_2_LENGTH) {
                if (nums.length != OUTPUT_LENGTH) {
                    System.err.println((lineCount + 1) + "行目の列数が異なります");
                }
            }
            for (String num : nums) {
                try {
                    double n = Double.parseDouble(num);
                    if (0 <= lineCount && lineCount < INPUT_LENGTH) {
                        this.middle1Weight[lineCount][numCount] = n;
                    }
                    if (INPUT_LENGTH <= lineCount && lineCount < INPUT_LENGTH + MIDDLE_1_LENGTH) {
                        this.middle2Weight[lineCount - INPUT_LENGTH][numCount] = n;
                    }
                    if (76 <= lineCount && lineCount < INPUT_LENGTH + MIDDLE_1_LENGTH + MIDDLE_2_LENGTH) {
                        this.outWeight[lineCount - INPUT_LENGTH - MIDDLE_1_LENGTH][numCount] = n;
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("数値じゃないものが含まれています。");
                }
                numCount++;
            }
            lineCount++;
        }
        this.serialize();
    }

    public void outputCSV(String filePath) {
        // ファイルを読み込む
        File file = new File(filePath);

        // ファイルに書き込む
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            pw.println(this);
            pw.println("# 1-1F,1-1G,1-1M,2-1F,2-1G,2-2,3-1,3-2,3-3,3-4F,3-4G,4-1,4-2,4-3,4-4,4-5,5-1,5-2,5-3,5-4,5-5,6-1,6-2FF,6-2FG,6-2GG,6-3,7-1 T0,7-1 T1,7-1 T2,7-1 T3,7-1 T4,7-1 T5");
            pw.close();
        } catch (IOException ex) {
            System.err.println("ファイル書き込みエラー");
        }
    }

    /**
     * ２点交叉する<br>
     *
     * @param g1 親
     * @param g2 親
     * @return 子
     */
    public static GeneticIndividual[] crossover(GeneticIndividual g1, GeneticIndividual g2) {
        Random r = new Random();
        // 交差開始地点
        int top = r.nextInt(CHROMOSOME_LENGTH);
        // 交差する長さ
        int length = r.nextInt(CHROMOSOME_LENGTH - top);

//        System.out.println("top: " + top + "  length: " + length);
        // 子供を作る
        double[] newGenes1 = new double[CHROMOSOME_LENGTH];
//        for (int i = 0; i < newGenes1.length; i++) {
//            double k = r.nextDouble() * 0.2 - 0.1;
//            newGenes1[i] += k;
//            if (newGenes1[i] > 1.0) {
//                newGenes1[i] = 1.0;
//            } else if (newGenes1[i] < -1.0) {
//                newGenes1[i] = -1.0;
//            }
//        }
        System.arraycopy(g1.genes, 0, newGenes1, 0, g1.genes.length);
        System.arraycopy(g2.genes, top, newGenes1, top, length);
        GeneticIndividual newG1 = new GeneticIndividual(newGenes1);

        double[] newGenes2 = new double[CHROMOSOME_LENGTH];
//        for (int i = 0; i < newGenes2.length; i++) {
//            double k = r.nextGaussian() * GAUSSIAN_SIGMA;
//            newGenes2[i] += k;
//            if (newGenes2[i] > 1.0) {
//                newGenes2[i] = 1.0;
//            } else if (newGenes2[i] < -1.0) {
//                newGenes2[i] = -1.0;
//            }
//        }
        System.arraycopy(g2.genes, 0, newGenes2, 0, g2.genes.length);
        System.arraycopy(g1.genes, top, newGenes2, top, length);
        GeneticIndividual newG2 = new GeneticIndividual(newGenes2);

        GeneticIndividual[] children = {newG1, newG2};

        return children;
    }

    /**
     * 突然変異関数 genomMutationRateの確率で遺伝子がランダムに変化します
     *
     * @param genomMutaionRate
     */
    public void mutation(double genomMutaionRate) {
        Random r = new Random();
        for (int i = 0; i < CHROMOSOME_LENGTH; i++) {
            if (genomMutaionRate > r.nextDouble()) {
                this.genes[i] = this.rand();
            }
        }
    }

    /**
     * -1〜1の乱数を出す
     *
     * @return 乱数
     */
    private double rand() {
        Random r = new Random();
        return r.nextDouble() * 2.0 - 1.0;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getWin() {
        return win;
    }

    public double[][] getMiddle1Weight() {
        return middle1Weight;
    }

    public double[][] getMiddle2Weight() {
        return middle2Weight;
    }

    public double[][] getOutWeight() {
        return outWeight;
    }

    public double[] getGenes() {
        return genes;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public void addTotalScore(int totalScore) {
        this.totalScore += totalScore;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public void addWin() {
        this.win += 1;
    }
    
    public boolean getNewcomer() {
        return this.newcomer;
    }
    
    public void setNewComer(boolean b) {
        this.newcomer = b;
    }

    @Override
    public String toString() {
        String line = "";
        // 1-60行はmiddle1Weight
        for (int i = 0; i < INPUT_LENGTH; i++) {
            for (int j = 0; j < MIDDLE_1_LENGTH; j++) {
                line += this.middle1Weight[i][j];
                if (j != MIDDLE_1_LENGTH - 1) {
                    line += ",";
                }
            }
            line += "\n";
        }
        // 61-76行はmiddle2Weight
        for (int i = 0; i < MIDDLE_1_LENGTH; i++) {
            for (int j = 0; j < MIDDLE_2_LENGTH; j++) {
                line += this.middle2Weight[i][j];
                if (j != MIDDLE_2_LENGTH - 1) {
                    line += ",";
                }
            }
            line += "\n";
        }
        // 77-86行はoutputWeight
        for (int i = 0; i < MIDDLE_2_LENGTH; i++) {
            for (int j = 0; j < OUTPUT_LENGTH; j++) {
                line += this.outWeight[i][j];
                if (j != OUTPUT_LENGTH - 1) {
                    line += ",";
                }
            }
            line += "\n";
        }
        return line;
    }

    @Override
    public int compareTo(GeneticIndividual o) {
        // 勝数が多いほうが勝ち
        if (this.win > o.win) {
            return 2;
        } else if (this.win < o.win) {
            return -2;
        }

        // 勝数が同じ時はスコアが多いほうが勝ち
        if (this.totalScore > o.totalScore) {
            return 1;
        } else if (this.totalScore < o.totalScore) {
            return -1;
        }

        // それでも同じなら引き分け
        return 0;
    }

}
