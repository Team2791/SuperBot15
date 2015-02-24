package gameRunners;

import edu.wpi.first.wpilibj.Timer;

public class AutonDriver{
    protected double maxOutput = 1.0;
    protected double minOutput = -1.0;
    protected double previousTime = 0.0;
    protected double currentTime = 0.0;
    protected double P, I, D;
	
    protected double setpoint = 0.0;
    protected double previousError = 0.0;
    protected double currentError = 0.0;
    protected double integrator = 0.0;
    protected double output = 0.0;
    protected double deadZone = 0.0;
    protected boolean disabled = false;
    
    protected boolean PID_IN_USE = false;
    
    public AutonDriver(double p, double i, double d){
        P = p;
        I = i;
        D = d;
    }
    
    public void update_values(double p, double i, double d){
        P = p;
        I = i;
        D = d;
    }
    
    public void reset(){ // pls fix
        
    }
    
    public void disable(){
    	PID_IN_USE = false;
    	reset();
    }
    
    // ---------------------------------------------------------------------------------    
    
    public void driveDistance(double distance){
    	PID_IN_USE = true;
    	setpoint = distance;
    }
    
    public void setTarget(double angle){
    	PID_IN_USE = true;
    	setpoint = angle;
    }
    
    public double getIPart(){
    	if(previousTime == 0.0 || I == 0.0)
    		return 0.0;
    	
    	integrator += ((currentError + previousError) / 2.0) * (currentTime - previousTime);
    	
    	if(integrator * I > maxOutput)
    		integrator = maxOutput / I;
    	if(integrator * I < minOutput)
    		integrator = minOutput / I;
    	
    	return integrator * I;
    }

    public double getDPart(){
    	if(previousTime == 0.0 || D == 0.0)
    		return 0.0;
    	else
    		return D * ((currentError - previousError) / (currentTime - previousTime));
    }
    
    public double getPPart(){
    	return currentError * P;
    }
    
    public double updateOutput(double currentVal){
    	double normalOutput = updateAndGetOutput(currentVal);
    	
    	if(currentError < deadZone && currentError > -deadZone)
        	return 0.0;
        else
        	return normalOutput;
    }
    
    public double updateAndGetOutput(double cVal){
    	currentTime = Timer.getFPGATimestamp();
    	currentError = cVal - setpoint;
    	double newOutput = getPPart() + getIPart() + getDPart();
    	previousTime = Timer.getFPGATimestamp();
    	previousError = currentError;
    	
    	if(disabled)
    		return 0;
    	
    	if(newOutput > maxOutput)
    		newOutput = maxOutput;
    	if(newOutput < minOutput)
    		newOutput = minOutput;
    	
    	return newOutput;
    }
    
    public double getSetpoint(){ return setpoint; }
    public void setMinOutput(double min) {        minOutput = min; }
    public void setMaxOutput(double max) {        maxOutput = max; } 
    public double getError() { return currentError; }
    public void setDeadzone(double zone) {        deadZone = zone; }
    public void setDisabled(boolean disabled) { this.disabled = disabled; }
}
