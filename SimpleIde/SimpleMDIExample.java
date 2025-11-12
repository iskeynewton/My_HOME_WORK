package org.my_homework.SimpleIde;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;

public class SimpleMDIExample extends JFrame {
    private JDesktopPane desktopPane;
    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu, windowMenu;
    private JMenuItem newMenuItem, openMenuItem, saveMenuItem, exitMenuItem;
    private JMenuItem fontMenuItem, boldMenuItem, italicMenuItem, underlineMenuItem;
    private JMenuItem cascadeMenuItem, tileHorizontallyMenuItem, tileVerticallyMenuItem;
    private int documentCount = 0;

    public SimpleMDIExample() {
        // 设置窗口标题和大小
        super("Simple MDI Text Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建桌面面板
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(Color.LIGHT_GRAY);
        setContentPane(desktopPane);

        // 创建菜单栏
        createMenuBar();

        // 创建工具栏
        createToolBar();

        // 设置窗口位置居中
        setLocationRelativeTo(null);
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();

        // 文件菜单
        fileMenu = new JMenu("文件");
        newMenuItem = new JMenuItem("新建");
        newMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewDocument();
            }
        });

        openMenuItem = new JMenuItem("打开");
        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDocument();
            }
        });

        saveMenuItem = new JMenuItem("保存");
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCurrentDocument();
            }
        });

        exitMenuItem = new JMenuItem("退出");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        // 编辑菜单
        editMenu = new JMenu("编辑");
        fontMenuItem = new JMenuItem("设置字体");
        fontMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFontForSelectedText();
            }
        });

        boldMenuItem = new JMenuItem("粗体");
        boldMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleBold();
            }
        });

        italicMenuItem = new JMenuItem("斜体");
        italicMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleItalic();
            }
        });

        underlineMenuItem = new JMenuItem("下划线");
        underlineMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleUnderline();
            }
        });

        editMenu.add(fontMenuItem);
        editMenu.addSeparator();
        editMenu.add(boldMenuItem);
        editMenu.add(italicMenuItem);
        editMenu.add(underlineMenuItem);

        // 窗口菜单
        windowMenu = new JMenu("窗口");
        cascadeMenuItem = new JMenuItem("层叠");
        cascadeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cascadeFrames();
            }
        });

        tileHorizontallyMenuItem = new JMenuItem("水平平铺");
        tileHorizontallyMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tileFramesHorizontally();
            }
        });

        tileVerticallyMenuItem = new JMenuItem("垂直平铺");
        tileVerticallyMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tileFramesVertically();
            }
        });

        windowMenu.add(cascadeMenuItem);
        windowMenu.add(tileHorizontallyMenuItem);
        windowMenu.add(tileVerticallyMenuItem);

        // 添加所有菜单到菜单栏
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(windowMenu);

        // 设置菜单栏
        setJMenuBar(menuBar);
    }

    private void createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton newButton = new JButton("新建");
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewDocument();
            }
        });

        JButton openButton = new JButton("打开");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDocument();
            }
        });

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCurrentDocument();
            }
        });

        toolBar.add(newButton);
        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.addSeparator();

        JButton boldButton = new JButton("B");
        boldButton.setFont(new Font("Arial", Font.BOLD, 12));
        boldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleBold();
            }
        });

        JButton italicButton = new JButton("I");
        italicButton.setFont(new Font("Arial", Font.ITALIC, 12));
        italicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleItalic();
            }
        });

        JButton underlineButton = new JButton("U");
        underlineButton.setFont(new Font("Arial", Font.PLAIN, 12));
        underlineButton.setBorder(BorderFactory.createEtchedBorder());
        underlineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleUnderline();
            }
        });

        toolBar.add(boldButton);
        toolBar.add(italicButton);
        toolBar.add(underlineButton);

        add(toolBar, BorderLayout.NORTH);
    }

    private void createNewDocument() {
        documentCount++;
        DocumentFrame frame = new DocumentFrame("无标题 " + documentCount);
        desktopPane.add(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void openDocument() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            DocumentFrame frame = new DocumentFrame(file.getName());
            frame.loadFile(file);
            desktopPane.add(frame);
            frame.setVisible(true);
            try {
                frame.setSelected(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveCurrentDocument() {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        for (JInternalFrame frame : frames) {
            if (frame.isSelected() && frame instanceof DocumentFrame) {
                ((DocumentFrame) frame).saveFile();
                break;
            }
        }
    }

    private void setFontForSelectedText() {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        for (JInternalFrame frame : frames) {
            if (frame.isSelected() && frame instanceof DocumentFrame) {
                ((DocumentFrame) frame).setFontForSelectedText();
                break;
            }
        }
    }

    private void toggleBold() {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        for (JInternalFrame frame : frames) {
            if (frame.isSelected() && frame instanceof DocumentFrame) {
                ((DocumentFrame) frame).toggleBold();
                break;
            }
        }
    }

    private void toggleItalic() {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        for (JInternalFrame frame : frames) {
            if (frame.isSelected() && frame instanceof DocumentFrame) {
                ((DocumentFrame) frame).toggleItalic();
                break;
            }
        }
    }

    private void toggleUnderline() {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        for (JInternalFrame frame : frames) {
            if (frame.isSelected() && frame instanceof DocumentFrame) {
                ((DocumentFrame) frame).toggleUnderline();
                break;
            }
        }
    }

    private void cascadeFrames() {
        desktopPane.getAllFramesInLayer(JLayeredPane.DEFAULT_LAYER);
        JInternalFrame[] frames = desktopPane.getAllFrames();
        int x = 20, y = 20;
        int width = 400, height = 300;
        int offset = 20;

        for (int i = frames.length - 1; i >= 0; i--) {
            JInternalFrame frame = frames[i];
            frame.reshape(x, y, width, height);
            x += offset;
            y += offset;
            try {
                frame.setSelected(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        }
    }

    private void tileFramesHorizontally() {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        if (frames.length == 0) return;

        int rows = (int) Math.ceil(Math.sqrt(frames.length));
        int cols = (int) Math.ceil((double) frames.length / rows);
        int frameWidth = desktopPane.getWidth() / cols;
        int frameHeight = desktopPane.getHeight() / rows;

        int index = 0;
        for (int i = 0; i < rows && index < frames.length; i++) {
            for (int j = 0; j < cols && index < frames.length; j++) {
                JInternalFrame frame = frames[index++];
                frame.reshape(j * frameWidth, i * frameHeight, frameWidth, frameHeight);
                try {
                    frame.setSelected(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void tileFramesVertically() {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        if (frames.length == 0) return;

        int cols = (int) Math.ceil(Math.sqrt(frames.length));
        int rows = (int) Math.ceil((double) frames.length / cols);
        int frameWidth = desktopPane.getWidth() / cols;
        int frameHeight = desktopPane.getHeight() / rows;

        int index = 0;
        for (int i = 0; i < rows && index < frames.length; i++) {
            for (int j = 0; j < cols && index < frames.length; j++) {
                JInternalFrame frame = frames[index++];
                frame.reshape(j * frameWidth, i * frameHeight, frameWidth, frameHeight);
                try {
                    frame.setSelected(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SimpleMDIExample editor = new SimpleMDIExample();
                editor.setVisible(true);
            }
        });
    }
}