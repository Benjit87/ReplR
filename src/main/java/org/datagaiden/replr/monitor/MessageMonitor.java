package org.datagaiden.replr.monitor;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;

public class MessageMonitor{
	
	static Logger log = Logger.getLogger(MessageMonitor.class.getName());
	private String message;
	private boolean send;
	private boolean received;
	private boolean complete;
	
	public MessageMonitor()
	{
		this.send = false;
		this.received = false;
		this.complete = false;
	}

	public synchronized String getSendMessage() throws Exception {
		this.complete = true;
		while (!this.send) {this.notify();this.wait();}
		log.debug("Getting Send Message: " + message +"]");
		this.send = false;
		String messageHolder = message;
		message = "";
		return messageHolder;
	}

	public synchronized byte[] getReceivedMessage() throws Exception {
		while (!(this.received && this.complete)) {this.wait();}
		log.debug("Getting Received Message: " + message);
		this.received = false;
		String messageHolder = message;
		message = "";
		if (messageHolder.equals("Processing Plot")) { return convertImg(); }
		return messageHolder.getBytes();
	}
	
	public synchronized void setSendMessage(String message) throws Exception {
		log.debug("Set Send Message: " + message);
		this.message = message;
		this.send = true;
		this.received = false;
		this.notifyAll();
	}
	
	public synchronized void setRecieveMessage(String message) throws Exception
	{
		log.debug("Getting Received Message: " + message);
		this.complete = false;
		this.message += message;
		this.send = false;
		this.received = true;

	}
	
	private byte[] convertImg()
	{	
		log.debug("Converting Image File");
		FileInputStream fileInputStream=null;
		File file = new File("plot.png");
        
        byte[] bFile = new byte[(int) file.length()];
 
        try {
            //convert file into array of bytes
	    fileInputStream = new FileInputStream(file);
	    fileInputStream.read(bFile);
	    fileInputStream.close();
 
 
        }catch(Exception e){
        	e.printStackTrace();
        }
		return bFile;
	}


}
