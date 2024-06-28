/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Juan Carlos
 */
package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.Conexion;

public class Panel_Inventario extends JPanel {

    private JComboBox<String> categoryComboBox;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> productComboBox;
    private JTextField quantityField;
    private JButton addButton;
    private JTable inventoryTable;
    private DefaultTableModel inventoryTableModel;
    private JButton registerButton;
    private JButton cancelButton;
    private JButton historyButton;
    private JLabel categoria;
    private JLabel tipo;
    private JLabel producto;
    private JLabel cantidad;

    public Panel_Inventario() {
        setLayout(new BorderLayout());

        // Panel para el registro de ingreso
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new GridBagLayout());
        registerPanel.setBorder(BorderFactory.createTitledBorder("Registrar Ingreso"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        categoria = new JLabel("Categoría:");
        gbc.gridx = 1;
        gbc.gridy = 0;
        registerPanel.add(categoria, gbc);
        categoryComboBox = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        categoryComboBox.setPreferredSize(new Dimension(150, 20));
        registerPanel.add(categoryComboBox, gbc);

        tipo = new JLabel("Tipo:");
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        registerPanel.add(tipo, gbc);
        typeComboBox = new JComboBox<>();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        typeComboBox.setPreferredSize(new Dimension(150, 20));
        registerPanel.add(typeComboBox, gbc);

        producto = new JLabel("Talla-Producto:");
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        registerPanel.add(producto, gbc);
        productComboBox = new JComboBox<>();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        productComboBox.setPreferredSize(new Dimension(150, 20));
        registerPanel.add(productComboBox, gbc);

        cantidad = new JLabel("Cantidad:");
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        registerPanel.add(cantidad, gbc);
        quantityField = new JTextField();
        gbc.gridx = 7;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        quantityField.setPreferredSize(new Dimension(150, 20));
        registerPanel.add(quantityField, gbc);

        addButton = new JButton("Añadir al Registro");
        gbc.gridx = 8;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        registerPanel.add(addButton, gbc);

        add(registerPanel, BorderLayout.NORTH);

        // Panel para el registro de inventario
        JPanel inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new BorderLayout());
        inventoryPanel.setBorder(BorderFactory.createTitledBorder("Registro de Inventario"));

        inventoryTableModel = new DefaultTableModel(new Object[]{"Categoría", "Tipo", "Producto", "Cantidad"}, 0);
        inventoryTable = new JTable(inventoryTableModel);
        inventoryPanel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);

