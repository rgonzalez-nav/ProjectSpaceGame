/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace;

/**
 *
 * @author rafagonz
 */
public class Main {
    //float time;
    public void initMain() {
        BattleManager initBattle = new BattleManager();
        initBattle.start();
    }
    
    public static void main(String[] args){
        Main app = new Main();
        app.initMain();
    }
}
