package com.mh13.storm;

import java.util.concurrent.TimeUnit;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.mh13.Tweet;

/**
 * To run this topology you should execute this main as: 
 * java -cp jar-name.jar com.mh13.storm.Topology <topology name> <twitter users comma separated> 
 *
 * @author amola 
 *
 */
public class Topology {
	private static Logger LOG =  LoggerFactory.getLogger(Topology.class);
	
 
	public static void main(String[] args) throws InterruptedException, AlreadyAliveException, InvalidTopologyException, AuthorizationException {
		
		String toplogyName = args[0];
		//comma separated list of interested users
		String tweeterUser = args[1];
		
		LOG.info("=========================START========================");
		
		TopologyBuilder builder = new TopologyBuilder();
		
		builder.setSpout("tweets-collector", new TwitterStramingSpout(),1);
		
		builder.setBolt("all-tweets-to-kafka", new TwitterStreamingBolt("all_tweets","tweet")).shuffleGrouping("tweets-collector");
		builder.setBolt("tweet-hashtag-summerizer", new TwitterSumarizeHashtagsBolt().withMessageIdField("id").withTumblingWindow(new Duration(30, TimeUnit.SECONDS)),1).shuffleGrouping("tweets-collector");
		builder.setBolt("trend-to-kafka", new TwitterStreamingBolt("tweet_trends", "trendMap")).shuffleGrouping("tweet-hashtag-summerizer");
		
		LocalCluster cluster = new LocalCluster();		
		Config conf = new Config();
		
		conf.put("metadata.broker.list", "<kafka broker list>");		
		//Create twitter accessToken and accessTokenSecret from twitter dev site https://apps.twitter.com/
		//and put in below config parameters
		conf.put("twitter.OAuthConsumerKey", "<replace here>");
		conf.put("twitter.OAuthConsumerSecret", "<replace here>");
		conf.put("twitter.OAuthAccessToken", "<replace here>");
		conf.put("twitter.OAuthAccessTokenSecret", "<replace here>");
		
		conf.put("twitter.user", tweeterUser);
		
		Config.registerSerialization(conf, Tweet.class, FieldSerializer.class);
		
		LOG.info("Running topology [{}] for Twitter user {}",toplogyName,tweeterUser);
		
		cluster.submitTopology(toplogyName, conf, builder.createTopology());

		//use below to run topology on cluster
		//StormSubmitter.submitTopology(toplogyName, conf, builder.createTopology());
		 
	}
}
