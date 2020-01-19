/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import gameElements.Board;
import gameElements.Game;
import gameElements.GameResources;

/**
 *
 * @author niwatakumi
 */
public class SampleAI2 extends TajimaLabAI {

    // 自分が何手打ったかを数える
    int handNum = 0;

    public SampleAI2(Game game, int playerNum) {
        super(game);
        this.myName = "Sample AI";
        this.myNumber = playerNum;
        this.enemyNumber = (playerNum == 0) ? 1 : 0;
    }
    
    private void putWorker(String worker, String place, String option){
        this.gameBoard.play(myNumber, place, worker, option);
    }

    @Override
    public void think() {
        // 現在のゲームの状態はthis.gameBoradに入っています。
        // 上の方の処理で自動的にサーバーと同期する…はず
        // 同期外れを防ぐため、cloneして取得することをおすすめします。
        Game currentGameBoard = this.gameBoard.clone();
        
        // 最高評価(初期値は負の無限大)
        double maxEva = Double.NEGATIVE_INFINITY;
        String bestWorker = "";
        String bestPlace = "";
        String bestOption = "";

        // 全部の手をループする
        String[] workers = {"P", "S"};
        String[] places = Board.PLACE_NAMES;
        for (String worker : workers) {
            for (String place : places) {
                // 場所に合わせてoptionsを決める
                String[] options;
                switch (place) {
                    case "1-1":
                        options = new String[]{"F", "G", "M"};
                        break;
                    case "2-1":
                    case "3-4":
                        options = new String[]{"F", "G"};
                        break;
                    case "6-2":
                        options = new String[]{"FF", "FG", "GG"};
                        break;
                    case "7-1":
                        options = new String[]{"T0", "T1", "T2", "T3", "T4", "T5"};
                        break;
                    default:
                        options = new String[]{""};
                        break;
                }
                // オプションループ
                for (String option : options) {
                    // もし置けるなら
                    if (currentGameBoard.canPutWorker(this.myNumber, place, worker, option)) {
                        // 試しに打ってみて
                        Game virtualGame = currentGameBoard.clone();
                        virtualGame.play(this.myNumber, place, worker, option);
                        // 評価値を計算
                        double eva = this.evaluateGame(virtualGame);
                        // もし最大評価なら、ベストを更新
                        if (eva >= maxEva) {
                            maxEva = eva;
                            bestWorker = worker;
                            bestPlace = place;
                            bestOption = option;
                        }
                    }
                }
            }
        }

        // 最善手をうつ
        putWorker(bestWorker, bestPlace, bestOption);
    }
    
    /**
     * ゲーム盤面を評価する関数<br>
     * 評価関数は季節ごとに
     *
     * 春秋→「（フラスコ＋ギア）×２＋お金」 <br>
     * 夏冬→「（フラスコ＋ギア）×２＋お金＋スコア×２０」<br>
     * もし，負債を抱えるようであれば評価は最低
     *
     * @param game 評価したい盤面
     * @return 評価値
     */
    private double evaluateGame(Game game) {
        GameResources resources = game.getResourcesOf(myNumber);

        // 負債を抱えるようであれば-10000点
        int debt = resources.getDebt();
        if (debt > 0) {
            return -10000;
        }

        // フラスコとギアとお金を取得
        int flask = resources.getCurrentResrchPoint(0);
        int gear = resources.getCurrentResrchPoint(1);
        int money = resources.getCurrentMoney();

        String season = game.getSeason();

        // （フラスコ＋ギア）×２＋お金
        int eva = (flask + gear) * 2 + money;
        // 夏冬はスコアを加算
        if (season.contains("b")) {
            // 夏冬＝季節の文字列に「b」が含まれる
            int score = resources.getTotalScore();
            eva += score * 20;
        }

        return eva;
    }


    @Override
    protected void seasonChanged() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void playerNumDecided() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
