package com.example.receiver;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;

public class CounselList  extends JList<Object> implements MouseListener {
    private DefaultListModel<Object> model = null;

    public CounselList(){
        this.setSize(Cons.JLIST_WIDTH, Cons.WINDOW_HEIGHT-Cons.UPPER_PANEL_HEIGHT);
        this.setBackground(Color.LIGHT_GRAY);

        model = new DefaultListModel<Object>();
        this.setModel(model);

        this.setCellRenderer(new CustomCellRenderer());
        this.addMouseListener(this);

        model.addElement("Hello");
        model.addElement("한글 사랑");
    }

    // Custom cell renderer class
    private class CustomCellRenderer extends javax.swing.DefaultListCellRenderer{

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            //Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            JLabel renderer = new JLabel(value.toString());

            // Set the preferred size to adjust the cell height (adjust the height as needed)
            renderer.setPreferredSize(new Dimension(renderer.getWidth(), 30));

            if (isSelected) {
                renderer.setBackground(list.getSelectionBackground());
                renderer.setForeground(list.getSelectionForeground());
            } else {
                renderer.setBackground(list.getBackground());
                renderer.setForeground(list.getForeground());
            }

            // Customize the font size here (adjust the font size as needed)
            Font font = new Font("Serif", Font.PLAIN, 16); // Example: Font size 16
            renderer.setFont(font);

            renderer.setOpaque(true);

            return renderer;
        }

    }

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        //System.out.println(e.getClickCount());
        int index = locationToIndex(e.getPoint());
        System.out.println("row in JList : " + index);
        CustomModalDialog customModalDialog = new CustomModalDialog(this, "lineList", "C:\\javaclass\\abuji\\CSNet\\demo\\boardImage.csr");
        customModalDialog.showDialog();
    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {
    }    
}
