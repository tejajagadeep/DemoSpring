interface Drawable {
    void draw();
}

class Circle implements Drawable {
    @Override
    public void draw() {
        System.out.println("Drawing a circle");
    }
}

class Rectangle implements Drawable {
    @Override
    public void draw() {
        System.out.println("Drawing a rectangle");
    }
}

class Triangle implements Drawable {
    @Override
    public void draw() {
        System.out.println("Drawing a triangle");
    }
}

class CanvasRenderer {
    void renderShape(Drawable shape) {
        shape.draw();
    }
}

public class ShapeDrawingApp {
    public static void main(String[] args) {
        CanvasRenderer canvas = new CanvasRenderer();

        Drawable circle = new Circle();
        Drawable rectangle = new Rectangle();
        Drawable triangle = new Triangle();

        // Render different shapes on the canvas
        canvas.renderShape(circle);
        canvas.renderShape(rectangle);
        canvas.renderShape(triangle);
    }
}
