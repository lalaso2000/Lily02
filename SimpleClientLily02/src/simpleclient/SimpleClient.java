/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleclient;

import ai.*;
import gameElements.Game;
import gui.ClientGUI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author koji
 */
public class SimpleClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, null, ex);
        }
//        Game myGame = new Game();
        // TajimaAI myAI = new TajimaAI(myGame);
//        Lily3 myAI = new Lily3(myGame);
//        Lily4 myAI = new Lily4(myGame);
//        Lily5 myAI = new Lily5(myGame);
//        Lily5plus myAI = new Lily5plus(myGame);
//        Lily0 myAI = new Lily0(myGame, "weight9-3.csv");
        ClientGUI gui = new ClientGUI();
        
        gui.setVisible(true);
    }
}
