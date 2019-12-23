/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

/**
 *
 * @author niwatakumi
 */
public class Action {
    public String worker;
    public String place;
//    public String trend;
    public String option;
    
    public Action(String worker, String place){
        this.worker = worker;
        this.place = place;
        this.option = "";
    }
    
    public Action(String worker, String place, String option){
        this.worker = worker;
        this.place = place;
        this.option = option;
    }
    
    @Override
    public String toString(){
        if(!option.equals("")){
            return worker + " : " + place + "(" + option + ")";
        }
        else{
            return worker + " : " + place;
        }
    }
}
