package client;

import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class UI extends JFrame {

	/**
	 * UI elements to show textfields and button to run the program
	 */
	JTextField txtHostName;
	JTextField txtPortNumber;
	JTextField txtResourceID;
	JButton btnStartRequest;
	JLabel lblHostName;
	JLabel lblPort;
	JLabel lblPortWarning;
	JLabel lblHostNameWarning;
	JLabel lblResourceID;

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public UI() {
		// This gets the current screen width and height and centers the GUI on
		// the users screen
		GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getScreenDevices();
		int screenWidth = gs[0].getDisplayMode().getWidth();
		int screenHeight = gs[0].getDisplayMode().getHeight();
		int x = (screenWidth - Constants.WINDOW_WIDTH) / 2;
		int y = (screenHeight - Constants.WINDOW_HEIGHT) / 2;

		/**
		 * sets the size from the Constants class and centers the JFrame in the current window
		 */
		this.setBounds(x, y, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
		this.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(Constants.WINDOW_RESIZEABLE);

		initTextFieldsAndButtons();
		
	}

	/**
	 * This method initializes the textfields to their correct lengths and buttons
	 * with the correct titles and adds the OnClickListener or OnCaretListener for 
	 * text fields.  The components are added to the JFrame as well using a GridBagLayout
	 * 
	 * The program will not run without checking to make sure the port number and host name
	 * entered is valid.
	 */
	public void initTextFieldsAndButtons() {
		JPanel outerPanel = new JPanel();
		outerPanel.setLayout(new FlowLayout());

		txtHostName = new JTextField(15);
		txtHostName.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				validateHostName();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				validateHostName();
			}
		});
		txtHostName.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ENTER
						|| event.getKeyChar() == '\n') {
					validateHostName();
				}
			}
		});
		txtPortNumber = new JTextField(5);
		txtPortNumber.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent arg0) {
				validatePortNumber();
			}
		});

		txtResourceID = new JTextField(20);

		btnStartRequest = new JButton("Start Request");
		btnStartRequest.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (!lblPortWarning.isVisible()
						&& !lblHostNameWarning.isVisible()
						&& txtPortNumber.getText().length() > 0
						&& txtHostName.getText().length() > 0) {
					if (!UI.this.validateHostName() && !UI.this.validatePortNumber()) {
						String resource = txtResourceID.getText();
						try {
							if (resource.charAt(0) != '/') {
								resource = "/"+resource;
							}
						} catch (StringIndexOutOfBoundsException sioobe) {
							resource = "/";
						}
						HttpClient.startRequest(txtHostName.getText(),
								Integer.parseInt(txtPortNumber.getText()),
								resource);
					}
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});

		lblHostName = new JLabel("Host name: ");
		lblPort = new JLabel("Port number: ");
		lblResourceID = new JLabel("Resource ID: ");
		lblPortWarning = new JLabel("The port number may only contain numbers.");
		lblPortWarning.setVisible(false);
		lblHostNameWarning = new JLabel(
				"The host name must be a valid ip address or of form http://www.example.com");
		lblHostNameWarning.setVisible(false);

		GridBagConstraints gbc = new GridBagConstraints();

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		panel.add(lblHostName, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		panel.add(txtHostName, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		panel.add(lblHostNameWarning, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		panel.add(lblPort, gbc);

		gbc.gridx = 1;
		panel.add(txtPortNumber, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		panel.add(lblPortWarning, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		panel.add(lblResourceID, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 3;
		panel.add(txtResourceID, gbc);

		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		panel.add(btnStartRequest, gbc);

		outerPanel.add(panel);
		this.getContentPane().add(outerPanel);
	}

	/**
	 * @return true if the host name is invalid, false = all ok
	 */
	public boolean validateHostName() {
		String hostName = txtHostName.getText();
		try {
			InetAddress.getByName(hostName);
			lblHostNameWarning.setVisible(false);
			return false;
		} catch (UnknownHostException e) {
			lblHostNameWarning.setVisible(true);
			return true;
		}
	}

	/**
	 * @return true if the port number is invalid, false = all ok
	 */
	public boolean validatePortNumber() {
		boolean invalid = false;
		for (char c : txtPortNumber.getText().toCharArray()) {
			if (!Character.isDigit(c)) {
				invalid = true;
			}
		}
		lblPortWarning.setVisible(invalid);
		return invalid;
	}

}
