package com.mifashow.server.controler;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.mifashow.server.domain.Booking;
import com.mifashow.server.domain.Client;
import com.mifashow.server.domain.Constance;
import com.mifashow.server.domain.Constance.REPORTTYPE;
import com.mifashow.server.domain.Message;
import com.mifashow.server.domain.Posting;
import com.mifashow.server.domain.ResponseException;
import com.mifashow.server.domain.Salon;
import com.mifashow.server.domain.User;
import com.mifashow.server.domain.Constance.MESSAGETYPE;
import com.mifashow.server.service.BookingService;
import com.mifashow.server.service.ClientService;
import com.mifashow.server.service.FollowingService;
import com.mifashow.server.service.MarkingService;
import com.mifashow.server.service.MessageService;
import com.mifashow.server.service.PostingService;
import com.mifashow.server.service.SalonService;
import com.mifashow.server.service.UserService;


@Controller
public class RestfulController {
	@Autowired
	private UserService userService;
	@Autowired
	private SalonService salonService;
	@Autowired
	private FollowingService followingService;
	@Autowired
	private PostingService postingService;
	@Autowired
	private MarkingService markingService;
	@Autowired
	private BookingService bookingService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private ClientService clientService;
	@RequestMapping(value = "/storageList", method = RequestMethod.GET)
    public @ResponseBody List<String> user_getStorageList() throws ResponseException{
		return userService.getStorageList();
    }
	@RequestMapping(value = "/fixStorage", method = RequestMethod.GET)
    public void user_fixStorage() throws ResponseException{
		userService.fixStorage();
    }
	private String getAuthSign(){
		UserDetails ud = null;
		try{
			ud=(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}catch(Exception e){}
		if(ud!=null)
		return ud.getUsername();
		else
			return "";
	}
	@ExceptionHandler(ResponseException.class)
    public ResponseEntity<String> handleException(ResponseException ex) {
         return new ResponseEntity<String>(ex.getMessage(), ex.getStatus());
    }
	@RequestMapping(value = "/user", method = RequestMethod.POST)
    public @ResponseBody User user_post(@RequestParam(value="user",required=true) String s_user,@RequestParam(value="figure",required=true) MultipartFile figure) throws ResponseException{
		User user=new Gson().fromJson(s_user, User.class);
		User b_user=null;
		try {
			b_user=userService.post(user,figure.getBytes());
		} catch (IOException e) {
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return b_user;
    }
	@RequestMapping(value = "/user", method = RequestMethod.GET)
    public @ResponseBody User user_getBySign() throws ResponseException{
//		System.out.println("getBySign:"+getAuthSign()+":"+"select * from user where sign="+getAuthSign());
//		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		String sign = ((UserDetails)principal).getUsername();
//		System.out.println("sign="+sign);
		
//		System.out.println(Constance.parseAreaCode(22.83720f,113.26841f, 100)+","+Constance.parseAreaCode(22.83720f,113.26841f, 10));//李兆基0012500300,0125403005
//		System.out.println(Constance.parseAreaCode(22.76725f,113.26247f, 100)+","+Constance.parseAreaCode(22.76725f, 113.26247f, 10));//百昌后街0012500300,0125303006
//		System.out.println(Constance.parseAreaCode(31.24925f, 121.48977f, 100)+","+Constance.parseAreaCode(31.24925f, 121.48977f, 10));//上海吴淞路0013400286,0134802866
//		System.out.println(Constance.parseAreaCode(23.1266f, 113.2599f, 100)+","+Constance.parseAreaCode(23.1266f, 113.2599f, 10));//六榕路0012500299,0125702998
//		System.out.println(Constance.parseAreaCode(39.18210f, 117.13055f, 100)+","+Constance.parseAreaCode(39.18210f, 117.13055f, 10));//天津0014300256,0143602561
//		System.out.println(Constance.parseAreaCode(30.9379f, 120.8881f, 100)+","+Constance.parseAreaCode(30.9379f, 120.8881f, 10));//西塘0013400286,0134402869
//		System.out.println(Constance.parseAreaCode(31.32079f, 120.62965f, 100)+","+Constance.parseAreaCode(31.32079f, 120.62965f, 10));//苏州0013400285,0134902855
//		System.out.println(Constance.parseAreaCode(22.80374f, 113.27584f, 100)+","+Constance.parseAreaCode(22.80374f, 113.27584f, 10));//仙泉洒店0012500300,0125403006
//		System.out.println(Constance.parseAreaCode(22.70979f, 113.2851f, 100)+","+Constance.parseAreaCode(22.70979f, 113.2851f, 10));//rita713
		return userService.getBySign(getAuthSign());
    }
	@RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public @ResponseBody User user_getById(@PathVariable long userId) throws ResponseException{
//		System.out.println("--------userId="+userId);
		User user=userService.getById(userId);
		if(user!=null && !user.getSign().equals(getAuthSign())){
			user.setPassword(null);
		}
		return user;
    }
	@RequestMapping(value = "/user/discovery", method = RequestMethod.GET)
    public @ResponseBody List<User> user_getByDiscovery(@RequestParam(value="latitude",defaultValue="0",required=false) float latitude,@RequestParam(value="longitude",defaultValue="0",required=false) float longitude,@RequestParam(value="maxId",defaultValue="-1",required=false) long maxId,@RequestParam(value="limit",defaultValue="20",required=false) int limit) throws ResponseException{
//		System.out.println("--------latitude="+latitude);
		return userService.getByDiscovery(getAuthSign(),latitude,longitude,maxId,limit);
    }
	@RequestMapping(value = "/user/10km", method = RequestMethod.GET)
    public @ResponseBody List<User> user_getBy10km(@RequestParam(value="latitude",required=true) float latitude,@RequestParam(value="longitude",required=true) float longitude) throws ResponseException{
		return userService.getBy10km(getAuthSign(),latitude,longitude);
    }
	@RequestMapping(value = "/user/100km", method = RequestMethod.GET)
    public @ResponseBody List<User> user_getBy100km(@RequestParam(value="latitude",required=true) float latitude,@RequestParam(value="longitude",required=true) float longitude) throws ResponseException{
		return userService.getBy100km(getAuthSign(),latitude,longitude);
    }
	@RequestMapping(value = "/user/followedUser/{userId}", method = RequestMethod.GET)
    public @ResponseBody List<User> user_getByFollowingUserId(@PathVariable long userId,@RequestParam(value="maxId",defaultValue="-1",required=false) long maxId,@RequestParam(value="limit",defaultValue="20",required=false) int limit) throws ResponseException{
		return userService.getByFollowingUserId(userId,maxId,limit);
    }
	@RequestMapping(value = "/user/followingUser/{userId}", method = RequestMethod.GET)
    public @ResponseBody List<User> user_getByFollowedUserId(@PathVariable long userId,@RequestParam(value="maxId",defaultValue="-1",required=false) long maxId,@RequestParam(value="limit",defaultValue="20",required=false) int limit) throws ResponseException{
		return userService.getByFollowedUserId(userId,maxId,limit);
    }
	@RequestMapping(value = "/user/salon/{salonId}", method = RequestMethod.GET)
    public @ResponseBody List<User> user_getBySalonId(@PathVariable long salonId) throws ResponseException{
		return userService.getBySalonId(salonId);
    }
	@RequestMapping(value = "/password/{sign:.+}", method = RequestMethod.GET)
    public @ResponseBody void user_getPasswordBySign(@PathVariable String sign) throws ResponseException{
		userService.getPasswordBySign(sign);
    }
	@RequestMapping(value = "/user/search", method = RequestMethod.GET)
    public @ResponseBody List<User> user_getByKeyword(@RequestParam(value="keyword",required=true) String keyword,@RequestParam(value="maxId",defaultValue="9999999999",required=false) long maxId,@RequestParam(value="limit",defaultValue="20",required=false) int limit) throws ResponseException{
//		try{
//			keyword=new String(keyword.getBytes("iso-8859-1"),"utf-8");
//    	}catch(Exception e){}
		return userService.getByKeyword(keyword,maxId,limit);
    }
	@RequestMapping(value = "/salon", method = RequestMethod.POST)
    public @ResponseBody Salon salon_post(@RequestBody(required=true) String s_salon) throws ResponseException{
		Salon salon=new Gson().fromJson(s_salon, Salon.class);
		Salon b_salon=salonService.post(getAuthSign(),salon);
		return b_salon;
    }
	@RequestMapping(value = "/salon/image", method = RequestMethod.POST)
    public @ResponseBody Salon salon_postImage(@RequestPart(value="image",required=true) MultipartFile image) throws ResponseException{
		Salon b_salon;
		byte[] imageBytes = null;
		try {
			imageBytes=image.getBytes();
			b_salon=salonService.postImageBySign(getAuthSign(),imageBytes);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return b_salon;
    }
	@RequestMapping(value = "/salon/image", method = RequestMethod.PUT)
    public @ResponseBody Salon salon_putImage(@RequestParam(value="image",required=true) String image,@RequestParam(value="place",required=true) int place) throws ResponseException{
		Salon b_salon;
			b_salon=salonService.putImageBySign(getAuthSign(),image,place);
		return b_salon;
    }
	@RequestMapping(value = "/salon/{salonId}", method = RequestMethod.GET)
    public @ResponseBody Salon salon_getById(@PathVariable long salonId) throws ResponseException{
		Salon salon=salonService.getById(salonId);
		return salon;
    }
	@RequestMapping(value = "/salon/stylist/{stylistId}", method = RequestMethod.DELETE)
    public @ResponseBody void salon_deleteStylistById(@PathVariable long stylistId) throws ResponseException{
		salonService.deleteStylistById(getAuthSign(),stylistId);
    }
	@RequestMapping(value = "/salon/manager/{stylistId}", method = RequestMethod.PUT)
    public @ResponseBody void salon_setManagerById(@PathVariable long stylistId) throws ResponseException{
		salonService.setManagerById(getAuthSign(),stylistId);
    }
	@RequestMapping(value = "/salon/discovery", method = RequestMethod.GET)
    public @ResponseBody List<Salon> salon_getByDiscovery(@RequestParam(value="latitude",defaultValue="0",required=false) float latitude,@RequestParam(value="longitude",defaultValue="0",required=false) float longitude,@RequestParam(value="maxId",defaultValue="-1",required=false) long maxId,@RequestParam(value="limit",defaultValue="20",required=false) int limit) throws ResponseException{
//		System.out.println("--------latitude="+latitude);
		return salonService.getByDiscovery(latitude,longitude,maxId,limit);
    }
	@RequestMapping(value = "/salon/search", method = RequestMethod.GET)
    public @ResponseBody List<Salon> salon_getByKeyword(@RequestParam(value="keyword",required=true) String keyword,@RequestParam(value="maxId",defaultValue="9999999999",required=false) long maxId,@RequestParam(value="limit",defaultValue="20",required=false) int limit) throws ResponseException{
//		try{
//			keyword=new String(keyword.getBytes("iso-8859-1"),"utf-8");
//    	}catch(Exception e){}
		return salonService.getByKeyword(keyword,maxId,limit);
    }
	@RequestMapping(value = "/salon/100km", method = RequestMethod.GET)
    public @ResponseBody List<Salon> salon_getBy100km(@RequestParam(value="latitude",required=true) float latitude,@RequestParam(value="longitude",required=true) float longitude) throws ResponseException{
		return salonService.getBy100km(latitude,longitude);
    }
	@RequestMapping(value = "/message/invite", method = RequestMethod.POST)
    public @ResponseBody void message_inviteStylistBySign(@RequestParam(value="sign",required=true)String sign) throws ResponseException{
		messageService.inviteStylistBySign(getAuthSign(),sign);
    }
	@RequestMapping(value = "/message/invite/{messageId}/accept", method = RequestMethod.PUT)
    public @ResponseBody void message_acceptInviteById(@PathVariable long messageId) throws ResponseException{
		messageService.acceptInviteById(getAuthSign(),messageId);
    }
	@RequestMapping(value = "/message/join/{salonId}", method = RequestMethod.POST)
    public @ResponseBody void message_joinSalonBySign(@PathVariable long salonId) throws ResponseException{
		messageService.joinSalonBySign(getAuthSign(),salonId);
    }
	@RequestMapping(value = "/message/join/{messageId}/accept", method = RequestMethod.PUT)
    public @ResponseBody void message_acceptJoinById(@PathVariable long messageId) throws ResponseException{
		messageService.acceptJoinById(getAuthSign(),messageId);
    }
	@RequestMapping(value = "/password", method = RequestMethod.PUT)
    public @ResponseBody User user_putPasswordBySign(@RequestParam(value="newPassword",required=true) String newPassword) throws ResponseException{
		return userService.putPasswordBySign(getAuthSign(),newPassword);
    }
	@RequestMapping(value = "/user", method = RequestMethod.PUT)
    public @ResponseBody User user_putBySign(@RequestBody(required=true) User user) throws ResponseException{
		return userService.putBySign(getAuthSign(),user);
    }
	@RequestMapping(value = "/user/address", method = RequestMethod.PUT)
    public @ResponseBody User user_putAddressBySign(@RequestParam(value="address",required=true) String address,@RequestParam(value="latitude",required=true) float latitude,@RequestParam(value="longitude",required=true) float longitude) throws ResponseException{
		return userService.putAddressBySign(getAuthSign(), address, latitude, longitude);
		
    }
	@RequestMapping(value = "/user/location", method = RequestMethod.PUT)
    public @ResponseBody User user_putLocationBySign(@RequestParam(value="latitude",required=true) float latitude,@RequestParam(value="longitude",required=true) float longitude) throws ResponseException{
		return userService.putLocationBySign(getAuthSign(), latitude, longitude);
		
    }
	@RequestMapping(value = "/user/about", method = RequestMethod.PUT)
    public @ResponseBody User user_putAboutBySign(@RequestParam(value="about",required=true) String about) throws ResponseException{
		return userService.putAboutBySign(getAuthSign(), about);
		
    }
	@RequestMapping(value = "/user/shopImage", method = RequestMethod.POST)
    public @ResponseBody User user_postShopImageBySign(@RequestPart(value="shopImage",required=true) MultipartFile shopImage) throws ResponseException{
		byte[] shopImageBytes = null;
		try {
			shopImageBytes=shopImage.getBytes();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userService.postShopImageBySign(getAuthSign(), shopImageBytes);
		
    }
	@RequestMapping(value = "/user/figure", method = RequestMethod.POST)
	public @ResponseBody User user_postFigureBySign(@RequestPart(value="figure",required=true) MultipartFile figure) throws ResponseException{
		byte[] figureBytes = null;
		try {
			figureBytes=figure.getBytes();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userService.postFigureBySign(getAuthSign(),figureBytes);
		
    }
	@RequestMapping(value = "/following/{followedUserId}", method = RequestMethod.POST)
	public @ResponseBody void following_post(@PathVariable long followedUserId) throws ResponseException{
		followingService.post(getAuthSign(),followedUserId);
			
    }
	@RequestMapping(value = "/following/{followedUserId}", method = RequestMethod.DELETE)
	public @ResponseBody void following_delete(@PathVariable long followedUserId) throws ResponseException{
		followingService.delete(getAuthSign(),followedUserId);
			
    }
	@RequestMapping(value = "/marking/{postingId}", method = RequestMethod.POST)
	public @ResponseBody void marking_post(@PathVariable long postingId) throws ResponseException{
		markingService.post(getAuthSign(),postingId);
			
    }
	@RequestMapping(value = "/marking/{postingId}", method = RequestMethod.DELETE)
	public @ResponseBody void marking_delete(@PathVariable long postingId) throws ResponseException{
		markingService.delete(getAuthSign(),postingId);	
    }
	@RequestMapping(value = "/posting", method = RequestMethod.POST)
    public @ResponseBody Posting posting_post(@RequestParam(value="posting",required=true) String s_posting,@RequestParam(value="front",required=true) MultipartFile front,@RequestParam(value="back",required=false) MultipartFile back,@RequestParam(value="side",required=false) MultipartFile side,@RequestParam(value="more",required=false) MultipartFile more) throws ResponseException{
		Posting posting=new Gson().fromJson(s_posting, Posting.class);
		Posting b_posting = null;
		LinkedHashMap<String,byte[]> imageMap=new LinkedHashMap<String,byte[]>();
		try{
		if(front!=null){
			imageMap.put("front", front.getBytes());
		}
		if(back!=null){
			imageMap.put("back", back.getBytes());
		}
		if(side!=null){
			imageMap.put("side", side.getBytes());
		}
		if(more!=null){
			imageMap.put("more", more.getBytes());
		}
		}catch(Exception e){
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		b_posting=postingService.post(getAuthSign(),posting,imageMap);
		return b_posting;
    }
	@RequestMapping(value = "/posting/{postingId}", method = RequestMethod.GET)
    public @ResponseBody Posting posting_getById(@PathVariable long postingId) throws ResponseException{
		return postingService.getById(postingId);
    }
	@RequestMapping(value = "/posting/similar/{postingId}", method = RequestMethod.GET)
    public @ResponseBody List<Posting> posting_getBySimilarPostingId(@PathVariable long postingId,@RequestParam(value="maxId",defaultValue="9999999999",required=false) long maxId,@RequestParam(value="sinceId",defaultValue="0",required=false) long sinceId,@RequestParam(value="limit",defaultValue="20",required=false) int limit) throws ResponseException{
		return postingService.getBySimilarPostingId(postingId,maxId,sinceId,limit);
    }
	@RequestMapping(value = "/posting/creater/{createrId}", method = RequestMethod.GET)
    public @ResponseBody List<Posting> posting_getByCreaterId(@PathVariable long createrId,@RequestParam(value="maxId",defaultValue="9999999999",required=false) long maxId,@RequestParam(value="sinceId",defaultValue="0",required=false) long sinceId,@RequestParam(value="limit",defaultValue="20",required=false) int limit) throws ResponseException{
		return postingService.getByCreaterId(createrId,maxId,sinceId,limit);
    }
	@RequestMapping(value = "/posting/suitableUser/{suitableUserId}", method = RequestMethod.GET)
    public @ResponseBody List<Posting> posting_getBySuitableUserId(@PathVariable long suitableUserId,@RequestParam(value="maxId",defaultValue="9999999999",required=false) long maxId,@RequestParam(value="sinceId",defaultValue="0",required=false) long sinceId,@RequestParam(value="limit",defaultValue="20",required=false) int limit) throws ResponseException{
		return postingService.getBySuitableUserId(suitableUserId,maxId,sinceId,limit);
    }
	@RequestMapping(value = "/posting/markingUser/{markingUserId}", method = RequestMethod.GET)
    public @ResponseBody List<Posting> posting_getByMarkingUserId(@PathVariable long markingUserId,@RequestParam(value="maxId",defaultValue="-1",required=false) long maxId,@RequestParam(value="sinceId",defaultValue="0",required=false) long sinceId,@RequestParam(value="limit",defaultValue="20",required=false) int limit) throws ResponseException{
		return postingService.getByMarkingUserId(markingUserId,maxId,sinceId,limit);
    }
	@RequestMapping(value = "/posting/followingUser/{followingUserId}", method = RequestMethod.GET)
    public @ResponseBody List<Posting> posting_getByFollowingUserId(@PathVariable long followingUserId,@RequestParam(value="maxId",defaultValue="9999999999",required=false) long maxId,@RequestParam(value="sinceId",defaultValue="0",required=false) long sinceId,@RequestParam(value="limit",defaultValue="20",required=false) int limit) throws ResponseException{
		return postingService.getByFollowingUserId(followingUserId,maxId,sinceId,limit);
    }
	@RequestMapping(value = "/posting/100km", method = RequestMethod.GET)
    public @ResponseBody List<Posting> posting_getBy100km(@RequestParam(value="latitude",required=true) float latitude,@RequestParam(value="longitude",required=true) float longitude,@RequestParam(value="maxId",defaultValue="9999999999",required=false) long maxId,@RequestParam(value="sinceId",defaultValue="0",required=false) long sinceId,@RequestParam(value="limit",defaultValue="20",required=false) int limit) throws ResponseException{
//		System.out.println("--------------------latitiude="+latitude+",logitude="+longitude+",maxId="+maxId+",sinceId="+sinceId);
		return postingService.getBy100km(latitude,longitude,maxId,sinceId,limit);
    }
	@RequestMapping(value = "/posting/10km", method = RequestMethod.GET)
    public @ResponseBody List<Posting> posting_getBy10km(@RequestParam(value="latitude",required=true) float latitude,@RequestParam(value="longitude",required=true) float longitude,@RequestParam(value="maxId",defaultValue="9999999999",required=false) long maxId,@RequestParam(value="sinceId",defaultValue="0",required=false) long sinceId,@RequestParam(value="limit",defaultValue="20",required=false) int limit) throws ResponseException{
		return postingService.getBy10km(latitude,longitude,maxId,sinceId,limit);
    }
	@RequestMapping(value = "/posting", method = RequestMethod.GET)
    public @ResponseBody List<Posting> posting_get(@RequestParam(value="maxId",defaultValue="9999999999",required=false) long maxId,@RequestParam(value="sinceId",defaultValue="0",required=false) long sinceId,@RequestParam(value="limit",defaultValue="20",required=false) int limit) throws ResponseException{
//		System.out.println("maxId="+maxId+"sinceId="+sinceId+"limit="+limit);
		return postingService.get(maxId,sinceId,limit);
    }
	@RequestMapping(value = "/posting/{postingId}/bookingset", method = RequestMethod.PUT)
    public @ResponseBody Posting posting_putBookingsetById(@PathVariable long postingId,@RequestParam(value="price",required=true) String price,@RequestParam(value="bookingDay",required=true) String bookingDay,@RequestParam(value="bookingTime",required=true) String bookingTime) throws ResponseException{
		return postingService.putBookingSetById(getAuthSign(),postingId,Integer.valueOf(price),bookingDay,bookingTime);
    }
	@RequestMapping(value = "/posting/{postingId}", method = RequestMethod.DELETE)
    public @ResponseBody void posting_deleteById(@PathVariable long postingId) throws ResponseException{
		postingService.deleteById(getAuthSign(),postingId);
    }
	@RequestMapping(value = "/booking", method = RequestMethod.POST)
    public @ResponseBody void booking_post(@RequestBody(required=true) String s_booking) throws ResponseException{
		Booking booking=new Gson().fromJson(s_booking, Booking.class);
		bookingService.post(getAuthSign(),booking);
		
    }
	@RequestMapping(value = "/booking/{bookingId}/comment", method = RequestMethod.POST)
    public @ResponseBody void booking_postComment(@PathVariable(value="bookingId") long bookingId,@RequestParam(value="comment",required=true) String comment,@RequestParam(value="rating",required=true) int rating,@RequestParam(value="front",required=true) MultipartFile front,@RequestParam(value="back",required=false) MultipartFile back,@RequestParam(value="side",required=false) MultipartFile side,@RequestParam(value="more",required=false) MultipartFile more) throws ResponseException{
		HashMap<String,byte[]> imageMap=new HashMap<String,byte[]>();
		try{
		if(front!=null){
			imageMap.put("front", front.getBytes());
		}
		if(back!=null){
			imageMap.put("back", back.getBytes());
		}
		if(side!=null){
			imageMap.put("side", side.getBytes());
		}
		if(more!=null){
			imageMap.put("more", more.getBytes());
		}
		bookingService.comment(getAuthSign(),bookingId,comment,rating,imageMap);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseException("Server Error!",HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	@RequestMapping(value = "/booking/customer/{customerId}", method = RequestMethod.GET)
    public @ResponseBody List<Booking> booking_getByCustomerId(@PathVariable(value="customerId") long customerId) throws ResponseException{
		return bookingService.getByCustomerId(customerId);	
    }
	@RequestMapping(value = "/booking/stylist/{stylistId}", method = RequestMethod.GET)
    public @ResponseBody List<Booking> booking_getByStylistId(@PathVariable(value="stylistId") long stylistId) throws ResponseException{
		return bookingService.getByStylistId(stylistId);	
    }
	@RequestMapping(value = "/booking/posting/{postingId}", method = RequestMethod.GET)
    public @ResponseBody List<Booking> booking_getByPostingId(@PathVariable(value="postingId") long postingId) throws ResponseException{
		return bookingService.getByPostingId(postingId);	
    }
	@RequestMapping(value = "/booking/{bookingId}", method = RequestMethod.GET)
    public @ResponseBody Booking booking_getById(@PathVariable(value="bookingId") long bookingId) throws ResponseException{
		return bookingService.getById(bookingId);	
    }
	@RequestMapping(value = "/booking/{bookingId}/cancel", method = RequestMethod.PUT)
    public @ResponseBody void booking_cancel(@PathVariable long bookingId) throws ResponseException{
		bookingService.cancel(getAuthSign(),bookingId);
		
    }
	@RequestMapping(value = "/booking/{bookingId}/commit", method = RequestMethod.PUT)
    public @ResponseBody void booking_putCommit(@PathVariable long bookingId) throws ResponseException{
		bookingService.commit(getAuthSign(),bookingId);
		
    }
	@RequestMapping(value = "/booking/{bookingId}/explain", method = RequestMethod.PUT)
    public @ResponseBody void booking_putExplain(@PathVariable long bookingId,@RequestParam(value="explanation",required=true) String explanation) throws ResponseException{
		bookingService.explain(getAuthSign(),bookingId,explanation);
	}
	@RequestMapping(value = "/booking/{bookingId}/arbitrate", method = RequestMethod.PUT)
    public @ResponseBody void booking_putArbitrate(@PathVariable long bookingId,@RequestParam(value="explanation",required=true) String explanation) throws ResponseException{
		bookingService.arbitrate(getAuthSign(),bookingId,explanation);
	}
	@RequestMapping(value = "/message", method = RequestMethod.POST)
    public @ResponseBody Message sendMessage(@RequestBody(required=true) String s_message) throws ResponseException{
		Message message=new Gson().fromJson(s_message, Message.class);
		return messageService.create(getAuthSign(),message);
		
    }
	@RequestMapping(value = "/message", method = RequestMethod.GET)
    public @ResponseBody List<Message> getMessagesByToUserId(@RequestParam(value="fromUserId",required=false,defaultValue="-1") long fromUserId) throws ResponseException{
//		List<Message> ms=messageService.getByToUserId(toUserId,fromUserId);
//		for(Message m:ms){
//			System.out.println(m.getMessageType().name()+":"+m.isHandlable());
//		}
//		return ms;
		return messageService.getBySign(getAuthSign(),fromUserId);
    }
	@RequestMapping(value = "/arbitrate/{messageId}/reject", method = RequestMethod.PUT)
    public @ResponseBody void allowArbitrateByMessageId(@PathVariable long messageId) throws ResponseException{
		Message message=messageService.getById(messageId);
		if(message.getMessageType()==MESSAGETYPE.ARBITRATE){
			bookingService.terminate(getAuthSign(), message.getObjectId());
		}
		messageService.handleMessage(messageId);
    }
	@RequestMapping(value = "/arbitrate/{messageId}/allow", method = RequestMethod.PUT)
    public @ResponseBody void rejectArbitrateByMessageId(@PathVariable long messageId) throws ResponseException{
		Message message=messageService.getById(messageId);
		if(message.getMessageType()==MESSAGETYPE.ARBITRATE){
			bookingService.cancel(getAuthSign(), message.getObjectId());
		}
		messageService.handleMessage(messageId);
    }
	@RequestMapping(value = "/report/{postingId}", method = RequestMethod.POST)
    public @ResponseBody Message report(@PathVariable long postingId,@RequestParam(value="reportType",required=true) String reportType) throws ResponseException{
		Posting p=postingService.getById(postingId);
		System.out.println("posting:"+p.toString());
		return messageService.report(getAuthSign(),p,REPORTTYPE.valueOf(reportType));
    }
	@RequestMapping(value = "/report/{messageId}/allow", method = RequestMethod.PUT)
    public @ResponseBody void allowReportByMessageId(@PathVariable long messageId) throws ResponseException{
		Message message=messageService.getById(messageId);
		if(message.getMessageType()==MESSAGETYPE.REPORT){
			postingService.deleteById(getAuthSign(), message.getObjectId());
		}
		messageService.handleMessage(messageId);
    }
	@RequestMapping(value = "/report/{messageId}/reject", method = RequestMethod.PUT)
    public @ResponseBody void rejectReportByMessageId(@PathVariable long messageId) throws ResponseException{
		messageService.handleMessage(messageId);
    }
	@RequestMapping(value = "/client/{clientName}", method = RequestMethod.GET)
    public @ResponseBody Client getClientByClientName(@PathVariable String clientName) throws ResponseException{
		return clientService.getByClientName(clientName);
    }
	@RequestMapping(value = "/image/{fileName}/{size}")
    public String getFileByName(@PathVariable String fileName,@PathVariable String size) throws ResponseException{
		int s = 0;
		try{
			s=Integer.parseInt(size);
		}catch(NumberFormatException e){
			s=0;
		}
		String redirect = null;
		if(s>0 && s<=75){
			redirect=Constance.getUrlByFileName("75/"+fileName);
		}else if(s>75 && s<=180){
			redirect=Constance.getUrlByFileName("180/"+fileName);
		}
		if(redirect==null||"".equals(redirect))
			redirect=Constance.getUrlByFileName(fileName);
//		System.out.println(fileName+"/"+size+" redirect:"+redirect);
		return redirect==null?"":"redirect:"+redirect;
    }

}
