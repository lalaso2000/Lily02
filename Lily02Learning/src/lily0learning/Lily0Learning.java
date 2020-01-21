
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lily0learning;

import ai.Action;
import ai.SampleAI;
import ai.TajimaLabAI;
import ai.Tochka;
import gameElements.Game;
import geneticAlgorithm.GeneticIndividual;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author niwatakumi
 */
public class Lily0Learning {

    private static final int ELITE_NUM = 35;
    private static final int NON_ELITE_NUM = 4;
    private static final int ELITE_CHILDEN_NUM = 6;
    private static final int RANDOM_NUM = 5;
    private static final int INDIVIDUAL_NUM = ELITE_NUM + NON_ELITE_NUM + ELITE_CHILDEN_NUM + RANDOM_NUM;
    private static final int BATTLE_NUM = 90;
    private static final int MIRROR_BATTLE_NUM = 10;
    private static final double INDIVIDUAL_MUTATION_RATE = 0.05;
    private static final double GENOM_MUTATION_RATE = 0.025;
    private static final String DIR_NAME = "D:\\output5-1";
    private static final String ONLINE_DIR_NAME = "C:\\Users\\raras\\OneDrive - 独立行政法人 国立高等専門学校機構\\Tochka\\Lily02\\outputs\\output5-1";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        // 個体
        ArrayList<GeneticIndividual> gis = new ArrayList<>(INDIVIDUAL_NUM);
        // 乱数
        Random random = new Random();
        // フォルダ
        File newdir;

//        // 初期化
//        // フォルダを作る
//        newdir = new File(DIR_NAME);
//        newdir.mkdir();
//        newdir = new File(DIR_NAME + File.separator + "0");
//        newdir.mkdir();
//        for (int i = 0; i < INDIVIDUAL_NUM; i++) {
//            gis.add(new GeneticIndividual());
//            gis.get(i).outputCSV(DIR_NAME + File.separator + "0" + File.separator + "weight0-" + i + ".csv");
//        }
//        newdir = new File(ONLINE_DIR_NAME);
//        newdir.mkdir();
//        newdir = new File(ONLINE_DIR_NAME + File.separator + "0");
//        newdir.mkdir();
//        for (int i = 0; i < INDIVIDUAL_NUM; i++) {
//            gis.add(new GeneticIndividual());
//            gis.get(i).outputCSV(ONLINE_DIR_NAME + File.separator + "0" + File.separator + "weight0-" + i + ".csv");
//        }
//        int count = 0;
        // 途中から
        int generation = 5570;
        for (int i = 0; i < INDIVIDUAL_NUM; i++) {
            String filePath = DIR_NAME;
            filePath += File.separator;
            filePath += generation;
            filePath += File.separator;
            filePath += "weight" + generation + "-";
            filePath += i;
            filePath += ".csv";
            gis.add(new GeneticIndividual(filePath));
//            System.out.println(gis.get(i));
        }
        int count = generation;

