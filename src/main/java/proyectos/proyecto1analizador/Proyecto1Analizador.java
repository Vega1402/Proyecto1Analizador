/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package proyectos.proyecto1analizador;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;



/**
 *
 * @author Windows 10
 */
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
public class Proyecto1Analizador {

    private static String codigoOptimizado = ""; // Variable para almacenar el código optimizado

    public static void main(String[] args) {
        // Establecer el Look and Feel de Nimbus para una apariencia moderna
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel de Nimbus.");
        }

        // Crear ventana principal
        JFrame frame = new JFrame("Analizador HTML");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400); // Cambiado para dar más espacio al nuevo panel
        frame.setLayout(new BorderLayout());

        // Crear área de texto para ingresar el código
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Crear área de texto para mostrar el código optimizado
        JTextArea optimizedTextArea = new JTextArea();
        optimizedTextArea.setEditable(false); // Hacerlo no editable
        JScrollPane optimizedScrollPane = new JScrollPane(optimizedTextArea);

        // Crear un panel para contener ambas áreas de texto
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, optimizedScrollPane);
        splitPane.setDividerLocation(600); // Ajusta el tamaño inicial de cada panel

        // Botones para analizar, optimizar y mostrar HTML
        JButton analizarButton = new JButton("Analizar Código");
        JButton optimizarButton = new JButton("Optimizar Código");
        JButton mostrarHTMLButton = new JButton("Mostrar Página HTML");

        // Panel inferior para los botones
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new FlowLayout());
        panelInferior.add(analizarButton);
        panelInferior.add(optimizarButton);
        panelInferior.add(mostrarHTMLButton);

        // Agregar componentes al frame
        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(panelInferior, BorderLayout.SOUTH);

        // Acción del botón analizar
        analizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Usar el código optimizado si existe, de lo contrario usar el original
                String codigoFuente = codigoOptimizado.isEmpty() ? textArea.getText() : codigoOptimizado;

                // Aquí va la lógica de análisis
                ManejadorAnalizadores analizador = new ManejadorAnalizadores(codigoFuente);
                analizador.analizar();
               // analizador.imprimirTokens();
                analizador.generarArchivoHTML("Reportes.html");
                List<Token> tokens = analizador.analizar();
                analizador.generarPaginaHTML("Pagina.html");

                // Mostrar mensaje de éxito
                JOptionPane.showMessageDialog(frame, "Análisis completado. Tokens generados en tokens.html.");
            }
        });

        // Acción del botón optimizar
        optimizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String codigoFuente = textArea.getText(); // Obtener el código del JTextArea

                // Eliminar comentarios y líneas en blanco
                codigoOptimizado = eliminarEspaciosYComentarios(codigoFuente);

                // Actualizar el área de texto de código optimizado
                optimizedTextArea.setText(codigoOptimizado);
            }
        });

        // Acción del botón para mostrar la página HTML generada
        mostrarHTMLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Llamar al método para mostrar la ventana con el HTML
                mostrarHTML("Pagina.html");
            }
        });

        // Hacer visible la ventana
        frame.setVisible(true);
    }

    // Método para eliminar espacios en blanco y comentarios del código
    private static String eliminarEspaciosYComentarios(String codigo) {
        StringBuilder codigoOptimizado = new StringBuilder();
        String[] lineas = codigo.split("\n");

        for (String linea : lineas) {
            String lineaSinEspacios = linea.trim();

            // Ignorar comentarios que empiezan con //
            if (!lineaSinEspacios.startsWith("//") && !lineaSinEspacios.isEmpty()) {
                codigoOptimizado.append(lineaSinEspacios).append("\n");
            }
        }

        return codigoOptimizado.toString();
    }

    // Método para mostrar la página HTML generada en una nueva ventana
    private static void mostrarHTML(String rutaArchivoHTML) {
        JFrame htmlFrame = new JFrame("Página HTML Generada");
        htmlFrame.setSize(600, 400);

        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false); // No editable
        try {
            File archivoHTML = new File(rutaArchivoHTML);
            editorPane.setPage(archivoHTML.toURI().toURL()); // Cargar el archivo HTML
        } catch (IOException e) {
            editorPane.setContentType("text/html");
            editorPane.setText("<html><body><h1>Error cargando la página HTML.</h1></body></html>");
        }

        JScrollPane scrollPaneHTML = new JScrollPane(editorPane);
        htmlFrame.add(scrollPaneHTML);

        // Mostrar la ventana con el HTML
        htmlFrame.setVisible(true);
    }
}
