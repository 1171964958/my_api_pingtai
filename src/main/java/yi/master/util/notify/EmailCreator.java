package yi.master.util.notify;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public interface EmailCreator {
	
	MimeMessage createMessage(Session session, String sendAddress, Address[] receiveAddresses, Address[] copyAddresses) throws Exception;
	
}
