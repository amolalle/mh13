/**

 * 
 */
package com.mh13.storm;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/**
 * @author amola
 *
 */
public class LearningStormSpout extends BaseRichSpout {

	/**
	 * 
	 */	private static final long serialVersionUID = 1L;

	private SpoutOutputCollector spoutOutputCollector;

	private static final Map<Integer, Integer> map = new HashMap();

	static {
		map.put(0, 0);
		map.put(1, 1);
		map.put(2, 2);
		map.put(3, 3);
		map.put(4, 4);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see backtype.storm.spout.ISpout#open(java.util.Map,
	 * backtype.storm.task.TopologyContext,
	 * backtype.storm.spout.SpoutOutputCollector)
	 */
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.spoutOutputCollector = collector;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see backtype.storm.spout.ISpout#nextTuple()
	 */
	public void nextTuple() {
		final Random rand = new Random();
	    // generate the random number from 0 to 4.
	    int randomNumber = rand.nextInt(5);
	    spoutOutputCollector.emit(new Values(map.get(randomNumber)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * backtype.storm.topology.IComponent#declareOutputFields(backtype.storm.
	 * topology.OutputFieldsDeclarer)
	 */
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("site"));
	}

}
