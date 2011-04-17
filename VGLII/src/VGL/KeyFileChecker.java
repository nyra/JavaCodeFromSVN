package VGL;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.jdom.Document;
import org.jdom.Element;

public class KeyFileChecker {

	public static PrivateKey checkGradingKeys(VGLII vglII) {
		File graderTokenFile = new File("grader.key");
		if (!graderTokenFile.exists()) {
			return null;
		} else {
			Document doc = EncryptionTools.readRSAEncrypted(graderTokenFile);
			List<Element> elements = doc.getRootElement().getChildren(); 
			Iterator<Element> elIt = elements.iterator();
			Date date = null;
			String b64cryptPW = null;
			while (elIt.hasNext()) {
				Element e = elIt.next();
				String name = e.getName();

				if (name.equals("ExpDate")) {
					SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
					try {
						date = sdf.parse(e.getText());
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
				}

				if (name.equals("Pswd")) {
					b64cryptPW = e.getText();
				}
			}

			if ((date == null) || (b64cryptPW == null)) {
				return null;
			} else {
				Date today = new Date();
				if (today.compareTo(date) > 0) {
					JOptionPane.showMessageDialog(vglII, 
							"<html>Your grading token (grader.key) has expired<br>"
							+ "on " + date.toString() + ".<br>"
							+ "You will not be able to grade VGLII problems.<br>"
							+ "You should contact Brian.White@umb.edu for a new one.",
							"grader.key expired",
							JOptionPane.WARNING_MESSAGE);
					return null;
				} else {
					JPanel panel = new JPanel();
					panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
					final JPasswordField pwf = new JPasswordField(15);
					panel.add(new JLabel(
							"<html>Your grading token is valid.<br>"
							+ "It will expire on " + date.toString() + ".<br>"
							+ "Enter your grading password below:"));
					panel.add(pwf);
					pwf.addAncestorListener(new AncestorListener() {
						public void ancestorAdded(AncestorEvent event) {
							pwf.requestFocusInWindow();
						}
						public void ancestorMoved(AncestorEvent event) {}
						public void ancestorRemoved(AncestorEvent event) {}
					});

					int action = JOptionPane.showConfirmDialog(vglII,
							panel,
							"Enter grading password",
							JOptionPane.OK_CANCEL_OPTION);
					if (action != JOptionPane.OK_OPTION) {
						return null;
					} else {
						String enteredPwd = new String(pwf.getPassword());
						MessageDigest md = null;
						try {
							md = MessageDigest.getInstance("SHA-1");
							md.update(enteredPwd.getBytes());
						} catch (Exception e) {
							JOptionPane.showMessageDialog(vglII, 
									"Unable to test your password due to system error",
									"Error",
									JOptionPane.WARNING_MESSAGE);
						}
						byte[] raw = md.digest();
						String hash = new String(Base64Coder.encode(raw));
						if (!hash.equals(b64cryptPW)) {
							JOptionPane.showMessageDialog(vglII, 
									"Password Incorrect; grading disabled.",
									"Incorrect Password",
									JOptionPane.WARNING_MESSAGE);
							return null;
						} else {
							// get the grading key "instructor.key"
							File instructorKeyFile = new File("instructor.key");
							if (!instructorKeyFile.exists()) {
								JOptionPane.showMessageDialog(vglII, 
										"Cannot find instructor.key; grading disabled.",
										"Missing Key file",
										JOptionPane.WARNING_MESSAGE);
								return null;
							} else {
								try {
									return EncryptionTools.readPrivateKeyFromFile("instructor.key");
								} catch (IOException e) {
									e.printStackTrace();
									return null;
								}
							}
						}
					}
				}
			}
		}
	}

	public static PublicKey checkSaveForGradingKey() {
		PublicKey result = null;
		File studentKeyFile = new File("student.key");
		if (!studentKeyFile.exists()) return result;
		try {
			result = EncryptionTools.readPublicKeyFromFile("student.key");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
