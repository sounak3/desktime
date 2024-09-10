package com.sounaks.desktime;

import java.awt.*;
import javax.swing.JPanel;

public class TwilightPanel extends JPanel {
    private float alpha = 1f;
    private transient Image bgImage;

    public TwilightPanel() {
        super();
        this.bgImage = null;
    }

    public TwilightPanel(LayoutManager manager) {
        super(manager);
        this.bgImage = null;
    }

    public TwilightPanel(Image bgImage) {
        super();
        this.bgImage = bgImage;
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
        AlphaComposite apc = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha());
        g2d.setComposite(apc);
        g2d.setColor(getBackground());
        if (bgImage == null)
            g2d.fillRect(0, 0, getWidth(), getHeight());
        else
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        super.paintComponent(g2d);
        g2d.dispose();
    }
}
