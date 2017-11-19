/**
 * @since 11/4/2017
 */

package org.usfirst.frc.team4150.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends IterativeRobot {
	
	private RobotDrive drive = new RobotDrive(0, 1); // class that handles basic drive
	private Joystick stick1 = new Joystick(0); // set to ID 1 in DriverStation
	private Compressor compressor = new Compressor(0);
	private DoubleSolenoid idkSolenoid = new DoubleSolenoid(0, 1);
	private DoubleSolenoid gearPlatform = new DoubleSolenoid(2, 3);
	private DoubleSolenoid gearArms = new DoubleSolenoid(4, 5);
	private boolean xPressed = false;
	private boolean yPressed = false;
	
	
	Timer timer = new Timer();

	/**
	 * Robot initialization code here; called when the robot starts
	 */
	@Override
	public void robotInit() {
		drive.setExpiration(0.1); // No Java docs D:
		compressor.setClosedLoopControl(true);
		gearPlatform.set(Value.kReverse);
		gearArms.set(Value.kReverse);
	}
	
	/**
	 * Main code for teleop; periodically called until teleop ends
	 */
	@Override
	public void teleopPeriodic() {
		if(stick1 != null) {
			//get controller input
			double fb = stick1.getY(); // forward & backward
			double lr = stick1.getRawAxis(4)/2; //left & right
			boolean rightBumper = stick1.getRawButton(6);
			boolean xButton = stick1.getRawButton(3);
			boolean yButton = stick1.getRawButton(4);
			
			//front solenoids
			if(xButton) {
				if(!xPressed) {
					Value v = gearArms.get();
					switch(v) {
					//if the arms are closed
					case kForward:
						//let gear out
						gearArms.set(Value.kReverse); //open the arms
						gearPlatform.set(Value.kReverse); //lower the platform
						break;
					//if the arms are open
					case kReverse:
						//keep gear in
						gearPlatform.set(Value.kForward); //raise the platform
						gearArms.set(Value.kForward); //close the arms
						break;
					default:
						break;
					}
					xPressed = true;
				}
			} else {
				xPressed = false;
			}
			
			//solenoid (wip)
			if(yButton) {
				if(!yPressed) {
					Value v = idkSolenoid.get();
					switch(v) {
					case kForward:
						idkSolenoid.set(Value.kReverse);
						break;
					case kReverse:
						idkSolenoid.set(Value.kForward); //raise the platform
						break;
					default:
						break;
					}
					yPressed = true;
				}
			} else {
				yPressed = false;
			}
			
			//start out with just forward and backward
			double left = fb;
			double right = fb;
			
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
			if(rightBumper) {
				right /= 2;
				left /= 2;
			}
			
			if(Math.abs(left) < 0.2 && Math.abs(right) < 0.2) left = right = 0;
			
			//set left and right motor power output
			drive.setLeftRightMotorOutputs(limit(left), limit(right));
		}
	}
	
	/**
	 * Init code for autonomous; called once before it starts
	 */
	@Override
	public void autonomousInit() {
		timer.reset();
		timer.start();
	}
	
	/**
	 * Main autonomous code; called periodically until autonomous ends
	 */
	@Override
	public void autonomousPeriodic() {
		// Drive for 2 seconds
		if (timer.get() < 2.0) {
			drive.drive(-0.5, 0.0); // drive forwards half speed
		} else {
			drive.drive(0.0, 0.0); // stop robot
			
		}
	}

	/**
	 * Limit motor values to the -1.0 to +1.0 range
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
