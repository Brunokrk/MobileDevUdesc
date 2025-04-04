public class Buffer{
    private int head, tail, qt_items;

    private int queue[];

    public Buffer(int size){
        this.queue = new int[size];
        this.head = 0;
        this.tail = 0;
        this.qt_items = 0;
    
    }

    public synchronized void enqueue(int item) throws InterruptedException  {
        // put
        while(isFull()){
            wait();
        }

        this.queue[this.tail] = item; 
        this.tail = (this.tail + 1) % this.queue.length;
        this.qt_items ++;
        notifyAll(); 
    }

    public synchronized int dequeue () throws InterruptedException  {
        // get
        while (isEmpty()){
            wait();
        }

        int item = this.queue[head];

        this.head = (this.head + 1) % this.queue.length;
        this.qt_items --;
        notifyAll();
        return item;
    }

    public boolean isEmpty() {
        return qt_items == 0;
    }

    public boolean isFull() {
        return qt_items == queue.length;
    }

}