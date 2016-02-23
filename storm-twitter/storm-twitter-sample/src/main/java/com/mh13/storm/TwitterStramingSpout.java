package com.mh13.storm;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.mh13.Tweet;
/*
	@author amola 

	Spout to read tweets using TwitterStreaming and emit Tweet
	kafka spout can be used instead. A flume source will get tweets from stream and send messges to kafka topic.
*/
public class TwitterStramingSpout extends BaseRichSpout {
	
	private static Logger LOG =  LoggerFactory.getLogger(TwitterStramingSpout.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SpoutOutputCollector collector;
	
	private  TwitterStream twitterStream;

	LinkedBlockingQueue<Status> tweets = new LinkedBlockingQueue<Status>();	
		
	public void nextTuple() {
		

		Tweet tweet = null;
		Status retweet = null;
		Status newTweet = tweets.poll();
		
		if(newTweet!=null){	
			
			tweet = new Tweet();			
			tweet.setName(newTweet.getUser().getName());
			tweet.setScreenname(newTweet.getUser().getScreenName());
			tweet.setText(newTweet.getText());
			tweet.setCreatedAt(newTweet.getCreatedAt());
			
			for(HashtagEntity hashTag:newTweet.getHashtagEntities()){
				tweet.setHashTag(hashTag.getText());
			}			
			retweet = newTweet.getQuotedStatus();
			if(retweet!=null){
				tweet.setQuotedScreenName(retweet.getUser().getScreenName());
				tweet.setText(retweet.getText());
			}
			LOG.info("Emiting Tweet = {}",tweet);
			collector.emit(new Values(tweet,newTweet.getId(),tweet.getCreatedAt().getTime()));
		}	
	}
	
	public void open(Map conf, TopologyContext context,SpoutOutputCollector collector){
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey((String)conf.get("twitter.OAuthConsumerKey"));
		cb.setOAuthConsumerSecret((String)conf.get("twitter.OAuthConsumerSecret"));		
		cb.setOAuthAccessToken((String)conf.get("twitter.OAuthAccessToken"));
	    cb.setOAuthAccessTokenSecret((String)conf.get("twitter.OAuthAccessTokenSecret"));
	    	    
	    cb.setJSONStoreEnabled(true);
	    cb.setIncludeEntitiesEnabled(true);
	    long userId=0;
	    Configuration configuration  = cb.build();
	    Twitter twitter = new TwitterFactory(configuration).getInstance();
	    long[] userIds = null;
	    try {
	    	String[] users = conf.get("twitter.user").toString().split(",");	    	
	    	userIds = new long[users.length];
	    	int i=0;
	    	for(String user:users){
	    		userIds[i++] = twitter.showUser(user).getId();
	    	}
	    	LOG.info("User {} with twitter id = {}",conf.get("twitter.user"),Arrays.asList(userIds));
	    	
		} catch (TwitterException e1) {			
			LOG.error("Failed to get Twitter id",e1);			
		}
	    
	    twitterStream = new TwitterStreamFactory(configuration).getInstance();	    
	    
	    // The StatusListener is a twitter4j API, which can be added to a Twitter
	    // stream, and will execute methods every time a message comes in through
	    // the stream.
	    StatusListener listener = new StatusListener() {
	      // The onStatus method is executed every time a new tweet comes in.
	      public void onStatus(Status status) {	        
	        try {	        	
				tweets.put(status);				
			} catch (InterruptedException e) {
				LOG.error("Failed onStatus",e);
			}	     
	      }

	      // This listener will ignore everything except for new tweets
	      public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
	      public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
	      public void onScrubGeo(long userId, long upToStatusId) {}
	      public void onException(Exception ex) {}
	      public void onStallWarning(StallWarning warning) {}
	    };
	    
	    twitterStream.addListener(listener);
	    
	    FilterQuery query = new FilterQuery().follow(userIds);
	    
	    twitterStream.filter(query);
	    
		this.collector = collector;
		LOG.info("Twitter stream is ready..!!!!");
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet","id","createdAt"));
	}

}
