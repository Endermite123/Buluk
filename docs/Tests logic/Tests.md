

# Tests

## @MARSTEST
Parameters: name

The `@MARSTEST` annotation is used to define a test case for a MARS module. The `name` parameter specifies the name of the test case.

## Extends TestRoutine 
The `TestRoutine` class provides the necessary methods to include the test case in the MARS testing framework, which will also publish it in the Elasic chooser and activate it when selected in the driver station.

## EmptyTest class
The class should have the name of the mechanisms that will be tested, followed by "Test". An object of that mechanism (or any other that will be used in the test) should be created to use their commands.

### Test constructor 
The constructor should have as parameters the mechanisms that will be used in the tests.

### Test method
Inside of command method, a return statement should be used to return a command that will be executed when the test is selected in the driver station. This command can also be a sequence. See Test commands documentation for more information on how to create commands and sequences.