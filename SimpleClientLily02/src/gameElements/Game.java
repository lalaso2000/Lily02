/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameElements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ゲームの進行状況を管理するクラス 手を打てるプレイヤーや得点を管理する
 *
 * @author koji
 */
public class Game extends Observable implements Cloneable {

    public static final int STATE_WAIT_PLAYER_CONNECTION = 0;
    public static final int STATE_WAIT_PLAYER_PLAY = 1;
    public static final int STATE_SEASON_END = 2;
    public static final int STATE_GAME_END = 4;
    //最大思考時間
    public static long maxThinkingTime = 1000*60*5;
    
    private int CurrentPlayer;
    private Board gameBoard;
    private String[] PlayerName;
    private int gameState;
    private GameResources[] gameResource;
    private int currentSeason;
   
    public int getSeasonClass(){
        if(this.currentSeason == 0 ||this.currentSeason == 1 ||this.currentSeason == 6 ||this.currentSeason == 7){
            return 0;
        } else if(this.currentSeason == 2 ||this.currentSeason == 3 ||this.currentSeason == 8 ||this.currentSeason == 9){
            return 1;
        } else if(this.currentSeason == 4 ||this.currentSeason == 5 ||this.currentSeason == 10 ||this.currentSeason == 11){
            return 2;
        }
        return -1;
    }
    
    public static String[] SEASON_NAMES = {"1a","1b","2a","2b","3a","3b","4a","4b","5a","5b","6a","6b"};
//    private int trendID;
//    public static String[] TREAND_ID_LIST = {"T1","T2","T3"};
    private int currentStartPlayer = 0;
    
    public static final int MachinesCount = 6;
    
    
    /** タイマー */
    private TimerThread timerThread;
    
    public Game(){
        this.init();
    }
    
    /**
     * AIが考える用
     */
    public void startGame() {
        this.PlayerName[0] = "0";
        this.PlayerName[1] = "1";
        this.gameState = STATE_WAIT_PLAYER_PLAY;
    }
    
    /** ゲームの状況を取得する */
    public int getGameState(){
        return this.gameState;
    }
    
    /**
     * 強制的にゲーム状態を変更する（AI用）
     * @param gameState 
     */
    public void setGameState(int gameState) {
        this.gameState = gameState;
    }
    
    public String[] getPlayerName(){
        return this.PlayerName;
    }
    
    public int[] getScore(){
        int[] score = new int[2];
        score[0] = this.gameResource[0].getTotalScore();
        score[1] = this.gameResource[1].getTotalScore();
        return score;
    }
    
    public int getCurrentPlayer(){
        return this.CurrentPlayer;
    }
    
    /** 時間計測開始 */
    public void TimerStart(int PlayerID){
        this.timerThread.StartTimeCount(PlayerID);
    }
    /** 時間計測終了 */
    public void TimerStop(int PlayerID){
        this.timerThread.StopTimeCount(PlayerID);
    }

    public void setObserver(Observer gui) {
        this.addObserver(gui);
    }
    
    public void setTimerObserver(Observer gui) {
        this.timerThread.addObserver(gui);
    }
    
    //以下はボードの状態を変更するメソッドのため、呼び出し時はObserverに必ず通知すること
    
    /** ボードなどの初期化 */
    private void init(){
        this.CurrentPlayer = 0;
        this.gameBoard = new Board();
        
        this.gameResource = new GameResources[2];
        this.gameResource[0] = new GameResources();
        this.gameResource[0].setStartPlayer(true);
        this.gameResource[1] = new GameResources();
        
        this.PlayerName = new String[2];
        this.PlayerName[0] = null;
        this.PlayerName[1] = null;
        
        this.gameState = STATE_WAIT_PLAYER_CONNECTION;
        
        this.currentSeason = 0;
//        this.trendID = -1;
        
        this.timerThread = new TimerThread();
        new Thread(this.timerThread).start();
        
        this.setChanged();
        this.notifyObservers();
    }

