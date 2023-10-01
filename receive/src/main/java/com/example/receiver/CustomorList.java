package com.example.receiver;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class CustomorList extends JList<String> implements MouseListener{

    private DefaultListModel<String> model = null;

    public CustomorList(){
        this.setBackground(Color.green);

        model = new DefaultListModel<String>();
        this.setModel(model);

        this.setCellRenderer(new CustomCellRenderer());
        this.addMouseListener(this);

        model.addElement("Hello");
        model.addElement("한글 사랑");
    }

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
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
}
