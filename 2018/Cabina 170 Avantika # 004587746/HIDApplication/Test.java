/* Author:	SYLAM
 * Date:	15 July 2007
 * 
 * Description:	This application communicates with an HID via Win32 API.
 * 
 * Acknowlegdement: www.lvr.com
 * 
 * 
*/



import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;

public class Test implements Runnable
{
	MyHID myHID = new MyHID();

	ReportTM	featureTM = new ReportTM(ReportTM.Editable),
				inputTM = new ReportTM(ReportTM.NonEditable),
				outputTM = new ReportTM(ReportTM.Editable);

	String[] comboItems = { "Feature Report", "Input Report", "Output Report" };
	JComboBox combo = new JComboBox(comboItems);
	JButton	setFeatureBtn = new JButton("Set Feature"),
			getFeatureBtn = new JButton("Get Feature"),
			intGetBtn = new JButton("INT Read"),
			ctrlGetBtn = new JButton("CTRL Read"),
			intSetBtn = new JButton("INT Write"),
			ctrlSetBtn = new JButton("CTRL Write");
	JTable table = new JTable(featureTM);
	JLabel	featureLBL = new JLabel(" Feature Report Length: 0"),
			inputLBL = new JLabel(" Input Report Length: 0"),
			outputLBL = new JLabel(" Output Report Length: 0"),
			status = new JLabel(" Numbers shall be entered in HEX.");
	HexField	vendorTF = new HexField(0x0925, 4),
				productTF = new HexField(0x1299, 4);

	Thread selfThread = null;
	int threadStatus = 0;

	public void run()
	{
		byte[] arr = myHID.IntReadInputReport();
		intGetBtn.setEnabled(true);
		if (arr.length > 0)
		{
			inputTM.report = arr;
			combo.setSelectedIndex(1);
			status.setText(" Input Report was read");
		}
		else status.setText(" Read Input Report failed");
		threadStatus = 0;
	}

	protected void finalizer()
	{
		myHID.CloseHandles();
	}

