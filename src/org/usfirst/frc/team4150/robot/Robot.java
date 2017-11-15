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
	private DoubleSolenoid climbBreak = new DoubleSolenoid(2, 3);
	boolean solenoidPressed = false;
	
	Timer timer = new Timer();

	/**
	 * Robot initialization code here; called when the robot starts
	 */
	@Override
	public void robotInit() {
		drive.setExpiration(0.1); // No Java docs D:
		compressor.setClosedLoopControl(true);
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
			boolean slowMode = stick1.getRawButton(6);
			boolean solenoid = stick1.getRawButton(3);
			
			//solenoid (wip)
			if(solenoid) {
				if(!solenoidPressed) {
					Value v = climbBreak.get();
					switch(v) {
					case kForward:
						climbBreak.set(Value.kReverse);
						break;
					case kOff:
						break;
					case kReverse:
						climbBreak.set(Value.kForward);
						break;
					default:
						break;
					}
					solenoidPressed = true;
				}
			} else {
				solenoidPressed = false;
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
			if(slowMode) {
				right /= 2;
				left /= 2;
			}
			
			if(Math.abs(left) < 0.2 && Math.abs(right) < 0.2) left = right = 0;

			if(right > 0) {
				//left *= 0.8;
			} else {
				right *= 0.8;
			}
			
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
