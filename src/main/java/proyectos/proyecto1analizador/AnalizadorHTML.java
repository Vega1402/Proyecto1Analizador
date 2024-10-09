/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectos.proyecto1analizador;



/**
 *
 * @author Windows 10
 */


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class AnalizadorHTML {
    private String codigoFuente;
    private int pos;
    private int linea;
    private int columna;
    private List<Token> tokens;
    private Set<String> palabrasReservadas;
    
    // Diccionario de traducciones de etiquetas
    private static final Map<String, String> TRADUCCION_ETIQUETAS = new HashMap<>();

    static {
        TRADUCCION_ETIQUETAS.put("principal", "main");
        TRADUCCION_ETIQUETAS.put("encabezado", "header");
        TRADUCCION_ETIQUETAS.put("navegacion", "nav");
        TRADUCCION_ETIQUETAS.put("apartado", "aside");
        TRADUCCION_ETIQUETAS.put("listaordenada", "ul");
        TRADUCCION_ETIQUETAS.put("listadesordenada", "ol");
        TRADUCCION_ETIQUETAS.put("itemlista", "li");
        TRADUCCION_ETIQUETAS.put("anclaje", "a");
        TRADUCCION_ETIQUETAS.put("contenedor", "div");
        TRADUCCION_ETIQUETAS.put("seccion", "section");
        TRADUCCION_ETIQUETAS.put("titulo1", "h1");
        TRADUCCION_ETIQUETAS.put("titulo2", "h2");
        TRADUCCION_ETIQUETAS.put("titulo3", "h3");
        TRADUCCION_ETIQUETAS.put("titulo4", "h4");
        TRADUCCION_ETIQUETAS.put("titulo5", "h5");
        TRADUCCION_ETIQUETAS.put("titulo6", "h6");
        TRADUCCION_ETIQUETAS.put("parrafo", "p");
        TRADUCCION_ETIQUETAS.put("span", "span");
        TRADUCCION_ETIQUETAS.put("entrada", "input");
        TRADUCCION_ETIQUETAS.put("formulario", "form");
        TRADUCCION_ETIQUETAS.put("label", "label");
        TRADUCCION_ETIQUETAS.put("area", "textarea");
        TRADUCCION_ETIQUETAS.put("boton", "button");
        TRADUCCION_ETIQUETAS.put("piepagina", "footer");
    }
    
    
    

    private static final char[] SIGNOS_HTML = {'<', '>', '/', '\"', '\''};
    
    public AnalizadorHTML(String codigoFuente) {
        this.codigoFuente = codigoFuente;
        this.pos = 0;
        this.linea = 1;
        this.columna = 1;
        this.tokens = new ArrayList<>();
         palabrasReservadas = new HashSet<>(Arrays.asList("class", "=", "href","onclick", "id", "style", "type", "placeholder", "required", "name", "value"));
    }

    public int getPosicionActual() {
    return this.pos;
}
    

    
public List<Token> analizar() {

    // Continuar el análisis del código fuente, incluso si hay un error de formato
    while (pos < codigoFuente.length()) {
        char actual = codigoFuente.charAt(pos);

        if (Character.isWhitespace(actual)) {
            manejarEspacio();
        } else if (actual == '<') {
            manejarEtiqueta(); // Maneja una etiqueta de apertura o cierre
        } else if (actual == '\"') {
            tokens.add(manejarCadena()); // Manejo de cadena entre comillas
        } else if (actual == '/' && pos + 1 < codigoFuente.length() && codigoFuente.charAt(pos + 1) == '/') {
            manejarComentario(); // Manejo de comentario
        } else if (esSignoHTML(actual)) {
            tokens.add(manejarSigno()); // Manejo de un signo HTML
        } else {
            manejarTexto(); // Manejo del texto entre etiquetas
        }
    }

    return tokens;
}


    private void manejarTexto() {
        StringBuilder sb = new StringBuilder();
        int inicioColumna = columna;

        // Verificar si el texto está entre etiquetas
        if (pos > 0 && codigoFuente.charAt(pos - 1) == '>' && pos + 1 < codigoFuente.length() && codigoFuente.charAt(pos + 1) == '<') {
            while (pos < codigoFuente.length() && codigoFuente.charAt(pos) != '<') {
                sb.append(codigoFuente.charAt(pos));
                avanzar();
            }

            String texto = sb.toString().trim();
            if (!texto.isEmpty()) {
                tokens.add(crearToken(texto, "Texto", "Texto entre etiquetas", "HTML", "Valido", linea, inicioColumna));
            }
        } else {
            manejarErrorTextoFueraDeEtiqueta();
        }
    }
 
        private void manejarErrorTextoFueraDeEtiqueta() {
        StringBuilder sb = new StringBuilder();
        int inicioColumna = columna;

        while (pos < codigoFuente.length() && codigoFuente.charAt(pos) != '<') {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        String texto = sb.toString().trim();
        if (!texto.isEmpty()) {
            tokens.add(crearToken(texto, "Error", "Texto fuera de etiqueta", "HTML", "Error", linea, inicioColumna));
        }
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

    private void manejarEspacio() {
        while (pos < codigoFuente.length() && Character.isWhitespace(codigoFuente.charAt(pos))) {
            avanzar();
        }
    }

    private void manejarComentario() {
    StringBuilder sb = new StringBuilder();
    int inicioColumna = columna;

    avanzar(); // Avanza sobre la primera '/'
    
    if (pos < codigoFuente.length() && codigoFuente.charAt(pos) == '/') {
        avanzar(); // Avanza sobre la segunda '/'
        
        // Captura todo el contenido del comentario hasta el final de la línea
        while (pos < codigoFuente.length() && codigoFuente.charAt(pos) != '\n') {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        // Avanza sobre el salto de línea
        if (pos < codigoFuente.length() && codigoFuente.charAt(pos) == '\n') {
            avanzar(); // Aquí aseguramos que se avance correctamente
        }

        // Crea un token para el comentario
        String lexema = sb.toString().trim(); // Elimina los espacios innecesarios
        tokens.add(crearToken(lexema, "Comentario", "Comentario", "HTML", "Comentario", linea, inicioColumna));
    }
}


    private boolean esEtiqueta(String lexema) {
        return TRADUCCION_ETIQUETAS.containsKey(lexema);
    }

    private void manejarEtiqueta() {
        avanzar(); // Avanza sobre '<'
        if (codigoFuente.charAt(pos) == '/') {
            manejarEtiquetaCierre(); // Manejo de etiquetas de cierre
        } else {
            manejarEtiquetaApertura(); // Manejo de etiquetas de apertura
        }
    }
    
    
    private void manejarEtiquetaApertura() {
        StringBuilder sb = new StringBuilder();
        int inicioColumna = columna; // Captura la columna inicial para el signo de apertura

        // Token para el signo de apertura
        tokens.add(crearToken("<", "<", "<", "HTML", "Apertura", linea, inicioColumna));
            columna++;
            
        
        // Construir el lexema de la etiqueta
        inicioColumna = columna; // Actualiza para capturar la columna del nombre de la etiqueta
        while (pos < codigoFuente.length() && !Character.isWhitespace(codigoFuente.charAt(pos)) && codigoFuente.charAt(pos) != '/' && codigoFuente.charAt(pos) != '>') {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        String lexema = sb.toString();

        // Después de validar la etiqueta
        if (esEtiqueta(lexema)) {
            String traduccion = TRADUCCION_ETIQUETAS.get(lexema);
            Token etiquetaToken = crearToken(lexema, lexema, traduccion, "HTML", "Etiqueta", linea, inicioColumna);
            tokens.add(etiquetaToken);

            // Manejo de atributos si los hubiera
            manejarPalabrasReservadasDentroEtiqueta();

            // Manejo del cierre especial de la etiqueta
            if (codigoFuente.charAt(pos) == '/') {
                avanzar(); // Avanza sobre '/'
                if (codigoFuente.charAt(pos) == '>') {
                    tokens.add(crearToken(">", ">", ">", "HTML", "Cierre", linea, columna)); // Cierra la etiqueta
                    avanzar(); // Avanza sobre '>'
                }
            } else if (codigoFuente.charAt(pos) == '>') {
                tokens.add(crearToken(">", ">", ">", "HTML", "Cierre", linea, columna)); // Usa la columna actual
                avanzar(); // Avanza sobre '>'
                
                // Manejo del texto dentro de la etiqueta
                manejarTextoDentroEtiqueta(lexema);
            }
        } else {
            // Añadir un token de error si la etiqueta no es válida
            Token errorToken = crearToken(lexema, "Error", "Sin Traducción", "HTML", "Error", linea, inicioColumna);
            tokens.add(errorToken);
        }
    }


    
   private void manejarEtiquetaCierre() {
        StringBuilder sb = new StringBuilder();
        int inicioColumna = columna; // Captura la columna inicial para el signo de cierre
        avanzar(); // Avanza sobre '/'

        // Token para el signo de cierre
        tokens.add(crearToken("/", "/", "/", "HTML", "Cierre", linea, inicioColumna));

        // Construir el lexema de la etiqueta de cierre
        while (pos < codigoFuente.length() && !Character.isWhitespace(codigoFuente.charAt(pos)) && codigoFuente.charAt(pos) != '>') {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        String lexema = sb.toString();

        // Validar la etiqueta de cierre
        if (esEtiqueta(lexema)) {
            tokens.add(crearToken(lexema, lexema, "Cierre", "HTML", "Cierre", linea, inicioColumna));
        } else {
            // Añadir un token de error si la etiqueta no es válida
            Token errorToken = crearToken(lexema, "Error", "Sin Traducción", "HTML", "Error", linea, inicioColumna);
            tokens.add(errorToken);
        }

        // Manejo del cierre de la etiqueta
        if (codigoFuente.charAt(pos) == '>') {
            tokens.add(crearToken(">", ">", ">", "HTML", "Cierre", linea, columna)); // Cierra la etiqueta
            avanzar(); // Avanza sobre '>'
        }
    }


   private void manejarPalabrasReservadasDentroEtiqueta() {
    while (pos < codigoFuente.length() && codigoFuente.charAt(pos) != '>') {
        manejarEspacio();
        StringBuilder sb = new StringBuilder();
        int inicioColumna = columna;

        // Captura el nombre del atributo
        while (pos < codigoFuente.length() && codigoFuente.charAt(pos) != '=' && codigoFuente.charAt(pos) != '>' && !Character.isWhitespace(codigoFuente.charAt(pos))) {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        String atributoNombre = sb.toString();

        if (palabrasReservadas.contains(atributoNombre)) {
            tokens.add(crearToken(atributoNombre, atributoNombre, atributoNombre, "HTML", "Palabra Reservada", linea, inicioColumna));
        }

        // Aquí aseguramos que se crea un token para el signo '='
        if (pos < codigoFuente.length() && codigoFuente.charAt(pos) == '=') {
            tokens.add(crearToken("=", "=", "=", "HTML", "Palabra Reservada", linea, columna));  // Añade el token '='
            avanzar();  // Avanza sobre '='
            manejarEspacio();
        }

        // Manejo del valor del atributo (si existe)
        if (codigoFuente.charAt(pos) == '\"') {
            avanzar();  // Avanza sobre la comilla de apertura
            int inicioValorColumna = columna;
            StringBuilder valor = new StringBuilder();

            // Captura el valor del atributo
            while (pos < codigoFuente.length() && codigoFuente.charAt(pos) != '\"') {
                valor.append(codigoFuente.charAt(pos));
                avanzar();
            }

            // Avanza sobre la comilla de cierre
            if (pos < codigoFuente.length() && codigoFuente.charAt(pos) == '\"') {
                avanzar();
            }

            String lexemaCadena = "\"" + valor.toString() + "\"";  // Valor del atributo con comillas
            tokens.add(crearToken(lexemaCadena, "Cadena", lexemaCadena, "HTML", "Cadena", linea, inicioValorColumna));
        }
        
                // Manejo del carácter '/' antes del cierre de la etiqueta
        else if (!atributoNombre.equals("required")) {
            tokens.add(crearToken("/", "/", "/", "HTML", "Signo de Cierre", linea, columna));  // Añade el token para '/'
           columna++;
        }

    }
}

    
private void manejarTextoDentroEtiqueta(String nombreEtiqueta) {
    StringBuilder sb = new StringBuilder();
    int inicioColumna = columna;

    // Verifica si el siguiente carácter es un espacio; si lo es, no captura texto
    while (pos < codigoFuente.length() && Character.isWhitespace(codigoFuente.charAt(pos))) {
        avanzar(); // Avanza sobre los espacios
    }

    // A partir de aquí, comienza a capturar el texto solo si no hay espacios
    while (pos < codigoFuente.length() && !(codigoFuente.charAt(pos) == '<' && codigoFuente.charAt(pos + 1) == '/')) {
        // Si encontramos otra etiqueta de apertura, detenemos la captura
        if (codigoFuente.charAt(pos) == '<') {
            break;
        }
        sb.append(codigoFuente.charAt(pos)); // Captura el carácter actual
        avanzar(); // Avanza al siguiente carácter
    }

    String texto = sb.toString().trim(); // Elimina espacios en blanco al final
    if (!texto.isEmpty()) {
        // Solo se agrega el token si el texto no está vacío
        tokens.add(crearToken(texto, texto, texto, "HTML", "Texto", linea, inicioColumna));
    }
}

        
        
    private Token manejarSigno() {
        char actual = codigoFuente.charAt(pos);
        Token token = crearToken(String.valueOf(actual), String.valueOf(actual), String.valueOf(actual), "HTML", "Signo", linea, columna);
        avanzar(); // Avanza el signo
        return token;
    }

    private Token manejarCadena() {
        StringBuilder sb = new StringBuilder();
        int inicioColumna = columna;

        avanzar(); // Avanza sobre '\"'

        while (pos < codigoFuente.length() && codigoFuente.charAt(pos) != '\"') {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        // Avanzar sobre el cierre de la cadena
        if (pos < codigoFuente.length() && codigoFuente.charAt(pos) == '\"') {
            avanzar(); // Avanza sobre '\"'
        }

        String lexema = sb.toString();
        return crearToken(lexema, lexema, lexema, "HTML", "Cadena", linea, inicioColumna);
    }



    private boolean esSignoHTML(char actual) {
        for (char signo : SIGNOS_HTML) {
            if (signo == actual) {
                return true;
            }
        }
        return false;
    }

    private Token crearToken(String lexema, String tipoLexema, String traduccion, String lenguaje, String categoria, int linea, int columna) {
        return new Token(lexema, tipoLexema, traduccion, lenguaje, categoria, linea, columna);
    }

} 