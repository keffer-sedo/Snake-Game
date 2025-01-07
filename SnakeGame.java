import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int boardwidth;
    int boardheight;
    int TileSize = 25;
    // Snake
    Tile snakehead;
    ArrayList<Tile> snakebody;
    // Food
    Tile food;
    Random random;
    Timer gameloop;
    int velocityX = 0;
    int velocityY = 0;
    boolean gameover = false;

    SnakeGame(int boardwidth, int boardheight) {
        this.boardheight = boardheight;
        this.boardwidth = boardwidth;
        setPreferredSize(new Dimension(this.boardwidth, this.boardheight));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        snakebody = new ArrayList<>();
        snakehead = new Tile(5, 5);
        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        gameloop = new Timer(100, this);
        gameloop.start();
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (gameover) {
            g.setColor(Color.RED);
            g.drawString("Game Over! Press R to Restart.", boardwidth / 4, boardheight / 2);
            return;
        }

        // Grid
        g.setColor(Color.GRAY);
        for (int i = 0; i < boardheight / TileSize; i++) {
            g.drawLine(i * TileSize, 0, i * TileSize, boardheight);
            g.drawLine(0, i * TileSize, boardwidth, i * TileSize);
        }

        // Snake body
        g.setColor(Color.GREEN);
        for (Tile snakepart : snakebody) {
            g.fillRect(snakepart.x * TileSize, snakepart.y * TileSize, TileSize, TileSize);
        }

        // Snake head
        g.fillRect(snakehead.x * TileSize, snakehead.y * TileSize, TileSize, TileSize);

        // Food
        g.setColor(Color.RED);
        g.fillRect(food.x * TileSize, food.y * TileSize, TileSize, TileSize);
    }

    public void placeFood() {
        food.x = random.nextInt(boardwidth / TileSize);
        food.y = random.nextInt(boardheight / TileSize);
    }

    public void move() {
        // Add current head position to body
        snakebody.add(0, new Tile(snakehead.x, snakehead.y));

        // Move the snake head
        snakehead.x += velocityX;
        snakehead.y += velocityY;

        // Game over conditions
        for (int i = 1; i < snakebody.size(); i++) { // Skip checking the head
            Tile snakepart = snakebody.get(i);
            if (collision(snakehead, snakepart)) {
                gameover = true;
                return;
            }
        }

        // Wrap-around collision
        if (snakehead.x < 0) snakehead.x = boardwidth / TileSize - 1;
        if (snakehead.y < 0) snakehead.y = boardheight / TileSize - 1;
        if (snakehead.x >= boardwidth / TileSize) snakehead.x = 0;
        if (snakehead.y >= boardheight / TileSize) snakehead.y = 0;

        // Check collision with food
        if (collision(snakehead, food)) {
            placeFood();
        } else {
            // Remove the tail if no food was eaten
            snakebody.remove(snakebody.size() - 1);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameover) {
            move();
        }
        repaint();
    }
 
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY == 0) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY == 0) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX == 0) {
            velocityX = -1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX == 0) {
            velocityX = 1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_R && gameover) {
            // Restart game
            gameover = false;
            snakebody.clear();
            snakehead = new Tile(5, 5);
            velocityX = 0;
            velocityY = 0;
            placeFood();
            gameloop.start();
        }
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame snakeGame = new SnakeGame(500, 500);
        frame.add(snakeGame);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        snakeGame.requestFocus();
    }
}

