/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameElements;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kosen
 */
public class GameResources implements Cloneable {

    //機材の利用コスト()
    public final int MachinesCost[] = {2,1,1,2,2,2};

    //以下は個別に持つ値
    //所持金
    private int money;
    //研究成果
    private int[] reserchPoint;
    //得点
    private int[] score;
    //負債の数
    private int debtCount;
    //使用前のコマ　[0]学生 [1]該当なし [2]教授
    private int[] workerList;
    //使用済みのコマ　[0]学生 [1]該当なし [2]教授
    private int[] usedWorkers;
    //スタートプレーヤか否か
    private boolean startPlayerFlag;
    //設備の配備状況
    private boolean[] machines;
    
    public GameResources(){
        this.money = 5;
        this.reserchPoint = new int[2];
        this.reserchPoint[0] = 0;
        this.reserchPoint[1] = 0;
        this.score = new int[3];
        this.score[0] = 0;
        this.score[1] = 0;
        this.score[2] = 0;
        this.workerList = new int[3];
        this.workerList[0] = 1;
        this.workerList[1] = 0;
        this.workerList[2] = 2;
        this.usedWorkers = new int[3];
        this.debtCount = 0;
        this.startPlayerFlag = false;
        
        this.machines = new boolean[MachinesCost.length];
        for(int i=0;i<MachinesCost.length;i++){
             this.machines[i] = false;
        }
    }

    public boolean hasWorkerOf(String typeOfWorker) {
        if(typeOfWorker.equals("P")){
            return (this.workerList[0] > 0);
        } else if (typeOfWorker.equals("S")){
            return (this.workerList[2] > 0);
        }
        return false;
    }

    public int getCurrentMoney() {
        return this.money;
    }

    public void addMoney(int i) {
        this.money += i;
    }
    
    public void putWorker(String typeOfWorker) {
        if(this.hasWorkerOf(typeOfWorker)){
            if(typeOfWorker.equals("P")){
                this.workerList[0]--;
                this.usedWorkers[0]++;
            } else if (typeOfWorker.equals("S")){
                this.workerList[2]--;
                this.usedWorkers[2]++;
            }
        }
    }

    public int getCurrentResrchPoint(int type) {
        return this.reserchPoint[type];
    }

    public void addReserchPoint(int point,int type) {
        this.reserchPoint[type] += point;
        if(this.reserchPoint[type] > 15){
            this.reserchPoint[type] = 15;
        }
    }

    public boolean hasWorker() {
        return ((this.workerList[0]+this.workerList[1]+this.workerList[2]) > 0);
    }

    public int getTotalScore() {
        return this.score[0]+this.score[1]+this.score[2]-3*this.debtCount;
    }

    public boolean isStartPlayer() {
        return this.startPlayerFlag;
    }

    public void addScorePoint(int scoreTreand,int point) {
        this.score[scoreTreand] += point;
    }

    public void addNewStudent() {
        this.workerList[2]++;
    }

    public void addNewAssistant() {
        this.usedWorkers[1]++;
    }

    public void returnAllWorkers() {
        for(int i=0;i<3;i++){
            this.workerList[i] = this.usedWorkers[i];
            this.usedWorkers[i] = 0;
        }
    }

    public void payMoneyforTools() {
        for(int m=0;m<MachinesCost.length;m++){
            if(this.machines[m]){
                this.money -= MachinesCost[m];
            }
        }
        if(this.money < 0 ) {
            this.debtCount += (-1)*this.money;
            this.money = 0;
        }
    }

    public void setStartPlayer(boolean b) {
        this.startPlayerFlag = b;
    }

    public int getTotalStudentsCount() {
        return this.workerList[2] + this.usedWorkers[2];
    }

    public int getNumberofUseableWorkers(String typeOfWorker) {
        if(typeOfWorker.equals("P")){
            return this.workerList[0];
        } else if (typeOfWorker.equals("A")){
            return this.workerList[1];
        } else if (typeOfWorker.equals("S")){
            return this.workerList[2];
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getDebt() {
        return this.debtCount;
    }

    //機材の有無
    public boolean hasMachine(int i) {
        return this.machines[i];
    }
    public void getMachine(int i){
        this.machines[i] = true;
    }
    
    public int getSocreOf(String trend) {
        if(trend.equals("T1")){
            return this.score[0];
        } else if(trend.equals("T2")){
            return this.score[1];
        } else if(trend.equals("T3")){
            return this.score[2];
        }
        return -1;
    }

    @Override
    public GameResources clone() {
        GameResources cloned = null;
        try {
            cloned = (GameResources) super.clone();
            cloned.reserchPoint = new int[this.reserchPoint.length];
            for (int i = 0; i < reserchPoint.length; i++) {
                int r = this.reserchPoint[i];
                cloned.reserchPoint[i] = r;
            }
            cloned.score = new int[this.score.length];
            for (int i = 0; i < score.length; i++) {
                int s = this.score[i];
                cloned.score[i] = s;
            }
            cloned.workerList = new int[this.workerList.length];
            for (int i = 0; i < workerList.length; i++) {
                int w = this.workerList[i];
                cloned.workerList[i] = w;
            }

            cloned.usedWorkers = new int[this.usedWorkers.length];
            for (int i = 0; i < usedWorkers.length; i++) {
                int usedWorker = this.usedWorkers[i];
                cloned.usedWorkers[i] = usedWorker;
            }
            cloned.machines = new boolean[this.machines.length];
            for (int i = 0; i < machines.length; i++) {
                boolean m = this.machines[i];
                cloned.machines[i] = m;
            }
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(GameResources.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cloned;
    }

}
