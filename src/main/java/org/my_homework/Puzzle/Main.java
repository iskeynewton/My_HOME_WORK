package org.my_homework.Puzzle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        // 设置外观为系统默认
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 创建主窗口
        JFrame frame = new JFrame("myPuzzle - 智能拼图游戏");
        // 设置全局字体，确保中文显示正常
        try {
            UIManager.put("Label.font", new Font("SimHei", Font.PLAIN, 12));
            UIManager.put("Button.font", new Font("SimHei", Font.PLAIN, 12));
            UIManager.put("TextField.font", new Font("SimHei", Font.PLAIN, 12));
            UIManager.put("TextArea.font", new Font("SimHei", Font.PLAIN, 12));
            UIManager.put("Dialog.font", new Font("SimHei", Font.PLAIN, 12));
            UIManager.put("OptionPane.font", new Font("SimHei", Font.PLAIN, 12));
        } catch (Exception e) {
            // 如果SimHei不可用，尝试使用Microsoft YaHei
            try {
                UIManager.put("Label.font", new Font("Microsoft YaHei", Font.PLAIN, 12));
                UIManager.put("Button.font", new Font("Microsoft YaHei", Font.PLAIN, 12));
                UIManager.put("TextField.font", new Font("Microsoft YaHei", Font.PLAIN, 12));
                UIManager.put("TextArea.font", new Font("Microsoft YaHei", Font.PLAIN, 12));
                UIManager.put("Dialog.font", new Font("Microsoft YaHei", Font.PLAIN, 12));
                UIManager.put("OptionPane.font", new Font("Microsoft YaHei", Font.PLAIN, 12));
            } catch (Exception ex) {
                // 如果都不可用，使用系统默认字体
            }
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null); // 居中显示
        frame.setResizable(false);

        // 创建游戏面板
        PuzzleGamePanel gamePanel = new PuzzleGamePanel(frame);
        frame.add(gamePanel);

        // 窗口关闭事件处理
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gamePanel.stopTimer(); // 停止计时器
            }
        });

        frame.setVisible(true);
    }
}
    