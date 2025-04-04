
public class Consumer implements Runnable {
    Buffer buffer;
    
    public Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    public void run() {
        try {
            while (true) {
                int value = this.buffer.dequeue();
                System.out.println("Consumido: " + value);
                Thread.sleep((int)(Math.random() * 1000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}