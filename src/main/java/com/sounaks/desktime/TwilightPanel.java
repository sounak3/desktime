package com.sounaks.desktime;

import java.awt.*;
import javax.swing.JPanel;

public class TwilightPanel extends JPanel {
    private float alpha = 1f;

    public TwilightPanel() {
        super();
    }

    public TwilightPanel(LayoutManager manager) {
        super(manager);
    }

    public void setAlpha(float value) {
        if (alpha != value) {
            alpha = Math.min(Math.max(0f, value), 1f);
            setOpaque(alpha == 1.0f);
            repaint();
        }
    }

    public float getAlpha() {
        return alpha;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.Src.derive(getAlpha()));
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g2d);
        g2d.dispose();
    }
}
