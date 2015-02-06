package config;

import overriddenClasses.ShakerJoystick;

public class UserControls {
	public ShakerJoystick driver, operator;
	public int buttonY, buttonX, buttonA, buttonB, buttonLB, buttonRB, buttonSel, buttonStart, buttonLS, buttonRS;
	private int buttons[];
	
	public UserControls(){
		driver   = new ShakerJoystick(0);
		operator = new ShakerJoystick(1);
		buttons = new int[11];
		
		buttonA     = buttons[1]  = 1;
		buttonB     = buttons[2]  = 2;
		buttonX     = buttons[3]  = 3;
		buttonY     = buttons[4]  = 4;
		buttonLB    = buttons[5]  = 5;
		buttonRB    = buttons[6]  = 6;
		buttonSel   = buttons[7]  = 7;
		buttonStart = buttons[8]  = 8;
		buttonLS    = buttons[9]  = 9;
		buttonRS    = buttons[10] = 10;
	}
	
	public void updateControls(int[] newButtons){
		buttonA     = newButtons[1];
		buttonB     = newButtons[2];
		buttonX     = newButtons[3];
		buttonY     = newButtons[4];
		buttonLB    = newButtons[5];
		buttonRB    = newButtons[6];
		buttonSel   = newButtons[7];
		buttonStart = newButtons[8];
		buttonLS    = newButtons[9];
		buttonRS    = newButtons[10];
	}
}
