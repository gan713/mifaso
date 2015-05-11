<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
<meta charset="utf-8">
<meta name="keywords" content="美发秀,美发秀发型助手,发型,预约,优惠,lbs,地理位置,交友,android,app" />
<meta name="description" content="当你发现越来越多的美发店开到了楼上，理发变得更加便宜，发型师开始重视你的评价，那就是美发秀想要给你的生活带来的改变。" />
<link rel="shortcut icon" href="static/logo.ico" />
<link href="static/style.css" rel="stylesheet" />
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js">
</script>
<title>美发秀官网</title>
</head>
<body class="bodyNormal Index">
		<div class="mainContainer">
			<div class="BannerBg">
				<div class="HeaderBg blackHeader">
					<div class="wrapper Header">
						<a id="logo" class="Logo" href=""></a>
						<div class="Menu" id="MainMenu">
							
							<span class="lk"><span><a href="#about" onclick="gotoAbout();return false;">关于美发秀</a></span></span><span
								class="divider"><span></span></span> <span class="lk"><span><a
									href="http://weibo.com/u/1840587235" target="_blank">联系我们</a></span></span> 
						</div>
					</div>
				</div>
				<div class="wrapper intro">
					<div class="introBtns fl">
						<h1>MiFaShow</h1>
						<h2>您的时尚好闺蜜!</h2>
						<div class="layoutH40"></div>
						<div class="colorBtn blueBtn downloadnow">
							<a id="home_goto_download_btn" href="http://mifashow-mifashow.stor.sinaapp.com/Mifashow.apk"><span></span></a>
						</div>
						<div class="qrcode"></div>
					</div>
					<div class="intropic fl"></div>
				</div>
			</div>
		</div>
		<div class="wrapper indexBox Feature">
		<div class="featureItem ImgLeft clearfix">	
			<div class="featureImg feature1"></div>
			<div class="featureNote">
				<h3>好的发型师却找不到好多顾客?</h3>
				<p>把你做过的发型发布在美发秀，共享到微博或其实社交平台，介绍你的顾客使用它。那么你的顾客会在美发秀上关注你，看到你发布的发型，在线预约并且贴图评价你的服务。只要你服务真诚，附近一定会有更多人发现你的。</p>
			</div>
		</div>
		<div class="featureItem ImgRight clearfix">
			<div class="featureImg feature2"></div>
			<div class="featureNote">
				<h3>天生丽质只是找不到合适的发型?</h3>
				<p>使用美发秀需要注册，你要输入自己的脸型、现在的发型、年龄等情况，因为这样才可以为你推荐最适合的发型。除了能找到最美的发型，你还可以发布自己的发型，让别人成为你的粉丝，让发型师给你意见。</p>
			</div>
		</div>
		<div class="featureItem ImgLeft clearfix">
			<div class="featureImg feature3"></div>
			<div class="featureNote">
				<h3>满街美发店却找不到最好的发型师?</h3>
				<p>看看附近的发型师发布的发型，顾客做完的真实效果是怎么样的，给他怎么样的评价，再去决定是否通过美发秀预约他。</p>
			</div>
		</div>
	</div>
	<div class="wrapper indexBox about">
		<div class="bigText joinus fl">
				<span class="cn">关于我们</span><br/>
				<span class="en">ABOUT US</span>
		</div>
		<div id="about" class="about fl">
			<div id="aboutContent" class="aboutContent">如果移动互联网足以改造每一个传统行业的话，那么我们每一个人都将会感受到自己的生活被彻底改变。不管你有没有去拥抱这个浪潮，不管你有没有听说过移动互联网，更不管你是不是认为手机能打电话能发短信就已经足够好，你已在其中。当你发现越来越多的美发店开到了楼上，理发变得更加便宜，发型师开始重视你的评价，那就是美发秀给你的生活带来的改变。</div>						
		</div>
		<div class="layoutH40"></div>
	</div>
	<div class="footerWrapper">
		<div class="wrapper footer color_gray">
		<div class="CopyRight">www.mifashow.com 2014 © All Rights Reserved.</div>
		</div>
	</div>
	<script>
function gotoAbout(){
	$("html,body").animate({scrollTop: $("#about").offset().top}, 800);
}
</script>
</body>
</html>
