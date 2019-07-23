/*
 * TCSS 305, Spring 2018
 * Assignment 4 - SnapShop
 */

package gui;

import filters.AbstractFilter;


import filters.EdgeDetectFilter;
import filters.EdgeHighlightFilter;
import filters.FlipHorizontalFilter;
import filters.FlipVerticalFilter;
import filters.GrayscaleFilter;
import filters.SharpenFilter;
import filters.SoftenFilter;
import image.PixelImage;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


/**
 * 
 * @author David Shannon
 * @version 1
 */
public class SnapShopGUI implements ActionListener {
    
    //data fields
    
    /** Image selected by the user. */
    private PixelImage myImage;
    
    /** JFrame for main window. */
    private final JFrame myJFrame;
    
    /** JFileChooser for image myImage. */
    private final JFileChooser myJFileChooser;
    
    /** Panel for the filter buttons. */
    private JPanel myFilterPanel;

    /** Panel for the Image Label. */
    private final JPanel myImagePanel;
    
    /** Label for the image. */
    private final JLabel myImageJLabel;

    /** Button for Save As. */
    private JButton mySaveAsButton;
    
    /** Button for Close Image. */
    private JButton myCloseImageButton;
    
    /** Button for Opening the Image. */
    private JButton myOpenButton;

    
    /**
     * Initializes the JFrame.
     */
    public SnapShopGUI() {
        myJFrame = new JFrame("TCSS 305 SnapShop");
        myJFileChooser = new JFileChooser(System.getProperty("user.dir"));  //relative path
        myImageJLabel = new JLabel();
        myImagePanel = new JPanel(new BorderLayout());
    }

    /**
     * Starting point for the SnapShopGUI.
     */
    public void start() {
        myJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myJFrame.setLocationRelativeTo(null);
        
        //creates/adds the following panels to the JFrame window
        myJFrame.add(makeNavPanel(), BorderLayout.SOUTH);
        myJFrame.add(makeFilterPanel(), BorderLayout.WEST);
        myJFrame.add(myImagePanel, BorderLayout.CENTER);
        
        myJFrame.pack();
        myJFrame.setMinimumSize(myJFrame.getMinimumSize());
        myJFrame.setVisible(true);  
    }
    
    /**
     * Method to create a FilterPanel.
     * 
     * @return myFilterPanel.
     */
    private JPanel makeFilterPanel() {
        
        //creates Panel for filter Buttons
        myFilterPanel = new JPanel();
        myFilterPanel.setLayout(new GridLayout(0, 1));
 
        //calls method to create all buttons to belong on filterPanel
        makeFilterButton(new EdgeDetectFilter(), "Edge Detect");
        makeFilterButton(new EdgeHighlightFilter(), "Edge Highlight");
        makeFilterButton(new FlipHorizontalFilter(), "Flip Horizontal");
        makeFilterButton(new FlipVerticalFilter(), "Flip Vertical");
        makeFilterButton(new GrayscaleFilter(), "Grayscale");
        makeFilterButton(new SharpenFilter(), "Sharpen");
        makeFilterButton(new SoftenFilter(), "Soften");
        
        return myFilterPanel;
    }
    
    /**
     * Method to create a NavigationPanel.
     * 
     * @return myNavPanel.
     */
    private JPanel makeNavPanel() {
        final JPanel navPanel = new JPanel();
        
        //creates open Button and assigns ActionListener
        myOpenButton = new JButton("Open...");
        myOpenButton.setEnabled(true);
        myOpenButton.addActionListener(this); 

        //creates Save As Button and assigns ActionListener
        mySaveAsButton = new JButton("Save As...");
        mySaveAsButton.setEnabled(false);
        mySaveAsButton.addActionListener(this); 
        
        //creates Close Button and assigns ActionListener
        myCloseImageButton = new JButton("Close Image");
        myCloseImageButton.setEnabled(false);
        myCloseImageButton.addActionListener(this); 

        navPanel.add(myOpenButton);
        navPanel.add(mySaveAsButton);
        navPanel.add(myCloseImageButton);
  
        return navPanel;
    }
    
