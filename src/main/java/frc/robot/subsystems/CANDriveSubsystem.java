// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

// [Java Estándar]
// Provee una interfaz funcional que entrega un número decimal (double) al ser evaluada.
// Se usa para pasar las lecturas dinámicas del joystick a los comandos en tiempo real.
import java.util.function.DoubleSupplier;

// [REVLib - REV Robotics]
// Modo de persistencia: se usa para guardar la configuración enviada al controlador
// en su memoria Flash no volátil, evitando que se pierda si el robot se reinicia o pierde energía.
import com.revrobotics.spark.SparkBase.PersistMode;

// [REVLib - REV Robotics]
// Modo de reinicio: se usa para limpiar cualquier configuración previa en el controlador
// antes de cargar los nuevos valores, garantizando un estado inicial limpio y seguro.
import com.revrobotics.spark.SparkBase.ResetMode;

// [REVLib - REV Robotics]
// Define la tecnología física del motor (kBrushless para motores NEO sin escobillas,
// o kBrushed para motores con carbones tipo CIM/775).
import com.revrobotics.spark.SparkLowLevel.MotorType;

// [REVLib - REV Robotics]
// Objeto principal de control del controlador físico SPARK MAX comunicado por bus CAN.
import com.revrobotics.spark.SparkMax;

// [REVLib - REV Robotics]
// Define la respuesta del motor en reposo: kBrake (freno activo electromagnético) o kCoast (inercia libre).
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

// [REVLib - REV Robotics]
// Estructura de configuración moderna de REVLib para concentrar los ajustes (inversión, corriente, seguidor)
// antes de enviarlos al SparkMax en un solo paquete de datos.
import com.revrobotics.spark.config.SparkMaxConfig;

// [WPILib - First Robotics Software]
// Clase que contiene la cinemática de un robot con diferencial (chasis tipo tanque).
// Realiza internamente la mezcla matemática de avance y giro (Arcade Drive y Tank Drive).
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

// [WPILib New Commands]
// Interfaz base de la arquitectura por comandos de FRC que representa una tarea ejecutable.
import edu.wpi.first.wpilibj2.command.Command;

// [WPILib New Commands]
// Clase utilitaria con constructores directos para crear comandos en línea (inline commands)
// como Commands.run() o Commands.runOnce() sin necesidad de crear archivos separados por cada acción.
import edu.wpi.first.wpilibj2.command.Commands;

// [WPILib New Commands]
// Clase abstracta fundamental que representa un componente de hardware del robot (un subsistema).
// Gestiona el bloqueo de recursos para que dos comandos no peleen por los mismos motores al mismo tiempo.
import edu.wpi.first.wpilibj2.command.SubsystemBase;

// [Constantes Locales del Proyecto]
// Importa las constantes físicas y de bus CAN definidas para el chasis en Constants.java.
import frc.robot.Constants.DriveConstants;

/**
 * ============================================================================
 * SUBSISTEMA DE TRACCIÓN (CANDriveSubsystem)
 * ============================================================================
 * 
 * Maneja el chasis tipo tanque utilizando 4 motores CIM (con escobillas / brushed)
 * controlados por SparkMax:
 * - 2 motores en el lado izquierdo (1 líder y 1 seguidor).
 * - 2 motores en el lado derecho (1 líder y 1 seguidor).
 * - Control de aceleración y giro mediante la clase DifferentialDrive de
 * WPILib.
 */
public class CANDriveSubsystem extends SubsystemBase {

  // ==========================================================================
  // HARDWARE: DECLARACIÓN DE MOTORES SPARK MAX PARA MOTORES CIM (CON ESCOBILLAS)
  // ==========================================================================

  // Motor líder del lado izquierdo: recibe las señales de movimiento directas del
  // diferencial
  private final SparkMax m_leftLeader = new SparkMax(DriveConstants.kLeftLeaderId, MotorType.kBrushed);

