package com.example.receiver;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CustomImageDialog {
    private Window wnd;
    private JDialog dialog;
    private JPanel bodyPanel;
    private String text;
    private String imageFilePath;
    private int rotationDegrees;
    private BufferedImage originalImage;
    private JLabel imageLabel;

    public CustomImageDialog(Window parentWnd, String text, String imageFilePath){
        this.wnd = parentWnd;
        this.text = text;
        this.imageFilePath = imageFilePath;
        initBodyPanel();
        rotationDegrees = 0;
    }

    public void initBodyPanel(){
        bodyPanel = new JPanel();

        try{
            //ImageIcon originalImageIcon = new ImageIcon( ImageIO.read(new File(imageFilePath)) );
            originalImage = ImageIO.read(new File(imageFilePath));

            // Calculate the new dimensions while preserving the aspect ratio
            int originalWidth = originalImage.getWidth(null);
            int originalHeight = originalImage.getHeight(null);
            int newWidth, newHeight;

            if (originalWidth > originalHeight) {
                newWidth = 700;
                newHeight = (int) ((double) originalHeight / originalWidth * 700);
            } else {
                newHeight = 700;
                newWidth = (int) ((double) originalWidth / originalHeight * 700);
            }

            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
            imageLabel = new JLabel(scaledImageIcon);
            bodyPanel.add(imageLabel);
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        
    } 

    public void rotateImage() {
        rotationDegrees = (rotationDegrees + 90) % 360;

        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(rotationDegrees), originalImage.getWidth() / 2, originalImage.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage rotatedImage = op.filter(originalImage, null);
    
        // Calculate the new dimensions while preserving the aspect ratio
        int originalWidth = rotatedImage.getWidth();
        int originalHeight = rotatedImage.getHeight();
        int newWidth, newHeight;
    
        if (originalWidth > originalHeight) {
            newWidth = 700;
            newHeight = (int) ((double) originalHeight / originalWidth * 700);
        } else {
            newHeight = 700;
            newWidth = (int) ((double) originalWidth / originalHeight * 700);
        }
    
        // Create a scaled image with the new dimensions while preserving the aspect ratio
        Image scaledImage = rotatedImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
        imageLabel.setIcon(scaledImageIcon);
    
        // Refresh the dialog
        dialog.revalidate();
        dialog.repaint();
    }

    public void showDialog() {
        dialog = new JDialog();
        dialog.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);

        JButton closeButton = new JButton("Close");

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose(); // Close the dialog
            }
        });

        JButton rotateButton = new JButton("Rotate");
        rotateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rotateImage(); // Rotate the image when the button is clicked
            }
        });

        dialog.setLayout(new BorderLayout()); 
        //if (label!=null)dialog.add(label, BorderLayout.NORTH);
        dialog.add(closeButton, BorderLayout.SOUTH);
        dialog.add(bodyPanel, BorderLayout.CENTER);
        dialog.add(rotateButton, BorderLayout.NORTH);
       
        dialog.setSize(700, 760);
        dialog.setLocationRelativeTo( wnd) ; // Center on the parent frame
        dialog.setVisible(true); // Display the modal dialog
    }       
}