    /***
     * 手が打てるか事前に検証するメソッド。実際にはPLAYとやることは変わらないが
     * 呼び出し時の引数により実際にはおかない
     * 
     * @param PlayerID 第１引数：プレイヤー番号0-1
     * @param place 第２引数：コマを置く場所
     * @param workerType 第３引数：workerの種類 PまたはS
     * @param option 第４引数：コマを置くさいのオプション
     * @return 設置可能ならtrueが帰る
     */
    public boolean canPutWorker(int PlayerID, String place, String workerType,String option) {
        return this.play(PlayerID, place, workerType ,option, false);
    }

    
    /***
     * 実際に手を打つメソッド  
     * @param player 第１引数：プレイヤー番号0-1
     * @param place 第２引数：コマを置く場所
     * @param typeOfWorker 第３引数：workerの種類 PまたはS
     * @param option 第４引数：コマを置くさいのオプション
     * @return 設置できたらtrueが帰る
     */
    public boolean play(int player,String place,String typeOfWorker,String option){
        return this.play(player, place, typeOfWorker,option,true);
    }
    /***
     * 実際に手を打つメソッドで最後の引数により打てるかの調査なのかが決まる
     * @param player 第１引数：プレイヤー番号0-1
     * @param place 第２引数：コマを置く場所
     * @param typeOfWorker 第３引数：workerの種類 PまたはS
     * @param option 第４引数：コマを置くさいのオプション
     * @param putmode 第５引数：trueの場合は実際に手を打つ：ここで効果を分ける
     * @return 設置に成功できたらtrueが帰る
     */
    public boolean play(int player,String place,String typeOfWorker,String option,boolean putmode){

        if(this.gameState != STATE_WAIT_PLAYER_PLAY){
            return false;
        }
        if(this.CurrentPlayer != player){
            return false;
        }
        if(!this.gameResource[player].hasWorkerOf(typeOfWorker)){
            return false;
        }
        if(!this.gameBoard.canPutWorker(player, place, option)){
            return false;
        }
        
        //1-1はオプションに合わせて打つだけ
        if(place.equals("1-1")){
            if(putmode){
                this.gameBoard.putWorker(player, place, typeOfWorker, option);
                this.gameResource[player].putWorker(typeOfWorker);
                if(option.toUpperCase().equals("M")){
                    this.gameResource[player].addMoney(1);
                } else if(option.toUpperCase().equals("F")){
                    if(this.gameResource[player].hasMachine(2)){
                        this.gameResource[player].addReserchPoint(1, 0);
                    }
                    this.gameResource[player].addReserchPoint(1, 0);
                } else if(option.toUpperCase().equals("G")){
                    if(this.gameResource[player].hasMachine(1)){
                        this.gameResource[player].addReserchPoint(1, 1);
                    }
                    this.gameResource[player].addReserchPoint(1, 1);
                }
                this.changePlayer();
                this.setChanged();
                this.notifyObservers();
            }
            return true;
        }
        //2-1と2-2もそのまま打てる
        if(place.startsWith("2-1")){
            if(putmode){
                this.gameBoard.putWorker(player, place, typeOfWorker,option);
                if(this.gameResource[player].hasMachine(2)){
                    this.gameResource[player].addReserchPoint(1+1, 0);
                } else {
                    this.gameResource[player].addReserchPoint(1, 0);
                }
                if(option.toUpperCase().equals("F")){
                    if(this.gameResource[player].hasMachine(2)){
                        this.gameResource[player].addReserchPoint(1+1, 0);
                    } else {
                        this.gameResource[player].addReserchPoint(1, 0);
                    }
                } else if(option.toUpperCase().equals("G")){
                    if(this.gameResource[player].hasMachine(1)){
                        this.gameResource[player].addReserchPoint(1+1, 1);
                    } else {
                        this.gameResource[player].addReserchPoint(1, 1);
                    }
                }
                this.gameResource[player].putWorker(typeOfWorker);
                this.changePlayer();
                this.setChanged();
                this.notifyObservers();
            }
            return true;
        }
        if(place.startsWith("2-2")){
            if(putmode){
                this.gameBoard.putWorker(player, place, typeOfWorker,option);
                //現在の値を一度記録
                int baseValue = this.gameResource[player].getCurrentResrchPoint(1);
                if(baseValue >= 1 || baseValue <= 7){
                     if(this.gameResource[player].hasMachine(1)){
                        this.gameResource[player].addReserchPoint(1, 1);
                     }
                }
                if(baseValue == 4 || baseValue == 5){
                    if(this.gameResource[player].hasMachine(0)){
                        this.gameResource[player].addReserchPoint(2, 0);
                    }
                    if(this.gameResource[player].hasMachine(4)){
                        this.gameResource[player].addReserchPoint(baseValue, 1);
                    }
                }
                this.gameResource[player].addReserchPoint(baseValue, 1);
                this.gameResource[player].putWorker(typeOfWorker);
                this.changePlayer();
                this.setChanged();
                this.notifyObservers();
            }
            return true;
        }

        if(place.equals("3-1")){
            if(this.gameResource[player].getCurrentMoney() >= 2){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker,option);
                    if(this.gameResource[player].hasMachine(0)){
                        this.gameResource[player].addReserchPoint(2,1);
                    }
                    if(this.gameResource[player].hasMachine(2)){
                        this.gameResource[player].addReserchPoint(1,0);
                    }
                    if(this.gameResource[player].hasMachine(5)){
                        this.gameResource[player].addReserchPoint(3,0);
                    }
                    this.gameResource[player].addReserchPoint(4,0);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.gameResource[player].addMoney(-2);
                    
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            } else {
                return false;
            }
        }
        if(place.equals("3-2")){
            if(this.gameResource[player].getCurrentMoney() >= 2){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker,option);
                    if(this.gameResource[player].hasMachine(0)){
                        this.gameResource[player].addReserchPoint(2,0);
                    }
                    if(this.gameResource[player].hasMachine(1)){
                        this.gameResource[player].addReserchPoint(1,1);
                    }
                    if(this.gameResource[player].hasMachine(4)){
                        this.gameResource[player].addReserchPoint(4,1);
                    }
                    this.gameResource[player].addReserchPoint(4,1);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.gameResource[player].addMoney(-2);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            } else {
                return false;
            }
        }
                
        if(place.equals("3-3")){
            int cost = 3;
            if(this.gameResource[player].hasMachine(3)){
                cost--;
            }
            if(this.gameResource[player].getCurrentMoney() >= cost){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker,option);
                    //flask2
                    if(this.gameResource[player].hasMachine(2)){
                        this.gameResource[player].addReserchPoint(1,0);
                    }
                    this.gameResource[player].addReserchPoint(2,0);
                    //gear3
                    if(this.gameResource[player].hasMachine(1)){
                        this.gameResource[player].addReserchPoint(1,1);
                    }
                    this.gameResource[player].addReserchPoint(3,1);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.gameResource[player].addMoney(-1*cost);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            } else {
                return false;
            }
        }
        
        if(place.equals("3-4")){
            int cost = 4;
            if(this.gameResource[player].hasMachine(3)){
                cost=3;
            }
            if(this.gameResource[player].hasMachine(5)){
                cost=3;
            }
            if(this.gameResource[player].getCurrentMoney() >= cost){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker,option);
                    if(option.toUpperCase().equals("F")){
                    //flask4
                        if(this.gameResource[player].hasMachine(0)){
                            this.gameResource[player].addReserchPoint(2,1);
                        }
                        if(this.gameResource[player].hasMachine(2)){
                            this.gameResource[player].addReserchPoint(1,0);
                        }
                        if(this.gameResource[player].hasMachine(5)){
                            this.gameResource[player].addReserchPoint(4,0);
                        }
                        this.gameResource[player].addReserchPoint(4,0);
                    } else if(option.toUpperCase().equals("G")){
                    //gear3
                        if(this.gameResource[player].hasMachine(0)){
                            this.gameResource[player].addReserchPoint(2,0);
                        }
                        if(this.gameResource[player].hasMachine(1)){
                            this.gameResource[player].addReserchPoint(1,1);
                        }
                        if(this.gameResource[player].hasMachine(4)){
                            this.gameResource[player].addReserchPoint(5,1);
                        }
                        this.gameResource[player].addReserchPoint(5,1);
                    }
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.gameResource[player].addMoney(-1*cost);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            } else {
                return false;
            }
        }
        
        if(place.equals("4-1")){
            if(this.gameResource[player].getCurrentResrchPoint(0) >= 2){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker,option);
                    this.gameResource[player].addScorePoint(this.getSeasonClass(), 2);
                    this.gameResource[player].addReserchPoint(-2,0);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            }
            return false;
        }
        if(place.equals("4-2")){
            if(this.gameResource[player].getCurrentResrchPoint(1) >= 2){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker,option);
                    this.gameResource[player].addScorePoint(this.getSeasonClass(), 2);
                    this.gameResource[player].addReserchPoint(-2,1);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            }
            return false;
        }
        if(place.equals("4-3")){
            if(this.gameResource[player].getCurrentResrchPoint(1) >= 4){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker,option);
                    this.gameResource[player].addScorePoint(this.getSeasonClass(), 4);
                    this.gameResource[player].addReserchPoint(-4,1);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            }
            return false;
        }
        if(place.equals("4-4")){
            if(this.gameResource[player].getCurrentResrchPoint(0) >= 8){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker,option);
                    this.gameResource[player].addScorePoint(this.getSeasonClass(), 8);
                    this.gameResource[player].addReserchPoint(-8,0);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            }
            return false;
        }

        if(place.equals("4-5")){
            if(this.gameResource[player].getCurrentResrchPoint(1) >= 10){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker,option);
                    this.gameResource[player].addMoney(10);
                    this.gameResource[player].addReserchPoint(-10,1);
                    if(this.gameResource[player].hasMachine(1)){
                        this.gameResource[player].addReserchPoint(1,1);
                    }
                    this.gameResource[player].addReserchPoint(2,1);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            }
            return false;
        }

        if(place.equals("5-1")){
            if(this.gameResource[player].getCurrentResrchPoint(0) >= 5 &&
               this.gameResource[player].getCurrentResrchPoint(1) >= 3 &&
               this.gameResource[player].getCurrentMoney() >= 2){
                    if(putmode){
                        this.gameBoard.putWorker(player, place, typeOfWorker,option);
                        this.gameResource[player].addReserchPoint(-5,0);
                        this.gameResource[player].addReserchPoint(-3,1);
                        this.gameResource[player].addMoney(-2);
                        this.gameResource[player].addScorePoint(this.getSeasonClass(), 10);
                        this.gameResource[player].putWorker(typeOfWorker);
                        this.changePlayer();
                        this.setChanged();
                        this.notifyObservers();
                    }
                    return true;
            }
            return false;
        }

        if(place.equals("5-2")){
            if(this.gameResource[player].getCurrentResrchPoint(0) >= 10 &&
               this.gameResource[player].getCurrentMoney() >= 2){
                    if(putmode){
                        this.gameBoard.putWorker(player, place, typeOfWorker,option);
                        this.gameResource[player].addReserchPoint(-10,0);
                        this.gameResource[player].addMoney(-2);
                        this.gameResource[player].addScorePoint(this.getSeasonClass(), 12);
                        this.gameResource[player].putWorker(typeOfWorker);
                        this.changePlayer();
                        this.setChanged();
                        this.notifyObservers();
                    }
                    return true;
            }
            return false;
        }

        if(place.equals("5-3")){
            if(this.gameResource[player].getCurrentResrchPoint(1) >= 10 &&
               this.gameResource[player].getCurrentMoney() >= 2){
                    if(putmode){
                        this.gameBoard.putWorker(player, place, typeOfWorker,option);
                        this.gameResource[player].addReserchPoint(-10,1);
                        this.gameResource[player].addMoney(-2);
                        this.gameResource[player].addScorePoint(this.getSeasonClass(), 12);
                        this.gameResource[player].putWorker(typeOfWorker);
                        this.changePlayer();
                        this.setChanged();
                        this.notifyObservers();
                    }
                    return true;
            }
            return false;
        }

        if(place.equals("5-4")){
            if(this.gameResource[player].getCurrentResrchPoint(0) >= 7 &&
               this.gameResource[player].getCurrentResrchPoint(1) >= 9 &&
               this.gameResource[player].getCurrentMoney() >= 2){
                    if(putmode){
                        this.gameBoard.putWorker(player, place, typeOfWorker,option);
                        this.gameResource[player].addReserchPoint(-7,0);
                        this.gameResource[player].addReserchPoint(-9,1);
                        this.gameResource[player].addMoney(-2);
                        this.gameResource[player].addScorePoint(this.getSeasonClass(), 18);
                        this.gameResource[player].putWorker(typeOfWorker);
                        this.changePlayer();
                        this.setChanged();
                        this.notifyObservers();
                    }
                    return true;
            }
            return false;
        }
        
        if(place.equals("5-5")){
            if(this.gameResource[player].getCurrentResrchPoint(0) >= 15 &&
               this.gameResource[player].getCurrentMoney() >= 2){
                    if(putmode){
                        this.gameBoard.putWorker(player, place, typeOfWorker,option);
                        this.gameResource[player].addReserchPoint(-15,0);
                        this.gameResource[player].addMoney(-2);
                        this.gameResource[player].addScorePoint(this.getSeasonClass(), 20);
                        this.gameResource[player].putWorker(typeOfWorker);
                        this.changePlayer();
                        this.setChanged();
                        this.notifyObservers();
                    }
                    return true;
            }
            return false;
        }

        if(place.equals("6-1")){
            if(typeOfWorker.equals("P")){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker,option);
                    this.gameResource[player].addMoney(3);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.gameResource[player].setStartPlayer(true);
                    this.gameResource[(player+1)%2].setStartPlayer(false);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            } else {
               return false;
            }
        }
            
        if(place.equals("6-2")){
            if(!typeOfWorker.equals("P")){
                return false;
            }
            if(option.toUpperCase().equals("FF")){
               if(this.gameResource[player].getCurrentResrchPoint(0) < 2){
                   return false;
               }
                if(putmode){
                    this.gameResource[player].addReserchPoint(-2, 0);
                }
            } else if(option.toUpperCase().equals("FG")){
               if(this.gameResource[player].getCurrentResrchPoint(0) < 1){
                   return false;
               }
               if(this.gameResource[player].getCurrentResrchPoint(1) < 1){
                   return false;
               }
                if(putmode){
                    this.gameResource[player].addReserchPoint(-1, 0);
                    this.gameResource[player].addReserchPoint(-1, 1);
                }
            } else if(option.toUpperCase().equals("GG")){
               if(this.gameResource[player].getCurrentResrchPoint(1) < 2){
                   return false;
               }
                if(putmode){
                    this.gameResource[player].addReserchPoint(-2, 1);
                }
            } else {
                return false;
            }
            if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker,option);
                    this.gameResource[player].addMoney(5);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
            }
            return true;
        }

        if(place.equals("6-3")){
            if(!typeOfWorker.equals("P")){
                return false;
            }
            if(this.gameResource[player].getCurrentResrchPoint(0) < 5){
                return false;
            }
            if(putmode){
                this.gameBoard.putWorker(player, place, typeOfWorker,option);
                this.gameResource[player].addReserchPoint(-5, 0);
                this.gameResource[player].addMoney(10);
                this.gameResource[player].putWorker(typeOfWorker);
                this.changePlayer();
                this.setChanged();
                this.notifyObservers();
            }
            return true;
        }
        
        if(place.equals("7-1")){
            if(!typeOfWorker.equals("P")){
                return false;
            }
            if(this.gameResource[player].getCurrentMoney() < 5){
                return false;
            }
            //オプションによるmachineの判定
            int mnumber = 0;
            if(option.startsWith("T")){
                try{
                    mnumber = Integer.parseInt(option.substring(1));
                    if(!this.gameBoard.machineEnable[mnumber]){
                        return false;
                    }
                } catch(NumberFormatException e){
                    return false;
                }
            } else {
                return false;
            }
            //machineの付与
            if(putmode){
                this.gameBoard.putWorker(player, place, typeOfWorker,option);
                this.gameBoard.soldMachine(mnumber);
                if(mnumber == 3){
                    //アシスタントの購入
                    this.gameResource[player].addNewStudent();
                }
                this.gameResource[player].putWorker(typeOfWorker);
                this.gameResource[player].addMoney(-5);
                this.gameResource[player].getMachine(mnumber);
                this.changePlayer();
                this.setChanged();
                this.notifyObservers();
            }
            return true;
        }
        return false;
   }
    
    /** タイムアウトが発生した場合 */
    public void pass(int playerID) {
        /* 強制的にゼミに打たれる？ */
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        this.setChanged();
//        this.notifyObservers();
    }
            
    /** 通常の手番切換え */
    public void changePlayer(){
        //this.timerThread.StopTimeCount(this.CurrentPlayer);

        if(this.gameResource[(this.CurrentPlayer+1)%2].hasWorker()){
            //相手に手がうつせる場合
            this.CurrentPlayer = (this.CurrentPlayer+1)%2;
            //this..StartTimeCount(this.CurrentPlayer);
            this.setChanged();
            this.notifyObservers();
            return;
        } else {
            //相手がもう手が打てない場合
            if(this.gameResource[this.CurrentPlayer].hasWorker()){
                //自分がまだ打てるんであれば、そのまま自分の手番で継続
                //this.timerThread.StartTimeCount(this.CurrentPlayer);
                this.setChanged();
                this.notifyObservers();
                return;
            } else {
                //互いに手が打てないのであれば、季節を進める
                this.CurrentPlayer = -1;
                this.setChanged();
                this.notifyObservers();
                this.gameState = STATE_SEASON_END;
                //表示のために待つならココ
                
                //次のシーズンの準備
                //this.changeNewSeason();
                return;
            }
        }
    }
    
    /** ボードそのもののメソッドを呼び出すための取得 */
    public Board getBoard(){
        return this.gameBoard;
    }
    
    public int setPlayerName(String name){
        if(this.gameState == STATE_WAIT_PLAYER_CONNECTION){
            if(this.PlayerName[0] == null){
                this.setPlayerName(0, name);
                return 0;
            } else if(this.PlayerName[1] == null){
                this.setPlayerName(1, name);
                return 1;
            }
        }
        return -1;
    }
    
    public void setPlayerName(int player,String name){
        if(player>=0 && player < 2){
            this.PlayerName[player] = name;
        }
        if(this.PlayerName[0] != null && this.PlayerName[1] != null){
            this.gameState = STATE_WAIT_PLAYER_PLAY;
        }
        this.setChanged();
        this.notifyObservers();
    }
    
    public GameResources getgetResources(int playerID) {
        return this.gameResource[playerID];
    }

    public ArrayList<String> getWorkerNameOf(String place) {
        return this.gameBoard.getWorkersOnBoard().get(place);
    }

    public String getSeason() {
        return Game.SEASON_NAMES[this.currentSeason];
    }

