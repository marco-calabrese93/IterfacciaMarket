package market;

import java.sql.*;
import java.util.*;

public class InserimentoRichiesta {

    public static void esegui() {
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DBManager.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Navigazione categorie
            int categoriaId = scegliCategoria(scanner, conn);

            // 2. Dati richiesta
            System.out.print("Numero richiesta: ");
            int numero = Integer.parseInt(scanner.nextLine());

            System.out.print("ID ordinante: ");
            int ordinante = Integer.parseInt(scanner.nextLine());

            System.out.print("ID tecnico (0 se non assegnato): ");
            int tecnico = Integer.parseInt(scanner.nextLine());
            Integer tecnicoParam = (tecnico > 0 ? tecnico : null);

            System.out.print("Note: ");
            String note = scanner.nextLine();

            // 3. Inserimento richiesta tramite procedura 1.0
            CallableStatement csInsert = conn.prepareCall("{CALL InserisciRichiesta(?, ?, ?, ?, ?, ?)}");
            csInsert.setInt(1, numero);
            csInsert.setInt(2, ordinante);
            if (tecnicoParam != null) {
                csInsert.setInt(3, tecnicoParam);
            } else {
                csInsert.setNull(3, Types.INTEGER);
            }
            csInsert.setInt(4, categoriaId);
            csInsert.setString(5, note);
            csInsert.setNull(6, Types.INTEGER);      
            csInsert.execute();

            // Recupero id con LAST_INSERT_ID()
            int idRichiesta;
            try (Statement st = conn.createStatement();
                 ResultSet rsLast = st.executeQuery("SELECT LAST_INSERT_ID()")) {
                rsLast.next();
                idRichiesta = rsLast.getInt(1);
            }
            System.out.println("Richiesta inserita con ID: " + idRichiesta);

            // 4. Recupero e valorizzazione delle caratteristiche specifiche
            PreparedStatement ps = conn.prepareStatement(
                "SELECT c.ID, c.nome FROM caratteristica c " +
                "JOIN possiede p ON c.ID = p.ID_caratteristica WHERE p.ID_categoria = ?");
            ps.setInt(1, categoriaId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idCar = rs.getInt("ID");
                String nomeCar = rs.getString("nome");
                System.out.print("Valore per " + nomeCar + ": ");
                String valore = scanner.nextLine();

                CallableStatement csVal = conn.prepareCall("{CALL ValorizzaCaratteristica(?, ?, ?)}");
                csVal.setInt(1, idRichiesta);
                csVal.setInt(2, idCar);
                csVal.setString(3, valore);
                csVal.execute();
            }

            conn.commit();
            System.out.println("Richiesta completata con successo.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int scegliCategoria(Scanner scanner, Connection conn) throws SQLException {
        int scelta = -1;
        while (true) {
            String query = (scelta == -1)
                ? "SELECT ID, nome, foglia FROM categoria WHERE ID_sopracategoria IS NULL"
                : "SELECT ID, nome, foglia FROM categoria WHERE ID_sopracategoria = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            if (scelta != -1) {
                ps.setInt(1, scelta);
            }

            ResultSet rs = ps.executeQuery();
            List<Integer> ids = new ArrayList<>();

            System.out.println("Categorie disponibili:");
            while (rs.next()) {
                int id = rs.getInt("ID");
                String nome = rs.getString("nome");
                boolean foglia = rs.getBoolean("foglia");
                System.out.printf("[%d] %s %s\n", id, nome, foglia ? "(foglia)" : "");
                ids.add(id);
            }

            System.out.print("Scegli ID categoria: ");
            int input = Integer.parseInt(scanner.nextLine());
            if (!ids.contains(input)) {
                System.out.println("Scelta non valida.");
                continue;
            }

            PreparedStatement check = conn.prepareStatement("SELECT foglia FROM categoria WHERE ID = ?");
            check.setInt(1, input);
            ResultSet r = check.executeQuery();
            if (r.next() && r.getBoolean("foglia")) return input;
            scelta = input;
        }
    }
}

