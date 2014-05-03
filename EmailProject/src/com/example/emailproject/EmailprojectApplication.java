package com.example.emailproject;

import com.google.gwt.user.client.ui.DialogBox;
import com.sun.mail.handlers.multipart_mixed;
import com.vaadin.Application;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import java.awt.font.LineBreakMeasurer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import javax.management.Notification;
import javax.swing.JFileChooser;
import javax.activation.*;

import org.apache.james.mime4j.message.Header;
import org.apache.tools.ant.taskdefs.LoadFile;

/**
 * Main application class.
 */
@SuppressWarnings("serial")
public class EmailprojectApplication extends Application implements ClickListener, 
																	Upload.Receiver,
																	Upload.SucceededListener,
																	Upload.FailedListener{
	private Button btn;
	private Button btnAttach;
	private TextArea textmsg;
	private TextField toAddress;
	private TextField subj;
	private Label lb1;
	private Label lblAttach;
	File f;	
	private Upload upload;
    	
	private Window mainWindow;
	
	
	private VerticalLayout mainLayout;
	
	String fname;
	
	@Override
	public void init() {
		mainWindow = new Window("Emailproject Application");
		lb1 = new Label("Email");
	    toAddress = new TextField("TO");
	    subj = new TextField("SUBJECT");
	    
	    textmsg = new TextArea("Message");
	    textmsg.setRows(15);
	    textmsg.setColumns(50);
	    btn = new Button("Send");	    
//	    btnAttach = new Button("Attach");
	    
	    
	    
	    final Upload upload = new Upload("Upload the file here", this);

        // Use a custom button caption instead of plain "Upload".
        upload.setButtonCaption("Upload Now");
        upload.addListener((Upload.SucceededListener) this);
        upload.addListener((Upload.FailedListener) this);
		
		mainLayout = new VerticalLayout();		
		mainLayout.addComponent(lb1);
		mainLayout.addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
		mainLayout.addComponent(toAddress);
		mainLayout.addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
		mainLayout.addComponent(subj);
		mainLayout.addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
		//mainLayout.addComponent(btnAttach);
		mainLayout.addComponent(upload);
		mainLayout.addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
		mainLayout.addComponent(textmsg);
		mainLayout.addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
		mainLayout.addComponent(btn);		
		
		btn.addListener(this);
			
		
		
		mainWindow.addComponent(mainLayout);
		
		setMainWindow(mainWindow);
	}
	

	
	public void buttonClick(Button.ClickEvent event){
		
		if (event.getButton() == btn){
			btn.setCaption("Send Email");
			try {
				sendMail();
			} catch (MessagingException e) {
		
				e.printStackTrace();
			}	
		}				
	}	
	
	
	public void sendMail() throws MessagingException{
		
		String to = (String) toAddress.getValue();
		String from = "moemoeaung1@gmail.com";
		String subject = (String)subj.getValue(); 
		String text = (String)textmsg.getValue();
		
		
		String host = "smtp.gmail.com";
		final String username = "mmmoemoeaung1";
	    final String password = "mmmoemoeaung1";

	    Properties props = new Properties();
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.port", "587");
		
		Session session = Session.getDefaultInstance(props,
		    new javax.mail.Authenticator(){
			    protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(username, password);
	        }
		 }
	    );
		try{
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);			
			message.setText(text);
			
			
//			String filename = "C:/Users/Public/Pictures/2014-04/To Print/photo Moe 3.jpg";
			String filename = fname;

			DataSource source = new FileDataSource(filename);
			
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("This is attachement");
			
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filename);
			
			 Multipart multipart = new MimeMultipart();
			 multipart.addBodyPart(messageBodyPart);
			 
			 message.setContent(multipart);		 
		
			
			Transport.send(message);
			System.out.println("Sent message successfully....");
			
			mainWindow.showNotification("Your message has been sent.");
			
			toAddress.setValue(" ");
			subj.setValue("");
			textmsg.setValue("");				
		
			
		}catch(RuntimeException re){
			String err = re.toString();
			mainWindow.showNotification(err);
			re.printStackTrace();
		}	
	}



	public void uploadFailed(FailedEvent event) {
		System.out.println("uploadFailed");		
	}



	public void uploadSucceeded(SucceededEvent event) {
		System.out.println("uploadSucceeded");
		fname = event.getFilename();	
		
		lblAttach = new Label();		
		lblAttach.setCaption(fname);
		mainLayout.addComponent(lblAttach);
		
	}



	public OutputStream receiveUpload(String filename, String mimeType) {
		FileOutputStream fos = null; // Output stream to write to
      //  f = new File("/tmp/uploads/" + filename);
		f = new File(filename);
        try {
            // Open the file for writing.
            fos = new FileOutputStream(f);
        } catch (final java.io.FileNotFoundException e) {
            // Error while opening the file. Not reported here.
            e.printStackTrace();
            return null;
	    }
		return fos;
	}
        

}

