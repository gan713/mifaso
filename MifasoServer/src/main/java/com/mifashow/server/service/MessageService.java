package com.mifashow.server.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mifashow.server.domain.Constance.REPORTTYPE;
import com.mifashow.server.domain.Constance.USERTYPE;
import com.mifashow.server.domain.Message;
import com.mifashow.server.domain.Constance;
import com.mifashow.server.domain.Posting;
import com.mifashow.server.domain.ResponseException;
import com.mifashow.server.domain.Salon;
import com.mifashow.server.domain.User;
import com.mifashow.server.domain.Constance.BOOKINGSTATUS;
import com.mifashow.server.domain.Constance.MESSAGETYPE;

@Service("messageService")
@Scope("singleton")
public class MessageService {
	JdbcTemplate w_jdbc,r_jdbc;
	@Autowired
	UserService userService;
	@Autowired
	EmailService emailService;
	@Autowired
	SalonService salonService;
	String baseSql="select m.*,fu.userName as fromUserName,fu.figure as fromUserFigure,tu.userName as toUserName,tu.figure as toUserFigure from message m left join user fu on m.fromUserId=fu.userId left join user tu on m.toUserId=tu.userId ";

	public MessageService() {}

	@Autowired
	public void setDataSource(BasicDataSource w_dataSource,BasicDataSource r_dataSource) {
		this.w_jdbc = new JdbcTemplate(w_dataSource);
		this.r_jdbc = new JdbcTemplate(r_dataSource);
		w_jdbc.execute("create table if not exists message (messageId int auto_increment primary key,messageType enum('CHAT','BOOK','COMMIT','COMMENT','EXPLAIN','ARBITRATE','CANCEL','REPORT','FOLLOW','INVITE','JOIN') not null default 'CHAT',fromUserId long not null,toUserId long not null,content varchar(280),photos varchar(1024),createTime long not null,objectId long,objectValues varchar(280),checked boolean not null default 0,processed boolean not null default 1)");
	}
	public Message create(String sign,final Message m) throws ResponseException{
		long toUserId=m.getToUserId();
		if(m.getMessageType()==Constance.MESSAGETYPE.REPORT){
			toUserId=r_jdbc.query("select a.userId from authority a where a.role in ('ROLE_REPORTMAN','ROLE_ADMIN') order by a.role DESC limit 1",new ResultSetExtractor<Long>(){

				public Long extractData(ResultSet arg0) throws SQLException,
						DataAccessException {
					long l=0;
					if(arg0.next()){
						l=arg0.getLong(1);
					}
					return l;
				}
				
			});
		}else if(m.getMessageType()==MESSAGETYPE.ARBITRATE){
			toUserId=r_jdbc.query("select a.userId from authority a where a.role in ('ROLE_ARBITRATEMAN','ROLE_ADMIN') order by a.role DESC limit 1",new ResultSetExtractor<Long>(){

				public Long extractData(ResultSet arg0) throws SQLException,
						DataAccessException {
					long l=0;
					if(arg0.next()){
						l=arg0.getLong(1);
					}
					return l;
				}
				
			});
		}else if(m.getToUserId()==-1){
			toUserId=r_jdbc.query("select a.userId from authority a where a.role in ('ROLE_HELPMAN','ROLE_ADMIN') order by a.role DESC limit 1",new ResultSetExtractor<Long>(){

				public Long extractData(ResultSet arg0) throws SQLException,
						DataAccessException {
					long l=0;
					if(arg0.next()){
						l=arg0.getLong(1);
					}
					return l;
				}
				
			});
		}
		if(toUserId==0){
			throw new ResponseException("No reportman or administrator.",HttpStatus.NOT_ACCEPTABLE);
		}else{
			m.setToUserId(toUserId);
		}
		User user=userService.getBySign(sign);
		if(m.getFromUserId()!=user.getUserId()){
			throw new ResponseException("fromUserId",HttpStatus.NOT_ACCEPTABLE);
		}
		final String sql = "insert into message (messageType,fromUserId,toUserId,createTime,content,photos,objectId,objectValues,processed) values (?,?,?,?,?,?,?,?,?)";
		long messageId;
		try{
			KeyHolder kh=new GeneratedKeyHolder();
			w_jdbc.update(new PreparedStatementCreator(){

				public PreparedStatement createPreparedStatement(Connection connection)
						throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql);
					PreparedStatementSetter pss=new PreparedStatementSetter(){

						public void setValues(PreparedStatement ps) throws SQLException {
							ps.setString(1, m.getMessageType().name());
							ps.setLong(2, m.getFromUserId());
							ps.setLong(3, m.getToUserId());
							ps.setLong(4, Calendar.getInstance().getTimeInMillis());
							ps.setString(5, m.getContent());
							ps.setString(6, m.getPhotos());
							ps.setLong(7, m.getObjectId());
							ps.setString(8, m.getObjectValues());
							ps.setBoolean(9, m.getMessageType()!=MESSAGETYPE.REPORT&& m.getMessageType()!=MESSAGETYPE.ARBITRATE&& m.getMessageType()!=MESSAGETYPE.INVITE&& m.getMessageType()!=MESSAGETYPE.JOIN);
						}};
						pss.setValues(ps);
						return ps;
				}}, kh);
			messageId=kh.getKey().longValue();
//		jdbc.update(sql, new PreparedStatementSetter() {
//			public void setValues(PreparedStatement ps) throws SQLException {
//				ps.setString(1, m.getMessageType().name());
//				ps.setLong(2, m.getFromUserId());
//				ps.setLong(3, m.getToUserId());
//				ps.setLong(4, Calendar.getInstance().getTimeInMillis());
//				ps.setString(5, m.getContent());
//				ps.setLong(6, m.getBookingId());
//			}
//
//		});
	}catch(Exception e){
		e.printStackTrace();
		throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
	}
		if(messageId!=0){
			return this.getById(messageId);			
		}
		return null;
	}
	public Message report(String sign,Posting posting,REPORTTYPE reportType) throws ResponseException{
		System.out.println("s_report");
		Message m=new Message();
		User fu=userService.getBySign(sign);
		m.setMessageType(MESSAGETYPE.REPORT);
		m.setFromUserId(fu.getUserId());
		m.setObjectId(posting.getPostingId());
		m.setObjectValues(reportType.name());
		StringBuffer sb=new StringBuffer();
		for(String image:posting.getImages()){
			sb.append(image).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		m.setPhotos(sb.toString());
		return create(sign,m);
	}
	public void inviteStylistBySign(String inviterSign,String inviteeSign) throws ResponseException{
		User inviter=userService.getBySign(inviterSign);
		User invitee=userService.getBySign(inviteeSign);
		if(invitee==null){
			throw new ResponseException("User Not Found!",HttpStatus.NOT_FOUND);
		}
		if(invitee.getUserType()!=USERTYPE.STYLIST){
			throw new ResponseException("The user is not a stylist!",HttpStatus.NOT_ACCEPTABLE);
		}
		if(!inviter.isSalonManager()||inviter.getSalonId()==0){
			throw new ResponseException("You're not a salon manager!",HttpStatus.UNAUTHORIZED);
		}
		Message message=new Message();
		message.setMessageType(MESSAGETYPE.INVITE);
		message.setFromUserId(inviter.getUserId());
		message.setToUserId(invitee.getUserId());
		message.setObjectId(inviter.getSalonId());
		message.setObjectValues(inviter.getSalonName());
		message.setPhotos(inviter.getSalonImages());
		create(inviterSign,message);
	}
	public void acceptInviteById(String sign,long messageId) throws ResponseException{
		Message m=getById(messageId);
		User u=userService.getBySign(sign);
		if(u.getUserId()!=m.getToUserId()){
			throw new ResponseException("You are not the invitee!",HttpStatus.NOT_ACCEPTABLE);
		}
		if(u.getSalonId()!=0){
			throw new ResponseException("You have joined another salon!",HttpStatus.CONFLICT);
		}
		salonService.join(u.getUserId(), m.getObjectId());
		w_jdbc.execute("update message set processed=1 where messageId="+messageId);
	}
	public void joinSalonBySign(String sign,long salonId) throws ResponseException{
		Salon salon=salonService.getById(salonId);
		User user=userService.getBySign(sign);
		if(salon==null){
			throw new ResponseException("Salon Not Found!",HttpStatus.NOT_FOUND);
		}
		if(user.getUserType()!=USERTYPE.STYLIST){
			throw new ResponseException("You are not a stylist!",HttpStatus.NOT_ACCEPTABLE);
		}
		Message message=new Message();
		message.setMessageType(MESSAGETYPE.JOIN);
		message.setFromUserId(user.getUserId());
		message.setToUserId(salon.getManagerId());
		message.setObjectId(salonId);
		message.setObjectValues(salon.getName());
		create(sign,message);
	}
	public void acceptJoinById(String sign,long messageId) throws ResponseException{
		Message m=getById(messageId);
		Salon salon=salonService.getByManagerSign(sign);
		User stylist=userService.getById(m.getFromUserId());
		if(stylist.getUserType()!=USERTYPE.STYLIST||stylist.getSalonId()!=0){
			throw new ResponseException("You are not a stylist or you have joined a salon!",HttpStatus.NOT_ACCEPTABLE);
		}
		if(salon==null||salon.getSalonId()!=m.getObjectId()){
			throw new ResponseException("You're not the manager of the salon!",HttpStatus.UNAUTHORIZED);
		}
		salonService.join(stylist.getUserId(), m.getObjectId());
		w_jdbc.execute("update message set processed=1 where messageId="+messageId);
	}
	public Message getById(long messageId) throws ResponseException{
		Message message = null;
		try{
			message=r_jdbc.queryForObject(baseSql+"where m.messageId=?", new Long[]{messageId},new MessageMapper());
		}catch(EmptyResultDataAccessException e){
			throw new ResponseException("No matching message found!",HttpStatus.NOT_FOUND);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return message;
	}
	
	public List<Message> getBySign(String sign,long fromUserId) throws ResponseException{
		User user = userService.getBySign(sign);
		List<Message> messages;
		String sql = "";
		if(fromUserId==-1){
			sql+=baseSql+"where m.toUserId="+user.getUserId()+" and m.checked=0";
		}else{
			sql+=baseSql+"where (m.toUserId="+user.getUserId()+" and m.fromUserId="+fromUserId+") or (m.fromUserId="+user.getUserId()+" and m.toUserId="+fromUserId+")";
		}
		try{
//			System.out.println(sql);
		messages=r_jdbc.query(sql, new MessageMapper());
//		StringBuilder sb = new StringBuilder();
//		for(Message message:messages){
//			sb.append(message.getMessageId()).append(",");
//		}
		if(fromUserId==-1){
			w_jdbc.update("update message set checked=1 where toUserId=?",user.getUserId());
//			String deleteIds=sb.deleteCharAt(sb.length()-1).toString();
//			System.out.println("delete from message where messageId in ("+deleteIds+")");
//			jdbc.execute("delete from message where messageId in ("+deleteIds+")");
		}else{
			w_jdbc.update("update message set checked=1 where toUserId=? and fromUserId=?",user.getUserId(),fromUserId);
		}
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return messages;
		
	}
	public void handleMessage(long messageId){
		w_jdbc.update("update message set processed=1 where messageId=?", messageId);
	}
	@Scheduled(cron="0 0 3 * * ?")
	public void deleteOldMessage(){
		w_jdbc.execute("delete from message where checked=1 and unix_timestamp(now())*1000-createTime>2592000000");
	}
	@Scheduled(cron="0 0 18 * * ?")
	public void emailMessage(){
		String sql="select m.messageType as messageType,m.content as messageContent,fu.userName as fromUserName,fu.figure as fromUserFigure,tu.userName as toUserName,tu.sign as toUserSign from message m left join user fu on m.fromUserId=fu.userId left join user tu on m.toUserId=tu.userId where m.checked=0 and unix_timestamp(now())*1000-m.createTime>1*24*60*60*1000 and unix_timestamp(now())*1000-m.createTime<2*24*60*60*1000";
		r_jdbc.query(sql ,new ResultSetExtractor<String>(){

			public String extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				while(rs.next()){
					String messageType=rs.getString("messageType");
					String fromUserName=rs.getString("fromUserName");
					String fromUserFigure=rs.getString("fromUserFigure");
					String toUserName=rs.getString("toUserName");
					String toUserSign=rs.getString("toUserSign");
					String imgUrl = null;
					String content_zh = null,content_en = null;
					if(MESSAGETYPE.valueOf(messageType)==MESSAGETYPE.CHAT){
						imgUrl=fromUserFigure;
						content_zh="尊敬的"+toUserName+"：<br>收到来自"+fromUserName+"的消息";
						content_en=toUserName+":<br>Your have got a message from "+fromUserName;
						}else if(MESSAGETYPE.valueOf(messageType)==MESSAGETYPE.BOOK){
						imgUrl=fromUserFigure;
						content_zh="尊敬的"+toUserName+"：<br>"+fromUserName+"预约了你。";
						content_en=toUserName+":<br>"+fromUserName+" commited a reservation.";
						}else if(MESSAGETYPE.valueOf(messageType)==MESSAGETYPE.COMMIT){
							imgUrl=fromUserFigure;
							content_zh="尊敬的"+toUserName+"：<br>"+fromUserName+"同意了你的预约。";
							content_en=toUserName+":<br>"+fromUserName+" accepted your reservation.";
						}else if(MESSAGETYPE.valueOf(messageType)==MESSAGETYPE.COMMENT){
							imgUrl=fromUserFigure;
							content_zh="尊敬的"+toUserName+"：<br>"+fromUserName+"评价了你的服务。";
							content_en=toUserName+":<br>"+fromUserName+" evaluate your service.";
						}else if(MESSAGETYPE.valueOf(messageType)==MESSAGETYPE.EXPLAIN){
							imgUrl=fromUserFigure;
							content_zh="尊敬的"+toUserName+"：<br>"+fromUserName+"对你的评价作出了解释。";
							content_en=toUserName+":<br>"+fromUserName+" explain for your evaluation.";
						}else if(MESSAGETYPE.valueOf(messageType)==MESSAGETYPE.ARBITRATE){
							imgUrl=fromUserFigure;
							content_zh="尊敬的"+toUserName+"：<br>"+fromUserName+"发起了申诉。";
							content_en=toUserName+":<br>"+fromUserName+" appealed。";
						}else if(MESSAGETYPE.valueOf(messageType)==MESSAGETYPE.FOLLOW){
							imgUrl=fromUserFigure;
							content_zh="尊敬的"+toUserName+"：<br>"+fromUserName+"关注了你。";
							content_en=toUserName+":<br>"+fromUserName+" followed you。";
						}else if(MESSAGETYPE.valueOf(messageType)==MESSAGETYPE.REPORT){
							imgUrl=fromUserFigure;
							content_zh="尊敬的"+toUserName+"：<br>"+fromUserName+"举报有问题发型。";
							content_en=toUserName+":<br>"+fromUserName+" reported。";
						}else if(MESSAGETYPE.valueOf(messageType)==MESSAGETYPE.CANCEL){
							imgUrl=fromUserFigure;
							content_zh="尊敬的"+toUserName+"：<br>"+fromUserName+"取消预约。";
							content_en=toUserName+":<br>"+fromUserName+" canceled the reservation。";
						}
					try {
						emailService.send(toUserSign,"Mifashow",imgUrl,content_zh,content_en);
					} catch (ResponseException e) {
						e.printStackTrace();
					}
				}
				return null;
			}
			
		});
	}
	private static final class MessageMapper implements RowMapper<Message> {

		public Message mapRow(ResultSet rs, int arg1) throws SQLException {
			Message m=new Message();
			m.setMessageId(rs.getLong("messageId"));
			m.setMessageType(MESSAGETYPE.valueOf(rs.getString("messageType")));
			m.setFromUserId(rs.getLong("fromUserId"));
			m.setFromUserName(rs.getString("fromUserName"));
			m.setFromUserFigure(rs.getString("fromUserFigure"));
			m.setToUserId(rs.getLong("toUserId"));
			m.setToUserName(rs.getString("toUserName"));
			m.setToUserFigure(rs.getString("toUserFigure"));
			m.setCreateTime(rs.getLong("createTime"));
			m.setContent(rs.getString("content"));
			m.setObjectId(rs.getLong("objectId"));
			m.setObjectValues(rs.getString("objectValues"));
			m.setPhotos(rs.getString("photos"));
			m.setProcessed(rs.getBoolean("processed"));
			if(m.getMessageType()==MESSAGETYPE.BOOK){
				try{
				String[] ovs=m.getObjectValues().split(",");
				m.setProcessed(!BOOKINGSTATUS.COMMITTING.name().equals(ovs[0])||Long.valueOf(ovs[5])<=Calendar.getInstance().getTimeInMillis());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			return m;
		}
		
	}


}
