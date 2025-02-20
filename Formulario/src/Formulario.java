import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Formulario extends JFrame {
    private static final long serialVersionUID = 1L;
    private JLabel lblDescripcinDelArticulo;
    private JLabel lblCantidad;
    private JTextField tf3, tf1, tf2, tf4;
    private final JLabel lblIngreseCdigoDe;
    private final JButton btnConsultaporCdigo;
    private final JButton btnEliminar;
    private JButton btnAlta;
    private JButton btnActualizar;
    private JLabel labelResultado_1;

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Formulario frame = new Formulario();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @SuppressWarnings("unused")
    public Formulario() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 500);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        lblDescripcinDelArticulo = new JLabel("Descripcion del Articulo");
        lblDescripcinDelArticulo.setBounds(50, 50, 150, 20);
        contentPane.add(lblDescripcinDelArticulo);

        tf1 = new JTextField();
        tf1.setBounds(220, 50, 200, 20);
        contentPane.add(tf1);

        JLabel lblPrecio = new JLabel("Precio");
        lblPrecio.setBounds(50, 100, 100, 20);
        contentPane.add(lblPrecio);

        tf2 = new JTextField();
        tf2.setBounds(220, 100, 100, 20);
        contentPane.add(tf2);

        lblCantidad = new JLabel("Cantidad");
        lblCantidad.setBounds(50, 150, 100, 20);
        contentPane.add(lblCantidad);

        tf4 = new JTextField();
        tf4.setBounds(220, 150, 100, 20);
        contentPane.add(tf4);

        btnAlta = new JButton("ALTA");
        btnAlta.addActionListener((ActionEvent e) -> {
            labelResultado_1.setText("");
            try {
                if (tf1.getText().isEmpty() || tf2.getText().isEmpty() || tf4.getText().isEmpty()) {
                    labelResultado_1.setText("Por favor complete todos los campos.");
                    return;
                }

                double precio = Double.parseDouble(tf2.getText());
                int cantidad = Integer.parseInt(tf4.getText());

                try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/base1", "root", "")) {
                    String query = "INSERT INTO articulos(descripcion, precio, cantidad) VALUES (?, ?, ?)";
                    PreparedStatement comando = conexion.prepareStatement(query);
                    comando.setString(1, tf1.getText());
                    comando.setDouble(2, precio);
                    comando.setInt(3, cantidad);
                    comando.executeUpdate();
                }

                labelResultado_1.setText("Se registraron los datos");
                tf1.setText(""); tf2.setText(""); tf4.setText("");
            } catch (NumberFormatException ex) {
                labelResultado_1.setText("Error: Precio o Cantidad inválidos.");
            } catch (SQLException ex) {
                labelResultado_1.setText("Error: " + ex.getMessage());
            }
        });
        btnAlta.setBounds(220, 200, 100, 25);
        contentPane.add(btnAlta);

        labelResultado_1 = new JLabel("Resultado");
        labelResultado_1.setBounds(350, 200, 200, 20);
        contentPane.add(labelResultado_1);

        lblIngreseCdigoDe = new JLabel("Ingrese el codigo del Articulo a consultar");
        lblIngreseCdigoDe.setBounds(50, 250, 250, 20);
        contentPane.add(lblIngreseCdigoDe);

        tf3 = new JTextField();
        tf3.setBounds(320, 250, 100, 20);
        contentPane.add(tf3);

        btnConsultaporCdigo = new JButton("Consultar por Codigo");
        btnConsultaporCdigo.addActionListener(e -> {
            labelResultado_1.setText("");
            try {
                try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/base1", "root", "")) {
                    String query = "SELECT * FROM articulos WHERE codigo=?";
                    PreparedStatement comando = conexion.prepareStatement(query);
                    comando.setInt(1, Integer.parseInt(tf3.getText()));
                    ResultSet registro = comando.executeQuery();
                    if (registro.next()) {
                        tf1.setText(registro.getString("descripcion"));
                        tf2.setText(String.valueOf(registro.getDouble("precio")));
                        tf4.setText(String.valueOf(registro.getInt("cantidad")));
                    } else {
                        labelResultado_1.setText("No Existe Articulo");
                    }
                }
            } catch (SQLException ex) {
                labelResultado_1.setText("Error: " + ex.getMessage());
            }
        });
        btnConsultaporCdigo.setBounds(50, 300, 180, 25);
        contentPane.add(btnConsultaporCdigo);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> {
            try {
                try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/base1", "root", "")) {
                    String query = "DELETE FROM articulos WHERE codigo=?";
                    PreparedStatement comando = conexion.prepareStatement(query);
                    comando.setInt(1, Integer.parseInt(tf3.getText()));
                    int rowsAffected = comando.executeUpdate();
                    if (rowsAffected > 0) {
                        labelResultado_1.setText("Articulo eliminado exitosamente");
                    } else {
                        labelResultado_1.setText("No existe el articulo");
                    }
                }
            } catch (SQLException ex) {
                labelResultado_1.setText("Error: " + ex.getMessage());
            }
        });
        btnEliminar.setBounds(250, 300, 100, 25);
        contentPane.add(btnEliminar);

        btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> {
            try {
                if (tf3.getText().isEmpty() || tf1.getText().isEmpty() || tf2.getText().isEmpty() || tf4.getText().isEmpty()) {
                    labelResultado_1.setText("Por favor complete todos los campos.");
                    return;
                }

                double precio = Double.parseDouble(tf2.getText());
                int cantidad = Integer.parseInt(tf4.getText());

                try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/base1", "root", "")) {
                    String query = "UPDATE articulos SET descripcion=?, precio=?, cantidad=? WHERE codigo=?";
                    PreparedStatement comando = conexion.prepareStatement(query);
                    comando.setString(1, tf1.getText());
                    comando.setDouble(2, precio);
                    comando.setInt(3, cantidad);
                    comando.setInt(4, Integer.parseInt(tf3.getText()));
                    int rowsAffected = comando.executeUpdate();
                    if (rowsAffected > 0) {
                        labelResultado_1.setText("Articulo actualizado exitosamente");
                    } else {
                        labelResultado_1.setText("No existe el articulo para actualizar");
                    }
                }
            } catch (NumberFormatException ex) {
                labelResultado_1.setText("Error: Precio o Cantidad inválidos.");
            } catch (SQLException ex) {
                labelResultado_1.setText("Error: " + ex.getMessage());
            }
        });
        btnActualizar.setBounds(370, 300, 100, 25);
        contentPane.add(btnActualizar);
    }
}
