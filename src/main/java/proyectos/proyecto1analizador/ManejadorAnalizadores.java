/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectos.proyecto1analizador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Windows 10
 */
import java.util.ArrayList;
import java.util.List;
public class ManejadorAnalizadores {
    private String codigoFuente;
    private int pos;
    private List<Token> tokens;

    private AnalizadorCSS analizadorCSS;
    private AnalizadorHTML analizadorHTML;
    private AnalizadorJS analizadorJS;

    public ManejadorAnalizadores(String codigoFuente) {
        this.codigoFuente = codigoFuente;
        this.pos = 0;
        this.tokens = new ArrayList<>();
    }

    public List<Token> analizar() {
        while (pos < codigoFuente.length()) {
            if (codigoFuente.startsWith(">>[js]", pos)) {
                pos += ">>[js]".length();
                procesarJS();
            } else if (codigoFuente.startsWith(">>[html]", pos)) {
                pos += ">>[html]".length();
                procesarHTML();
            } else if (codigoFuente.startsWith(">>[css]", pos)) {
                pos += ">>[css]".length();
                procesarCSS();
            } else {
                avanzar();
            }
        }
        return tokens;
    }

    private void procesarJS() {
        StringBuilder sb = new StringBuilder();
        while (pos < codigoFuente.length() && !codigoFuente.startsWith(">>[", pos)) {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }
        analizadorJS = new AnalizadorJS(sb.toString());
        tokens.addAll(analizadorJS.analizar());
    }

    private void procesarHTML() {
        StringBuilder sb = new StringBuilder();
        while (pos < codigoFuente.length() && !codigoFuente.startsWith(">>[", pos)) {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }
        analizadorHTML = new AnalizadorHTML(sb.toString());
        tokens.addAll(analizadorHTML.analizar());
    }

    private void procesarCSS() {
        StringBuilder sb = new StringBuilder();
        while (pos < codigoFuente.length() && !codigoFuente.startsWith(">>[", pos)) {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }
        analizadorCSS = new AnalizadorCSS(sb.toString());
        tokens.addAll(analizadorCSS.analizar());
    }

    private void avanzar() {
        pos++;
    }

    public void imprimirTokens() {
        System.out.printf("%-10s %-20s %-10s %-30s %-5s %-5s%n", "Token", "Expresión Regular", "Lenguaje", "Tipo", "Fila", "Columna");
        for (Token token : tokens) {
            System.out.printf("%-10s %-20s %-10s %-30s %-5d %-5d%n",
                    token.getLexema(),
                    token.getExpresionRegular(),
                    token.getLenguaje(),
                    token.getTipo(),
                    token.getFila(),
                    token.getColumna());
        }
    }

public void generarArchivoHTML(String nombreArchivo) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
        writer.write("<!DOCTYPE html>\n<html>\n<head>\n<title>Tokens de HTML</title>\n");
        writer.write("<style>\ntable {border-collapse: collapse; width: 100%;} th, td {border: 1px solid black; padding: 8px; text-align: left;} th {background-color: #f2f2f2;}\n</style>\n</head>\n<body>\n");
        writer.write("<h1>Tokens de HTML</h1>\n<table>\n<tr>\n<th>Lexema</th><th>Expresion Regular</th><th>Traducción</th><th>Tipo</th><th>Lenguaje</th><th>Fila</th><th>Columna</th></tr>\n");

        // Tabla para todos los tokens
        for (Token token : tokens) {
            writer.write("<tr>\n<td>" + token.getLexema() + "</td>\n<td>" + token.getExpresionRegular() + "</td>\n<td>" + token.getTraduccion() + "</td>\n<td>" + token.getTipo() + "</td>\n<td>" + token.getLenguaje() + "</td>\n<td>" + token.getFila() + "</td>\n<td>" + token.getColumna() + "</td>\n</tr>\n");
        }
        
        writer.write("</table>\n");

        // Nueva tabla solo para tokens de tipo "error" o "comentario"
        writer.write("<h2>Tokens de Error y Comentario</h2>\n<table>\n<tr>\n<th>Lexema</th><th>Expresion Regular</th><th>Tipo</th><th>Lenguaje</th><th>Fila</th><th>Columna</th></tr>\n");

        for (Token token : tokens) {
            if (token.getTipo().equalsIgnoreCase("error") || token.getTipo().equalsIgnoreCase("comentario")) {
                writer.write("<tr>\n<td>" + token.getLexema() + "</td>\n<td>" + token.getExpresionRegular() + "</td>\n<td>" + token.getTipo() + "</td>\n<td>" + token.getLenguaje() + "</td>\n<td>" + token.getFila() + "</td>\n<td>" + token.getColumna() + "</td>\n</tr>\n");
            }
        }

        writer.write("</table>\n</body>\n</html>");
        System.out.println("Archivo HTML generado exitosamente: " + nombreArchivo);
    } catch (IOException e) {
        System.err.println("Error al generar el archivo HTML: " + e.getMessage());
    }
}

    
    
