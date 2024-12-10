/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mavenproject1;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author Милана
 */
public class Mavenproject1 extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private double angleX = 0;
    private double angleY = 0;

    private double translateX = 0;
    private double translateY = 0;
    private double scale = 1;
    
    private final double focalLength = 500;

    private JTextField rField;
    private JTextField tField;

    private double A = 100, B = 50, C = 80; // Параметры суперэллипсоида
    private double t = 2, r = 2;             // Параметры формы


    public Mavenproject1() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
         // Создаем панель для ввода параметров
        JPanel inputPanel = new JPanel();
        tField = new JTextField(String.valueOf(t), 5);
        rField = new JTextField(String.valueOf(r), 5);
        JButton updateButton = new JButton("Обновить");

        inputPanel.add(new JLabel("Параметр t:"));
        inputPanel.add(tField);
        inputPanel.add(new JLabel("Параметр r:"));
        inputPanel.add(rField);
        inputPanel.add(updateButton);

        add(inputPanel, BorderLayout.NORTH);
        
        updateButton.addActionListener((ActionEvent e) -> {
            try {
                this.t = Double.parseDouble(tField.getText());
                this.r = Double.parseDouble(rField.getText());
                repaint(); // Перерисовываем панель
                validate();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Введите корректные значения для t и r.");
            }
        });

        // Добавляем обработчик клавиатуры
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        translateY -= 10; // Движение вверх
                        break;
                    case KeyEvent.VK_DOWN:
                        translateY += 10; // Движение вниз
                        break;
                    case KeyEvent.VK_LEFT:
                        translateX -= 10; // Движение влево
                        break;
                    case KeyEvent.VK_RIGHT:
                        translateX += 10; // Движение вправо
                        break;
                    case KeyEvent.VK_W:
                        angleX += 0.1; // Вращение вверх
                        break;
                    case KeyEvent.VK_S:
                        angleX -= 0.1; // Вращение вниз
                        break;
                    case KeyEvent.VK_A:
                        angleY -= 0.1; // Вращение влево
                        break;
                    case KeyEvent.VK_D:
                        angleY += 0.1; // Вращение вправо
                        break;
                    case KeyEvent.VK_E: // Увеличить масштаб
                        scale += 0.1;
                        break;
                    case KeyEvent.VK_Q: // Уменьшить масштаб
                        scale -= 0.1;
                        break;
                }
                repaint();
            }
        });
        setFocusable(true); // Позволяет панели получать фокус для обработки клавиатуры
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawSuperEllipsoid(g);
    }

    private void drawSuperEllipsoid(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(1));
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int uSteps = 50;
        int vSteps = 25;
        double du = 2 * Math.PI / uSteps;
        double dv = Math.PI / vSteps;

        for (int i = 0; i <= vSteps; i++) {
            double v = -Math.PI / 2 + i * dv;
            for (int j = 0; j <= uSteps; j++) {
                double u = -Math.PI + j * du;
                double x = A * c(v, 2 / t) * c(u, 2 / r); // 2/t и 2/r - важно!!!
            double y = B * c(v, 2 / t) * s(u, 2 / r); // 2/t и 2/r - важно!!!
            double z = C * s(v, 2 / t);                // 2/t - важно!!!

                double[] rotated = rotateX(x, y, z, angleX);
                rotated = rotateY(rotated[0], rotated[1], rotated[2], angleY);

                double projectedX = rotated[0] * focalLength / (focalLength + rotated[2]);
                double projectedY = rotated[1] * focalLength / (focalLength + rotated[2]);

                int screenX = (int) (centerX + projectedX * scale + translateX);
                int screenY = (int) (centerY - projectedY * scale + translateY);

                g2d.fillRect(screenX, screenY, 2, 2); // Рисуем точки

                if (j > 0) {
                    double prevU = -Math.PI + (j - 1) * du;
                    double prevX = A * c(v, t/2) * c(prevU, r/2);
                    double prevY = B * c(v, t/2) * s(prevU, r/2);
                    double prevZ = C * s(v, t/2);
                    double[] prevRotated = rotateX(prevX, prevY, prevZ, angleX);
                    prevRotated = rotateY(prevRotated[0], prevRotated[1], prevRotated[2], angleY);
                    double projectedXPrev = prevRotated[0] * focalLength / (focalLength + prevRotated[2]);
                    double projectedYPrev = prevRotated[1] * focalLength / (focalLength + prevRotated[2]);
                    int prevScreenX = (int) (centerX + projectedXPrev * scale + translateX);
                    int prevScreenY = (int) (centerY - projectedYPrev * scale + translateY);
                    g2d.drawLine(prevScreenX, prevScreenY, screenX, screenY);
                }

                if (i > 0) {
                    double prevV = -Math.PI / 2 + (i - 1) * dv;
                    double prevX = A * c(prevV, t/2) * c(u, r/2);
                    double prevY = B * c(prevV, t/2) * s(u, r/2);
                    double prevZ = C * s(prevV, t/2);
                    double[] prevRotated = rotateX(prevX, prevY, prevZ, angleX);
                    prevRotated = rotateY(prevRotated[0], prevRotated[1], prevRotated[2], angleY);
                    double projectedXPrev = prevRotated[0] * focalLength / (focalLength + prevRotated[2]);
                    double projectedYPrev = prevRotated[1] * focalLength / (focalLength + prevRotated[2]);
                    int prevScreenX = (int) (centerX + projectedXPrev * scale + translateX);
                    int prevScreenY = (int) (centerY - projectedYPrev * scale + translateY);
                    g2d.drawLine(prevScreenX, prevScreenY, screenX, screenY);
                }
            }
        }
    }
    // Вспомогательные методы для вычисления c(w, m) и s(w, m)
    private double c(double angle, double exponent) {
        return Math.signum(Math.cos(angle)) * Math.pow(Math.abs(Math.cos(angle)), exponent);
    }

    private double s(double angle, double exponent) {
        return Math.signum(Math.sin(angle)) * Math.pow(Math.abs(Math.sin(angle)), exponent);
    }

    private double[] rotateX(double x, double y, double z, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new double[]{
            x,
            y * cos - z * sin,
            y * sin + z * cos
        };
    }

    private double[] rotateY(double x, double y, double z, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new double[]{
            x * cos + z * sin,
            y,
            -x * sin + z * cos
        };
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("3D Spiral");
        Mavenproject1 panel = new Mavenproject1();
        frame.add(panel);
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
