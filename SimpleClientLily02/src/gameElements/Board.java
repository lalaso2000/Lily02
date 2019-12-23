/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameElements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 盤面を管理するクラス HashMapで状態を取得可能で, テキストによる出力も可能
 *
 * -1:誰もおいていない 0:プレイヤー0の設置 1:プレイヤー1の設置 とする。
 *
 * ワーカー配置のメソッドを持つ
 *
 * @author ktajima
 */
public class Board implements Cloneable {

    public static final String[] PLACE_NAMES = {"1-1","2-1","2-2","3-1","3-2","3-3","3-4","4-1","4-2","4-3","4-4","4-5","5-1","5-2","5-3","5-4","5-5","6-1","6-2","6-3","7-1"};
    public static final int PLAYER_COUNT = 2;

    /** 現在の盤面におかれているコマを表すマップ（ただし１－１だけは無限におけるのでここでは表さない）*/
    public HashMap<String,String> boardState;
    /** 1-1におかれたコマのリスト */
    public ArrayList<String> SeminorWorkers;
    /** 設備の利用状況 falsenになっていたら売り切れ */
    public boolean[] machineEnable;
    
    /** 基本的なコンストラクタ */
    public Board(){
        init();
    }
    /** ボードの初期化メソッド */
    private void init(){
        //ボードの初期化
        this.clear();
        //設備を初期状態にする
        this.machineEnable = new boolean[Game.MachinesCount];
        for(int i=0;i<this.machineEnable.length;i++){
            this.machineEnable[i] = true;
        }
    }
    
    /** ボード上におかれたコマを消す（設備の利用状況は変わらない） */
    private void clear(){
        this.boardState = new HashMap<String,String>();
        for(String key:PLACE_NAMES){
            this.boardState.put(key, "");
        }
        this.SeminorWorkers = new ArrayList<String>();
    }
    
    /***
     *  季節の変わり目などにボード上におかれたワーカーをすべて除去する
     */
    public void returnAllWorkers() {
        this.clear();
    }
    
    /** ピース設置可能かの判定
     * @param player 第1引数:プレイヤー番号0または1
     * @param place 第2引数:設置場所
     * @param option 第3引数:コマンドのオプション
     * @return 戻り値は設置可能かどうかのブール値
     */
    public boolean canPutWorker(int player,String place,String option){
        if(player < 0 || player > 1){
            //プレイヤー番号が不正
            return false;
        }
        if(!this.boardState.containsKey(place)){
            //設置場所が不正
            return false;
        }
        //1-1はいくつでもピースを受け入れ可能
        if(place.equals("1-1")){
            if(option.equals("F") || option.equals("G") || option.equals("M")){
                return true;
            } else {
                return false;
            }
        }
        //その場所に既にコマがおかれているかを確認
        if(!this.boardState.get(place).equals("")){
            return false;
        }
        //設置誓約により置けない場合
        if(place.equals("2-1")){
            if(!this.boardState.get("2-2").equals("")){
                return false;
            }
        }
        if(place.equals("2-1")){
            if(option.equals("F") || option.equals("G")){
                return true;
            } else {
                return false;
            }
        }
        //2-2はオプションによる制約あり
        if(place.equals("2-2")){
            if(!this.boardState.get("2-1").equals("")){
                return false;
            }
        }
        
        if(place.equals("3-3")){
            if(!this.boardState.get("3-4").equals("")){
                return false;
            }
        }
        if(place.equals("3-4")){
            if(!this.boardState.get("3-3").equals("")){
                return false;
            }
        }
        if(place.equals("3-4")){
            if(option.equals("F") || option.equals("G")){
                return true;
            } else {
                return false;
            }
        }
        
        if(place.equals("4-1")){
            if(!this.boardState.get("4-2").equals("")){
                return false;
            }
        }
        if(place.equals("4-2")){
            if(!this.boardState.get("4-1").equals("")){
                return false;
            }
        }


        if(place.equals("5-2")){
            if(!this.boardState.get("5-3").equals("")){
                return false;
            }
        }
        if(place.equals("5-3")){
            if(!this.boardState.get("5-2").equals("")){
                return false;
            }
        }
        
        if(place.equals("7-1")){
            //machine関係。もうない場合はtrueにする
            int mc = 0;
            if(option.startsWith("T")){
                try{
                    mc = Integer.parseInt(option.substring(1));
                } catch(NumberFormatException e){
                    return false;
                }
            } else {
                return false;
            }
            
            if(mc >= 0 && mc < this.machineEnable.length ){
                if(this.machineEnable[mc] == false){
                    return false;
                }
            }
        }
        
        //以上の条件に引っかからなければOK
        return true;
    }
    
