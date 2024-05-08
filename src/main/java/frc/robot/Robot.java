package frc.robot;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

import org.littletonrobotics.junction.LoggedRobot;



public class Robot extends LoggedRobot {

  private static XboxController driver;

  private final PneumaticHub testHub = new PneumaticHub();
  private final DoubleSolenoid testSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, 0, 1);
  private final Compressor testCompressor = new Compressor(PneumaticsModuleType.REVPH);


  private Command m_autoSelected;

  private SendableChooser<Command> m_chooser = new SendableChooser<>();


  @Override
  public void robotInit() {
 
    driver = new XboxController(0);

    SmartDashboard.putData("Auto choices", m_chooser);

    // testSolenoid.close();
    // testCompressor.enableDigital();
    // testHub.enableCompressorDigital();
    testCompressor.enableAnalog(10, 20);
    testSolenoid.set(Value.kForward);
  }

  @Override

  public void robotPeriodic() {

    CommandScheduler.getInstance().run();

    SmartDashboard.putNumber("Pressure", testCompressor.getPressure());
    SmartDashboard.putNumber("Current", testCompressor.getCurrent());
    SmartDashboard.putNumber("SolenoidR", testSolenoid.getRevChannel());
    SmartDashboard.putNumber("SolenoidF", testSolenoid.getFwdChannel());


    // SmartDashboard.putNumber("Pressure", testHub.getPressure(0));
    if(testSolenoid.get() == Value.kForward)
      SmartDashboard.putString("Solenoid", "kForward");
    if(testSolenoid.get() == Value.kReverse)
      SmartDashboard.putString("Solenoid", "kReverse");
    if(testSolenoid.get() == Value.kOff)
      SmartDashboard.putString("Solenoid", "kOff");
  

  }

  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();


    if (m_autoSelected != null) {
      m_autoSelected.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    if (m_autoSelected != null) {
      m_autoSelected.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {

  
   if(driver.getAButton()){
    testSolenoid.set(Value.kReverse);
   }else{
    testSolenoid.set(Value.kForward);
   }
   
     
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {
  }

}