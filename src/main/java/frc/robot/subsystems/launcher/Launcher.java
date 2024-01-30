package frc.robot.subsystems.launcher;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.Ports;
import frc.robot.subsystems.launcher.LauncherStates.*;

public class Launcher {

    double power = -.5;

    private boolean test = true;

    private CANSparkMax launchMotor1;
    private CANSparkMax launchMotor2;

    private CANSparkMax flicker;

    private CANSparkMax launcherAngle1;
    private CANSparkMax launcherAngle2;

    // private LauncherPID control;

    private AbsoluteEncoder angleEncoder1;
    private AbsoluteEncoder angleEncoder2;


    private static LauncherState launcherState = LauncherState.RETRACTED;
    private static LauncherVoltage launcherVolts = LauncherVoltage.OFF;
    private static FlickerState flickerState = FlickerState.IN;

    public static Launcher instance;

    public Launcher() {
        launchMotor1 = new CANSparkMax(Ports.flywheel1, MotorType.kBrushless);
        launchMotor1.restoreFactoryDefaults();

        launchMotor1.setSmartCurrentLimit(60);
        launchMotor1.setIdleMode(IdleMode.kCoast);
        launchMotor1.setInverted(false);
        launchMotor1.burnFlash();

        launchMotor2 = new CANSparkMax(Ports.flywheel2, MotorType.kBrushless);
        launchMotor2.restoreFactoryDefaults();

        launchMotor2.setSmartCurrentLimit(60);
        launchMotor2.setIdleMode(IdleMode.kCoast);
        launchMotor2.setInverted(true);
        launchMotor2.burnFlash();

        flicker = new CANSparkMax(Ports.flicker, MotorType.kBrushless);
        flicker.restoreFactoryDefaults();

        flicker.setSmartCurrentLimit(20);
        flicker.setIdleMode(IdleMode.kBrake);
        flicker.setInverted(false);
        flicker.burnFlash();

        launcherAngle1 = new CANSparkMax(Ports.bigFlipper1, MotorType.kBrushless);
        launcherAngle1.restoreFactoryDefaults();

        launcherAngle1.setSmartCurrentLimit(60);
        launcherAngle1.setIdleMode(IdleMode.kBrake);
        launcherAngle1.setInverted(false);
        launcherAngle1.burnFlash();

        launcherAngle2 = new CANSparkMax(Ports.bigFlipper2, MotorType.kBrushless);
        launcherAngle2.restoreFactoryDefaults();

        launcherAngle2.setIdleMode(IdleMode.kBrake);
        launcherAngle2.setSmartCurrentLimit(60);
        launcherAngle2.setInverted(true);
        launcherAngle2.burnFlash();

        // control = new LauncherPID(launchMotor1, launchMotor2, launcherAngle1, launcherAngle2, flicker);
    }

    public void periodic(){
        if (test){
            setLauncherPower();
        }

        // control.setAngleSP(launcherState.position);
        // control.setFlickerSP(flickerState.position);
        // control.setVoltageSP(launcherVolts.volts);
        
    }

    public void setLauncherAngle(double angle1, double angle2){
        launcherAngle1.set(angle1);
        launcherAngle2.set(angle2);

    }

    public void setReverse(){
        launchMotor1.set(-power);
        launchMotor2.set(-power);
    }
    

    public void setAngleStop(){
        launcherAngle1.set(0.0);
        launcherAngle2.set(0.0);
    }

    public void setLauncherPower() {
        launchMotor1.set(power);
        launchMotor2.set(power);
    }

    public void setLaunchZero(){
        launchMotor1.set(0.0);
        launchMotor2.set(0.0);
    }

    public void increasePower(){
        power += .1;
    }

    public void decreasePower(){
        power -= .1;
    }

    public double getPower(){
        return power;
    }

    public double getLauncherPosition() {
        return (angleEncoder1.getPosition() + angleEncoder2.getPosition())/2;
    }

    public boolean hasReachedPose(double tolerance) {
        if (Math.abs(getLauncherPosition() - launcherState.position) > tolerance) {
            return true;
        }
            return false;
    }

    public void setTest(){
        test = !test;
    }

    public void setFlickerState(FlickerState state){
        flickerState = state;
    }

    public void setLauncherState(LauncherState state) {
        launcherState = state;
    }

     public void setLauncherVolts(LauncherVoltage state){
        launcherVolts = state;
    }

    public static Launcher getInstance() {
        if(instance == null)
            instance = new Launcher();
        return instance;
    }
}
