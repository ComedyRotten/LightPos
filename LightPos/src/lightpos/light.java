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
    int intensityOp;
    double intensity;
    int watts;
    boolean power;
    
    public light() {
        this.pos_x = 0;
        this.pos_y = 0;
        this.intensityOp = 0;
        this.power = false;
        switch(this.intensityOp)
        {
            case 0:
                this.watts = 40;
                this.intensity = 167.9544249;
                break;
            case 1:
                this.watts = 60;
                this.intensity = 298.5856442;
                break;
            case 2:
                this.watts = 75;
                this.intensity = 410.5552608;
                break;
            case 3:
                this.watts = 100;
                this.intensity = 597.1712885;
                break;
            case 4:
                this.watts = 150;
                this.intensity = 970.4033438;
                break;
            default:
                // Represents an incorrect value
                this.watts = 0;
                this.intensity = 0;
                break;
        }
    }
    
    public light(int pos_x, int pos_y, int intensityOp, boolean power) {
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.intensityOp = intensityOp;
        this.power = power;
        switch(this.intensityOp)
        {
            case 0:
                this.watts = 40;
                this.intensity = 167.9544249;
                break;
            case 1:
                this.watts = 60;
                this.intensity = 298.5856442;
                break;
            case 2:
                this.watts = 75;
                this.intensity = 410.5552608;
                break;
            case 3:
                this.watts = 100;
                this.intensity = 597.1712885;
                break;
            case 4:
                this.watts = 150;
                this.intensity = 970.4033438;
                break;
            default:
                // Represents an incorrect value
                this.watts = 0;
                this.intensity = 0;
                break;
        }
    }
    
    //enum for initialization
    public int getIntensityOp()
    {
        return this.intensityOp;
    }
    
    //candellas
    public double getIntensity()
    {
        return this.intensity;
    }
    
    //watts
    public int getWatts()
    {
        return this.watts;
    }
    
    //inches
    public int getPos_x() {
        return pos_x;
    }
    
    //inches
    public int getPos_y() {
        return pos_y;
    }
    
    //boolean
    public boolean isOn() {
        return power;
    }
}
