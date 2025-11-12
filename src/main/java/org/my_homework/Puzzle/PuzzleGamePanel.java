package org.my_homework.Puzzle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;

public class PuzzleGamePanel extends JPanel {
    // 游戏配置常量
    private static final int BASE_SIZE = 600; // 拼图容器基础尺寸
    private static final String[] NETWORK_IMAGE_URLS = {
            "https://picsum.photos/800/800?random=1",
            "https://picsum.photos/800/800?random=2",
            "https://picsum.photos/800/800?random=3",
            "https://picsum.photos/800/800?random=4",
            "https://picsum.photos/800/800?random=5"
    };
    private static final int[] DIFFICULTY_SIZES = {3, 4, 5}; // 3×3, 4×4, 5×5
    private static final int[] DIFFICULTY_TIMES = {30, 60, 90}; // 对应难度的挑战时间（秒）
    private static final int SHUFFLE_ITERATIONS = 100; // 拼图打乱次数

    // 游戏状态变量
    private int currentDifficulty = 0; // 0-简单，1-中等，2-困难
    private int currentSize; // 当前拼图尺寸（N×N）
    private int remainingTime; // 剩余挑战时间
    private BufferedImage originalImage; // 原图
    private List<PuzzlePiece> puzzlePieces = new ArrayList<>();
    private PuzzlePiece emptyPiece; // 空白块
    private boolean isPlaying = false;
    private Timer gameTimer;
    private JLabel timerLabel;
    private JLabel statusLabel;
    private JFrame parentFrame;
    private Random random = new Random(); // 随机数生成器

    // UI 组件
    private JPanel puzzleContainer;
    private JButton startBtn;
    private JButton showOriginalBtn;
    private JButton customImageBtn;
    private JRadioButton easyBtn;
    private JRadioButton mediumBtn;
    private JRadioButton hardBtn;
    private JFileChooser fileChooser;

    public PuzzleGamePanel(JFrame frame) {
        this.parentFrame = frame;
        this.currentSize = DIFFICULTY_SIZES[currentDifficulty];
        this.remainingTime = DIFFICULTY_TIMES[currentDifficulty];
        initUI();
        initGameResources();
        initTimer();
    }

