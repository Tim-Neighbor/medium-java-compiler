Compiler for "Medium Java"
Version 1.0
Updated 4-25-21

The generated assembly code has been tested using version 4.5 of MARS. Support 
for other MIPS assemblers is not guaranteed.

The following is a list of assumptions we made about the behavior of our 
language. These behaviors were neither explicitly mandated nor prohibited, 
so we used our best judgment in reaching these assumptions.

Booleans
    - BOOLEAN variables are stored as integer values. If a BOOLEAN variable 
      contains a value other than 0, it is interpreted as TRUE. If the 
      variable is equal to 0, it is FALSE.
    - SEE Printing
    - SEE Reading Input

Strings
    - STRING variables may not exceed a length of 255.
    - STRING variables may only contain characters whose ASCII values fall 
      within the range 35 - 255 (inclusive).

Arrays
    - The only way to modify an array's value is to access an individual
      element of that array.
        The following is supported:
        Ex.    arr : INT[2];
               arr[0] := 6;
        The following is not supported:
        Ex.    arr : INT[2];
               arr[0] := 6;
               arr2 : INT[2];
               arr2 := arr;     <-- ERROR

For Loops
    - A variable declared in the assignment section of a FOR loop is only 
      accessible inside the FOR loop, including within the parentheses, but 
      nowhere else.

Switches
    - Each case of a switch statement is its own, closed scope.

Expression Evaluation
    - All subexpressions of an expression must share the same type. 
        The following is supported:
        Ex.     temperature : REAL;
                temperature := 1.0 + 0.5;
        The following is not supported:
                temperature : REAL;
                temperature := 1 + 0.5;     <- ERROR
    - Among BOOLEAN variables, the only operations with defined behavior are 
      "==" (equal to) and "!=" (not equal to); any other operations between 
      BOOLEAN variables will not cause a compiler error, but the behavior will 
      be undefined.

Printing
    - PUT() will print the value of the variable passed in, and will NOT 
      terminate the value with a new line character.
    - Printing any variable is supported, however, if the user tries to print 
      an array without first accessing an element, only the first element of 
      that array will be printed.
    - Printing a BOOLEAN variable will print its integer value.
      Therefore, it may be printed as a number other
      than 0 or 1 (SEE Booleans)

Reading Input
    - Type widening with the GET() function is allowed. 
    - When reading a boolean input, the input must be given as an integer.
      The integer will be converted to 0 or 1. Using "true" or
      "false" is unsupported. (SEE Booleans)

Default Variable Values
    - When a variable is declared, it is always initialized to some default 
      value.
        The following is a list of default values for variable types:
            BOOL := 0;
            INT := 0;
            REAL := 0.0;
            STRING := "";
    - When a STRING is declared, the first character is initialized
      to the null character '\0', and the remaining characters
      are unmodified.
    - When an array of variables is declared, each element of the array is 
      initialized to the default value corresponding to the array's type;

Type Widening
    - Type widening is supported if and only if the user is assigning an 
      expression to a variable where the variable has a "wider" type than the 
      expression.
        The following is supported:
            Ex.     airPressure : REAL;
                    airPressure := 6 + 4;
        The following is not suppported:
            Ex.     airPressure : INT;
                    airPressure := 6.0 + 4.0; <- ERROR