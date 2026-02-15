package com.mycompany.wawawa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Main class for the inventory manager with GUI.
 */
public class Wawawa extends JFrame {

    // Lógica de Negocio (Arrays)
    private static final int MAX_PRODUCTOS = 100;
    private Producto[] inventario = new Producto[MAX_PRODUCTOS];
    private int cantidadProductos = 0;

    // Componentes de la GUI
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtCantidad;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;

    public Wawawa() {
        setTitle("Gestor de Inventario - Wawawa Store");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel de Entrada de Datos
        JPanel panelEntrada = new JPanel(new GridLayout(4, 2, 10, 10));
        panelEntrada.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelEntrada.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelEntrada.add(txtNombre);

        panelEntrada.add(new JLabel("Precio:"));
        txtPrecio = new JTextField();
        panelEntrada.add(txtPrecio);

        panelEntrada.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField();
        panelEntrada.add(txtCantidad);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnAgregar = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        // Panel Superior (Entrada + Botones)
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelEntrada, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        // Tabla de Productos
        String[] columnas = { "Nombre", "Precio", "Cantidad" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacemos que la tabla no sea editable directamente
            }
        };
        tablaProductos = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        add(scrollPane, BorderLayout.CENTER);

        // Eventos
        btnAgregar.addActionListener(e -> agregarProducto());
        btnActualizar.addActionListener(e -> actualizarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        // Evento click en tabla para llenar campos
        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaSeleccionada = tablaProductos.getSelectedRow();
                if (filaSeleccionada != -1) {
                    cargarDatosDeFila(filaSeleccionada);
                }
            }
        });
    }

    private void agregarProducto() {
        if (cantidadProductos >= MAX_PRODUCTOS) {
            JOptionPane.showMessageDialog(this, "Inventario lleno.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String cantidadStr = txtCantidad.getText().trim();

        if (nombre.isEmpty() || precioStr.isEmpty() || cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.");
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            int cantidad = Integer.parseInt(cantidadStr);

            // Verificar duplicados (por nombre, lógica simple)
            for (int i = 0; i < cantidadProductos; i++) {
                if (inventario[i].getNombre().equalsIgnoreCase(nombre)) {
                    JOptionPane.showMessageDialog(this, "El producto ya existe.");
                    return;
                }
            }

            // Agregar al Array
            Producto nuevoProducto = new Producto(nombre, precio, cantidad);
            inventario[cantidadProductos] = nuevoProducto;
            cantidadProductos++;

            // Agregar a la Tabla (Sincronización Visual)
            modeloTabla.addRow(new Object[] { nombre, precio, cantidad });
            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio o Cantidad inválidos.");
        }
    }

    private void actualizarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.");
            return;
        }

        String nombre = txtNombre.getText().trim(); // El nombre original se usa para buscar en el array real en una app
                                                    // mas compleja, aqui asumimos que la fila corresponde al indice
        // NOTA: Como permitimos borrar, filaSeleccionada DEBERIA coincidir con el
        // indice del array SIEMPRE QUE sincronicemos bien.
        // Pero si borramos y hacemos shift, la fila JTable 0 siempre será Array index
        // 0.

        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());

            // Actualizar Array
            Producto p = inventario[filaSeleccionada];
            p.setNombre(nombre);
            p.setPrecio(precio);
            p.setCantidad(cantidad);

            // Actualizar Tabla
            modeloTabla.setValueAt(nombre, filaSeleccionada, 0);
            modeloTabla.setValueAt(precio, filaSeleccionada, 1);
            modeloTabla.setValueAt(cantidad, filaSeleccionada, 2);

            JOptionPane.showMessageDialog(this, "Producto actualizado.");
            limpiarCampos();
            tablaProductos.clearSelection();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio o Cantidad inválidos.");
        }
    }

    private void eliminarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar este producto?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Eliminar del Array (Shift Left)
            // filaSeleccionada coincide con el índice en el array porque los mantenemos
            // sincronizados 1:1
            for (int i = filaSeleccionada; i < cantidadProductos - 1; i++) {
                inventario[i] = inventario[i + 1];
            }
            inventario[cantidadProductos - 1] = null;
            cantidadProductos--;

            // Eliminar de la Tabla
            modeloTabla.removeRow(filaSeleccionada);

            limpiarCampos();
        }
    }

    private void cargarDatosDeFila(int fila) {
        String nombre = (String) modeloTabla.getValueAt(fila, 0);
        double precio = (double) modeloTabla.getValueAt(fila, 1);
        int cantidad = (int) modeloTabla.getValueAt(fila, 2);

        txtNombre.setText(nombre);
        txtPrecio.setText(String.valueOf(precio));
        txtCantidad.setText(String.valueOf(cantidad));
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtCantidad.setText("");
        tablaProductos.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Wawawa().setVisible(true);
        });
    }
}
