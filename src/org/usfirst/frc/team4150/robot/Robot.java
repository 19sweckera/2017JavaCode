/**
 * @since 11/4/2017
 */

package org.usfirst.frc.team4150.robot;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends SampleRobot {
	RobotDrive robot = new RobotDrive(0, 1); // class that handles basic drive
	// operations
	Joystick leftStick = new Joystick(0); // set to ID 1 in DriverStation
	Joystick rightStick = new Joystick(1); // set to ID 2 in DriverStation

	public Robot() {
		robot.setExpiration(0.1);
	}
	
	@Override
	public void operatorControl() {
		robot.setSafetyEnabled(true);
		while (isOperatorControl() && isEnabled()) {
			if(leftStick != null && rightStick != null) {
				//get controller input
				double fb = leftStick.getY(); // forward & backward
				double lr = leftStick.getRawAxis(4)/2; //left & right
				boolean slowMode = leftStick.getRawButton(6);
				
				//start out with just forward and backward
				double left = fb;
				double right = fb;
				
				//apply turning
				//left += lr;
				//right -= lr;
				
				//apply turning v2
				if (fb > 0.0) {
					if (lr > 0.0) {
						left = fb - lr;
						right = Math.max(fb, lr);
					} else {
						left = Math.max(fb, -lr);
						right = fb + lr;
					}
				} else {
					if (lr > 0.0) {
						left = -Math.max(-fb, lr);
						right = fb + lr;
					} else {
						left = fb - lr;
						right = -Math.max(-fb, -lr);
					}
				}
				
				//apply slow mode
				if(slowMode) {
					right /= 2;
					left /= 2;
				}

				//set left and right motor power output
				robot.setLeftRightMotorOutputs(limit(left), limit(right));
			}
			Timer.delay(0.005); // wait for a motor update time
		}
	}

	/**
	 * Limit motor values to the -1.0 to +1.0 range.
	 */
	static double limit(double num) {
		if (num > 1.0) {
			return 1.0;
		}
		if (num < -1.0) {
			return -1.0;
		}
		return num;
	}
}
