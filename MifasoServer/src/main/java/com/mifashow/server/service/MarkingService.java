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

import com.mifashow.server.domain.ResponseException;
import com.mifashow.server.domain.User;

@Service("markingService")
@Scope("singleton")
public class MarkingService {
	JdbcTemplate w_jdbc,r_jdbc;
	public MarkingService(){}
	@Autowired
	UserService userService;
	@Autowired
	CounterService counterService;
	@Autowired
	public void setDataSource(BasicDataSource w_dataSource,BasicDataSource r_dataSource) {
		this.w_jdbc = new JdbcTemplate(w_dataSource);
		this.r_jdbc = new JdbcTemplate(r_dataSource);
		w_jdbc.execute("create table if not exists marking (markingId int auto_increment primary key,userId int not null,postingId int not null,createTime bigInt not null)");
	}
	public void post(final String sign,final long postingId) throws ResponseException{
		final User user=userService.getBySign(sign);
		try{
			KeyHolder keyHolder = new GeneratedKeyHolder();
			PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection)
						throws SQLException {
					// TODO Auto-generated method stub
					PreparedStatement ps = connection.prepareStatement("insert into marking (userId,postingId,createTime) select ?,?,? from dual where not exists (select * from marking where userId=? and postingId=?)");
					PreparedStatementSetter pss=new PreparedStatementSetter(){

						public void setValues(PreparedStatement ps) throws SQLException {
							ps.setLong(1, user.getUserId());
							ps.setLong(2, postingId);
							ps.setLong(3, Calendar.getInstance().getTimeInMillis());
							ps.setLong(4, user.getUserId());
							ps.setLong(5, postingId);
							
						}};
						pss.setValues(ps);
						return ps;
				}
	        };
	        w_jdbc.update(preparedStatementCreator, keyHolder);
			Long generatedId = keyHolder.getKey().longValue();
			counterService.postMarking(generatedId);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	public void delete(String sign,long postingId) throws ResponseException{
		User user=userService.getBySign(sign);
		try{
			w_jdbc.execute("delete from marking where userId="+user.getUserId()+" and postingId="+postingId);
			counterService.deleteMarking(user.getUserId(), postingId);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