    /** コマを設置するメソッド
     * @param player 第1引数:プレイヤー番号0または1
     * @param place 第2引数:設置場所
     * @param worker 第3引数:ワーカーの種類
     * @param option 第4引数:設置時のオプション
     * @return 設置ができたらtrue
     */
    public boolean putWorker(int player,String place,String worker,String option){
        if(!this.canPutWorker(player, place,option)){
            return false;
        }
        
        if(place.equals("1-1")){
            this.SeminorWorkers.add(worker+player);
        } else {
            this.boardState.put(place, worker+player);
        }
        if(place.equals("7-1")){
            //machine関係。該当するmachineを消す
            int mc = 0;
            if(option.startsWith("T")){
                try{
                    mc = Integer.parseInt(option.substring(1));
                } catch(NumberFormatException e){
                    return false;
                }
            } else {
                return false;
            }
            
            if(mc < 0 ){
                return false;
            } else if(mc >= this.machineEnable.length ){
                return false;
            }
            this.machineEnable[mc] = false;
        }
        return true;

    }
    
    /** ゼミにおかれたコマの一覧を取得 */
    public ArrayList<String> getSeminorWorkers(){
        return this.SeminorWorkers;
    }
    
    /** ボード上におかれたコマを得るためのマップを取得する,これを使うと1-1もまとめて手に入る */
    public HashMap<String,ArrayList<String>> getWorkersOnBoard(){
         HashMap<String,ArrayList<String>> workers = new  HashMap<String,ArrayList<String>>();
        for(String key:PLACE_NAMES){
            if(key.equals("1-1")){
                if(!this.SeminorWorkers.isEmpty()){
                    workers.put(key, this.SeminorWorkers);
                }
            } else {
                if(!this.boardState.get(key).equals("")){
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(this.boardState.get(key));
                    workers.put(key,list);
                }
            }
        }
        return workers;
    }

    /** 現在のボードの情チアを表示する（CUI用） */
    public void printCurrentBoard(){
        for(String key:PLACE_NAMES){
            if(key.equals("1-1")){
                System.out.print("1-1:");
                System.out.println(this.SeminorWorkers);
            } else {
                System.out.print(key);
                System.out.print(":");
                System.out.println(this.boardState.get(key));
            }
        }
    }

    /** 設備が購入された場合に呼び出す。
     * @param mnumber 第一引数は設備の番号で0-5までの整数　*/
    public void soldMachine(int mnumber) {
        if(mnumber > 0 && mnumber < Game.MachinesCount){
            this.machineEnable[mnumber] = false;
        }
    }

    @Override
    public Board clone() {
        Board cloned = null;

        try {
            cloned = (Board) super.clone();
            cloned.boardState = (HashMap<String, String>) this.boardState.clone();
            cloned.SeminorWorkers = (ArrayList<String>) this.SeminorWorkers.clone();
            cloned.machineEnable = new boolean[this.machineEnable.length];
            for (int i = 0; i < this.machineEnable.length; i++) {
                boolean b = this.machineEnable[i];
                cloned.machineEnable[i] = b;
            }
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }

        return cloned;
    }

}
