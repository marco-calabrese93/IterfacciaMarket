package market;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Handler per invocare la procedura EstraiRichiesteNonAssegnate (procedura 6)
 */
public class RichiesteNonAssegnate {

    /**
     * Esegue la procedura EstraiRichiesteNonAssegnate e stampa le richieste non assegnate.
     */
    public static void execute() {
        System.out.println("\n--- Richieste senza tecnico ---");
        System.out.printf("%-10s %-15s %-25s%n", "ID", "Numero", "DataInserimento");

        String callSql = "{CALL EstraiRichiesteNonAssegnate()}";

        try (Connection conn = DBManager.getConnection();
             CallableStatement cs = conn.prepareCall(callSql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("IDRichiesta");
                int numero = rs.getInt("NumeroRichiesta");
                Timestamp data = rs.getTimestamp("DataInserimentoRichiesta");
                System.out.printf("%-10d %-15d %-25s%n", id, numero, data);
            }

        } catch (SQLException e) {
            System.err.println("Errore nell'esecuzione di EstraiRichiesteNonAssegnate: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
