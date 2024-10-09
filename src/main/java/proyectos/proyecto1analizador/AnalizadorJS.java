/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectos.proyecto1analizador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Windows 10
 */
import java.util.ArrayList;
import java.util.List;

public class AnalizadorJS {
    private String codigoFuente;
    private int pos;
    private int linea;
    private int columna;
    private List<Token> tokens;

    public AnalizadorJS(String codigoFuente) {
        this.codigoFuente = codigoFuente;
        this.pos = 0;
        this.linea = 1;
        this.columna = 1;
        this.tokens = new ArrayList<>();
    }
    
    public int getPosicionActual() {
    return this.pos;
}


    public List<Token> analizar() {
        while (pos < codigoFuente.length()) {
            char actual = codigoFuente.charAt(pos);

            if (Character.isWhitespace(actual)) {
                manejarEspacio();
            } else if (Character.isLetter(actual)) {
                manejarIdentificadorOPalabraReservada();
            } else if (Character.isDigit(actual)) {
                manejarNumero();
            } else if (actual == '/' && pos + 1 < codigoFuente.length() && codigoFuente.charAt(pos + 1) == '/') {
                manejarComentario();
            } else if (esOperadorAritmetico(actual)) {
                manejarOperadorAritmetico(actual);
            } else if (esOperadorRelacional(actual)) {
                manejarOperadorRelacional(actual);
            } else if (esCadena(actual)) {
                tokens.add(manejarCadena(actual)); // Manejo de cadenas
            } else if (esSimbolo(actual)) {
                manejarSimbolo(actual);
            } else {
                avanzar();
            }
        }
        return tokens;
    }

    private void manejarEspacio() {
        while (pos < codigoFuente.length() && Character.isWhitespace(codigoFuente.charAt(pos))) {
            avanzar();
        }
    }

