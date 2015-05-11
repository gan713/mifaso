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
import org.springframework.dao.DuplicateKeyException;
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

import com.mifashow.server.domain.Constance;
import com.mifashow.server.domain.ResponseException;
import com.mifashow.server.domain.User;

@Service("userService")
@Scope("singleton")
public class UserService {
	JdbcTemplate w_jdbc,r_jdbc;
//	@Autowired
//	private StorageService storageService;
	@Autowired
	EmailService emailService;
	String baseSql="select u.*,s.salonId,j.manager as salonManager,s.name as salonName,s.phone as salonPhone,s.images as salonImages,s.address as salonAddress,s.latitude as salonLatitude,s.longitude as salonLongitude from user u left join user_salon j on u.userId=j.userId left join salon s on j.salonId=s.salonId ";
	

	public UserService() {
	}

	@Autowired
	public void setDataSource(BasicDataSource w_dataSource,BasicDataSource r_dataSource) {
		this.w_jdbc = new JdbcTemplate(w_dataSource);
		this.r_jdbc = new JdbcTemplate(r_dataSource);
		w_jdbc.execute("create table if not exists user (userId int auto_increment primary key,sign varchar(80) not null unique,about varchar(40),faceshape enum('STANDARD','JIA','SHEN','YOU','GUO','CIRCLE','SQUARE') NOT NULL,hairLength enum('LONG','MIDDLE','SHORT') not null,bangType enum('PART','LONGSIDE','SHORTSIDE','STRAIGHT') not null,curlyType enum('STRAIGHT','CURLY') not null,sex enum('FEMALE','MALE') not null,birthday bigint not null,userType enum('CUSTOMER','STYLIST'),weight int not null,height int not null,userName varchar(15) not null,password varchar(20) not null,figure varchar(256),shopImage varchar(256),address varchar(100),latitude float,longitude float,areaCode100km varchar(10),areaCode10km varchar(10),bookingNum int not null default 0,bookedNum int not null default 0,commentingNum int not null default 0,commentedNum int not null default 0,markingNum int not null default 0,markedNum int not null default 0,postingNum int not null default 0,followerNum int not null default 0,followingNum int not null default 0,rating int not null default 0,ranking int not null default 999999999,grade float not null default 0,salonId int,createTime bigint not null)");
		w_jdbc.execute("create table if not exists authority (authorityId int auto_increment primary key,userId int not null,role enum('ROLE_ADMIN','ROLE_HELPMAN','ROLE_REPORTMAN','ROLE_ARBITRATEREPORTMAN') not null)");
	}

