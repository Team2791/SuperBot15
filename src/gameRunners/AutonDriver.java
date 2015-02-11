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
    	return updateAndGetOutput(currentVal);
    }
    
    public double updateAndGetOutput(double cVal){
    	currentTime = Timer.getFPGATimestamp();
    	currentError = cVal - setpoint;
    	double newOutput = getPPart() + getIPart() + getDPart();
    	previousTime = Timer.getFPGATimestamp();
    	previousError = currentError;
    	
    	if(newOutput > maxOutput)
    		newOutput = maxOutput;
    	if(newOutput < minOutput)
    		newOutput = minOutput;
    	
    	return newOutput;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
