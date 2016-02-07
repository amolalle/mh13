package com.mh13.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

public class LearningStormTopology {

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("LearningStormSpout", new LearningStormSpout(), 2);
		builder.setBolt("LearningStormBolt", new LearningStormBolt(), 4).shuffleGrouping("LearningStormSpout");

		Config conf = new Config();
		conf.setDebug(false);

		LocalCluster cluster = new LocalCluster();

		cluster.submitTopology("Topo1", conf, builder.createTopology());
		try {
			Thread.sleep(100);
		} catch (Exception exception) {
			System.out.println("Thread interrupted exception : " + exception);
		}

		// kill the LearningStormTopology
		cluster.killTopology("LearningStormToplogy");
		// shutdown the storm test cluster
		cluster.shutdown();

	}

}
