/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import gameElements.Board;
import gameElements.Game;
import gameElements.GameResources;
import geneticAlgorithm.GeneticIndividual;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <h1> Lily0 </h1>
 *
 * 遺伝的アルゴリズムをするらしい<br>
 *
 * <ol>
 * <li>フラスコ...3次式</li>
 * <li>ギア...3次式</li>
 * <li>お金...3次式x支払額に応じて11パターン</li>
 * <li>トレンドスコア差...3次式</li>
 * <li>合計点差...3次式</li>
 * <li>機材...定数x6種類</li>
 * <li>先手マーカー...定数xアシスタントの条件に応じて3種類</li>
 * </ol>
 * 以上を12季節分
 *
 * @author niwatakumi
 */
public class Lily0 extends TajimaLabAI {

    private static final int PREFETCH_MAX_LEVEL = 8;    // 先読みの最高階数

    private double[][] flaskWeight = new double[12][3];
    private double[][] gearWeight = new double[12][3];
    private double[][][] moneyWeight = new double[12][11][3];
    private double[][] trendWeight = new double[12][3];
    private double[][] scoreWeight = new double[12][3];
    private double[][] machineWeight = new double[12][22];
    private double[][] startPlayerWeight = new double[12][3];

    private final List<String> PLACE_FOR_PROFESSOR = Arrays.asList("6-1", "6-2", "6-3", "7-1");

