package com.mifashow.server.service;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service("counterService")
@Scope("singleton")
public class CounterService {
	JdbcTemplate w_jdbc,r_jdbc;

	public CounterService() {}

	@Autowired
	public void setDataSource(BasicDataSource w_dataSource,BasicDataSource r_dataSource) {
		this.w_jdbc = new JdbcTemplate(w_dataSource);
		this.r_jdbc = new JdbcTemplate(r_dataSource);
	}
	
	public void postBooking(long bookingId){
		w_jdbc.update("update user set bookingNum=bookingNum+1 where userId=(select customerId from booking where bookingId=?)",bookingId);
	}
	public void commitBooking(long bookingId){
		w_jdbc.update("update posting set bookedNum=bookedNum+1 where postingId=(select postingId from booking where bookingId=?)",bookingId);
		w_jdbc.update("update user set bookedNum=bookedNum+1 where userId=(select stylistId from booking where bookingId=?)",bookingId);
	}
	public void explainBooking(long bookingId){
		w_jdbc.update("update posting set commentedNum=commentedNum+1 where postingId=(select postingId from booking where bookingId=?)",bookingId);
		w_jdbc.update("update user set commentedNum=commentedNum+1 where userId=(select stylistId from booking where bookingId=?)",bookingId);
		w_jdbc.update("update user set commentingNum=commentingNum+1 where userId=(select customerId from booking where bookingId=?)",bookingId);
	}
	public void terminateBooking(long bookingId){
		w_jdbc.update("update posting set commentedNum=commentedNum+1 where postingId=(select postingId from booking where commentDate>0 and bookingId=?)",bookingId);
		w_jdbc.update("update user set commentedNum=commentedNum+1 where userId=(select stylistId from booking where commentDate>0 and bookingI=?)",bookingId);
		w_jdbc.update("update user set commentingNum=commentingNum+1 where userId=(select customerId from booking where commentDate>0 and bookingId=?)",bookingId);
	}
	public void postMarking(long markingId){
		w_jdbc.update("update posting set markedNum=markedNum+1 where postingId=(select postingId from marking where markingId=?)",markingId);
		w_jdbc.update("update user set markedNum=markedNum+1 where userId=(select p.createrId from marking m left join posting p on m.postingId=p.postingId where markingId=?)",markingId);
		w_jdbc.update("update user set markingNum=markingNum+1 where userId=(select userId from marking where markingId=?)",markingId);
	}
	public void deleteMarking(long userId,long postingId){
		w_jdbc.update("update posting set markedNum=markedNum-1 where postingId=?",postingId);
		w_jdbc.update("update user set markedNum=markedNum-1 where userId=(select o.createrId from posting o where o.postingId=?)",postingId);
		w_jdbc.update("update user set markingNum=markingNum-1 where userId=?",userId);
	}
	public void postPosting(long postingId){
		w_jdbc.update("update user set postingNum=postingNum+1 where userId=(select createrId from posting where postingId=?)",postingId);
	}
	public void deletePosting(long postingId){
		w_jdbc.update("update user set postingNum=postingNum-1 where userId=(select createrId from posting where postingId=?)",postingId);
	}
	public void postFollowing(long followingId){
		w_jdbc.update("update user set followingNum=followingNum+1 where userId=(select followingUserId from following where followingId=?)",followingId);
		w_jdbc.update("update user set followerNum=followerNum+1 where userId=(select followedUserId from following where followingId=?)",followingId);
	}
	public void deleteFollowing(long followingUserId,long followedUserId){
		w_jdbc.update("update user set followingNum=followingNum-1 where userId=?",followingUserId);
		w_jdbc.update("update user set followerNum=followerNum-1 where userId=?",followedUserId);
	}

}
