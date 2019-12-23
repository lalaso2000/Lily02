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

    public static final int CHROMOSOME_LENGTH = 840;
    public static final int SEASON_LENGTH = 12;
    public static final int EACH_SEASON_LENGTH = 70;
    public static final double GAUSSIAN_SIGMA = 0.01;
    private double[] genes = new double[CHROMOSOME_LENGTH];
    private int win = 0;
    private int totalScore = 0;

    /**
     * ランダムに生成する
     */
    public GeneticIndividual() {
        for (int i = 0; i < CHROMOSOME_LENGTH; i++) {
            genes[i] = this.rand();
        }
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
    }

    /**
     * csvファイルから読み込んで生成
     *
     * @param filePath
     */
    public GeneticIndividual(String filePath) {
        this.loadWeights(filePath);
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
        if (lines.size() != SEASON_LENGTH) {
            System.err.println("csvの行数が不正です。");
            return;
        }

        // 数値を読み込む
        int seasonCount = 0;
        for (String line : lines) {
            String[] nums = line.split(",");
            // 長さが不正
            if (nums.length != EACH_SEASON_LENGTH) {
                System.err.println(seasonCount + "番目の季節の染色体の長さが不正です。");
                return;
            }
            int count = 0;
            for (String num : nums) {
                try {
                    double n = Double.parseDouble(num);
                    // ひどくなくなった
                    this.genes[seasonCount * EACH_SEASON_LENGTH + count] = n;
                } catch (NumberFormatException ex) {
                    System.err.println("数値じゃないものが含まれています。");
                }
                count++;
            }
            seasonCount++;
        }
    }

    public void outputCSV(String filePath) {
        // ファイルを読み込む
        File file = new File(filePath);

        // ファイルに書き込む
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            // 1行目はヘッダ的な
            String line = "# Flask,Flask,Flask,Gear,Gear,Gear,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Money,Trend,Trend,Trend,Score,Score,Score,T0,T1,T2,T3,T4,T5,T0T1,T0T2,T0T3,T0T4,T0T5,T1T2,T1T3,T1T4,T1T5,T2T3,T2T4,T2T5,T3T4,T3T5,T4T5,Tx3,StartPlayer,StartPlayer,StartPlayer";
            pw.println(line);
            // 2行目以降は書く
            pw.println(this);
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
        for (int i = 0; i < newGenes1.length; i++) {
            double k = r.nextDouble() * 0.2 - 0.1;
            newGenes1[i] += k;
            if (newGenes1[i] > 1.0) {
                newGenes1[i] = 1.0;
            } else if (newGenes1[i] < -1.0) {
                newGenes1[i] = -1.0;
            }
        }
        System.arraycopy(g1.genes, 0, newGenes1, 0, g1.genes.length);
        System.arraycopy(g2.genes, top, newGenes1, top, length);
        GeneticIndividual newG1 = new GeneticIndividual(newGenes1);

        double[] newGenes2 = new double[CHROMOSOME_LENGTH];
        for (int i = 0; i < newGenes2.length; i++) {
            double k = r.nextGaussian() * GAUSSIAN_SIGMA;
            newGenes2[i] += k;
            if (newGenes2[i] > 1.0) {
                newGenes2[i] = 1.0;
            } else if (newGenes2[i] < -1.0) {
                newGenes2[i] = -1.0;
            }
        }
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

    @Override
    public String toString() {
        String line = "";
        // 1行は1季節
        for (int season = 0; season < SEASON_LENGTH; season++) {
            for (int i = 0; i < EACH_SEASON_LENGTH; i++) {
                line += this.genes[season * EACH_SEASON_LENGTH + i];
                if (i != (EACH_SEASON_LENGTH) - 1) {
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
