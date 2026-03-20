package frc.tests;

import com.stzteam.mars.test.MARSTest;
import com.stzteam.mars.test.TestRoutine;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;


/**
 * <b> @MARSTest </b>
 * Annotation used to register a {@link TestRoutine} in the MARS system.
 *
 * <p>Any class annotated with {@code @MARSTest} will be automatically
 * discovered and made available to run during test mode.
 *
 * <ul>
 *   <li>The class must extend {@link TestRoutine}</li>
 *   <li>The class must implement {@link TestRoutine#getRoutineCommand()}</li>
 * </ul>
 *
 * <p><b>Parameters:</b>
 * <ul>
 *   <li><b>name</b>: A unique name used to identify the test</li>
 * </ul>
 *
 * <p><b>How it works:</b>
 * <ol>
 *   <li>MARS scans the code for this annotation</li>
 *   <li>Registers all test routines</li>
 *   <li>Allows selecting and running them</li>
 * </ol>
 */


@MARSTest(name = "NONE")
public class EmptyTest extends TestRoutine{
    public EmptyTest() {}

    @Override
    public Command getRoutineCommand(){
        return Commands.none();
    }
}