	Test()
	{
		//Main Frame Setting
		JFrame frame = new JFrame("MyHID Application");
		frame.setSize(450,300);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cp = frame.getContentPane();

		//North Panel
		GridBagLayout gridbag = new GridBagLayout();
		JPanel northPanel = new JPanel(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		cp.add(northPanel, BorderLayout.NORTH);

		c.fill = GridBagConstraints.HORIZONTAL;
		JLabel vendorLBL = new JLabel(" Vendor ID: ");
		gridbag.setConstraints(vendorLBL, c);
		northPanel.add(vendorLBL);
		c.weightx = 1;
		gridbag.setConstraints(vendorTF, c);
		northPanel.add(vendorTF);
		JLabel productLBL = new JLabel(" Product ID: ");
		c.weightx = 0;
		gridbag.setConstraints(productLBL, c);
		northPanel.add(productLBL);
		c.weightx = 1;
		gridbag.setConstraints(productTF, c);
		northPanel.add(productTF);
		c.weightx = 0;
		JButton findHidBtn = new JButton("Find HID");
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(findHidBtn, c);
		northPanel.add(findHidBtn);
		c.gridwidth = 4;
		c.weightx = 1;
		gridbag.setConstraints(featureLBL, c);
		northPanel.add(featureLBL);
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = 1;
		c.weightx = 0;
		gridbag.setConstraints(setFeatureBtn, c);
		northPanel.add(setFeatureBtn);
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(getFeatureBtn, c);
		northPanel.add(getFeatureBtn);
		c.gridwidth = 4;
		c.weightx = 1;
		gridbag.setConstraints(inputLBL, c);
		northPanel.add(inputLBL);
		c.gridwidth = 1;
		c.weightx = 0;
		gridbag.setConstraints(ctrlGetBtn, c);
		northPanel.add(ctrlGetBtn);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0;
		gridbag.setConstraints(intGetBtn, c);
		northPanel.add(intGetBtn);
		c.gridwidth = 4;
		c.weightx = 1;
		gridbag.setConstraints(outputLBL, c);
		northPanel.add(outputLBL);
		c.gridwidth = 1;
		c.weightx = 0;
		gridbag.setConstraints(ctrlSetBtn, c);
		northPanel.add(ctrlSetBtn);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0;
		gridbag.setConstraints(intSetBtn, c);
		northPanel.add(intSetBtn);
		gridbag.setConstraints(combo, c);
		northPanel.add(combo);
		

		//Main Display Panel
		table.setDefaultRenderer(Byte.class, new HexRenderer());
		table.setDefaultEditor(Byte.class, new HexEditor(2));
		table.setSurrendersFocusOnKeystroke(true);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); 
		table.getTableHeader().setReorderingAllowed(false);
		formatTable();

		JScrollPane mainPanel = new JScrollPane(table);
		cp.add(mainPanel, BorderLayout.CENTER);

		//Status Bar
		cp.add(status, BorderLayout.SOUTH);

		setButtonState();

		frame.setVisible(true);

		//Add Listerners
		combo.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { doCombo();} });
		findHidBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { doFindHid(); } });
		getFeatureBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { doGetFeature(); } });
		setFeatureBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { doSetFeature(); } });
		intGetBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { doIntGet(); } });
		ctrlGetBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { doCtrlGet(); } });
		intSetBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { doIntSet(); } });
		ctrlSetBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { doCtrlSet(); } });

	}

	public static void main(String[] args) 
	{
		new Test();
	}

	void doCombo()
	{
		switch (combo.getSelectedIndex())
		{
			case 0:
				table.setModel(featureTM);
				break;
			case 1:
				table.setModel(inputTM);
				break;
			case 2:
				table.setModel(outputTM);
				break;
			default: return;
		}
		formatTable();
		setButtonState();
	}

	void doFindHid()
	{
		if (myHID.FindTheHID(vendorTF.getValue(), productTF.getValue()))
			status.setText(" The HID found");
		else
		{
			status.setText(" Did not found the HID: " + vendorTF.getText() + "/" + productTF.getText());
			featureLBL.setText(" Feature Report Length: 0");
			inputLBL.setText(" Input Report Length: 0");
			outputLBL.setText(" Output Report Length: 0");
		}
		int i = myHID.ReadFeatureReportSize();
		featureTM.report = new byte[i];
		featureLBL.setText(" Feature Report Length: " + i);
		i = myHID.ReadInputReportSize();
		inputTM.report = new byte[i];
		inputLBL.setText(" Input Report Length: " + i);
		i = myHID.ReadOutputReportSize();
		outputTM.report = new byte[i];
		outputLBL.setText(" Output Report Length: " + i);
		setButtonState();
		table.repaint();
	}

	void doGetFeature()
	{
		byte[] arr = myHID.ReadFeatureReport();	// I wonder why it might fail
		if (arr.length == 0)
		{
			doFindHid();						// try once more may work
			arr = myHID.ReadFeatureReport();
		}
		if (arr.length > 0)
		{
			featureTM.report = arr;
			combo.setSelectedIndex(0);
			status.setText(" Feature Report was read");
		}
		else status.setText(" Read Feature Report failed");
	}

	void doSetFeature()
	{
		if (myHID.SendFeatureReport(featureTM.report)) status.setText(" Feature Report was sent");
		else status.setText(" Feature Report could not be sent");
	}

	void doIntGet()
	{
		if (threadStatus != 0) return;
		threadStatus = 1;
		selfThread = new Thread(this);
		selfThread.start();
		intGetBtn.setEnabled(false);
	}

	void doCtrlGet()
	{
		byte[] arr = myHID.CtrlReadInputReport();
		if (arr.length > 0)
		{
			inputTM.report = arr;
			combo.setSelectedIndex(1);
			status.setText(" Input Report was read");
		}
		else status.setText(" Read Input Report failed");
	}

	void doIntSet()
	{
		if (myHID.IntSendOutputReport(outputTM.report))
			status.setText(" Output Report was sent");
		else status.setText(" Send Input Report failed");
	}

	void doCtrlSet()
	{
		if (myHID.CtrlSendOutputReport(outputTM.report))
			status.setText(" Output Report was sent");
		else status.setText(" Send Input Report failed");
	}

	void formatTable()
	{
		TableColumn column;
		int cnt = table.getColumnCount();
		for (int i = 0; i < cnt; i++)
		{
			column = table.getColumnModel().getColumn(i);
			column.setResizable(false);
			if (i == 0) column.setPreferredWidth(35);
		}
	}

	void setButtonState()
	{
		int cIndex = combo.getSelectedIndex();
		if ((featureTM.report.length > 0) && (cIndex == 0)) setFeatureBtn.setEnabled(true);
		else setFeatureBtn.setEnabled(false);
		if (featureTM.report.length > 0) getFeatureBtn.setEnabled(true);
		else getFeatureBtn.setEnabled(false);
		if (inputTM.report.length > 0)
		{
			if (threadStatus == 0) intGetBtn.setEnabled(true);
			ctrlGetBtn.setEnabled(true);
		}
		else
		{
			intGetBtn.setEnabled(false);
			ctrlGetBtn.setEnabled(false);
		}
		if ((outputTM.report.length > 0) && (cIndex == 2))
		{
			intSetBtn.setEnabled(true);
			ctrlSetBtn.setEnabled(true);
		}
		else
		{
			intSetBtn.setEnabled(false);
			ctrlSetBtn.setEnabled(false);
		}
	}

}

