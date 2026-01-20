package com.websocket;

public class JsonParser {
    public static String extraer(String json, String campo) {
        try {
            String llave = "\"" + campo + "\"";
            int inicioLlave = json.indexOf(llave);
            if (inicioLlave == -1) return "";
            int inicioValor = json.indexOf(":", inicioLlave) + 1;
            int finValor;
            if (json.trim().substring(json.indexOf(":", inicioLlave)).trim().startsWith("\"")) {
                inicioValor = json.indexOf("\"", inicioValor) + 1;
                finValor = json.indexOf("\"", inicioValor);
            } else {
                int finComa = json.indexOf(",", inicioValor);
                int finLlave = json.indexOf("}", inicioValor);
                finValor = (finComa != -1 && finComa < finLlave) ? finComa : finLlave;
            }
            return json.substring(inicioValor, finValor).trim().replace("\"", "");
        } catch (Exception e) { return ""; }
    }
}