  // Motor seguidor del lado izquierdo: imita automáticamente al motor líder
  // izquierdo
  private final SparkMax m_leftFollower = new SparkMax(DriveConstants.kLeftFollowerId, MotorType.kBrushed);

  // Motor líder del lado derecho: recibe las señales de movimiento directas del
  // diferencial
  private final SparkMax m_rightLeader = new SparkMax(DriveConstants.kRightLeaderId, MotorType.kBrushed);

  // Motor seguidor del lado derecho: imita automáticamente al motor líder derecho
  private final SparkMax m_rightFollower = new SparkMax(DriveConstants.kRightFollowerId, MotorType.kBrushed);

  // ==========================================================================
  // OBJETO CINEMÁTICO: DIFFERENTIAL DRIVE
  // ==========================================================================
  // Solo se le pasan los motores líderes (m_leftLeader y m_rightLeader).
  // Los seguidores copiarán los comandos del líder vía firmware a través del bus
  // CAN.
  private final DifferentialDrive m_differentialDrive = new DifferentialDrive(m_leftLeader, m_rightLeader);

  /**
   * Constructor del subsistema:
   * Se ejecuta una sola vez cuando el robot enciende y se encarga de parametrizar
   * el comportamiento de los motores SparkMax y el chasis diferencial.
   */
  public CANDriveSubsystem() {

    // ------------------------------------------------------------------------
    // BLOQUE 1: CONFIGURACIÓN DEL MOTOR LÍDER IZQUIERDO
    // ------------------------------------------------------------------------
    SparkMaxConfig leftLeaderConfig = new SparkMaxConfig();
    leftLeaderConfig
        // Sentido de giro invertido para coordinar con la orientación mecánica de los
        // engranajes
        .inverted(DriveConstants.kLeftInverted)
        // Modo freno activo para que el robot se detenga inmediatamente al soltar el
        // joystick
        .idleMode(IdleMode.kBrake)
        // Protección contra sobrecorriente (40 Amperios) para proteger el motor CIM
        .smartCurrentLimit(DriveConstants.kCurrentLimitAmps);

    // ------------------------------------------------------------------------
    // BLOQUE 2: CONFIGURACIÓN DEL MOTOR SEGUIDOR IZQUIERDO
    // ------------------------------------------------------------------------
    SparkMaxConfig leftFollowerConfig = new SparkMaxConfig();
    leftFollowerConfig
        // Modo freno activo para acompañar al líder en la detención
        .idleMode(IdleMode.kBrake)
        // Límite de corriente idéntico al líder
        .smartCurrentLimit(DriveConstants.kCurrentLimitAmps)
        // Instrucción de seguimiento: replica la velocidad y sentido del motor líder
        // izquierdo (ID 1)
        .follow(DriveConstants.kLeftLeaderId);

    // ------------------------------------------------------------------------
    // BLOQUE 3: CONFIGURACIÓN DEL MOTOR LÍDER DERECHO
    // ------------------------------------------------------------------------
    SparkMaxConfig rightLeaderConfig = new SparkMaxConfig();
    rightLeaderConfig
        // Sentido no invertido (lado derecho estándar)
        .inverted(DriveConstants.kRightInverted)
        // Modo freno activo
        .idleMode(IdleMode.kBrake)
        // Límite de corriente de protección
        .smartCurrentLimit(DriveConstants.kCurrentLimitAmps);

    // ------------------------------------------------------------------------
    // BLOQUE 4: CONFIGURACIÓN DEL MOTOR SEGUIDOR DERECHO
    // ------------------------------------------------------------------------
    SparkMaxConfig rightFollowerConfig = new SparkMaxConfig();
    rightFollowerConfig
        // Modo freno activo
        .idleMode(IdleMode.kBrake)
        // Límite de corriente de protección
        .smartCurrentLimit(DriveConstants.kCurrentLimitAmps)
        // Instrucción de seguimiento: replica la velocidad y sentido del motor líder
        // derecho (ID 3)
        .follow(DriveConstants.kRightLeaderId);

    // ------------------------------------------------------------------------
    // BLOQUE 5: TRANSMISIÓN Y PERSISTENCIA DE PARÁMETROS EN CADA SPARK MAX
    // ------------------------------------------------------------------------
    // configure() envía los parámetros por CAN. ResetMode limpia basura previa y
    // PersistMode graba en memoria flash.
    m_leftLeader.configure(leftLeaderConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_leftFollower.configure(leftFollowerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_rightLeader.configure(rightLeaderConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_rightFollower.configure(rightFollowerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    // Desactivamos la comprobación de timeout de seguridad de WPILib
    // para evitar advertencias de "Motor Safety" en la consola de la Driver
    // Station.
    m_differentialDrive.setSafetyEnabled(false);
  }

  // ==========================================================================
  // MÉTODOS DIRECTOS DE CONDUCCIÓN
  // ==========================================================================

  /**
   * Conducción Arcade (un control maneja aceleración y otro dirección).
   *
   * @param xSpeed    Velocidad adelante/atrás (-1.0 a 1.0).
   * @param zRotation Velocidad de giro sobre el eje (-1.0 a 1.0).
   */
  public void arcadeDrive(double xSpeed, double zRotation) {
    // La clase DifferentialDrive calcula matemáticamente la potencia para cada
    // rueda
    m_differentialDrive.arcadeDrive(xSpeed, zRotation);
  }

  /**
   * Conducción tipo Tanque independiente.
   *
   * @param leftSpeed  Potencia aplicada a las ruedas izquierdas (-1.0 a 1.0).
   * @param rightSpeed Potencia aplicada a las ruedas derechas (-1.0 a 1.0).
   */
  public void tankDrive(double leftSpeed, double rightSpeed) {
    m_differentialDrive.tankDrive(leftSpeed, rightSpeed);
  }

  /**
   * Frena y detiene los motores de ambos lados inmediatamente.
   */
  public void stop() {
    m_differentialDrive.stopMotor();
  }

  // ==========================================================================
  // FÁBRICA DE COMANDOS (Command Factories)
  // ==========================================================================

  /**
   * Genera el comando periódico para teleoperado tipo Arcade.
   *
   * @param speedSupplier    Función que entrega la velocidad de avance/retroceso
   *                         (eje Y del joystick).
   * @param rotationSupplier Función que entrega la velocidad de giro (eje X o
   *                         twist del joystick).
   * @return Comando continuo para ejecutar en el bucle principal.
   */
  public Command arcadeDriveCommand(DoubleSupplier speedSupplier, DoubleSupplier rotationSupplier) {
    // Commands.run ejecuta la función continuamente (cada 20 ms) mientras el
    // comando esté activo.
    // 'this' declara que este comando requiere el CANDriveSubsystem, evitando
    // colisiones con otros comandos.
    return Commands.run(
        () -> arcadeDrive(speedSupplier.getAsDouble(), rotationSupplier.getAsDouble()),
        this);
  }

  /**
   * Genera el comando periódico para teleoperado tipo Tanque (control de 2
   * palancas).
   *
   * @param leftSpeedSupplier  Función que entrega la potencia para el lado
   *                           izquierdo.
   * @param rightSpeedSupplier Función que entrega la potencia para el lado
   *                           derecho.
   * @return Comando continuo de conducción tanque.
   */
  public Command tankDriveCommand(DoubleSupplier leftSpeedSupplier, DoubleSupplier rightSpeedSupplier) {
    return Commands.run(
        () -> tankDrive(leftSpeedSupplier.getAsDouble(), rightSpeedSupplier.getAsDouble()),
        this);
  }

  /**
   * Genera un comando instantáneo para detener los motores.
   *
   * @return Comando que ejecuta stop() una sola vez.
   */
  public Command stopCommand() {
    return Commands.runOnce(this::stop, this);
  }
}