import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String args[]) throws IOException {
        var cliente = new Socket("127.0.0.1", 12345);
        var teclado = new Scanner(System.in);
        var saida = new PrintStream(cliente.getOutputStream());
        
        while (teclado.hasNextLine()) {
            saida.println(teclado.nextLine());
        }

        saida.close();
        teclado.close();
        cliente.close();
    }
}

//If server OFF -> Conn refused