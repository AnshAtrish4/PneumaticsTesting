package frc.robot.subsystems.launcher;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;


import frc.robot.Ports;
import frc.robot.subsystems.launcher.LauncherStates.*;

public class Launcher {

    double power = .75;

    double anglePower = 0.35;

    private CANSparkMax launchMotor1;
    private CANSparkMax launchMotor2;

    private CANSparkMax flicker;

    private CANSparkMax bigFlipper1;
    private CANSparkMax bigFlipper2;

    private LauncherPID control;

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

        bigFlipper1 = new CANSparkMax(Ports.bigFlipper1, MotorType.kBrushless);
        bigFlipper1.restoreFactoryDefaults();

        bigFlipper1.setSmartCurrentLimit(60);
        bigFlipper1.setIdleMode(IdleMode.kBrake);
        bigFlipper1.setInverted(false);
        bigFlipper1.setOpenLoopRampRate(1);
        bigFlipper1.burnFlash();

        bigFlipper2 = new CANSparkMax(Ports.bigFlipper2, MotorType.kBrushless);
        bigFlipper2.restoreFactoryDefaults();

        bigFlipper2.setIdleMode(IdleMode.kBrake);
        bigFlipper2.setSmartCurrentLimit(60);
        bigFlipper2.setInverted(true);
        bigFlipper2.setOpenLoopRampRate(1);
        bigFlipper2.burnFlash();

        // control = new LauncherPID(launchMotor1.getPIDController(), launchMotor2.getPIDController(), launchMotor1.getEncoder(), launchMotor2.getEncoder(), 
        // bigFlipper1.getPIDController(), bigFlipper2.getPIDController(), bigFlipper1.getAbsoluteEncoder(Type.kDutyCycle), bigFlipper2.getAbsoluteEncoder(Type.kDutyCycle),
        //  flicker.getPIDController(), flicker.getAbsoluteEncoder(Type.kDutyCycle));
    }

    public void periodic(){
        control.setAngleSP(launcherState.position);
        control.setFlickerSP(flickerState.position);
        control.setVoltageSP(launcherVolts.volts);
    }

    public void setLauncherAngle(){
        bigFlipper1.set(anglePower);
        bigFlipper2.set(anglePower);


    }

    public void setReverseLauncherAngle(){
        bigFlipper1.set(-anglePower);
        bigFlipper2.set(-anglePower);

    }

    public void setReverse(){
        launchMotor1.set(-power/2);
        launchMotor2.set(-power/2);
    }
    

    public void setAngleStop(){
        bigFlipper1.set(0.0);
        bigFlipper2.set(0.0);
    }

    public void setLauncherPower() {
        launchMotor1.set(power);
        launchMotor2.set(power);
    }

    public void setLaunchZero(){
        launchMotor1.set(0.0);
        launchMotor2.set(0.0);
    }

    public void setFlickerOn(){
        flicker.set(.5);
    }

    public void setFlickOff(){
        flicker.set(0);
    }

    public void setAngle(){
        bigFlipper1.set(anglePower);
        bigFlipper2.set(anglePower);
    }

    public void setReverseAngle(){
        bigFlipper1.set(-anglePower);
        bigFlipper2.set(-anglePower);
    }

     public void setZeroAngle(){
        bigFlipper1.set(0.0);
        bigFlipper2.set(0.0);
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