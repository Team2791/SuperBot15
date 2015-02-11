package gameRunners;

import edu.wpi.first.wpilibj.Timer;

public class AutonDriver{
    protected double maxOutput = 1.0;
    protected double minOutput = -1.0;
    protected double previousTime = 0.0;
    protected double currentTime = 0.0;
	protected double P, I, D;
	
    protected double setpointX = 0.0;
    protected double previousErrorX = 0.0;
    protected double currentErrorX = 0.0;
    protected double integratorX = 0.0;
    protected double outputX = 0.0;
    
    protected double setpointY = 0.0;
    protected double previousErrorY = 0.0;
    protected double currentErrorY = 0.0;
    protected double integratorY = 0.0;
    protected double outputY = 0.0;
    
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
        resetX();
        resetY();
    }
    public void resetX(){}
    public void resetY(){}
    
    public void disable(){
    	PID_IN_USE = false;
    	reset();
    }
    
    // ---------------------------------------------------------------------------------    
    
    public void driveDistance(double x, double y, boolean cont){
    	
    }
    
    public double getIXPart(){
    	if(previousTime == 0.0 || I == 0.0)
    		return 0.0;
    	
    	integratorX += ((currentErrorX + previousErrorX) / 2.0) * (currentTime - previousTime);
    	
    	if(integratorX * I > maxOutput)
    		integratorX = maxOutput / I;
    	if(integratorX * I < minOutput)
    		integratorX = minOutput / I;
    	
    	return integratorX * I;
    }
    public double getIYPart(){
    	if(previousTime == 0.0 || I == 0.0)
    		return 0.0;
    	
    	integratorY += ((currentErrorY + previousErrorY) / 2.0) * (currentTime - previousTime);
    	
    	if(integratorY * I > maxOutput)
    		integratorY = maxOutput / I;
    	if(integratorY * I < minOutput)
    		integratorY = minOutput / I;
    	
    	return integratorY * I;
    }

    public double getDXPart(){
    	if(previousTime == 0.0 || D == 0.0)
    		return 0.0;
    	else
    		return D * ((currentErrorX - previousErrorX) / (currentTime - previousTime));
    }
    public double getDYPart(){
    	if(previousTime == 0.0 || D == 0.0)
    		return 0.0;
    	else
    		return D * ((currentErrorY - previousErrorY) / (currentTime - previousTime));
    }
    
    public double getPXPart(){
    	return currentErrorX * P;
    }
    public double getPYPart(){
    	return currentErrorY * P;
    }
    
    public double updateOutputX(double currentVal){
    	return updateAndGetOutputX(currentVal);
    }
    public double updateOutputY(double currentVal){
    	return updateAndGetOutputY(currentVal);
    }
    
    public double updateAndGetOutputX(double cVal){
    	currentTime = Timer.getFPGATimestamp();
    	currentErrorX = cVal - setpointX;
    	double newOutput = getPXPart() + getIXPart() + getDXPart();
    	previousTime = Timer.getFPGATimestamp();
    	previousErrorX = currentErrorX;
    	
    	if(newOutput > maxOutput)
    		newOutput = maxOutput;
    	if(newOutput < minOutput)
    		newOutput = minOutput;
    	
    	return newOutput;
    }
    public double updateAndGetOutputY(double cVal){
    	currentTime = Timer.getFPGATimestamp();
    	currentErrorY = cVal - setpointY;
    	double newOutput = getPYPart() + getIYPart() + getDYPart();
    	previousTime = Timer.getFPGATimestamp();
    	previousErrorY = currentErrorY;
    	
    	if(newOutput > maxOutput)
    		newOutput = maxOutput;
    	if(newOutput < minOutput)
    		newOutput = minOutput;
    	
    	return newOutput;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
