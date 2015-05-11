package com.mifashow.server.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.mifashow.server.domain.Message;
import com.mifashow.server.domain.ResponseException;
import com.mifashow.server.domain.User;
import com.mifashow.server.domain.Constance.MESSAGETYPE;

@Service("followingService")
@Scope("singleton")
public class FollowingService {
	JdbcTemplate w_jdbc,r_jdbc;
	public FollowingService(){}
	@Autowired
	UserService userService;
	@Autowired
	MessageService messageService;
	@Autowired
	CounterService counterService;
	@Autowired
	public void setDataSource(BasicDataSource w_dataSource,BasicDataSource r_dataSource) {
		this.w_jdbc = new JdbcTemplate(w_dataSource);
		this.r_jdbc = new JdbcTemplate(r_dataSource);
		w_jdbc.execute("create table if not exists following (followingId int auto_increment primary key,followingUserId int not null,followedUserId int not null,createTime bigInt not null)");
	}
	public void post(String sign,final long followedUserId) throws ResponseException{
		final User user=userService.getBySign(sign);
		if(user.getUserId()==followedUserId){
			throw new ResponseException("Can't follow yourself!",HttpStatus.NOT_ACCEPTABLE);
		}
		try{
			KeyHolder keyHolder = new GeneratedKeyHolder();
			PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection)
						throws SQLException {
					// TODO Auto-generated method stub
					PreparedStatement ps = connection.prepareStatement("insert into following (followingUserId,followedUserId,createTime) select ?,?,? from dual where not exists (select * from following where followingUserId=? and followedUserId=?)");
					PreparedStatementSetter pss=new PreparedStatementSetter(){

						public void setValues(PreparedStatement ps) throws SQLException {
							ps.setLong(1, user.getUserId());
							ps.setLong(2, followedUserId);
							ps.setLong(3, Calendar.getInstance().getTimeInMillis());
							ps.setLong(4, user.getUserId());
							ps.setLong(5, followedUserId);
							
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
			counterService.postFollowing(generatedId);
			Message message=new Message();
			message.setMessageType(MESSAGETYPE.FOLLOW);
			message.setFromUserId(user.getUserId());
			message.setToUserId(followedUserId);
			messageService.create(sign,message);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	public void delete(String sign,long followedUserId) throws ResponseException{
		User user=userService.getBySign(sign);
		try{
			w_jdbc.execute("delete from following where followingUserId="+user.getUserId()+" and followedUserId="+followedUserId);
			counterService.deleteFollowing(user.getUserId(),followedUserId);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
