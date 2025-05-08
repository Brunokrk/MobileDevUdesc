import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {
    private DataInputStream in;
    private DataOutputStream out;
    private String username;

    public ChatClient(String host, int port, String username) {
        this.username = username;
        try {
            Socket socket = new Socket(host, port);
            in  = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // envia nome de usuário
            out.writeUTF(username);
            out.flush();

            // thread que escuta todo retorno do servidor
            new Thread(this::listen).start();

            // loop de leitura de comandos do usuário
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if (line.equals("/users")) {
                    out.writeUTF("/users");
                    out.flush();

                } else if (line.equals("/sair")) {
                    out.writeUTF("/sair");
                    out.flush();
                    socket.close();
                    System.exit(0);

                } else if (line.startsWith("/send message ")) {
                    String[] parts = line.split(" ", 4);
                    if (parts.length < 4) {
                        System.out.println("Uso: /send message <destinatario> <mensagem>");
                        continue;
                    }
                    out.writeUTF("/send message");
                    out.writeUTF(parts[2]);  // destinatário
                    out.writeUTF(parts[3]);  // mensagem
                    out.flush();

                } else if (line.startsWith("/send file ")) {
                    String[] parts = line.split(" ", 4);
                    if (parts.length < 4) {
                        System.out.println("Uso: /send file <destinatario> <caminho do arquivo>");
                        continue;
                    }
                    String dest = parts[2];
                    String path = parts[3];
                    File file = new File(path);
                    if (!file.exists()) {
                        System.out.println("Arquivo não encontrado: " + path);
                        continue;
                    }

                    out.writeUTF("/send file");
                    out.writeUTF(dest);
                    out.writeUTF(file.getName());
                    out.writeLong(file.length());

                    FileInputStream fis = new FileInputStream(file);
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = fis.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    fis.close();
                    out.flush();

                } else {
                    System.out.println("Comando inválido.");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private void listen() {
        try {
            while (true) {
                String type = in.readUTF();
                switch (type) {
                    case "USERS":
                        System.out.println("Usuários online: " + in.readUTF());
                        break;
                    case "MESSAGE":
                        System.out.println(in.readUTF() + ": " + in.readUTF());
                        break;
                    case "FILE":
                        String from    = in.readUTF();
                        String name    = in.readUTF();
                        long   length  = in.readLong();
                        FileOutputStream fos = new FileOutputStream(name);
                        byte[] buf = new byte[4096];
                        long rem = length;
                        while (rem > 0) {
                            int r = in.read(buf, 0, (int)Math.min(buf.length, rem));
                            if (r == -1) break;
                            fos.write(buf, 0, r);
                            rem -= r;
                        }
                        fos.close();
                        System.out.println("Arquivo recebido de " + from + ": " + name);
                        break;
                    case "ERROR":
                        System.out.println("Erro: " + in.readUTF());
                        break;
                    case "EXIT":
                        System.out.println("Servidor encerrou a conexão.");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Mensagem inesperada: " + type);
                }
            }
        } catch (IOException e) {
            System.out.println("Conexão perdida.");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: java ChatClient <host> <port> <username>");
            return;
        }
        new ChatClient(args[0], Integer.parseInt(args[1]), args[2]);
    }
}
