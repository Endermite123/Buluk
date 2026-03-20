
# Test commands

## sequence()
The `sequence()` method is used to create a sequence of commands that will be executed in order. 

## run ()
The `run()` method is used to execute a command or a sequence of commands when the test case is selected in the driver station. It should return a command that will be executed.

## waitFor()
The `waitFor()` method is used to create a command that will wait for a certain condition to be met before proceeding to the next command in the sequence. It takes a lambda function as a parameter that defines the condition to be met. For example, `waitFor(() -> mechanism.isAtPosition())` will create a command that waits until the mechanism is at a certain position before proceeding to the next command in the sequence.

## delay()
The `delay()` method is used to create a command that will wait for a certain amount of time before proceeding to the next command in the sequence. It takes a parameter that specifies in seconds the amount of time to wait in seconds. For example, `delay(2)` will create a command that waits for 2 seconds before proceeding to the next command in the sequence.

