package ch;

    public class Vertex implements Comparable<Vertex> {
        private int vertexId;
        private double distance;

        public Vertex(int vertexId, double distance) {
            this.vertexId = vertexId;
            this.distance = distance;
        }

        @Override
        public int compareTo(Vertex other) {
            return Double.compare(this.distance, other.distance);
        }

        public int getVertexId() {
            return vertexId;
        }

        public double getDistance() {
            return distance;
        }
    }