public void generarPaginaHTML(String nombreArchivo) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n<meta charset=\"UTF-8\">\n<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n<title>Document</title>\n");
  
        // Procesar y agregar el código CSS
htmlBuilder.append("<style>\n/* Aquí todo el lenguaje CSS */\n");
int columnaActualCSS = 0;

for (int i = 0; i < tokens.size(); i++) {
    Token token = tokens.get(i);

    if (token.getLenguaje().equalsIgnoreCase("CSS") && !token.getTipo().equals("comentario")) {
        if (token.getColumna() > columnaActualCSS) {
            htmlBuilder.append(" ".repeat(token.getColumna() - columnaActualCSS));
        }

        // Obtener el lexema actual
        String lexema = token.getLexema();

        // Añadir el lexema al HTML
        htmlBuilder.append(lexema);

        // Si el lexema es `]` y el siguiente token es `,`, agregar un salto después de ambos
        if (lexema.equals("]") && (i + 1 < tokens.size()) && tokens.get(i + 1).getLexema().equals(",")) {
            htmlBuilder.append(","); // Agregar la coma
            i++; // Saltar el siguiente token que es la coma
            htmlBuilder.append("\n"); // Agregar el salto de línea después de `],`
            columnaActualCSS = 0; // Reiniciar columna actual después de salto de línea
        }

        // Manejo de saltos de línea después de `;`
        if (lexema.equals(";")) {
            htmlBuilder.append("\n");
            columnaActualCSS = 0; // Reiniciar columna actual después de salto de línea
        } // Manejo de saltos de línea después de `{`
        else {
            columnaActualCSS = token.getColumna() + lexema.length();
        }
    }
}

htmlBuilder.append("</style>\n");


        // Procesar y agregar el código JavaScript directamente del texto fuente
        htmlBuilder.append("<script>\n/* Aquí todo el lenguaje JavaScript */\n");
        
        int posJS = codigoFuente.indexOf(">>[js]") + ">>[js]".length(); // Buscar la posición del JS en el código fuente
        int endJS = codigoFuente.indexOf(">>[", posJS); // Buscar la siguiente sección para delimitar el final del bloque JS

        if (endJS == -1) {
            endJS = codigoFuente.length(); // Si no hay más secciones, leer hasta el final
        }

        String codigoJSOriginal = codigoFuente.substring(posJS, endJS); // Extraer el bloque de código JS original
        htmlBuilder.append(codigoJSOriginal); // Insertar el código JS sin procesar
        
        htmlBuilder.append("</script>\n");

        
        // Procesar y agregar el código HTML
        htmlBuilder.append("</head>\n<body>\n<!-- Aquí todo el lenguaje HTML -->\n");
        int columnaActualHTML = 0;
        for (Token token : tokens) {
            if (token.getLenguaje().equalsIgnoreCase("HTML") && !token.getTipo().equals("comentario")) {
                if (token.getColumna() > columnaActualHTML) {
                    htmlBuilder.append(" ".repeat(token.getColumna() - columnaActualHTML));
                }

                // Insertar la traducción del token (código HTML)
                htmlBuilder.append(token.getTraduccion());

                // Añadir un salto de línea después de cada etiqueta HTML
                if (token.getTraduccion().trim().endsWith(">")) {
                    htmlBuilder.append("\n"); // Agregar un salto de línea
                }

                columnaActualHTML = token.getColumna() + token.getLexema().length(); // Actualizar columna actual
            }
        }

        htmlBuilder.append("</body>\n</html>");

        // Corregir etiquetas auto-cerradas si es necesario
        String htmlCorregido = corregirEtiquetasAutoCerradas(htmlBuilder.toString());
        
        
         // Eliminar los espacios en blanco dentro de las etiquetas
        String htmlOptimizado = eliminarEspaciosInnecesariosDentroDeEtiquetas(htmlCorregido);

        writer.write(htmlOptimizado);
        System.out.println("Archivo HTML generado exitosamente: " + nombreArchivo);
    } catch (IOException e) {
        System.err.println("Error al generar el archivo HTML: " + e.getMessage());
    }
}



    private String corregirEtiquetasAutoCerradas(String html) {
        String[] etiquetas = {"h1", "h2", "h3", "h4", "h5", "h6", "p", "div", "span", "a", "li", "ul", "ol", "footer", "button",
        "header", "nav", "aside", "section" , "article", "input", "form", "label", "textarea"};
        for (String etiqueta : etiquetas) {
            html = html.replaceAll("<\\s*" + etiqueta + "\\s*/\\s*>", "</" + etiqueta + ">");
        }
        return html;
    }
    
    
    private String eliminarEspaciosInnecesariosDentroDeEtiquetas(String html) {
        // Elimina solo los espacios posteriores al signo igual y añade un espacio entre los atributos si falta
        return html.replaceAll("=\\s+", "="); // Elimina los espacios después del signo igual

    }
}


