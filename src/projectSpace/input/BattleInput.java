/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectSpace.input;

/**
 *
 * @author jhovanni
 */
public final class BattleInput {

    public static final String CURSOR_LEFT = "CursorLeft";
    public static final String CURSOR_RIGHT = "CursorRight";
    public static final String CURSOR_DOWN = "CursorDown";
    public static final String CURSOR_UP = "CursorUp";
    public static final String SHOOT = "Shoot";
    public static final String BUILD = "Build";
    public static final String COMMAND = "Command";
    public static final String SELECT = "Select";

    public static final String[] INPUTS;

    static {
        INPUTS = new String[]{CURSOR_LEFT, CURSOR_RIGHT, CURSOR_DOWN, CURSOR_UP, SHOOT, BUILD, COMMAND, SELECT};
    }

}
