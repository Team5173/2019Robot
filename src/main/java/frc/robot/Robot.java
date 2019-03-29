package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.XboxController;
//import edu.wpi.first.wpilibj.Compressor;
//import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.cscore.VideoSource;
import edu.wpi.cscore.VideoMode.PixelFormat;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Robot extends TimedRobot {
  DifferentialDrive myDrive;

  XboxController Controller;
  UsbCamera Camera1;
  UsbCamera Camera2;
  VideoSink Server;

  Joystick Joy1;

  VictorSPX leftSpinner, rightSpinner;
  VictorSP Left, Right;

  /*DoubleSolenoid Plunger;
  DoubleSolenoid pushPlunger;*/

  TalonSRX leftLiftMotor, rightLiftMotor, gripperFlipper;
  PowerDistributionPanel powerDistributionPanel = new PowerDistributionPanel();

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //Compressor c;

  public void robotInit() {

    //Usb Cameras
    Camera1 = CameraServer.getInstance().startAutomaticCapture();
    Camera2 = CameraServer.getInstance().startAutomaticCapture();
    Server = CameraServer.getInstance().getServer();
    Camera1.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);
    Camera2.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);
    Camera1.setVideoMode(PixelFormat.kMJPEG, 360, 360, 20);
    Camera2.setVideoMode(PixelFormat.kMJPEG, 360, 360, 20);
    Camera1.setBrightness(30);
    Camera2.setBrightness(30);
    

    //Drive System
    Left = new VictorSP(0);
    Right = new VictorSP(1);
    Right.setInverted(true);
    
    myDrive = new DifferentialDrive(Left, Right);
    myDrive.setRightSideInverted(false);
    //Lift Motor Controllers
    leftLiftMotor = new TalonSRX(4);
    rightLiftMotor = new TalonSRX(5);
    rightLiftMotor.follow(leftLiftMotor);

    //Xbox Controller
    Controller = new XboxController(0);
    Joy1 = new Joystick(1);

    //Spinners
    leftSpinner = new VictorSPX(6);
    rightSpinner = new VictorSPX(7);

    //Arm Flipper
    gripperFlipper = new TalonSRX(8);

    //Pnuematics
    //c = new Compressor(0);
    /*Plunger = new DoubleSolenoid(0, 1);
    pushPlunger = new DoubleSolenoid(2, 3);*/

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }
  public void teleopInit() {
    super.teleopInit();
    //c.setClosedLoopControl(true);
  }

  public void robotPeriodic() {
  }

  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);
  }

  public void autonomousPeriodic() {
    teleop();
    }
  private void teleop(){
    myDrive.arcadeDrive(Controller.getRawAxis(1)*-1, Controller.getRawAxis(0));

    //Use Talon For Flipper
    if (Controller.getRawButton(5)){
      gripperFlipper.set(ControlMode.PercentOutput, 1.0);
    }
    else if(Controller.getRawButton(6)){
      gripperFlipper.set(ControlMode.PercentOutput, -0.45);
    }
    else { gripperFlipper.set(ControlMode.PercentOutput, 0.08); }

    //Invert the right lift motor
    leftLiftMotor.setInverted(true);

    //Lift Motor Driving with Controller
    double liftPower = Controller.getRawAxis(5);
    if (liftPower > 0.5) liftPower = .07;
    else if (liftPower > -0.07) liftPower = -0.07;
    leftLiftMotor.set(ControlMode.PercentOutput, liftPower);



    //Use the Spinners
    rightSpinner.setInverted(true);

    if (Controller.getRawAxis(2) > 0.7){
      leftSpinner.set(ControlMode.PercentOutput, 1);
      rightSpinner.set(ControlMode.PercentOutput, 1);
    }
    else if (Controller.getRawAxis(3) > 0.7){
      leftSpinner.set(ControlMode.PercentOutput, -1);
      rightSpinner.set(ControlMode.PercentOutput, -1);

    }
    else{
      leftSpinner.set(ControlMode.PercentOutput, 0);
      rightSpinner.set(ControlMode.PercentOutput, 0);
    }


    //Use of the Solenoids
    /*if (Controller.getRawButton(3)){
      Plunger.set(DoubleSolenoid.Value.kForward);
    }
    else{
      Plunger.set(DoubleSolenoid.Value.kReverse);
    }

    if (Controller.getAButtonPressed()){
      if (pushPlunger.get() == DoubleSolenoid.Value.kForward){
        pushPlunger.set(DoubleSolenoid.Value.kReverse);
      }
      else{
        pushPlunger.set(DoubleSolenoid.Value.kForward);
      }
    }*/

    SmartDashboard.putNumber("RightLiftMotorCurrent", powerDistributionPanel.getCurrent(2));
    SmartDashboard.putNumber("LeftLiftMotorCurrent", powerDistributionPanel.getCurrent(12));
  }
  public void teleopPeriodic() {
    teleop();
  }
    

  public void testPeriodic() {
  }
}