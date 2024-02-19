SnakeGame.java file:

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
    private final int CELL_SIZE = 20;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int NUM_ROWS = HEIGHT / CELL_SIZE;
    private final int NUM_COLS = WIDTH / CELL_SIZE;
    private final int INITIAL_SNAKE_LENGTH = 5;

    private LinkedList<Point> snake;
    private Point food;
    private int direction;
    private boolean isRunning;

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        initializeGame();
    }

    private void initializeGame() {
        snake = new LinkedList<>();
        snake.add(new Point(NUM_COLS / 2, NUM_ROWS / 2));
        generateFood();
        direction = KeyEvent.VK_RIGHT;
        isRunning = true;

        Timer timer = new Timer(100, this);
        timer.start();

        Thread renderingThread = new Thread(() -> {
            while (isRunning) {
                repaint();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        renderingThread.start();
    }

    private void generateFood() {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(NUM_COLS);
            y = rand.nextInt(NUM_ROWS);
        } while (snake.contains(new Point(x, y)));

        food = new Point(x, y);
    }

    private void handleKeyPress(int keyCode) {
        if ((keyCode == KeyEvent.VK_LEFT) && (direction != KeyEvent.VK_RIGHT)) {
            direction = KeyEvent.VK_LEFT;
        } else if ((keyCode == KeyEvent.VK_RIGHT) && (direction != KeyEvent.VK_LEFT)) {
            direction = KeyEvent.VK_RIGHT;
        } else if ((keyCode == KeyEvent.VK_UP) && (direction != KeyEvent.VK_DOWN)) {
            direction = KeyEvent.VK_UP;
        } else if ((keyCode == KeyEvent.VK_DOWN) && (direction != KeyEvent.VK_UP)) {
            direction = KeyEvent.VK_DOWN;
        }
    }

    private void move() {
        Point head = snake.getFirst();
        Point newHead = new Point(head);

        if (direction == KeyEvent.VK_LEFT) {
            newHead.x--;
        } else if (direction == KeyEvent.VK_RIGHT) {
            newHead.x++;
        } else if (direction == KeyEvent.VK_UP) {
            newHead.y--;
        } else if (direction == KeyEvent.VK_DOWN) {
            newHead.y++;
        }

        if (newHead.equals(food)) {
            snake.addFirst(food);
            generateFood();
        } else {
            snake.addFirst(newHead);
            snake.removeLast();
        }

        checkCollision();
    }

    private void checkCollision() {
        Point head = snake.getFirst();

        if (head.x < 0 || head.x >= NUM_COLS || head.y < 0 || head.y >= NUM_ROWS || snake.contains(head)) {
            isRunning = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isRunning) {
            move();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.RED);
        g.fillRect(food.x * CELL_SIZE, food.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        g.setColor(Color.GREEN);
        for (Point point : snake) {
            g.fillRect(point.x * CELL_SIZE, point.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new SnakeGame());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}