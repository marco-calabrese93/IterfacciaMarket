package market;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handler per invocare la procedura TempoMedioTecnici
 */
public class TempoMedioTecnici {

    /**
     * Esegue la procedura TempoMedioTecnici e stampa i risultati.
     */
    public static void execute() {
        System.out.println("\n--- Tempo medio di evasione per tecnici ---");
        System.out.printf("%-10s %-25s %-20s%n", "ID", "Tecnico", "MediaGiorni");

        try (Connection conn = DBManager.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL TempoMedioTecnici()}");
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                int idTec = rs.getInt("IDTecnico");
                String tecnico = rs.getString("Tecnico");
                double media = rs.getDouble("TempoMedioEvasione_Giorni");
                System.out.printf("%-10d %-25s %-20.2f%n", idTec, tecnico, media);
            }

        } catch (SQLException e) {
            System.err.println("Errore nell'esecuzione di TempoMedioTecnici: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

