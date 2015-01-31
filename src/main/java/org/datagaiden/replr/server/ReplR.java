package org.datagaiden.replr.server;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.datagaiden.replr.monitor.MessageMonitor;
import org.rosuda.JRI.Rengine;
import org.zeromq.ZMQ;

public class ReplR {
	
	static Logger log = Logger.getLogger(ReplR.class.getName());
	public MessageMonitor messageMonitor;
	

	
	public static void main(String[] args) throws Exception
	{
		
		
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket responder = context.socket(ZMQ.REP);
        //  Socket to talk to clients
        ReplR replr = new ReplR(responder);
        responder.bind("tcp://*:5555");
        log.info("Starting ReplR message queue");
        while (!Thread.currentThread().isInterrupted()) {
            // Wait for next request from the client
            byte[] request = responder.recv(0);
            log.info("Received message: " + new String(request));
            replr.messageMonitor.setSendMessage(new String(request));    
            byte[] received = replr.messageMonitor.getReceivedMessage();
            byte[] mimetype = Arrays.copyOfRange(received, 0, 4);
    		byte[] mimearray = new byte[] { (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47};
    		if (Arrays.equals(mimearray, mimetype)) { 
    		log.info("Sending plot image");	
    		}
    		else
    		{
    		log.info("Sending message: " + new String(received));	      
            }
        responder.send(received, 0);
        }
        
        responder.close();
        context.term();
        
	}
	
    public ReplR(ZMQ.Socket responder) {
	    log.info("Starting R Session");
	    
		if (!Rengine.versionCheck()) {
		    System.err.println("** Version mismatch - Java files don't match library version.");
		    System.exit(1);
		}
		
	        this.messageMonitor = new MessageMonitor();
	        TextConsole textconsole = new TextConsole(messageMonitor,responder);
			String[] args = null;
			Rengine re=new Rengine(args , false, textconsole);
			// the engine creates R is a new thread, so we should wait until it's ready
	        if (!re.waitForR()) {
	            System.out.println("Cannot load R");
	            return;
	        }

		if (true) {
		    re.startMainLoop();
		    log.info("R Session Started");
		}
		
    }
}

