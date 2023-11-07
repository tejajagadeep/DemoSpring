// Step 1: Create an abstract Document class
abstract class Document {
    abstract void open();
    abstract void save();
    abstract void close();
}

// Step 2: Create concrete classes for specific document types
class Report extends Document {
    @Override
    void open() {
        System.out.println("Opening a report");
    }
    
    @Override
    void save() {
        System.out.println("Saving a report");
    }
    
    @Override
    void close() {
        System.out.println("Closing a report");
    }
}

class Spreadsheet extends Document {
    @Override
    void open() {
        System.out.println("Opening a spreadsheet");
    }
    
    @Override
    void save() {
        System.out.println("Saving a spreadsheet");
    }
    
    @Override
    void close() {
        System.out.println("Closing a spreadsheet");
    }
}

class Presentation extends Document {
    @Override
    void open() {
        System.out.println("Opening a presentation");
    }
    
    @Override
    void save() {
        System.out.println("Saving a presentation");
    }
    
    @Override
    void close() {
        System.out.println("Closing a presentation");
    }
}

// Step 3: Create a DocumentFactory interface
interface DocumentFactory {
    Document createDocument();
}

// Step 4: Implement the DocumentFactory interface with a concrete class
class ConcreteDocumentFactory implements DocumentFactory {
    enum DocumentType { REPORT, SPREADSHEET, PRESENTATION }

    @Override
    public Document createDocument(DocumentType type) {
        switch (type) {
            case REPORT:
                return new Report();
            case SPREADSHEET:
                return new Spreadsheet();
            case PRESENTATION:
                return new Presentation();
            default:
                throw new IllegalArgumentException("Invalid document type");
        }
    }
}

public class Client {
    public static void main(String[] args) {
        // Step 5: Demonstrate the use of the Factory Method pattern
        ConcreteDocumentFactory factory = new ConcreteDocumentFactory();
        
        Document report = factory.createDocument(ConcreteDocumentFactory.DocumentType.REPORT);
        report.open();
        report.save();
        report.close();
        
        Document spreadsheet = factory.createDocument(ConcreteDocumentFactory.DocumentType.SPREADSHEET);
        spreadsheet.open();
        spreadsheet.save();
        spreadsheet.close();
        
        Document presentation = factory.createDocument(ConcreteDocumentFactory.DocumentType.PRESENTATION);
        presentation.open();
        presentation.save();
        presentation.close();
    }
}


// Step 1: Define the Component Interface
interface ProductCategory {
    void display();
}

// Step 2: Implement Leaf Classes
class Product implements ProductCategory {
    private String name;
    private double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public void display() {
        System.out.println("Product: " + name + " - Price: $" + price);
    }
}

// Step 3: Implement Composite Class
class ProductCatalog implements ProductCategory {
    private String name;
    private List<ProductCategory> children = new ArrayList<>();

    public ProductCatalog(String name) {
        this.name = name;
    }

    public void addProductCategory(ProductCategory category) {
        children.add(category);
    }

    @Override
    public void display() {
        System.out.println("Category: " + name);
        for (ProductCategory category : children) {
            category.display();
        }
    }
}

// Step 4: Build the Product Hierarchy
public class ECommercePlatform {
    public static void main(String[] args) {
        ProductCatalog electronics = new ProductCatalog("Electronics");
        Product tv = new Product("Smart TV", 799.99);
        Product smartphone = new Product("iPhone 13", 999.99);
        electronics.addProductCategory(tv);
        electronics.addProductCategory(smartphone);

        ProductCatalog clothing = new ProductCatalog("Clothing");
        Product tShirt = new Product("Cotton T-Shirt", 19.99);
        Product jeans = new Product("Blue Jeans", 39.99);
        clothing.addProductCategory(tShirt);
        clothing.addProductCategory(jeans);

        ProductCatalog rootCatalog = new ProductCatalog("Root Catalog");
        rootCatalog.addProductCategory(electronics);
        rootCatalog.addProductCategory(clothing);

        // Step 5: Display Information
        rootCatalog.display();
    }
}
