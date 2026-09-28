package com.deepak.genAI;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTool {


    @Tool(description = """
        Performs basic mathematical calculations on two numbers.
        Supported operations are add, subtract, multiply, divide, and modulo.
        Use 'add' for addition, 'subtract' for subtraction,
        'multiply' for multiplication, 'divide' for division,
        and 'modulo' for finding the remainder.
        The second number must not be zero for divide and modulo operations.
        """)
    public Double calculate(@ToolParam(description = "The mathematical operation to perform. Supported operations are: add, subtract, multiply, divide, and modulo.")
                            String operation,

                            @ToolParam(description = "The first numeric value (left operand) used in the calculation.")
                            Double a,

                            @ToolParam(description = "The second numeric value (right operand) used in the calculation. Must not be zero for divide and modulo operations.")
                            Double b) {
        System.out.println("calculator has been called"+a+" and b is"+b);

        switch (operation.toLowerCase()) {

            case "add":
                return a + b;

            case "subtract":
                return a - b;

            case "multiply":
                return a * b;

            case "divide":
                if (b == 0) {
                    throw new ArithmeticException("Cannot divide by zero");
                }
                return a / b;

            case "mod":
                if (b == 0) {
                    throw new ArithmeticException("Cannot perform modulo by zero");
                }
                return a % b;

            default:
                throw new IllegalArgumentException("Invalid operation: " + operation);
        }
    }
}
