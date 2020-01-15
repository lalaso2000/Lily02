
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
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author niwatakumi
 */
public class Lily0Learning {

    private static final int ELITE_NUM = 3;
    private static final int NON_ELITE_NUM = 6;
    private static final int ELITE_CHILDEN_NUM = 6;
    private static final int GROUP_NUM = 1;
    private static final int RANDOM_NUM = 35;
    private static final int INDIVIDUAL_NUM = ELITE_NUM + NON_ELITE_NUM + ELITE_CHILDEN_NUM + RANDOM_NUM;
    private static final double INDIVIDUAL_MUTATION_RATE = 0.05;
    private static final double GENOM_MUTATION_RATE = 0.025;
    private static final String DIR_NAME = "test";

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

        // 初期化
        // フォルダを作る
        newdir = new File(DIR_NAME);
        newdir.mkdir();
        newdir = new File(DIR_NAME + File.separator + "0");
        newdir.mkdir();
        for (int i = 0; i < INDIVIDUAL_NUM; i++) {
            gis.add(new GeneticIndividual());
            gis.get(i).outputCSV(DIR_NAME + File.separator + "0" + File.separator + "weight0-" + i + ".csv");
        }
        int count = 0;

        // 途中から
//        int generation = 100;
//        for (int i = 0; i < INDIVIDUAL_NUM; i++) {
//            String filePath = DIR_NAME;
//            filePath += File.separator;
//            filePath += generation;
//            filePath += File.separator;
//            filePath += "weight" + generation + "-";
//            filePath += i;
//            filePath += ".csv";
//            gis.add(new GeneticIndividual(filePath));
////            System.out.println(gis.get(i));
//        }
//        int count = generation;
        // 兵庫県警に逮捕される。。。
        while (count < 1000000) {
            count++;
            /* 対戦する */
//            System.out.println("======第" + count + "世代 対戦フェーズ======");
            // 乱数にしてグループ分け
            Collections.shuffle(gis);

            int groupIndividualNum = INDIVIDUAL_NUM / GROUP_NUM;
            // グループの数ループ
            for (int k = 0; k < GROUP_NUM; k++) {
                for (int j = k * groupIndividualNum; j < (k + 1) * groupIndividualNum; j++) {
                    for (int i = k * groupIndividualNum; i < (k + 1) * groupIndividualNum; i++) {
                        if (j == i) {
                            continue;
                        }
                        // ゲーム初期化
                        Game game = new Game();
                        // Lily生成
                        Tochka[] ai = new Tochka[2];
                        ai[0] = new Tochka(game, gis.get(j), 0);
                        ai[1] = new Tochka(game, gis.get(i), 1);
                        game.startGame();
//                        System.out.println(j + " vs " + i);
                        // 対戦
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
                        if (scores[0] > scores[1]) {
//                            System.out.println("winner : " + i);
                            gis.get(j).addWin();
                        } else if (scores[0] < scores[1]) {
//                            System.out.println("winner : " + j);
                            gis.get(i).addWin();
                        } else {
//                            System.out.println("drow");
                        }
                        gis.get(j).addTotalScore(scores[0] - scores[1]);
                        gis.get(i).addTotalScore(scores[1] - scores[0]);
                    }
                    // 教師と対戦
                    // 教師後手
                    // ゲーム初期化
                    Game game = new Game();
                    // Lily生成
                    TajimaLabAI[] ai = new TajimaLabAI[2];
                    ai[0] = new Tochka(game, gis.get(j), 0);
                    ai[1] = new SampleAI(game, 1);
                    game.startGame();
                    // 対戦
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
                    if (scores[0] > scores[1]) {
//                            System.out.println("winner : " + i);
                        gis.get(j).addTeacherWin();
                    }
                    gis.get(j).addTeacherScore(scores[0] - scores[1]);
                    
                    // 教師先手
                    // ゲーム初期化
                    game = new Game();
                    // Lily生成
                    ai = new TajimaLabAI[2];
                    ai[1] = new Tochka(game, gis.get(j), 1);
                    ai[0] = new SampleAI(game, 0);
                    game.startGame();
                    // 対戦
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
                    if (scores[1] > scores[0]) {
                        gis.get(j).addTeacherWin();
                    }
                    gis.get(j).addTeacherScore(scores[1] - scores[0]);
                }
            }


            /* 世代交代 */
            // ソートして
            Collections.sort(gis, Comparator.reverseOrder());

            // デバッグ用に出力
//            for (GeneticIndividual gi : gis) {
//                System.out.println(gi.getWin() + " : " + gi.getTotalScore());
//                System.out.println(gi.getGenes()[0] + "," + gi.getGenes()[1] + "," + gi.getGenes()[2]);
//            }
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

            ArrayList<GeneticIndividual> randomChildren = new ArrayList<>();
            for (int i = 0; i < RANDOM_NUM; i++) {
                randomChildren.add(new GeneticIndividual());
            }

            // 次の世代に引き継ぎ
            gis = new ArrayList<>();
            gis.addAll(elite);
            gis.addAll(nonElite);
            gis.addAll(eliteChilden);
            gis.addAll(randomChildren);
//            for (GeneticIndividual gi : gis) {
//                gi.setTotalScore(0);
//                gi.setWin(0);
//            }

            // フォルダを作る
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
        }

    }

}
