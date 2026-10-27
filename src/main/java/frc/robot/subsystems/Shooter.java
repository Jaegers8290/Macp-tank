package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Subsistema del Shooter (lanzador) utilizando dos motores CIM (con escobillas / Brushed)
 * conectados a controladores REV SPARK MAX por bus CAN.
 */
public class Shooter extends SubsystemBase {

    public static final double SHOOT_SPEED = 0.5;
    public static final double SHOOT_SPEED_RIGHT = 1.0;
    public static final double SHOOT_FULL_SPEED = 1.0;

    private static final double SPIN_UP_DELAY = 0.4;
    private static final double SHOOT_FULL_TIMEOUT = 4.0;

    private final SparkMax motorLeft = new SparkMax(5, MotorType.kBrushed);
    private final SparkMax motorRight = new SparkMax(6, MotorType.kBrushed);

    private final Timer spinUpTimer = new Timer();

    public Shooter() {
        SparkMaxConfig leftConfig = new SparkMaxConfig();
        leftConfig.idleMode(IdleMode.kCoast);
        leftConfig.smartCurrentLimit(40);
        motorLeft.configure(leftConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        SparkMaxConfig rightConfig = new SparkMaxConfig();
        rightConfig.idleMode(IdleMode.kCoast);
        rightConfig.smartCurrentLimit(40);
        motorRight.configure(rightConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    public void shoot() {
        motorLeft.set(-SHOOT_SPEED);
        motorRight.set(SHOOT_SPEED_RIGHT);
    }

    public void shootFull() {
        motorLeft.set(0.5);
        motorRight.set(0.1);
    }

    public void set(double leftSpeed, double rightSpeed) {
        motorLeft.set(leftSpeed);
        motorRight.set(rightSpeed);
    }

    public void stop() {
        motorLeft.stopMotor();
        motorRight.stopMotor();
    }

    public Command shootCommand() {
        return startEnd(this::shoot, this::stop);
    }

    public Command shootFullCommand() {
        return startEnd(this::shootFull, this::stop);
    }

    public Command stopCommand() {
        return runOnce(this::stop);
    }
}
