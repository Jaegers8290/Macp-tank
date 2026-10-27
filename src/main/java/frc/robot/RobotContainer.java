// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

// ============================================================================
// IMPORTACIÓN DE DEPENDENCIAS Y EXPLICACIÓN DE CADA UNA
// ============================================================================

// [WPILib Math]
// Clase utilitaria de funciones matemáticas de WPILib.
// Se utiliza específicamente el método MathUtil.applyDeadband() para eliminar la "zona muerta"
// o drift que ocurre cuando el joystick físico no vuelve exactamente al centro (0.0).
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import frc.robot.Constants.LogitechExtreme3DConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.ShooterIntake;

/**
 * ============================================================================
 * CONTENEDOR PRINCIPAL DEL ROBOT (RobotContainer)
 * ============================================================================
 * En el paradigma Command-Based de FRC, esta clase actúa como el cerebro
 * central
 * de interconexión donde se declaran los subsistemas, los controles del piloto
 * y
 * la asignación de botones (bindings).
 */
public class RobotContainer {

  // ==========================================================================
  // DECLARACIÓN DE SUBSISTEMAS Y DISPOSITIVOS DE ENTRADA (OI)
  // ==========================================================================

  // Instancia única del subsistema de tracción tipo tanque (4 motores CIM con
  // SparkMax)
  private final CANDriveSubsystem m_driveSubsystem = new CANDriveSubsystem();

  // Instancia del subsistema combinado Shooter+Intake (2 motores CIM en SparkMax)
  private final ShooterIntake m_shooterIntake = new ShooterIntake();

  // Instancia del joystick de vuelo Logitech Extreme 3D Pro conectado al puerto
  // USB configurado
  private final CommandJoystick m_driverJoystick = new CommandJoystick(OperatorConstants.kDriverJoystickPort);

  /**
   * Constructor del RobotContainer:
   * Inicializa la configuración de comandos predeterminados y el enlace de
   * botones.
   */
  public RobotContainer() {
    // 1. Asigna el comando por defecto al chasis para teleoperado continuo
    configureDefaultCommands();

    // 2. Asigna las acciones y funciones a los botones físicos del joystick
    configureBindings();
  }

  // ==========================================================================
  // COMANDOS POR DEFECTO (Default Commands)
  // ==========================================================================

  /**
   * Configura qué hace el subsistema cuando ningún otro comando lo está
   * utilizando.
   * En este caso, el chasis siempre escuchará los ejes del joystick para
   * conducir.
   */
  private void configureDefaultCommands() {
    // Asignamos arcadeDriveCommand como comando predeterminado del chasis:
    // Pasa dos funciones Lambda que evalúan en tiempo real la velocidad de avance y
    // giro.
    m_driveSubsystem.setDefaultCommand(
        m_driveSubsystem.arcadeDriveCommand(
            () -> getForwardSpeed(),
            () -> getTurnSpeed()));
  }

  // ==========================================================================
  // CÁLCULO Y FILTRADO DE EJES DEL JOYSTICK DE VUELO
  // ==========================================================================

  /**
   * Calcula la velocidad de avance o retroceso a partir del eje Y de la palanca:
   * - En WPILib, empujar el joystick hacia adelante produce valores negativos
   * (-1.0),
   * por lo que se multiplica por -1 para que avanzar sea positivo (+1.0).
   * - Aplica zona muerta (Deadband) para ignorar vibraciones menores a 0.05.
   * - Aplica el factor de velocidad activo (Normal, Turbo o Precisión).
   *
   * @return Velocidad final de avance entre -1.0 y 1.0.
   */
  private double getForwardSpeed() {
    // Lectura cruda del eje Y invertida (-1 adelante -> +1 hacia adelante)
    double rawY = -m_driverJoystick.getY();
    // Filtro de zona muerta: cualquier valor entre -0.05 y +0.05 se convierte en
    // 0.0 exacto
    double deadbandY = MathUtil.applyDeadband(rawY, OperatorConstants.kJoystickDeadband);
    return deadbandY * getSpeedMultiplier();
  }

  /**
   * Calcula la velocidad de giro (rotación) a partir de la inclinación lateral
   * del joystick:
   * - Aplica zona muerta (Deadband) sobre el eje X.
   * - Aplica el multiplicador de velocidad.
   *
   * @return Velocidad final de rotación entre -1.0 y 1.0.
   */
  private double getTurnSpeed() {
    // Lectura cruda de la inclinación hacia los lados (Eje X)
    // NOTA: Si prefieres girar rotando la empuñadura sobre su eje (timón de cola /
    // rudder),
    // puedes sustituir m_driverJoystick.getX() por m_driverJoystick.getTwist().
    double rawX = m_driverJoystick.getX();

    // Filtro de zona muerta
    double deadbandX = MathUtil.applyDeadband(rawX, OperatorConstants.kJoystickDeadband);

    // Multiplicamos por la escala de velocidad
    return deadbandX * getSpeedMultiplier();
  }