    @Override
    public void actionPerformed(final ActionEvent theEvent) {
        if (theEvent.getSource() == mySaveAsButton) {
            saveImage();
        } else if (theEvent.getSource() == myCloseImageButton) {
            closeImage();
        } else if (theEvent.getSource() == myOpenButton) {
            openImage();
        }
    }
        
    /**
     * Method that creates filter button and connects listener to it. 
     * 
     * @param theFilter the type of Filter associated with each button
     * @param theFilterName the name of the particular button
     * @return JButton
     */
    private JButton makeFilterButton(final AbstractFilter theFilter, 
                                     final String theFilterName) {
        final JButton filterButton = 
                        new JButton(theFilterName); 
        
        filterButton.addActionListener(new FilterListener(theFilter));
        filterButton.setEnabled(false);
        
        myFilterPanel.add(filterButton);
        return filterButton;
    }
   

    /**
     * Private helper class for opening an Image.
     */
    private void openImage() {
        if (myJFileChooser.showOpenDialog(myJFrame) == JFileChooser.APPROVE_OPTION) {  

            try {
                myImage = PixelImage.load(myJFileChooser.getSelectedFile());
               
                myImageJLabel.setIcon(new ImageIcon(myImage));
                myImageJLabel.setHorizontalAlignment(SwingConstants.CENTER);
                myImageJLabel.setVerticalAlignment(SwingConstants.CENTER);
                
                myImagePanel.add(myImageJLabel);

                mySaveAsButton.setEnabled(true);
                myCloseImageButton.setEnabled(true);
                
                myJFrame.pack();
                myJFrame.setMinimumSize(myJFrame.getSize());
                
                //accesses buttons in filter panel and sets enable to true
                for (int n = 0; n < myFilterPanel.getComponentCount(); n++) {
                    myFilterPanel.getComponent(n).setEnabled(true);
                }
               
            } catch (final IOException e) {
                JOptionPane.showMessageDialog(null, myJFileChooser.getSelectedFile().getName() 
                                              + " is not a proper image file. Try again.");
            } 
        }  
    }
    
    /**
     * Private helper class for saving an Image.
     */
    private void saveImage() {
        if (myJFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                if (myJFileChooser.getSelectedFile().exists()) {
                    final int choice = JOptionPane.showConfirmDialog(myJFrame, 
                                                     "You are about to overwrite a file"
                                                     + " that already exists. Are you sure?", 
                                                     "File Exists",
                                                     JOptionPane.YES_NO_OPTION); //0=yes, 1=no
                    if (choice == 0) {
                        myImage.save(myJFileChooser.getSelectedFile());
                    } 
                } else {
                    myImage.save(myJFileChooser.getSelectedFile());
                }                
            } catch (final IOException e) {
                JOptionPane.showMessageDialog(null, "Cannot save file.");
            }
        }
    }
    
    /**
     * Private helper class for closing an Image.
     */
    private void closeImage() {
        myImageJLabel.setIcon(null);
        mySaveAsButton.setEnabled(false);
        myCloseImageButton.setEnabled(false);
        
        //accesses buttons in filter panel and sets enable to false
        for (int n = 0; n < myFilterPanel.getComponentCount(); n++) {
            myFilterPanel.getComponent(n).setEnabled(false);
        }
        
        myJFrame.setMinimumSize(null);
        myJFrame.pack();
    }
      
    /**
     * Private inner class to create a Listener for filter buttons. 
     * 
     * @author davidshannon
     * @version 1
     */
    private class FilterListener implements ActionListener {    
        
        /**
         * the filter for each particular button.
         */
        protected final AbstractFilter myFilter;

        /**
         * Constructor for FilterListener, that passes in the particular filter.
         * 
         * @param theFilter the filter to be applied to each button. 
         */
        FilterListener(final AbstractFilter theFilter) {
            myFilter = theFilter;
        }
        
        /**
         * Method called when Button event is activated on filter.
         */
        @Override
        public void actionPerformed(final ActionEvent theEvent) {
            myFilter.filter(myImage);
            myImageJLabel.setIcon(new ImageIcon(myImage)); 
        }
    }
}




