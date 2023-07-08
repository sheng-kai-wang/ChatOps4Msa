package ntou.soselab.chatops4msa.Entity.ToolkitFunction;

import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Stack;

@Component
public class MathToolkit extends ToolkitFunction {

    /**
     * @param expression like 2 * 3 / 4
     * @return like 1.5
     */
    public String toolkitMathCalculate(String expression) throws ToolkitFunctionException {
        double answer = calculate(expression);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(answer);
    }

    private static double calculate(String expression) {
        expression = expression.replaceAll("\\s+", "");

        Stack<Double> numberStack = new Stack<>();
        Stack<Character> operatorStack = new Stack<>();

        int index = 0;
        while (index < expression.length()) {
            char ch = expression.charAt(index);
            if (Character.isDigit(ch)) {
                StringBuilder sb = new StringBuilder();
                while (index < expression.length() && (Character.isDigit(expression.charAt(index)) || expression.charAt(index) == '.')) {
                    sb.append(expression.charAt(index));
                    index++;
                }
                double number = Double.parseDouble(sb.toString());
                numberStack.push(number);
            } else if (ch == '(') {
                operatorStack.push(ch);
                index++;
            } else if (ch == ')') {
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                    double result = performOperation(operatorStack.pop(), numberStack.pop(), numberStack.pop());
                    numberStack.push(result);
                }
                if (!operatorStack.isEmpty() && operatorStack.peek() == '(') {
                    operatorStack.pop();
                }
                index++;
            } else if (isOperator(ch)) {
                while (!operatorStack.isEmpty() && hasPrecedence(ch, operatorStack.peek())) {
                    double result = performOperation(operatorStack.pop(), numberStack.pop(), numberStack.pop());
                    numberStack.push(result);
                }
                operatorStack.push(ch);
                index++;
            } else {
                throw new IllegalArgumentException("Invalid character: " + ch);
            }
        }

        while (!operatorStack.isEmpty()) {
            double result = performOperation(operatorStack.pop(), numberStack.pop(), numberStack.pop());
            numberStack.push(result);
        }

        return numberStack.pop();
    }

    private static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private static boolean hasPrecedence(char op1, char op2) {
        return (op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-');
    }

    private static double performOperation(char operator, double operand2, double operand1) {
        return switch (operator) {
            case '+' -> operand1 + operand2;
            case '-' -> operand1 - operand2;
            case '*' -> operand1 * operand2;
            case '/' -> operand1 / operand2;
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }
}
