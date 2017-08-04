package galaxy;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class Visualizer implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    private BufferedImage bufferImage;
    private ArrayList<Planet> planets;
    private LinkedList<Player> players;
    private Director director;
    private JFrame frame;
    private JPanel panel = new JPanel() {
        @Override
        public void paint(Graphics g) {
            g.drawImage(bufferImage, 0, 0, this);
        }
    };

    protected MouseOverInfo mouseOverInfo;

    protected interface MouseOverInfo {
        void draw(Graphics g);
    }

    protected Visualizer(int winWidth, int winHeight, int dimensions) {
        if (dimensions != GameSettings.DIMENSIONS.dimensions()) {
            throw new IllegalArgumentException("Visualizer does not match galaxy dimension space.");
        }

        Visualizer listener = this;
        frame = new JFrame() {
            {
                setName("Galcon AI Challenge");
                setSize(winWidth, winHeight);
                setContentPane(panel);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setResizable(true);
                setVisible(true);
                addKeyListener(listener);
                addMouseListener(listener);
                addMouseMotionListener(listener);
                addMouseWheelListener(listener);
            }
        };
    }

    protected Visualizer setDirector(Director director) {
        this.director = director;
        return this;
    }

    protected final int getWidth() {
        return panel.getWidth();
    }

    protected final int getHeight() {
        return panel.getHeight();
    }

    final void nextGame(LinkedList<Player> active, ArrayList<Planet> newMap) {
        planets = newMap;
        players = active;
        newGame();
    }

    protected abstract void newGame();

    void update(ArrayList<Fleet> fleets) {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        draw(planets, fleets, image.getGraphics());
        bufferImage = image;
        repaint();
    }

    protected void draw(ArrayList<Planet> planets, ArrayList<Fleet> fleets, Graphics g) {
        drawBackground(g);
        drawPlanets(planets, g);
        drawFleets(fleets, g);
        drawOther(g);
        drawPlayerInfo(players, g);
        if (mouseOverInfo != null) {
            mouseOverInfo.draw(g);
        }
    }

    protected void repaint() {
        frame.repaint();
    }

    protected void requestFocus() {
        panel.requestFocus(true);
    }

    protected boolean checkRecentlyConquered(Planet p) {
        return p.checkRecentlyConquered();
    }

    protected int numUnitsOwnedBy(Player p) {
        return director.numUnitsOwnedBy(p);
    }

    protected int numUnitsInPlanets(Player p) {
        return director.numUnitsInPlanets(p);
    }

    protected int numUnitsInFleets(Player p) {
        return director.numUnitsInFleets(p);
    }

    protected void drawBackground(Graphics g) {
    }

    protected void drawPlanets(ArrayList<Planet> planets, Graphics g) {
    }

    protected void drawFleets(ArrayList<Fleet> fleets, Graphics g) {
    }

    protected void drawPlayerInfo(LinkedList<Player> players, Graphics g) {
    }

    protected void drawOther(Graphics g) {
    }

    protected void keystroke(KeyEvent e) {
    }

    @Override
    public final void keyPressed(KeyEvent e) {
        char command = (char)e.getKeyCode();
        if (command == 'Q') {
            System.exit(0);
        } else if (command == 'S') {
            director.skipGame();
        } else if (command == ' ') {
            director.togglePause();
        } else if (command == 'R') {
            director.restartGame();
        } else if (command == 'T') {
            director.reverseMap();
        }

        keystroke(e);
    }

    protected void mouseOverPress(MouseEvent e, ArrayList<Planet> planets) {
    }

    protected void mousePress(MouseEvent e) {
    }

    @Override
    public final void mousePressed(MouseEvent e) {
        mouseOverPress(e, planets);
        mousePress(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    }
}