  /**
   * Multiplicador de velocidad controlado por el SLIDER (palanca deslizante en la
   * base):
   * - El slider del Logitech Extreme 3D Pro es el eje 3 (kSliderAxis).
   * - Su rango crudo va de -1.0 (empujado hacia adelante) a +1.0 (jalado hacia
   * atrás).
   * - Se mapea a un rango útil de 0.0 (mínimo) a 1.0 (máximo) con la fórmula:
   * velocidad = (-rawSlider + 1.0) / 2.0
   * 
   * Esto permite al piloto controlar la velocidad del chasis de forma continua
   * usando la pequeña palanca física, como un acelerador de avión.
   *
   * @return Factor multiplicador decimal entre 0.0 y 1.0.
   */
  private double getSpeedMultiplier() {
    // Lectura cruda del slider (eje 3): -1.0 a +1.0
    double rawSlider = m_driverJoystick.getRawAxis(LogitechExtreme3DConstants.kSliderAxis);
    // Mapeo: -1.0 → 1.0 (máxima velocidad), +1.0 → 0.0 (detenido)
    double speed = (-rawSlider + 1.0) / 2.0;
    // Clamp para asegurar que siempre esté entre 0.0 y 1.0
    return Math.max(0.0, Math.min(1.0, speed));
  }

  // ==========================================================================
  // ENLACE DE BOTONES DEL JOYSTICK (Bindings)
  // ==========================================================================

  /**
   * Conecta los botones físicos del joystick de vuelo con comandos del robot.
   *
   * ★★★ AQUÍ SE CONFIGURAN LAS VELOCIDADES DE CADA BOTÓN ★★★
   *
   * Cada moverCommand recibe 2 números:
   *   moverCommand( velocidadIzquierdo, velocidadDerecho )
   *
   *   Positivo (+) = gira en un sentido
   *   Negativo (-) = gira en sentido contrario
   *   Rango: -1.0 (máximo reversa) a +1.0 (máximo adelante)
   */
  private void configureBindings() {
    // ========================================================================
    // BOTÓN 1 (GATILLO / TRIGGER) — SHOOTER (disparar)
    // Motor izquierdo: +0.5 (lanza)  |  Motor derecho: -1.0 (arrastra)
    // ========================================================================
    m_driverJoystick.button(LogitechExtreme3DConstants.kTriggerButton)
        .whileTrue(m_shooterIntake.moverCommand(
            -1,    // ← ★ VELOCIDAD MOTOR IZQUIERDO (shooter/lanzar)
            1    // ← ★ VELOCIDAD MOTOR DERECHO   (arrastrar pelotas)
        ));

    // ========================================================================
    // BOTÓN 7 (BASE, PRIMERA FILA IZQUIERDA) — INTAKE (recoger)
    // Motor izquierdo: -0.5 (absorbe)  |  Motor derecho: +1.0 (absorbe)
    // ========================================================================
    m_driverJoystick.button(LogitechExtreme3DConstants.kBaseButton7)
        .whileTrue(m_shooterIntake.moverCommand(
            -0.5,   // ← ★ VELOCIDAD MOTOR IZQUIERDO (intake/recoger)
            -0.3     // ← ★ VELOCIDAD MOTOR DERECHO   (intake/recoger)
        ));
  }

  // ==========================================================================
  // MÉTODOS DE ACCESO A SUBSISTEMAS (Getters)
  // ==========================================================================

  public CANDriveSubsystem getDriveSubsystem() {
    return m_driveSubsystem;
  }

  public ShooterIntake getShooterIntake() {
    return m_shooterIntake;
  }

  // ==========================================================================
  // RUTINA AUTÓNOMA
  // ==========================================================================

  /**
   * Retorna la rutina autónoma seleccionada para ser ejecutada al inicio de la
   * partida.
   * Este método es llamado automáticamente desde Robot.java en autonomousInit().
   *
   * @return Comando autónomo secuencial.
   */
  public Command getAutonomousCommand() {
    // Ejecuta la rutina definida en Autos que avanza 2 segundos y se detiene
    return Autos.exampleAuto(m_driveSubsystem);
  }
}