//    public String getTrend() {
//        if(this.trendID < 0){
//            return "T0";
//        } else if(this.trendID < 3){
//            return Game.TREAND_ID_LIST[this.trendID];
//        } else {
//            return "T0";
//        }
//    }

    public int getScoreOf(String trend, int playerID) {
        return this.gameResource[playerID].getSocreOf(trend);
    }

    /***
     * 季節の進行
     */
    public void changeNewSeason() {
        if(this.gameState == STATE_SEASON_END){
            HashMap<String,ArrayList<String>> workers = this.getBoard().getWorkersOnBoard();
            //発表による業績の獲得
            int ScoreTreand = 0;
            if(this.currentSeason == 0 || this.currentSeason == 1 || this.currentSeason == 6 || this.currentSeason == 7){
                ScoreTreand = 0;
            } else if(this.currentSeason == 2 || this.currentSeason == 3 || this.currentSeason == 8 || this.currentSeason == 9){
                ScoreTreand = 1;
            } else if(this.currentSeason == 4 || this.currentSeason == 5 || this.currentSeason == 10 || this.currentSeason == 11){
                ScoreTreand = 2;
            }
            //スタートプレイヤーの決定
            if(this.gameResource[0].isStartPlayer()){
                this.currentStartPlayer = 0;
            } else if(this.gameResource[1].isStartPlayer()){
                this.currentStartPlayer = 1;
            }
            this.CurrentPlayer = this.currentStartPlayer;

            //ボードのコマを全部戻す
            this.gameResource[0].returnAllWorkers();
            this.gameResource[1].returnAllWorkers();
            this.getBoard().returnAllWorkers();
            
            if(this.currentSeason == 11) {
                //最後の季節の終了
                this.gameState = STATE_GAME_END;
            } else if(this.currentSeason % 2 == 1){
                //奇数は表彰のある季節なので表彰する
                int addmoney = 5;
                if(this.currentSeason == 1 || this.currentSeason == 7){
//                    if(this.getTrend().equals("T1")) { addmoney += 3; }
                    if(this.gameResource[0].getSocreOf("T1") == this.gameResource[1].getSocreOf("T1")){
                        this.gameResource[0].addMoney(addmoney);
                        this.gameResource[1].addMoney(addmoney);
                    } else if(this.gameResource[0].getSocreOf("T1") > this.gameResource[1].getSocreOf("T1")){
                        this.gameResource[0].addMoney(addmoney);
                    } else {
                        this.gameResource[1].addMoney(addmoney);
                    }
                } else if(this.currentSeason == 3 || this.currentSeason == 9){
//                    if(this.getTrend().equals("T2")) { addmoney += 3; }
                    if(this.gameResource[0].getSocreOf("T2") == this.gameResource[1].getSocreOf("T2")){
                        this.gameResource[0].addMoney(addmoney);
                        this.gameResource[1].addMoney(addmoney);
                    } else if(this.gameResource[0].getSocreOf("T2") > this.gameResource[1].getSocreOf("T2")){
                        this.gameResource[0].addMoney(addmoney);
                    } else {
                        this.gameResource[1].addMoney(addmoney);
                    }
                } else if(this.currentSeason == 5){
//                    if(this.getTrend().equals("T3")) { addmoney += 3; }
                    if(this.gameResource[0].getSocreOf("T3") == this.gameResource[1].getSocreOf("T3")){
                        this.gameResource[0].addMoney(addmoney);
                        this.gameResource[1].addMoney(addmoney);
                    } else if(this.gameResource[0].getSocreOf("T3") > this.gameResource[1].getSocreOf("T3")){
                        this.gameResource[0].addMoney(addmoney);
                    } else {
                        this.gameResource[1].addMoney(addmoney);
                    }
                }
                //季節を一つ進める
                this.currentSeason++;
                //雇っているコストのお金を支払う
                this.gameResource[0].payMoneyforTools();
                this.gameResource[1].payMoneyforTools();
                this.gameState = STATE_WAIT_PLAYER_PLAY;
            } else {
                //表彰なく進行する場合
                //季節を一つ進める
                this.currentSeason++;
                //雇っているコストのお金を支払う
                this.gameResource[0].payMoneyforTools();
                this.gameResource[1].payMoneyforTools();
                this.gameState = STATE_WAIT_PLAYER_PLAY;
            }
        }
        this.setChanged();
        this.notifyObservers();
    }

    public int getStartPlayer() {
        if(this.gameResource[0].isStartPlayer()){
            return 0;
        } else {
            return 1;
        }
    }

