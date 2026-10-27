/*
 * ============================================================================
 * ARCHIVO DE RESPALDO — CÓDIGO ANTERIOR (NO FUNCIONAL)
 * ============================================================================
 * Este archivo contiene el subsistema Intake ORIGINAL antes de combinarse
 * con el Shooter en ShooterIntake.java.
 * 
 * NOTA: Este subsistema usaba los MISMOS CAN IDs (5 y 6) que el Shooter,
 * lo cual causaba conflicto. Por eso se combinaron en un solo subsistema.
 * 
 * Está completamente comentado para referencia. NO se compila ni se ejecuta.
 * El subsistema activo es: ShooterIntake.java
 * ============================================================================
 *

package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {

    public static final double INTAKE_SPEED_LEFT = -0.5;
    public static final double INTAKE_SPEED_RIGHT = -1.0;

    public static final double EJECT_SPEED_LEFT = -0.5;
    public static final double EJECT_SPEED_RIGHT = 1.0;

    private final SparkMax motorLeft = new SparkMax(5, MotorType.kBrushed);
    private final SparkMax motorRight = new SparkMax(6, MotorType.kBrushed);

    public Intake() {
        SparkMaxConfig leftConfig = new SparkMaxConfig();
        leftConfig.idleMode(IdleMode.kCoast);
        leftConfig.smartCurrentLimit(40);
        motorLeft.configure(leftConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        SparkMaxConfig rightConfig = new SparkMaxConfig();
        rightConfig.idleMode(IdleMode.kCoast);
        rightConfig.smartCurrentLimit(40);
        motorRight.configure(rightConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    public void intake() {
        motorLeft.set(INTAKE_SPEED_LEFT);
        motorRight.set(INTAKE_SPEED_RIGHT);
    }

    public void eject() {
        motorLeft.set(EJECT_SPEED_LEFT);
        motorRight.set(EJECT_SPEED_RIGHT);
    }

    public void set(double leftSpeed, double rightSpeed) {
        motorLeft.set(leftSpeed);
        motorRight.set(rightSpeed);
    }

    public void stop() {
        motorLeft.stopMotor();
        motorRight.stopMotor();
    }

    public Command intakeCommand() {
        return startEnd(this::intake, this::stop);
    }

    public Command ejectCommand() {
        return startEnd(this::eject, this::stop);
    }

    public Command stopCommand() {
        return runOnce(this::stop);
    }
}

*/