    private void manejarIdentificadorOPalabraReservada() {
        StringBuilder sb = new StringBuilder();
        int inicioColumna = columna;

        while (pos < codigoFuente.length() && (Character.isLetterOrDigit(codigoFuente.charAt(pos)) || codigoFuente.charAt(pos) == '_')) {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        String lexema = sb.toString();
        if (esPalabraReservada(lexema)) {
            tokens.add(crearToken(lexema, "Palabra Reservada", lexema, "JS", "Palabra Reservada", linea, inicioColumna));
        } else if(esBooleano(lexema)){
            manejarBooleano(lexema);
        }else {
            tokens.add(crearToken(lexema, "Identificador", lexema, "JS", "Identificador", linea, inicioColumna));
        }
    }
    
    private void manejarBooleano(String lexema) {
        int inicioColumna = columna - lexema.length(); // Ajustar la posición inicial
        tokens.add(crearToken(lexema, lexema, lexema, "JS", "booleano", linea, inicioColumna));
    }
    
    private boolean esBooleano(String lexema) {
        return "true".equals(lexema) || "false".equals(lexema);
    }



    private void manejarNumero() {
        StringBuilder sb = new StringBuilder();
        int inicioColumna = columna;
        boolean esDecimal = false;

        while (pos < codigoFuente.length() && (Character.isDigit(codigoFuente.charAt(pos)) || codigoFuente.charAt(pos) == '.')) {
            if (codigoFuente.charAt(pos) == '.') {
                if (esDecimal) {
                    break;
                }
                esDecimal = true;
            }
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        String lexema = sb.toString();
        if (esDecimal) {
            tokens.add(crearToken(lexema, "Decimal", lexema, "JS", "Número Decimal", linea, inicioColumna));
        } else {
            tokens.add(crearToken(lexema, "Entero", lexema, "JS", "Número Entero", linea, inicioColumna));
        }
    }

    private void manejarComentario() {
        StringBuilder sb = new StringBuilder();
        int inicioColumna = columna;
        avanzar(); // Avanza sobre la primera '/'
        avanzar(); // Avanza sobre la segunda '/'

        while (pos < codigoFuente.length() && codigoFuente.charAt(pos) != '\n') {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        tokens.add(crearToken(sb.toString(), "Comentario", sb.toString(), "JS", "Comentario", linea, inicioColumna));
    }

    private void manejarOperadorAritmetico(char actual) {
        int inicioColumna = columna;
        String lexema = String.valueOf(actual);
        avanzar();

        if (actual == '+' && pos < codigoFuente.length() && codigoFuente.charAt(pos) == '+') {
            avanzar();
            tokens.add(crearToken("++", "Incremento", "++", "JS", "Operador Incremental", linea, inicioColumna));
        } else if (actual == '-' && pos < codigoFuente.length() && codigoFuente.charAt(pos) == '-') {
            avanzar();
            tokens.add(crearToken("--", "Decremento", "--", "JS", "Operador Incremental", linea, inicioColumna));
        } else {
            tokens.add(crearToken(lexema, "Operador Aritmético", lexema, "JS", "Operador Aritmético", linea, inicioColumna));
        }
    }

    private void manejarOperadorRelacional(char actual) {
        int inicioColumna = columna;
        String lexema = String.valueOf(actual);
        avanzar();

        if (actual == '=' && pos < codigoFuente.length() && codigoFuente.charAt(pos) == '=') {
            avanzar();
            lexema += "=";
            tokens.add(crearToken(lexema, "Igualdad", lexema, "JS", "Operador Relacional", linea, inicioColumna));
        } else if (actual == '!' && pos < codigoFuente.length() && codigoFuente.charAt(pos) == '=') {
            avanzar();
            lexema = "!=";
            tokens.add(crearToken(lexema, "Diferente", lexema, "JS", "Operador Relacional", linea, inicioColumna));
        } else {
            tokens.add(crearToken(lexema, "Operador Relacional", lexema, "JS", "Operador Relacional", linea, inicioColumna));
        }
    }
    
    private Token manejarCadena(char delimitador) {
    StringBuilder sb = new StringBuilder();
    int inicioColumna = columna;
    avanzar(); // Avanza sobre el delimitador inicial

    // Agregar el delimitador de apertura a la cadena
    sb.append(delimitador);
    
    while (pos < codigoFuente.length()) {
        char actual = codigoFuente.charAt(pos);
        
        // Si encuentra el delimitador de cierre, rompe el bucle
        if (actual == delimitador) {
            break;
        }

        // Manejar los caracteres escapados, como \", \', etc.
        if (actual == '\\' && (pos + 1) < codigoFuente.length()) {
            // Avanza sobre el carácter escapado
            sb.append(codigoFuente.charAt(pos + 1));
            avanzar(); // Avanzar dos veces para saltar el caracter escapado
        } else {
            // Agrega el carácter actual a la cadena
            sb.append(actual);
        }
        
        avanzar();
    }
        // Agregar el delimitador de apertura a la cadena
    sb.append(delimitador);
    avanzar(); // Avanza sobre el delimitador de cierre
    return crearToken(sb.toString(), "Cadena", sb.toString(), "JS", "Cadena", linea, inicioColumna);
}
    
    private boolean esCadena(char actual) {
    return actual == '\"' || actual == '\'' || actual == '“' || actual == '”' || actual == '`';
}

    
    private void manejarSimbolo(char actual) {
        int inicioColumna = columna;
        String lexema = String.valueOf(actual);
        String tipoSimbolo = obtenerNombreSimbolo(actual); // Obtener el nombre del símbolo
        avanzar();
        tokens.add(crearToken(lexema, lexema, lexema, "JS", tipoSimbolo, linea, inicioColumna));
    }

    private boolean esOperadorAritmetico(char actual) {
        return actual == '+' || actual == '-' || actual == '*' || actual == '/';
    }

    private boolean esOperadorRelacional(char actual) {
        return actual == '>' || actual == '<' || actual == '=' || actual == '!';
    }

    private boolean esSimbolo(char actual) {
        return actual == '(' || actual == ')' || actual == '{' || actual == '}' ||
               actual == '[' || actual == ']' || actual == '=' || actual == ';' || 
               actual == '.' || actual == ',' || actual == ':';
    }
    
    private String obtenerNombreSimbolo(char actual) {
    switch (actual) {
        case '(':
            return "paréntesis abierto";
        case ')':
            return "paréntesis cerrado";
        case '{':
            return "llave abierta";
        case '}':
            return "llave cerrada";
        case '[':
            return "corchete abierto";
        case ']':
            return "corchete cerrado";
        case '=':
            return "signo igual";
        case '.':
            return "punto";
        case ';':
            return "punto y coma";
            case ',':
            return "coma";
            case ':':
            return "dos puntos";
        default:
            return "símbolo desconocido"; // Por si se añade algún símbolo adicional
    }
}

    private boolean esPalabraReservada(String lexema) {
        String[] palabrasReservadas = {"function", "const", "let", "document", "event", "alert", "for", "while", "if", "else", "return", "console.log", "null"};
        for (String palabra : palabrasReservadas) {
            if (palabra.equals(lexema)) {
                return true;
            }
        }
        return false;
    }

    private void avanzar() {
        if (codigoFuente.charAt(pos) == '\n') {
            linea++;
            columna = 1;
        } else {
            columna++;
        }
        pos++;
    }

    private Token crearToken(String lexema, String expresionRegular, String traduccion, String lenguaje, String tipo, int fila, int columna) {
        return new Token(lexema, expresionRegular, traduccion, lenguaje, tipo, fila, columna);
    }

}

