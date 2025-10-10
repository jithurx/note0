package com.note0.simple;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * A custom panel that represents a single note/material in the feed.
 * This component handles the display and interaction for a single post.
 */
public class NotePostPanel extends JPanel {
    private final Material material;
    private final User currentUser;
    private final MaterialDAO materialDAO;
    private final LikeDAO likeDAO;
    private final JLabel likeCountLabel;
    private final JButton likeButton;
    private final JButton downloadButton;

    public NotePostPanel(Material material, User currentUser, MaterialDAO materialDAO, LikeDAO likeDAO) {
        this.material = material;
        this.currentUser = currentUser;
        this.materialDAO = materialDAO;
        this.likeDAO = likeDAO;

        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        setLayout(new BorderLayout(10, 5));

        // Left Panel: Title and metadata
        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        // Title
        JLabel titleLabel = new JLabel(material.getTitle());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        leftPanel.add(titleLabel, gbc);

        // Uploader
        gbc.gridy++;
        leftPanel.add(new JLabel("Uploaded by: " + material.getUploaderName()), gbc);

        // Subject
        gbc.gridy++;
        leftPanel.add(new JLabel("Subject: " + material.getSubjectName()), gbc);

        // Rating
        gbc.gridy++;
        leftPanel.add(new JLabel(String.format("Average Rating: %.1f/5.0", material.getAverageRating())), gbc);

        add(leftPanel, BorderLayout.CENTER);

        // Right Panel: Actions (Like and Download)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints actionGbc = new GridBagConstraints();
        actionGbc.insets = new Insets(5, 5, 5, 5);

        // Like Button and Count
        likeButton = new JButton(material.isLikedByUser() ? "Unlike" : "Like");
        likeCountLabel = new JLabel(material.getLikeCount() + " likes");
        
        actionGbc.gridy = 0;
        rightPanel.add(likeButton, actionGbc);
        actionGbc.gridy = 1;
        rightPanel.add(likeCountLabel, actionGbc);

        // Download Button
        downloadButton = new JButton("Download");
        actionGbc.gridy = 2;
        rightPanel.add(downloadButton, actionGbc);

        add(rightPanel, BorderLayout.EAST);

        // Action Listeners
        likeButton.addActionListener(e -> handleLikeAction());
        downloadButton.addActionListener(e -> handleDownloadAction());
    }

    private void handleLikeAction() {
        try {
            boolean isNowLiked = likeDAO.toggleLike(material.getId(), currentUser.getId());
            material.setLikedByUser(isNowLiked);
            material.setLikeCount(likeDAO.getLikeCount(material.getId()));
            
            // Update UI
            likeButton.setText(isNowLiked ? "Unlike" : "Like");
            likeCountLabel.setText(material.getLikeCount() + " likes");
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error updating like: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDownloadAction() {
        try {
            Desktop.getDesktop().open(new java.io.File(material.getFilePath()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error opening file: " + ex.getMessage(),
                "File Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}