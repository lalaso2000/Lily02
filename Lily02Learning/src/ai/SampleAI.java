/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import gameElements.Game;

/**
 *
 * @author niwatakumi
 */
public class SampleAI extends TajimaLabAI {

    // 自分が何手打ったかを数える
    int handNum = 0;

    public SampleAI(Game game, int playerNum) {
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
        
        // 6手ごとにループ
        switch (this.handNum % 6) {
            case 0:
            case 3:
                // S 3-1 or S 2-1 F
                if (currentGameBoard.canPutWorker(this.myNumber, "3-1", "S", "")) {
                    this.putWorker("S", "3-1", "");
                } else if (currentGameBoard.canPutWorker(this.myNumber, "2-1", "S", "F")) {
                    this.putWorker("S", "2-1", "F");
                } else {
                    this.putWorker("S", "1-1", "M");
                }
                break;
            case 1:
            case 4:
                // ↑のが残っていればそれ
                if (currentGameBoard.canPutWorker(this.myNumber, "3-1", "S", "")) {
                    this.putWorker("S", "3-1", "");
                } else if (currentGameBoard.canPutWorker(this.myNumber, "2-1", "S", "F")) {
                    this.putWorker("S", "2-1", "F");
                } // なければ1-1 F
                else {
                    this.putWorker("S", "1-1", "F");
                }
                break;
            case 2:
                // 6-1 or 6-2
                if (currentGameBoard.canPutWorker(this.myNumber, "6-1", "P", "")) {
                    this.putWorker("P", "6-1", "");
                } else if (currentGameBoard.canPutWorker(this.myNumber, "6-2", "P", "FF")) {
                    this.putWorker("P", "6-2", "FF");
                } else {
                    this.putWorker("P", "1-1", "M");
                }
                break;
            case 5:
                // 5-2 F or 4-4 or 1-1 F
                if (currentGameBoard.canPutWorker(this.myNumber, "5-2", "P", "F")) {
                    this.putWorker("P", "5-2", "F");
                } else if (currentGameBoard.canPutWorker(this.myNumber, "4-4", "P", "")) {
                    this.putWorker("P", "4-4", "");
                } else {
                    this.putWorker("P", "1-1", "F");
                }
                break;
        }
        this.handNum++;

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
