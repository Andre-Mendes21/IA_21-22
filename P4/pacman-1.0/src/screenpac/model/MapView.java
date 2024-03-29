package screenpac.model;

import utilities.JEasyFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import screenpac.extract.Constants;
import screenpac.util.ImageLoader;

public class MapView extends JComponent implements Constants {

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        GameState gs = new GameState();
        gs.nextLevel();
        gs.nextLevel();
        gs.nextLevel();
        new JEasyFrame(new MapView(gs), "Map Test", true);
    }

    GameStateInterface gs;
    MazeInterface maze;
    BufferedImage[] images;

    public MapView(GameStateInterface gs) {
        this.gs = gs;
        images = ImageLoader.loadImages();
    }

    public void paintComponent(Graphics g) {
        // for now paint just the map
        maze = gs.getMaze();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, maze.getWidth() * MAG, maze.getHeight() * MAG + 20);
        int mazeNumber = gs.getMaze().getNumber();
        // System.out.println("Game level: " + gs.getLevel());
        // System.out.println("Maze number = " + mazeNumber);
        BufferedImage bg = images[mazeNumber];
        if (bg != null) {
            g.drawImage(bg, 2, 6, null);
        }
        g.setColor(Color.cyan);
        // for (Node n : maze.getMap()) {
        //    g.fillRect(n.x * MAG - 9, n.y * MAG - 9, 18, 18);
        // }
        g.setColor(Color.black);
        // for (Node n : maze.getMap()) {
        //    g.fillRect(n.x * MAG - 8, n.y * MAG - 8, 16, 16);
        // }

 //        only use this to show the true graph nodes
        // g.setColor(Color.red);
        // for (Node n : maze.getMap()) {
        //     g.fillRect(n.x * MAG - 0, n.y * MAG - 1, 1, 1);
        // }

     // these are part of the game state rather than the map
     // so need to consider which pills are still there
        g.setColor(Color.blue);
        for (Node n : maze.getPills()) {
            // g.fillOval(n.x*mag-1, n.y*mag-1, 3, 3);
        }
        g.setColor(Color.white);
        for (Node n : maze.getPowers()) {
            // g.fillOval(n.x * mag - 3, n.y * mag - 3, 6, 6);
        }
        // now need to check out the power pills etc
        for (Node n : maze.getMap()) {
            if (n.col != Color.black) {
                g.setColor(n.col);
                g.fillRect(n.x * MAG - 8, n.y * MAG - 8, 16, 16);
                // reset the color ready for next time
                n.col = Color.black;
            }
        }
    }

    public Dimension getPreferredSize() {
        maze = gs.getMaze();
        return new Dimension(maze.getWidth() * MAG, maze.getHeight() * MAG);
    }
}
