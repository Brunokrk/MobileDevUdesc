public class Producer implements Runnable {
    Buffer buffer;
    
    public Producer(Buffer buffer) {
        this.buffer = buffer;
    }

    public void run() {
        int i = 0;
        try {
            while (true) {
                this.buffer.enqueue(i);
                System.out.println("Produzido: " + i);
                i++;
                Thread.sleep((int)(Math.random() * 1000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}