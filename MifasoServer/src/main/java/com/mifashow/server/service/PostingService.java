package com.mifashow.server.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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

import com.mifashow.server.domain.Constance;
import com.mifashow.server.domain.Posting;
import com.mifashow.server.domain.ResponseException;
import com.mifashow.server.domain.User;
import com.mifashow.server.domain.Constance.AGETYPE;
import com.mifashow.server.domain.Constance.BANGTYPE;
import com.mifashow.server.domain.Constance.CURLYTYPE;
import com.mifashow.server.domain.Constance.DISCOUNT;
import com.mifashow.server.domain.Constance.FACESHAPE;
import com.mifashow.server.domain.Constance.HAIRLENGTH;
import com.mifashow.server.domain.Constance.SERVICETYPE;
import com.mifashow.server.domain.Constance.SEX;
import com.mifashow.server.domain.Constance.WEEKDAY;
import com.mifashow.server.domain.Constance.POSTINGTYPE;;

@Service("postingService")
@Scope("singleton")
public class PostingService {
	JdbcTemplate w_jdbc,r_jdbc;
//	@Autowired
//	private StorageService storageService;
	@Autowired
	private UserService userService;
	@Autowired
	CounterService counterService;
	final static String  BASESQL="select o.*,c.userName as createrName,c.figure as createrFigure,s.salonId,s.name as salonName,s.address as address,s.latitude as latitude,s.longitude as longitude,s.areaCode100km as areaCode100km,s.areaCode10km as areaCode10km,c.latitude as u_latitude,c.longitude as u_longitude,c.areaCode100km as u_areaCode100km,c.areaCode10km as u_areaCode10km from posting o left join user c on o.createrId=c.userId left join user_salon r on c.userId=r.userId left join salon s on r.salonId=s.salonId";
	public PostingService() {}
	@Autowired
	public void setDataSource(BasicDataSource w_dataSource,BasicDataSource r_dataSource) {
		this.w_jdbc = new JdbcTemplate(w_dataSource);
		this.r_jdbc = new JdbcTemplate(r_dataSource);
		w_jdbc.execute("create table if not exists posting (postingId int auto_increment primary key,createTime bigint not null,createrId int not null,postingType enum('POSTER','SHOW') NOT NULL default 'POSTER',images varchar(1024),serviceTypes set('CUT','PERMANENT','COLOR','BRAID','TREATMENT'),hairLength enum('LONG','MIDDLE','SHORT') not null,bangType enum('PART','LONGSIDE','SHORTSIDE','STRAIGHT') not null,curlyType enum('STRAIGHT','CURLY') not null,sex enum('FEMALE','MALE') not null,faceShapes set('STANDARD','JIA','SHEN','YOU','GUO','CIRCLE','SQUARE') NOT NULL,ageTypes set('BEFORE5','FROM6TO15','FROM16TO21','FROM22TO25','FROM26TO30','FROM31TO35','AFTER36') NOT NULL,price int,bookingTime varchar(239),bookingDay set('SUN','MON','TUE','WED','THU','FRI','SAT'),bookedNum int not null default 0,commentedNum int not null default 0,markedNum int not null default 0,deleteTime bigInt)");
	}
	private void validPosting(long userId,Posting posting,HashMap<String,byte[]> imageMap) throws ResponseException{
		StringBuilder sb=new StringBuilder();
		if(posting.getAgeTypes()==null || posting.getAgeTypes().length==0){
			sb.append("ageType,");
		}
		if(posting.getBangType()==null){
			sb.append("bangType,");
		}
		if(posting.getCreaterId()==0 || posting.getCreaterId()!=userId){
			sb.append("createrId,");
		}
		if(posting.getCurlyType()==null){
			sb.append("curlyType,");
		}
		if(posting.getFaceShapes()==null || posting.getFaceShapes().length==0){
			sb.append("faceShapes,");
		}
		if(posting.getPostingType()==POSTINGTYPE.POSTER && (posting.getServiceTypes()==null || posting.getServiceTypes().length==0)){
			sb.append("serviceTypes,");
		}
		if(posting.getSex()==null){
			sb.append("sex,");
		}
		if(imageMap.size()<1){
			sb.append("images,");
		}else{
			Iterator<byte[]> imageIterator=imageMap.values().iterator();
			while(imageIterator.hasNext()){
				if(imageIterator.next().length>204800)sb.append("images,");
			}
		}
		if(sb.length()>0){
			sb.deleteCharAt(sb.length()-1).append(" invalid!");
			throw new ResponseException(sb.toString(),HttpStatus.NOT_ACCEPTABLE);
		}
	}
	public Posting post(String sign,final Posting posting,LinkedHashMap<String,byte[]> imageMap) throws ResponseException {
		final User user=userService.getBySign(sign);
		validPosting(user.getUserId(),posting,imageMap);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection)
					throws SQLException {
				// TODO Auto-generated method stub
				PreparedStatement ps = connection.prepareStatement("insert into posting (createTime,createrId,postingType,serviceTypes,hairLength,bangType,curlyType,sex,faceShapes,ageTypes) values (?,?,?,?,?,?,?,?,?,?)");
				PreparedStatementSetter pss=new PreparedStatementSetter(){

					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setLong(1, Calendar.getInstance().getTimeInMillis());
						ps.setLong(2, user.getUserId());
						ps.setString(3, posting.getPostingType()==POSTINGTYPE.SHOW?POSTINGTYPE.SHOW.name():POSTINGTYPE.POSTER.name());
						ps.setString(4, Constance.buildSet(posting.getServiceTypes()));
						ps.setString(5, posting.getHairLength().name());
						ps.setString(6, posting.getBangType().name());
						ps.setString(7, posting.getCurlyType().name());
						ps.setString(8, posting.getSex().name());
						ps.setString(9, Constance.buildSet(posting.getFaceShapes()));
						ps.setString(10, Constance.buildSet(posting.getAgeTypes()));
						
					}};
					pss.setValues(ps);
					return ps;
			}
        };
        w_jdbc.update(preparedStatementCreator, keyHolder);
		Long generatedId = keyHolder.getKey().longValue();
		StringBuilder imageUrls=new StringBuilder();
		for(String name:imageMap.keySet()){
			Constance.writeFile("p_"+generatedId+"_"+name+".jpg", Constance.scaleImageBytes(imageMap.get(name), 360));
			Constance.writeFile("75/p_"+generatedId+"_"+name+".jpg", Constance.scaleImageBytes(imageMap.get(name), 75));
			Constance.writeFile("180/p_"+generatedId+"_"+name+".jpg", Constance.scaleImageBytes(imageMap.get(name), 180));
			imageUrls.append(Constance.getUrlByFileName("p_"+generatedId+"_"+name+".jpg")).append(',');
		}
		if(imageUrls.length()>0)imageUrls=imageUrls.deleteCharAt(imageUrls.length()-1);
		w_jdbc.update("update posting set images=? where postingId=?", new Object[]{imageUrls.toString(),generatedId});
		counterService.postPosting(generatedId);
		Posting postingInserted;
		postingInserted=getById(generatedId);
		return postingInserted;
	}
	public Posting getById(long postingId) throws ResponseException{
		Posting posting;
		try{
			posting=w_jdbc.queryForObject(BASESQL+" where o.postingId=? limit 1", new Object[]{postingId},new PostingMapper());
		}catch(EmptyResultDataAccessException e){
			throw new ResponseException("No matching posting!",HttpStatus.NOT_FOUND);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return posting;
	}
	public List<Posting> getByCreaterId(long createrId,long maxId,long sinceId,int limit) throws ResponseException{
		List<Posting> postings = null;
		try{
			postings=r_jdbc.query(BASESQL+" where o.deleteTime is null and o.createrId=? and o.postingId>? and o.postingId<? order by o.postingId DESC limit ?", new PostingMapper(),new Object[]{createrId,sinceId,maxId,limit});
		}catch(Exception e){
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return postings;
	}
	public List<Posting> getBySimilarPostingId(long postingId,long maxId,long sinceId,int limit) throws ResponseException{
		List<Posting> postings = null;
		try{
			postings=r_jdbc.query(BASESQL+",posting sm  where sm.postingId=? and o.deleteTime is null and o.sex=sm.sex and o.hairLength=sm.hairLength and o.bangType=sm.bangType and o.curlyType=sm.curlyType and o.postingId>? and o.postingId<? order by o.postingId DESC limit ?", new PostingMapper(),new Object[]{postingId,sinceId,maxId,limit});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return postings;
	}
	public List<Posting> getBySuitableUserId(long userId,long maxId,long sinceId,int limit) throws ResponseException{
		List<Posting> postings = null;
		try{
			User user=userService.getById(userId);
//			System.out.println("sex="+user.getSex().ordinal());
			postings=r_jdbc.query(BASESQL+" where o.deleteTime is null and o.sex=? and o.bangType>=? and o.hairLength>=? and FIND_IN_SET(?,o.faceShapes)>0 and FIND_IN_SET(?,o.ageTypes)>0 and o.postingId>? and o.postingId<? order by o.postingId DESC limit ?", new PostingMapper(),new Object[]{user.getSex().ordinal()+1,user.getBangType().ordinal()+1,user.getHairLength().ordinal()+1,user.getFaceShape().name(),Constance.getAgeType(user.getBirthday()).name(),sinceId,maxId,limit});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return postings;
	}
	public List<Posting> get(long maxId,long sinceId,int limit) throws ResponseException{
		List<Posting> postings = null;
		try{
			postings=r_jdbc.query(BASESQL+" where o.deleteTime is null and o.postingId>? and o.postingId<? order by o.postingId DESC limit ?", new PostingMapper(),new Object[]{sinceId,maxId,limit});
//	        System.out.println("select o.*,c.userName as createrName,c.figure as createrFigure,c.address as address,c.latitude as latitude,c.longitude as longitude from posting o left join user c on o.createrId=c.userId where o.deleteTime is null and o.postingId>"+sinceId+" and o.postingId<"+maxId+" order by o.postingId DESC limit "+limit+")");
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return postings;
	}
	public List<Posting> getBy100km(float latitude,float longitude,long maxId,long sinceId,int limit) throws ResponseException{
		List<Posting> postings = null;
		try{
		postings=r_jdbc.query(BASESQL+" where o.deleteTime is null and o.postingType='POSTER' and s.areaCode100km=? and o.postingId>? and o.postingId<? order by o.postingId DESC limit ?", new PostingMapper(),new Object[]{Constance.parseAreaCode(latitude, longitude, 100),sinceId,maxId,limit});
//		System.out.println("select o.*,c.userName as createrName,c.figure as createrFigure,c.address as address,c.latitude as latitude,c.longitude as longitude from posting o left join user c on o.createrId=c.userId where o.deleteTime is null and c.areaCode100km="+Constance.parseAreaCode(latitude, longitude, 100)+" and o.postingId>"+sinceId+" and o.postingId<"+maxId+" order by o.postingId DESC limit "+limit);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return postings;
	}
	public List<Posting> getBy10km(float latitude,float longitude,long maxId,long sinceId,int limit) throws ResponseException{
		List<Posting> postings = null;
		try{
		postings=r_jdbc.query(BASESQL+" where o.deleteTime is null and o.postingType='POSTER' and s.areaCode10km=? and o.postingId>? and o.postingId<? order by o.postingId DESC limit ?", new PostingMapper(),new Object[]{Constance.parseAreaCode(latitude, longitude, 10),sinceId,maxId,limit});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return postings;
	}
	public List<Posting> getByMarkingUserId(long markingUserId,long maxId,long sinceId,int limit) throws ResponseException{
		List<Posting> postings = null;
		try{
		    String filter="";
			if(maxId>0)filter+=" and m.markingId<(select markingId from marking where userId="+markingUserId+" and postingId="+maxId+" limit 1)";
			if(sinceId>0)filter+=" and m.markingId>(select markingId from marking where userId="+markingUserId+" and postingId="+sinceId+" limit 1)";
			postings=r_jdbc.query(BASESQL+" left join marking m on o.postingId=m.postingId where m.userId=? "+filter+" order by m.markingId DESC limit ?", new PostingMapper(),new Object[]{markingUserId,limit});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return postings;
	}
	public List<Posting> getByFollowingUserId(long followingUserId,long maxId,long sinceId,int limit) throws ResponseException{
		List<Posting> postings = null;
		try{
			postings=r_jdbc.query(BASESQL+" left join following f on o.createrId=f.followedUserId where f.followingUserId=? and o.deleteTime is null and o.postingId>? and o.postingId<? order by o.postingId DESC limit ?", new PostingMapper(),new Object[]{followingUserId,sinceId,maxId,limit});
		}catch(Exception e){
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return postings;
	}
	
	private static final class PostingMapper implements RowMapper<Posting> {

		public Posting mapRow(ResultSet rs, int rn) throws SQLException {
			Posting posting=new Posting();
			posting.setPostingType(POSTINGTYPE.valueOf(rs.getString("postingType")));
			posting.setPostingId(rs.getLong("postingId"));
			posting.setCreateTime(rs.getLong("createTime"));
			posting.setCreaterId(rs.getLong("createrId"));
			posting.setCreaterName(rs.getString("createrName"));
			posting.setCreaterFigure(rs.getString("createrFigure"));
			posting.setSalonId(rs.getLong("salonId"));
			posting.setSalonName(rs.getString("salonName"));
			posting.setAddress(rs.getString("address"));
			posting.setLatitude(posting.getPostingType()==POSTINGTYPE.POSTER?rs.getFloat("latitude"):rs.getFloat("u_latitude"));
			posting.setLongitude(posting.getPostingType()==POSTINGTYPE.POSTER?rs.getFloat("longitude"):rs.getFloat("u_longitude"));
//			String[] fn=rs.getString("images").split(",");
//			for(int i=0;i<fn.length;i++){
//				fn[i]=new SaeStorage().getUrl(Constance.SAE_DOMAIN, fn[i]);
//			}
			posting.setImages(rs.getString("images").split(","));
			posting.setPrice(rs.getInt("price"));
			posting.setBookingDay(Constance.parseSet(WEEKDAY.class, rs.getString("bookingDay")));
			posting.setBookingTime(Constance.parseSet(DISCOUNT.class, rs.getString("bookingTime")));
//			String[] serviceTypeNames=rs.getString("serviceTypes").split(",");
//			SERVICETYPE[] serviceTypes=new SERVICETYPE[serviceTypeNames.length];
//			for(int i=0;i<serviceTypeNames.length;i++){
//				serviceTypes[i]=SERVICETYPE.valueOf(serviceTypeNames[i]);
//			}
			posting.setServiceTypes(Constance.parseSet(SERVICETYPE.class, rs.getString("serviceTypes")));
			posting.setHairLength(HAIRLENGTH.valueOf(rs.getString("hairLength")));
			posting.setBangType(BANGTYPE.valueOf(rs.getString("bangType")));
			posting.setCurlyType(CURLYTYPE.valueOf(rs.getString("curlyType")));
			posting.setSex(SEX.valueOf(rs.getString("sex")));
//			String[] faceShapeNames=rs.getString("faceShapes").split(",");
//			FACESHAPE[] faceShapes=new FACESHAPE[faceShapeNames.length];
//			for(int i=0;i<faceShapeNames.length;i++){
//				faceShapes[i]=FACESHAPE.valueOf(faceShapeNames[i]);
//			}
			posting.setFaceShapes(Constance.parseSet(FACESHAPE.class, rs.getString("faceShapes")));
			String[] ageTypeNames=rs.getString("ageTypes").split(",");
			AGETYPE[] ageTypes=new AGETYPE[ageTypeNames.length];
			for(int i=0;i<ageTypeNames.length;i++){
				ageTypes[i]=AGETYPE.valueOf(ageTypeNames[i]);
			}
			posting.setAgeTypes(ageTypes);
			posting.setBookedNum(rs.getInt("bookedNum"));
			posting.setCommentedNum(rs.getInt("commentedNum"));
			posting.setMarkedNum(rs.getInt("markedNum"));
			posting.setDeleteTime(rs.getLong("deleteTime"));
			return posting;
		}
    }
	@Scheduled(cron="0 0 3 * * ?")
	public void deleteNotUsed(){
//		List<Long> postingIds=r_jdbc.query("select p.postingId from posting p where p.deleteTime>0 and (select count(*) from booking where postingId=p.postingId)=0 and (select count(*) from marking where postingId=p.postingId)=0 and (select count(*) from message where postingId=p.postingId)=0",new ResultSetExtractor<List<Long>>(){
//
//			public List<Long> extractData(ResultSet rs) throws SQLException,
//					DataAccessException {
//				List<Long> ids=new ArrayList<Long>();
//				while(rs.next()){
//					ids.add(rs.getLong("postingId"));
//				}
//				return ids;
//			}
//			
//		});
//		for(long postingId:postingIds){
//			Constance.deleteFile("p_"+postingId+"_*.jpg");
//		}
		w_jdbc.execute("delete from posting  where deleteTime>0 and postingId not in (select postingId from booking union all select postingId from marking union all select postingId from marking union all select objectId from message where messageType='REPORT')");
	}
	public void deleteById(String sign,long postingId) throws ResponseException{
		try{
		w_jdbc.update("update posting set deleteTime=? where postingId=? and (createrId=(select userId from user where sign=?) or ? in (select u.sign from authority a left join user u on a.userId=u.userId where a.role in ('ROLE_ADMIN','ROLE_REPORTMAN')))",Calendar.getInstance().getTimeInMillis(),postingId,sign,sign);
//		SaeStorage ss=new SaeStorage();
//		List<String> imageNames=ss.getList(Constance.SAE_DOMAIN, "p_"+postingId+"_*", 100, 0);
//		for(String imageName:imageNames)ss.delete(Constance.SAE_DOMAIN, imageName);
		counterService.deletePosting(postingId);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	public Posting putBookingSetById(String sign,long postingId,int price,String bookingDay,String bookingTime) throws ResponseException{
		try{
			w_jdbc.update("update posting set price=?,bookingDay=?,bookingTime=? where postingId=? and createrId=(select userId from user where sign=?)", new Object[]{price,bookingDay,bookingTime,postingId,sign});
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return getById(postingId);
	}

}
