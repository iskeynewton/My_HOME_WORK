package org.my_homework.caculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 基础计算器面板（连续运算+扩展功能）
 */
public class BasicCalculatorPanel extends JPanel {
    // 显示框（输入+结果）
    private final JTextField displayField;
    // 输入的表达式
    private String input = "";

    public BasicCalculatorPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        // 1. 显示框配置
        displayField = new JTextField();
        displayField.setText("0");
        displayField.setEditable(false);
        displayField.setFont(new Font("微软雅黑", Font.BOLD, 28));
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setBackground(Color.LIGHT_GRAY);
        displayField.setForeground(Color.WHITE);
        displayField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(displayField, BorderLayout.NORTH);

        // 2. 按钮面板（网格布局：6行4列）
        JPanel buttonPanel = new JPanel(new GridLayout(6, 4, 8, 8));
        buttonPanel.setBackground(Color.WHITE);

        // 按钮文本数组（按布局顺序）
        String[] buttonTexts = {
                "AC", "←", "%", "÷",
                "7", "8", "9", "×",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "00", "0", ".", "=",
                "√", "1/x", " ", " " // 空按钮占位
        };

        // 创建并添加所有按钮
        for (String text : buttonTexts) {
            JButton button = new JButton(text);
            // 按钮样式
            button.setFont(new Font("微软雅黑", Font.BOLD, 16));
            button.setFocusPainted(false);
            button.setBackground(getButtonColor(text));
            button.setForeground(text.equals("AC") ? Color.WHITE : Color.BLACK);
            button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            // 按钮点击事件
            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * 根据按钮文本设置颜色
     */
    private Color getButtonColor(String text) {
        if (text.equals("AC")) return Color.RED;
        if (text.equals("←") || text.equals("√") || text.equals("1/x")) return Color.ORANGE;
        if (text.equals("+") || text.equals("-") || text.equals("×") || text.equals("÷") || text.equals("%") || text.equals("="))
            return Color.CYAN;
        return Color.WHITE;
    }

    /**
     * 按钮点击监听器
     */
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case "AC": // 清空
                    input = "";
                    displayField.setText("0");
                    break;
                case "←": // 后退
                    if (!input.isEmpty()) {
                        input = input.substring(0, input.length() - 1);
                        displayField.setText(input.isEmpty() ? "0" : input);
                    }
                    break;
                case "=": // 计算结果
                    calculateResult();
                    break;
                case "√": // 平方根
                    calculateSqrt();
                    break;
                case "1/x": // 倒数
                    calculateReciprocal();
                    break;
                default: // 数字、运算符、小数点
                    handleInput(command);
                    break;
            }
        }

        /**
         * 处理数字、运算符、小数点输入
         */
        private void handleInput(String command) {
            // 避免开头是运算符（除了负号，但简化处理，不支持负数开头）
            if (input.isEmpty() && isOperator(command)) {
                JOptionPane.showMessageDialog(BasicCalculatorPanel.this, "无效输入：不能以运算符开头");
                return;
            }
            // 避免连续输入运算符
            if (!input.isEmpty() && isOperator(input.charAt(input.length() - 1)) && isOperator(command)) {
                JOptionPane.showMessageDialog(BasicCalculatorPanel.this, "无效输入：不能连续输入运算符");
                return;
            }
            // 避免多个小数点
            if (command.equals(".") && input.contains(".")) {
                JOptionPane.showMessageDialog(BasicCalculatorPanel.this, "无效输入：只能有一个小数点");
                return;
            }

            input += command;
            displayField.setText(input);
        }

        /**
         * 判断是否为运算符
         */
        private boolean isOperator(char c) {
            return c == '+' || c == '-' || c == '×' || c == '÷' || c == '%';
        }

        private boolean isOperator(String s) {
            return s.equals("+") || s.equals("-") || s.equals("×") || s.equals("÷") || s.equals("%");
        }

        /**
         * 计算表达式结果（调用工具类）
         */
        private void calculateResult() {
            if (input.isEmpty()) return;
            try {
                // 替换×为*，÷为/（工具类支持标准运算符）
                String standardInput = input.replace("×", "*").replace("÷", "/");
                double result = CalculateUtil.calculateExpression(standardInput);
                // 处理整数结果（去掉末尾的.0）
                String resultStr = result % 1 == 0 ? String.valueOf((long) result) : String.valueOf(result);
                displayField.setText(resultStr);
                input = resultStr; // 结果作为下一次计算的初始值（支持连续计算）
            } catch (ArithmeticException e) {
                displayField.setText("错误：" + e.getMessage());
                input = "";
            } catch (Exception e) {
                displayField.setText("错误：无效表达式");
                input = "";
            }
        }

        /**
         * 计算平方根
         */
        private void calculateSqrt() {
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(BasicCalculatorPanel.this, "请先输入数字");
                return;
            }
            try {
                double num = Double.parseDouble(input);
                if (num < 0) {
                    throw new ArithmeticException("负数不能开平方");
                }
                double result = Math.sqrt(num);
                String resultStr = result % 1 == 0 ? String.valueOf((long) result) : String.valueOf(result);
                displayField.setText(resultStr);
                input = resultStr;
            } catch (ArithmeticException e) {
                displayField.setText("错误：" + e.getMessage());
                input = "";
            } catch (Exception e) {
                displayField.setText("错误：无效数字");
                input = "";
            }
        }

        /**
         * 计算倒数
         */
        private void calculateReciprocal() {
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(BasicCalculatorPanel.this, "请先输入数字");
                return;
            }
            try {
                double num = Double.parseDouble(input);
                if (num == 0) {
                    throw new ArithmeticException("0不能取倒数");
                }
                double result = 1 / num;
                String resultStr = result % 1 == 0 ? String.valueOf((long) result) : String.valueOf(result);
                displayField.setText(resultStr);
                input = resultStr;
            } catch (ArithmeticException e) {
                displayField.setText("错误：" + e.getMessage());
                input = "";
            } catch (Exception e) {
                displayField.setText("错误：无效数字");
                input = "";
            }
        }
    }
}