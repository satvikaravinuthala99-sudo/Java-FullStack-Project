
package com.echovault.dao;

import com.echovault.model.Capsule;
import com.echovault.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class CapsuleDAO {

    // Convert database record into Capsule object
    // Hide description until the unlock date.
    private Capsule map(ResultSet r) throws SQLException {

        Capsule capsule = new Capsule();

        capsule.id = r.getInt("id");
        capsule.title = r.getString("title");
        capsule.recipientEmail = r.getString("recipient_email");
        capsule.visibility = r.getString("visibility");
        capsule.status = r.getString("status");
        capsule.unlockAt = r.getTimestamp("unlock_at");
        capsule.createdAt = r.getTimestamp("created_at");

        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Reveal description only after the unlock date.
        if (capsule.unlockAt != null
                && !capsule.unlockAt.after(now)) {

            capsule.description = r.getString("description");

        } else {

            capsule.description = null;
        }

        return capsule;
    }


    // Create a new time capsule
    public int create(
            int owner,
            String title,
            String description,
            String recipient,
            String visibility,
            Timestamp unlock
    ) throws SQLException {

        String sql = "INSERT INTO capsules "
                + "(owner_id, title, description, recipient_email, "
                + "visibility, unlock_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (
            Connection c = DBConnection.getConnection();

            PreparedStatement p = c.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            )
        ) {

            p.setInt(1, owner);
            p.setString(2, title);
            p.setString(3, description);
            p.setString(4, recipient);
            p.setString(5, visibility);
            p.setTimestamp(6, unlock);

            p.executeUpdate();

            try (ResultSet r = p.getGeneratedKeys()) {

                if (r.next()) {
                    return r.getInt(1);
                }

                throw new SQLException(
                        "Creating capsule failed. No ID was returned."
                );
            }
        }
    }


    // Get capsules created by the user
    // Also get capsules shared with the user's email.
    public List<Capsule> listForUser(
            int user,
            String email
    ) throws SQLException {

        List<Capsule> capsules = new ArrayList<>();

        String sql = "SELECT * FROM capsules "
                + "WHERE owner_id = ? "
                + "OR (recipient_email = ? AND visibility = 'SHARED') "
                + "ORDER BY created_at DESC";

        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement p = c.prepareStatement(sql)
        ) {

            p.setInt(1, user);
            p.setString(2, email);

            try (ResultSet r = p.executeQuery()) {

                while (r.next()) {
                    capsules.add(map(r));
                }
            }
        }

        return capsules;
    }


    // Find a capsule only if the user is its owner
    // or the authorized shared recipient.
    public Capsule findVisible(
            int id,
            int user,
            String email
    ) throws SQLException {

        String sql = "SELECT * FROM capsules "
                + "WHERE id = ? "
                + "AND (owner_id = ? "
                + "OR (recipient_email = ? AND visibility = 'SHARED'))";

        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement p = c.prepareStatement(sql)
        ) {

            p.setInt(1, id);
            p.setInt(2, user);
            p.setString(3, email);

            try (ResultSet r = p.executeQuery()) {

                if (r.next()) {
                    return map(r);
                }

                return null;
            }
        }
    }


    // Get all capsules (for administrative use)
    public List<Capsule> all() throws SQLException {

        List<Capsule> capsules = new ArrayList<>();

        String sql = "SELECT * FROM capsules "
                + "ORDER BY created_at DESC";

        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement p = c.prepareStatement(sql);
            ResultSet r = p.executeQuery()
        ) {

            while (r.next()) {
                capsules.add(map(r));
            }
        }

        return capsules;
    }


    // Add an uploaded memory/file to a capsule
    public void addMemory(
            int capsule,
            String original,
            String stored,
            String mime
    ) throws SQLException {

        String sql = "INSERT INTO memories "
                + "(capsule_id, original_name, stored_name, mime_type) "
                + "VALUES (?, ?, ?, ?)";

        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement p = c.prepareStatement(sql)
        ) {

            p.setInt(1, capsule);
            p.setString(2, original);
            p.setString(3, stored);
            p.setString(4, mime);

            p.executeUpdate();
        }
    }


    // Get uploaded memories belonging to a capsule
    public List<String[]> memories(int id) throws SQLException {

        List<String[]> memories = new ArrayList<>();

        String sql = "SELECT original_name, stored_name, mime_type "
                + "FROM memories "
                + "WHERE capsule_id = ? "
                + "ORDER BY id";

        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement p = c.prepareStatement(sql)
        ) {

            p.setInt(1, id);

            try (ResultSet r = p.executeQuery()) {

                while (r.next()) {

                    memories.add(new String[] {
                        r.getString("original_name"),
                        r.getString("stored_name"),
                        r.getString("mime_type")
                    });
                }
            }
        }

        return memories;
    }


    // Count capsules created by a particular user
    public int countForUser(int user) throws SQLException {

        String sql = "SELECT COUNT(*) "
                + "FROM capsules WHERE owner_id = ?";

        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement p = c.prepareStatement(sql)
        ) {

            p.setInt(1, user);

            try (ResultSet r = p.executeQuery()) {

                if (r.next()) {
                    return r.getInt(1);
                }

                return 0;
            }
        }
    }

}
