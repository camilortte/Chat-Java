/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTextPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 *
 * @author camilortte
 * Esta Clase Hereda de JtextPanes y nos permite Ingresar texto Con colores =D
 */
public class TextPanelEdit extends JTextPane{
   
     public TextPanelEdit() {
        
    }
    
    public TextPanelEdit(StyledDocument doc) {
        super(doc);
    }

    // Override getScrollableTracksViewportWidth
    // to preserve the full width of the text
   /* public boolean getScrollableTracksViewportWidth() {
        Component parent = getParent();
        ComponentUI ui = getUI();
        return parent != null ? (ui.getPreferredSize(this).width <= parent
            .getSize().width) : true;
    }*/
    
     public void append(Color c, String s)  {         
            StyleContext sc = StyleContext.getDefaultStyleContext();
            AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,StyleConstants.Foreground, c);

            int len = getDocument().getLength(); 
            setCaretPosition(len); 
            setCharacterAttributes(aset, false);
            replaceSelection(s); 
    }
}
