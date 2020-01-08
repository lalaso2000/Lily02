/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import gameElements.Game;
import gameElements.GameResources;
import java.util.ArrayList;

/**
 *
 * @author niwatakumi
 */
public class NeuralInput {

    String rowVal = "";

    public NeuralInput(Game game, int myNumber, int handCount) {
        int enemyNumber = (myNumber == 0) ? 1 : 0;
        GameResources myResources = game.getResourcesOf(myNumber);
        GameResources enemyResources = game.getResourcesOf(enemyNumber);

        // 入力
        int myMoney = myResources.getCurrentMoney() < 64 ? myResources.getCurrentMoney() : 63;
        rowVal += convertToBinary(myMoney, 6);
        int myFlask = myResources.getCurrentResrchPoint(0);
        rowVal += convertToBinary(myFlask, 4);
        int myGear = myResources.getCurrentResrchPoint(1);
        rowVal += convertToBinary(myGear, 4);
        int myMachine = this.getMachineInput(myResources);
        rowVal += convertToBinary(myMachine, 6);

        int enemyMoney = enemyResources.getCurrentMoney() < 64 ? enemyResources.getCurrentMoney() : 63;
        rowVal += convertToBinary(enemyMoney, 6);
        int enemyFlask = enemyResources.getCurrentResrchPoint(0);
        rowVal += convertToBinary(enemyFlask, 4);
        int enemyGear = enemyResources.getCurrentResrchPoint(1);
        rowVal += convertToBinary(enemyGear, 4);
        int enemyMachine = this.getMachineInput(enemyResources);
        rowVal += convertToBinary(enemyMachine, 6);

        int scoreDiff = myResources.getTotalScore() - enemyResources.getTotalScore();
        if (scoreDiff < 0) {
            rowVal += "1";
        } else {
            rowVal += "0";
        }
        rowVal += convertToBinary(scoreDiff, 5);
        // トレンド
        int seasonClass = game.getSeasonClass();
        int trendDiff = myResources.getSocreOf("T" + seasonClass) - enemyResources.getSocreOf("T" + seasonClass);
        if (trendDiff < 0) {
            rowVal += "1";
        } else {
            rowVal += "0";
        }
        rowVal += convertToBinary(trendDiff, 5);

        // 現在の手数
        rowVal += convertToBinary(handCount, 7);

        // 先行
        if (myResources.isStartPlayer()) {
            rowVal += "0";
        } else {
            rowVal += "1";
        }
    }

    public String getRowVal() {
        return rowVal;
    }
    
    public int getLength() {
        return rowVal.length();
    }

    private int getMachineInput(GameResources resources) {
        int ans = 0;
        for (int i = 0; i < 6; i++) {
            if (resources.hasMachine(i)) {
                ans += Math.pow(2, i);
            }
        }
        return ans;
    }

    static private String convertToBinary(int val, int digits) {
        if (val < 0) {
            val = -val;
        }
        String str = Integer.toBinaryString(val);
        if (str.length() > digits) {
            str = "";
            for (int i = 0; i < digits; i++) {
                str += "1";
            }
            return str;
        } else if (str.length() < digits) {
            int cnt = digits - str.length();
            for (int i = 0; i < cnt; i++) {
                str = "0" + str;
            }
            return str;
        }
        return str;
    }

}
