import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class Sprites4 extends JPanel {

    private final ControlBullet c = new ControlBullet();
    private final Timer timer = new Timer(50, new animationGUI());
    private int megamanX = 228, megamanY = 226;
    private int picNumber = 0;
    private final JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 10, 5);
    private final JPanel controlPanel = new JPanel();
    private boolean keyHoldRight = false, keyHoldLeft = false, keyHoldUp = false, jumpAction = false;

    public Sprites4() { //Adding keylistener and starting the time
        this.addKeyListener(new keyboard());
        timer.start();
    }

    public static void main(String[] args) {
        Sprites4 animation = new Sprites4();

        JFrame frame = new JFrame("Animation");
        animation.prepareGUI(frame);
    }

    public void prepareGUI(JFrame frame) { //Sets up the GUI
        Hashtable<Integer, JLabel> sliderTable = new Hashtable<>();
        sliderTable.put(0, new JLabel("Fast"));
        sliderTable.put(5, new JLabel("Normal"));
        sliderTable.put(10, new JLabel("Slow"));
        slider.setLabelTable(sliderTable);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        this.setPreferredSize(new Dimension(701, 345));
        slider.addChangeListener(new SliderChange(timer));
        JLabel description = new JLabel("Press WASD or arrow keys to move or press spacebar to fire");
        controlPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createTitledBorder("Drag to change animation speed")));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.add(slider);
        controlPanel.add(description);
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(this, BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(5000, 5000);
        frame.pack();
    }

    @Override
    public void paintComponent(Graphics g) {	//Paints/draws the background, megaman and bullets
        this.requestFocusInWindow();
        ImageIcon background = new ImageIcon("background.gif");
        ImageIcon megaman = new ImageIcon("Megaman//" + picNumber + ".png");
        background.paintIcon(this, g, 0, 0);
        megaman.paintIcon(this, g, megamanX, megamanY);
        c.drawLeft(g);
        c.drawRight(g);
    }

    private class SliderChange implements ChangeListener {

        private final Timer timer;

        public SliderChange(Timer t) {
            timer = t;
        }

        @Override
        public void stateChanged(ChangeEvent event) { //When slider has been changed, timer's delay will change
            JSlider source = (JSlider) event.getSource();

            if (!source.getValueIsAdjusting()) {
                timer.stop();
                timer.setDelay((source.getValue() + 1) * 10);
                timer.start();
            }
        }
    }

    private class animationGUI implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //If right or left keys is pressed, animation moves right or left
            if (keyHoldRight && megamanX < 660) {
                megamanX = megamanX + 10;
            } else if (keyHoldLeft && megamanX > 0) {
                megamanX = megamanX - 10;
            }

            //If up key is pressed animation  moves up and down
            if (keyHoldUp && picNumber >= 17 && picNumber <= 19 || picNumber >= 35 && picNumber <= 37) {
                megamanY = megamanY - 30;
            } else if (keyHoldUp && picNumber >= 21 && picNumber <= 23 || picNumber >= 38 && picNumber <= 40) {
                megamanY = megamanY + 30;

                if (picNumber == 23 || picNumber == 40) {
                    keyHoldUp = false;
                    jumpAction = false;
                }
            }

            //Makes sure if animation needs to be standing still or needs to be moving again
            if (picNumber == 6 || picNumber == 23 && !keyHoldRight) {
                picNumber = 5;
            } else if (picNumber == 24 || picNumber == 40 && !keyHoldLeft) {
                picNumber = 23;
            } else if (picNumber == 16 || picNumber == 23) {
                picNumber = 6;
            } else if (picNumber == 34 || picNumber == 40) {
                picNumber = 24;
            }

            try {
                //Runs the controlbullet class and leads to moveRight and moveLeft
                c.moveRight();
                c.moveLeft();
            } catch (Exception ignored) {
            }
            picNumber++;
            repaint();
        }
    }

    private class keyboard implements KeyListener {
        int charge = 0;
        @Override
        public void keyPressed(KeyEvent e) {

            if (e.getKeyChar() == 'd' || e.getKeyCode() == KeyEvent.VK_RIGHT) { //Move megaman to the right when d or right arrow key is pressed
                if (!keyHoldRight && !keyHoldLeft && !jumpAction) {
                    keyHoldRight = true;

                    picNumber = 7;
                }
            }
            if (e.getKeyChar() == 'a' || e.getKeyCode() == KeyEvent.VK_LEFT) { //Move megaman to the left when a or left arrow key is pressed
                if (!keyHoldLeft && !keyHoldRight && !jumpAction) {
                    keyHoldLeft = true;

                    picNumber = 25;
                }
            }
            if (e.getKeyChar() == 'w' || e.getKeyCode() == KeyEvent.VK_UP) { //Move megaman up when w or up arrow key is pressed
                if (!keyHoldUp) {
                    keyHoldUp = true;
                    jumpAction = true;

                    if (picNumber <= 16 && picNumber >= 6) {
                        picNumber = 17;
                    } else if (picNumber <= 34 && picNumber >= 24) {
                        picNumber = 35;
                    }
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                charge++;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // When key has been released this will make megaman stand
            if ((e.getKeyChar() == 'd' || e.getKeyCode() == KeyEvent.VK_RIGHT) && !keyHoldLeft) {
                keyHoldRight = false;

                if (!jumpAction) {
                    picNumber = 6;
                }
            } else if ((e.getKeyChar() == 'a' || e.getKeyCode() == KeyEvent.VK_LEFT) && !keyHoldRight) {
                keyHoldLeft = false;

                if (!jumpAction) {
                    picNumber = 24;
                }
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) { //When spacebar has been released run the ControlBullet, adding an element to either arraylist
                if (picNumber >= 6 && picNumber <= 23) {
                    c.addBulletRight(new Bullet(megamanX, megamanY));
                } else if (picNumber >= 24 && picNumber <= 41) {
                    c.addBulletLeft(new Bullet(megamanX, megamanY));
                }
                charge = 0;
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }
    }

    public class Bullet {

        private double x;
        private final double y;
        private int shotNumberLeft = 3, shotNumberRight = 0;
        private Image bulletRight = new ImageIcon("Megaman//Shots//" + shotNumberRight + ".png").getImage();
        private Image bulletLeft = new ImageIcon("Megaman//Shots//" + shotNumberLeft + ".png").getImage();

        //Bullet constructor: To set the bullet's coordinates
        public Bullet(double x, double y) {
            this.x = x;
            this.y = y;
        }

        //Move bullet to the right or left
        public void moveRight() {
            x += 20;
            bulletRight = new ImageIcon("Megaman//Shots//" + shotNumberRight + ".png").getImage();
            shotNumberRight++;
            if (shotNumberRight == 3) {
                shotNumberRight = 0;
            }
        }

        public void moveLeft() {
            x -= 20;
            bulletLeft = new ImageIcon("Megaman//Shots//" + shotNumberLeft + ".png").getImage();
            shotNumberLeft++;
            if (shotNumberLeft == 6) {
                shotNumberLeft = 3;
            }

        }

        //Draws the bullet facing to the right or left
        public void drawRight(Graphics g) {
            g.drawImage(bulletRight, (int) x, (int) y, null);
        }

        public void drawLeft(Graphics g) {
            g.drawImage(bulletLeft, (int) x, (int) y, null);
        }

        //ControlBullet class needs this x value to know when to remove the bullet
        public double getX() {
            return x;
        }
    }

    public class ControlBullet {

        //Creates two arraylist depending on the direction the bullet is going to go where it stores the bullets information(coordinates)
        private final ArrayList<Bullet> bulletListRight = new ArrayList<>();
        private final ArrayList<Bullet> bulletListLeft = new ArrayList<>();

        //Runs the bullet class to move the bullet to the right or left until the for loop reaches at the end of the arraylist
        public void moveRight() {
            for (Bullet bulletRight : bulletListRight) {
                bulletRight.moveRight();

                if (bulletRight.getX() > 701) {
                    removeBulletRight(bulletRight);
                }
            }
        }

        public void moveLeft() {
            for (Bullet bulletLeft : bulletListLeft) {
                bulletLeft.moveLeft();

                if (bulletLeft.getX() < -46) {
                    removeBulletLeft(bulletLeft);
                }
            }
        }

        //Repaints the bullet because the bullet moves
        public void drawRight(Graphics g) {
            for (Bullet bulletRight : bulletListRight) {
                bulletRight.drawRight(g);
            }
        }

        public void drawLeft(Graphics g) {
            for (Bullet bulletLeft : bulletListLeft) {
                bulletLeft.drawLeft(g);
            }
        }

        //Adds the bullet's information(x and y)
        public void addBulletLeft(Bullet shot) {
            bulletListLeft.add(shot);
        }

        public void addBulletRight(Bullet shot) {
            bulletListRight.add(shot);
        }

        //When triggered bullet will be remove from the arraylist
        public void removeBulletLeft(Bullet shot) {
            bulletListLeft.remove(shot);
        }

        public void removeBulletRight(Bullet shot) {
            bulletListRight.remove(shot);
        }
    }
}
