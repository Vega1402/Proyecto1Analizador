/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectos.proyecto1analizador;



/**
 *
 * @author Windows 10
 */
import java.util.ArrayList;
import java.util.List;


public class AnalizadorCSS {
    private String codigoFuente;
    private int pos;
    private int linea;
    private int columna;
    private List<Token> tokens;

    // Arreglo de propiedades CSS reconocidas
    private static final String[] REGLAS_CSS = {
        "color", "background-color", "background", "font-size", "font-weight", "font-family", "font-align",
        "width", "height", "min-width", "min-height", "max-width", "max-height", "display", "inline", 
        "inline-block", "block", "flex", "grid", "none", "margin", "border", "padding", "content", 
        "border-color", "border-style", "border-width", "border-top", "border-bottom", "border-left", 
        "border-right", "box-sizing", "border-box", "position", "static", "relative", "absolute", 
        "sticky", "fixed", "top", "bottom", "left", "right", "z-index", "justify-content", "align-items", 
        "border-radius", "auto", "list-style", "float", "text-align", "box-shadow"
    };

    // Arreglo de etiquetas HTML reconocidas
    private static final String[] ETIQUETAS_HTML = {
        "body", "header", "main", "nav", "aside", "div", "ul", "ol", "li", "a", 
        "h1", "h2", "h3", "h4", "h5", "h6", "p", "span", "label", "textarea", 
        "button", "section", "article", "footer"
    };

    // Arreglo de unidades de medida CSS reconocidas
    private static final String[] OTROS = {"px", "%", "rem", "em", "vw", "vh", ":hover",
    ":active", ":not()", ":nth-child()", "odd", "even", "::before", "::after,", ":", ";", 
    ",", "(", ")" , "[" , "]", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", 
    "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E",
    "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
    "W", "X", "Y", "Z"};

    

