/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

/**
 *
 * @author Juan Carlos
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Inventario extends JFrame {
    private JComboBox<String> categoriaComboBox;
    private JComboBox<String> tipoComboBox;
    private JComboBox<String> productoComboBox;
    private JTextField cantidadField;
    private JTable table;
    private DefaultTableModel tableModel;

    public Inventario() {
        setTitle("Inventario");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior con botones
        JPanel topPanel = new JPanel();
        JButton verHistorialButton = new JButton("Ver Historial");
        JButton cerrarSesionButton = new JButton("Cerrar Sesión");
        topPanel.add(verHistorialButton);
        topPanel.add(cerrarSesionButton);
        add(topPanel, BorderLayout.NORTH);

        // Panel central con los combo boxes y el campo de texto
        JPanel centerPanel = new JPanel(new GridLayout(2, 4));

        categoriaComboBox = new JComboBox<>(new String[]{"Categoría 1", "Categoría 2"});
        tipoComboBox = new JComboBox<>(new String[]{"Tipo 1", "Tipo 2"});
        productoComboBox = new JComboBox<>(new String[]{"Producto 1", "Producto 2"});
        cantidadField = new JTextField();

        centerPanel.add(new JLabel("Categoría"));
        centerPanel.add(new JLabel("Tipo"));
        centerPanel.add(new JLabel("Producto"));
        centerPanel.add(new JLabel("Cantidad"));

        centerPanel.add(categoriaComboBox);
        centerPanel.add(tipoComboBox);
        centerPanel.add(productoComboBox);
        centerPanel.add(cantidadField);

        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior con la tabla y los botones
        JPanel bottomPanel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Categoría", "Tipo", "Producto", "Cantidad"}, 0);
        table = new JTable(tableModel);
        bottomPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton registrarButton = new JButton("Registrar");
        JButton cancelarButton = new JButton("Cancelar");
        buttonPanel.add(registrarButton);
        buttonPanel.add(cancelarButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Acción para registrar el inventario
        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String categoria = (String) categoriaComboBox.getSelectedItem();
                String tipo = (String) tipoComboBox.getSelectedItem();
                String producto = (String) productoComboBox.getSelectedItem();
                int cantidad = Integer.parseInt(cantidadField.getText());

                // Agregar a la tabla
                tableModel.addRow(new Object[]{categoria, tipo, producto, cantidad});

                // Limpiar campos
                categoriaComboBox.setSelectedIndex(0);
                tipoComboBox.setSelectedIndex(0);
                productoComboBox.setSelectedIndex(0);
                cantidadField.setText("");

                // Guardar en la base de datos
                guardarEnBaseDeDatos(categoria, tipo, producto, cantidad);
            }
        });

        // Acción para cancelar la entrada
        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                categoriaComboBox.setSelectedIndex(0);
                tipoComboBox.setSelectedIndex(0);
                productoComboBox.setSelectedIndex(0);
                cantidadField.setText("");
            }
        });

        // Mostrar la ventana
        setVisible(true);
    }

    private void guardarEnBaseDeDatos(String categoria, String tipo, String producto, int cantidad) {
        String url = "jdbc:mysql://localhost:3306/inventario_db";
        String user = "root";
        String password = "password";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String query = "INSERT INTO inventario (categoria, tipo, producto, cantidad) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, categoria);
                stmt.setString(2, tipo);
                stmt.setString(3, producto);
                stmt.setInt(4, cantidad);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Inventario());
    }
}

