package org.my_homework.caculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 贷款计算器面板（等额本息）
 */
public class LoanCalculatorPanel extends JPanel {
    // 输入组件
    private final JTextField loanAmountField;
    private final JTextField interestRateField;
    private final JTextField loanTermField;

    // 结果显示组件
    private final JLabel monthlyPaymentLabel;
    private final JLabel totalPaymentLabel;
    private final JLabel totalInterestLabel;

    public LoanCalculatorPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        // 标题
        JLabel titleLabel = new JLabel("贷款计算器（等额本息）");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // 输入面板（网格布局：3行2列）
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        inputPanel.setBackground(Color.WHITE);

        // 贷款金额
        JLabel amountLabel = new JLabel("贷款金额（元）：");
        amountLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        loanAmountField = new JTextField();
        loanAmountField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        inputPanel.add(amountLabel);
        inputPanel.add(loanAmountField);

        // 年利率
        JLabel rateLabel = new JLabel("年利率（%）：");
        rateLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        interestRateField = new JTextField();
        interestRateField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        inputPanel.add(rateLabel);
        inputPanel.add(interestRateField);

        // 贷款期限
        JLabel termLabel = new JLabel("贷款期限（年）：");
        termLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        loanTermField = new JTextField();
        loanTermField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        inputPanel.add(termLabel);
        inputPanel.add(loanTermField);

        // 计算按钮
        JButton calculateBtn = new JButton("计算还款");
        calculateBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        calculateBtn.setBackground(Color.CYAN);
        calculateBtn.setFocusPainted(false);
        calculateBtn.addActionListener(new CalculateListener());

        // 结果面板（网格布局：3行2列）
        JPanel resultPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        resultPanel.setBackground(Color.WHITE);
        resultPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "计算结果", 2, 0, new Font("微软雅黑", Font.PLAIN, 16)));

        // 每月还款
        JLabel monthlyLabel = new JLabel("每月还款额：");
        monthlyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        monthlyPaymentLabel = new JLabel("¥0.00");
        monthlyPaymentLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        monthlyPaymentLabel.setForeground(Color.BLUE);
        resultPanel.add(monthlyLabel);
        resultPanel.add(monthlyPaymentLabel);

        // 总还款额
        JLabel totalLabel = new JLabel("总还款额：");
        totalLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        totalPaymentLabel = new JLabel("¥0.00");
        totalPaymentLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        totalPaymentLabel.setForeground(Color.BLUE);
        resultPanel.add(totalLabel);
        resultPanel.add(totalPaymentLabel);

        // 总利息
        JLabel interestLabel = new JLabel("总支付利息：");
        interestLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        totalInterestLabel = new JLabel("¥0.00");
        totalInterestLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        totalInterestLabel.setForeground(Color.RED);
        resultPanel.add(interestLabel);
        resultPanel.add(totalInterestLabel);

        // 组装中间面板（输入+按钮）
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(inputPanel, BorderLayout.CENTER);
        centerPanel.add(calculateBtn, BorderLayout.SOUTH);

        // 组装整个面板
        add(centerPanel, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);
    }

    /**
     * 计算按钮监听器
     */
    private class CalculateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // 获取输入值并验证
                double loanAmount = Double.parseDouble(loanAmountField.getText().trim());
                double interestRate = Double.parseDouble(interestRateField.getText().trim());
                int loanTerm = Integer.parseInt(loanTermField.getText().trim());

                if (loanAmount <= 0) {
                    JOptionPane.showMessageDialog(LoanCalculatorPanel.this, "贷款金额必须大于0");
                    return;
                }
                if (interestRate < 0) {
                    JOptionPane.showMessageDialog(LoanCalculatorPanel.this, "年利率不能为负数");
                    return;
                }
                if (loanTerm <= 0) {
                    JOptionPane.showMessageDialog(LoanCalculatorPanel.this, "贷款期限必须大于0");
                    return;
                }

                // 调用工具类计算
                double[] results = CalculateUtil.calculateLoan(loanAmount, interestRate, loanTerm);
                double monthlyPayment = results[0];
                double totalPayment = results[1];
                double totalInterest = results[2];

                // 格式化结果（保留2位小数）
                monthlyPaymentLabel.setText(String.format("¥%.2f", monthlyPayment));
                totalPaymentLabel.setText(String.format("¥%.2f", totalPayment));
                totalInterestLabel.setText(String.format("¥%.2f", totalInterest));

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(LoanCalculatorPanel.this, "输入错误：请输入有效的数字");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(LoanCalculatorPanel.this, "计算错误：" + ex.getMessage());
            }
        }
    }
}