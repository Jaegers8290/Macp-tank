// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * ============================================================================
 * CLASE GLOBAL DE CONSTANTES (Constants)
 * ============================================================================
 * Centraliza todos los valores fijos del robot para evitar "números mágicos" 
 * en el código. Si cambias un ID CAN, un puerto USB o una calibración,
 * únicamente debes modificarlo en este archivo.
 */
public final class Constants {

  /**
   * ==========================================================================
   * CONSTANTES DEL CHASIS Y MOTORES CIM (DriveConstants)
   * ==========================================================================
   * Configuración física y eléctrica del sistema de tracción diferencial (tanque).
   */
  public static final class DriveConstants {

    // --- IDENTIFICADORES DEL BUS CAN (CAN IDs) ---
    // Cada controlador SparkMax tiene asignado un número único configurado
    // mediante la herramienta REV Hardware Client.
    
    /** ID CAN del motor SparkMax líder del lado izquierdo. */
    public static final int kLeftLeaderId = 1;

    /** ID CAN del motor SparkMax seguidor del lado izquierdo (sigue al ID 1). */
    public static final int kLeftFollowerId = 2;

    /** ID CAN del motor SparkMax líder del lado derecho. */
    public static final int kRightLeaderId = 3;

    /** ID CAN del motor SparkMax seguidor del lado derecho (sigue al ID 3). */
    public static final int kRightFollowerId = 4;

    // --- PROTECCIÓN ELÉCTRICA Y MECÁNICA ---
    /**
     * Límite de corriente inteligente en Amperios (Smart Current Limit).
     * Los motores CIM pueden quemarse o sobrecalentarse si se bloquean contra
     * un obstáculo. 40A es el estándar recomendado en FRC para proteger la electrónica
     * y los breakers de la PDP/PDH.
     */
    public static final int kCurrentLimitAmps = 40;

    // --- SENTIDO DE GIRO DE LAS RUEDAS ---
    /**
     * Inversión del lado izquierdo:
     * Al estar los motores montados en espejo físico, un lado debe girar en sentido
     * contrario al otro para que ambos lados avancen hacia adelante al recibir potencia positiva.
     */
    public static final boolean kLeftInverted = false;

    /** Inversión del lado derecho: normalmente false si el izquierdo está invertido. */
    public static final boolean kRightInverted = true;
  }

  /**
   * ==========================================================================
   * CONSTANTES DEL OPERADOR Y CONDUCCIÓN (OperatorConstants)
   * ==========================================================================
   * Puertos de comunicación con la Driver Station y calibración de sensibilidad.
   */
  public static final class OperatorConstants {

    /**
     * Puerto USB en el que la computadora de la Driver Station detecta el joystick.
     * (Indexado en la pestaña "USB Devices" del FRC Driver Station, típicamente el puerto 0).
     */
    public static final int kDriverJoystickPort = 0;

    /**
     * Zona muerta (Deadband) analógica:
     * Si la palanca del joystick tiene una pequeña holgura mecánica o desgaste y no
     * regresa exactamente a cero (por ejemplo, marca 0.02 en reposo), cualquier valor
     * entre -0.05 y +0.05 se forzará a 0.0 para que el robot no se mueva solo.
     */
    public static final double kJoystickDeadband = 0.05;

    // --- VELOCIDAD DEL CHASIS ---
    /**
     * La velocidad del chasis ahora es controlada de forma continua por el SLIDER
     * (palanca deslizante en la base del joystick, eje 3 / kSliderAxis).
     * El slider se mapea automáticamente de su rango crudo (-1.0 a +1.0)
     * a un multiplicador útil de 0.0 (detenido) a 1.0 (máxima potencia).
     * 
     * Ya no se necesitan perfiles de velocidad por botón (Turbo/Normal/Lento).
     */
  }

  /**
   * ==========================================================================
   * CONSTANTES DEL JOYSTICK LOGITECH EXTREME 3D PRO (Flight Simulator)
   * ==========================================================================
   * Mapeo de canales analógicos y botones físicos reconocidos por WPILib
   * para el control tipo simulador de aviación.
   */
  public static final class LogitechExtreme3DConstants {

    // --- EJES ANALÓGICOS (Valores continuos entre -1.0 y 1.0) ---
    /** Eje X: Inclinación lateral de la palanca (Izquierda = -1.0, Derecha = +1.0). */
    public static final int kXAxis = 0;

    /** Eje Y: Inclinación hacia adelante/atrás (Adelante = -1.0, Atrás = +1.0 en WPILib). */
    public static final int kYAxis = 1;

    /** Eje Z / Twist: Giro de la palanca sobre su propio eje vertical (torsión de timón/rudder). */
    public static final int kTwistAxis = 2;

    /** Eje Slider: Pequeña palanca deslizante situada en la base (potenciómetro de aceleración). */
    public static final int kSliderAxis = 3;

    // --- BOTONES FÍSICOS (Valores booleanos true/false) ---
    /** Botón 1: Gatillo frontal principal (operado con el dedo índice). */
    public static final int kTriggerButton = 1;

    /** Botón 2: Botón lateral grande en la empuñadura (operado con el dedo pulgar). */
    public static final int kThumbButton = 2;

    // Botones ubicados en la parte superior de la cabeza de la palanca:
    /** Botón 3: Botón superior izquierdo inferior. */
    public static final int kTopButton3 = 3;
    /** Botón 4: Botón superior derecho inferior. */
    public static final int kTopButton4 = 4;
    /** Botón 5: Botón superior izquierdo superior. */
    public static final int kTopButton5 = 5;
    /** Botón 6: Botón superior derecho superior. */
    public static final int kTopButton6 = 6;

    // Botones ubicados en la base plana del joystick:
    /** Botón 7: Base, primera fila izquierda. */
    public static final int kBaseButton7 = 7;
    /** Botón 8: Base, primera fila derecha. */
    public static final int kBaseButton8 = 8;
    /** Botón 9: Base, segunda fila izquierda. */
    public static final int kBaseButton9 = 9;
    /** Botón 10: Base, segunda fila derecha. */
    public static final int kBaseButton10 = 10;
    /** Botón 11: Base, tercera fila izquierda. */
    public static final int kBaseButton11 = 11;
    /** Botón 12: Base, tercera fila derecha. */
    public static final int kBaseButton12 = 12;
  }
}