        add(inventoryPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();// Agregar el botón de registrar ingresos al final del panel
        registerButton = new JButton("Registrar Ingresos");
        bottomPanel.add(registerButton);
        cancelButton = new JButton("Cancelar Ingreso");
        bottomPanel.add(cancelButton);
        historyButton = new JButton("Historial de Ingresos");
        bottomPanel.add(historyButton);
        add(bottomPanel, BorderLayout.SOUTH);
        // Acción del botón de añadir al registro
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToRegister();
            }
        });

        // Acción del botón de registrar ingresos
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerAllInventory();
            }
        });

        // Acción del botón de cancelar ingresos
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelSelectedRow();
            }
        });
        // Acción del botón de historial de ingresos
        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showIncomeHistory();
            }
        });

        // Añadir listeners para actualizar los JComboBox dinámicamente
        categoryComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTypeComboBox();
            }
        });

        typeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProductComboBox();
            }
        });

        loadCategories();
    }

    private void loadCategories() {
        List<String> categories = new ArrayList<>();
        try (Connection connection = Conexion.getConnection()) {
            String sql = "SELECT nombre FROM Categoria";
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    categories.add(resultSet.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        categoryComboBox.setModel(new DefaultComboBoxModel<>(categories.toArray(new String[0])));
    }

    private void updateTypeComboBox() {
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        if (selectedCategory != null) {
            List<String> types = new ArrayList<>();
            try (Connection connection = Conexion.getConnection()) {
                String sql = "SELECT nombre FROM Tipos WHERE id_categoria = (SELECT id_categoria FROM Categoria WHERE nombre = ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, selectedCategory);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            types.add(resultSet.getString("nombre"));
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            typeComboBox.setModel(new DefaultComboBoxModel<>(types.toArray(new String[0])));
        }
    }

    private void updateProductComboBox() {
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        String selectedType = (String) typeComboBox.getSelectedItem();

        if (selectedCategory != null && selectedType != null) {
            List<String> products = new ArrayList<>();
            try (Connection connection = Conexion.getConnection()) {
                String sql;

                // Si la categoría es "unisex" y el tipo es "otros", seleccionamos todos los productos de ese tipo
                if ("unisex".equalsIgnoreCase(selectedCategory) && "otros".equalsIgnoreCase(selectedType)) {
                    sql = "SELECT nombre FROM Productos WHERE id_tipo = (SELECT id_tipo FROM Tipos WHERE nombre = ? AND id_categoria = (SELECT id_categoria FROM Categoria WHERE nombre = ?))";
                } else {
                    // Para otras categorías y tipos, seleccionamos las tallas
                    sql = "SELECT talla FROM Productos WHERE id_tipo = (SELECT id_tipo FROM Tipos WHERE nombre = ? AND id_categoria = (SELECT id_categoria FROM Categoria WHERE nombre = ?))";
                }

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, selectedType);
                    statement.setString(2, selectedCategory);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            // Si estamos seleccionando productos por nombre (para unisex y otros), obtenemos el nombre
                            if ("unisex".equalsIgnoreCase(selectedCategory) && "otros".equalsIgnoreCase(selectedType)) {
                                products.add(resultSet.getString("nombre"));
                            } else {
                                // Para otras combinaciones, obtenemos la talla
                                products.add(resultSet.getString("talla"));
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Si no hay productos encontrados y la categoría es unisex y el tipo otros, agregamos "Unitalla"
            if (products.isEmpty() && "unisex".equalsIgnoreCase(selectedCategory) && "otros".equalsIgnoreCase(selectedType)) {
                products.add("Unitalla");
            }

            productComboBox.setModel(new DefaultComboBoxModel<>(products.toArray(new String[0])));
        }
    }

    private void addToRegister() {
        String category = (String) categoryComboBox.getSelectedItem();
        String type = (String) typeComboBox.getSelectedItem();
        String product = (String) productComboBox.getSelectedItem();
        String quantityStr = quantityField.getText();

        // Validar campos
        if (category == null || type == null || product == null || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cantidad debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Añadir registro a la tabla de inventario
        Object[] newRow = {category, type, product, quantity};
        inventoryTableModel.addRow(newRow);

        // Limpiar campos
        quantityField.setText("");
        productComboBox.setSelectedIndex(0);
        typeComboBox.setSelectedIndex(0);
        categoryComboBox.setSelectedIndex(0);
    }

    private void registerAllInventory() {
        int rows = inventoryTableModel.getRowCount();
        if (rows == 0) {
            JOptionPane.showMessageDialog(this, "No hay registros para ingresar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = Conexion.getConnection()) {
            String sql = "INSERT INTO Ingresos (id_producto, id_usuario, cantidad, fecha_hora) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (int i = 0; i < rows; i++) {
                    String category = (String) inventoryTableModel.getValueAt(i, 0);
                    String type = (String) inventoryTableModel.getValueAt(i, 1);
                    String product = (String) inventoryTableModel.getValueAt(i, 2);
                    int quantity = (int) inventoryTableModel.getValueAt(i, 3);

                    statement.setInt(1, getProductID(category, type, product)); // Debes implementar getProductID para obtener el ID del producto
                    statement.setInt(2, getUserID()); // Debes implementar getUserID para obtener el ID del usuario actual
                    statement.setInt(3, quantity);
                    statement.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));

                    statement.addBatch();
                }
                statement.executeBatch();
            }

            // Limpiar la tabla después de registrar los ingresos
            inventoryTableModel.setRowCount(0);

            JOptionPane.showMessageDialog(this, "Ingresos registrados exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al registrar los ingresos en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getProductID(String category, String type, String product) {
        int productId = 0;
        try (Connection connection = Conexion.getConnection()) {
            String sql;

            // Si la categoría es "unisex" y el tipo es "otros", buscamos por nombre del producto
            if ("unisex".equalsIgnoreCase(category) && "otros".equalsIgnoreCase(type)) {
                sql = "SELECT id_producto FROM Productos WHERE nombre = ? AND id_tipo = (SELECT id_tipo FROM Tipos WHERE nombre = ? AND id_categoria = (SELECT id_categoria FROM Categoria WHERE nombre = ?))";
            } else {
                sql = "SELECT id_producto FROM Productos WHERE talla = ? AND id_tipo = (SELECT id_tipo FROM Tipos WHERE nombre = ? AND id_categoria = (SELECT id_categoria FROM Categoria WHERE nombre = ?))";
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                if ("unisex".equalsIgnoreCase(category) && "otros".equalsIgnoreCase(type)) {
                    statement.setString(1, product);
                    statement.setString(2, type);
                    statement.setString(3, category);
                } else {
                    statement.setString(1, product);
                    statement.setString(2, type);
                    statement.setString(3, category);
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        productId = resultSet.getInt("id_producto");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productId;
    }

    private int getUserID() {
        // Implementa la lógica para obtener el ID del usuario actual
        // Podrías almacenar el ID del usuario en una variable de sesión al iniciar sesión
        // Aquí se asume un valor fijo para simplificación
        return 1;
    }

    private void cancelSelectedRow() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow != -1) {
            inventoryTableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una fila para cancelar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

   private void showIncomeHistory() {
    JFrame historyFrame = new JFrame("Historial de Ingresos y Stock Actual");
    historyFrame.setSize(800, 600);
    historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    historyFrame.setLayout(new GridLayout(2, 1));

    // Panel del historial de ingresos
    JPanel incomeHistoryPanel = new JPanel(new BorderLayout());
    DefaultTableModel historyTableModel = new DefaultTableModel(new Object[]{"Producto", "Cantidad", "Fecha y Hora"}, 0);
    JTable historyTable = new JTable(historyTableModel);
    incomeHistoryPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
    historyFrame.add(incomeHistoryPanel);

    // Panel del stock actual
    JPanel stockPanel = new JPanel(new BorderLayout());
    DefaultTableModel stockTableModel = new DefaultTableModel(new Object[]{"ID Producto", "Nombre", "Descripción", "Cantidad Total"}, 0);
    JTable stockTable = new JTable(stockTableModel);
    stockPanel.add(new JScrollPane(stockTable), BorderLayout.CENTER);
    historyFrame.add(stockPanel);

    // Cargar el historial de ingresos
    try (Connection connection = Conexion.getConnection()) {
        String sql = "SELECT p.nombre, i.cantidad, i.fecha_hora " +
                     "FROM Ingresos i " +
                     "JOIN Productos p ON i.id_producto = p.id_producto " +
                     "WHERE MONTH(i.fecha_hora) = MONTH(CURDATE()) AND YEAR(i.fecha_hora) = YEAR(CURDATE())";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String productName = resultSet.getString("nombre");
                int quantity = resultSet.getInt("cantidad");
                Timestamp timestamp = resultSet.getTimestamp("fecha_hora");

                Object[] row = {productName, quantity, timestamp.toString()};
                historyTableModel.addRow(row);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar el historial de ingresos.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Cargar el stock actual
    try (Connection connection = Conexion.getConnection()) {
        String sql = "SELECT p.id_producto, p.nombre, p.descripcion, inv.cantidad " +
                     "FROM Inventario inv " +
                     "JOIN Productos p ON inv.id_producto = p.id_producto";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int productId = resultSet.getInt("id_producto");
                String productName = resultSet.getString("nombre");
                String description = resultSet.getString("descripcion");
                int totalQuantity = resultSet.getInt("cantidad");

                Object[] row = {productId, productName, description, totalQuantity};
                stockTableModel.addRow(row);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar el stock actual.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    historyFrame.setVisible(true);
}

}

