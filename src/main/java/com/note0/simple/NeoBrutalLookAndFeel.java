package com.note0.simple;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.io.IOException;
import java.awt.geom.AffineTransform;

/**
 * A Java Swing theme template inspired by the provided "Neo-Brutalist" CSS theme.
 * This class applies a consistent and playful look and feel to a Swing application
 * by setting UIManager properties.
 *
 * It is designed to be used by an AI agent in VS Code to refactor an existing
 * Swing application's UI.
 */
public class NeoBrutalLookAndFeel {

    // -- THEME COLORS (Neo-Brutalist palette) --
    public static final ColorUIResource COLOR_BG = new ColorUIResource(0xFAFAFA); // Off-white background
    public static final ColorUIResource COLOR_TEXT = new ColorUIResource(0x333333); // Dark gray text
    public static final ColorUIResource COLOR_BORDER = new ColorUIResource(0x666666); // Medium gray border
    public static final ColorUIResource COLOR_PRIMARY = new ColorUIResource(0x4A90E2); // Bright blue
    public static final ColorUIResource COLOR_SECONDARY = new ColorUIResource(0x27AE60); // Emerald green
    public static final ColorUIResource COLOR_ACCENT1 = new ColorUIResource(0xF39C12); // Gold
    public static final ColorUIResource COLOR_ACCENT2 = new ColorUIResource(0xE74C3C); // Coral red
    public static final ColorUIResource COLOR_WHITE = new ColorUIResource(0xFFFFFF); // Pure white
    
    // -- EXTENDED COLOR PALETTE FOR ASYMMETRIC CARDS --
    public static final ColorUIResource COLOR_PASTEL_YELLOW = new ColorUIResource(0xFFF4A3); // Pale yellow
    public static final ColorUIResource COLOR_PASTEL_PINK = new ColorUIResource(0xFFB3E6); // Pale pink
    public static final ColorUIResource COLOR_PASTEL_BLUE = new ColorUIResource(0xB3E0FF); // Pale blue
    public static final ColorUIResource COLOR_PASTEL_GREEN = new ColorUIResource(0xB3FFB3); // Pale green
    public static final ColorUIResource COLOR_PASTEL_ORANGE = new ColorUIResource(0xFFD9B3); // Pale orange

    // -- THEME FONTS with fallbacks --
    public static final FontUIResource FONT_GENERAL_SANS = new FontUIResource(
        loadFont("/fonts/GeneralSans-Regular.ttf", 14, Font.PLAIN));
    public static final FontUIResource FONT_BEBAS_NEUE = new FontUIResource(
        loadFont("/fonts/BebasNeue-Regular.ttf", 18, Font.BOLD));
    public static final FontUIResource FONT_ANTON = new FontUIResource(
        loadFont("/fonts/Anton-Regular.ttf", 42, Font.PLAIN));
    public static final FontUIResource FONT_INSTRUMENT_SERIF = new FontUIResource(
        loadFont("/fonts/InstrumentSerif-Regular.ttf", 22, Font.PLAIN));
    public static final FontUIResource FONT_PIXELIFY = new FontUIResource(
        loadFont("/fonts/PixelifySans-Regular.ttf", 16, Font.PLAIN));

