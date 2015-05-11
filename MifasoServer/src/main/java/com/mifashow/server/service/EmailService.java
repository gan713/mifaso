package com.mifashow.server.service;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mifashow.server.domain.ResponseException;
//import com.sina.sae.mail.SaeMail;

@Service("emailService")
@Scope("singleton")
public class EmailService {
	private static final String from="mifashow@163.com",password="883809",host="smtp.163.com",port="25";
//	private static final String from="admin@mifashow.com",password="2004,jie",host="smtp.qq.com",port="25";
//	public void send(String to,String subject,String imgUrl,String content_zh,String content_en) throws ResponseException{
//		SaeMail mail = new SaeMail();
//		mail.setFrom(from);
//		mail.setTo(new String[]{to});
//		mail.setSmtpHost(host);
//		mail.setSmtpPort(Integer.valueOf(port));
//		mail.setContentType("HTML");
//		mail.setSmtpUsername(from);
//		mail.setSmtpPassword(password);
//		mail.setSubject(subject);
//		mail.setContent("<p><table cellpadding='20' cellspacing='0' style='border:1px #e53223 dashed;' class='ke-zeroborder'><tbody><tr><td style='background-color:#e53223;'><span style='color:#FFFFFF;font-size:24px;font-family:SimHei;'>美发秀用户中心</span></td></tr><tr><td><table cellpadding='5' class='ke-zeroborder'><tbody><tr><td style='border:1px #e53223 dashed;'><img src='"+imgUrl+"' width='75' height='75' /><br/></td><td><span style='font-size:14px;font-family:SimHei;'>"+content_zh+"</span></td></tr></tbody></table></td></tr><tr><td style='border-top:#E53333 1px dashed;'><a href='http://a.app.qq.com/o/simple.jsp?pkgname=com.mifashow&amp;g_f=992943' target='_blank'>下载最新版</a><br/></td></tr></tbody></table></p><table cellpadding='20' cellspacing='0' style='border:1px #E53333 dashed;' class='ke-zeroborder'><tbody><tr><td style='background-color:#e53223;'><span style='color:#FFFFFF;font-size:24px;font-family:SimHei;'>Mifashow User Center</span></td></tr><tr><td><table cellpadding='5' class='ke-zeroborder'><tbody><tr><td style='border:1px #e53223 dashed;'><img src='"+imgUrl+"' width='75' height='75' /><br/></td><td><span style='font-size:14px;font-family:SimHei;'>"+content_en+"</span></td></tr></tbody></table></td></tr><tr><td style='border-top:#e53223 1px dashed;'><a href='http://a.app.qq.com/o/simple.jsp?pkgname=com.mifashow&amp;g_f=992943' target='_blank'>Download The Lastest Version Of Mifashow!</a><br/></td></tr></tbody></table>");
////		mail.quickSend(from, new String[]{to}, subject, "<p><table cellpadding='20' cellspacing='0' style='border:1px #e53223 dashed;' class='ke-zeroborder'><tbody><tr><td style='background-color:#e53223;'><span style='color:#FFFFFF;font-size:24px;font-family:SimHei;'>美发秀用户中心</span></td></tr><tr><td><table cellpadding='5' class='ke-zeroborder'><tbody><tr><td style='border:1px #e53223 dashed;'><img src='"+imgUrl+"' width='75' height='75' /><br/></td><td><span style='font-size:14px;font-family:SimHei;'>"+content_zh+"</span></td></tr></tbody></table></td></tr><tr><td style='border-top:#E53333 1px dashed;'><a href='http://a.app.qq.com/o/simple.jsp?pkgname=com.mifashow&amp;g_f=992943' target='_blank'>下载最新版</a><br/></td></tr></tbody></table></p><table cellpadding='20' cellspacing='0' style='border:1px #E53333 dashed;' class='ke-zeroborder'><tbody><tr><td style='background-color:#e53223;'><span style='color:#FFFFFF;font-size:24px;font-family:SimHei;'>Mifashow User Center</span></td></tr><tr><td><table cellpadding='5' class='ke-zeroborder'><tbody><tr><td style='border:1px #e53223 dashed;'><img src='"+imgUrl+"' width='75' height='75' /><br/></td><td><span style='font-size:14px;font-family:SimHei;'>"+content_en+"</span></td></tr></tbody></table></td></tr><tr><td style='border-top:#e53223 1px dashed;'><a href='http://a.app.qq.com/o/simple.jsp?pkgname=com.mifashow&amp;g_f=992943' target='_blank'>Download The Lastest Version Of Mifashow!</a><br/></td></tr></tbody></table>", from, password);
//		boolean flag = mail.send();
//		if(!flag){
//			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
//	 }
//	}
	public void send(String to,String subject,String imgUrl,String content_zh,String content_en) throws ResponseException{
		Properties props = new Properties();
		props.put("mail.smtp.ssl.enable", "false" );
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.put("mail.smtp.starttls.enable", "false");
		Session session = Session.getInstance(props, new SimpleAuthenticator(from, password));
        session.setDebug(true);
		try {
		    // create a message
		    MimeMessage msg = new MimeMessage(session);
		    msg.setFrom(new InternetAddress(from));
		    InternetAddress[] address = {new InternetAddress(to)};
		    msg.setRecipients(Message.RecipientType.TO, address);
		    msg.setSubject(subject,"UTF-8");
		    msg.setSentDate(new Date());
		    msg.setContent("<p><table cellpadding='20' cellspacing='0' style='border:1px #E53333 dashed;' class='ke-zeroborder'><tbody><tr><td style='background-color:#E53333;'><span style='color:#FFFFFF;font-size:24px;font-family:SimHei;'>美发秀用户中心</span></td></tr><tr><td><table cellpadding='5' class='ke-zeroborder'><tbody><tr><td style='border:1px #E53333 dashed;'><img src='"+imgUrl+"' width='75' height='75' /><br/></td><td><span style='font-size:14px;font-family:SimHei;'>"+content_zh+"</span></td></tr></tbody></table></td></tr><tr><td style='border-top:#E53333 1px dashed;'><a href='http://a.app.qq.com/o/simple.jsp?pkgname=com.mifashow&amp;g_f=992943' target='_blank'>下载最新版</a><br/></td></tr></tbody></table></p><table cellpadding='20' cellspacing='0' style='border:1px #E53333 dashed;' class='ke-zeroborder'><tbody><tr><td style='background-color:#E53333;'><span style='color:#FFFFFF;font-size:24px;font-family:SimHei;'>Mifashow User Center</span></td></tr><tr><td><table cellpadding='5' class='ke-zeroborder'><tbody><tr><td style='border:1px #E53333 dashed;'><img src='"+imgUrl+"' width='75' height='75' /><br/></td><td><span style='font-size:14px;font-family:SimHei;'>"+content_en+"</span></td></tr></tbody></table></td></tr><tr><td style='border-top:#E53333 1px dashed;'><a href='http://a.app.qq.com/o/simple.jsp?pkgname=com.mifashow&amp;g_f=992943' target='_blank'>Download The Lastest Version Of Mifashow!</a><br/></td></tr></tbody></table>","text/html;charset=UTF-8");
		    Transport.send(msg);
		} catch (Exception mex) {
		    System.out.println("mail exception toString:"+mex.toString());
		    System.out.println("mail exception message:"+mex.getMessage());
		    Exception ex = mex;
		    do {
			if (ex instanceof SendFailedException) {
			    SendFailedException sfex = (SendFailedException)ex;
			    Address[] invalid = sfex.getInvalidAddresses();
			    if (invalid != null) {
				System.out.println("    ** Invalid Addresses");
				for (int i = 0; i < invalid.length; i++) 
				    System.out.println("         " + invalid[i]);
			    }
			    Address[] validUnsent = sfex.getValidUnsentAddresses();
			    if (validUnsent != null) {
				System.out.println("    ** ValidUnsent Addresses");
				for (int i = 0; i < validUnsent.length; i++) 
				    System.out.println("         "+validUnsent[i]);
			    }
			    Address[] validSent = sfex.getValidSentAddresses();
			    if (validSent != null) {
				System.out.println("    ** ValidSent Addresses");
				for (int i = 0; i < validSent.length; i++) 
				    System.out.println("         "+validSent[i]);
			    }
			}
			if (ex instanceof MessagingException)
			    ex = ((MessagingException)ex).getNextException();
			else
			    ex = null;
		    } while (ex != null);
		    throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}  
	     
	public class SimpleAuthenticator extends Authenticator{   
	    String userName=null;   
	    String password=null;   
	        
	    public SimpleAuthenticator(){   
	    }   
	    public SimpleAuthenticator(String username, String password) {    
	        this.userName = username;    
	        this.password = password;    
	    }    
	    @Override
		protected PasswordAuthentication getPasswordAuthentication(){   
	        return new PasswordAuthentication(userName, password);   
	    }   
	}   
}
