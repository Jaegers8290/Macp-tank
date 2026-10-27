// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

// ============================================================================
// IMPORTACIÓN DE DEPENDENCIAS Y EXPLICACIÓN DE CADA UNA
// ============================================================================

// [WPILib New Commands]
// Representa cualquier acción estructurada que puede ser ejecutada por el robot.
// Se usa como tipo de retorno de la rutina autónoma.
import edu.wpi.first.wpilibj2.command.Command;

// [WPILib New Commands]
// Contenedor de fábricas de comandos para crear secuencias temporizadas y compuestas.
// Se utiliza Commands.sequence() para encadenar acciones una tras otra de forma ordenada,
// y Commands.run() para ejecutar bucles continuos de control durante un tiempo límite.
import edu.wpi.first.wpilibj2.command.Commands;

// [Subsistemas Locales del Proyecto]
// Subsistema de tracción tipo tanque sobre el cual se aplicará la potencia motriz en el autónomo.
import frc.robot.subsystems.CANDriveSubsystem;

/**
 * ============================================================================
 * FÁBRICA DE RUTINAS AUTÓNOMAS (Autos)
 * ============================================================================
 * Centraliza las rutinas pregrabadas para el período autónomo de 15 segundos de FRC.
 */
public final class Autos {

  /**
   * Rutina de autónomo básica para salir de la zona de inicio (Taxi / Mobility):
   * 1. Avanza en línea recta a media potencia (50%) durante 2.0 segundos cronometrados.
   * 2. Al expirar los 2 segundos, se ejecuta stopCommand() para frenar en seco.
   *
   * @param driveSubsystem Instancia del subsistema del chasis requerida para el movimiento.
   * @return Secuencia de comandos lista para ser ejecutada por el CommandScheduler.
   */
  public static Command exampleAuto(CANDriveSubsystem driveSubsystem) {
    return Commands.sequence(
        // Paso 1: Ejecutar avance constante (arcadeDrive con 0.5 hacia adelante y 0.0 de giro)
        // con un límite de tiempo (withTimeout) de 2.0 segundos exactos.
        Commands.run(
            () -> driveSubsystem.arcadeDrive(0.5, 0.0), 
            driveSubsystem
        ).withTimeout(2.0),

        // Paso 2: Frenar y cortar la potencia a los 4 motores SparkMax
        driveSubsystem.stopCommand()
    );
  }

  /**
   * Constructor privado: evita que se instancie esta clase, ya que solo contiene
   * métodos utilitarios estáticos.
   */
  private Autos() {
    throw new UnsupportedOperationException("Esta es una clase utilitaria estática y no debe ser instanciada.");
  }
}
