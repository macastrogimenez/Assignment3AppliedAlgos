package visualiser;

import graph.ContractionHierachy;
import graph.Graph;
import graph.Pair;
import graph.Graph.Edge;
import graph.Graph.Vertex;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Visualiser {
    private static class Scaler {
        float minx = Float.MAX_VALUE;
        float maxx = Float.MIN_VALUE;
        float miny = Float.MAX_VALUE;
        float maxy = Float.MIN_VALUE;

        Scaler(float minx, float miny, float maxx, float maxy) {
            this.minx = minx;
            this.miny = miny;
            this.maxx = maxx;
            this.maxy = maxy;
        }

        Pair<Float, Float> normalise(Pair<Float, Float> point) {
            return new Pair<Float, Float>((point.first - minx) / (maxx - minx), (point.second - miny) / (maxy - miny));
        }

    }

    public static BufferedImage visualise(int width, int height, ContractionHierachy ch, long[] ids) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);

        Graphics2D rasterGraphics = image.createGraphics();
        rasterGraphics.fillRect(0, 0, width, height); // give the whole image a white background
        Graph g = ch.getGraph();
        float minx = Float.MAX_VALUE;
        float maxx = Float.MIN_VALUE;
        float miny = Float.MAX_VALUE;
        float maxy = Float.MIN_VALUE;

        for (long id : ids) {
            Vertex v = g.getVertex(id);
            minx = Math.min(minx, v.x);
            maxx = Math.max(maxx, v.x);
            miny = Math.min(miny, v.y);
            maxy = Math.max(maxy, v.y);
        }

        Scaler scaler = new Scaler(minx, maxy, maxx, miny);

        for (long id : ids) {
            Vertex v = g.getVertex(id);
            Pair<Float, Float> point1 = scaler.normalise(new Pair<Float, Float>(v.x, v.y));
            for (Edge e : g.getNeighbours(id)) {
                Vertex u = g.getVertex(e.to);
                Pair<Float, Float> point2 = scaler.normalise(new Pair<Float, Float>(u.x, u.y));
                if (e.contracted == -1) {
                    rasterGraphics.setColor(Color.BLACK);
                } else {
                    rasterGraphics.setColor(Color.RED);
                }

                int x1 = 10 + (int) (point1.first.floatValue() * (width - 20));
                int y1 = 10 + (int) (point1.second.floatValue() * (height - 20));
                int x2 = 10 + (int) (point2.first.floatValue() * (width - 20));
                int y2 = 10 + (int) (point2.second.floatValue() * (height - 20));

                rasterGraphics.drawLine(x1, y1, x2, y2);
            }
        }

        return image;
    }
}