    public AnalizadorCSS(String codigoFuente) {
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
            } else if (Character.isDigit(actual)) {
                manejarNumero();
            }else if (actual == 'r' && codigoFuente.startsWith("rgba(", pos)) {
                manejarColorRgba();
            } else if (Character.isLetter(actual)) {
                etiquetaOReglaOIdentificador();
            } else if (actual == '{') {
                tokens.add(manejarSigno("{", "Apertura de bloque"));
            } else if (actual == '}') {
                tokens.add(manejarSigno("}", "Cierre de bloque"));
            } else if (actual == '[') {
                tokens.add(manejarSigno("[", "otros"));
            } else if (actual == ']') {
                tokens.add(manejarSigno("]", "otros"));
            } else if (actual == '#') {  
                manejarColorHexadecimal();

            } else if (actual == '.') {
                manejarSelectorClase(); // Manejo de selectores de clase o ID
            } else if (actual == '/' && pos + 1 < codigoFuente.length() && codigoFuente.charAt(pos + 1) == '*') {
                manejarComentario(); // Manejo de comentarios CSS
            } else if (actual == '\'' || actual == '\"') {
                tokens.add(manejarCadena(actual)); 
            } else if (esCombinador(actual)) {
                tokens.add(manejarCombinador(actual)); 
            } else if (actual == '*') {
                tokens.add(manejarSelectorUniversal());
            } else if (actual == ','|| actual == '(' || actual == ')'  || actual == ',' || actual == '%'||  actual == ':' || actual == ';') {
                manejarOtrosToken();
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
    
    

    private void manejarNumero() {
        StringBuilder sb = new StringBuilder();
        int inicioColumna = columna;

        while (pos < codigoFuente.length() && Character.isDigit(codigoFuente.charAt(pos))) {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
    }

    // Verifica si hay un punto para un número decimal
    if (pos < codigoFuente.length() && codigoFuente.charAt(pos) == '.') {
        sb.append(codigoFuente.charAt(pos));  
        avanzar();  
        
        if (pos < codigoFuente.length() && Character.isDigit(codigoFuente.charAt(pos))) {
            while (pos < codigoFuente.length() && Character.isDigit(codigoFuente.charAt(pos))) {
                sb.append(codigoFuente.charAt(pos));
                avanzar();
            }

            // Crear el token para el número decimal
            String lexema = sb.toString();
            tokens.add(crearToken(lexema, "[0-9]+\\.[0-9]+", lexema, "CSS", "Número Decimal", linea, inicioColumna));
        } 
    } else {
        // Si no hay punto, el número es un entero
        String lexema = sb.toString(); 
        tokens.add(crearToken(lexema, "[0-9]+", lexema, "CSS", "Número Entero", linea, inicioColumna));
    }
}




    private void manejarOtrosToken() {
        StringBuilder sb = new StringBuilder();
        int inicioColumna = columna;

        // Mientras el carácter actual sea parte de un posible lexema (letras, dígitos o signos)
        while (pos < codigoFuente.length() && 
               (Character.isLetter(codigoFuente.charAt(pos)) || Character.isDigit(codigoFuente.charAt(pos)) ||
                codigoFuente.charAt(pos) == ':' || codigoFuente.charAt(pos) == ';' || 
                codigoFuente.charAt(pos) == ',' || codigoFuente.charAt(pos) == '(' || 
                codigoFuente.charAt(pos) == '-' || codigoFuente.charAt(pos) == ')' || 
                codigoFuente.charAt(pos) == '%')) {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        String lexema = sb.toString();

        tokens.add(crearToken(lexema, "Otros", lexema, "CSS", "Otros", linea, inicioColumna));
    }


    private void manejarColorHexadecimal() {
        int inicioColumna = columna;
        StringBuilder sb = new StringBuilder();

        // Añadimos el '#' inicial
        sb.append(codigoFuente.charAt(pos));
        avanzar();

        int contadorValido = 0; // Contador para verificar cuántos caracteres válidos encontramos

        // Aceptamos un color hexadecimal de 3 o 6 caracteres (0-9, a-f, A-F)
        while (pos < codigoFuente.length() && contadorValido < 6 && 
               (Character.isDigit(codigoFuente.charAt(pos)) || 
                (codigoFuente.charAt(pos) >= 'a' && codigoFuente.charAt(pos) <= 'f') || 
                (codigoFuente.charAt(pos) >= 'A' && codigoFuente.charAt(pos) <= 'F'))) {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
            contadorValido++;
        }

        String lexema = sb.toString();

        // Comprobamos si el lexema tiene 4 (3+1 del '#') o 7 (6+1 del '#') caracteres
        if (contadorValido == 3 || contadorValido == 6) { // #fff o #ffffff
            tokens.add(crearToken(lexema, "Color Hexadecimal", lexema, "CSS", "Valor", linea, inicioColumna));
        } else {
            // Si no es un color hexadecimal válido, lo tratamos como un posible ID
            manejarID();
        }
    }

    private void manejarColorRgba() {
        StringBuilder sb = new StringBuilder();
        int inicioColumna = columna;

        // Avanza sobre 'rgba('
        for (int i = 0; i < 5; i++) {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        // Captura los valores numéricos dentro de rgba( )
        while (pos < codigoFuente.length() && codigoFuente.charAt(pos) != ')') {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        // Cierra el paréntesis ')'
        sb.append(codigoFuente.charAt(pos));
        avanzar();

        tokens.add(crearToken(sb.toString(), "color RGBA", sb.toString(), "CSS", "Color RGBA", linea, inicioColumna));
    }

    

    private Token manejarSelectorUniversal() {
        int inicioColumna = columna;
        avanzar(); // Avanza sobre el '*'
        return crearToken("*", "Selector Universal", "*", "CSS", "Selector Universal", linea, inicioColumna);
    }

    private void etiquetaOReglaOIdentificador() {
        StringBuilder sb = new StringBuilder();
        int inicioColumna = columna;

        while (pos < codigoFuente.length() && (Character.isLetterOrDigit(codigoFuente.charAt(pos)) || codigoFuente.charAt(pos) == '-')) {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        String lexema = sb.toString();
        if (existeEnArreglo(ETIQUETAS_HTML, lexema)) {
            tokens.add(crearToken(lexema, "Etiqueta HTML", lexema, "CSS", "Etiqueta", linea, inicioColumna));
        } else if (existeEnArreglo(REGLAS_CSS, lexema)) {
            tokens.add(crearToken(lexema, "Regla CSS", lexema, "CSS", "Regla", linea, inicioColumna));
        } else if (existeEnArreglo(OTROS, lexema)) {
        tokens.add(crearToken(lexema, "Otros", lexema, "CSS", "Otros", linea, inicioColumna));
        }else if(esIdentificador(lexema)){
            tokens.add(crearToken(lexema, "[a-zA-Z_-][a-zA-Z0-9_-]*", lexema, "CSS", "Identificador", linea, inicioColumna));
         } 
    }
    
    private void manejarID() {
    StringBuilder sb = new StringBuilder();
    char inicial = codigoFuente.charAt(pos);
    int inicioColumna = columna;
    sb.append('#');
    sb.append(inicial);
    avanzar(); // Avanzamos sobre la almohadilla

    while (pos < codigoFuente.length() && (Character.isLetterOrDigit(codigoFuente.charAt(pos)) || codigoFuente.charAt(pos) == '-')) {
        sb.append(codigoFuente.charAt(pos));
        avanzar();
    }

    String lexema = sb.toString();
    tokens.add(crearToken(lexema, "#[a-z]+[0-9]*(-([a-z]|[0-9])+)*", lexema, "CSS", "ID", linea, inicioColumna));
}

    private void manejarSelectorClase() {
        StringBuilder sb = new StringBuilder();
        char inicial = codigoFuente.charAt(pos); //'#'
        int inicioColumna = columna;
        sb.append(inicial);
        avanzar(); // Avanzamos sobre el punto o la almohadilla

        while (pos < codigoFuente.length() && (Character.isLetterOrDigit(codigoFuente.charAt(pos)) || codigoFuente.charAt(pos) == '-')) {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        String lexema = sb.toString();
        if (inicial == '.') {
            tokens.add(crearToken(lexema, ".[a-z]+[0-9]*(- ([a-z]|[0-9])+)*", lexema, "CSS", "Clase", linea, inicioColumna));
        }
    }
    

    private boolean esCombinador(char actual) {
        return actual == '>' || actual == '+' || actual == '~' || actual == ' ';
    }

    private Token manejarCombinador(char actual) {
        String combinador = String.valueOf(actual);
        int inicioColumna = columna;
        avanzar();
        return crearToken(combinador, "Combinador CSS", combinador, "CSS", "Combinador", linea, inicioColumna);
    }

    private Token manejarSigno(String lexema, String tipo) {
        int inicioColumna = columna;
        avanzar();
        return crearToken(lexema, tipo, lexema, "CSS", tipo, linea, inicioColumna);
    }

    private void manejarComentario() {
        StringBuilder sb = new StringBuilder();
        int inicioColumna = columna;

        avanzar(); // Avanza sobre la primera '/'
        avanzar(); // Avanza sobre '*'

        while (pos < codigoFuente.length() && !(codigoFuente.charAt(pos) == '*' && pos + 1 < codigoFuente.length() && codigoFuente.charAt(pos + 1) == '/')) {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        avanzar(); // Avanza sobre '*'
        avanzar(); // Avanza sobre '/'
        tokens.add(crearToken(sb.toString(), "Comentario", sb.toString(), "CSS", "Comentario", linea, inicioColumna));
    }

    private Token manejarCadena(char delimitador) {
        StringBuilder sb = new StringBuilder();
        int inicioColumna = columna;
        avanzar(); // Avanza sobre el delimitador inicial

        while (pos < codigoFuente.length() && codigoFuente.charAt(pos) != delimitador) {
            sb.append(codigoFuente.charAt(pos));
            avanzar();
        }

        // Avanza sobre el delimitador de cierre
        avanzar(); 

        // Agregar las comillas de apertura y cierre a la cadena
        String cadenaCompleta = delimitador + sb.toString() + delimitador;

        return crearToken(cadenaCompleta, "Cadena", sb.toString(), "CSS", "Cadena", linea, inicioColumna);
    }

    private boolean esIdentificador(String cadena) {
        // Comprobar que la cadena no esté vacía
        if (cadena == null || cadena.isEmpty()) {
            return false;
        }

        // Comprobar que el primer carácter sea una letra minúscula
        if (!Character.isLowerCase(cadena.charAt(0))) {
            return false;
        }

        // Utilizar un índice para recorrer la cadena
        int i = 1;

        // Comprobar los caracteres de la cadena
        while (i < cadena.length()) {
            char c = cadena.charAt(i);

            // Verificar si el carácter es una letra minúscula o un dígito
            if (!Character.isLowerCase(c) && !Character.isDigit(c)) {
                // Verificar si hay un guion y si el siguiente carácter es válido
                if (c == '-') {
                    i++; // Avanza para verificar el siguiente carácter
                    if (i < cadena.length() && (Character.isLowerCase(cadena.charAt(i)) || Character.isDigit(cadena.charAt(i)))) {
                        // Si hay un carácter válido después del guion, continuar
                        continue;
                    } else {
                        // Si no hay un carácter válido después del guion, retornar false
                        return false;
                    }
                } else {
                    return false; // Carácter inválido
                }
            }
            i++; // Avanza al siguiente carácter
        }

        return true; // Si todos los caracteres son válidos, retornar true
    }


    
    private void Error() {
        StringBuilder sb = new StringBuilder();
        char inicial = codigoFuente.charAt(pos);
        int inicioColumna = columna;
        sb.append(inicial);
        avanzar(); // Avanzamos sobre la almohadilla


        String lexema = sb.toString();
        tokens.add(crearToken(lexema, "Error", lexema, "CSS", "Error", linea, inicioColumna));
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

    // Método auxiliar para verificar si una cadena está en un arreglo
    private boolean existeEnArreglo(String[] arreglo, String elemento) {
        for (String item : arreglo) {
            if (item.equals(elemento)) {
                return true;
            }
        }
        return false;
    }

}

