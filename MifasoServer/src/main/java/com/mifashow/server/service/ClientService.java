package com.mifashow.server.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.mifashow.server.domain.Client;
import com.mifashow.server.domain.ResponseException;

@Service("clientService")
@Scope("singleton")
public class ClientService {
	JdbcTemplate w_jdbc,r_jdbc;
	public ClientService() {}
	@Autowired
	public void setDataSource(BasicDataSource w_dataSource,BasicDataSource r_dataSource) {
		this.w_jdbc = new JdbcTemplate(w_dataSource);
		this.r_jdbc = new JdbcTemplate(r_dataSource);
		w_jdbc.execute("create table if not exists client (clientId int auto_increment primary key,clientName varchar(15) not null unique,versionCode int not null,versionName varchar(15) not null,url varchar(256) not null,description varchar(100) not null)");
	}
	public Client getByClientName(String clientName) throws ResponseException{
		Client client;
		try{
		client=r_jdbc.queryForObject("select * from client where clientName=?",new Object[]{clientName}, new RowMapper<Client>(){

			public Client mapRow(ResultSet arg0, int arg1) throws SQLException {
				Client c=new Client();
				c.setClientId(arg0.getLong("clientId"));
				c.setClientName(arg0.getString("clientName"));
				c.setDescription(arg0.getString("description"));
				c.setUrl(arg0.getString("url"));
				c.setVersionCode(arg0.getInt("versionCode"));
				c.setVersionName(arg0.getString("versionName"));
				return c;
			}
			
		});
		}catch(EmptyResultDataAccessException e){
//			e.printStackTrace();
			throw new ResponseException("No matching client found!",HttpStatus.NOT_FOUND);
		}catch(Exception e){
//			e.printStackTrace();
			throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return client;
		
	}

}
