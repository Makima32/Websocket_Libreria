package com.websocket;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        DbConector conector = new DbConector("jdbc:mysql://localhost:3306/library_db", "root", "");
        BookDAO dao = new BookDAO(conector);

        try {
            System.out.println("Verificando la conexion a la base de datos...");
            conector.testConnection();
            System.out.println("Base de datos conectada correctamente.");

            try (ServerSocket servidor = new ServerSocket(8080)) {
                System.out.println("Servidor iniciado en el puerto " + servidor.getLocalPort());

                while (true) {
                    try (Socket cliente = servidor.accept()) {
                        InputStreamReader isr = new InputStreamReader(cliente.getInputStream(), StandardCharsets.UTF_8);
                        BufferedReader entrada = new BufferedReader(isr);

                        String linea = entrada.readLine();
                        if (linea == null || linea.isEmpty()) {
                            continue;
                        }

                        String[] partes = linea.split(" ");
                        String metodo = partes[0];
                        String ruta = partes[1];

                        int longitud = 0;
                        String cabecera;
                        while (!(cabecera = entrada.readLine()).isEmpty()) {
                            if (cabecera.toLowerCase().startsWith("content-length:")) {
                                longitud = Integer.parseInt(cabecera.split(":")[1].trim());
                            }
                        }

                        String body = "";
                        if (longitud > 0) {
                            char[] bodyChars = new char[longitud];
                            int charsRead = 0;
                            while (charsRead < longitud) {
                                int result = entrada.read(bodyChars, charsRead, longitud - charsRead);
                                if (result == -1) {
                                    break;
                                }
                                charsRead += result;
                            }
                            body = new String(bodyChars);
                        }

                        String respuestaJson = "";
                        String httpStatus = "404 Not Found";

                        try {
                            if (ruta.equals("/books")) {
                                switch (metodo) {
                                    case "GET":
                                        List<Book> lista = dao.getAll();
                                        StringBuilder sb = new StringBuilder("[");
                                        for (int i = 0; i < lista.size(); i++) {
                                            sb.append(lista.get(i).toJson());
                                            if (i < lista.size() - 1)
                                                sb.append(",");
                                        }
                                        respuestaJson = sb.append("]").toString();
                                        httpStatus = "200 OK";
                                        break;
                                    case "POST":
                                        String titulo = JsonParser.extraer(body, "titulo");
                                        String autor = JsonParser.extraer(body, "autor");
                                        int annio = parseAnio(JsonParser.extraer(body, "anio"));
                                        Book newBook = new Book(0, titulo, autor, annio);
                                        dao.insert(newBook);
                                        respuestaJson = "{\"status\":\"creado\"}";
                                        httpStatus = "201 Created";
                                        break;
                                    default:
                                        httpStatus = "405 Method Not Allowed";
                                        respuestaJson = "{\"error\":\"Metodo no permitido\"}";
                                        break;
                                }
                            } else if (ruta.startsWith("/books/") && ruta.length() > 7) {
                                int id = Integer.parseInt(ruta.substring(7));
                                switch (metodo) {
                                    case "GET":
                                        Book b = dao.getById(id);
                                        if (b != null) {
                                            respuestaJson = b.toJson();
                                            httpStatus = "200 OK";
                                        } else {
                                            respuestaJson = "{\"error\":\"Libro no encontrado\"}";
                                            httpStatus = "404 Not Found";
                                        }
                                        break;
                                    case "PUT":
                                        if (dao.getById(id) != null) {
                                            String titulo = JsonParser.extraer(body, "titulo");
                                            String autor = JsonParser.extraer(body, "autor");
                                            int annio = parseAnio(JsonParser.extraer(body, "anio"));
                                            dao.update(new Book(id, titulo, autor, annio));
                                            respuestaJson = "{\"status\":\"actualizado\"}";
                                            httpStatus = "200 OK";
                                        } else {
                                            respuestaJson = "{\"error\":\"Libro no encontrado para actualizar\"}";
                                            httpStatus = "404 Not Found";
                                        }
                                        break;
                                    case "DELETE":
                                        if (dao.getById(id) != null) {
                                            dao.delete(id);
                                            respuestaJson = "{\"status\":\"borrado\"}";
                                            httpStatus = "200 OK";
                                        } else {
                                            respuestaJson = "{\"error\":\"Libro no encontrado para borrar\"}";
                                            httpStatus = "404 Not Found";
                                        }
                                        break;
                                    default:
                                        httpStatus = "405 Method Not Allowed";
                                        respuestaJson = "{\"error\":\"Metodo no permitido\"}";
                                        break;
                                }
                            }
                        } catch (Exception e) {
                            httpStatus = "500 Internal Server Error";
                            respuestaJson = "{\"error\":\"SQL Error: " + e.getMessage() + "}";
                        }

                        try {
                            sendResponse(cliente, httpStatus, respuestaJson);
                        } catch (IOException e) {
                            System.err.println("Error al enviar la respuesta: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("---ERROR NO SE PUDO INICIAR EL SERVIDOR---");
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void sendResponse(Socket cliente, String status, String jsonBody) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(cliente.getOutputStream(), StandardCharsets.UTF_8);
        PrintWriter salida = new PrintWriter(osw, true);

        salida.print("HTTP/1.1 " + status + "\r\n");
        salida.print("Content-Type: application/json; charset=UTF-8\r\n");
        salida.print("Content-Length: " + jsonBody.getBytes(StandardCharsets.UTF_8).length + "\r\n");
        salida.print("Connection: close\r\n");
        salida.print("\r\n");
        salida.print(jsonBody);
        salida.flush();
    }

    private static int parseAnio(String s) {
        try {
            return (s == null || s.isEmpty()) ? 0 : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