        // 兵庫県警に逮捕される。。。
        while (count < 1000000) {
            count++;
            for (int j = 0; j < INDIVIDUAL_NUM; j++) {
                // 育成対象(gis.get(j))をBATTLE_NUM回ランダム生成したものと対戦させる
                for (int i = 0; i < BATTLE_NUM; i++) {
                    // ゲーム初期化
                    Game game = new Game();
                    // AI生成
                    Tochka[] ai = new Tochka[2];
                    ai[0] = new Tochka(game, gis.get(j), 0);
                    ai[1] = new Tochka(game, 1);
                    // 対戦
                    game.startGame();
                    while (game.getGameState() != Game.STATE_GAME_END) {
                        // カレントプレイヤーが考えて打つ
                        ai[game.getCurrentPlayer()].think();
                        // もし季節終了なら季節進行
                        if (game.getGameState() == Game.STATE_SEASON_END) {
                            game.changeNewSeason();
                        }
                    }
                    // 終了したらスコアを取得して記録
                    int[] scores = game.getScore();
                    // 獲得した得点を記録
                    gis.get(j).addTotalScore(scores[0]);
//                    gis.get(j).addTotalScore(scores[0] - scores[1]);

                    // 先攻後攻逆転
                    // ゲーム初期化
                    game = new Game();
                    // AI生成
                    ai = new Tochka[2];
                    ai[1] = new Tochka(game, gis.get(j), 1);
                    ai[0] = new Tochka(game, 0);
                    // 対戦
                    game.startGame();
                    while (game.getGameState() != Game.STATE_GAME_END) {
                        // カレントプレイヤーが考えて打つ
                        ai[game.getCurrentPlayer()].think();
                        // もし季節終了なら季節進行
                        if (game.getGameState() == Game.STATE_SEASON_END) {
                            game.changeNewSeason();
                        }
                    }
                    // 終了したらスコアを取得して記録
                    scores = game.getScore();
                    // 得失点差を記録
                    // 獲得した得点を記録
                    gis.get(j).addTotalScore(scores[1]);
//                    gis.get(j).addTotalScore(scores[1] - scores[0]);
                }

                // 育成対象(gis.get(j))をMIRROR_BATTLE_NUM回4500代目のエリートたちと対戦させる
                for (int i = 0; i < MIRROR_BATTLE_NUM; i++) {
                    // ゲーム初期化
                    Game game = new Game();
                    // AI生成
                    Tochka[] ai = new Tochka[2];
                    ai[0] = new Tochka(game, gis.get(j), 0);
                    String oldEletePath = DIR_NAME;
                    oldEletePath += File.separator;
                    oldEletePath += 4500;
                    oldEletePath += File.separator;
                    oldEletePath += "weight" + 4500 + "-";
                    oldEletePath += i;
                    oldEletePath += ".csv";
                    ai[1] = new Tochka(game, oldEletePath, 1);
                    // 対戦
                    game.startGame();
                    while (game.getGameState() != Game.STATE_GAME_END) {
                        // カレントプレイヤーが考えて打つ
                        ai[game.getCurrentPlayer()].think();
                        // もし季節終了なら季節進行
                        if (game.getGameState() == Game.STATE_SEASON_END) {
                            game.changeNewSeason();
                        }
                    }
                    // 終了したらスコアを取得して記録
                    int[] scores = game.getScore();
                    // 獲得した得点を記録
                    gis.get(j).addTotalScore(scores[0]);
//                    System.out.println("vs." + oldEletePath);
//                    System.out.println(scores[0] + "vs" + scores[1]);
//                    gis.get(j).addTotalScore(scores[0] - scores[1]);

                    // 先攻後攻逆転
                    // ゲーム初期化
                    game = new Game();
                    // AI生成
                    ai = new Tochka[2];
                    ai[1] = new Tochka(game, oldEletePath, 1);
                    ai[0] = new Tochka(game, 0);
                    // 対戦
                    game.startGame();
                    while (game.getGameState() != Game.STATE_GAME_END) {
                        // カレントプレイヤーが考えて打つ
                        ai[game.getCurrentPlayer()].think();
                        // もし季節終了なら季節進行
                        if (game.getGameState() == Game.STATE_SEASON_END) {
                            game.changeNewSeason();
                        }
                    }
                    // 終了したらスコアを取得して記録
                    scores = game.getScore();
                    // 得失点差を記録
                    // 獲得した得点を記録
                    gis.get(j).addTotalScore(scores[1]);
//                    gis.get(j).addTotalScore(scores[1] - scores[0]);
                }
//                System.out.println("gis[" + j + "] score: " + gis.get(j).getTotalScore() + "  ave : " + gis.get(j).getTotalScore() / (BATTLE_NUM * 2.0));
            }

            /* 世代交代 */
            // ソートして
            Collections.sort(gis, Comparator.reverseOrder());

            // 上位はエリート
            List<GeneticIndividual> elite = gis.subList(0, ELITE_NUM);

            // エリート以外をランダムに引き継ぎ
            List<GeneticIndividual> nonElite = gis.subList(ELITE_NUM, INDIVIDUAL_NUM);
            Collections.shuffle(nonElite);
            nonElite = nonElite.subList(0, NON_ELITE_NUM);
            // 突然変異する
            for (GeneticIndividual g : nonElite) {
                if (random.nextDouble() < INDIVIDUAL_MUTATION_RATE) {
                    g.mutation(GENOM_MUTATION_RATE);
                }
            }

            // エリートから子供を生成
            ArrayList<GeneticIndividual> eliteChilden = new ArrayList<>();
            for (int i = 0; i < ELITE_CHILDEN_NUM; i += 2) {
                Collections.shuffle(elite);
                GeneticIndividual[] childen = GeneticIndividual.crossover(elite.get(0), elite.get(1));
                // 突然変異
                for (GeneticIndividual g : childen) {
                    if (random.nextDouble() < INDIVIDUAL_MUTATION_RATE) {
                        g.mutation(GENOM_MUTATION_RATE);
                    }
                }
                eliteChilden.add(childen[0]);
                eliteChilden.add(childen[1]);
            }

            // 新しくランダムなAIを追加
            ArrayList<GeneticIndividual> randomChildren = new ArrayList<>();
            for (int i = 0; i < RANDOM_NUM; i++) {
                randomChildren.add(new GeneticIndividual());
            }

            // 学習曲線を描画する
            double ave = 0;
            for (GeneticIndividual e : elite) {
                ave += e.getTotalScore() / ((BATTLE_NUM+MIRROR_BATTLE_NUM) * 2.0);
            }
            ave = ave / ELITE_NUM;
            File graphFile = new File(DIR_NAME + File.separator + "graph.csv");
            // ファイルに書き込む
            try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(graphFile, true)))) {
                pw.println(count + "," + ave);
                pw.close();
            } catch (IOException ex) {
                System.err.println("ファイル書き込みエラー");
            }

            // 次の世代に引き継ぎ
            gis = new ArrayList<>();
            gis.addAll(elite);
            gis.addAll(nonElite);
            gis.addAll(eliteChilden);
            gis.addAll(randomChildren);
            for (GeneticIndividual gi : gis) {
                gi.setTotalScore(0);
                gi.setWin(0);
            }

            newdir = new File(DIR_NAME + File.separator + count);
            newdir.mkdir();

            for (int i = 0; i < INDIVIDUAL_NUM; i++) {
                String filePath = DIR_NAME;
                filePath += File.separator;
                filePath += count;
                filePath += File.separator;
                filePath += "weight";
                filePath += count;
                filePath += "-";
                filePath += i;
                if (i < INDIVIDUAL_NUM - RANDOM_NUM && gis.get(i).getNewcomer()) {
                    filePath += "(r)";
                    gis.get(i).setNewComer(false);
                }
                filePath += ".csv";
                gis.get(i).outputCSV(filePath);
            }

            // フォルダを作る
            if (count % 100 == 0) {
                newdir = new File(ONLINE_DIR_NAME + File.separator + count);
                newdir.mkdir();

                for (int i = 0; i < INDIVIDUAL_NUM; i++) {
                    String filePath = ONLINE_DIR_NAME;
                    filePath += File.separator;
                    filePath += count;
                    filePath += File.separator;
                    filePath += "weight";
                    filePath += count;
                    filePath += "-";
                    filePath += i;
                    if (i < INDIVIDUAL_NUM - RANDOM_NUM && gis.get(i).getNewcomer()) {
                        filePath += "(r)";
                        gis.get(i).setNewComer(false);
                    }
                    filePath += ".csv";
                    gis.get(i).outputCSV(filePath);
                }
                // 学習曲線
                Path sourcePath = Paths.get(DIR_NAME + File.separator + "graph.csv");
                Path targetPath = Paths.get(ONLINE_DIR_NAME + File.separator + "graph.csv");
                try {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    // コピーエラー
                    System.err.println("ファイルのコピーに失敗しました．");
                    Logger.getLogger(Lily0Learning.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

}
