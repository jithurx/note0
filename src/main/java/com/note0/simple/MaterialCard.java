package com.note0.simple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * A reusable neobrutalist material card component.
 * Displays material information in an asymmetric, playful card layout.
 */
public class MaterialCard extends JPanel {
    
    private Material material;
    private DisplayMode displayMode;
    private MaterialClickListener clickListener;
    
    // UI Components
    private JLabel titleLabel;
    private JLabel subjectLabel;
    private JLabel uploaderLabel;
    private JLabel ratingLabel;
    private JLabel fileIconLabel;
    
    // Styling
    private Color backgroundColor;
    private Color shadowColor;
    private double rotation;
    private boolean isHovered = false;
    
    public enum DisplayMode {
        COMPACT, FULL
    }
    
    public interface MaterialClickListener {
        void onMaterialClicked(Material material);
    }
    
    public MaterialCard(Material material, DisplayMode displayMode, MaterialClickListener clickListener) {
        this.material = material;
        this.displayMode = displayMode;
        this.clickListener = clickListener;
        
        // Generate random styling for asymmetry
        this.backgroundColor = NeoBrutalLookAndFeel.getRandomPastelColor();
        this.shadowColor = getRandomShadowColor();
        this.rotation = NeoBrutalLookAndFeel.getRandomRotation();
        
        setupCard();
        setupComponents();
        setupLayout();
        setupInteractions();
    }
    
    private void setupCard() {
        setOpaque(false);
        setBorder(NeoBrutalLookAndFeel.createColoredShadow(shadowColor, 4, 12));
        
        // Set size based on display mode
        if (displayMode == DisplayMode.COMPACT) {
            setPreferredSize(new Dimension(250, 160));
            setMaximumSize(new Dimension(250, 160));
        } else {
            setPreferredSize(new Dimension(300, 200));
            setMaximumSize(new Dimension(300, 200));
        }
    }
    
    private void setupComponents() {
        // Title
        titleLabel = new JLabel(material.getTitle());
        titleLabel.setFont(NeoBrutalLookAndFeel.FONT_BEBAS_NEUE.deriveFont(16f));
        titleLabel.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Subject badge
        subjectLabel = NeoBrutalLookAndFeel.createStickerBadge(
            material.getSubjectName(), 
            NeoBrutalLookAndFeel.COLOR_SECONDARY, 
            NeoBrutalLookAndFeel.COLOR_WHITE
        );
        
        // Uploader
        uploaderLabel = new JLabel("by " + material.getUploaderName());
        uploaderLabel.setFont(NeoBrutalLookAndFeel.FONT_GENERAL_SANS.deriveFont(12f));
        uploaderLabel.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
        
        // Rating with stars
        ratingLabel = createRatingLabel(material.getAverageRating());
        
        // File type icon (placeholder)
        fileIconLabel = createFileIconLabel();
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        
        // File icon at top-left
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(fileIconLabel, gbc);
        
        // Subject badge at top-right
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(subjectLabel, gbc);
        
        // Title spans full width
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        add(titleLabel, gbc);
        
        // Uploader info
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.weightx = 0;
        add(uploaderLabel, gbc);
        
        // Rating
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        add(ratingLabel, gbc);
    }
    
    private void setupInteractions() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                // Lift effect: reduce shadow offset
                setBorder(NeoBrutalLookAndFeel.createColoredShadow(shadowColor, 2, 12));
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                setBorder(NeoBrutalLookAndFeel.createColoredShadow(shadowColor, 4, 12));
                repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (clickListener != null) {
                    clickListener.onMaterialClicked(material);
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Apply rotation if specified
        if (Math.abs(rotation) > 0.1) {
            AffineTransform transform = AffineTransform.getRotateInstance(
                Math.toRadians(rotation), 
                getWidth() / 2.0, 
                getHeight() / 2.0
            );
            g2.setTransform(transform);
        }
        
        // Draw background with rounded corners
        g2.setColor(backgroundColor);
        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
        
        // Draw border
        g2.setColor(NeoBrutalLookAndFeel.COLOR_BORDER);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
        
        // Add some decorative elements for playfulness
        if (displayMode == DisplayMode.FULL) {
            drawDecorativeElements(g2);
        }
        
        g2.dispose();
    }
    
    private void drawDecorativeElements(Graphics2D g2) {
        g2.setColor(backgroundColor.brighter());
        g2.setStroke(new BasicStroke(1));
        
        // Draw small circles or squares in corners
        int size = 8;
        g2.fillOval(8, 8, size, size);
        g2.fillRect(getWidth() - 16, getHeight() - 16, size, size);
    }
    
    private JLabel createRatingLabel(double rating) {
        JLabel label = new JLabel();
        label.setFont(NeoBrutalLookAndFeel.FONT_PIXELIFY.deriveFont(12f));
        label.setForeground(NeoBrutalLookAndFeel.COLOR_ACCENT1);
        
        StringBuilder stars = new StringBuilder();
        int fullStars = (int) rating;
        boolean hasHalfStar = (rating - fullStars) >= 0.5;
        
        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        if (hasHalfStar) {
            stars.append("☆");
        }
        while (stars.length() < 5) {
            stars.append("☆");
        }
        
        stars.append(" ").append(String.format("%.1f", rating));
        label.setText(stars.toString());
        
        return label;
    }
    
    private JLabel createFileIconLabel() {
        JLabel icon = new JLabel("📄");
        icon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
        icon.setPreferredSize(new Dimension(32, 32));
        return icon;
    }
    
    private Color getRandomShadowColor() {
        Color[] shadows = {
            NeoBrutalLookAndFeel.COLOR_PRIMARY,
            NeoBrutalLookAndFeel.COLOR_SECONDARY,
            NeoBrutalLookAndFeel.COLOR_ACCENT1,
            NeoBrutalLookAndFeel.COLOR_ACCENT2,
            NeoBrutalLookAndFeel.COLOR_BORDER
        };
        return shadows[(int) (Math.random() * shadows.length)];
    }
    
    // Getters
    public Material getMaterial() { return material; }
    public DisplayMode getDisplayMode() { return displayMode; }
    public Color getBackgroundColor() { return backgroundColor; }
    public Color getShadowColor() { return shadowColor; }
    public double getRotation() { return rotation; }
    
    // Setters
    public void setMaterial(Material material) { 
        this.material = material; 
        titleLabel.setText(material.getTitle());
        subjectLabel.setText(material.getSubjectName());
        uploaderLabel.setText("by " + material.getUploaderName());
        ratingLabel.setText(createRatingLabel(material.getAverageRating()).getText());
    }
    
    public void setClickListener(MaterialClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
