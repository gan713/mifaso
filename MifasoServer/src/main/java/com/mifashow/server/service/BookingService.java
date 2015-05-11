package com.mifashow.server.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mifashow.server.domain.Booking;
import com.mifashow.server.domain.Constance;
import com.mifashow.server.domain.Message;
import com.mifashow.server.domain.ResponseException;
import com.mifashow.server.domain.User;
import com.mifashow.server.domain.Constance.DISCOUNT;
import com.mifashow.server.domain.Constance.MESSAGETYPE;

@Service("bookingService")
@Scope("singleton")
public class BookingService {
	JdbcTemplate w_jdbc,r_jdbc;
//	@Autowired
//	StorageService storageService;
	@Autowired
	UserService userService;
	@Autowired
	MessageService messageService;
	@Autowired
	CounterService counterService;

	public BookingService() {}

	@Autowired
	public void setDataSource(BasicDataSource w_dataSource,BasicDataSource r_dataSource) {
		this.w_jdbc = new JdbcTemplate(w_dataSource);
		this.r_jdbc = new JdbcTemplate(r_dataSource);
		w_jdbc.execute("create table if not exists booking (bookingId int auto_increment primary key,createTime bigInt not null,postingId long not null,customerId long not null,stylistId long not null,agreedTime bigInt not null,agreedPrice int not null,listPrice int not null,discount enum('DR0','DR10','DR20','DR30','DR40','DR50','DR60','DR70','DR80','DR90') NOT NULL,commitTime bigInt,commentTime bigInt,comment varchar(1000),commentRendering varchar(1024),rating int,explainTime bigInt,explanation varchar(1000),terminateTime bigInt,cancelTime bigInt,status enum('COMMITTING','COMMENTING','EXPLAINING','ARBITRATING','TERMINATE','CANCELED') default 'COMMITTING')");
	}
	