    // 初始化UI
    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // 顶部控制面板
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);

        // 拼图容器
        puzzleContainer = new JPanel(null);
        puzzleContainer.setPreferredSize(new Dimension(BASE_SIZE, BASE_SIZE));
        puzzleContainer.setBorder(BorderFactory.createLineBorder(new Color(59, 130, 246), 4));
        puzzleContainer.setBackground(new Color(243, 244, 246));
        add(new JScrollPane(puzzleContainer), BorderLayout.CENTER);

        // 状态标签
        statusLabel = new JLabel("请选择难度并点击\"开始游戏\"", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SimHei", Font.PLAIN, 16));
        // 备选字体设置，确保中文显示
        if (statusLabel.getFont().getFamily().equals("Dialog")) {
            statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        }
        statusLabel.setForeground(new Color(30, 41, 59));
        add(statusLabel, BorderLayout.SOUTH);
    }

    // 创建控制面板
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(Color.WHITE);

        // 难度选择
        JLabel difficultyLabel = new JLabel("难度：");
        difficultyLabel.setFont(new Font("SimHei", Font.PLAIN, 14));
        // 备选字体设置，确保中文显示
        if (difficultyLabel.getFont().getFamily().equals("Dialog")) {
            difficultyLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        }
        panel.add(difficultyLabel);

        easyBtn = createDifficultyRadioButton("简单(3×3)", 0);
        mediumBtn = createDifficultyRadioButton("中等(4×4)", 1);
        hardBtn = createDifficultyRadioButton("困难(5×5)", 2);

        ButtonGroup difficultyGroup = new ButtonGroup();
        difficultyGroup.add(easyBtn);
        difficultyGroup.add(mediumBtn);
        difficultyGroup.add(hardBtn);
        easyBtn.setSelected(true);

        panel.add(easyBtn);
        panel.add(mediumBtn);
        panel.add(hardBtn);

        // 计时器显示
        timerLabel = new JLabel("剩余时间：00:30");
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        timerLabel.setForeground(new Color(30, 41, 59));
        timerLabel.setVisible(false);
        panel.add(timerLabel);

        // 功能按钮
        startBtn = createButton("开始游戏", new Color(59, 130, 246));
        showOriginalBtn = createButton("查看原图", new Color(16, 185, 129));
        customImageBtn = createButton("自定义图片", new Color(245, 158, 11));

        startBtn.addActionListener(e -> startGame());
        showOriginalBtn.addActionListener(e -> showOriginalImage());
        customImageBtn.addActionListener(e -> selectCustomImage());

        panel.add(startBtn);
        panel.add(showOriginalBtn);
        panel.add(customImageBtn);

        return panel;
    }

    // 创建难度选择单选按钮
    private JRadioButton createDifficultyRadioButton(String text, int index) {
        JRadioButton btn = new JRadioButton(text);
        btn.setFont(new Font("SimHei", Font.PLAIN, 14));
        // 备选字体设置，确保中文显示
        if (btn.getFont().getFamily().equals("Dialog")) {
            btn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        }
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(30, 41, 59));
        btn.addActionListener(e -> {
            currentDifficulty = index;
            currentSize = DIFFICULTY_SIZES[index];
            remainingTime = DIFFICULTY_TIMES[index];
            updateTimerLabel();
            if (!isPlaying) {
                statusLabel.setText(String.format("已选择%s难度，点击\"开始游戏\"开始挑战", text));
            }
        });
        return btn;
    }

    // 创建功能按钮
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SimHei", Font.PLAIN, 14));
        // 备选字体设置，确保中文显示
        if (btn.getFont().getFamily().equals("Dialog")) {
            btn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        }
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 30));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // 初始化游戏资源
    private void initGameResources() {
        // 初始化文件选择器
        fileChooser = new JFileChooser();
        // 使用更现代的文件过滤器方式
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "图片文件 (*.jpg, *.jpeg, *.png, *.gif)", "jpg", "jpeg", "png", "gif"));

        // 尝试从网络加载随机图片
        try {
            loadRandomNetworkImage();
        } catch (Exception e) {
            e.printStackTrace();
            // 如果网络图片加载失败，使用内置默认图片
            createDefaultPlaceholderImage();
            JOptionPane.showMessageDialog(this, "网络图片加载失败，使用默认占位图", "警告", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // 加载随机网络图片
    private void loadRandomNetworkImage() throws IOException {
        String randomImageUrl = NETWORK_IMAGE_URLS[random.nextInt(NETWORK_IMAGE_URLS.length)];
        // 使用URI创建URL，避免过时警告
        try {
            java.net.URI uri = new java.net.URI(randomImageUrl);
            URL url = uri.toURL();
            originalImage = ImageIO.read(url);
            statusLabel.setText("已加载网络图片，点击\"开始游戏\"开始挑战");
        } catch (java.net.URISyntaxException | java.net.MalformedURLException e) {
            throw new IOException("无效的图片URL: " + randomImageUrl, e);
        }
    }
    
    // 创建默认占位图片
    private void createDefaultPlaceholderImage() {
        originalImage = new BufferedImage(BASE_SIZE, BASE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics g = originalImage.getGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, BASE_SIZE, BASE_SIZE);
        g.setColor(Color.GRAY);
        g.setFont(new Font("SimHei", Font.PLAIN, 16));
        // 备选字体设置，确保中文显示
        if (g.getFont().getFamily().equals("Dialog")) {
            g.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        }
        g.drawString("默认占位图片", BASE_SIZE/2 - 60, BASE_SIZE/2);
        g.dispose();
    }

    // 初始化计时器
    private void initTimer() {
        gameTimer = new Timer(1000, e -> {
            remainingTime--;
            updateTimerLabel();

            // 时间到，挑战失败
            if (remainingTime <= 0) {
                stopTimer();
                isPlaying = false;
                puzzleContainer.setEnabled(false);
                statusLabel.setText("挑战失败！时间已用尽");
                startBtn.setText("重新挑战");
                JOptionPane.showMessageDialog(parentFrame, "时间到！未能在规定时间内完成拼图", "挑战失败", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    // 更新计时器显示
    private void updateTimerLabel() {
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;
        timerLabel.setText(String.format("剩余时间：%02d:%02d", minutes, seconds));

        // 剩余10秒时变红
        if (remainingTime <= 10) {
            timerLabel.setForeground(new Color(239, 68, 68));
        } else {
            timerLabel.setForeground(new Color(30, 41, 59));
        }
    }

    // 开始游戏
    private void startGame() {
        if (isPlaying) {
            // 重置游戏
            resetGame();
        } else {
            // 开始新游戏
            if (originalImage == null) {
                JOptionPane.showMessageDialog(this, "请先选择图片", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            isPlaying = true;
            puzzleContainer.setEnabled(true);
            timerLabel.setVisible(true);
            startBtn.setText("重新开始");
            statusLabel.setText("挑战进行中...");
            remainingTime = DIFFICULTY_TIMES[currentDifficulty];
            updateTimerLabel();

            // 生成拼图
            generatePuzzle();

            // 启动计时器
            gameTimer.start();
        }
    }

    // 生成拼图
    private void generatePuzzle() {
        // 清空容器
        puzzleContainer.removeAll();
        puzzlePieces.clear();

        // 计算每个碎片的尺寸
        int pieceSize = BASE_SIZE / currentSize;

        // 缩放原图以适应拼图容器
        BufferedImage scaledImage = scaleImage(originalImage, BASE_SIZE, BASE_SIZE);

        // 创建拼图碎片
        for (int row = 0; row < currentSize; row++) {
            for (int col = 0; col < currentSize; col++) {
                // 最后一个为空白块
                if (row == currentSize - 1 && col == currentSize - 1) {
                    emptyPiece = new PuzzlePiece(null, row, col, row, col);
                    continue;
                }

                // 裁剪图片生成碎片
                BufferedImage pieceImage = scaledImage.getSubimage(
                        col * pieceSize, row * pieceSize, pieceSize, pieceSize
                );

                // 创建碎片组件
                PuzzlePiece piece = new PuzzlePiece(pieceImage, row, col, row, col);
                piece.setSize(pieceSize, pieceSize);
                piece.setLocation(col * pieceSize, row * pieceSize);
                piece.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                // 初始化拖拽功能
                initDragAndDrop(piece);

                puzzlePieces.add(piece);
                puzzleContainer.add(piece);
            }
        }

        // 打乱拼图
        shufflePuzzle();

        // 刷新容器
        puzzleContainer.revalidate();
        puzzleContainer.repaint();
    }

    // 缩放图片（保持比例，自适应容器）
    private BufferedImage scaleImage(BufferedImage original, int targetWidth, int targetHeight) {
        double scale = Math.min(
                (double) targetWidth / original.getWidth(),
                (double) targetHeight / original.getHeight()
        );

        int newWidth = (int) (original.getWidth() * scale);
        int newHeight = (int) (original.getHeight() * scale);

        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaled.createGraphics();

        // 填充背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, targetWidth, targetHeight);

        // 绘制缩放后的图片（居中）
        int x = (targetWidth - newWidth) / 2;
        int y = (targetHeight - newHeight) / 2;
        g2d.drawImage(original, x, y, newWidth, newHeight, null);
        g2d.dispose();

        return scaled;
    }

    // 打乱拼图
    private void shufflePuzzle() {
        int pieceSize = BASE_SIZE / currentSize;

        // 随机交换指定次数
        for (int i = 0; i < SHUFFLE_ITERATIONS; i++) {
            // 找到可移动的碎片（与空白块相邻）
            List<PuzzlePiece> movablePieces = findMovablePieces();
            
            if (movablePieces.isEmpty()) continue;

            // 随机选择一个碎片交换
            PuzzlePiece selected = movablePieces.get(random.nextInt(movablePieces.size()));
            
            // 交换位置
            swapWithEmptyPiece(selected, pieceSize);
        }
    }
    
    // 查找可移动的拼图块
    private List<PuzzlePiece> findMovablePieces() {
        List<PuzzlePiece> movablePieces = new ArrayList<>();
        
        for (PuzzlePiece piece : puzzlePieces) {
            if (isAdjacentToEmpty(piece)) {
                movablePieces.add(piece);
            }
        }
        
        return movablePieces;
    }
    
    // 检查拼图块是否与空白块相邻
    private boolean isAdjacentToEmpty(PuzzlePiece piece) {
        int rowDiff = Math.abs(piece.getCurrentRow() - emptyPiece.getCurrentRow());
        int colDiff = Math.abs(piece.getCurrentCol() - emptyPiece.getCurrentCol());
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }
    
    // 与空白块交换位置
    private void swapWithEmptyPiece(PuzzlePiece piece, int pieceSize) {
        // 交换位置
        int tempRow = piece.getCurrentRow();
        int tempCol = piece.getCurrentCol();

        piece.setCurrentRow(emptyPiece.getCurrentRow());
        piece.setCurrentCol(emptyPiece.getCurrentCol());
        piece.setLocation(
                piece.getCurrentCol() * pieceSize,
                piece.getCurrentRow() * pieceSize
        );

        emptyPiece.setCurrentRow(tempRow);
        emptyPiece.setCurrentCol(tempCol);

        // 更新正确位置状态
        updatePieceCorrectStatus(piece);
    }
    
    // 更新拼图块的正确位置状态
    private void updatePieceCorrectStatus(PuzzlePiece piece) {
        piece.setCorrect(
                piece.getCurrentRow() == piece.getTargetRow() &&
                piece.getCurrentCol() == piece.getTargetCol()
        );
    }

    // 初始化拖拽功能
    // 初始化拖拽功能
    private void initDragAndDrop(PuzzlePiece piece) {
        // 设置拖拽目标
        new DropTarget(puzzleContainer, DnDConstants.ACTION_MOVE, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                if (!isPlaying) return;

                try {
                    Transferable tr = dtde.getTransferable();
                    if (tr.isDataFlavorSupported(PuzzlePiece.DATA_FLAVOR)) {
                        dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                        PuzzlePiece draggedPiece = (PuzzlePiece) tr.getTransferData(PuzzlePiece.DATA_FLAVOR);

                        // 计算鼠标位置对应的格子
                        int pieceSize = BASE_SIZE / currentSize;
                        Point dropPoint = dtde.getLocation();
                        int targetCol = Math.round(dropPoint.x / (float) pieceSize);
                        int targetRow = Math.round(dropPoint.y / (float) pieceSize);

                        // 检查是否可以交换（目标位置是空白块）
                        if (targetRow == emptyPiece.getCurrentRow() && targetCol == emptyPiece.getCurrentCol()) {
                            // 交换位置
                            int oldRow = draggedPiece.getCurrentRow();
                            int oldCol = draggedPiece.getCurrentCol();

                            draggedPiece.setCurrentRow(targetRow);
                            draggedPiece.setCurrentCol(targetCol);
                            draggedPiece.setLocation(targetCol * pieceSize, targetRow * pieceSize);

                            // 更新空白块位置
                            emptyPiece.setCurrentRow(oldRow);
                            emptyPiece.setCurrentCol(oldCol);

                            // 检查是否在正确位置
                            draggedPiece.setCorrect(
                                    draggedPiece.getCurrentRow() == draggedPiece.getTargetRow() &&
                                            draggedPiece.getCurrentCol() == draggedPiece.getTargetCol()
                            );

                            // 检查拼图是否完成
                            if (checkPuzzleComplete()) {
                                completePuzzle();
                            }
                        } else {
                            // 回到原位置
                            draggedPiece.setLocation(
                                    draggedPiece.getCurrentCol() * pieceSize,
                                    draggedPiece.getCurrentRow() * pieceSize
                            );
                        }

                        dtde.dropComplete(true);
                        puzzleContainer.repaint();
                    } else {
                        dtde.rejectDrop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dtde.rejectDrop();
                }
            }
        });

        piece.setTransferHandler(new TransferHandler() {
            @Override
            protected Transferable createTransferable(JComponent c) {
                return new Transferable() {
                    @Override
                    public DataFlavor[] getTransferDataFlavors() {
                        return new DataFlavor[]{PuzzlePiece.DATA_FLAVOR};
                    }

                    @Override
                    public boolean isDataFlavorSupported(DataFlavor flavor) {
                        return flavor.equals(PuzzlePiece.DATA_FLAVOR);
                    }

                    @Override
                    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                        if (!flavor.equals(PuzzlePiece.DATA_FLAVOR)) {
                            throw new UnsupportedFlavorException(flavor);
                        }
                        return piece;
                    }
                };
            }

            @Override
            public int getSourceActions(JComponent c) {
                return DnDConstants.ACTION_MOVE;
            }
        });

        piece.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (isPlaying) {
                    TransferHandler th = piece.getTransferHandler();
                    th.exportAsDrag(piece, e, TransferHandler.MOVE);
                }
            }
        });
    }


    // 检查拼图是否完成
    private boolean checkPuzzleComplete() {
        for (PuzzlePiece piece : puzzlePieces) {
            if (!piece.isCorrect()) {
                return false;
            }
        }
        return true;
    }

    // 拼图完成处理
    private void completePuzzle() {
        stopTimer();
        isPlaying = false;
        statusLabel.setText("恭喜！拼图完成！");
        startBtn.setText("再次挑战");
        timerLabel.setVisible(false);
        JOptionPane.showMessageDialog(parentFrame, "拼图成功！太棒了！", "挑战成功", JOptionPane.INFORMATION_MESSAGE);
    }

    // 查看原图
    private void showOriginalImage() {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(this, "暂无图片可查看", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 创建原图预览窗口
        JFrame originalFrame = new JFrame("原图预览");
        originalFrame.setSize(600, 600);
        originalFrame.setLocationRelativeTo(parentFrame);
        originalFrame.setResizable(false);

        JLabel imageLabel = new JLabel(new ImageIcon(scaleImage(originalImage, 580, 580)));
        originalFrame.add(new JScrollPane(imageLabel));

        originalFrame.setVisible(true);
    }

    // 选择自定义图片
    private void selectCustomImage() {
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                originalImage = ImageIO.read(selectedFile);
                statusLabel.setText("自定义图片已加载，点击\"开始游戏\"开始挑战");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "图片加载失败，请选择有效的图片文件", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 重置游戏
   private void resetGame() {
    stopTimer();
    isPlaying = false;
    timerLabel.setVisible(false);
    puzzleContainer.removeAll();
    statusLabel.setText(String.format("已选择%s难度，点击\"开始游戏\"开始挑战",
            easyBtn.isSelected() ? "简单(3×3)" : mediumBtn.isSelected() ? "中等(4×4)" : "困难(5×5)"));
    startBtn.setText("开始游戏");
    puzzleContainer.revalidate();
    puzzleContainer.repaint();
}


    // 停止计时器
    public void stopTimer() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
    }

    // 拼图碎片类
    public static class PuzzlePiece extends JLabel {
        // 使用更简单的方式创建DataFlavor，避免异常处理问题
        public static final DataFlavor DATA_FLAVOR;
        static {
            try {
                DATA_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=org.my_homework.Puzzle.PuzzleGamePanel$PuzzlePiece");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to initialize DataFlavor", e);
            }
        }

        private final int targetRow; // 目标行
        private final int targetCol; // 目标列
        private int currentRow; // 当前行
        private int currentCol; // 当前列
        private boolean isCorrect; // 是否在正确位置

        public PuzzlePiece(BufferedImage image, int currentRow, int currentCol, int targetRow, int targetCol) {
            super(image != null ? new ImageIcon(image) : null);
            this.currentRow = currentRow;
            this.currentCol = currentCol;
            this.targetRow = targetRow;
            this.targetCol = targetCol;
            this.isCorrect = false;

            if (image != null) {
                setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            }
        }

        // Getters and Setters
        public int getCurrentRow() { return currentRow; }
        public void setCurrentRow(int currentRow) { this.currentRow = currentRow; }
        public int getCurrentCol() { return currentCol; }
        public void setCurrentCol(int currentCol) { this.currentCol = currentCol; }
        public int getTargetRow() { return targetRow; }
        public int getTargetCol() { return targetCol; }
        public boolean isCorrect() { return isCorrect; }
        public void setCorrect(boolean correct) { isCorrect = correct; }
    }
}
    