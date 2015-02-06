package subsystems;

import edu.wpi.first.wpilibj.Timer;

public class Analyze {
	double[] mVals;
	public Timer analyzerTimer;
	
	public Analyze(){}
	
	public void teleopInit(){}
	
	public void teleopPeriodic(){}
	
	public void teleopEnd(){}
	
	public double round(double a){ return (double)(((int)(Math.round(a * 100.0))) / 100.0); }
	public double round(int a){ return (double)(((int)(Math.round(a * 100.0))) / 100.0); }
}
