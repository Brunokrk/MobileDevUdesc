public class Main {
    public static void main(String[] args) {
        Buffer buffer = new Buffer(5);
        new Thread(new Producer(buffer)).start();
        new Thread(new Consumer(buffer)).start();
    }
}