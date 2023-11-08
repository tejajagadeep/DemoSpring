import java.util.ArrayList;
import java.util.List;

interface Product {
    String getName();
    double getPrice();
}

class PhysicalProduct implements Product {
    private String name;
    private double price;

    public PhysicalProduct(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPrice() {
        return price;
    }
}

class DigitalProduct implements Product {
    private String name;
    private double price;

    public DigitalProduct(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPrice() {
        return price;
    }
}

class Order {
    private List<Product> products = new ArrayList<>();
    private double totalAmount;

    public void addProduct(Product product) {
        products.add(product);
        totalAmount += product.getPrice();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void checkout(PaymentProcessor paymentProcessor) {
        if (paymentProcessor.processPayment(totalAmount)) {
            ShippingService shippingService = new ShippingService();
            shippingService.shipProducts(products);
            System.out.println("Order processed successfully.");
        } else {
            System.out.println("Payment failed. Order not processed.");
        }
    }
}

interface PaymentProcessor {
    boolean processPayment(double amount);
}

class MyPaymentProcessor implements PaymentProcessor {
    @Override
    public boolean processPayment(double amount) {
        // Implement PayPal payment processing logic
        System.out.println("Processing payment via PayPal...");
        return true; // Payment is successful in this simplified example
    }
}

class ShippingService {
    public void shipProducts(List<Product> products) {
        for (Product product : products) {
            // Implement shipping logic for each product
            System.out.println("Shipping: " + product.getName());
        }
    }
}

public class ECommerceApp {
    public static void main(String[] args) {
        Product product1 = new PhysicalProduct("Laptop", 1000.0);
        Product product2 = new DigitalProduct("Software Download", 50.0);
        Order order = new Order();

        order.addProduct(product1);
        order.addProduct(product2);

        PaymentProcessor paymentProcessor = new MyPaymentProcessor();
        order.checkout(paymentProcessor);
    }
}
