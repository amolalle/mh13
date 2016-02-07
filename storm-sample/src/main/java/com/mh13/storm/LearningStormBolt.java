/**
 * 
 */
package com.mh13.storm;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * @author amola
 *
 */
public class LearningStormBolt extends BaseRichBolt {
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private OutputCollector _collector;


	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this._collector = collector;
		
	}

	public void execute(Tuple input) {
		int val = input.getInteger(0);        
        _collector.emit(input, new Values(val*2, val*3));
        _collector.ack(input);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("double","triple"));
	}	

}