# MARSIANO - Análisis Técnico del Proyecto

> Documentación técnica del robot MARSIANO, desarrollado por STZ Robotics para la temporada FRC 2026.
> Basado en el MARS Framework — una arquitectura modular propia del equipo.

---

## Tabla de Contenidos

1. [Introducción](#1-introducción)
2. [Tecnologías y Dependencias](#2-tecnologías-y-dependencias)
3. [Estructura de Directorios](#3-estructura-de-directorios)
4. Flujo de Inicio: Main → Robot → RobotContainer *(próximamente)*
5. El Manifest y los Feature Flags *(próximamente)*
6. Arquitectura de Módulos *(próximamente)*
7. El Patrón IO *(próximamente)*
8. El Patrón Request *(próximamente)*
9. Bindings de Controladores *(próximamente)*
10. Autonomía con PathPlanner *(próximamente)*
11. Telemetría y AdvantageScope *(próximamente)*
12. Features Instaladas *(próximamente)*

---

## 1. Introducción

MARSIANO es el código del robot competitivo de STZ Robotics para la temporada **FRC 2026** (FIRST Robotics Competition). El robot es un **shooter con swerve drive** — es decir, puede desplazarse en cualquier dirección mientras sus ruedas giran de forma independiente — y es capaz de recolectar y disparar "fuel" (piezas de juego) incluso mientras se mueve.

El proyecto está construido sobre el **MARS Framework**, una arquitectura modular desarrollada internamente por STZ Robotics que propone una forma ordenada de organizar subsistemas, abstraer el hardware y reutilizar código entre temporadas.

### Lo que puede hacer el robot

- Conducir en swerve drive con control de campo (field-centric)
- Recolectar fuel con un brazo de intake
- Transportar fuel internamente con un indexer de doble rueda
- Apuntar con una torreta giratoria (±85 grados)
- Ajustar el ángulo de disparo con un brazo (hood)
- Disparar con dos flywheels (lanzadores de rueda)
- Ejecutar rutas autónomas pre-programadas con PathPlanner
- Trepar con un mecanismo de climber de gancho

### Estado actual del hardware

El robot está configurado para funcionar con hardware real (`CURRENT_MODE = RunMode.REAL`). Algunas funcionalidades están deshabilitadas intencionalmente:

| Feature | Estado | Razón |
|---|---|---|
| `HAS_LIMELIGHT` | `false` | Deshabilitado explícitamente ("NO CAMBIAR") |
| `HAS_QUESTNAV` | `false` | Quest navigation no disponible |
| `HAS_MARS_GCS` | `false` | Ground Control Station no activo |

---

## 2. Tecnologías y Dependencias

El proyecto usa varias librerías externas que se declaran en la carpeta `vendordeps/` y se gestionan con Gradle.

### Librerías principales

| Librería | Versión | Para qué se usa | Documentación |
|---|---|---|---|
| **WPILib** | 2026.1.1 | Framework base de FRC: TimedRobot, Command Scheduler, SmartDashboard | [docs.wpilib.org](https://docs.wpilib.org/en/stable/) |
| **CTRE Phoenix 6** | 26.1.1 | Motores Kraken X60, Swerve Drive modular, TalonFX | [pro.docs.ctr-electronics.com](https://pro.docs.ctr-electronics.com/en/stable/) |
| **REV Robotics** | latest | Motores SparkMax (torreta e indexer) | [docs.revrobotics.com](https://docs.revrobotics.com/) |
| **PathPlanner** | 2026.1.2 | Generación y seguimiento de rutas autónomas | [pathplanner.dev](https://pathplanner.dev/home.html) |
| **MARS** | 1.6.0 | Arquitectura modular: subsistemas, requests, builders | [Mars-marketplace](https://github.com/Imcab/Mars-marketplace) |
| **ForgeMini** | 1.1.1 | Wrapper de NetworkTables para publicar datos al dashboard | [github.com/STZ-Robotics](https://github.com/STZ-Robotics) |

### Features del MARS Marketplace instaladas

Además de las librerías base, el proyecto instala "features" — extensiones modulares del ecosistema MARS — declaradas en la carpeta `workspace/features/`:

| Feature | Versión | Función |
|---|---|---|
| **DictionaryFeature** | 1.0.0 | Diccionario de strings para logging estructurado |
| **MARSPoseFinderCTRESwerve** | 1.0.0 | Navegación a coordenadas de campo con PathPlanner + CTRE |
| **MarsProcessor** | 1.0.5 | Annotation processor del framework MARS |
| **UnitProcessor** | 1.0.3 | Procesador de unidades tipadas para telemetría |

### Controladores de motor por subsistema

| Subsistema | Controlador | Tipo de motor |
|---|---|---|
| Torreta (Turret) | SparkMax (ID 19) | NEO (REV) |
| Brazo (Arm) | TalonFX | Kraken X60 (CTRE) |
| Intake | TalonFX | Kraken X60 (CTRE) |
| Indexer | SparkMax | NEO (REV) |
| Flywheel Shooter | TalonFX | Kraken X60 (CTRE) |
| Flywheel Intake | TalonFX | Kraken X60 (CTRE) |
| Climber | TalonFX | Kraken X60 (CTRE) |
| Swerve Drive (x4) | TalonFX + CANcoder | Kraken X60 (CTRE) |

---

## 3. Estructura de Directorios

A continuación se muestra el árbol de carpetas del proyecto con una breve explicación de cada parte. La convención de MARS separa claramente la **configuración** del **hardware** y de la **lógica de control**.

```
MARSIANO/
├── build.gradle                          # Configuración de Gradle: dependencias, tareas, deployment
├── settings.gradle                       # Nombre del proyecto Gradle
├── ProjectUnits.json                     # Definición de unidades para telemetría (UnitProcessor)
│
├── vendordeps/                           # Librerías externas en formato JSON (instaladas con WPILib)
│   ├── Phoenix6-26.1.1.json              # CTRE Phoenix 6 (Kraken, Swerve)
│   ├── REVLib.json                       # REV Robotics (SparkMax)
│   ├── PathplannerLib-2026.1.2.json      # PathPlanner (rutas autónomas)
│   ├── Mars.json                         # MARS Framework
│   ├── ForgeMini.json                    # ForgeMini (NetworkTables wrapper)
│   ├── WPILibNewCommands.json            # WPILib Command-Based
│   └── questnavlib.json                  # QuestNav (deshabilitado)
│
├── workspace/
│   └── features/                         # Features del MARS Marketplace instaladas
│       ├── DictionaryFeature.json
│       ├── MARSPoseFinderCTRESwerve.json
│       ├── MarsProcessor.json
│       └── UnitProcessor.json
│
└── src/main/java/frc/robot/
    │
    ├── Main.java                         # Punto de entrada: llama a RobotBase.startRobot(Robot::new)
    ├── Robot.java                        # Clase principal: extiende TimedRobot, maneja ciclo de vida
    ├── RobotContainer.java               # Contenedor central: instancia todos los subsistemas y bindings
    ├── BuildConstants.java               # Auto-generado por Gradle: info de commit git, fecha, rama
    │
    ├── configuration/                    # Todo lo que configura el robot (no el hardware en sí)
    │   ├── Manifest.java                 # Feature flags + clases Builder para cada subsistema
    │   ├── KeyManager.java               # Claves de NetworkTables para telemetría
    │   │
    │   ├── constants/                    # Constantes numericas del robot
    │   │   ├── Constants.java            # Tolerancias, coordenadas del hub, tablas de interpolación
    │   │   └── ModuleConstants/          # Constantes específicas por módulo
    │   │       ├── SwerveConstants.java
    │   │       ├── TurretConstants.java
    │   │       ├── ArmConstants.java
    │   │       ├── FlywheelConstants.java
    │   │       ├── IntakeConstants.java
    │   │       ├── IndexerConstants.java
    │   │       ├── ClimberConstants.java
    │   │       ├── VisionConstants.java
    │   │       └── TunerConstants.java   # Parámetros del Swerve (generados por Phoenix Tuner X)
    │   │
    │   ├── bindings/                     # Mapeo de botones de controlador a comandos
    │   │   ├── DriverBindings.java       # Conducción: joystick → drivetrain
    │   │   ├── OperatorBindings.java     # Operación: botones → superstructure
    │   │   ├── AutoBindings.java         # Comandos nombrados para PathPlanner
    │   │   └── TestBindings.java         # Comandos de modo test
    │   │
    │   └── advantageScope/              # Nodos de visualización para AdvantageScope
    │       └── visuals/nodes/
    │           ├── visualizer/           # Modelo 3D del robot (torreta, hood, intake)
    │           ├── trajectory/           # Visualización de trayectorias
    │           └── gamepiece/            # Visualización de piezas de juego con física
    │
    ├── core/                            # El hardware real del robot
    │   └── modules/
    │       ├── swerve/                  # Drivetrain (CTRE Swerve)
    │       │   ├── CommandSwerveDrivetrain.java   # Subsistema principal del drivetrain
    │       │   ├── SwerveRequestFactory.java      # Requests de movimiento (field-centric, brake, etc.)
    │       │   └── SwerveTelemetry.java           # Logging del estado del swerve
    │       │
    │       └── superstructure/          # Todos los mecanismos de disparo
    │           ├── composite/           # CompositeSubsystem: agrega todos los módulos
    │           │   ├── Superstructure.java         # Comandos de alto nivel (shoot, eat, clear)
    │           │   ├── SuperstructureIO.java        # Agrega los IOs de todos los módulos
    │           │   └── SuperstructureData.java      # Snapshot de estado de toda la superestructura
    │           │
    │           └── modules/             # Módulos individuales (cada uno es un ModularSubsystem)
    │               ├── turretmodule/    # Torreta giratoria
    │               │   ├── Turret.java
    │               │   ├── TurretIO.java           # Interfaz de hardware (contrato)
    │               │   ├── TurretIOSparkMax.java   # Implementación real (SparkMax)
    │               │   ├── TurretIOSim.java        # Implementación de simulación
    │               │   └── TurretIOFallback.java   # Implementación vacía (hardware desactivado)
    │               │
    │               ├── armmodule/       # Brazo de ángulo de disparo (hood)
    │               │   ├── Arm.java
    │               │   ├── ArmIO.java
    │               │   ├── ArmIOKraken.java
    │               │   ├── ArmIOSim.java
    │               │   └── ArmIOFallback.java
    │               │
    │               ├── flywheelmodule/  # Ruedas de disparo (2 instancias: shooter + intake)
    │               │   ├── FlyWheel.java
    │               │   ├── FlyWheelIO.java
    │               │   ├── FlyWheelIOKrakenShooter.java
    │               │   ├── FlyWheelIOKrakenIntake.java
    │               │   ├── FlyWheelIOSim.java
    │               │   └── FlyWheelIOFallback.java
    │               │
    │               ├── intakemodule/    # Brazo de recolección de fuel
    │               ├── indexermodule/   # Transportador interno de fuel
    │               └── climbermodule/   # Mecanismo de trepa
    │
    ├── requests/                        # Objetos Request (comandos con parámetros)
    │   └── moduleRequests/
    │       ├── TurretRequest.java
    │       ├── ArmRequest.java
    │       ├── FlyWheelRequest.java
    │       ├── IntakeRequest.java
    │       ├── IndexerRequest.java
    │       └── ClimberRequest.java
    │
    ├── diagnostics/                     # Códigos de estado para LEDs
    │   ├── TurretCode.java
    │   ├── ArmCode.java
    │   ├── FlywheelsCode.java
    │   ├── IntakeCode.java
    │   ├── IndexerCode.java
    │   └── ClimberCode.java
    │
    └── helpers/                         # Utilidades generales
        ├── Utils.java
        ├── AllianceUtil.java            # Voltea coordenadas para alianza Roja vs Azul
        ├── LimelightHelpers.java        # Parseo de JSON de cámara Limelight
        ├── SysIdRoutineManager.java     # Rutinas de caracterización de motores (SysId)
        ├── AutoSelector.java
        └── KeyManager.java
```

### Por qué esta separación importa

La separación entre `configuration/` y `core/` es fundamental en el MARS Framework:

- **`configuration/`** — Es donde decides **qué** tiene tu robot y **cómo** están conectados. No hay lógica de hardware aquí. Si cambias de un SparkMax a un Kraken, solo tocas el Manifest.
- **`core/`** — Es donde está el **hardware real**. Cada módulo tiene su propia implementación de IO que puede ser Real, Sim o Fallback.
- **`requests/`** — Es como se le dan órdenes a los subsistemas, sin depender de botones o comandos específicos.

---

## 4. Flujo de Inicio: Main → Robot → RobotContainer

Cuando el robot enciende, la JVM ejecuta `Main.java`, que simplemente lanza el robot:

```java
// Main.java — solo una línea importante
RobotBase.startRobot(Robot::new);
```

Esto llama al constructor de `Robot`, que es donde empieza todo lo interesante.

### Robot.java — el director de orquesta

`Robot` extiende `TimedRobot` de WPILib. WPILib llama automáticamente a sus métodos cada 20ms según el modo del robot (disabled, autonomous, teleop, test). Lo más importante ocurre en el constructor:

```java
public Robot() {
    // 1. Le dice al framework en qué modo operar (REAL, SIM, REPLAY)
    Environment.setMode(Manifest.CURRENT_MODE);

    // 2. Opcionalmente inicia el terminal del Ground Control Station
    if (Manifest.HAS_MARS_GCS) {
        TerminalGCS.initNetworkStream();
        TerminalGCS.bootSequence();
    }

    // 3. Crea TODOS los subsistemas y bindings
    m_robotContainer = new RobotContainer();

    // 4. En hardware real, inicia la cámara USB
    if (Environment.getMode() == RunMode.REAL) {
        CameraServer.startAutomaticCapture();
    }

    // 5. Publica el modo actual al dashboard
    NetworkIO.set("System", "IO", Environment.getMode().name());
}
```

Después, en cada ciclo de 20ms:

```java
@Override
public void robotPeriodic() {
    CommandScheduler.getInstance().run();   // Ejecuta todos los comandos activos
    m_robotContainer.updateNodes();         // Actualiza los nodos de visualización
}
```

El `CommandScheduler` es el corazón del sistema — revisa qué botones están presionados, qué comandos deben correr, y llama a `execute()` en cada uno de ellos.

### Ciclo de vida por modo

| Modo | Qué hace |
|---|---|
| `disabledInit()` | Configura la Limelight en modo IMU para no resetear la orientación |
| `autonomousInit()` | Obtiene el comando autónomo de `RobotContainer` y lo schedula |
| `teleopInit()` | Cancela el comando autónomo si seguía corriendo |
| `testInit()` | Cancela todos los comandos y corre la rutina de test seleccionada |

### RobotContainer.java — la fábrica del robot

`RobotContainer` construye y conecta todo. Implementa la interfaz `IRobotContainer` del MARS Framework. Su constructor sigue este orden fijo:

```java
public RobotContainer() {

    // Paso 1: Controladores
    this.driver   = ControlsBuilder.buildDriver();    // Puerto 0
    this.operator = ControlsBuilder.buildOperator();  // Puerto 1

    // Paso 2: Drivetrain (independiente de todo)
    this.drivetrain = DrivetrainBuilder.buildModule();

    // Paso 3: Módulos individuales
    this.turret         = TurretBuilder.create().withDrivetrain(drivetrain).buildModule();
    this.arm            = ArmBuilder.create().buildModule();
    this.intake         = IntakeBuilder.create().buildModule();
    this.index          = IndexerBuilder.create().buildModule();
    this.flywheelShooter = FlywheelShooterBuilder.create().buildModule();
    this.flywheelIntake  = FlywheelIntakeBuilder.create().buildModule();

    // Paso 4: Superstructure (necesita los módulos del paso 3)
    this.superstructure = Manifest.SuperstructureBuilder.superBuild(
        this.turret, this.arm, this.intake, this.index,
        this.flywheelShooter, this.flywheelIntake);

    // Paso 5: Nodos de visualización
    this.virtualRobot = VisualizerBuilder.buildNode(...);
    this.trajetorySim = TrajectoryBuilder.buildNode(...);
    this.gamePieceViz = GamePieceBuilder.buildNode(...);

    // Paso 6: Bindings (último, necesita todo lo anterior)
    DriverBindings.create(drivetrain, driver).bind();
    OperatorBindings.create(operator, superstructure).withNodes(...).bind();
    this.test = TestBindings.create(superstructure);
    this.autoCommand = AutoBindings.create(superstructure);
    test.bind();
    autoCommand.bind();
}
```

El orden importa: los módulos deben existir antes que la Superstructure, y los bindings deben ir al final porque necesitan los comandos de los subsistemas.

---

## 5. El Manifest y los Feature Flags

`Manifest.java` es el archivo de configuración central del robot. Tiene dos responsabilidades:

1. **Feature Flags** — booleanos que dicen qué hardware existe
2. **Builders** — clases estáticas anidadas que construyen cada subsistema

### Feature Flags

```java
public static final RunMode CURRENT_MODE = RunMode.REAL; // REAL | SIM | REPLAY

public static final boolean HAS_DRIVETRAIN     = true;
public static final boolean HAS_TURRET         = true;
public static final boolean HAS_ARM            = true;
public static final boolean HAS_INTAKE         = true;
public static final boolean HAS_INTAKE_WHEELS  = true;
public static final boolean HAS_SHOOTER_WHEELS = true;
public static final boolean HAS_INDEXER        = true;
public static final boolean HAS_LIMELIGHT      = false;  // NO CAMBIAR
public static final boolean HAS_QUESTNAV       = false;
public static final boolean HAS_MARS_GCS       = false;
public static final boolean HAS_VISUALS        = true;
```

Cuando un flag está en `false`, el Manifest construye un `Fallback` — una implementación vacía que no hace nada — en lugar del hardware real. Así el robot puede correr en simulación o con hardware parcial sin cambiar el código de lógica.

### El patrón Injector

La magia está en `Injector.createIO()`. Este método del MARS Framework decide automáticamente qué implementación de IO crear según el flag y el modo:

```java
// Ejemplo del ArmBuilder
@Override
public Arm buildModule() {
    ArmIO io = Injector.createIO(
        HAS_ARM,           // ¿el hardware existe?
        ArmIOFallback::new, // si false → usar Fallback
        ArmIOKraken::new,   // si true + REAL → usar hardware real
        ArmIOSim::new       // si true + SIM → usar simulación
    );
    return new Arm(io);
}
```

La tabla de decisión del `Injector`:

| `HAS_ARM` | `CURRENT_MODE` | IO creado |
|---|---|---|
| `false` | cualquiera | `ArmIOFallback` |
| `true` | `REAL` | `ArmIOKraken` |
| `true` | `SIM` | `ArmIOSim` |

### Builders con dependencias

Algunos módulos necesitan dependencias extra. El `TurretBuilder` necesita el drivetrain para calcular la pose del robot:

```java
public static class TurretBuilder implements Builder<Turret> {
    private CommandSwerveDrivetrain drivetrain;

    public TurretBuilder withDrivetrain(CommandSwerveDrivetrain dt) {
        this.drivetrain = dt;
        return this;
    }

    @Override
    public Turret buildModule() {
        if (HAS_TURRET && this.drivetrain == null) {
            throw new IllegalStateException(
                "Falta el Drivetrain en la Torreta. Usa .withDrivetrain()");
        }

        TurretIO io = Injector.createIO(
            HAS_TURRET, TurretIOFallback::new, TurretIOSparkMax::new, TurretIOSim::new);

        // Si no hay drivetrain, usa pose y speeds en cero
        Supplier<Pose2d> pose = (drivetrain != null)
            ? () -> drivetrain.getState().Pose
            : () -> Pose2d.kZero;

        return new Turret(io, pose, speeds);
    }
}
```

Este patrón de builder fluido (`TurretBuilder.create().withDrivetrain(...).buildModule()`) es consistente en todo el Manifest y hace que el `RobotContainer` sea fácil de leer de un vistazo.

---

## 6. El Patrón IO

El patrón IO es la piedra angular del MARS Framework. Su objetivo es **separar la lógica de control del hardware físico**, de forma que el mismo código de subsistema funcione en el robot real, en simulación, o sin hardware.

Cada módulo tiene tres capas:

```
TurretIO          ← interfaz (el contrato)
  ├── TurretIOSparkMax   ← implementación real (SparkMax)
  ├── TurretIOSim        ← implementación de simulación
  └── TurretIOFallback   ← implementación vacía (no hace nada)
```

### La interfaz IO

Define qué puede hacer el hardware y qué datos produce. Usando la torreta como ejemplo:

```java
// TurretIO.java
@Fallback  // el MarsProcessor genera TurretIOFallback automáticamente
public interface TurretIO extends IO<TurretInputs> {

    // Clase interna: snapshot del estado del hardware
    public static class TurretInputs extends Data<TurretInputs> {

        @Unit(value = "Rotations", group = "Turret")
        public Rotation2d angle = new Rotation2d();       // ángulo actual

        @Unit(value = "Rotations", group = "Turret")
        public Rotation2d targetAngle = new Rotation2d(); // ángulo objetivo

        @Unit(value = "RPS", group = "Turret")
        public double velocityRPS = 0;                    // velocidad en RPS

        @Unit(value = "Volts", group = "Turret")
        public double appliedVolts = 0.0;                 // voltaje aplicado

        public Pose2d robotPose = new Pose2d();
        public ChassisSpeeds robotSpeed = new ChassisSpeeds();

        @Override
        public TurretInputs snapshot() { /* crea una copia del estado */ }
    }

    // Métodos que el hardware debe implementar
    public void setVoltage(double volts);
    public void setPosition(Rotation2d angle);
    public void setSpeed(double speed);
    public void setPositionWithFF(Rotation2d angle, double arbFFVolts);
    public void stop();
    public void resetEnc();
}
```

La anotación `@Fallback` le indica al `MarsProcessor` que genere automáticamente un `TurretIOFallback` con todos los métodos vacíos. No hay que escribirlo a mano.

La anotación `@Unit` es del `UnitProcessor` — documenta las unidades de cada campo para telemetría tipada.

### La implementación real (SparkMax)

```java
// TurretIOSparkMax.java
public class TurretIOSparkMax implements TurretIO {
    private final SparkMax m_motor;
    private final RelativeEncoder m_encoder;

    public TurretIOSparkMax() {
        m_motor = new SparkMax(TurretConstants.kMotorId, MotorType.kBrushless);
        m_encoder = m_motor.getEncoder();
        motorConfig();
    }

    private void motorConfig() {
        var config = new SparkMaxConfig();

        // PID cerrado
        config.closedLoop
            .pid(TurretConstants.kP, TurretConstants.kI, TurretConstants.kD)
            .outputRange(TurretConstants.kMinOutput, TurretConstants.kMaxOutput);

        // Feed Forward (estático, de velocidad, de aceleración)
        config.closedLoop.feedForward
            .kS(TurretConstants.kS)
            .kV(TurretConstants.kV)
            .kA(TurretConstants.kA);

        // Límites de seguridad y corriente
        config
            .idleMode(IdleMode.kBrake)
            .inverted(TurretConstants.kMotorInverted)
            .smartCurrentLimit(TurretConstants.kCurrentLimit);

        // Soft limits (evita que la torreta gire más de lo permitido)
        config.softLimit
            .forwardSoftLimit(TurretConstants.kUpperLimit)
            .forwardSoftLimitEnabled(true)
            .reverseSoftLimit(TurretConstants.kLowerLimit)
            .reverseSoftLimitEnabled(true);

        m_motor.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    // Implementación de los métodos de la interfaz
    @Override
    public void updateInputs(TurretInputs inputs) {
        inputs.angle = Rotation2d.fromRotations(-m_encoder.getPosition());
        inputs.velocityRPS = m_encoder.getVelocity();
        inputs.appliedVolts = m_motor.getAppliedOutput() * m_motor.getBusVoltage();
    }

    @Override
    public void setPosition(Rotation2d angle) {
        m_motor.getClosedLoopController()
            .setSetpoint(angle.getRotations(), ControlType.kPosition);
    }

    @Override
    public void setPositionWithFF(Rotation2d angle, double arbFFVolts) {
        // Control de posición con feedforward arbitrario (para compensar velocidad del chasis)
        m_motor.getClosedLoopController().setSetpoint(
            angle.getRotations(), ControlType.kPosition,
            ClosedLoopSlot.kSlot0, arbFFVolts, ArbFFUnits.kVoltage);
    }

    @Override
    public void stop() { m_motor.stopMotor(); }
}
```

### La clase del subsistema (Turret)

El subsistema extiende `ModularSubsystem` del MARS Framework, que se encarga del ciclo de actualización IO y de correr los Requests. El código del subsistema no sabe si tiene un SparkMax o una simulación:

```java
public class Turret extends ModularSubsystem<TurretInputs, TurretIO> implements TurretCommands {

    public Turret(TurretIO io, Supplier<Pose2d> pose, Supplier<ChassisSpeeds> speeds) {
        super(
            SubsystemBuilder.<TurretInputs, TurretIO>setup()
                .key(KeyManager.TURRET_KEY)       // clave de NetworkTables
                .hardware(io, new TurretInputs()) // la IO y el snapshot inicial
                .request(new TurretRequest.Idle()) // request por defecto
                .telemetry(new TurretTelemetry())); // cómo publicar datos

        setDefaultCommand(runRequest(() -> new TurretRequest.Idle()));
    }

    // Verifica si la torreta llegó al ángulo objetivo
    public boolean isAtTarget(double toleranceDegrees) {
        return MathUtil.isNear(
            inputs.targetAngle.getDegrees(),
            inputs.angle.getDegrees(),
            toleranceDegrees);
    }

    // Ejecuta un request dado
    @Override
    public Command setControl(Supplier<TurretRequest> request) {
        return runRequest(request);
    }

    // Llamado cada ciclo — inyecta pose y speeds en el snapshot
    @Override
    public void absolutePeriodic(TurretInputs data) {
        data.robotPose = poseSupplier.get();
        data.robotSpeed = speedSupplier.get();
    }
}
```

`absolutePeriodic()` es el método del MARS Framework que se llama cada ciclo antes de ejecutar el Request activo. Es el lugar para inyectar datos externos (como la pose del drivetrain) en el snapshot de IO.

---

## 7. El Patrón Request

Los `Request` son la forma en que se le dan órdenes a un subsistema. Son objetos que implementan `apply(data, actor)` — donde `data` es el snapshot de IO y `actor` es la implementación de IO — y retornan un `ActionStatus` con el estado actual.

El beneficio de los Requests frente a comandos WPILib directos es que son **composables, configurables y no dependen de botones**. Un Request es simplemente datos + lógica.

### Estructura de TurretRequest

```java
@RequestFactory  // el MarsProcessor genera TurretRequestFactory automáticamente
public interface TurretRequest extends Request<TurretInputs, TurretIO> {

    // --- Request 1: Idle (no hace nada) ---
    @CreateCommand(name = "stop")
    public static class Idle implements TurretRequest {
        @Override
        public ActionStatus apply(TurretInputs data, TurretIO actor) {
            actor.stop();
            data.targetAngle = data.angle; // el target sigue siendo el ángulo actual
            return ActionStatus.of(TurretCode.IDLE, "Idle");
        }
    }

    // --- Request 2: Control de posición (builder fluido) ---
    @CreateCommand(name = "toPosition")
    public static class Position implements TurretRequest {
        private Rotation2d m_targetAngle = new Rotation2d();
        private double toleranceDegrees = 1.0;

        // Métodos fluidos para configurar el request
        public Position withTargetAngle(Rotation2d angle) {
            this.m_targetAngle = angle;
            return this;
        }

        public Position withTolerance(double tolerance) {
            this.toleranceDegrees = tolerance;
            return this;
        }

        @Override
        public ActionStatus apply(TurretInputs data, TurretIO actor) {
            data.targetAngle = m_targetAngle;
            actor.setPosition(m_targetAngle);

            boolean isLocked = MathUtil.isNear(
                m_targetAngle.getDegrees(), data.angle.getDegrees(), toleranceDegrees);

            // El ActionStatus comunica el estado al LED y al dashboard
            return isLocked
                ? ActionStatus.of(TurretCode.LOCKED, "Target reached")
                : ActionStatus.of(TurretCode.TRACKING, "Tracking " + m_targetAngle.getDegrees() + "°");
        }
    }

    // --- Request 3: Apuntar a un target en el campo (tracking dinámico) ---
    @CreateCommand(name = "lockToTarget")
    public static class LockOnTarget implements TurretRequest {
        private Supplier<Translation2d> targetSupplier;
        private DoubleSupplier rotationSupplier;

        public LockOnTarget withTarget(Supplier<Translation2d> target) { ... }
        public LockOnTarget withChassisOmega(DoubleSupplier omega) { ... }

        @Override
        public ActionStatus apply(TurretInputs data, TurretIO actor) {
            // Calcula el ángulo necesario para apuntar al target desde la pose actual
            Translation2d turretPose = data.robotPose
                .transformBy(TurretConstants.ROBOT_TO_TURRET_TRANSFORM)
                .getTranslation();

            Rotation2d fieldAngle = targetSupplier.get().minus(turretPose).getAngle();
            Rotation2d turretSetpoint = fieldAngle.minus(data.robotPose.getRotation());

            // Feed forward para compensar la rotación del chasis
            double ffVolts = rotationSupplier.getAsDouble() * TurretConstants.kChassisAngularCompensator;
            actor.setPositionWithFF(turretSetpoint, ffVolts);

            return isLocked
                ? ActionStatus.of(TurretCode.LOCKED, "Locked on target")
                : ActionStatus.of(TurretCode.TRACKING, "Moving...");
        }
    }
}
```

### Cómo se usa un Request

Los Requests se usan desde los Bindings a través del método `setControl()`:

```java
// Desde OperatorBindings — ir a posición fija
turret.setControl(() -> new TurretRequest.Position()
    .withTargetAngle(Rotation2d.fromDegrees(-30))
    .withTolerance(4.0));

// Desde la Superstructure — apuntar al hub dinámicamente
turret.setControl(() -> new TurretRequest.LockOnTarget()
    .withTarget(hubSupplier)
    .withChassisOmega(() -> speeds.omegaRadiansPerSecond));
```

La anotación `@RequestFactory` hace que el `MarsProcessor` genere automáticamente una clase `TurretRequestFactory` con métodos estáticos para crear cada Request sin hacer `new` directamente. Por eso en el código de Bindings se ve `TurretRequestFactory.manualControl()` en vez de `new TurretRequest.manualControl()`.

---

## 8. Bindings de Controladores

Los bindings conectan los inputs del controlador con los comandos de los subsistemas. MARS Framework provee la interfaz `Binding` con un método `bind()` donde se registran todas las asociaciones.

El patrón es siempre el mismo: `trigger.whileTrue(comando)` o `trigger.onTrue(comando)`.

### DriverBindings — conducir el robot

```java
public class DriverBindings implements Binding {

    @Override
    public void bind() {
        // --- Comando por defecto: conducir con joystick (field-centric) ---
        drivetrain.setDefaultCommand(
            drivetrain.applyRequest(() ->
                SwerveRequestFactory.driveFieldCentric()
                    .withVelocityX(
                        -driverLeftStick.y().getAsDouble()     // adelante/atrás
                        * SwerveConstants.MaxSpeed
                        * (driverBumpers.right().getAsBoolean() ? 0.5 : 1.0)  // RB = mitad velocidad
                        * (driverBumpers.left().getAsBoolean()  ? 0.2 : 1.0)) // LB = precisión
                    .withVelocityY(
                        -driverLeftStick.x().getAsDouble()     // izquierda/derecha
                        * SwerveConstants.MaxSpeed
                        * ...)
                    .withRotationalRate(
                        -driverRightStick.x().getAsDouble()    // rotación
                        * SwerveConstants.MaxAngularRate)));

        // Y = reset orientación campo (útil si el giroscopio se pierde)
        driverButtons.top().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));

        // A = frenos (todas las ruedas en X, el robot no se mueve)
        driverButtons.bottom().whileTrue(
            drivetrain.applyRequest(() -> SwerveRequestFactory.brake()));

        // X = navegar automáticamente a coordenada (0.579, 0.579)
        driverButtons.left().whileTrue(
            drivetrain.getPoseFinder().toPose(new Pose2d(0.579, 0.579, Rotation2d.kZero)));

        // B = navegar a (3.559, 2.766) mirando 90° CCW
        driverButtons.right().whileTrue(
            drivetrain.getPoseFinder().toPose(new Pose2d(3.559, 2.766, Rotation2d.kCCW_90deg)));
    }
}
```

### OperatorBindings — operar los mecanismos

```java
@Override
public void bind() {
    // A = bajar intake a -138° (posición de recolección)
    buttons.bottom().whileTrue(
        superstructure.getIntake().setControl(() ->
            IntakeRequestFactory.setAngle()
                .withAngle(-138)
                .Tolerance(Constants.INTAKE_TOLERANCE)
                .withMode(intakeMODE.kDOWN)));

    // Y = subir intake a -10° (posición de transporte)
    buttons.top().whileTrue(
        superstructure.getIntake().setControl(() ->
            IntakeRequestFactory.setAngle()
                .withAngle(-10)
                .Tolerance(Constants.INTAKE_TOLERANCE)
                .withMode(intakeMODE.kUP)));

    // B = comer fuel (intake + indexer juntos)
    buttons.right().whileTrue(superstructure.eatCommand());

    // LB = expulsar fuel
    bumpers.left().whileTrue(superstructure.clearFuel());

    // RT (sin RB) = disparar al hub dinámicamente con interpolación de ángulo y RPM
    triggers.right().and(bumpers.right().negate()).whileTrue(
        superstructure.shootOnTheMove(
            superstructure.getVirtualTarget(),
            ArmRequestFactory.interpolateTarget()
                .withDistance(() -> superstructure.getVirtualDistance())
                .withTolerance(Constants.ARM_TOLERANCE),
            FlyWheelRequestFactory.interpolateRPM()
                .withDistance(() -> superstructure.getVirtualDistance())
                .withTolerance(Constants.FLYWHEEL_TOLERANCE),
            12));  // torreta hacia la derecha

    // RT + RB = mismo disparo pero torreta hacia la izquierda (-12V)
    triggers.right().and(bumpers.right()).whileTrue(
        superstructure.shootOnTheMove(..., -12));

    // LT (sin RB) = disparar al speaker izquierdo (-30° arm, -3500 RPM, 12V torreta)
    triggers.left().and(bumpers.right().negate()).whileTrue(
        superstructure.shootOnTheMove(
            new Translation2d(0.863, 4.003),
            ArmRequestFactory.setAngle().withAngle(-30).withMode(ArmMODE.kUP),
            FlyWheelRequestFactory.setRPM().toRPM(-3500).withTolerance(50),
            12));

    // LT + X = disparar al amp (-45° arm, -25° torreta, -2500 RPM)
    triggers.left().and(buttons.left()).whileTrue(
        superstructure.shoot(-45, -25, -2500));
}
```

### Resumen del mapa de controles

**Driver (Puerto 0 — Xbox):**

| Botón | Acción |
|---|---|
| Left Stick | Desplazamiento (field-centric) |
| Right Stick X | Rotación |
| Right Bumper | Mitad de velocidad (0.5x) |
| Left Bumper | Modo precisión (0.2x) |
| Y | Reset orientación de campo |
| A | Frenos (X-brake) |
| X | Navegar a punto A del campo |
| B | Navegar a punto B del campo |

**Operator (Puerto 1 — Xbox):**

| Botón | Acción |
|---|---|
| A | Bajar intake (-138°) |
| Y | Subir intake (-10°) |
| B | Comer fuel (intake + indexer) |
| X | Girar flywheels shooter a -12V |
| Left Bumper | Expulsar fuel |
| Right Trigger (sin RB) | Disparar al hub con interpolación dinámica |
| Right Trigger + RB | Mismo disparo, torreta invertida |
| Left Trigger (sin RB) | Disparar al speaker izquierdo |
| Left Trigger + RB | Disparar al speaker, torreta invertida |
| Left Trigger + X | Disparar al amp |
| Left Trigger + B | Disparar al amp (variante) |
| Right Stick + D-Pad derecho | Control manual de torreta |
| Left Stick Y + D-Pad izquierdo | Control manual de disparo |

---

## 9. Módulos Individuales

La `Superstructure` agrupa seis `ModularSubsystem`. Cada uno tiene su propia interfaz IO, su implementación real y su simulación. A continuación se detalla la configuracion de hardware de cada uno.

### Turret (Torreta)

| Parametro | Valor |
|---|---|
| Motor | SparkMax (CAN ID 19) |
| Encoder | Relativo integrado del SparkMax |
| Rango de movimiento | -85° a +85° (soft limits) |
| Relacion de transmision | 20:1 |
| Control | PID cerrado (P=4, I=0, D=2.4) + Feed Forward (kS=0.45, kV=2) |
| Limite de corriente | 40 A |
| Modo idle | Brake |
| Posicion fisica | 15 cm adelante del centro del robot, 45 cm de altura |

La torreta rota sobre su eje vertical para apuntar a diferentes puntos del campo. El `kChassisAngularCompensator = 0.5` compensa la inercia cuando el chasis gira, aplicando feed forward adicional en proporcion a la velocidad angular del chasis (V/rad/s).

### Arm (Brazo / Hood)

| Parametro | Valor |
|---|---|
| Motor | TalonFX (Kraken X60) |
| Encoder | CANcoder externo (CANivore) |
| Rango de movimiento | -40° a 0° |
| Control | PID de posicion de Phoenix 6 (MotionMagic o PID) |
| Modos de operacion | `ArmMODE.kUP` / `ArmMODE.kDOWN` |

El brazo ajusta el angulo de lanzamiento. Tiene dos modos (`kUP` y `kDOWN`) que permiten usar ganancias PID diferentes segun si sube o baja, compensando la gravedad.

Los mapas de interpolacion en `Constants.java` relacionan distancia → angulo para el disparo dinamico:

```java
// Distancia en metros → angulo del brazo en grados
INTERPOLATION_MAP.put(2.03, -8.0);
INTERPOLATION_MAP.put(2.80, -8.0);
INTERPOLATION_MAP.put(3.80, -18.45);
INTERPOLATION_MAP.put(4.39, -15.0);
INTERPOLATION_MAP.put(4.93, -16.0);
```

### FlyWheels (Ruedas voladoras)

Existen dos instancias de `FlyWheel`, compartiendo la misma interfaz `FlyWheelIO`:

| Instancia | Clave | Motor | Funcion |
|---|---|---|---|
| `flywheelShooter` | `FLYWHEEL_OUTAKE_KEY` | Kraken X60 | Lanzar fuel hacia el hub |
| `flywheelIntake` | `FLYWHEEL_INTAKE_KEY` | Kraken X60 | Jalar fuel desde el indexer durante recoleccion |

Ambas usan control de velocidad en RPM con tolerancia de 50 RPM. El mapa de RPM interpola distancia → velocidad:

```java
// Distancia en metros → RPM (negativo = sentido de disparo)
RPM_MAP.put(2.03, -2650.0);
RPM_MAP.put(2.59, -2700.0);
RPM_MAP.put(3.80, -2900.0);
RPM_MAP.put(4.93, -3150.0);
```

### Intake

| Parametro | Valor |
|---|---|
| Motor | TalonFX (Kraken X60) |
| Rango de movimiento | -138° (abajo, recoleccion) a -10° (arriba, transporte) |
| Modos | `intakeMODE.kDOWN` / `intakeMODE.kUP` |
| Tolerancia | 2° |

El intake se baja para recoger fuel del suelo y sube para transportarlo. Igual que el brazo, tiene dos modos con ganancias distintas segun la direccion.

### Indexer

| Parametro | Valor |
|---|---|
| Motor principal | SparkMax — rollers (jala fuel hacia adentro) |
| Motor secundario | SparkMax — index (empuja fuel hacia los flywheels) |
| Control | Solo voltaje (sin PID, sin posicion) |

El indexer tiene dos motores: `rollers` que mueve el fuel horizontalmente y `index` que lo empuja hacia arriba al shooter. Los comandos le pasan voltaje directo:

```java
// Comer fuel: rollers a 8V, index en 0
IndexerRequestFactory.moveVoltage().withRollers(8).withIndex(0)

// Disparar: ambos a 12V
IndexerRequestFactory.moveVoltage().withRollers(12).withIndex(12)

// Expulsar: ambos a -12V, luego +12V, repetido
IndexerRequestFactory.moveVoltage().withRollers(-12).withIndex(-12)
```

### Climber

| Parametro | Valor |
|---|---|
| Motor | TalonFX (Kraken X60) |
| Tipo | Actuador lineal / winch |
| Control | Posicion o voltaje |
| Sim | `ClimberIOSim` disponible |

El climber no tiene bindings activos en competencia (no hay `ClimberBindings`). Su logica esta disponible pero inactiva hasta que sea necesario. Se puede activar desde `TestBindings`.

---

## 10. La Superstructure — CompositeSubsystem

La `Superstructure` es el unico punto de entrada para comandos que involucran multiples modulos. Ningun binding llama directamente a `turret.setControl()` para un disparo — todo pasa por la Superstructure, que coordina los movimientos paralelos y secuenciales.

### Comandos compuestos principales

**`shoot(turretAngle, armAngle, shooterRPM)`** — Disparo a posicion fija en 2 fases:

```java
public Command shoot(double turretAngle, double armAngle, double shooterRPM) {
    return Commands.sequence(
        // Fase 1: Posicionar todo en paralelo, esperar a que los flywheels lleguen a RPM
        Commands.parallel(
            flywheelShooter.runRequest(() -> FlyWheelRequestFactory.setRPM().toRPM(shooterRPM)...),
            turret.setControl(() -> TurretRequestFactory.position().withTargetAngle(turretAngle)),
            arm.setControl(() -> ArmRequestFactory.setAngle().withAngle(armAngle)...))
        .until(() -> flywheelShooter.isAtTarget(Constants.FLYWHEEL_TOLERANCE)),

        // Fase 2: Cuando los flywheels estan listos, activar indexer e intake
        Commands.parallel(
            index.setControl(() -> IndexerRequestFactory.moveVoltage().withRollers(12).withIndex(12)),
            intakeWheels.setControl(() -> FlyWheelRequestFactory.moveVoltage().withVolts(-5))));
}
```

**`shootOnTheMove(target, armRequest, shooterRequest, voltIndex)`** — Disparo dinamico mientras el robot se mueve, usando `LockOnTarget` con feed forward de velocidad angular.

**`eatCommand()`** — Activa los intake wheels a -5V y el indexer a 8V de rollers, en paralelo.

**`clearFuel()`** — Expulsa el fuel con secuencias inversas (indexer -12V por 1.5s, luego +12V por 1.5s, repetido).

**`getVirtualTarget()`** — Calcula donde apuntar compensando el movimiento del robot:

```java
// Proyecta la pose del robot 30ms en el futuro
Pose2d futureRobotPose = robotPose.exp(new Twist2d(vx*0.03, vy*0.03, omega*0.03));

// Itera 20 veces para refinar el tiempo de vuelo del fuel
for (int i = 0; i < 20; i++) {
    virtualTarget = realHubLocation.minus(robotVelVector.times(timeOfFlight));
    timeOfFlight = distanceToVirtualTarget / NOMINAL_FUEL_VELOCITY_MPS; // 18 m/s
}
```

El `@Tunable` en `RPMTest` y `AngleTest` permite ajustar estos valores en tiempo real desde ForgeMini sin recompilar.

---

## 11. Sistema de Autonomia (PathPlanner)

La autonomia usa PathPlanner para rutas precalculadas y `NamedCommands` para conectar eventos de la ruta con comandos de la Superstructure.

### Rutas disponibles

| Nombre en PathPlanner | Descripcion |
|---|---|
| `EatPost-auto` | Comer y disparar desde el post (opcion por defecto) |
| `DepotAuto` | Ruta desde el deposito |
| `BotBusters` | Ruta de busters por abajo |
| `TestRed` | Ruta de prueba lado rojo |
| `TestBlue` | Ruta de prueba lado azul |
| `FlipBusters` | Busters espejados |
| `FlipEatPostAuto` | Post espejado |

La seleccion se hace desde el SmartDashboard antes del partido con un `SendableChooser`:

```java
chooser.setDefaultOption("Post", bumpPost);
chooser.addOption("DepotAuto", depotBusters);
// ...
SmartDashboard.putData("AutoSelector", chooser);
```

### Comandos nombrados registrados

PathPlanner dispara estos comandos durante la ejecucion de la ruta cuando encuentra el marcador con ese nombre:

| Nombre | Lo que hace | Timeout |
|---|---|---|
| `"Angle->Eat_4.5"` | Baja intake a -140° y activa wheels de intake | 4.5 s |
| `"JustAngle"` | Solo baja el intake a -140° | sin timeout |
| `"Eat_5"` | Activa wheels de intake a -10V | 5 s |
| `"Eat_3.5"` | Activa wheels de intake a -10V | 3.5 s |
| `"Shoot_6"` | `shootAuto()` — disparo con interpolacion dinamica | 6 s |
| `"Shoot_10"` | Mismo disparo | 10 s |
| `"Shoot_13"` | Mismo disparo | 12 s |
| `"IntakeUp"` | Sube el intake a -10° | sin timeout |

El `shootAuto()` dispara con el turret bloqueado al hub virtual (compensacion de movimiento) usando los mapas de interpolacion para el angulo del brazo y las RPM, luego dispara en sentido opuesto 0.8 s, y repite.

---

## 12. Telemetria y Visualizacion

### Sistema de Telemetria por modulo (TurretTelemetry)

Cada `ModularSubsystem` tiene una clase `Telemetry` interna que define exactamente que se publica y como. El framework llama a `telemeterize()` cada ciclo:

```java
public static class TurretTelemetry extends Telemetry<TurretInputs> {

    @Override
    public void telemeterize(TurretInputs data, ActionStatus lastStatus) {
        String table = KeyManager.TURRET_KEY;  // "Turret"

        // Publica datos de hardware al NetworkTable "Turret"
        NetworkIO.set(table, "AppliedOutput",   data.appliedVolts);
        NetworkIO.set(table, "AngleDegrees",    data.angle.getDegrees());
        NetworkIO.set(table, "TargetAngleDeg",  data.targetAngle.getDegrees());
        NetworkIO.set(table, "VelocityRPS",     data.velocityRPS);
        NetworkIO.set(table, "LatencyMS",       (Timer.getFPGATimestamp() - data.timestamp) * 1000);

        // Publica el status del diagnostico (solo cuando cambia, para no saturar el bus)
        if (lastStatus != null && lastStatus.getPayload() != null) {
            String currentHex = lastStatus.getPayload().colorHex();
            if (!currentHex.equals(lastSentHex)) {
                NetworkIO.set(table, "PayloadName",    table);
                NetworkIO.set(table, "PayloadMessage", lastStatus.getPayload().message());
                NetworkIO.set(table, "PayloadHex",     currentHex);
                lastSentHex = currentHex;
            }
        }
    }
}
```

`NetworkIO` es de ForgeMini — es un wrapper sobre NetworkTables que gestiona tipos automaticamente y puede configurar actualizacion solo cuando cambia el valor (`onChange`).

### Nodes de AdvantageScope

Los `Node` son publicadores periodicos para AdvantageScope. Cada uno combina multiples fuentes de datos en un mensaje estructurado y lo publica al NetworkTable correspondiente.

**`VisualizerNode`** — Modelo 3D del robot en AdvantageScope:

```java
// Publica la pose 3D de cada componente movil
NetworkIO.set(tableName, "TurretComponent", turretPose3d); // rotacion de la torreta
NetworkIO.set(tableName, "HoodComponent",   hoodPose3d);   // angulo del brazo
NetworkIO.set(tableName, "IntakeComponent", intakePose3d); // posicion del intake
```

AdvantageScope interpreta estos `Pose3d` para animar el modelo CAD del robot en tiempo real, lo que permite ver visualmente si los angulos son correctos sin mirar el robot fisico.

**`TrajectoryNode`** — Muestra la trayectoria de PathPlanner siendo seguida en tiempo real.

**`GamePieceNode`** — Muestra la posicion de los game pieces (fuel) en el campo.

### Segnales Tunable y Signal

`@Tunable` permite modificar un campo desde el dashboard en tiempo real:

```java
@Tunable public double RPMTest = -3500;
@Tunable public double AngleTest = -20;
```

`@Signal` publica el valor de un metodo al NetworkTable, solo cuando cambia:

```java
@Signal(key = "RPM", onChange = true)
public double getRPM() { return RPMTest; }
```

### Claves de NetworkTables (KeyManager)

Todas las claves de NetworkTables estan centralizadas en `KeyManager.java`:

| Clave | Tabla |
|---|---|
| `TURRET_KEY` | `"Turret"` |
| `ARM_KEY` | `"Arm"` |
| `INTAKE_KEY` | `"Intake"` |
| `INDEX_KEY` | `"Indexer"` |
| `FLYWHEEL_OUTAKE_KEY` | `"FlywheelShooter"` |
| `FLYWHEEL_INTAKE_KEY` | `"FlywheelIntake"` |
| `CLIMBER_KEY` | `"Climber"` |
| `SUPERSTRUCTURE_KEY` | `"Superstructure"` |

---

## 13. Diagnosticos y Codigos de Estado LED

Cada Request retorna un `ActionStatus` con un `StatusCode`. El MARS Framework usa ese codigo para dos cosas: publicar el estado al dashboard (texto + color hex) y, si hay un LED conectado, controlar el patron de luz.

### Estructura de un StatusCode

```java
// TurretCode.java
public enum TurretCode implements StatusCode {
    IDLE    (Severity.OK,      DiagnosticPattern.solid(Color.kDarkGreen)),
    LOCKED  (Severity.OK,      DiagnosticPattern.solid(Color.kFirstBlue)),
    TRACKING(Severity.WARNING, DiagnosticPattern.solid(Color.kYellow)),
    MANUAL_CONTROL  (Severity.WARNING, DiagnosticPattern.solid(Color.kBrown)),
    MANUAL_OVERRIDE (Severity.WARNING, DiagnosticPattern.solid(Color.kPurple)),
    RESET   (Severity.OK,      DiagnosticPattern.solid(Color.kDarkSalmon));
    // ...
}
```

Cada codigo tiene:
- Una **severidad** (`OK`, `WARNING`, `ERROR`) para filtrar alertas criticas
- Un **patron visual** (`DiagnosticPattern.solid(Color)`) que define el color LED

### Tabla de codigos de todos los modulos

**Turret:**

| Codigo | Severidad | Color LED |
|---|---|---|
| `IDLE` | OK | Verde oscuro |
| `LOCKED` | OK | Azul FRC |
| `TRACKING` | WARNING | Amarillo |
| `MANUAL_CONTROL` | WARNING | Marron |
| `MANUAL_OVERRIDE` | WARNING | Morado |
| `RESET` | OK | Salmon |

**Arm:**

| Codigo | Severidad | Color LED |
|---|---|---|
| `IDLE` | OK | Verde oscuro |
| `ON_TARGET` | OK | Azul FRC |
| `MOVING_TO_ANGLE` | WARNING | Amarillo |
| `MANUAL_CONTROL` | WARNING | Marron |
| `MANUAL_OVERRIDE` | WARNING | Morado |
| `OUT_OF_RANGE` | ERROR | Naranja |

Los demas modulos (Intake, Indexer, FlyWheels, Climber) siguen el mismo patron — `IDLE`=verde, `ON_TARGET`/`LOCKED`=azul, en movimiento=amarillo, error=naranja/rojo.

La telemetria publica el color hex del status actual al NetworkTable del modulo (`PayloadHex`). Desde AdvantageScope se puede ver el estado de cada modulo con su color correspondiente.

---

## 14. Features Instaladas

Las features son paquetes de codigo reutilizables del ecosistema MARS. Se instalan via `build.gradle` y viven en `workspace/features/`.

### Features activas en MARSIANO

| Feature | Version | Funcion |
|---|---|---|
| `MarsProcessor` | 1.0.5 | Genera `@Fallback` (IO vacia) y `@RequestFactory` (factory estatica) en tiempo de compilacion con annotation processors de Java |
| `UnitProcessor` | 1.0.3 | Genera documentacion de unidades tipadas a partir de `@Unit` para logging y telemetria |
| `DictionaryFeature` | 1.0.0 | Centraliza los strings de logging (`CommonTables.ANGLE_KEY`, `Terminology.RPS`, etc.) para evitar typos en las claves de NetworkTables |
| `MARSPoseFinderCTRESwerve` | 1.0.0 | Agrega `getPoseFinder()` al `CommandSwerveDrivetrain` — permite navegar autonomamente a cualquier `Pose2d` desde un binding con una sola linea |

### Como funcionan los annotation processors

`MarsProcessor` y `UnitProcessor` son Java annotation processors que corren **en tiempo de compilacion** (no en el robot). Cuando ves `@Fallback` en una interfaz IO, al compilar el proyecto Gradle genera automaticamente el archivo `TurretIOFallback.java` en el directorio de build. No hay que escribir ni mantener esas clases manualmente.

```
Compilacion:
  TurretIO.java (@Fallback) ──► MarsProcessor ──► genera TurretIOFallback.java
  TurretRequest.java (@RequestFactory) ──► genera TurretRequestFactory.java
```

### Como instalar una nueva feature

Desde la extension `mars-framework` de VS Code, el comando `MARS: Install Feature` muestra el marketplace y agrega automaticamente la dependencia al `build.gradle`. Para instalacion manual, el `build.gradle` incluye la feature como dependencia de Maven:

```groovy
// build.gradle (fragmento)
dependencies {
    implementation "com.stzteam.features:MARSPoseFinderCTRESwerve:1.0.0"
    annotationProcessor "com.stzteam.features:MarsProcessor:1.0.5"
    annotationProcessor "com.stzteam.features:UnitProcessor:1.0.3"
}
```

---

## Resumen de Arquitectura

```
Main.java
  └─ Robot.java (TimedRobot — ciclo 20ms)
      └─ RobotContainer.java
          ├─ Manifest.java ──── Feature Flags + Injector (REAL/SIM/Fallback)
          ├─ Constants.java ─── Tolerancias, HUB location, mapas de interpolacion
          ├─ CommandSwerveDrivetrain ─── CTRE Phoenix 6, PoseFinder (PathPlanner)
          ├─ Superstructure (CompositeSubsystem)
          │   ├─ Turret ──── SparkMax  | IO → Request → ActionStatus → LED
          │   ├─ Arm ──────── Kraken   | IO → Request → ActionStatus → LED
          │   ├─ FlyWheel x2 ─ Kraken  | IO → Request → ActionStatus → LED
          │   ├─ Intake ───── Kraken   | IO → Request → ActionStatus → LED
          │   ├─ Indexer ──── SparkMax | IO → Request → ActionStatus → LED
          │   └─ Climber ──── Kraken   | IO → Request → ActionStatus → LED
          ├─ DriverBindings (Xbox Puerto 0)
          ├─ OperatorBindings (Xbox Puerto 1)
          ├─ AutoBindings (PathPlanner + NamedCommands)
          └─ AdvantageScope Nodes (Visualizer, Trajectory, GamePiece)
```

Cada modulo sigue el mismo flujo cada ciclo de 20ms:

```
IO.updateInputs(inputs)      ← leer sensores del hardware
absolutePeriodic(inputs)     ← inyectar datos externos (pose, speeds)
Request.apply(inputs, actor) ← ejecutar la orden activa
Telemetry.telemeterize(...)  ← publicar al NetworkTable
Node.processInformation()    ← publicar a AdvantageScope
```

---

*Documento completado. Ver tambien: [GUIA_INICIO_MARS.md](GUIA_INICIO_MARS.md)*
