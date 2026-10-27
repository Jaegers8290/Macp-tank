package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * ==========================================================================
 * SUBSISTEMA COMBINADO: SHOOTER + INTAKE (ShooterIntake)
 * ==========================================================================
 * Controla los 2 motores CIM (con escobillas / Brushed) conectados a
 * controladores REV SPARK MAX por bus CAN.
 *
 * Los mismos 2 motores físicos cumplen doble función:
 * - SHOOTER (lanzar): El motor izquierdo lanza y el derecho arrastra.
 * - INTAKE (recoger): Los motores giran en sentido inverso para absorber
 * piezas.
 *
 * Todas las velocidades se pasan directamente como números en los comandos
 * para facilitar la calibración rápida.
 */
public class ShooterIntake extends SubsystemBase {

    // =========================================================================
    // ★★★ LÍMITE DE CORRIENTE (Amperios) ★★★
    // =========================================================================
    public static final int LIMITE_CORRIENTE_AMPS = 40; // ← Protección para motores CIM

    // =========================================================================
    // Motores físicos
    // =========================================================================
    private final SparkMax motorIzquierdo = new SparkMax(5, MotorType.kBrushed); // ← ID CAN motor izquierdo
    private final SparkMax motorDerecho = new SparkMax(6, MotorType.kBrushed); // ← ID CAN motor derecho

    public ShooterIntake() {
        // Configuración motor izquierdo
        SparkMaxConfig leftConfig = new SparkMaxConfig();
        leftConfig.idleMode(IdleMode.kCoast);
        leftConfig.smartCurrentLimit(LIMITE_CORRIENTE_AMPS);
        motorIzquierdo.configure(leftConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        // Configuración motor derecho
        SparkMaxConfig rightConfig = new SparkMaxConfig();
        rightConfig.idleMode(IdleMode.kCoast);
        rightConfig.smartCurrentLimit(LIMITE_CORRIENTE_AMPS);
        motorDerecho.configure(rightConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    // =========================================================================
    // MÉTODOS DE CONTROL DIRECTO
    // =========================================================================

    /**
     * Establece la potencia de ambos motores con valores independientes.
     *
     * @param velocidadIzquierdo Potencia del motor izquierdo (-1.0 a 1.0). Positivo
     *                           = adelante.
     * @param velocidadDerecho   Potencia del motor derecho (-1.0 a 1.0). Negativo =
     *                           arrastra hacia shooter.
     */
    public void set(double velocidadIzquierdo, double velocidadDerecho) {
        motorIzquierdo.set(velocidadIzquierdo);
        motorDerecho.set(velocidadDerecho);
    }

    /**
     * Detiene ambos motores inmediatamente.
     */
    public void stop() {
        motorIzquierdo.stopMotor();
        motorDerecho.stopMotor();
    }

    // =========================================================================
    // ★★★ FÁBRICA DE COMANDOS — AQUÍ SE CAMBIAN LAS VELOCIDADES ★★★
    // =========================================================================
    //
    // Cada comando recibe las velocidades directamente como números.
    // Para cambiar velocidad o sentido, solo modifica los números aquí abajo.
    //
    // RECORDATORIO DE SENTIDOS:
    // Positivo (+) = gira en un sentido
    // Negativo (-) = gira en sentido contrario
    //
    // Ejemplo: shootCommand(0.5, -1.0)
    // → Motor izquierdo a +0.5 (lanza)
    // → Motor derecho a -1.0 (arrastra pelotas hacia el shooter)
    // =========================================================================

    /**
     * Comando parametrizable: mueve ambos motores a las velocidades indicadas
     * mientras el comando esté activo. Se detiene al soltar el botón.
     *
     * @param velocidadIzquierdo Potencia motor izquierdo (-1.0 a 1.0).
     * @param velocidadDerecho   Potencia motor derecho (-1.0 a 1.0).
     * @return Comando que activa los motores mientras se mantenga presionado.
     */
    public Command moverCommand(double velocidadIzquierdo, double velocidadDerecho) {
        return startEnd(
                () -> set(velocidadIzquierdo, velocidadDerecho),
                this::stop);
    }

    /**
     * Comando de ejecución única para apagar los motores.
     */
    public Command stopCommand() {
        return runOnce(this::stop);
    }
}
