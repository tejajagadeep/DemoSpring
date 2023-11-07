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
