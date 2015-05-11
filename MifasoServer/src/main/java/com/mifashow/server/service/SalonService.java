package com.mifashow.server.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.mifashow.server.domain.Constance;
import com.mifashow.server.domain.ResponseException;
import com.mifashow.server.domain.Salon;
import com.mifashow.server.domain.User;

@Service("salonService")
@Scope("singleton")
public class SalonService {
	JdbcTemplate w_jdbc,r_jdbc;
//	@Autowired
//	private StorageService storageService;
	@Autowired
	EmailService emailService;
	@Autowired
	MessageService messageService;

	public SalonService() {
	}
	@Autowired
	UserService userService;

	@Autowired
	public void setDataSource(BasicDataSource w_dataSource,BasicDataSource r_dataSource) {
		this.w_jdbc = new JdbcTemplate(w_dataSource);
		this.r_jdbc = new JdbcTemplate(r_dataSource);
		w_jdbc.execute("create table if not exists salon (salonId int auto_increment primary key,name varchar(80) not null,address varchar(40) not null,latitude float,longitude float,areaCode100km varchar(10),areaCode10km varchar(10),environment varchar(40) not null,phone varchar(15) not null,discount varchar(40),recruitment varchar(40),images varchar(1024),area int not null,cut int not null,permanent int not null,color int not null,braid int not null,treatment int not null,updateTime bigint not null,deleteTime bigint)");
		w_jdbc.execute("create table if not exists user_salon (userId int primary key,salonId int not null,manager tinyint not null default 0)");
	}
	public void validSalon(Salon salon) throws ResponseException{
		StringBuilder sb=new StringBuilder();
		if(salon==null){
			sb.append("salon,");
		}else{
		if(salon.getName()==null || "".equals(salon.getName())||salon.getName().length()>15){
			sb.append("name,");
		}
		if(salon.getAddress()==null || "".equals(salon.getAddress())||salon.getAddress().length()>100){
			sb.append("address,");
		}
		if(salon.getArea()<=0 ){
			sb.append("area,");
		}
		if(salon.getEnvironment()==null || "".equals(salon.getEnvironment())||salon.getEnvironment().length()>280){
			sb.append("environment,");
		}
		if(salon.getPhone()==null || "".equals(salon.getPhone())||salon.getPhone().length()>15||!salon.getPhone().matches("\\d{5,15}")){
			sb.append("phone,");
		}
		if(salon.getCut()<=0 ){
			sb.append("cut,");
		}
		if(salon.getPermanent()<=0 ){
			sb.append("permanent,");
		}
		if(salon.getColor()<=0 ){
			sb.append("color,");
		}
		if(salon.getBraid()<=0 ){
			sb.append("braid,");
		}
		if(salon.getTreatment()<=0 ){
			sb.append("treatment,");
		}
		if(salon.getDiscount()!=null && salon.getDiscount().length()>280){
			sb.append("discount,");
		}
		if(salon.getRecruitment()!=null && salon.getRecruitment().length()>280){
			sb.append("recruitment,");
		}
		}
		if(sb.length()>0){
			sb.deleteCharAt(sb.length()-1).append(" invalid!");
		}
		if(sb.length()>0){
			System.out.println(sb.toString());
			throw new ResponseException(sb.toString(),HttpStatus.NOT_ACCEPTABLE);
		}
	}
	public Salon post(String sign,final Salon salon) throws ResponseException{
		final String sql;
		final User user=userService.getBySign(sign);
		if(user.getSalonId()!=0 && salon.getSalonId()!=0 && user.isSalonManager()&&user.getSalonId()==salon.getSalonId()){
			sql="update salon set name=?,address=?,latitude=?,longitude=?,areaCode100km=?,areaCode10km=?,environment=?,phone=?,discount=?,recruitment=?,area=?,cut=?,permanent=?,color=?,braid=?,treatment=?,updateTime=? where salonId="+salon.getSalonId();
		}else if(user.getSalonId()==0 && salon.getSalonId()==0){
			sql="insert into salon (name,address,latitude,longitude,areaCode100km,areaCode10km,environment,phone,discount,recruitment,area,cut,permanent,color,braid,treatment,updateTime) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		}else{
//			System.out.println(""+(user.getSalonId()!=0)+(salon.getSalonId()!=0)+user.isSalonManager()+(user.getSalonId()==salon.getSalonId()));
			throw new ResponseException("You are not the manager of this salon!",HttpStatus.NOT_ACCEPTABLE);
		}
		validSalon(salon);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection)
					throws SQLException {
				// TODO Auto-generated method stub
				PreparedStatement ps = connection.prepareStatement(sql);
				PreparedStatementSetter pss=new PreparedStatementSetter(){

					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, salon.getName());
						ps.setString(2, salon.getAddress());
						ps.setFloat(3, salon.getLatitude());
						ps.setFloat(4, salon.getLongitude());
						ps.setString(5, Constance.parseAreaCode(salon.getLatitude(), salon.getLongitude(), 100));
						ps.setString(6, Constance.parseAreaCode(salon.getLatitude(), salon.getLongitude(), 10));
						ps.setString(7, salon.getEnvironment());
						ps.setString(8, salon.getPhone());
						ps.setString(9, salon.getDiscount());
						ps.setString(10, salon.getRecruitment());
						ps.setInt(11, salon.getArea());
						ps.setInt(12, salon.getCut());
						ps.setInt(13, salon.getPermanent());
						ps.setInt(14, salon.getColor());
						ps.setInt(15, salon.getBraid());
						ps.setInt(16, salon.getTreatment());
						ps.setLong(17, Calendar.getInstance().getTimeInMillis());
					}};
					pss.setValues(ps);
					return ps;
			}
        };
        w_jdbc.update(preparedStatementCreator, keyHolder);
		Number key = keyHolder.getKey();
		long salonId=salon.getSalonId();
		if(salonId==0&&key!=null){
			salonId=key.longValue();
		}
		if(user.getSalonId()==0&&salonId!=0){
			w_jdbc.update("insert into user_salon values(?,?,?)", user.getUserId(),salonId,1);
		}
		Salon salonInserted=getBySign(sign);
		if(salonInserted==null){
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return salonInserted;
	}
	public void join(long userId,long salonId){
		if(userId!=0&&salonId!=0)
		w_jdbc.update("insert into user_salon values(?,?,?)", userId,salonId,0);
	}
	public Salon postImageBySign(String sign,byte[] image) throws ResponseException{
		Salon salon=getByManagerSign(sign);
		if(salon.getImages()!=null &&salon.getImages().length>=10)throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		long timestamp=System.currentTimeMillis();
		if(image.length>204800)throw new ResponseException("image",HttpStatus.NOT_ACCEPTABLE);
		try{
			if(image!=null && image.length>0){
//			Constance.deleteFile("*s_"+user.getUserId()+"_*");
				Constance.writeFile("s_"+salon.getSalonId()+"_"+timestamp+".jpg", Constance.scaleImageBytes(image, 360));
				Constance.writeFile("75/s_"+salon.getSalonId()+"_"+timestamp+".jpg", Constance.scaleImageBytes(image, 75));
				Constance.writeFile("180/s_"+salon.getSalonId()+"_"+timestamp+".jpg", Constance.scaleImageBytes(image, 180));
			w_jdbc.update("update salon set images=concat(ifnull(concat(images,','),''),?),updateTime=? where salonId=?", new Object[]{Constance.getUrlByFileName("s_"+salon.getSalonId()+"_"+timestamp+".jpg"),Calendar.getInstance().getTimeInMillis(),salon.getSalonId()});
			}
		}catch(Exception e){
//			System.out.println(e.getMessage());
			throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return getBySign(sign);
	}
	public Salon putImageBySign(String sign,String image,int place) throws ResponseException{
		Salon salon=getByManagerSign(sign);
		String[] images=salon.getImages();
		ArrayList<String> il=new ArrayList<String>();
		for(int i=0;i<images.length;i++){
			il.add(images[i]);
		}
		int oPlace=il.indexOf(image);
		il.remove(oPlace);
		if(place==oPlace){
			return salon;
		}else if(place==-1){
			Constance.deleteFile(image);
		}else{
			il.add(place, image);
		}
		
		StringBuffer sb=new StringBuffer();
		for(String img:il){
			sb.append(img).append(",");
		}
		if(sb.length()>0)sb.deleteCharAt(sb.length()-1);
		String s=sb.length()==0?null:sb.toString();
		w_jdbc.update("update salon set images=? where salonId=?",new Object[]{s,salon.getSalonId()});
		return getBySign(sign);
	}
	public List<Salon> getByDiscovery(float latitude,float longitude,long maxId,int limit) throws ResponseException {
		List<Salon> salons = null;
//			String maxIdFilter="";
//			if(maxId>0){
//				maxIdFilter=" and f.followingId<(select followingId from following where followingUserId="+userId+" and followedUserId="+maxId+" limit 1)";
//			}
		try{
			String nearSql;
			if(maxId>0){
				nearSql="select s.*,u.userName,u.figure,u.userId FROM salon s inner join user_salon r on s.salonId=r.salonId inner join user u on r.userId=u.userId where r.manager=1 and s.salonId<="+maxId+" and s.areaCode100km='"+Constance.parseAreaCode(latitude, longitude, 100)+"' order by salonId desc limit "+(limit+1);
			}else{
				nearSql="select s.*,u.userName,u.figure,u.userId FROM salon s inner join user_salon r on s.salonId=r.salonId inner join user u on r.userId=u.userId where r.manager=1 and s.areaCode100km='"+Constance.parseAreaCode(latitude, longitude, 100)+"' order by salonId desc limit "+limit;
			}
//			System.out.println(nearSql);

		salons=r_jdbc.query(nearSql,new SalonMapper());
		if(salons!=null && maxId>0){
			if(salons.get(0).getSalonId()==maxId){
				salons.remove(0);
				maxId=0;
			}else{
				salons=null;
			}
		}
		}catch( DataAccessException e){}
		if(salons==null||salons.size()<limit){
			if(salons==null)salons=new ArrayList<Salon>();
			String sql;
			try{
			if(maxId>0){
				sql="select s.*,u.userName,u.figure,u.userId FROM salon s inner join user_salon r on s.salonId=r.salonId inner join user u on r.userId=u.userId where r.manager=1 and s.userId<"+maxId+" and (s.areaCode100km is null or s.areaCode100km<>"+"'"+Constance.parseAreaCode(latitude, longitude, 100)+"') order by salonId desc limit "+limit;
			}else{
				sql="select s.*,u.userName,u.figure,u.userId FROM salon s inner join user_salon r on s.salonId=r.salonId inner join user u on r.userId=u.userId where r.manager=1 and (s.areaCode100km is null or s.areaCode100km<>"+"'"+Constance.parseAreaCode(latitude, longitude, 100)+"') order by salonId desc limit "+limit;
			}
//			System.out.println(sql);
			salons.addAll(r_jdbc.query(sql,new SalonMapper()));
			}catch(Exception e){
				e.printStackTrace();
				throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		return salons;
	}
	public List<Salon> getByKeyword(String keyword,long maxId,int limit) throws ResponseException {
		List<Salon> salons = null;
		keyword=keyword.toLowerCase();
		String sql="select s.*,u.userName,u.figure,u.userId FROM salon s inner join user_salon r on s.salonId=r.salonId inner join user u on r.userId=u.userId where r.manager=1 and s.salonId<"+maxId+" and lower(s.name) like '%"+keyword+"%' or lower(s.address) like '%"+keyword+"%' order by salonId desc limit "+limit;
		try{
			salons=r_jdbc.query(sql,new SalonMapper());
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return salons;
	}
	public Salon getBySign(String sign) throws ResponseException{
		Salon salon = null;
		try{
			salon=w_jdbc.queryForObject("select s.*,u.userName,u.figure,u.userId FROM salon s inner join user_salon r on s.salonId=r.salonId inner join user u on r.userId=u.userId where r.manager=1 and s.salonId=(select salonId from user_salon join user on user_salon.userId=user.userId where user.sign=?)", new String[]{sign},new SalonMapper());
		}catch(EmptyResultDataAccessException e){
			throw new ResponseException("No matching salon found!",HttpStatus.NOT_FOUND);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return salon;
	}
	public Salon getByManagerSign(String sign) throws ResponseException{
		Salon salon = null;
		try{
			salon=r_jdbc.queryForObject("select s.*,u.userName,u.figure,u.userId FROM salon s inner join user_salon r on s.salonId=r.salonId inner join user u on r.userId=u.userId where r.manager=1 and u.sign=?", new String[]{sign},new SalonMapper());
		}catch(EmptyResultDataAccessException e){
			throw new ResponseException("No matching salon found!",HttpStatus.NOT_FOUND);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return salon;
	}
	public void setManagerById(String sign,long stylistId) throws ResponseException{
		Salon salon = this.getByManagerSign(sign);
		User user=userService.getBySign(sign);
		if(salon!=null){
			w_jdbc.update("update user_salon set manager=1 where salonId=? and userId=?", salon.getSalonId(),stylistId);
			w_jdbc.update("update user_salon set manager=0 where salonId=? and userId=?", salon.getSalonId(),user.getUserId());
		}
	}
	public void deleteStylistById(String sign,long stylistId) throws ResponseException{
		Salon salon = this.getBySign(sign);
		User user=userService.getBySign(sign);
		if(salon!=null){
			if(user.isSalonManager() && user.getUserId()==stylistId){
				w_jdbc.update("delete from user_salon where salonId=?", salon.getSalonId());
				w_jdbc.update("update salon set deleteTime=? where salonId=?",Calendar.getInstance().getTimeInMillis(), salon.getSalonId());
			}else if(user.getUserId()==stylistId || user.isSalonManager()){
				w_jdbc.update("delete from user_salon where salonId=? and userId=? and manager=0", salon.getSalonId(),stylistId);
			}
		}
	}
	public Salon getById(long salonId) throws ResponseException{
		Salon salon = null;
		try{
			salon=r_jdbc.queryForObject("select s.*,u.userName,u.figure,u.userId FROM salon s inner join user_salon r on s.salonId=r.salonId inner join user u on r.userId=u.userId where r.manager=1 and s.salonId=?", new Long[]{salonId},new SalonMapper());
		}catch(EmptyResultDataAccessException e){
			throw new ResponseException("No matching salon found!",HttpStatus.NOT_FOUND);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return salon;
	}
	public List<Salon> getBy100km(float latitude,float longitude) throws ResponseException{
		List<Salon> salons = null;
		try{
			salons=r_jdbc.query("select s.*,u.userName,u.figure,u.userId FROM salon s inner join user_salon r on s.salonId=r.salonId inner join user u on r.userId=u.userId where r.manager=1 and deleteTime is null and s.areaCode100km=?", new SalonMapper(),new Object[]{Constance.parseAreaCode(latitude, longitude, 100)});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return salons;
	}
	
	private static final class SalonMapper implements RowMapper<Salon> {

		public Salon mapRow(ResultSet rs, int arg1) throws SQLException {
			Salon s=new Salon();
			s.setSalonId(rs.getLong("salonId"));
			s.setName(rs.getString("name"));
			s.setAddress(rs.getString("address"));
			s.setLatitude(rs.getFloat("latitude"));
			s.setLongitude(rs.getFloat("longitude"));
			s.setArea(rs.getInt("area"));
			s.setEnvironment(rs.getString("environment"));
			s.setPhone(rs.getString("phone"));
			s.setDiscount(rs.getString("discount"));
			s.setCut(rs.getInt("cut"));
			s.setColor(rs.getInt("color"));
			s.setBraid(rs.getInt("braid"));
			s.setPermanent(rs.getInt("permanent"));
			s.setTreatment(rs.getInt("treatment"));
			s.setRecruitment(rs.getString("recruitment"));
			s.setUpdateTime(rs.getLong("updateTime"));
			if(rs.getString("images")!=null && !"".equals(rs.getString("images")))
			s.setImages(rs.getString("images").split(","));
			s.setManagerId(rs.getLong("userId"));
			s.setManagerName(rs.getString("userName"));
			s.setManagerFigure(rs.getString("figure"));
			return s;
		}
		
	}

}
