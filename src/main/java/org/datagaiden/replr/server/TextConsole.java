package org.datagaiden.replr.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.datagaiden.replr.monitor.MessageMonitor;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;
import org.zeromq.ZMQ;

class TextConsole implements RMainLoopCallbacks
{
	static Logger log = Logger.getLogger(TextConsole.class.getName());
	private MessageMonitor messageMonitor;
	private ZMQ.Socket responder;
	private boolean inWriteConsole;
	public MessageMonitor getrMessageReceiver() {
		return messageMonitor;
	}

	public TextConsole(MessageMonitor rMessageReceiver, ZMQ.Socket responder)
	{
		this.messageMonitor = rMessageReceiver;
		this.responder = responder;
		this.inWriteConsole = false;
	}
	
	
    public void rWriteConsole(Rengine re, String text, int oType) {
    	log.debug("Write to console: " + text);
        inWriteConsole = true;
        try {
			messageMonitor.setRecieveMessage(text);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void rBusy(Rengine re, int which) {
        //System.out.println("rBusy("+which+")");
    }
    
    public String rReadConsole(Rengine re, String prompt, int addToHistory) {
    	log.debug("Prompt: " + prompt);
        try {
        	
        	if (!inWriteConsole) {
        		inWriteConsole = false; 
        		messageMonitor.setRecieveMessage("");
        		}
        	else { inWriteConsole = false; }
        	
        	String s = messageMonitor.getSendMessage();
        	
        	//If plot function is called
        	String pattern = "^plot\\(.*\\)";
        	Pattern p = Pattern.compile(pattern);
        	Matcher m = p.matcher(s);
        	if (m.matches())
        	{
        		log.debug("Plot function is called, returning an image array");
        		
        		REXP workingDirectory = re.eval("getwd()");        		
        		re.eval("png('"+ workingDirectory.asString() + "//plot.png')");
        		re.eval(s);
        		re.eval("dev.off()");
        		s = "cat('Processing Plot')";
        	}
            return (s==null||s.length()==0)?s:s+"\n";
        } catch (Exception e) {
            System.out.println("jriReadConsole exception: "+e.getMessage());
        }
        return null;
    }
    
    public void rShowMessage(Rengine re, String message) {
        //System.out.println("rShowMessage \""+message+"\"");

    }
	
    public String rChooseFile(Rengine re, int newFile) {
	    /*	
		FileDialog fd = new FileDialog(new Frame(), (newFile==0)?"Select a file":"Select a new file", (newFile==0)?FileDialog.LOAD:FileDialog.SAVE);
		fd.show();
		String res=null;
		if (fd.getDirectory()!=null) res=fd.getDirectory();
		if (fd.getFile()!=null) res=(res==null)?fd.getFile():(res+fd.getFile());
		return res;
		*/
	    return null;
    }
    
    public void   rFlushConsole (Rengine re) {
    }
	
    public void   rLoadHistory  (Rengine re, String filename) {
    }			
    
    public void   rSaveHistory  (Rengine re, String filename) {
    }			
}