//    public void setTreand(String treand) {
//        for(int i=0;i<3;i++){
//            String key = TREAND_ID_LIST[i];
//            if(key.equals(treand)){
//                this.trendID = i;
//            }
//        }
//        this.setChanged();
//        this.notifyObservers();
//    }
    
    /***
     * CUI出力用
     * 現在のボードの状態（どこに誰のコマがおいてあるか）を文字列で出力
     * @return 
     */
    public String getBoardInformation() {
        if(this.gameState == STATE_WAIT_PLAYER_CONNECTION){
            return "プレイヤー接続待ち";
        }
        
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_//_/_/_/_/_/_/_/\n");
        sbuf.append("/_/_/_/_/_/_/_/  ボードの状態  /_/_/_/_/_/_/_/\n");
        sbuf.append("/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_//_/_/_/_/_/_/_/\n");
        sbuf.append("1-1 ゼミの配置状況\n");
        sbuf.append(this.gameBoard.getWorkersOnBoard().get("1-1")+"\n");
        sbuf.append("2 図書館の配置状況\n");
        sbuf.append("2-1:"+this.gameBoard.getWorkersOnBoard().get("2-1")+"\n");
        sbuf.append("2-2:"+this.gameBoard.getWorkersOnBoard().get("2-2")+"\n");
        sbuf.append("3 実験の配置状況\n");
        sbuf.append("3-1:"+this.gameBoard.getWorkersOnBoard().get("3-1")+"\n");
        sbuf.append("3-2:"+this.gameBoard.getWorkersOnBoard().get("3-2")+"\n");
        sbuf.append("3-3:"+this.gameBoard.getWorkersOnBoard().get("3-3")+"\n");
        sbuf.append("3-4:"+this.gameBoard.getWorkersOnBoard().get("3-3")+"\n");
        sbuf.append("4 発表の配置状況\n");
        sbuf.append("4-1:"+this.gameBoard.getWorkersOnBoard().get("4-1")+"\n");
        sbuf.append("4-2:"+this.gameBoard.getWorkersOnBoard().get("4-2")+"\n");
        sbuf.append("4-3:"+this.gameBoard.getWorkersOnBoard().get("4-3")+"\n");
        sbuf.append("4-4:"+this.gameBoard.getWorkersOnBoard().get("4-4")+"\n");
        sbuf.append("4-5:"+this.gameBoard.getWorkersOnBoard().get("4-5")+"\n");
        sbuf.append("5 論文の配置状況\n");
        sbuf.append("5-1:"+this.gameBoard.getWorkersOnBoard().get("5-1")+"\n");
        sbuf.append("5-2:"+this.gameBoard.getWorkersOnBoard().get("5-2")+"\n");
        sbuf.append("5-3:"+this.gameBoard.getWorkersOnBoard().get("5-3")+"\n");
        sbuf.append("5-4:"+this.gameBoard.getWorkersOnBoard().get("5-4")+"\n");
        sbuf.append("5-5:"+this.gameBoard.getWorkersOnBoard().get("5-5")+"\n");
        sbuf.append("6 研究報告の配置状況\n");
        sbuf.append("6-1:"+this.gameBoard.getWorkersOnBoard().get("6-1")+"\n");
        sbuf.append("6-2:"+this.gameBoard.getWorkersOnBoard().get("6-2")+"\n");
        sbuf.append("6-3:"+this.gameBoard.getWorkersOnBoard().get("6-3")+"\n");
        sbuf.append("7 設備の設置状況\n");
        sbuf.append("7-1:"+this.gameBoard.getWorkersOnBoard().get("7-1")+"\n");
        sbuf.append("----------------------------------------------\n");
        sbuf.append("設備の利用状況\n");
        for(int i=0;i<Game.MachinesCount;i++){
            sbuf.append("M0"+i+":"+this.gameBoard.machineEnable[i]+"\n");
        }
        sbuf.append("----------------------------------------------\n");
        sbuf.append("時間経過と研究成果\n");
        sbuf.append("現在の季節："+this.getSeason()+"\n");
//        sbuf.append("現在のトレンド："+this.getTrend()+"\n");
        sbuf.append("季節グループ１のスコア：Player0="+this.gameResource[0].getSocreOf("T1")+",Player1="+this.gameResource[1].getSocreOf("T1")+"\n");
        sbuf.append("季節グループ２のスコア：Player0="+this.gameResource[0].getSocreOf("T2")+",Player1="+this.gameResource[1].getSocreOf("T2")+"\n");
        sbuf.append("季節グループ３のスコア：Player0="+this.gameResource[0].getSocreOf("T3")+",Player1="+this.gameResource[1].getSocreOf("T3")+"\n");
        sbuf.append("----------------------------------------------\n");
        if(this.CurrentPlayer == -1){
            sbuf.append("現在季節を進めています\n");
        } else {
            sbuf.append("現在プレイ待ちのプレイヤー：Player"+this.CurrentPlayer+"("+ this.PlayerName[this.CurrentPlayer] +")\n");
        }
        sbuf.append("スタートプレイヤー：Player"+this.currentStartPlayer+"\n");
        sbuf.append("/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_//_/_/_/_/_/_/_/\n");
        return sbuf.toString();
    }

    /***
     * CUI出力用
     * 現在のリソース状態（各プレイヤーが持つリソース）を文字列で出力
     * @return 
     */
    public String getResourceInformation() {
        if(this.gameState == STATE_WAIT_PLAYER_CONNECTION){
            return "プレイヤー接続待ち";
        }

        StringBuilder sbuf = new StringBuilder();
        sbuf.append("/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_//_/_/_/_/_/_/_/\n");
        sbuf.append("/_/_/_/_/_/_/_/  プレイヤーの状態  /_/_/_/_/_/_/_/\n");
        sbuf.append("/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_//_/_/_/_/_/_/_/\n");
        sbuf.append(this.getResourceInformation(0));
        sbuf.append(this.getResourceInformation(1));
        sbuf.append("/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_//_/_/_/_/_/_/_/\n");
        return sbuf.toString();
    }
    
    private String getResourceInformation(int playerID){
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("Player"+playerID+"("+ this.PlayerName[playerID] +")\n");
        sbuf.append("----------------------------------------------\n");
        sbuf.append("1 コマの利用可能状況\n");
        sbuf.append("教授:"+this.gameResource[playerID].hasWorkerOf("P")+"\n");
        sbuf.append("学生:"+this.gameResource[playerID].hasWorkerOf("S")+"\n");
        sbuf.append("学生コマの保持数:");
        sbuf.append(this.gameResource[playerID].getTotalStudentsCount()+"\n");
        sbuf.append("2 資金と研究ポイントの状況\n");
        sbuf.append("お金:"+this.gameResource[playerID].getCurrentMoney()+"\n");
        sbuf.append("研究ポイントF:"+this.gameResource[playerID].getCurrentResrchPoint(0)+"\n");
        sbuf.append("研究ポイントG:"+this.gameResource[playerID].getCurrentResrchPoint(1)+"\n");
        sbuf.append("3 総合得点:"+this.gameResource[playerID].getTotalScore()+"\n");
        sbuf.append("----------------------------------------------\n");
        return sbuf.toString();
    }
    
    public void printMessage(String text){
        this.setChanged();
        this.notifyObservers(text);
    }

    public GameResources getResourcesOf(int i) {
        if(i==0 || i ==1) {
            return this.gameResource[i];
        }
        return null;
    }
    
    public String getPlayerNameOf(int i) {
        if(i==0 || i ==1) {
            return this.PlayerName[i];
        }
        return "";
    }

    public boolean[] getMachinesStatus() {
        return this.gameBoard.machineEnable;
    }
    

    @Override
    public Game clone() {
        Game cloned = null;

        try {
            cloned = (Game) super.clone();
            cloned.gameBoard = this.gameBoard.clone();
            cloned.gameResource = new GameResources[this.gameResource.length];
            for (int i = 0; i < this.gameResource.length; i++) {
                cloned.gameResource[i] = this.gameResource[i].clone();
            }
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }

        return cloned;
    }

}
