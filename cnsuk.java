
import java.util.*;
import java.awt.*;

public class cnsuk {




    public static void drawHex(double x, double y, double size, Color color) {
        // Définir la couleur du trait
        StdDraw.setPenColor(color);

        // Calculer les coordonnées x et y de chaque point de l'hexagone
        double x1 = x + size * Math.cos(Math.toRadians(30));
        double y1 = y + size * Math.sin(Math.toRadians(30));
        double x2 = x + size * Math.cos(Math.toRadians(90));
        double y2 = y + size * Math.sin(Math.toRadians(90));
        double x3 = x + size * Math.cos(Math.toRadians(150));
        double y3 = y + size * Math.sin(Math.toRadians(150));
        double x4 = x + size * Math.cos(Math.toRadians(210));
        double y4 = y + size * Math.sin(Math.toRadians(210));
        double x5 = x + size * Math.cos(Math.toRadians(270));
        double y5 = y + size * Math.sin(Math.toRadians(270));
        double x6 = x + size * Math.cos(Math.toRadians(330));
        double y6 = y + size * Math.sin(Math.toRadians(330));

        // Dessiner l'hexagone en utilisant les coordonnées calculées
        double[] xPoints = {x1, x2, x3, x4, x5, x6};
        double[] yPoints = {y1, y2, y3, y4, y5, y6};
        StdDraw.polygon(xPoints, yPoints);
    }

    public static void main(String[] args) {
        // Définir la taille de la fenêtre de dessin
        StdDraw.setCanvasSize(800, 600);

        // Définir les limites de l'espace de dessin
        StdDraw.setXscale(-11, 11);
        StdDraw.setYscale(-11, 11);

        // Dessiner les hexagones du plateau de jeu
        drawHex(0, 0, 1, Color.BLACK);
        drawHex(0.8, 1.5, 1, Color.BLACK);
        drawHex(0, 4, 1, Color.BLACK);
        drawHex(0, 6, 1, Color.BLACK);
        drawHex(0, 8, 1, Color.BLACK);

    }

}