	public User getBySign(String sign) throws ResponseException {
		User user = null;
		try{
//			System.out.println(baseSql+" where u.sign="+sign);
		user=w_jdbc.queryForObject(baseSql+" where u.sign=?", new String[]{sign},new UserMapper(true));
		}catch(EmptyResultDataAccessException e){
//			e.printStackTrace();
			throw new ResponseException("No matching user!",HttpStatus.NOT_FOUND);
		}catch(Exception e){
//			e.printStackTrace();
			throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return user;
	}
	public User getById(long userId) throws ResponseException {
		User user;
		try{
		user=r_jdbc.queryForObject(baseSql+" where u.userId=?", new Object[]{userId},new UserMapper(true));
		}catch(EmptyResultDataAccessException e){
//			e.printStackTrace();
			throw new ResponseException("No matching user found!",HttpStatus.NOT_FOUND);
		}catch(Exception e){
//			e.printStackTrace();
			throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return user;
	}
	public List<User> getByDiscovery(String sign,float latitude,float longitude,long maxId,int limit) throws ResponseException {
		List<User> users = null;
//			String maxIdFilter="";
//			if(maxId>0){
//				maxIdFilter=" and f.followingId<(select followingId from following where followingUserId="+userId+" and followedUserId="+maxId+" limit 1)";
//			}
		try{
			String nearSql;
			if(maxId>0){
				nearSql=baseSql+" where u.sign<>'"+sign+"' and u.userId<="+maxId+" and u.areaCode100km="+((latitude==0 && longitude==0)?"(select areaCode100km from user where sign='"+sign+"')":"'"+Constance.parseAreaCode(latitude, longitude, 100)+"'")+" order by userId desc limit "+(limit+1);
			}else{
				nearSql=baseSql+" where u.sign<>'"+sign+"' and u.areaCode100km="+((latitude==0 && longitude==0)?"(select areaCode100km from user where sign='"+sign+"')":"'"+Constance.parseAreaCode(latitude, longitude, 100)+"'")+" order by userId desc limit "+limit;
			}
//			System.out.println(nearSql);

		users=r_jdbc.query(nearSql,new UserMapper());
		if(users!=null && users.size()>0 && maxId>0){
			if(users.get(0).getUserId()==maxId){
				users.remove(0);
				maxId=0;
			}else{
				users=null;
			}
		}
		}catch( DataAccessException e){}
		if(users==null||users.size()<limit){
			if(users==null)users=new ArrayList<User>();
			String sql;
			try{
			if(maxId>0){
				sql=baseSql+" where u.sign<>'"+sign+"' and u.userId<"+maxId+" and (u.areaCode100km is null or u.areaCode100km<>"+((latitude==0 && longitude==0)?"(select areaCode100km from user where sign='"+sign+"')":"'"+Constance.parseAreaCode(latitude, longitude, 100)+"'")+") order by userId desc limit "+limit;
			}else{
				sql=baseSql+" where u.sign<>'"+sign+"' and (u.areaCode100km is null or u.areaCode100km<>"+((latitude==0 && longitude==0)?"(select areaCode100km from user where sign='"+sign+"')":"'"+Constance.parseAreaCode(latitude, longitude, 100)+"'")+") order by userId desc limit "+limit;
			}
//			System.out.println(sql);
			users.addAll(r_jdbc.query(sql,new UserMapper()));
			}catch(Exception e){
				e.printStackTrace();
				throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		return users;
	}
	public List<User> getByFollowingUserId(long userId,long maxId,int limit) throws ResponseException {
		List<User> users;
		try{
			String maxIdFilter="";
			if(maxId>0){
				maxIdFilter=" and f.followingId<(select followingId from following where followingUserId="+userId+" and followedUserId="+maxId+" limit 1)";
			}
		users=r_jdbc.query(baseSql+"inner join following f on f.followedUserId=u.userId where f.followingUserId=? "+maxIdFilter+" order by f.followingId DESC limit ?", new Object[]{userId,limit},new UserMapper());
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return users;
	}
	public List<User> getByFollowedUserId(long userId,long maxId,int limit) throws ResponseException {
		List<User> users;
		try{
			String maxIdFilter="";
			if(maxId>0){
				maxIdFilter="and f.followingId<(select followingId from following where followedUserId="+userId+" and followingUserId="+maxId+" limit 1)";
			}
		users=r_jdbc.query(baseSql+"inner join following f on f.followingUserId=u.userId where f.followedUserId=? "+maxIdFilter+" order by f.followingId DESC limit ?", new Object[]{userId,limit},new UserMapper());
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return users;
	}
	public List<User> getBySalonId(long salonId) throws ResponseException {
		List<User> users;
		try{
		users=r_jdbc.query(baseSql+" where j.salonId=? order by u.ranking DESC", new Object[]{salonId},new UserMapper());
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return users;
	}
	public List<User> getByKeyword(String keyword,long maxId,int limit) throws ResponseException {
		List<User> users = null;
		String sql;
		keyword=keyword.toLowerCase();
		if(keyword.matches("\\w+@\\w+\\.\\w+")){
			sql=baseSql+" where lower(u.sign)='"+keyword+"'";
		}else{
			sql=baseSql+" where u.userId<"+maxId+" and lower(u.userName) like '%"+keyword+"%' or lower(s.address) like '%"+keyword+"%' order by u.userId desc limit "+limit;
		}
		try{
			users=r_jdbc.query(sql,new UserMapper());
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return users;
	}
	public List<User> getBy10km(String sign,float latitude,float longitude) throws ResponseException{
		List<User> users = null;
		try{
		users=r_jdbc.query(baseSql+" where u.areaCode10km=? and u.sign<>?", new UserMapper(),new Object[]{Constance.parseAreaCode(latitude, longitude, 10),sign});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return users;
	}
	public List<User> getBy100km(String sign,float latitude,float longitude) throws ResponseException{
		List<User> users = null;
		try{
		users=r_jdbc.query(baseSql+" where u.areaCode100km=? and u.sign<>?", new UserMapper(),new Object[]{Constance.parseAreaCode(latitude, longitude, 100),sign});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return users;
	}
	public void validUser(boolean create,User user,byte[] figure) throws ResponseException{
		if(figure==null || figure.length>204800)throw new ResponseException("figure",HttpStatus.NOT_ACCEPTABLE);
		StringBuilder sb=new StringBuilder();
		if(user==null){
			sb.append("user,");
		}else{
		if(user.getBangType()==null){
			sb.append("bangType,");
		}
		if((Calendar.getInstance().getTimeInMillis()-user.getBirthday())<0){
			sb.append("birthday,");
		}
		if(user.getCurlyType()==null){
			sb.append("curlyType,");
		}
		if(user.getFaceShape()==null){
			sb.append("faceShape,");
		}
		if(user.getHairLength()==null){
			sb.append("hairLength,");
		}
		if(user.getHeight()<=0 || user.getHeight()>250){
			sb.append("height,");
		}
		if(create && (user.getPassword()==null || !user.getPassword().matches("^\\w{1,15}"))){
			sb.append("password,");
		}
		if(create && (user.getSign()==null)){
			sb.append("sign,");
		}
		if(user.getSex()==null){
			sb.append("sex,");
		}
		if(user.getUserName()==null || "".equals(user.getUserName()) || user.getUserName().length()>15){
			sb.append("userName,");
		}
		if(user.getUserType()==null){
			sb.append("userType,");
		}
		if(user.getWeight()<=0){
			sb.append("weight,");
		}
		}
		if(sb.length()>0){
			sb.deleteCharAt(sb.length()-1).append(" invalid!");
		}
		if(sb.length()>0)throw new ResponseException(sb.toString(),HttpStatus.NOT_ACCEPTABLE);
	}
	public void getPasswordBySign(final String sign) throws ResponseException{
		r_jdbc.query("select figure,password from user where sign='"+sign+"' limit 1",new ResultSetExtractor<String>(){
			public String extractData(ResultSet rs) throws SQLException,DataAccessException {
				String figure = null;
				String password = null;
				if (rs.first()) {
					figure=rs.getString("figure");
					password=rs.getString("password");
				    }
				if(figure!=null && !"".equals(figure) && password!=null && !"".equals(password)){
					try {
						emailService.send(sign, "Mifashow", figure,"你的密码是："+password,"Your password is "+password);
					} catch (ResponseException e) {
						e.printStackTrace();
					}
				}
				    return null;
			}
		});
		
	}
	public User putPasswordBySign(String sign,String newPassword) throws ResponseException{
		w_jdbc.update("update user set password=? where sign=?", newPassword,newPassword);
		return getBySign(sign);
	}
	public User putBySign(String sign,User user) throws ResponseException{
		validUser(false,user,null);
		try{
		w_jdbc.update("update user set userName=?,userType=?,sex=?,birthday=?,height=?,weight=?,faceShape=?,hairLength=?,curlyType=?,bangType=? where sign=?", new Object[]{user.getUserName(),user.getUserType().name(),user.getSex().name(),user.getBirthday(),user.getHeight(),user.getWeight(),user.getFaceShape().name(),user.getHairLength().name(),user.getCurlyType().name(),user.getBangType().name(),sign});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return getBySign(sign);
		
	}
	public User postShopImageBySign(String sign,byte[] shopImage) throws ResponseException{
		User user=getBySign(sign);
		long timestamp=System.currentTimeMillis();
		if(shopImage.length>204800)throw new ResponseException("shopImage",HttpStatus.NOT_ACCEPTABLE);
		try{
			if(shopImage!=null && shopImage.length>0){
//			Constance.deleteFile("*s_"+user.getUserId()+"_*");
				Constance.writeFile("s_"+user.getUserId()+"_"+timestamp+".jpg", Constance.scaleImageBytes(shopImage, 360));
				Constance.writeFile("75/s_"+user.getUserId()+"_"+timestamp+".jpg", Constance.scaleImageBytes(shopImage, 75));
				Constance.writeFile("180/s_"+user.getUserId()+"_"+timestamp+".jpg", Constance.scaleImageBytes(shopImage, 180));
			w_jdbc.update("update user set shopImage=? where sign=?", new Object[]{Constance.getUrlByFileName("s_"+user.getUserId()+"_"+timestamp+".jpg"),sign});
			}
		}catch(Exception e){
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		user.setShopImage(Constance.getUrlByFileName("s_"+user.getUserId()+"_"+timestamp+".jpg"));
		return user;
	}
	public User postFigureBySign(String sign,byte[] figure) throws ResponseException{
		User user=getBySign(sign);
		long timestamp=System.currentTimeMillis();
		if(figure.length>204800)throw new ResponseException("figure",HttpStatus.NOT_ACCEPTABLE);
		try{
			if(figure!=null && figure.length>0){
//			Constance.deleteFile("u_"+user.getUserId()+"*");
				Constance.writeFile("u_"+user.getUserId()+"_"+timestamp+".jpg", Constance.scaleImageBytes(figure, 360));
				Constance.writeFile("75/u_"+user.getUserId()+"_"+timestamp+".jpg", Constance.scaleImageBytes(figure, 75));
				Constance.writeFile("180/u_"+user.getUserId()+"_"+timestamp+".jpg", Constance.scaleImageBytes(figure, 180));
			w_jdbc.update("update user set figure=? where sign=?", new Object[]{Constance.getUrlByFileName("u_"+user.getUserId()+"_"+timestamp+".jpg"),sign});
			}
		}catch(Exception e){
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		user.setFigure(Constance.getUrlByFileName("u_"+user.getUserId()+"_"+timestamp+".jpg"));
		return user;
	}
	public User putAddressBySign(String sign,String address,float latitude,float longitude) throws ResponseException{
		try{
			w_jdbc.update("update user u set u.address=?,u.latitude=?,u.longitude=?,u.areaCode100km=?,u.areaCode10km=? where u.sign=?",new Object[]{address,latitude,longitude,Constance.parseAreaCode(latitude,longitude,100),Constance.parseAreaCode(latitude,longitude,10),sign});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return this.getBySign(sign);
	}
	public User putLocationBySign(String sign,float latitude,float longitude) throws ResponseException{
		try{
			w_jdbc.update("update user u set u.latitude=?,u.longitude=?,u.areaCode100km=?,u.areaCode10km=? where u.sign=?",new Object[]{latitude,longitude,Constance.parseAreaCode(latitude,longitude,100),Constance.parseAreaCode(latitude,longitude,10),sign});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return this.getBySign(sign);
	}
	public User putAboutBySign(String sign,String about) throws ResponseException{
		try{
			w_jdbc.update("update user u set u.about=? where u.sign=?",new Object[]{about,sign});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return this.getBySign(sign);
	}
	public void putRatingByBookingId(long bookingId) throws ResponseException{
		try{
			w_jdbc.update("update user u set u.rating=u.rating+(select b.rating from booking b where b.bookingId=? limit 1) where u.userId=(select b.stylistId from booking b where b.bookingId=? limit 1)",new Object[]{bookingId,bookingId});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@Scheduled(cron="0 0 5 * * ?")
	public void putRanking() throws ResponseException{
		try{
			w_jdbc.update("update user A, (select u1.userId,count(u2.rating) r,1-count(u2.rating)/count(u2.userId) grade from user u1,user u2 where u2.userType='STYLIST' and (u1.rating<=u2.rating or (u1.rating=u2.rating and u1.userId=u2.userId)) group by u1.userId,u2.rating) B set A.ranking=B.r,A.grade=B.grade where A.userId=B.userId");
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	public void fixStorage(){
//		SaeStorage storage = new SaeStorage(SaeUserInfo.getAccessKey(),SaeUserInfo.getSecretKey(),SaeUserInfo.getAppName(),SaeUserInfo.getSaeTmpPath());
		final List<String> validFileNames = new ArrayList<String>();
		validFileNames.addAll(w_jdbc.queryForList("select SUBSTRING_INDEX(figure,'/',-1) from user", String.class));
		validFileNames.addAll(w_jdbc.queryForList("select SUBSTRING_INDEX(shopImage,'/',-1) from user where shopImage is not null", String.class));
		List<String> postingImages=w_jdbc.queryForList("select images from posting where images is not null", String.class);
		postingImages.addAll(w_jdbc.queryForList("select commentRendering from booking where commentRendering is not null", String.class));
		for(String postingImage:postingImages){
			String[] images=postingImage.split(",");
			for(String image:images){
				validFileNames.add(image.substring(image.lastIndexOf("/")+1));
			}
		}
		List<String> storageFileNames=this.getStorageList();
		for(String s:storageFileNames){
//			System.out.println(s);
			if(!validFileNames.contains(s.substring(s.lastIndexOf("/")+1))){
				Constance.deleteFile(s);
			}
		}
		storageFileNames=this.getStorageList();
		for(String s:storageFileNames){
			if(s.endsWith(".jpg")&&!s.contains("/")&&!storageFileNames.contains("75/"+s)){
//				System.out.println("writing:75");
				Constance.writeFile("75/"+s, Constance.scaleImageBytes(Constance.getFileBytes(s),75));
			}
			if(s.endsWith(".jpg")&&!s.contains("/")&&!storageFileNames.contains("180/"+s)){
//				System.out.println("writing:180");
				Constance.writeFile("180/"+s, Constance.scaleImageBytes(Constance.getFileBytes(s),180));
			}
		}
	}
	public List<String> getStorageList(){
//		SaeStorage storage = new SaeStorage(SaeUserInfo.getAccessKey(),SaeUserInfo.getSecretKey(),SaeUserInfo.getAppName(),SaeUserInfo.getSaeTmpPath());
		List<String> storageList=new ArrayList<String>();
		int storageCount=Constance.getFileCount();
		int skipPosition=0;
		int limitCount=0;
//		System.out.println("storageCount="+storageCount);
		while(skipPosition<storageCount){
			limitCount=Math.min(100, storageCount-skipPosition);
//			System.out.println("storageCount="+storageCount+",skipPosition="+skipPosition+",limitCount="+limitCount);
			storageList.addAll(Constance.getFileList(limitCount,skipPosition));
			skipPosition+=limitCount;
		}
		return storageList;
	}
	public User post(final User user,byte[] figure) throws ResponseException{
		validUser(true,user,figure);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection)
					throws SQLException {
				// TODO Auto-generated method stub
				PreparedStatement ps = connection.prepareStatement("insert into user (bangType,birthday,curlyType,faceShape,figure,hairLength,height,password,sex,sign,userName,about,userType,weight,createTime) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				PreparedStatementSetter pss=new PreparedStatementSetter(){

					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, user.getBangType().name());
						ps.setLong(2, user.getBirthday());
						ps.setString(3, user.getCurlyType().name());
						ps.setString(4, user.getFaceShape().name());
						ps.setString(5, user.getFigure());
						ps.setString(6, user.getHairLength().name());
						ps.setInt(7, user.getHeight());
						ps.setString(8, user.getPassword());
						ps.setString(9,user.getSex().name());
						ps.setString(10, user.getSign().toLowerCase());
						ps.setString(11, user.getUserName());
						ps.setString(12, user.getAbout());
						ps.setString(13, user.getUserType().name());
						ps.setInt(14, user.getWeight());
						ps.setLong(15, Calendar.getInstance().getTimeInMillis());
					}};
					pss.setValues(ps);
					return ps;
			}
        };
        try{
        w_jdbc.update(preparedStatementCreator, keyHolder);
        }catch(DuplicateKeyException e){
        	e.printStackTrace();
        	throw new ResponseException("The user already exists.",HttpStatus.NOT_ACCEPTABLE);
        }
		Long generatedId = keyHolder.getKey().longValue();
		long timestamp=System.currentTimeMillis();
		Constance.writeFile("u_"+generatedId+"_"+timestamp+".jpg", Constance.scaleImageBytes(figure, 360));
		Constance.writeFile("75/u_"+generatedId+"_"+timestamp+".jpg", Constance.scaleImageBytes(figure, 75));
		Constance.writeFile("180/u_"+generatedId+"_"+timestamp+".jpg", Constance.scaleImageBytes(figure, 180));
		User userInserted=getBySign(user.getSign().toLowerCase());
		if(userInserted==null){
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}else{
			w_jdbc.update("update user set figure=? where userId=?", new Object[]{Constance.getUrlByFileName("u_"+generatedId+"_"+timestamp+".jpg"),generatedId});			
		}
		return userInserted;
	}
	
	protected static final class UserMapper implements RowMapper<User> {
		private boolean needPassword=false;
		public UserMapper(){
			super();
		}
		public UserMapper(boolean needPassword){
			super();
			this.needPassword=needPassword;
		}

		public User mapRow(ResultSet rs, int rn) throws SQLException {
			User user=new User();
			user.setBangType(Constance.BANGTYPE.valueOf(rs.getString("bangType")));
			user.setBirthday(rs.getLong("birthday"));
			user.setCurlyType(Constance.CURLYTYPE.valueOf(rs.getString("curlyType")));
			user.setFaceShape(Constance.FACESHAPE.valueOf(rs.getString("faceShape")));
			user.setFigure(rs.getString("figure"));
			user.setHairLength(Constance.HAIRLENGTH.valueOf(rs.getString("hairLength")));
			user.setHeight(rs.getInt("height"));
			if(needPassword)user.setPassword(rs.getString("password"));
			user.setSex(Constance.SEX.valueOf(rs.getString("sex")));
			user.setUserId(rs.getLong("userId"));
			user.setSign(rs.getString("sign"));
			user.setUserName(rs.getString("userName"));
			user.setAbout(rs.getString("about"));
			user.setUserType(Constance.USERTYPE.valueOf(rs.getString("userType")));
			user.setWeight(rs.getInt("weight"));
			user.setShopImage(rs.getString("shopImage"));
			user.setAddress(rs.getString("address"));
			user.setLatitude(rs.getFloat("latitude"));
			user.setLongitude(rs.getFloat("longitude"));
			user.setBookingNum(rs.getInt("bookingNum"));
			user.setBookedNum(rs.getInt("bookedNum"));
			user.setCommentingNum(rs.getInt("commentingNum"));
			user.setCommentedNum(rs.getInt("commentedNum"));
			user.setMarkingNum(rs.getInt("markingNum"));
			user.setMarkedNum(rs.getInt("markedNum"));
			user.setPostingNum(rs.getInt("postingNum"));
			user.setFollowerNum(rs.getInt("followerNum"));
			user.setFollowingNum(rs.getInt("followingNum"));
			user.setRating(rs.getInt("rating"));
			user.setRanking(rs.getInt("ranking"));
			user.setGrade(rs.getFloat("grade"));
			user.setCreateTime(rs.getLong("createTime"));
			user.setSalonId(rs.getLong("salonId"));
			user.setSalonManager(rs.getBoolean("salonManager"));
			user.setSalonName(rs.getString("salonName"));
			user.setSalonPhone(rs.getString("salonPhone"));
			user.setSalonAddress(rs.getString("salonAddress"));
			user.setSalonImages(rs.getString("salonImages"));
			user.setSalonLatitude(rs.getFloat("salonLatitude"));
			user.setSalonLongitude(rs.getFloat("salonLongitude"));
			return user;
		}
    }

}