	public void post(String sign,final Booking booking) throws ResponseException{
		User authUser=userService.getBySign(sign);
		String error="";
		if(booking==null){
			error="booking";
		}else if(booking.getStylistId()==booking.getCustomerId()){
			error="customerId";
		}else if(booking.getPostingId()==0){
			error="postingId";
		}else if(booking.getAgreedTime()<Calendar.getInstance().getTimeInMillis()){
			error="agreeTime";
		}else if(booking.getDiscount()==null){
			error="discount";
		}else if(booking.getListPrice()<0){
			error="listPrice";
		}else if(booking.getCustomerId()!=authUser.getUserId()){
			error="customerId";
		}else{
			booking.setAgreedPrice(booking.getListPrice()*(11-booking.getDiscount().ordinal())/10);
		}
		if(error.length()>0){
			throw new ResponseException(error,HttpStatus.NOT_ACCEPTABLE);
		}
		try{
			KeyHolder keyHolder = new GeneratedKeyHolder();
			PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection)
						throws SQLException {
					// TODO Auto-generated method stub
					PreparedStatement ps = connection.prepareStatement("insert into booking (createTime,postingId,customerId,stylistId,agreedTime,agreedPrice,listPrice,discount) values (?,?,?,?,?,?,?,?)");
					PreparedStatementSetter pss=new PreparedStatementSetter(){

						public void setValues(PreparedStatement ps) throws SQLException {
							ps.setLong(1, Calendar.getInstance().getTimeInMillis());
							ps.setLong(2, booking.getPostingId());
							ps.setLong(3, booking.getCustomerId());
							ps.setLong(4, booking.getStylistId());
							ps.setLong(5, booking.getAgreedTime());
							ps.setInt(6, booking.getAgreedPrice());
							ps.setInt(7, booking.getListPrice());
							ps.setString(8, booking.getDiscount().name());
							
						}};
						pss.setValues(ps);
						return ps;
				}
	        };
	        try{
	        w_jdbc.update(preparedStatementCreator, keyHolder);
	        }catch(Exception e){
	        	e.printStackTrace();
	        	throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
	        }
			Long generatedId = keyHolder.getKey().longValue();
			counterService.postBooking(generatedId);
			Booking b=getById(generatedId);
			Message m=new Message();
			m.setObjectId(generatedId);
			m.setObjectValues(b.getStatus().name()+","+b.getPostingId()+","+b.getListPrice()+","+b.getDiscount().name()+","+b.getAgreedPrice()+","+b.getAgreedTime());
			m.setPhotos(b.getPostingRendering());
			m.setFromUserId(b.getCustomerId());
			m.setToUserId(b.getStylistId());
			m.setMessageType(MESSAGETYPE.BOOK);
			messageService.create(sign,m);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void commit(String sign,long bookingId) throws ResponseException{
		try{
			int i=w_jdbc.update("update booking set commitTime=?,status='COMMENTING' where bookingId=? and status='COMMITTING' and stylistId=(select userId from user where sign=?)",Calendar.getInstance().getTimeInMillis(),bookingId,sign);
			if(i>0){
			counterService.commitBooking(bookingId);
			Booking booking=getById(bookingId);
			Message m=new Message();
			m.setObjectId(bookingId);
			m.setFromUserId(booking.getStylistId());
			m.setToUserId(booking.getCustomerId());
			m.setMessageType(MESSAGETYPE.COMMIT);
			messageService.create(sign,m);
			}
		}catch(Exception e){
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	public void comment(String sign,long bookingId,String comment,int rating,HashMap<String,byte[]> imageMap) throws ResponseException{
		Iterator<byte[]> imageIterator=imageMap.values().iterator();
		while(imageIterator.hasNext()){
			if(imageIterator.next().length>204800)throw new ResponseException("images",HttpStatus.NOT_ACCEPTABLE);
		}
		try{
			int i=w_jdbc.update("update booking set commentTime=?, comment=?,rating=?,status='EXPLAINING' where bookingId=? and status in ('COMMENTING','EXPLAINING') and unix_timestamp(now())*1000-agreedTime<=1296000000 and unix_timestamp(now())*1000>agreedTime and customerId=(select userId from user where sign=?)",Calendar.getInstance().getTimeInMillis(),comment,rating,bookingId,sign);
			if(i>0){
			StringBuilder imageUrls=new StringBuilder();
			for(String name:imageMap.keySet()){
				Constance.writeFile("c_"+bookingId+"_"+name+".jpg", Constance.scaleImageBytes(imageMap.get(name), 360));
				Constance.writeFile("c_"+bookingId+"_"+name+"_75.jpg", Constance.scaleImageBytes(imageMap.get(name), 75));
				Constance.writeFile("c_"+bookingId+"_"+name+"_180.jpg", Constance.scaleImageBytes(imageMap.get(name), 180));
				imageUrls.append(Constance.getUrlByFileName("c_"+bookingId+"_"+name+".jpg")).append(',');
			}
			if(imageUrls.length()>0)imageUrls=imageUrls.deleteCharAt(imageUrls.length()-1);
			w_jdbc.update("update booking set commentRendering=? where bookingId=?", new Object[]{imageUrls.toString(),bookingId});
			Booking booking=getById(bookingId);
			Message m=new Message();
			m.setObjectId(bookingId);
			m.setFromUserId(booking.getCustomerId());
			m.setToUserId(booking.getStylistId());
			m.setPhotos(imageUrls.toString());
			m.setMessageType(MESSAGETYPE.COMMENT);
			messageService.create(sign,m);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	public void explain(String sign,long bookingId,String explanation) throws ResponseException{
		try{
			long now=Calendar.getInstance().getTimeInMillis();
			w_jdbc.update("update booking set explainTime=?,explanation=?,terminateTime=?,status='TERMINATE' where bookingId=? and status='EXPLAINING' and unix_timestamp(now())*1000-commentTime<=1296000000 and stylistId=(select userId from user where sign=?)",now,explanation,now,bookingId,sign);
			counterService.explainBooking(bookingId);
			Booking booking=getById(bookingId);
			userService.putRatingByBookingId(bookingId);
			Message m=new Message();
			m.setObjectId(bookingId);
			m.setFromUserId(booking.getStylistId());
			m.setToUserId(booking.getCustomerId());
			m.setMessageType(MESSAGETYPE.EXPLAIN);
			messageService.create(sign,m);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	public void terminate(String sign,long bookingId) throws ResponseException{
		try{
			w_jdbc.update("update booking set status='TERMINATE',terminateTime=? where bookingId=? and status='ARBITRATING' and ? in (select u.sign from authority a left join user u on a.userId=u.userId where a.role in ('ROLE_ADMIN','ROLE_ARBITRATEMAN'))",Calendar.getInstance().getTimeInMillis(),bookingId,sign);
			counterService.explainBooking(bookingId);
			userService.putRatingByBookingId(bookingId);
//			Booking booking=getById(bookingId);
//			Message m=new Message();
//			m.setBookingId(bookingId);
//			m.setFromUserId(booking.getStylistId());
//			m.setToUserId(booking.getCustomerId());
//			m.setMessageType(MESSAGETYPE.EXPLAIN);
//			messageService.create(sign,m);
		}catch(Exception e){
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	public void arbitrate(String sign,long bookingId,String explanation) throws ResponseException{
		try{
			Booking booking=getById(bookingId);
			Message m_a=new Message();
			m_a.setObjectId(bookingId);
			m_a.setFromUserId(booking.getStylistId());
			m_a.setPhotos(booking.getCommentRendering());
			m_a.setMessageType(MESSAGETYPE.ARBITRATE);
			messageService.create(sign,m_a);
			w_jdbc.update("update booking set explainTime=?,explanation=?,status='ARBITRATING' where bookingId=? and status='EXPLAINING' and unix_timestamp(now())*1000-commentTime<=1296000000 and stylistId=(select userId from user where sign=?)",Calendar.getInstance().getTimeInMillis(),explanation,bookingId,sign);
		}catch(Exception e){
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	public void cancel(String sign,long bookingId) throws ResponseException{
		User user=userService.getBySign(sign);
		try{
			w_jdbc.update("update booking set status='CANCELED',cancelTime=? where bookingId=? and (status in ('COMMITTING','COMMENTING') and unix_timestamp(now())*1000<agreedTime and (select userId from user where sign=?) in (stylistId,customerId)) or (status='ARBITRATING' and ? in (select u.sign from authority a left join user u on a.userId=u.userId where a.role in ('ROLE_ADMIN','ROLE_ARBITRATEMAN')))",Calendar.getInstance().getTimeInMillis(),bookingId,sign,sign);
			Booking booking=getById(bookingId);
			if(booking.getStylistId()==user.getUserId()){
				Message m_c=new Message();
				m_c.setObjectId(bookingId);
				m_c.setFromUserId(booking.getStylistId());
				m_c.setToUserId(booking.getCustomerId());
				m_c.setMessageType(MESSAGETYPE.CANCEL);
				messageService.create(sign,m_c);
			}else if(booking.getCustomerId()==user.getUserId()){
				Message m_s=new Message();
				m_s.setObjectId(bookingId);
				m_s.setFromUserId(booking.getCustomerId());
				m_s.setToUserId(booking.getStylistId());
				m_s.setMessageType(MESSAGETYPE.CANCEL);
				messageService.create(sign,m_s);
			}
		}catch(Exception e){
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@Scheduled(cron="0 0 6 * * ?")
	public void autoCancel(){
		w_jdbc.update("update booking set status='CANCELED',cancelTime=? where status='COMMITTING' and unix_timestamp(now())*1000>agreedTime",Calendar.getInstance().getTimeInMillis());
		w_jdbc.update("update booking set status='TERMINATE',terminateTime=? where (status='COMMENTING' and unix_timestamp(now())*1000-agreedTime>1296000000) or (status='EXPLAINING' and unix_timestamp(now())*1000-commentTime>1296000000)",Calendar.getInstance().getTimeInMillis());
	}
	public Booking getById(long bookingId) throws ResponseException{
		Booking booking;
		try{
			booking=w_jdbc.queryForObject("select b.*,s.userName as stylistName,s.figure as stylistFigure,c.userName as customerName,c.figure as customerFigure,p.images as postingRendering from booking b left join user s on b.stylistId=s.userId left join user c on b.customerId=c.userId left join posting p on b.postingId=p.postingId where b.bookingId=?", new BookingMapper(),new Object[]{bookingId});
		}catch(EmptyResultDataAccessException e){
			throw new ResponseException("No matching booking!",HttpStatus.NOT_FOUND);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return booking;
	}
	public List<Booking> getByStylistId(long stylistId) throws ResponseException{
		List<Booking> bookings = null;
		try{
			bookings=r_jdbc.query("select b.*,s.userName as stylistName,s.figure as stylistFigure,c.userName as customerName,c.figure as customerFigure,p.images as postingRendering from booking b left join user s on b.stylistId=s.userId left join user c on b.customerId=c.userId left join posting p on b.postingId=p.postingId where b.stylistId=? and b.status<>'COMMITTING' order by b.agreedTime", new BookingMapper(),new Object[]{stylistId});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return bookings;
	}
	public List<Booking> getByCustomerId(long customerId) throws ResponseException{
		List<Booking> bookings = null;
		try{
			bookings=r_jdbc.query("select b.*,s.userName as stylistName,s.figure as stylistFigure,c.userName as customerName,c.figure as customerFigure,p.images as postingRendering from booking b left join user s on b.stylistId=s.userId left join user c on b.customerId=c.userId left join posting p on b.postingId=p.postingId where b.customerId=? order by b.agreedTime", new BookingMapper(),new Object[]{customerId});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return bookings;
	}
	public List<Booking> getByPostingId(long postingId) throws ResponseException{
		List<Booking> bookings = null;
		try{
			bookings=r_jdbc.query("select b.*,s.userName as stylistName,s.figure as stylistFigure,c.userName as customerName,c.figure as customerFigure,p.images as postingRendering from booking b left join user s on b.stylistId=s.userId left join user c on b.customerId=c.userId left join posting p on b.postingId=p.postingId where b.postingId=? and b.status<>'COMMITTING'", new BookingMapper(),new Object[]{postingId});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return bookings;
	}
	private static final class BookingMapper implements RowMapper<Booking> {

		public Booking mapRow(ResultSet r, int arg1) throws SQLException {
			Booking b=new Booking();
			b.setAgreedPrice(r.getInt("agreedPrice"));
			b.setAgreedTime(r.getLong("agreedTime"));
			b.setTerminateTime(r.getLong("terminateTime"));
			b.setCancelTime(r.getLong("cancelTime"));
			b.setBookingId(r.getLong("bookingId"));
			b.setComment(r.getString("comment"));
			b.setCommentRendering(r.getString("commentRendering"));
			b.setCommentTime(r.getLong("commentTime"));
			b.setCommitTime(r.getLong("commitTime"));
			b.setCreateTime(r.getLong("createTime"));
			b.setCustomerFigure(r.getString("customerFigure"));
			b.setCustomerId(r.getLong("customerId"));
			b.setCustomerName(r.getString("customerName"));
			b.setDiscount(DISCOUNT.valueOf(r.getString("discount")));
			b.setExplainTime(r.getLong("explainTime"));
			b.setExplanation(r.getString("explanation"));
			b.setListPrice(r.getInt("listPrice"));
			b.setPostingId(r.getLong("postingId"));
			b.setPostingRendering(r.getString("postingRendering"));
			b.setRating(r.getInt("rating"));
			b.setStatus(Constance.BOOKINGSTATUS.valueOf(r.getString("status")));
			b.setStylistFigure(r.getString("stylistFigure"));
			b.setStylistId(r.getLong("stylistId"));
			b.setStylistName(r.getString("stylistName"));
			return b;
		}
		
	}

}