    /**
     * コンストラクタ for learning
     *
     * @param game
     */
    public Lily0(Game game, GeneticIndividual gi, int playerNum) {
        super(game);
        this.myName = "Lily 0";
        this.myNumber = playerNum;
        this.enemyNumber = (playerNum + 1) % 2;
        // giから係数設定
        for (int i = 0; i < GeneticIndividual.CHROMOSOME_LENGTH; i++) {
            int seasonCount = i / GeneticIndividual.EACH_SEASON_LENGTH;
            int count = i % GeneticIndividual.EACH_SEASON_LENGTH;
            // これはひどい
            if (count >= 0 && count < 3) {
                this.flaskWeight[seasonCount][count] = gi.getGenes()[i];
            } else if (count >= 3 && count < 6) {
                this.gearWeight[seasonCount][count - 3] = gi.getGenes()[i];
            } else if (count >= 6 && count < 39) {
                this.moneyWeight[seasonCount][(count - 6) / 3][(count - 6) % 3] = gi.getGenes()[i];
            } else if (count >= 39 && count < 42) {
                this.trendWeight[seasonCount][count - 39] = gi.getGenes()[i];
            } else if (count >= 42 && count < 45) {
                this.scoreWeight[seasonCount][count - 42] = gi.getGenes()[i];
            } else if (count >= 45 && count < 67) {
                this.machineWeight[seasonCount][count - 45] = gi.getGenes()[i];
            } else if (count >= 67 && count < 70) {
                this.startPlayerWeight[seasonCount][count - 67] = gi.getGenes()[i];
            }
        }
//        this.printWeight(game);
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
        if (lines.size() != 12) {
            System.err.println("csvの行数が不正です。");
        }

        // 数値を読み込む
        int seasonCount = 0;
        for (String line : lines) {
            String[] nums = line.split(",");
            int count = 0;
            for (String num : nums) {
                try {
                    double n = Double.parseDouble(num);
                    // これはひどい
                    if (count >= 0 && count < 3) {
                        this.flaskWeight[seasonCount][count] = n;
                    } else if (count >= 3 && count < 6) {
                        this.gearWeight[seasonCount][count - 3] = n;
                    } else if (count >= 6 && count < 39) {
                        this.moneyWeight[seasonCount][(count - 6) / 3][(count - 6) % 3] = n;
                    } else if (count >= 39 && count < 42) {
                        this.trendWeight[seasonCount][count - 39] = n;
                    } else if (count >= 42 && count < 45) {
                        this.scoreWeight[seasonCount][count - 42] = n;
                    } else if (count >= 45 && count < 67) {
                        this.machineWeight[seasonCount][count - 45] = n;
                    } else if (count >= 67 && count < 70) {
                        this.startPlayerWeight[seasonCount][count - 67] = n;
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("数値じゃないものが含まれています。");
                }
                count++;
            }
            seasonCount++;
        }
    }

    private void printWeight(Game game) {
        Integer season = this.convertSeasonStrToSeasonNum(game.getSeason());
        if (season == null) {
            return;
        }

        System.out.println("==========================");
        System.out.println("========== weights ==========");
        System.out.println("==========================");
        String line = "Flask {\n\t";
        line += this.flaskWeight[season][0] + ", ";
        line += this.flaskWeight[season][1] + ", ";
        line += this.flaskWeight[season][2] + "\n},\n";

        line += "Gear {\n\t";
        line += this.gearWeight[season][0] + ",";
        line += this.gearWeight[season][1] + ",";
        line += this.gearWeight[season][2] + "\n},\n";

        line += "Money {\n";
        for (int i = 0; i < 11; i++) {
            line += "\t" + i + " {\n";
            line += "\t\t";
            line += this.moneyWeight[season][i][0] + ",";
            line += this.moneyWeight[season][i][1] + ",";
            line += this.moneyWeight[season][i][2] + "\n\t},\n";
        }
        line += "},\n";

        line += "Trend {\n\t";
        line += this.trendWeight[season][0] + ",";
        line += this.trendWeight[season][1] + ",";
        line += this.trendWeight[season][2] + "\n},\n";

        line += "Score {\n\t";
        line += this.scoreWeight[season][0] + ",";
        line += this.scoreWeight[season][1] + ",";
        line += this.scoreWeight[season][2] + "\n},\n";

        line += "Machine {\n\t";
        line += this.machineWeight[season][0] + ",";
        line += this.machineWeight[season][1] + ",";
        line += this.machineWeight[season][2] + ",";
        line += this.machineWeight[season][3] + ",";
        line += this.machineWeight[season][4] + ",";
        line += this.machineWeight[season][5] + ",";
        line += this.machineWeight[season][6] + ",";
        line += this.machineWeight[season][7] + ",";
        line += this.machineWeight[season][8] + ",";
        line += this.machineWeight[season][9] + ",";
        line += this.machineWeight[season][10] + ",";
        line += this.machineWeight[season][11] + ",";
        line += this.machineWeight[season][12] + ",";
        line += this.machineWeight[season][13] + ",";
        line += this.machineWeight[season][14] + ",";
        line += this.machineWeight[season][15] + ",";
        line += this.machineWeight[season][16] + ",";
        line += this.machineWeight[season][17] + ",";
        line += this.machineWeight[season][18] + ",";
        line += this.machineWeight[season][19] + ",";
        line += this.machineWeight[season][20] + ",";
        line += this.machineWeight[season][21] + "\n},\n";

        line += "StartPlayer {\n\t";
        line += this.startPlayerWeight[season][0] + ",";
        line += this.startPlayerWeight[season][1] + ",";
        line += this.startPlayerWeight[season][2] + "\n},\n";

        System.out.println(line);
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

    /**
     * 先読み関数
     *
     * @param level 先読みの階層
     * @param game 現在のゲーム場面
     * @param playerNum 次にプレイする人
     * @param action 次のアクション
     * @param alpha アルファ値
     * @param beta ベータ値
     * @return
     */
    private Double prefetch(int level, Game game, int playerNum, Action action, Double alpha, Double beta, boolean[] purchased) {
        // 最下層まで読んだら評価値を返す
        if (level == PREFETCH_MAX_LEVEL) {
            Double eva = evaluateBoard(game, playerNum, action, purchased);
            return eva;
        }

        // 仮想でゲームを進める（打てないならnull返して終了）
        Game cloneGame = clonePlay(game, playerNum, action, false);
        if (cloneGame == null) {
            return null;
        }
        // 機材買ったらフラグを立てる
        if (action.place.equals("7-1")) {
            purchased[playerNum] = true;
        }

        // もし打った手で季節が変わるとき評価を返す
        if (cloneGame.getGameState() == Game.STATE_SEASON_END) {
            Double eva = evaluateBoard(game, playerNum, action, purchased);
            return eva;
        }

        // もし打った手でゲーム終了なら評価を返す
        if (cloneGame.getGameState() == Game.STATE_GAME_END) {
            Double eva = evaluateBoard(game, playerNum, action, purchased);
            return eva;
        }

        // 次のプレイヤーを調べる
        int nextPlayer = cloneGame.getCurrentPlayer();
        // 次の手を探索
        if (nextPlayer == this.myNumber) {
            return this.prefetchMax(level, cloneGame, alpha, beta, purchased);
        } else {
            return this.prefetchMin(level, cloneGame, alpha, beta, purchased);
        }

    }

    /**
     * 先読み中、自分の手を探索する
     *
     * @param level
     * @param game
     * @param alpha
     * @param beta
     * @return
     */
    private Double prefetchMax(int level, Game game, Double alpha, Double beta, boolean[] purchased) {
        // 全手やってみて一番いい手を探す
        Double bestEva = Double.NEGATIVE_INFINITY;
        Double eva = 0.0;

        // ワーカーの一覧を作る
        GameResources resource = game.getResourcesOf(this.myNumber);
        int studentNum = resource.getNumberofUseableWorkers("S");

        // 全手探索
        for (String p : Board.PLACE_NAMES) {
            // オプションの一覧を生成する
            String[] options;
            if (p.equals("1-1")) {
                String[] o = {"F", "G", "M"};
                options = o;
            } else if (p.equals("2-1") || p.equals("3-4")) {
                String[] o = {"F", "G"};
                options = o;
            } else if (p.equals("6-2")) {
                String[] o = {"FF", "FG", "GG"};
                options = o;
            } else if (p.equals("7-1")) {
                String[] o = {"T00", "T01", "T02", "T03", "T04", "T05"};
                options = o;
            } else {
                String[] o = {""};
                options = o;
            }
            // 全オプションループ
            for (String o : options) {
                // ワーカーは学生優先
                String w = "S";
                if (PLACE_FOR_PROFESSOR.contains(p)) {
                    w = "P";
                }
                if (studentNum <= 0) {
                    w = "P";
                }
                Action a = new Action(w, p, o);
                eva = this.prefetch(level + 1, game, this.myNumber, a, alpha, beta, purchased);
                // bata値を上回ったら探索中止
                if (eva != null && eva >= beta) {
                    bestEva = eva;
                    return bestEva;
                }
                // 評価良いの見つけたら
                if (eva != null && eva >= bestEva) {
                    // 更新
                    bestEva = eva;
                    alpha = Double.max(alpha, bestEva);
                }
            }
        }
        return bestEva;
    }

    /**
     * 先読み中、相手の手を探索する
     *
     * @param level
     * @param game
     * @param alpha
     * @param beta
     * @return
     */
    private Double prefetchMin(int level, Game game, Double alpha, Double beta, boolean[] purchased) {
        // 全手やってみて一番いい手を探す
        Double bestEva = Double.POSITIVE_INFINITY;
        Double eva = 0.0;

        // ワーカーの一覧を作る
        GameResources resource = game.getResourcesOf(this.enemyNumber);
        int studentNum = resource.getNumberofUseableWorkers("S");

        // 全手探索
        for (String p : Board.PLACE_NAMES) {
            // オプションの一覧を生成する
            String[] options;
            if (p.equals("1-1")) {
                String[] o = {"F", "G", "M"};
                options = o;
            } else if (p.equals("2-1") || p.equals("3-4")) {
                String[] o = {"F", "G"};
                options = o;
            } else if (p.equals("6-2")) {
                String[] o = {"FF", "FG", "GG"};
                options = o;
            } else if (p.equals("7-1")) {
                String[] o = {"T00", "T01", "T02", "T03", "T04", "T05"};
                options = o;
            } else {
                String[] o = {""};
                options = o;
            }
            // 全オプションループ
            for (String o : options) {
                // ワーカーは学生優先
                String w = "S";
                if (PLACE_FOR_PROFESSOR.contains(p)) {
                    w = "P";
                }
                if (studentNum <= 0) {
                    w = "P";
                }
                Action a = new Action(w, p, o);
                eva = this.prefetch(level + 1, game, this.enemyNumber, a, alpha, beta, purchased);
                // alpha値を下回ったら探索中止
                if (eva != null && eva <= alpha) {
                    bestEva = eva;
                    return bestEva;
                }
                // 評価良いの見つけたら
                if (eva != null && eva <= bestEva) {
                    // 更新
                    bestEva = eva;
                    beta = Double.min(beta, bestEva);
                }
            }
        }

        return bestEva;
    }

//
    /**
     * 評価関数
     *
     * @param game アクションする前のゲーム状態
     * @param playerNum アクションする人
     * @param action アクション内容
     * @param purchased
     * @return 評価値
     */
    protected Double evaluateBoard(Game game, int playerNum, Action action, boolean[] purchased) {
        Double evaluation = 0.0;

        // ゲームを複製
        Game cloneGame = this.clonePlay(game, playerNum, action, false);
        if (cloneGame == null) {
            return null;
        }
        // 機材買ったらフラグを立てる
        if (action.place.equals("7-1")) {
            purchased[playerNum] = true;
        }

        // 季節の数字を取得
        Integer season = this.convertSeasonStrToSeasonNum(cloneGame.getSeason());
        if (season == null) {
            System.err.println("季節の数字が取得できません");
            return null;
        }

        // 季節が変わるなら更新
        if (cloneGame.getGameState() == Game.STATE_SEASON_END) {
            cloneGame.changeNewSeason();
        }

        // アシスタントは売れている？
        boolean assistant = false;
        if (cloneGame.getResourcesOf(this.myNumber).hasMachine(3)) {
            assistant = true;
        } else if (cloneGame.getResourcesOf(this.enemyNumber).hasMachine(3)) {
            assistant = true;
        }

        // 評価値の計算
        evaluation += calcEvaluate(cloneGame.getResourcesOf(this.myNumber), season, assistant, purchased[this.myNumber]);
        evaluation -= calcEvaluate(cloneGame.getResourcesOf(this.enemyNumber), season, assistant, purchased[this.enemyNumber]);

        return evaluation;
    }

    /**
     * リソースから評価値を計算
     *
     * @param resource リソース
     * @param seasonTrendID 現在の季節
     * @param trendInt トレンドの場所
     * @return
     */
    private Double calcEvaluate(GameResources resource, int season, boolean assistant, boolean purchased) {
        // リソースに応じて評価値を計算
        Double evaluation = 0.0;

        // 54*12個の変数が踊りだす！
        // フラスコ
        int flask = resource.getCurrentResrchPoint(0);
        evaluation += this.flaskWeight[season][0] * flask * flask * flask;
        evaluation += this.flaskWeight[season][1] * flask * flask;
        evaluation += this.flaskWeight[season][2] * flask;

        // ギア
        int gear = resource.getCurrentResrchPoint(1);
        evaluation += this.gearWeight[season][0] * gear * gear * gear;
        evaluation += this.gearWeight[season][1] * gear * gear;
        evaluation += this.gearWeight[season][2] * gear;

        // お金
        int money = resource.getCurrentMoney();
        int cost = 0;
        for (int i = 0; i < resource.MachinesCost.length; i++) {
            if (resource.hasMachine(i)) {
                cost += resource.MachinesCost[i];
            }
        }
        evaluation += this.moneyWeight[season][cost][0] * money * money * money;
        evaluation += this.moneyWeight[season][cost][1] * money * money;
        evaluation += this.moneyWeight[season][cost][2] * money;

        // トレンドスコア
        int trendScore = resource.getSocreOf(this.convertSeasonToTrendStr(Game.SEASON_NAMES[season]));
        evaluation += this.trendWeight[season][0] * trendScore * trendScore * trendScore;
        evaluation += this.trendWeight[season][1] * trendScore * trendScore;
        evaluation += this.trendWeight[season][2] * trendScore;

        // 合計スコア
        int score = resource.getTotalScore();
        evaluation += this.scoreWeight[season][0] * score * score * score;
        evaluation += this.scoreWeight[season][1] * score * score;
        evaluation += this.scoreWeight[season][2] * score;

        // 機材
        if (purchased) {
            int machinePattern = 0;
            for (int i = 0; i < resource.MachinesCost.length; i++) {
                if (resource.hasMachine(i)) {
                    machinePattern += 1 << i;
                }
            }
            // これはひどいpt.2
            switch (machinePattern) {
                case 0:
                    break;
                case 1:
                    evaluation += this.machineWeight[season][0];
                    break;
                case 2:
                    evaluation += this.machineWeight[season][1];
                    break;
                case 4:
                    evaluation += this.machineWeight[season][2];
                    break;
                case 8:
                    evaluation += this.machineWeight[season][3];
                    break;
                case 16:
                    evaluation += this.machineWeight[season][4];
                    break;
                case 32:
                    evaluation += this.machineWeight[season][5];
                    break;
                case 3:
                    evaluation += this.machineWeight[season][6];
                    break;
                case 5:
                    evaluation += this.machineWeight[season][7];
                    break;
                case 9:
                    evaluation += this.machineWeight[season][8];
                    break;
                case 17:
                    evaluation += this.machineWeight[season][9];
                    break;
                case 33:
                    evaluation += this.machineWeight[season][10];
                    break;
                case 6:
                    evaluation += this.machineWeight[season][11];
                    break;
                case 10:
                    evaluation += this.machineWeight[season][12];
                    break;
                case 18:
                    evaluation += this.machineWeight[season][13];
                    break;
                case 34:
                    evaluation += this.machineWeight[season][14];
                    break;
                case 12:
                    evaluation += this.machineWeight[season][15];
                    break;
                case 20:
                    evaluation += this.machineWeight[season][16];
                    break;
                case 36:
                    evaluation += this.machineWeight[season][17];
                    break;
                case 24:
                    evaluation += this.machineWeight[season][18];
                    break;
                case 40:
                    evaluation += this.machineWeight[season][19];
                    break;
                case 48:
                    evaluation += this.machineWeight[season][20];
                    break;
                default:
                    evaluation += this.machineWeight[season][21];
                    break;
            }
        }

        // スタートプレイヤー
        if (resource.isStartPlayer()) {
            if (assistant) {
                // 自分が持ってる
                if (resource.hasMachine(3)) {
                    evaluation += this.startPlayerWeight[season][1];
                } // 相手が持ってる
                else {
                    evaluation += this.startPlayerWeight[season][2];
                }
            } else {
                // どっちも持ってない
                evaluation += this.startPlayerWeight[season][0];
            }
        }

        return evaluation;
    }

    /**
     * 考えるフェーズ 手を打つところまで実装
     */
    @Override
    public void think() {

//        System.out.println("==========================");
//        System.out.println("========== thinking ==========");
//        System.out.println("==========================");
        Action bestAction = null;

        // 全手やってみて一番いい手を探す
        Double bestEva = Double.NEGATIVE_INFINITY;
        Double eva = 0.0;

        Action a;

        // 探索場所の設定
        String places[] = Board.PLACE_NAMES;
        // 探索ワーカーの設定
        GameResources resource = this.gameBoard.getResourcesOf(this.myNumber);
        int studentNum = resource.getNumberofUseableWorkers("S");

        // 全手探索
        for (String p : places) {
            // オプションの一覧を生成する
            String[] options;
            if (p.equals("1-1")) {
                String[] o = {"F", "G", "M"};
                options = o;
            } else if (p.equals("2-1") || p.equals("3-4")) {
                String[] o = {"F", "G"};
                options = o;
            } else if (p.equals("6-2")) {
                String[] o = {"FF", "FG", "GG"};
                options = o;
            } else if (p.equals("7-1")) {
                String[] o = {"T00", "T01", "T02", "T03", "T04", "T05"};
                options = o;
            } else {
                String[] o = {""};
                options = o;
            }
            // 全オプションループ
            for (String o : options) {
                // ワーカーは学生優先
                String w = "S";
                if (PLACE_FOR_PROFESSOR.contains(p)) {
                    w = "P";
                }
                if (studentNum <= 0) {
                    w = "P";
                }
                a = new Action(w, p, o);
                boolean[] purchaced = {false, false};
                eva = this.prefetch(1, gameBoard, this.myNumber, a, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, purchaced);
                if (eva != null) {
//                        System.out.println(a + " -> " + eva);
                }
                // 評価良いの見つけたら
                if (eva != null && eva >= bestEva) {
                    // 更新
                    bestEva = eva;
                    bestAction = a;
                }

            }
        }

        // デバッグ用
//        System.out.println("player" + this.myNumber + " -> " + bestAction);
        // 最適解を打つ
        this.gameBoard.play(this.myNumber, bestAction.place, bestAction.worker, bestAction.option);

//        System.out.println("===========================");
//        System.out.println("========== think end ==========");
//        System.out.println("===========================");
//
//        System.out.println(this.myNumber + " : " + bestAction + " -> " + bestEva);
        // 最適解を打つ
//        this.putWorker(bestAction);
//        return bestAction;
    }

    /**
     * 季節が変わった時に呼び出される
     */
    @Override
    protected void seasonChanged() {
//        printWeight(gameBoard);
    }

    /**
     * プレイヤー番号が通知された時呼び出される
     */
    @Override
    protected void playerNumDecided() {

    }

}
