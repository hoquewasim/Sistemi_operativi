package osExtra;

import java.awt.*;
import java.awt.event.*;

/**{c}
 * GuiComps.java
 * esempio di vari componenti grafici
 *
 * @version 1.00 02/03/10
 * @version 2.00 2005-10-07 package osExtra
 * @version 2.01 2005-10-21 show() -&gt; setVisible(true);
 * @author M.Moro DEI UPD
 */

public class GuiComps extends Frame 
implements ActionListener, TextListener, WindowListener 
{
    // voci menu` File
    MenuItem fileNew = new MenuItem("Nuovo");
    MenuItem fileOpen = new MenuItem("Apri...");
    MenuItem fileSave = new MenuItem("Salva");
    MenuItem fileExit = new MenuItem("Esci");

    // barra e menu` 
    MenuBar menubar = new MenuBar();
    Menu fileMenu = new Menu("File");

    // componenti elementari
    Button button = new Button("Schiaccia!");
    Checkbox checkbox = new Checkbox("Seleziona!");
    Choice choice = new Choice();
    Label label = new Label("Testo non modificabile.");
    List list = new List();
    Scrollbar scrollbar = new Scrollbar();
    TextField textfield = new TextField(
  "Campo testo modificabile (return per vedere)");
    TextArea textarea = new TextArea(
          "Area di testo modificabile\nsu piu` linee." +
          " Si provi anche a selezionare\n" +
          "1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n");
    int textCount = 1;

    String openDialogStr = "Apri dialogo";
    String closeDialogStr = "Chiudi dialogo";
    String closeFrameStr = "Chiudi frame";
    String closeScrollStr = "Chiudi scroll pane";
    String openTextStr = "Vedi testo modificato";
    Button openDialogButton = new Button(openDialogStr);
    Button closeDialogButton = new Button(closeDialogStr);
    Button closeFrameButton = new Button(closeFrameStr);
    Button closeScrollButton = new Button(closeScrollStr);
    Button openTextButton = new Button(openTextStr);
    Dialog dialog;
    ScrollPane scroll;

    /**[c]
     * costruttore base, aggiunge al frame tutti i
     * componenti previsti
     */
    public GuiComps() {
        super("Esempio di componenti grafiche");
        addWindowListener(this);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        fileMenu.add(fileNew);
        fileMenu.add(fileOpen);
        fileSave.setEnabled(false);
        fileMenu.add(fileSave);
        fileMenu.addSeparator();
        fileMenu.add(fileExit);
        menubar.add(fileMenu);

        setMenuBar(menubar);

        choice.add("Voce 1");
        choice.add("Voce 2");
        choice.add("Voce 3");

        list.add("Elemento 1");
        list.add("Elemento 2");
        list.add("Elemento 3");

        add(button);
        add(checkbox);
        add(choice);
        add(label);
        add(list);
        add(scrollbar);
        add(textfield);
        add(textarea);
        openDialogButton.addActionListener(this);
        closeFrameButton.addActionListener(this);
        closeDialogButton.addActionListener(this);
        closeScrollButton.addActionListener(this);
        openTextButton.addActionListener(this);
        add(openTextButton);
        add(openDialogButton);
        add(closeFrameButton);

        textfield.addActionListener(this);
        textarea.addTextListener(this);
        
        setSize(new Dimension(500, 400));
        // [2.01]
        // show();
        setVisible(true);
    } //[c] GuiComps

    /**[m]
     * apertura di una finestra di dialogo
     */
    public void showDialog() 
    {
        dialog = new Dialog(this, "Un dialogo", true);
        dialog.setLayout(new FlowLayout());
        dialog.addWindowListener(this);
        
        
        dialog.add(closeDialogButton);
        
        dialog.pack();
        // [2.01]
        // dialog.show();
        dialog.setVisible(true);
    } //[m] showDialog

    /**[m]
     * visualizzazione di un testo su finestra dialogo
     * @param src  intero contenuto
     * @param selStr  parte selezionata
     */
    public void showTextDialog(String src,
      String selStr) {
        dialog = new Dialog(this, "Il testo", true);
        dialog.setLayout(new FlowLayout());
        dialog.addWindowListener(this);
            scroll = new ScrollPane();
            scroll.setSize(500, 100);
            if (src.indexOf('\n') != -1)
        {
            // piu` righe
                TextArea view = new TextArea(src+
                  "\n\nSelez:\n" + selStr);
            view.setEditable(false);
            scroll.add(view);
        }
        else
            // una riga
                    scroll.add(new Label(src+"(Selez:" +
                      selStr+")"));
        dialog.add(scroll);
        dialog.add(closeScrollButton);
        dialog.pack();
        // [2.01]
        // dialog.show();
        dialog.setVisible(true);
    } //[m] showTextDialog
    
    /**[m]
     * implementazione di ActionListener
     * @see ActionListener
     */
    public void actionPerformed(ActionEvent e) 
    {
        String buttonCommand = e.getActionCommand();
        if(buttonCommand.equals(openDialogStr))
            showDialog();
        else if(buttonCommand.equals(closeDialogStr) || 
          buttonCommand.equals(closeScrollStr))
            dialog.dispose();
        else if(buttonCommand.equals(closeFrameStr))
            processEvent(new 
              WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            //EventQueue.getEventQueue().postEvent(new 
            // WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        else if(buttonCommand.equals(openTextStr))
                    showTextDialog(textarea.getText(),
                      textarea.getSelectedText());
        else if(e.getSource() == textfield)
            // return su text field
                    showTextDialog(textfield.getText(),
                      textfield.getSelectedText());
    } //[m] actionPerformed
    
    /**[m]
     * implementazione di TextListener
     * @see TextListener
     */
    public void textValueChanged(TextEvent e) 
    {
        System.out.println(
          "Nuovo testo count=" + textCount++);
    } //[m] textValueChanged
    
    /**[m]
     * implementazione di WindowListener
     * @see WindowListener
     */
    public void windowClosing(WindowEvent e) 
    {
        Window originator = e.getWindow();
        if(originator.equals(this)) 
        {
            this.dispose();
            System.exit(0);
        } 
        else if(originator.equals(dialog))
            dialog.dispose();
    } //[m] windowClosing
    
    public void windowActivated(WindowEvent e) { }
    public void windowDeactivated(WindowEvent e) { }
    public void windowDeiconified(WindowEvent e) { }
    public void windowClosed(WindowEvent e) { }
    public void windowIconified(WindowEvent e) { }
    public void windowOpened(WindowEvent e) { }

    /**[m][s]
     * metodo di attivazione
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        GuiComps me = new GuiComps();
    } //[m][s] main

} //{c} GuiComps