    /**
     * Loads a custom font from the resources folder.
     * Falls back to system fonts if the custom font cannot be loaded.
     */
    private static Font loadFont(String resourcePath, int size, int style) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, 
                NeoBrutalLookAndFeel.class.getResourceAsStream(resourcePath));
            return font.deriveFont(style, size);
        } catch (FontFormatException | IOException e) {
            System.err.println("Could not load font " + resourcePath + ". Using system font.");
            // Fallback to system fonts in order of preference
            String[] fallbacks = {"Arial", "Helvetica", "SansSerif"};
            for (String fallback : fallbacks) {
                if (isSystemFontAvailable(fallback)) {
                    return new Font(fallback, style, size);
                }
            }
            // Last resort: return the default system font
            return new Font(Font.SANS_SERIF, style, size);
        }
    }

    /**
     * Checks if a font is available on the system
     */
    private static boolean isSystemFontAvailable(String fontName) {
        return java.util.Arrays.asList(
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()
        ).contains(fontName);
    }

    // -- THEME BORDERS (replicating CSS styles) --
    /**
     * Replicates the CSS style: border: 2px solid var(--color-border);
     */
    public static final Border BORDER_SOLID = BorderFactory.createLineBorder(COLOR_BORDER, 2);

    /**
     * Replicates the CSS style: border: 2px dashed var(--color-border);
     */
    public static final Border BORDER_DASHED = new DashedBorder(COLOR_BORDER, 3, 2);

    /**
     * Replicates the CSS box-shadow: 2px 2px 0px var(--color-border);
     */
    public static final Border BORDER_BUTTON_SHADOW = new ShadowBorder(BORDER_SOLID, COLOR_BORDER, 2, 8);
    
    /**
     * Replicates the CSS box-shadow: 4px 4px 0px var(--color-border);
     */
    public static final Border BORDER_CARD_SHADOW = new ShadowBorder(BORDER_SOLID, COLOR_BORDER, 4, 12);

    /**
     * A simple border with shadow for primary containers like cards.
     * Replicates: box-shadow: 5px 5px 0px var(--color-primary);
     */
    public static final Border BORDER_PRIMARY_SHADOW_PANEL = new ShadowBorder(BORDER_SOLID, COLOR_PRIMARY, 5, 12);


    /**
     * Installs the NeoBrutal Look and Feel.
     * This method should be called at the very beginning of the main() method,
     * before any Swing components are created.
     */
    public static void install() {
        try {
            // Use a base L&F that is easy to customize. Metal is a safe choice.
            UIManager.setLookAndFeel(new MetalLookAndFeel());

            UIDefaults defaults = UIManager.getLookAndFeelDefaults();

            // == General & Panel Styles ==
            // Set consistent background colors
            Color[] backgrounds = {COLOR_BG};
            String[] bgComponents = {
                "Panel", "Viewport", "OptionPane", "ColorChooser",
                "ComboBox", "List", "Menu", "MenuItem", "TextField",
                "TextArea", "EditorPane", "PasswordField"
            };
            
            for (String comp : bgComponents) {
                defaults.put(comp + ".background", COLOR_BG);
            }
            
            // Set global corner radius for components
            defaults.put("Component.arrowType", "chevron");
            defaults.put("Component.arc", 8);
            defaults.put("Component.minimumWidth", 80);
            
            // == Label Styles ==
            defaults.put("Label.font", FONT_GENERAL_SANS);
            defaults.put("Label.foreground", COLOR_TEXT);

            // == Button Styles ==
            defaults.put("Button.font", FONT_BEBAS_NEUE);
            defaults.put("Button.background", COLOR_WHITE);
            defaults.put("Button.foreground", COLOR_TEXT);
            defaults.put("Button.border", createButtonBorder());
            defaults.put("Button.focus", COLOR_ACCENT1);
            defaults.put("Button.margin", new Insets(12, 24, 12, 24));
            defaults.put("Button.rollover", true);
            defaults.put("Button.rolloverBackground", COLOR_PRIMARY);
            defaults.put("Button.select", COLOR_ACCENT1);
            defaults.put("Button.arc", 12); // Rounded corners
            defaults.put("Button.textShiftOffset", 0); // No text shift when pressed
            
            // Table styles with better contrast and readability
            defaults.put("Table.font", FONT_GENERAL_SANS);
            defaults.put("Table.background", COLOR_WHITE);
            defaults.put("Table.foreground", COLOR_TEXT);
            defaults.put("Table.selectionBackground", COLOR_PRIMARY);
            defaults.put("Table.selectionForeground", COLOR_WHITE);
            defaults.put("Table.gridColor", new ColorUIResource(new Color(0xE0E0E0))); // Light grey grid
            defaults.put("Table.intercellSpacing", new Dimension(2, 2));
            defaults.put("Table.rowHeight", 32); // Taller rows for better readability
            defaults.put("Table.showGrid", true);
            defaults.put("Table.cellMargins", new Insets(8, 12, 8, 12));

            // Table header with distinct style
            defaults.put("TableHeader.font", FONT_BEBAS_NEUE.deriveFont(16f));
            defaults.put("TableHeader.background", COLOR_SECONDARY);
            defaults.put("TableHeader.foreground", COLOR_TEXT);
            defaults.put("TableHeader.cellBorder", BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 1, COLOR_BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            
            // == Text Field & Input Styles ==
            Border textFieldBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 2),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
            );

            // Input field styles
            String[] inputComponents = {
                "TextField", "PasswordField", "FormattedTextField",
                "EditorPane", "TextArea", "TextPane"
            };

            for (String comp : inputComponents) {
                defaults.put(comp + ".font", FONT_GENERAL_SANS);
                defaults.put(comp + ".background", COLOR_WHITE);
                defaults.put(comp + ".foreground", COLOR_TEXT);
                defaults.put(comp + ".border", textFieldBorder);
                defaults.put(comp + ".selectionBackground", COLOR_PRIMARY);
                defaults.put(comp + ".selectionForeground", COLOR_WHITE);
                defaults.put(comp + ".caretForeground", COLOR_TEXT);
                defaults.put(comp + ".margin", new Insets(5, 10, 5, 10));
            }
            
            // Copy for PasswordField and FormattedTextField
            defaults.put("PasswordField.font", FONT_GENERAL_SANS);
            defaults.put("PasswordField.background", COLOR_WHITE);
            defaults.put("PasswordField.foreground", COLOR_TEXT);
            defaults.put("PasswordField.border", textFieldBorder);
            defaults.put("PasswordField.selectionBackground", COLOR_PRIMARY);
            defaults.put("PasswordField.selectionForeground", COLOR_WHITE);
            defaults.put("PasswordField.caretForeground", COLOR_TEXT);

            // ComboBox Styles
            defaults.put("ComboBox.font", FONT_GENERAL_SANS);
            defaults.put("ComboBox.background", COLOR_WHITE);
            defaults.put("ComboBox.foreground", COLOR_TEXT);
            defaults.put("ComboBox.selectionBackground", COLOR_PRIMARY);
            defaults.put("ComboBox.selectionForeground", COLOR_WHITE);
            defaults.put("ComboBox.border", textFieldBorder);
            defaults.put("ComboBox.buttonBackground", COLOR_SECONDARY);
            defaults.put("ComboBox.buttonHighlight", COLOR_ACCENT1);
            defaults.put("ComboBox.buttonDarkShadow", COLOR_BORDER);
            
            // Table Styles
            defaults.put("Table.font", FONT_GENERAL_SANS);
            defaults.put("Table.background", COLOR_WHITE);
            defaults.put("Table.foreground", COLOR_TEXT);
            defaults.put("Table.selectionBackground", COLOR_PRIMARY);
            defaults.put("Table.selectionForeground", COLOR_WHITE);
            defaults.put("Table.gridColor", new ColorUIResource(COLOR_BORDER.darker()));
            defaults.put("Table.focusCellBackground", new ColorUIResource(COLOR_ACCENT1.brighter()));
            
            // TableHeader Styles
            defaults.put("TableHeader.font", FONT_BEBAS_NEUE);
            defaults.put("TableHeader.background", COLOR_SECONDARY);
            defaults.put("TableHeader.foreground", COLOR_TEXT);
            defaults.put("TableHeader.cellBorder", 
                BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 2, COLOR_BORDER),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            
            // == Titled Border (for grouping panels) ==
            defaults.put("TitledBorder.font", FONT_ANTON.deriveFont(24f));
            defaults.put("TitledBorder.titleColor", COLOR_PRIMARY);
            defaults.put("TitledBorder.border", BORDER_DASHED);
            
            // == ScrollPane ==
            defaults.put("ScrollPane.background", COLOR_BG);
            defaults.put("ScrollPane.border", BORDER_SOLID);
            
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to install NeoBrutal Look and Feel.");
            e.printStackTrace();
        }
    }
    
    /**
     * A helper method to style a JPanel like the themed cards.
     * @param panel The JPanel to style.
     */
    public static void styleAsCard(JPanel panel) {
        panel.setBorder(BORDER_CARD_SHADOW);
        panel.setBackground(Color.WHITE);
    }
    
    /**
     * A custom border class to replicate the CSS "box-shadow" effect with a solid color.
     */
    private static class ShadowBorder extends AbstractBorder {
        private final Border innerBorder;
        private final Color shadowColor;
        private final int shadowSize;
        private final int cornerRadius;

        public ShadowBorder(Border innerBorder, Color shadowColor, int shadowSize, int cornerRadius) {
            this.innerBorder = innerBorder;
            this.shadowColor = shadowColor;
            this.shadowSize = shadowSize;
            this.cornerRadius = cornerRadius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Paint the shadow with rounded corners
            g2.setColor(shadowColor);
            g2.fillRoundRect(x + shadowSize, y + shadowSize, width - shadowSize, height - shadowSize, cornerRadius, cornerRadius);
            
            // Paint the inner border on top with rounded corners
            if (innerBorder != null) {
                g2.setColor(COLOR_BORDER);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(x + 1, y + 1, width - shadowSize - 2, height - shadowSize - 2, cornerRadius, cornerRadius);
            }
            
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            Insets insets = innerBorder != null ? innerBorder.getBorderInsets(c) : new Insets(0, 0, 0, 0);
            return new Insets(insets.top, insets.left, insets.bottom + shadowSize, insets.right + shadowSize);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }
    }

    /**
     * Creates a rounded button border with shadow effect.
     */
    private static Border createButtonBorder() {
        return new ShadowBorder(null, COLOR_BORDER, 3, 8);
    }
    
    /**
     * A custom border class to replicate a CSS "dashed" border.
     */
    private static class DashedBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int dashLength;

        public DashedBorder(Color color, int thickness, int dashLength) {
            this.color = color;
            this.thickness = thickness;
            this.dashLength = dashLength;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(color);
            Stroke dashed = new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{dashLength}, 0.0f);
            g2.setStroke(dashed);
            g2.drawRect(x, y, width - 1, height - 1);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }
    
    // -- NEW UTILITY METHODS FOR ASYMMETRIC DESIGN --
    
    /**
     * Creates a colored shadow border with the specified color and shadow size
     */
    public static Border createColoredShadow(Color shadowColor, int shadowSize, int cornerRadius) {
        return new ShadowBorder(null, shadowColor, shadowSize, cornerRadius);
    }
    
    /**
     * Creates an asymmetric card panel with rotation and colored shadow
     */
    public static void styleAsAsymmetricCard(JPanel panel, Color backgroundColor, Color shadowColor, double rotationDegrees) {
        panel.setBackground(backgroundColor);
        panel.setBorder(createColoredShadow(shadowColor, 4, 12));
        
        // Note: Rotation effect is handled in MaterialCard's paintComponent method
        // This method just sets up the styling, actual rotation is done during painting
    }
    
    /**
     * Creates a sticker-style badge with colored background and border
     */
    public static JLabel createStickerBadge(String text, Color bgColor, Color textColor) {
        JLabel badge = new JLabel(text);
        badge.setFont(FONT_PIXELIFY.deriveFont(12f));
        badge.setForeground(textColor);
        badge.setBackground(bgColor);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 2),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return badge;
    }
    
    /**
     * Creates a decorative element (circle or square) for playful layouts
     */
    public static JPanel createDecorativeElement(ShapeType shape, Color color, int size) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                switch (shape) {
                    case CIRCLE:
                        g2.fillOval(x, y, size, size);
                        g2.setColor(COLOR_BORDER);
                        g2.setStroke(new BasicStroke(2));
                        g2.drawOval(x, y, size, size);
                        break;
                    case SQUARE:
                        g2.fillRect(x, y, size, size);
                        g2.setColor(COLOR_BORDER);
                        g2.setStroke(new BasicStroke(2));
                        g2.drawRect(x, y, size, size);
                        break;
                }
                g2.dispose();
            }
        };
    }
    
    /**
     * Enum for decorative element shapes
     */
    public enum ShapeType {
        CIRCLE, SQUARE
    }
    
    /**
     * Creates a tilted button with hover effects
     */
    public static JButton createTiltedButton(String text, Color normalBg, Color hoverBg) {
        JButton button = new JButton(text);
        button.setBackground(normalBg);
        button.setFont(FONT_BEBAS_NEUE);
        button.setBorder(BORDER_BUTTON_SHADOW);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverBg);
                // Add slight tilt on hover
                button.setBorder(createColoredShadow(hoverBg, 6, 8));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(normalBg);
                button.setBorder(BORDER_BUTTON_SHADOW);
            }
        });
        
        return button;
    }
    
    /**
     * Gets a random pastel color for varied card backgrounds
     */
    public static ColorUIResource getRandomPastelColor() {
        ColorUIResource[] pastels = {
            COLOR_PASTEL_YELLOW, COLOR_PASTEL_PINK, COLOR_PASTEL_BLUE,
            COLOR_PASTEL_GREEN, COLOR_PASTEL_ORANGE
        };
        return pastels[(int) (Math.random() * pastels.length)];
    }
    
    /**
     * Gets a random rotation angle for asymmetric layouts
     */
    public static double getRandomRotation() {
        return (Math.random() - 0.5) * 6; // Range: -3 to +3 degrees
    }
}