package frc.robot.subsystems.intake;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Ports;

public class Intake {

    public enum IntakePosition {
        STOP(0.0),
        GROUND(-9.00039100646973),
        TRAP(0.0),
        HANDOFF(-3.999995708465576);

        public double position;

        private IntakePosition(double position) {
            this.position = position;
        }
    }

    private CANSparkMax roller;
    private CANSparkMax flipper;

    // private IntakePID control;

    public IntakePosition intakePosition = IntakePosition.STOP;
    public static Intake instance;

    private double power = .4;

    private double flip = 0.25;

    private ArmFeedforward feedforward;
    private SparkMaxPIDController flipperController;

    private PIDController dumbyController;

    private RelativeEncoder relativeEncoder;

    private boolean[] connections = new boolean[4];

    private double veloSP = .02;
    private double startTime = 0.0;
    private double timeElapsed = 0.0;

    public Intake() {
        roller = new CANSparkMax(Ports.roller, MotorType.kBrushless);
        roller.restoreFactoryDefaults();

        roller.setSmartCurrentLimit(40);
        roller.setIdleMode(IdleMode.kCoast);
        roller.setInverted(false);
        roller.burnFlash();

        flipper = new CANSparkMax(Ports.flipper, MotorType.kBrushless);
        flipper.restoreFactoryDefaults();

        flipper.setSmartCurrentLimit(70);
        flipper.setIdleMode(IdleMode.kBrake);
        flipper.setInverted(true);
        flipper.burnFlash();

        feedforward = new ArmFeedforward(0.037, 0.05, 0, 0);
        //.037

        // prototype numbers
        // low bound: .022 upper bound:.047 ks: .0125 kg: .0345

        relativeEncoder = flipper.getEncoder();

        flipperController = flipper.getPIDController();
        flipperController.setFeedbackDevice(relativeEncoder);
        flipperController.setOutputRange(-1.0, 1.0);

        flipperController.setP(IntakeConstants.flipperPCoefficient);
        flipperController.setI(IntakeConstants.flipperICoefficient);
        flipperController.setD(IntakeConstants.flipperDCoefficient);

        dumbyController = new PIDController(.08, 0, 0);
    }

    public void periodic() {

        flipper.set(dumbyController.calculate(relativeEncoder.getPosition(), intakePosition.position) + feedforward.calculate(0, veloSP));

        // flipperController.setReference(intakePosition.position, ControlType.kPosition, 0,
        //         feedforward.calculate(relativeEncoder.getPosition(), veloSP));

        // if (intakePosition == IntakePosition.HANDOFF && hasReachedPose(.36)) {

        // // if (Launcher.hasReachedPose(.55)) {

        // if (startTime == 0.0) {
        // startTime = Timer.getFPGATimestamp();
        // }

        // timeElapsed = Timer.getFPGATimestamp() - startTime;
        // SmartDashboard.putNumber("Time Elpased", timeElapsed);

        // if (timeElapsed < .32 && timeElapsed != Timer.getFPGATimestamp()) {
        // setRollerPower();
        // } else if (timeElapsed > .32 && timeElapsed < 2 && timeElapsed !=
        // Timer.getFPGATimestamp()) {
        // setRollerOff();
        // } else if ( timeElapsed < 3 && timeElapsed > 2 && timeElapsed !=
        // Timer.getFPGATimestamp()) {
        // setReverseRollerPower();
        // } else if (timeElapsed > 3 && timeElapsed != Timer.getFPGATimestamp()) {
        // setRollerOff();
        // }
        // }
        // .31 works

    }

    public double getStartTime() {
        return startTime;
    }

    public double getTimeElapsed() {
        return timeElapsed;
    }

    public void resetStartTime() {
        startTime = 0.0;
    }

    public void reverseFlipper() {
        flipper.set(-flip + feedforward.calculate(relativeEncoder.getPosition(), veloSP));
    }

    public void setRollerPower() {
        roller.set(power);
    }

    public void setReverseRollerPower() {
        roller.set(-power);
    }

    public void setFlipperPower() {
        flipper.set(flip);
        // flipper.set(flip + feedforward.calculate(relativeEncoder.getPosition(),
        // veloSP));
    }

    public void setReverseFlipperPower() {
        flipper.set(-flip);
    }

    public void setFlipperOff() {
        flipper.set(0.0);
    }

    public void setRollerOff() {
        roller.set(0);
    }

    public double getRollerCurrent() {
        return roller.getOutputCurrent();
    }

    public double getFlipperVoltage() {
        return flipper.getBusVoltage();
    }

    public double getFlipperCurrent(){
        return flipper.getOutputCurrent();
    }

    public double getFlipperPosition() {
        return relativeEncoder.getPosition();
    }

    public double getFlipperVelocitySetpoint() {
        return veloSP;
    }

    public String getIntakeState() {
        return intakePosition.toString();
    }

    public boolean hasReachedPose(double tolerance) {
        if (Math.abs(relativeEncoder.getPosition() - intakePosition.position) > tolerance) {
            return false;
        }
        return true;
    }

    public void setIntakeState(IntakePosition state) {
        intakePosition = state;
    }

    public boolean[] intakeConnections() {
        if (roller.getBusVoltage() != 0) {
            connections[0] = true;
        } else {
            connections[0] = false;
        }

        if (roller.getOutputCurrent() != 0) {
            connections[1] = true;
        } else {
            connections[1] = false;
        }

        if (flipper.getBusVoltage() != 0) {
            connections[2] = true;
        } else {
            connections[2] = false;
        }

        if (flipper.getOutputCurrent() != 0) {
            connections[3] = true;
        } else {
            connections[3] = false;
        }

        return connections;
    }

    public void printConnections() {
        SmartDashboard.putBoolean("roller Voltage", connections[0]);
        SmartDashboard.putBoolean("roller Current", connections[1]);

        SmartDashboard.putBoolean("flipper Voltage", connections[2]);
        SmartDashboard.putBoolean("flipper Current", connections[3]);
    }

    public static Intake getInstance() {
        if (instance == null)
            instance = new Intake();
        return instance;
    }
}