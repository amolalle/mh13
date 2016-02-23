package com.mh13.storm;

import java.util.Map;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * @author amola
	sends field from tuple to kafka topic
	field name and kafka topic are configurable
 *
 */
public class TwitterStreamingBolt extends BaseBasicBolt {
	private static Logger LOG =  LoggerFactory.getLogger(TwitterStreamingBolt.class);
	
	private static final long serialVersionUID = 1L;
	
	private String topic;
	private String fieldName;
	
	public TwitterStreamingBolt(String topic, String fieldName) {
		super();
		this.topic = topic;
		this.fieldName = fieldName;
	}
	private Gson gson;
	
	Producer<String, String> producer = null;

	/*
	 * setup kafka producer 
	 */
	@Override
    public void prepare(Map stormConf, TopologyContext context) {
		gson = new Gson();
		Properties props = new Properties();		 
		props.put("metadata.broker.list", stormConf.get("metadata.broker.list"));
		props.put("serializer.class", "kafka.serializer.StringEncoder");				 
		ProducerConfig config = new ProducerConfig(props);		
		producer = new Producer<String, String>(config);
		LOG.info("Producer Configured with topic [{}] | Tuple filed [{}]",topic,fieldName);		
    }
	
	/**
	 * get the field from tuple and send to kafka topic  
	 */
	public void execute(Tuple input, BasicOutputCollector collector) {		
		KeyedMessage<String, String> message = new KeyedMessage<String, String>(topic, gson.toJson(input.getValueByField(fieldName)));
		LOG.info("Sending message to kafka <{}> ",message.toString());
		producer.send(message);			
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// this bolt doesn't emit anything..

	}

}