class ReportTM extends AbstractTableModel
{
	final public static boolean Editable = true;
	final public static boolean NonEditable = false;

	static String[] columnTitles = { "", "-0-", "-1-", "-2-", "-3-", "-4-", "-5-", "-6-", "-7-" };
	public byte[] report = new byte[0];
	boolean editable;

	public ReportTM(boolean isEditable)
	{
		editable = isEditable;
	}

	public int getRowCount()
	{
		if (report.length == 0) return 1;
		return ((report.length - 1) >> 3) + 1;
	}

	public int getColumnCount() 
	{ 
		return 9;
	}

	public Object getValueAt(int row, int column)
	{
		if (column == 0)	// column 0 is no data, it is address label
		{
			String str = Integer.toHexString(row * 8).toUpperCase();
			str = "0" + str;
			return str.substring(str.length() - 2) + ":";
		}
		int pos = row * 8 + column - 1;
		if (pos < report.length) return new Byte(report[pos]);
		else return new Integer(0);
	}

	public String getColumnName(int column)
	{
		return columnTitles[column];
	}

	public Class getColumnClass(int column)
	{
		if (column == 0) return String.class;
		return Byte.class;
	}

	public boolean isCellEditable(int row, int column)
	{
		if (column == 0) return false;
		int pos = row * 8 + column - 1;
		if (pos < report.length) return editable;
		else return false;
	}

	public void setValueAt(Object value, int row, int column)
	{
		report[row * 8 + column - 1] = ((Byte)value).byteValue();
	}
}

class HexField extends JTextField implements KeyListener, FocusListener
{
	String hex = "0123456789ABCDEF";
	int size;

	public void keyTyped(KeyEvent e)
	{
		char c = e.getKeyChar();
		if (c == KeyEvent.VK_BACK_SPACE)
		{
			setText("0" + getText());
			setCaretPosition(size);
		}
		if (c == KeyEvent.VK_DELETE)
		{
			if (size == 4) setText("0000");
			else setText("00");
			setCaretPosition(size);
		}
		int i = hex.indexOf(c);
		if (i >= 0)
		{
			setText(getText().substring(1));
			setCaretPosition(size - 1);
		}
	}
	public void keyPressed(KeyEvent e) { }
	public void keyReleased(KeyEvent e) { }
	public void focusLost(FocusEvent e) { }
	public void focusGained(FocusEvent e)	// Do you know how JTable passes a key to the Editor
	{										// before surrendering focus to it?
		String str = getText();
		if (str.length() == size) return;	// Stupid enough, it appends the key to the Editor's Text.
		str = str.toUpperCase();
		if (str.length() != size + 1) setText("00000");
		int i = hex.indexOf(str.charAt(str.length() - 1));
		if (i < 0) setText(str.substring(0, size));
		else setText(str.substring(str.length() - size));
	}

