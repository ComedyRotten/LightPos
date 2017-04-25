/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lightpos;

/**
 *  class: light
 * @author reube
 * 
 */
class light {
    int pos_x;
    int pos_y;
    int watts;
    boolean power;
    
    public light() {
        this.pos_x = 0;
        this.pos_y = 0;
        this.watts = 60;
        this.power = false;
    }
    
    public light(int pos_x, int pos_y, int watts, boolean power) {
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.watts = watts;
        this.power = power;
    }
    
}
