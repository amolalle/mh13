package com.mh13.storm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.storm.state.KeyValueState;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseStatefulWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mh13.Tweet;
/*
	@author amola 
	stateful window bolt using in memory key value store for state
*/
public class TwitterSumarizeHashtagsBolt extends BaseStatefulWindowedBolt<KeyValueState<String, Object>>{
	private static Logger LOG =  LoggerFactory.getLogger(TwitterSumarizeHashtagsBolt.class);

	private static final long serialVersionUID = 1L;
	private KeyValueState<String, Object> state;    

    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;

    }
    
    //initialize state 
	public void initState(KeyValueState<String, Object> state) {		
		 this.state = state;
	     this.state.put("trendMap", new ConcurrentHashMap<String, Long>());
	}
	
	//executed for each window
	public void execute(TupleWindow inputWindow) {		
		 List<Tuple> tuplesInWindow = inputWindow.get();
         LOG.info("Tweets in window {}",tuplesInWindow.size());
         
         Map<String,Long> trendMap = (ConcurrentHashMap<String, Long>)state.get("trendMap");
         
         if (tuplesInWindow.size() > 0) {
        	 for(Tuple tweetTuple:tuplesInWindow){
        		 Tweet tweet = (Tweet)tweetTuple.getValueByField("tweet");
        		 List<String> tweetHashTags =  tweet.getHashTags();
        		 
        		 if(tweetHashTags.size()>0){        			 
        			 for(String hashTag:tweetHashTags){
        				 if(trendMap.containsKey(hashTag)){
        					 long count = trendMap.get(hashTag);        					 
        					 trendMap.put(hashTag,count+1);
        				 }else{        					 
        					 trendMap.put(hashTag, 1l);
        				 }        				 
        			 }
        			 
        		 }
        		 
        	 }
         } 
         LOG.info("Current window Trend = {}",trendMap);
         collector.emit(new Values(trendMap)); 
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		//emit trend map for window
		declarer.declare(new Fields("trendMap"));

	}
}