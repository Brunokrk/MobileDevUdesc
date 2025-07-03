import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private static final String LOG_FILE = "server.log";

    public static void main(String[] args) {
        System.out.println("Servidor de chat iniciado na porta " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private DataInputStream in;
        private DataOutputStream out;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                in  = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                username = in.readUTF().trim();
                if (username.isEmpty() || clients.containsKey(username)) {
                    out.writeUTF("ERROR");
                    out.writeUTF("Nome de usuário inválido ou já em uso.");
                    out.flush();
                    socket.close();
                    return;
                }
                clients.put(username, this);
                logConnection();

                while (true) {
                    String cmd = in.readUTF();
                    switch (cmd) {
                        case "/users":
                            handleUsers();
                            break;
                        case "/send message":
                            handleSendMessage();
                            break;
                        case "/send file":
                            handleSendFile();
                            break;
                        case "/sair":
                            handleExit();
                            return;
                        default:
                            out.writeUTF("ERROR");
                            out.writeUTF("Comando desconhecido.");
                            out.flush();
                    }
                }
            } catch (IOException e) {

            } finally {
                cleanup();
            }
        }

        private void handleUsers() throws IOException {
            out.writeUTF("USERS");
            out.writeUTF(String.join(",", clients.keySet()));
            out.flush();
        }

        private void handleSendMessage() throws IOException {
            String dest = in.readUTF();
            String msg  = in.readUTF();
            ClientHandler target = clients.get(dest);
            if (target != null) {
                target.out.writeUTF("MESSAGE");
                target.out.writeUTF(username);
                target.out.writeUTF(msg);
                target.out.flush();
            } else {
                out.writeUTF("ERROR");
                out.writeUTF("Usuário '" + dest + "' não encontrado.");
                out.flush();
            }
        }

        private void handleSendFile() throws IOException {
            String dest     = in.readUTF();
            String fileName = in.readUTF();
            long   length   = in.readLong();
            ClientHandler target = clients.get(dest);
            byte[] buffer = new byte[4096];

            if (target != null) {

                target.out.writeUTF("FILE");
                target.out.writeUTF(username);
                target.out.writeUTF(fileName);
                target.out.writeLong(length);

                long remaining = length;
                while (remaining > 0) {
                    int read = in.read(buffer, 0, (int)Math.min(buffer.length, remaining));
                    if (read == -1) break;
                    target.out.write(buffer, 0, read);
                    remaining -= read;
                }
                target.out.flush();
            } else {
                // descarta bytes restantes
                long rem = length;
                while (rem > 0) {
                    int read = in.read(buffer, 0, (int)Math.min(buffer.length, rem));
                    if (read == -1) break;
                    rem -= read;
                }
                out.writeUTF("ERROR");
                out.writeUTF("Usuário '" + dest + "' não encontrado.");
                out.flush();
            }
        }

        private void handleExit() throws IOException {
            out.writeUTF("EXIT");
            out.flush();
        }

        private void logConnection() {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                bw.write(username + " - " + socket.getInetAddress().getHostAddress() + " - " + time);
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void cleanup() {
            try {
                if (username != null) {
                    clients.remove(username);
                    System.out.println(username + " desconectado.");
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
