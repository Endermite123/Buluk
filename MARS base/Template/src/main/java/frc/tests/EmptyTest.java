package frc.tests;

import com.stzteam.mars.test.MARSTest;
import com.stzteam.mars.test.TestRoutine;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;


@MARSTest(name = "NONE")
public class EmptyTest extends TestRoutine{
    private Mechanism mechanism;
    public EmptyTest(Mechanism mechanism) {
        this.mechanism = mechanism;
    }

    @Override
    public Command getRoutineCommand(){
        return Commands.sequence(
            Commands.print("No test routine configured")
        );
    }
}

