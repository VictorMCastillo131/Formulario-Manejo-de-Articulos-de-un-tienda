import socket
import os

# Obtener la ruta de la carpeta "Documentos" en Windows
def obtener_ruta_documentos():
    documentos = os.path.expanduser("~/Documents")
    if not os.path.exists(documentos):
        os.makedirs(documentos)  # Crear la carpeta si no existe
    return documentos

# Función del servidor
def server(host, port, file_path):
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
            server_socket.bind((host, port))
            server_socket.listen(1)
            print(f"Servidor escuchando en {host}:{port}")
            
            conn, addr = server_socket.accept()
            with conn:
                print(f"Conexión establecida con {addr}")
                
                # Enviar el nombre del archivo
                file_name = os.path.basename(file_path)
                conn.sendall(file_name.encode('utf-8'))
                conn.recv(1024)  # Esperar confirmación
                
                # Enviar el contenido del archivo
                with open(file_path, 'rb') as file:
                    data = file.read()
                    conn.sendall(data)
                    print(f"Archivo '{file_name}' enviado exitosamente a {addr}")
    except Exception as e:
        print(f"Error en el servidor: {e}")

# Función del cliente
def client(host, port):
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client_socket:
            client_socket.connect((host, port))
            print(f"Conectado al servidor en {host}:{port}")
            
            # Recibir el nombre del archivo
            file_name = client_socket.recv(1024).decode('utf-8')
            client_socket.sendall(b"Nombre recibido")  # Confirmación
            
            # Ruta donde se guardará el archivo
            output_dir = obtener_ruta_documentos()
            output_file = os.path.join(output_dir, file_name)
            
            # Guardar el archivo en "Documentos"
            with open(output_file, 'wb') as file:
                while True:
                    data = client_socket.recv(1024)
                    if not data:
                        break
                    file.write(data)
            print(f"Archivo recibido exitosamente y guardado en '{output_file}'")
    except Exception as e:
        print(f"Error en el cliente: {e}")

# Lógica principal
if __name__ == "__main__":
    opcion = input("Elige qué deseas hacer\n1: Enviar archivo\n2: Recibir archivo\n> ")
    if opcion == "1":
        ip = input("Ingresa la IP del servidor (por ejemplo, 127.0.0.1): ").strip()
        ruta = input("Ingresa la ruta del archivo a enviar: ").strip()
        server(ip, 5001, ruta)
    elif opcion == "2":
        ip = input("Ingresa la IP del servidor (por ejemplo, 127.0.0.1): ").strip()
        client(ip, 5001)
    else:
        print("Opción no válida. Por favor, selecciona 1 o 2.")