/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
 
package frc.robot;
 
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
 
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANEncoder; 
 
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
 
  private CANSparkMax leftMotor1, leftMotor2, rightMotor1, rightMotor2;
  SpeedControllerGroup left_m, right_m;
  //DifferentialDrive teleop_drive;
  Joystick driveStick = new Joystick(0);
  private CANEncoder encoder_left;
  private CANEncoder encoder_right;

  public double lmcurr;
  public double rmcurr;
  //State
  public static int state;
  //Holds target position 
  double leftEncoderTarget;
  double rightEncoderTarget;
  
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
//}
  @Override
  public void robotInit() {
    // m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    // m_chooser.addOption("My Auto", kCustomAuto);
    // SmartDashboard.putData("Auto choices", m_chooser);
 
    leftMotor1 = new CANSparkMax(3, MotorType.kBrushless);
    leftMotor2 = new CANSparkMax(2, MotorType.kBrushless);
    rightMotor1 = new CANSparkMax(9, MotorType.kBrushless);
    rightMotor2  = new CANSparkMax(8, MotorType.kBrushless);
 
    left_m = new SpeedControllerGroup(leftMotor1,leftMotor2);
    right_m = new SpeedControllerGroup(rightMotor1,rightMotor2);
    //teleop_drive = new DifferentialDrive(left_m, right_m);
    encoder_left = leftMotor1.getEncoder();
    encoder_right = rightMotor1.getEncoder(); 
 
    leftMotor1.setInverted(true);
    leftMotor2.setInverted(true);
 
    lmcurr = encoder_left.getPosition();
    rmcurr = encoder_right.getPosition();

    state = 0;

    leftEncoderTarget = 0;
    rightEncoderTarget = 0;
 
 
  }
 
  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
 
    //System.out.println(" * L:  " + lmcurr + "\t" + "  R:   " + rmcurr);
 
  }
 
  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
 
  }
 
  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {

  //FSM Setup
  if (state == 0){
    // System.out.println(state);
     setDistanceTarget(6);
     state = 1;
   }
   if (state == 1){
     //System.out.println(state);
     forwardDrive(2);  
   }
   if(state == 2){
     //setDistanceTarget(5);
     //state = 3;
     stopDrive();
  }
}

//Takes in the distance to travel and converts to the final position encoders must reach.
  public void setDistanceTarget(double distanceToTravel)
  {
    leftEncoderTarget = -(encoder_left.getPosition() + (distanceToTravel*4.7));
    rightEncoderTarget = encoder_right.getPosition() + (distanceToTravel*4.7); 
  }

//Takes in the next state to continue to once distance completed. USes a speed correction
//algorithm to maintain straight path.(** increase adjustSpeed value for left lean and decrease for right lean.)
  private void forwardDrive( int nextState) {
    // leftMotor1.set(0.1);
    // leftMotor2.set(0.1);
    // rightMotor1.set(0.1);
    // rightMotor2.set(0.1);
 
    double adjustSpeed = 0.021;
    double threshold = 2;
    double differenceof_rotation = lmcurr - rmcurr;

    //Track whether encoders reached target position.
    boolean leftReachedTarget = false;
    boolean rightReachedTarget = false;

//     if (Math.abs(differenceof_rotation) > threshold){
//       if(differenceof_rotation > (0)){
//         adjustSpeed = 0.021;
//       }
//       if(differenceof_rotation < (0)){
//         adjustSpeed = -0.021;
//       }
//   }
//   leftMotor1.set(0.15 - adjustSpeed );
//   leftMotor1.set(0.15 - adjustSpeed );
//   rightMotor1.set(0.15 + adjustSpeed);
//   rightMotor1.set(0.15 + adjustSpeed);
// }
if (lmcurr > leftEncoderTarget)
{
  leftMotor1.set(0.15 - adjustSpeed);
  leftMotor2.set(0.15 - adjustSpeed);
}
else{
  leftMotor1.set(0.0);
  leftMotor2.set(0.0);
  leftReachedTarget = true;
}

if (rmcurr < rightEncoderTarget)
{
  rightMotor1.set(0.15 + adjustSpeed);
  rightMotor2.set(0.15 + adjustSpeed);
}
else{
  rightMotor1.set(0.0);
  rightMotor2.set(0.0);
  rightReachedTarget = true;
}
if(leftReachedTarget && rightReachedTarget){
  state = nextState;
}

//Ends robot movement by setting motors back to zero.
public void stopDrive(){
  leftMotor1.set(0.0);
  leftMotor2.set(0.0);
  rightMotor1.set(0.0);
  rightMotor2.set(0.0);
}
//Turns robot **Add axis turn code here!!
public void turnDrive(){
  leftMotor1.set(-0.24);
  leftMotor2.set(-0.24);
  rightMotor1.set(-0.24);
  rightMotor2.set(-0.24);
}


 
  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
  }
 
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
 

