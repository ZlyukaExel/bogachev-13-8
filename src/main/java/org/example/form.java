package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class form extends JFrame {
    private JTable table1;
    private final DefaultTableModel model;
    private int rows = 14;
    private int columns = rows;
    Random random = new Random();
    ImageIcon[] icons = {new ImageIcon("red.gif"), new ImageIcon("green.gif"),
            new ImageIcon("purple.gif"), new ImageIcon("blue.gif"),
            new ImageIcon("pink.gif"), new ImageIcon("yellow.gif")};
    private JPanel panel;
    private JLabel levelLabel;
    private int level = 1;
    private JLabel scoreLabel;
    private int score;
    private int kSaver;
    private JLabel turnsLabel;
    private int turns = 30;
    private int shuffles = 2;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JButton button6;
    private JButton button7;

    public form() {
        this.setTitle("Перекраска!");
        this.setContentPane(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        model = new DefaultTableModel(rows, columns) {
            @Override
            public Class<?> getColumnClass(int column) {
                return ImageIcon.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        buildTable();

        table1.setModel(model);

        button1.setIcon(icons[4]);
        button2.setIcon(icons[5]);
        button3.setIcon(icons[0]);
        button4.setIcon(icons[2]);
        button5.setIcon(icons[1]);
        button6.setIcon(icons[3]);

        button1.addActionListener(e -> paintTable(4));

        button2.addActionListener(e -> paintTable(5));

        button3.addActionListener(e -> paintTable(0));

        button4.addActionListener(e -> paintTable(2));

        button5.addActionListener(e -> paintTable(1));

        button6.addActionListener(e -> paintTable(3));

        button7.addActionListener(e -> shuffleTable());
        button7.setText("Перемешать! (Осталось: " + shuffles + ")");
    }

    public void buildTable() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int colorID = random.nextInt(icons.length);
                model.setValueAt(icons[colorID], i, j);
            }
        }

        kSaver = 0;
    }

    public void paintTable(int colorID) {
        turns--;
        turnsLabel.setText(turns + "");

        Object oldColor = model.getValueAt(0, 0);
        boolean[][] valuesSaver = new boolean[rows][columns];
        valuesSaver[0][0] = true;
        boolean isChanged;

        do {
            isChanged = false;

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    boolean up = (i > 0) && valuesSaver[i - 1][j];
                    boolean down = (i < rows - 1) && valuesSaver[i + 1][j];
                    boolean left = (j > 0) && valuesSaver[i][j - 1];
                    boolean right = (j < columns - 1) && valuesSaver[i][j + 1];

                    if (!valuesSaver[i][j] && model.getValueAt(i, j).equals(oldColor) &&
                            (up || down || left || right)) {
                        valuesSaver[i][j] = true;
                        isChanged = true;
                    }
                }
            }
        } while (isChanged);

        int k = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (valuesSaver[i][j])
                    k++;
            }
        }
        score += (k - kSaver) * 5;
        scoreLabel.setText(score + "");
        kSaver = k;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (valuesSaver[i][j])
                    model.setValueAt(icons[colorID], i, j);
            }
        }

        checkWin();
    }

    public void checkWin() {
        Object color = model.getValueAt(0, 0);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (!model.getValueAt(i, j).equals(color)) {
                    if (turns > 0) return;
                    else {
                        int response = JOptionPane.showOptionDialog(this, "Вы проиграли!\nВаш счёт: " + score, "Конец игры",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                                new Object[]{"Играть заново", "Выход"}, null);
                        if (response == 0)
                            restartGame();
                        else
                            System.exit(0);
                    }
                }
            }
        }

        //пабэда
        level++;
        levelLabel.setText(level + "");

        turns *= level;
        turns += 30;
        turnsLabel.setText(turns + "");

        rows += 2;
        model.setRowCount(rows);

        columns = rows;
        model.setColumnCount(columns);

        score += (int) Math.pow(10, 2 + level);
        scoreLabel.setText(score + "");

        shuffles += 1 + level;
        button7.setEnabled(true);
        button7.setText("Перемешать! (Осталось: " + shuffles + ")");

        buildTable();
    }

    public void restartGame() {
        rows = columns = 14;
        model.setRowCount(rows);
        model.setColumnCount(columns);

        turns = 30;
        turnsLabel.setText(turns + "");

        score = 0;
        scoreLabel.setText(score + "");

        level = 1;
        levelLabel.setText(level + "");

        shuffles = 2;
        button7.setEnabled(true);
        button7.setText("Перемешать! (Осталось: " + shuffles + ")");

        buildTable();
    }

    public void shuffleTable(){
        List<List<Object>> savedTable = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<Object> row = new ArrayList<>();
            for (int j = 0; j < columns; j++) {
                row.add(model.getValueAt(i, j));
            }
            Collections.shuffle(row);
            savedTable.add(row);
        }
        Collections.shuffle(savedTable);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                model.setValueAt(savedTable.get(i).get(j), i, j);
            }
        }

        shuffles--;
        button7.setText("Перемешать! (Осталось: " + shuffles + ")");
        if (shuffles == 0)
            button7.setEnabled(false);
    }
}
