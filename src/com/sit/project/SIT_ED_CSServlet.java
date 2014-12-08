/*
 * Shibaura Institute of Technology
   College of Engineering and Design
   Information of Cancel and Supplementary
   https://twitter.com/SIT_ED_CS
 * Google App Engine
   http://sit-ed-cs.appspot.com/sit_ed_cs
 * programed by
   soeda yusuke / Engineering and Design / y10226@shibaura-it.ac.jp
 * since 2012/5/4
*/

package com.sit.project;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.*;
import java.text.*;


public class SIT_ED_CSServlet extends HttpServlet{
	static Logger logger = Logger.getLogger("SIT_ED_CSServlet");

	public static String[] getMessage(URL url){
		String charset = "Shift_JIS";
		String[] tweets = new String[20];
		int twnum = 0;
		int oiflag = 0; //other information flag
		String buff = new String();

		try {
			URLConnection uc = url.openConnection();
			BufferedInputStream bis = new BufferedInputStream(uc.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(bis, charset));
			String line;
			br.mark(1024);
			while((line = br.readLine())!=null) {
				int st = line.indexOf("&lt;");
				int en = line.indexOf("<br><br>");
				if (st>=0) {
					tweets[twnum] = line.substring(st,en).replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("<br>", "\n");
					twnum++;
				}
			}
			if(twnum==0){
				br.reset();
				while((line = br.readLine())!=null) {
					if (line.indexOf("<BODY>")>=0) {
						oiflag = 1;
					}
					if(oiflag==1){
						buff = buff + line;
					}
				}
				if(oiflag==1){
					SimpleDateFormat sdf = new SimpleDateFormat("MM'��'dd'��'");
					tweets[twnum] = "����("+sdf.format(getDate())+")�́A"+buff.replaceAll("<BODY>", "").replaceAll("</BODY>", "").replaceAll("<br>", "").replaceAll("</HTML>", "").replaceAll(" ", "");
					twnum++;
				}
			}
		} catch (MalformedURLException ex) {
			System.out.println("URL is wrong.");
			logger.log(Level.SEVERE, "MalformedURLException, URL is wrong ", ex);
		} catch (UnknownHostException ex) {
			System.out.println("not found.");
			logger.log(Level.SEVERE, "UnknownHostException, not found", ex);
		} catch (IOException ex) {
			ex.printStackTrace();
			logger.log(Level.SEVERE, "IOException", ex);
		} finally {
			return tweets;
		}

	}

	public static Date getDate(){
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DAY_OF_MONTH,1);
		Date tomorrow = today.getTime();
		return tomorrow;
	}

	public static String getTime(){
		SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss:SSS");
		sdf1.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
		return sdf1.format(new Date());
	}

	public static String[] getTweet(String campas, String order) throws MalformedURLException{
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		sdf1.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
//		URL url = new URL("http://attend.sic.shibaura-it.ac.jp/"+order+"/"+campas+"/"+sdf1.format(getDate())+".html");
//		URL url = new URL("http://attend.sic.shibaura-it.ac.jp/"+order+"/"+campas+"/20141006.html");
//		URL url = new URL("http://attend.sic.shibaura-it.ac.jp/"+order+"/"+campas+"/20141104.html");
		URL url = new URL("http://attend.sic.shibaura-it.ac.jp/"+order+"/"+campas+"/20141126.html");
		String[] tweets = getMessage( url );
		return tweets;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		//http://twitter4j.org/ja/configuration.html
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthAccessToken(accessToken)
		.setOAuthAccessTokenSecret(accessTokenSecret)
		.setOAuthConsumerKey(consumerKey)
		.setOAuthConsumerSecret(consumerSecret);

		Twitter twitter = new TwitterFactory(cb.build()).getInstance();

		String[] message1 = getTweet("s06","cancel");
		String[] message2 = getTweet("s06","supplement");
		String[] message3 = getTweet("o06","cancel");
		String[] message4 = getTweet("o06","supplement");

		try {
			twitter.sendDirectMessage(ACCOUNT, "start\n"+getTime());

			for(int i=0; message4[i]!=null; i++){
				twitter.updateStatus("++++++��{��u++++++\n"+message4[i]);
				twitter.sendDirectMessage(ACCOUNT, "��{��u\n"+message4[i]+"\n"+getTime());
				Thread.sleep(10000);
			}
		} catch (TwitterException e) {
			logger.log(Level.SEVERE, "Twitter error at omiya supplement", e);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "error at omiya supplement", e);
		} try {
			for(int i=0; message3[i]!=null; i++){
				twitter.updateStatus("------��{�x�u------\n"+message3[i]);
				twitter.sendDirectMessage(ACCOUNT, "��{�x�u\n"+message3[i]+"\n"+getTime());
				Thread.sleep(10000);
			}
		} catch (TwitterException e) {
			logger.log(Level.SEVERE, "Twitter error at omiya cancel", e);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "error at omiya cancel", e);
		} try {
			for(int i=0; message2[i]!=null; i++){
				twitter.updateStatus("++++++�ŉY��u++++++\n"+message2[i]);
				twitter.sendDirectMessage(ACCOUNT, "�ŉY��u\n"+message2[i]+"\n"+getTime());
				Thread.sleep(10000);
			}
		} catch (TwitterException e) {
			logger.log(Level.SEVERE, "Twitter error at shibaura supplement", e);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "error at shibaura supplement", e);
		} try {
			for(int i=0; message1[i]!=null; i++){
				twitter.updateStatus("------�ŉY�x�u------\n"+message1[i]);
				twitter.sendDirectMessage(ACCOUNT, "�ŉY�x�u\n"+message1[i]+"\n"+getTime());
				Thread.sleep(10000);
			}
		} catch (TwitterException e) {
			logger.log(Level.SEVERE, "Twitter error at shibaura cancel", e);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "error at shibaura cancel", e);
		} try {
			twitter.sendDirectMessage(ACCOUNT, "end\n"+getTime());
		} catch (TwitterException e) {
			logger.log(Level.SEVERE, "Twitter error at end", e);
		}
	}
}