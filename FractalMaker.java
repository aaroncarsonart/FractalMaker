import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Random;

/**
 * FractalMaker is designed to render images by updating an image buffer
 * and then painting that result onto a JPanel. Use the buttons or arrow
 * keys to scroll through the various selected update methods for the demo.
 * <p>
 * All methodologies for updating the pixels array were discovered by
 * accident. By simply playing with arithmetic, bitwise operators, and
 * bit shifting, many unique patterns were found. A variety of different
 * results were selected for exhibition in this demo.
 *
 * @author Aaron Carson
 * @since Jun 23, 2014
 * @version Feb 3, 2024
 */
public class FractalMaker extends JPanel {
    public static final int ALGORITHM_COUNT = 15;

    private final BufferedImage image;
    private final int[] pixels;
    private final Random random;
    private int index;

    public FractalMaker(int width, int height) {
        super();
        setPreferredSize(new Dimension(width, height));
        setSize(getPreferredSize());
        setBackground(Color.BLACK);
        random = new Random();
        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        index = 0;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        render((Graphics2D) g);
    }

    public void render(Graphics2D g) {
        g.drawImage(image, null, null);
    }

    public void drawSquareFractal(int divisor, int bitShift) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                pixels[x + y * getWidth()] = ((x ^ y) / divisor) << bitShift;
            }
        }
    }

    public void drawBlueSquareFractal() {
        drawSquareFractal(2, 0);
    }

    public void drawGreenSquareFractal() {
        drawSquareFractal(2, 8);
    }

    public void drawRedSquareFractal() {
        drawSquareFractal(2, 16);
    }

    public void drawRandomNoise() {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                pixels[x + y * getWidth()] = random.nextInt();
            }
        }
    }

    public void drawHorizontalNoiseBands() {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                int i = x + y * getWidth();
                pixels[i] = i & random.nextInt(256) << 8;
            }
        }
    }

    public void drawVerticalAngledStripes() {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                pixels[x + y * getWidth()] = (x * 2 + y / 2) ^ 255 << 23;
            }
        }
    }

    public void drawQuadraticFractal() {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                pixels[x + y * getWidth()] = (~x * ~y) ^ (x ^ y) / 2;
            }
        }
    }

    public void drawTriangleFractal() {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                pixels[x + y * getWidth()] = (x ^ y) / ((x & y) + 1) ^ 255 << 24;
            }
        }
    }

    public void drawFadingTriangleFractal() {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                pixels[x + y * getWidth()] = (x ^ y) / ((~x & y) + 1);
            }
        }
    }

    private void updateFractalImage() {
        switch (index) {
            // Bit shifting to change the effected color channel.
            case 0:
                drawBlueSquareFractal();
                break;
            case 1:
                drawGreenSquareFractal();
                break;
            case 2:
                drawRedSquareFractal();
                break;
            // Scaling the size.
            case 3:
                drawSquareFractal(1, 0);
                break;
            case 4:
                drawSquareFractal(2, 18);
                break;
            case 5:
                drawSquareFractal(2, 12);
                break;
            // Odd divisors change the pattern.
            // Increasing the bit shift between RGB channel boundaries zooms out.
            case 6:
                drawSquareFractal(7, 5);
                break;
            case 7:
                drawSquareFractal(3, 21);
                break;
            case 8:
                drawSquareFractal(5, 13);
                break;
            // Other interesting patterns.
            case 9:
                drawRandomNoise();
                break;
            case 10:
                drawHorizontalNoiseBands();
                break;
            case 11:
                drawVerticalAngledStripes();
                break;
            case 12:
                drawQuadraticFractal();
                break;
            // The Sierpiński triangle.
            case 13:
                drawTriangleFractal();
                break;
            case 14:
                drawFadingTriangleFractal();
                break;
        }
    }

    private void onLeftButtonClick() {
        index--;
        if (index < 0) {
            index = ALGORITHM_COUNT - 1;
        }
        updateFractalImage();
        repaint();
    }

    private void onRightButtonClick() {
        index++;
        if (index >= ALGORITHM_COUNT) {
            index = 0;
        }
        updateFractalImage();
        repaint();
    }

    private static class LeftRightKeyListener implements KeyListener {
        private final Runnable onLeft;
        private final Runnable onRight;

        public LeftRightKeyListener(Runnable onLeft, Runnable onRight) {
            this.onLeft = onLeft;
            this.onRight = onRight;
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                onLeft.run();
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                onRight.run();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FractalMaker fractalMaker = new FractalMaker(512, 512);
            fractalMaker.updateFractalImage();

            JFrame frame = new JFrame("Fractal Maker");
            Container contentPane = frame.getContentPane();
            contentPane.add(fractalMaker, BorderLayout.CENTER);

            JPanel buttonPane = new JPanel(new FlowLayout());
            JButton leftButton = new JButton("<");
            JButton rightButton = new JButton(">");
            leftButton.addActionListener(event -> fractalMaker.onLeftButtonClick());
            rightButton.addActionListener(event -> fractalMaker.onRightButtonClick());
            buttonPane.add(leftButton);
            buttonPane.add(rightButton);
            contentPane.add(buttonPane, BorderLayout.SOUTH);

            Runnable onLeft = () -> {
                fractalMaker.onLeftButtonClick();
                leftButton.requestFocusInWindow();
            };
            Runnable onRight = () -> {
                fractalMaker.onRightButtonClick();
                rightButton.requestFocusInWindow();
            };
            KeyListener keyListener = new LeftRightKeyListener(onLeft, onRight);

            fractalMaker.addKeyListener(keyListener);
            frame.addKeyListener(keyListener);
            leftButton.addKeyListener(keyListener);
            rightButton.addKeyListener(keyListener);

            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.requestFocus();
        });
    }
}
