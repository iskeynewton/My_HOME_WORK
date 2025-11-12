package org.my_homework.caculator;

import java.util.Deque;
import java.util.LinkedList;

/**
 * 计算工具类（表达式计算+贷款计算）
 */
public class CalculateUtil {
    /**
     * 计算数学表达式（支持+、-、*、/、%，自动处理优先级）
     * @param expression 标准表达式（例：5*9+5+50）
     * @return 计算结果
     */
    public static double calculateExpression(String expression) {
        // 转换为后缀表达式（逆波兰表达式）
        String postfix = infixToPostfix(expression);
        // 计算后缀表达式
        return calculatePostfix(postfix);
    }

    /**
     * 中缀表达式转后缀表达式（逆波兰表达式）
     */
    private static String infixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        Deque<Character> operatorStack = new LinkedList<>();

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                // 数字或小数点：直接加入后缀表达式
                postfix.append(c);
                // 处理多位数（数字或小数点连续）
                while (i + 1 < infix.length() && (Character.isDigit(infix.charAt(i + 1)) || infix.charAt(i + 1) == '.')) {
                    postfix.append(infix.charAt(++i));
                }
                postfix.append(' '); // 用空格分隔不同数字
            } else if (c == '(') {
                // 左括号：压栈
                operatorStack.push(c);
            } else if (c == ')') {
                // 右括号：弹出栈中运算符直到左括号（左括号不加入后缀）
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                    postfix.append(operatorStack.pop()).append(' ');
                }
                operatorStack.pop(); // 弹出左括号
            } else if (isOperator(c)) {
                // 运算符：弹出栈中优先级>=当前的运算符，再压栈
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(' && getPriority(operatorStack.peek()) >= getPriority(c)) {
                    postfix.append(operatorStack.pop()).append(' ');
                }
                operatorStack.push(c);
            } else {
                throw new IllegalArgumentException("无效字符：" + c);
            }
        }

        // 弹出栈中剩余运算符
        while (!operatorStack.isEmpty()) {
            postfix.append(operatorStack.pop()).append(' ');
        }

        return postfix.toString().trim();
    }

    /**
     * 计算后缀表达式
     */
    private static double calculatePostfix(String postfix) {
        Deque<Double> numberStack = new LinkedList<>();
        String[] tokens = postfix.split(" ");

        for (String token : tokens) {
            if (token.isEmpty()) continue;

            if (token.matches("\\d+(\\.\\d+)?")) {
                // 数字：压栈
                numberStack.push(Double.parseDouble(token));
            } else if (isOperator(token.charAt(0))) {
                // 运算符：弹出两个数计算，结果压栈
                if (numberStack.size() < 2) {
                    throw new IllegalArgumentException("无效表达式");
                }
                double num2 = numberStack.pop();
                double num1 = numberStack.pop();
                double result = calculate(num1, num2, token.charAt(0));
                numberStack.push(result);
            } else {
                throw new IllegalArgumentException("无效 token：" + token);
            }
        }

        if (numberStack.size() != 1) {
            throw new IllegalArgumentException("无效表达式");
        }

        return numberStack.pop();
    }

    /**
     * 二元运算
     */
    private static double calculate(double num1, double num2, char operator) {
        switch (operator) {
            case '+':
                return num1 + num2;
            case '-':
                return num1 - num2;
            case '*':
                return num1 * num2;
            case '/':
                if (num2 == 0) {
                    throw new ArithmeticException("除数不能为0");
                }
                return num1 / num2;
            case '%':
                if (num2 == 0) {
                    throw new ArithmeticException("模数不能为0");
                }
                return num1 % num2;
            default:
                throw new IllegalArgumentException("无效运算符：" + operator);
        }
    }

    /**
     * 判断是否为运算符
     */
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    /**
     * 获取运算符优先级（*、/、% 为1，+、- 为0）
     */
    private static int getPriority(char operator) {
        return (operator == '*' || operator == '/' || operator == '%') ? 1 : 0;
    }

    /**
     * 贷款计算（等额本息）
     * @param loanAmount 贷款金额（元）
     * @param annualRate 年利率（%）
     * @param loanTerm 贷款期限（年）
     * @return 结果数组：[每月还款额, 总还款额, 总利息]
     */
    public static double[] calculateLoan(double loanAmount, double annualRate, int loanTerm) {
        double monthlyRate = annualRate / 100 / 12; // 月利率
        int totalMonths = loanTerm * 12; // 总月数
        double monthlyPayment;

        if (monthlyRate == 0) {
            // 零利率：每月等额本金
            monthlyPayment = loanAmount / totalMonths;
        } else {
            // 等额本息公式：每月还款额 = 本金 × [月利率 × (1+月利率)^总月数] ÷ [(1+月利率)^总月数 - 1]
            double pow = Math.pow(1 + monthlyRate, totalMonths);
            monthlyPayment = loanAmount * (monthlyRate * pow) / (pow - 1);
        }

        double totalPayment = monthlyPayment * totalMonths;
        double totalInterest = totalPayment - loanAmount;

        return new double[]{monthlyPayment, totalPayment, totalInterest};
    }
}