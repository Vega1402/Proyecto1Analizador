/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectos.proyecto1analizador;

/**
 *
 * @author Windows 10
 */

public class Token {
    private String lexema;
    private String expresionRegular;
    private String traduccion;
    private String lenguaje;
    private String tipo;
    private int fila;
    private int columna;

    public Token(String lexema, String expresionRegular, String traduccion, String lenguaje, String tipo, int fila, int columna) {
        this.lexema = lexema;
        this.expresionRegular = expresionRegular;
        this.traduccion = traduccion;
        this.lenguaje = lenguaje;
        this.tipo = tipo;
        this.fila = fila;
        this.columna = columna;
    }

    public String getLexema() {
        return lexema;
    }

    public String getExpresionRegular() {
        return expresionRegular;
    }
    
    public String getTraduccion(){
        return traduccion;
    }

    public String getLenguaje() {
        return lenguaje;
    }

    public String getTipo() {
        return tipo;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}

