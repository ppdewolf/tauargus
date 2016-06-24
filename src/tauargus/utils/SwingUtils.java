/*
* Argus Open Source
* Software to apply Statistical Disclosure Control techniques
* 
* Copyright 2014 Statistics Netherlands
* 
* This program is free software; you can redistribute it and/or 
* modify it under the terms of the European Union Public Licence 
* (EUPL) version 1.1, as published by the European Commission.
* 
* You can find the text of the EUPL v1.1 on
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
* 
* This software is distributed on an "AS IS" basis without 
* warranties or conditions of any kind, either express or implied.
*/

package tauargus.utils;

import java.awt.Component;
import java.awt.Container;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class SwingUtils {

    public static String getLabelText(JTextField textField) {
        Container container = textField.getParent();
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                if (label.getLabelFor() == textField) {
                    return label.getText();
                }
            }
        }
        return null;
    }

    public static boolean verifyTextFields(Component component) {
        if (component instanceof JTextField) {
            JTextField textField = (JTextField) component;
            if (textField.isVisible() && textField.isEnabled()) {
                InputVerifier verifier = textField.getInputVerifier();
                if (verifier != null && !verifier.verify(textField)) {
                    textField.requestFocusInWindow();
                    if (verifier instanceof ArgusInputVerifier) {
                        JOptionPane.showMessageDialog(textField, ((ArgusInputVerifier) verifier).getMessage(textField));
                    }
                    return false;
                }
            }
        } else if (component instanceof Container) {
            Container container = (Container) component;
            for (Component subComponent : container.getComponents()) {
                if (!verifyTextFields(subComponent)) {
                    return false;
                }
            }
        }
        return true;
    }
    
   public static JRadioButton getSelectedButton(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected() && button instanceof JRadioButton) {
                return (JRadioButton)button;
            }
        }
        return null;
    }   
   
    /**
     * Only works for default look and feel, where Swing creates a real close
     * button. In other look and feels close buttons are handled by the native
     * OS and are not part of the Swing component hierarchy.
     */
   public static void removeCloseButton(Component comp) {
        if (comp instanceof JMenu) {
            Component[] children = ((JMenu) comp).getMenuComponents();
            for (int i = 0; i < children.length; ++i) {
                removeCloseButton(children[i]);
            }
        } else if (comp instanceof AbstractButton) {
            Action action = ((AbstractButton) comp).getAction();
            String cmd = (action == null) ? "" : action.toString();
            if (cmd.contains("CloseAction")) {
                comp.getParent().remove(comp);
            }
        } else if (comp instanceof Container) {
            Component[] children = ((Container) comp).getComponents();
            for (int i = 0; i < children.length; ++i) {
                removeCloseButton(children[i]);
            }
        }
    }
}
