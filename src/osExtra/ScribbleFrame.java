package osExtra;

import java.awt.*;               // ScrollPane, PopupMenu, MenuShortcut, etc.
import java.awt.datatransfer.*;  // Clipboard, Transferable, DataFlavor, etc.
import java.awt.event.*;         // New event model.
import java.io.*;                // Object serialization streams.
import java.util.zip.*;          // Data compression/decompression streams.
import java.util.Vector;         // To store the scribble in.
import java.util.Properties;     // To store printing preferences in.


/**{c}
 * ScribbleFrame
 * esempio di disegno con memoria per repaint
 * e popup menu` con cut-copy-paste
 * e save-load su file
 *
 * @version 1.00 99/11/13
 * @version 2.00 2005-10-07 package osExtra
 * @version 2.01 2005-10-21 show() -> setVisible(true);
 * @version 2.02 2011-03-30 usati generics
 * @author D.Flanagan O'Really
 */

// This example is from the book "Java in a Nutshell, Second Edition".
// Written by David Flanagan.  Copyright (c) 1997 O'Reilly & Associates.
// You may distribute this source code for non-commercial purposes only.
// You may study, modify, and use this example for any purpose, as long as
// this notice is retained.  Note that this example is provided "as is",
// WITHOUT WARRANTY of any kind either expressed or implied.

/**
 * This class places a Scribble component in a ScrollPane container,
 * puts the ScrollPane in a window, and adds a simple pulldown menu system.
 * The menu uses menu shortcuts.  Events are handled with anonymous classes.
 */
public class ScribbleFrame extends Frame {
  /** A very simple main() method for our program. */
  public static void main(String[] args) { new ScribbleFrame(); }

  /** Remember # of open windows so we can quit when last one is closed */
  protected static int num_windows = 0;

  /** Create a Frame, Menu, and ScrollPane for the scribble component */
  public ScribbleFrame() {
    super("ScribbleFrame");                  // Create the window.
    num_windows++;                           // Count it.

    ScrollPane pane = new ScrollPane();      // Create a ScrollPane.
    pane.setSize(300, 300);                  // Specify its size.
    this.add(pane, "Center");                // Add it to the frame.
    Scribble2 scribble;
    scribble = new Scribble2(this, 500, 500); // Create a bigger scribble area.
    pane.add(scribble);                      // Add it to the ScrollPane.

    MenuBar menubar = new MenuBar();         // Create a menubar.
    this.setMenuBar(menubar);                // Add it to the frame.
    Menu file = new Menu("File");            // Create a File menu.
    menubar.add(file);                       // Add to menubar.

    // Create three menu items, with menu shortcuts, and add to the menu.
    MenuItem n, c, q;
    file.add(n = new MenuItem("New Window", new MenuShortcut(KeyEvent.VK_N)));
    file.add(c = new MenuItem("Close Window",new MenuShortcut(KeyEvent.VK_W)));
    file.addSeparator();                     // Put a separator in the menu
    file.add(q = new MenuItem("Quit", new MenuShortcut(KeyEvent.VK_Q)));

    // Create and register action listener objects for the three menu items.
    n.addActionListener(new ActionListener() {     // Open a new window
      public void actionPerformed(ActionEvent e) { new ScribbleFrame(); }
    });
    c.addActionListener(new ActionListener() {     // Close this window.
      public void actionPerformed(ActionEvent e) { close(); }
    });
    q.addActionListener(new ActionListener() {     // Quit the program.
      public void actionPerformed(ActionEvent e) { System.exit(0); }
    });

    // Another event listener, this one to handle window close requests.
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) { close(); }
    });

    // Set the window size and pop it up.
    this.pack();
    // [2.01]
    // this.show();
    this.setVisible(true);
  }

  /** Close a window.  If this is the last open window, just quit. */
  void close() {
    if (--num_windows == 0) System.exit(0);
    else this.dispose();
  }
}

/**
 * This class is a custom component that supports scribbling.  It also has
 * a popup menu that allows the scribble color to be set and provides access
 * to printing, cut-and-paste, and file loading and saving facilities.
 * Note that it extends Component rather than Canvas, making it "lightweight."
 */