	public int getValue()
	{
		return Integer.parseInt(getText(), 16);
	}

	public void setValue(int i)
	{
		String str = Integer.toHexString(i).toUpperCase();
		str = "000" + str;
		str = str.substring(str.length() - size);
		setText(str);
	}

	public HexField(int v, int s)
	{
		addKeyListener(this);
		addFocusListener(this);
		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				setCaretPosition(size);
			}
			public void mousePressed(MouseEvent e)
			{
				setCaretPosition(size);
			}
			public void mouseReleased(MouseEvent e)
			{
				setCaretPosition(size);
			}
		});
		size = s;
		setValue(v);
		setHorizontalAlignment(JTextField.CENTER);
		setCaretPosition(size);
	}

	protected void processKeyEvent(KeyEvent e)
	{
		char c = e.getKeyChar();
		c = Character.toUpperCase(c);
		e.setKeyChar(c);
		int cd = e.getKeyCode();
		if ((c == KeyEvent.VK_DELETE) || (c == KeyEvent.VK_ENTER))
		{
			super.processKeyEvent(e);
			return;
		}
		if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_ESCAPE))
		{
			super.processKeyEvent(e);
			return;
		}
		if (((c >= '0') && (c <= '9')) || ((c >= 'A') && (c <= 'F')))
		{
			if (getCaretPosition() > 0) super.processKeyEvent(e);
			return;
		}
	}
}

class HexRenderer extends DefaultTableCellRenderer
{
	public HexRenderer()
	{
		super();
		setHorizontalAlignment(JLabel.CENTER);
	}

	public void setValue(Object value)
	{
		if (value.getClass() != Byte.class)
		{
			setText("");
			return;
		}
		int i = ((Byte)value).intValue();
		if (i < 0) i += 256;
		String str = "0" + Integer.toHexString(i).toUpperCase();
		setText(str.substring(str.length() - 2));
	}
}

class HexEditor extends AbstractCellEditor
						 implements TableCellEditor, ActionListener
{
	HexField hexField;

	public HexEditor(int i)
	{
		hexField = new HexField(0, i);
		hexField.addActionListener(this);
	}

	//Implement the one CellEditor method that AbstractCellEditor doesn't.
	public Object getCellEditorValue()
	{
		int i = hexField.getValue();
		if (i > 127) i -= 256;
		return new Byte((byte)i);
	}

	public void actionPerformed(ActionEvent e)
	{
		fireEditingStopped(); 
	}

	//Implement the one method defined by TableCellEditor.
	public Component getTableCellEditorComponent(JTable table,
												 Object value,
												 boolean isSelected,
												 int row,
												 int column)
	{
		hexField.setValue(((Byte)value).intValue());
		return hexField;
	}
}

class MyHID
{
	public native int ReadFeatureReportSize();
	public native int ReadInputReportSize();
	public native int ReadOutputReportSize();
	public native byte[] ReadFeatureReport();
	public native byte[] IntReadInputReport();
	public native byte[] CtrlReadInputReport();
	public native boolean SendFeatureReport(byte[] featureReport);
	public native boolean IntSendOutputReport(byte[] outputReport);
	public native boolean CtrlSendOutputReport(byte[] outputReport);
	public native boolean FindTheHID(int vendorID, int productID);
	public native void CloseHandles();
	static
	{
		System.loadLibrary("Wrapper");
	}
}
