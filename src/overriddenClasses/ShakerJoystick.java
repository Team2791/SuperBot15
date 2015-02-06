package overriddenClasses;

import edu.wpi.first.wpilibj.Joystick;

public class ShakerJoystick extends Joystick {
    
    public static double SCALE_FACTOR = 1.0;
    public double deadzone = 0.03;
    
    public ShakerJoystick(final int port){
        super(port);
    }
    
    public void setScaleFactor(double scale){ SCALE_FACTOR = scale; }
    public double getx(){
        double X = super.getX();
        
        if(X < deadzone && X > -deadzone) X = 0;
        X *= SCALE_FACTOR;
        
        return X;
    }
    public double gety(){
        double Y = super.getY();
        
        if(Y  < deadzone && Y > -deadzone) Y = 0;
        Y *= SCALE_FACTOR;
        
        return Y;
    }

	public static double getScaleFactor() {
		return SCALE_FACTOR;
	}
}