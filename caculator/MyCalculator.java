package org.my_homework.caculator;

import javax.swing.*;
import java.awt.*;
import org.my_homework.caculator.BasicCalculatorPanel;
import org.my_homework.caculator.LoanCalculatorPanel;

/**
 * 多功能计算器主窗口（控制面板切换）
 */
public class MyCalculator extends JFrame {
    // 面板切换布局
    private final CardLayout cardLayout = new CardLayout();
    // 基础计算器面板
    private final BasicCalculatorPanel basicPanel = new BasicCalculatorPanel();
    // 贷款计算器面板
    private final LoanCalculatorPanel loanPanel = new LoanCalculatorPanel();

    public MyCalculator() {
        // 窗口配置
        setTitle("myCalculator - 多功能连续计算器");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示
        setResizable(true);

        // 主容器（使用CardLayout切换面板）
        JPanel mainContainer = new JPanel(cardLayout);
        mainContainer.add(basicPanel, "basic");
        mainContainer.add(loanPanel, "loan");

        // 切换按钮面板
        JPanel togglePanel = new JPanel();
        JButton switchToBasicBtn = new JButton("切换到基础计算器");
        JButton switchToLoanBtn = new JButton("切换到贷款计算器");

        // 按钮样式
        switchToBasicBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        switchToLoanBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        togglePanel.add(switchToBasicBtn);
        togglePanel.add(switchToLoanBtn);

        // 切换事件
        switchToBasicBtn.addActionListener(e -> cardLayout.show(mainContainer, "basic"));
        switchToLoanBtn.addActionListener(e -> cardLayout.show(mainContainer, "loan"));

        // 组装窗口
        add(togglePanel, BorderLayout.NORTH);
        add(mainContainer, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        //  Swing线程安全启动
        SwingUtilities.invokeLater(() -> {
            MyCalculator calculator = new MyCalculator();
            calculator.setVisible(true);
        });
    }
}