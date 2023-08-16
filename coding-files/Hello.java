import edu.princeton.cs.algs4.StdIn;

public class HelloGoodbye {
    public static void main(String[] args) {
        String a = StdIn.readString();
        String b = StdIn.readString();
        System.out.println("Hello "+a+ " and "+b+".");
        System.out.println("Goodbye "+b+ " and "+a+".");
    }
}
