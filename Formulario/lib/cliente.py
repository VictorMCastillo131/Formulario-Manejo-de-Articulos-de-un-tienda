import java.io.*;
import java.net.*;
import java.util.Scanner;

public class cliente {
    public static void main(String[] args) {
        String serverAddress = "192.168.43.145"; // IP del servidor
        int port = 5000;

        try (Socket socket = new Socket(serverAddress, port);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);

            // Enviar archivo al servidor
            System.out.println("Introduce el nombre del archivo que deseas enviar al servidor (por ejemplo, archivo.txt):");
            String clientFilePath = scanner.nextLine();
            File clientFile = new File(clientFilePath);

            if (!clientFile.exists()) {
                System.err.println("El archivo no existe.");
                return;
            }

            // Enviar nombre del archivo y tamaño
            dos.writeUTF(clientFile.getName());
            dos.writeLong(clientFile.length());

            // Enviar contenido del archivo
            try (FileInputStream fis = new FileInputStream(clientFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) > 0) {
                    dos.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("Archivo enviado al servidor: " + clientFile.getName());

            // Recibir archivo del servidor
            System.out.println("Esperando recibir archivo del servidor...");
            String serverFileName = dis.readUTF();
            long serverFileSize = dis.readLong();

            if (serverFileSize > 0) {
                System.out.println("Recibiendo archivo: " + serverFileName);

                // Crear directorio si no existe
                String directoryPath = "C:\\Users\\casti\\OneDrive\\Documentos\\ClienteArchivos";
                File directory = new File(directoryPath);
                if (!directory.exists()) {
                    directory.mkdirs(); // Crear la carpeta si no existe
                }

                // Concatenar ruta y nombre del archivo recibido
                String outputPath = directoryPath + File.separator + serverFileName;
                try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalRead = 0;

                    while ((bytesRead = dis.read(buffer)) > 0) {
                        fos.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;
                        if (totalRead >= serverFileSize) break;
                    }
                }
                System.out.println("Archivo recibido del servidor y guardado en: " + outputPath);
            } else {
                System.out.println("El servidor no envió ningún archivo.");
            }

        } catch (IOException e) {
            System.err.println("Error en el cliente: " + e.getMessage());
        }
    }
}