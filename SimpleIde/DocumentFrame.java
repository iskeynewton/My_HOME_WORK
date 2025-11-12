package org.my_homework.SimpleIde;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.beans.PropertyVetoException;

public class DocumentFrame extends JInternalFrame {
    private JTextPane textPane;
    private JScrollPane scrollPane;
    private File currentFile;
    private boolean isNewDocument; // 标识是否为新建文档
    private boolean isModified; // 标识文档是否被修改

    public DocumentFrame(String title) {
        super(title, true, true, true, true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // 设置为手动处理关闭
        initComponents();
        isNewDocument = true;
        isModified = false;
        setSize(600, 400);
        updateTitle();
    }

    private void initComponents() {
        // 创建文本编辑区域
        textPane = new JTextPane();
        textPane.setFont(new Font("宋体", Font.PLAIN, 14));
        textPane.setMargin(new Insets(5, 5, 5, 5));
        
        // 添加文档监听器，跟踪文档修改状态
        textPane.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                setModified(true);
            }

            public void removeUpdate(DocumentEvent e) {
                setModified(true);
            }

            public void changedUpdate(DocumentEvent e) {
                setModified(true);
            }
        });

        // 创建滚动面板
        scrollPane = new JScrollPane(textPane);
        setContentPane(scrollPane);

        // 添加内部窗口监听器
        addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e) {
                if (isModified) {
                    int option = JOptionPane.showConfirmDialog(
                            DocumentFrame.this,
                            "文档已修改，是否保存？",
                            "保存确认",
                            JOptionPane.YES_NO_CANCEL_OPTION
                    );
                    if (option == JOptionPane.YES_OPTION) {
                        saveFile();
                    } else if (option == JOptionPane.CANCEL_OPTION) {
                        // 用户取消关闭，什么都不做
                    } else if (option == JOptionPane.NO_OPTION) {
                        // 用户选择不保存，手动关闭窗口
                        dispose();
                    }
                } else {
                    // 文档未修改，直接关闭
                    dispose();
                }
            }
        });
    }

    public void loadFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            textPane.read(reader, null);
            currentFile = file;
            isNewDocument = false;
            isModified = false;
            setTitle(file.getName());
            updateTitle();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "加载文件失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveFile() {
        // 如果是新建文档或当前文件为空，显示保存对话框
        if (isNewDocument || currentFile == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
            fileChooser.setSelectedFile(new File(getTitle() + ".txt"));
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
                // 确保文件扩展名为.txt
                if (!currentFile.getName().toLowerCase().endsWith(".txt")) {
                    currentFile = new File(currentFile.getPath() + ".txt");
                }
                isNewDocument = false;
                setTitle(currentFile.getName());
            } else {
                return; // 用户取消保存
            }
        }

        // 保存文件内容
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
            textPane.write(writer);
            isModified = false;
            updateTitle();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "保存文件失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setFontForSelectedText() {
        try {
            JTextPane editor = textPane;
            JDialog fontDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "选择字体", true);
            fontDialog.setLayout(new BorderLayout());
            
            // 创建字体选择面板
            JPanel fontPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            fontPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // 字体选择器
            JLabel fontLabel = new JLabel("字体:");
            JComboBox<String> fontComboBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
            fontComboBox.setSelectedItem(editor.getFont().getFamily());
            
            // 字号选择器
            JLabel sizeLabel = new JLabel("字号:");
            SpinnerModel sizeModel = new SpinnerNumberModel(editor.getFont().getSize(), 8, 72, 1);
            JSpinner sizeSpinner = new JSpinner(sizeModel);
            
            // 样式选择器
            JLabel styleLabel = new JLabel("样式:");
            JComboBox<String> styleComboBox = new JComboBox<>(new String[]{"常规", "粗体", "斜体", "粗斜体"});
            int style = editor.getFont().getStyle();
            if (style == Font.PLAIN) styleComboBox.setSelectedIndex(0);
            else if (style == Font.BOLD) styleComboBox.setSelectedIndex(1);
            else if (style == Font.ITALIC) styleComboBox.setSelectedIndex(2);
            else if (style == Font.BOLD + Font.ITALIC) styleComboBox.setSelectedIndex(3);
            
            fontPanel.add(fontLabel);
            fontPanel.add(fontComboBox);
            fontPanel.add(sizeLabel);
            fontPanel.add(sizeSpinner);
            fontPanel.add(styleLabel);
            fontPanel.add(styleComboBox);
            
            // 创建按钮面板
            JPanel buttonPanel = new JPanel();
            JButton okButton = new JButton("确定");
            JButton cancelButton = new JButton("取消");
            
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String fontFamily = (String) fontComboBox.getSelectedItem();
                    int fontSize = (int) sizeSpinner.getValue();
                    int fontStyle = Font.PLAIN;
                    int styleIndex = styleComboBox.getSelectedIndex();
                    if (styleIndex == 1) fontStyle = Font.BOLD;
                    else if (styleIndex == 2) fontStyle = Font.ITALIC;
                    else if (styleIndex == 3) fontStyle = Font.BOLD + Font.ITALIC;
                    
                    // 应用字体到选中的文本或整个文档
                    StyledDocument doc = editor.getStyledDocument();
                    int start = editor.getSelectionStart();
                    int end = editor.getSelectionEnd();
                    
                    if (start != end) {
                        // 应用到选中的文本
                        SimpleAttributeSet set = new SimpleAttributeSet();
                        StyleConstants.setFontFamily(set, fontFamily);
                        StyleConstants.setFontSize(set, fontSize);
                        StyleConstants.setBold(set, (fontStyle & Font.BOLD) != 0);
                        StyleConstants.setItalic(set, (fontStyle & Font.ITALIC) != 0);
                        doc.setCharacterAttributes(start, end - start, set, false);
                    } else {
                        // 应用到整个文档
                        editor.setFont(new Font(fontFamily, fontStyle, fontSize));
                    }
                    
                    fontDialog.dispose();
                    setModified(true);
                }
            });
            
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fontDialog.dispose();
                }
            });
            
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            
            fontDialog.add(fontPanel, BorderLayout.CENTER);
            fontDialog.add(buttonPanel, BorderLayout.SOUTH);
            fontDialog.pack();
            fontDialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(DocumentFrame.this));
            fontDialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "设置字体失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void toggleBold() {
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        
        if (start != end) {
            // 检查选中文本的粗体状态
            AttributeSet attrs = doc.getCharacterElement(start).getAttributes();
            boolean isBold = StyleConstants.isBold(attrs);
            
            // 切换粗体状态
            SimpleAttributeSet newAttrs = new SimpleAttributeSet();
            StyleConstants.setBold(newAttrs, !isBold);
            doc.setCharacterAttributes(start, end - start, newAttrs, false);
            setModified(true);
        }
    }

    public void toggleItalic() {
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        
        if (start != end) {
            // 检查选中文本的斜体状态
            AttributeSet attrs = doc.getCharacterElement(start).getAttributes();
            boolean isItalic = StyleConstants.isItalic(attrs);
            
            // 切换斜体状态
            SimpleAttributeSet newAttrs = new SimpleAttributeSet();
            StyleConstants.setItalic(newAttrs, !isItalic);
            doc.setCharacterAttributes(start, end - start, newAttrs, false);
            setModified(true);
        }
    }

    public void toggleUnderline() {
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        
        if (start != end) {
            // 检查选中文本的下划线状态
            AttributeSet attrs = doc.getCharacterElement(start).getAttributes();
            boolean isUnderline = StyleConstants.isUnderline(attrs);
            
            // 切换下划线状态
            SimpleAttributeSet newAttrs = new SimpleAttributeSet();
            StyleConstants.setUnderline(newAttrs, !isUnderline);
            doc.setCharacterAttributes(start, end - start, newAttrs, false);
            setModified(true);
        }
    }

    private void setModified(boolean modified) {
        this.isModified = modified;
        updateTitle();
    }

    private void updateTitle() {
        String baseTitle = super.getTitle();
        if (isModified) {
            if (!baseTitle.endsWith(" *")) {
                setTitle(baseTitle + " *");
            }
        } else {
            if (baseTitle.endsWith(" *")) {
                setTitle(baseTitle.substring(0, baseTitle.length() - 2));
            }
        }
    }

    public boolean isNewDocument() {
        return isNewDocument;
    }

    public boolean isModified() {
        return isModified;
    }

    public File getCurrentFile() {
        return currentFile;
    }
}