class Scribble2 extends Component implements ActionListener {
  protected short last_x, last_y;                // Coordinates of last click.
  protected Vector<Line> lines = new Vector<Line>(256,256);  
    // Store the scribbles.
  protected Color current_color = Color.black;   // Current drawing color.
  protected int width, height;                   // The preferred size.
  protected PopupMenu popup;                     // The popup menu.
  protected Frame frame;                         // The frame we are within.

  /** This constructor requires a Frame and a desired size */
  public Scribble2(Frame frame, int width, int height) {
    this.frame = frame;
    this.width = width;
    this.height = height;

    // We handle scribbling with low-level events, so we must specify
    // which events we are interested in.
    this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    this.enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);

    // Create the popup menu using a loop.  Note the separation of menu
    // "action command" string from menu label.  Good for internationalization.
    String[] labels = new String[] {
      "Clear", "Print", "Save", "Load", "Cut", "Copy", "Paste" };
    String[] commands = new String[] {
      "clear", "print", "save", "load", "cut", "copy", "paste" };
    popup = new PopupMenu();                   // Create the menu
    for(int i = 0; i < labels.length; i++) {
      MenuItem mi = new MenuItem(labels[i]);   // Create a menu item.
      mi.setActionCommand(commands[i]);        // Set its action command.
      mi.addActionListener(this);              // And its action listener.
      popup.add(mi);                           // Add item to the popup menu.
    }
    Menu colors = new Menu("Color");           // Create a submenu.
    popup.add(colors);                         // And add it to the popup.
    String[] colornames = new String[] { "Black", "Red", "Green", "Blue"};
    for(int i = 0; i < colornames.length; i++) {
      MenuItem mi = new MenuItem(colornames[i]);  // Create the submenu items
      mi.setActionCommand(colornames[i]);         // in the same way.
      mi.addActionListener(this);
      colors.add(mi);
    }
    // Finally, register the popup menu with the component it appears over
    this.add(popup);
  }

  /** Specifies big the component would like to be.  It always returns the
   *  preferred size passed to the Scribble2() constructor */
  public Dimension getPreferredSize() { return new Dimension(width, height); }

  /** This is the ActionListener method invoked by the popup menu items */
  public void actionPerformed(ActionEvent event) {
    // Get the "action command" of the event, and dispatch based on that.
    // This method calls a lot of the interesting methods in this class.
    String command = event.getActionCommand();
    if (command.equals("clear")) clear();
    else if (command.equals("print")) print();
    else if (command.equals("save")) save();
    else if (command.equals("load")) load();
    else if (command.equals("cut")) cut();
    else if (command.equals("copy")) copy();
    else if (command.equals("paste")) paste();
    else if (command.equals("Black")) current_color = Color.black;
    else if (command.equals("Red")) current_color = Color.red;
    else if (command.equals("Green")) current_color = Color.green;
    else if (command.equals("Blue")) current_color = Color.blue;
  }

  /** Draw all the saved lines of the scribble, in the appropriate colors */
  public void paint(Graphics g) {
    for(int i = 0; i < lines.size(); i++) {
      Line l = (Line)lines.elementAt(i);
      g.setColor(l.color);
      g.drawLine(l.x1, l.y1, l.x2, l.y2);
    }
  }

  /**
   * This is the low-level event-handling method called on mouse events
   * that do not involve mouse motion.  Note the use of isPopupTrigger()
   * to check for the platform-dependent popup menu posting event, and of
   * the show() method to make the popup visible.  If the menu is not posted,
   * then this method saves the coordinates of a mouse click or invokes
   * the superclass method.
   */
  public void processMouseEvent(MouseEvent e) {
    if (e.isPopupTrigger())                               // If popup trigger,
      popup.show(this, e.getX(), e.getY());               // pop up the menu.
    else if (e.getID() == MouseEvent.MOUSE_PRESSED) {
      last_x = (short)e.getX(); last_y = (short)e.getY(); // Save position.
    }
    else super.processMouseEvent(e);  // Pass other event types on.
  }

  /**
   * This method is called for mouse motion events.  It adds a line to the
   * scribble, on screen, and in the saved representation
   */
  public void processMouseMotionEvent(MouseEvent e) {
    if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
      Graphics g = getGraphics();                     // Object to draw with.
      g.setColor(current_color);                      // Set the current color.
      g.drawLine(last_x, last_y, e.getX(), e.getY()); // Draw this line
      lines.addElement(new Line(last_x, last_y,       // and save it, too.
                                (short) e.getX(), (short)e.getY(),
                                current_color));
      last_x = (short) e.getX();  // Remember current mouse coordinates.
      last_y = (short) e.getY();
    }
    else super.processMouseMotionEvent(e);  // Important!
  }

  /** Clear the scribble.  Invoked by popup menu */
  void clear() {
    lines.removeAllElements();   // Throw out the saved scribble
    repaint();                   // and redraw everything.
  }

  /** Print out the scribble.  Invoked by popup menu. */
  void print() {
    // Obtain a PrintJob object.  This posts a Print dialog.
    // printprefs (created below) stores user printing preferences.
    Toolkit toolkit = this.getToolkit();
    PrintJob job = toolkit.getPrintJob(frame, "Scribble", printprefs);

    // If the user clicked Cancel in the print dialog, then do nothing.
    if (job == null) return;

    // Get a Graphics object for the first page of output.
    Graphics page = job.getGraphics();

    // Check the size of the scribble component and of the page.
    Dimension size = this.getSize();
    Dimension pagesize = job.getPageDimension();

    // Center the output on the page.  Otherwise it would be
    // be scrunched up in the upper-left corner of the page.
    page.translate((pagesize.width - size.width)/2,
                   (pagesize.height - size.height)/2);

    // Draw a border around the output area, so it looks neat.
    page.drawRect(-1, -1, size.width+1, size.height+1);

    // Set a clipping region so our scribbles don't go outside the border.
    // On-screen this clipping happens automatically, but not on paper.
    page.setClip(0, 0, size.width, size.height);

    // Print this Scribble component.  By default this will just call paint().
    // This method is named print(), too, but that is just coincidence.
    this.print(page);

    // Finish up printing.
    page.dispose();   // End the page--send it to the printer.
    job.end();        // End the print job.
  }

  /** This Properties object stores the user print dialog settings. */
  private static Properties printprefs = new Properties();

  /**
   * The DataFlavor used for our particular type of cut-and-paste data.
   * This one will transfer data in the form of a serialized Vector object.
   * Note that in Java 1.1.1, this works intra-application, but not between
   * applications.  Java 1.1.1 inter-application data transfer is limited to
   * the pre-defined string and text data flavors.
   */
  public static final DataFlavor dataFlavor =
      new DataFlavor(Vector.class, "ScribbleVectorOfLines");

  /**
   * Copy the current scribble and store it in a SimpleSelection object
   * (defined below).  Then put that object on the clipboard for pasting.
   */
  public void copy() {
    // Get system clipboard
    Clipboard c = this.getToolkit().getSystemClipboard();
    // Copy and save the scribble in a Transferable object
    SimpleSelection s = new SimpleSelection(lines.clone(), dataFlavor);
    // Put that object on the clipboard
    c.setContents(s, s);
  }

  /** Cut is just like a copy, except we erase the scribble afterwards */
  public void cut() { copy(); clear();  }

  /**
   * Ask for the Transferable contents of the system clipboard, then ask that
   * object for the scribble data it represents.  If either step fails, beep!
   */
  public void paste() {
    Clipboard c = this.getToolkit().getSystemClipboard();  // Get clipboard.
    Transferable t = c.getContents(this);                  // Get its contents.
    if (t == null) {              // If there is nothing to paste, beep.
      System.out.println("*** nothing to paste");
      this.getToolkit().beep();
      return;
    }
    try {
      // Ask for clipboard contents to be converted to our data flavor.
      // This will throw an exception if our flavor is not supported.
@SuppressWarnings("unchecked")
      Vector<Line> newlines = (Vector<Line>) t.getTransferData(dataFlavor);
      // Add all those pasted lines to our scribble.
      for(int i = 0; i < newlines.size(); i++)
        lines.addElement(newlines.elementAt(i));
      // And redraw the whole thing
      repaint();
    }
    catch (UnsupportedFlavorException e) {
      System.out.println(e);
      this.getToolkit().beep();   // If clipboard has some other type of data
    }
    catch (Exception e) {
      System.out.println(e);
      this.getToolkit().beep();   // Or if anything else goes wrong...
    }
  }

  /**
   * This nested class implements the Transferable and ClipboardOwner
   * interfaces used in data transfer.  It is a simple class that remembers a
   * selected object and makes it available in only one specified flavor.
   */
  static class SimpleSelection implements Transferable, ClipboardOwner {
    protected Object selection;    // The data to be transferred.
    protected DataFlavor flavor;   // The one data flavor supported.
    public SimpleSelection(Object selection, DataFlavor flavor) {
      this.selection = selection;  // Specify data.
      this.flavor = flavor;        // Specify flavor.
    }

    /** Return the list of supported flavors.  Just one in this case */
    public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[] { flavor };
    }
    /** Check whether we support a specified flavor */
    public boolean isDataFlavorSupported(DataFlavor f) {
      return f.equals(flavor);
    }
    /** If the flavor is right, transfer the data (i.e. return it) */
    public Object getTransferData(DataFlavor f)
         throws UnsupportedFlavorException {
      if (f.equals(flavor)) return selection;
      else throw new UnsupportedFlavorException(f);
    }

    /** This is the ClipboardOwner method.  Called when the data is no
     *  longer on the clipboard.  In this case, we don't need to do much. */
    public void lostOwnership(Clipboard c, Transferable t) {
      selection = null;
    }
  }

  /**
   * Prompt the user for a filename, and save the scribble in that file.
   * Serialize the vector of lines with an ObjectOutputStream.
   * Compress the serialized objects with a GZIPOutputStream.
   * Write the compressed, serialized data to a file with a FileOutputStream.
   * Don't forget to flush and close the stream.
   */
  public void save() {
    // Create a file dialog to query the user for a filename.
    FileDialog f = new FileDialog(frame, "Save Scribble", FileDialog.SAVE);
    // [2.01]
    // f.show();                        // Display the dialog and block.
    f.setVisible(true);
    String filename = f.getFile();   // Get the user's response
    if (filename != null) {          // If user didn't click "Cancel".
      try {
        // Create the necessary output streams to save the scribble.
        FileOutputStream fos = new FileOutputStream(filename); // Save to file
        GZIPOutputStream gzos = new GZIPOutputStream(fos);     // Compressed
        ObjectOutputStream out = new ObjectOutputStream(gzos); // Save objects
        out.writeObject(lines);      // Write the entire Vector of scribbles
        out.flush();                 // Always flush the output.
        out.close();                 // And close the stream.
      }
      // Print out exceptions.  We should really display them in a dialog...
      catch (IOException e) { System.out.println(e); }
    }
  }

  /**
   * Prompt for a filename, and load a scribble from that file.
   * Read compressed, serialized data with a FileInputStream.
   * Uncompress that data with a GZIPInputStream.
   * Deserialize the vector of lines with a ObjectInputStream.
   * Replace current data with new data, and redraw everything.
   */
  public void load() {
    // Create a file dialog to query the user for a filename.
    FileDialog f = new FileDialog(frame, "Load Scribble", FileDialog.LOAD);
    // [2.01]
    // f.show();                         // Display the dialog and block.
    f.setVisible(true);
    String filename = f.getFile();    // Get the user's response
    if (filename != null) {           // If user didn't click "Cancel".
      try {
        // Create necessary input streams
        FileInputStream fis = new FileInputStream(filename); // Read from file
        GZIPInputStream gzis = new GZIPInputStream(fis);     // Uncompress
        ObjectInputStream in = new ObjectInputStream(gzis);  // Read objects
        // Read in an object.  It should be a vector of scribbles
@SuppressWarnings("unchecked")
        Vector<Line> newlines = (Vector<Line>)in.readObject();
        in.close();                    // Close the stream.
        lines = newlines;              // Set the Vector of lines.
        repaint();                     // And redisplay the scribble.
      }
      // Print out exceptions.  We should really display them in a dialog...
      catch (Exception e) { System.out.println(e); }
    }
  }

  /** A class to store the coordinates and color of one scribbled line.
   *  The complete scribble is stored as a Vector of these objects */
  static class Line implements Serializable {
    public short x1, y1, x2, y2;
    public Color color;
    public Line(short x1, short y1, short x2, short y2, Color c) {
      this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2; this.color = c;
    }
  }
}
