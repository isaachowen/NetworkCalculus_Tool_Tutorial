/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2011 - 2018 Steffen Bondorf
 * Copyright (C) 2017 - 2018 The DiscoDNC contributors
 * Copyright (C) 2019+ The DNC contributors
 *
 * http://networkcalculus.org
 *
 *
 * The Deterministic Network Calculator (DNC) is free software;
 * you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package org.networkcalculus.dnc.demos;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.AnalysisConfig.*;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.network.server_graph.ServerGraphFactory;
import org.networkcalculus.dnc.network.server_graph.Turn;
import org.networkcalculus.dnc.tandem.analyses.TotalFlowAnalysis;
import org.networkcalculus.dnc.tandem.analyses.SeparateFlowAnalysis;
import org.networkcalculus.dnc.tandem.analyses.PmooAnalysis;

public class Tandem_ServerGraph_Generator implements ServerGraphFactory{
	private final int sc_R = 10;
	private final double sc_T =  0.1;
	private final double ac_r = 0.67;
	private final int ac_b = 1;
	
	private Server[] serv_list = new Server[20];
	private Turn[] turn_list = new Turn[19];
	
	private ServiceCurve service_curve = Curve.getFactory().createRateLatency(sc_R, sc_T);
	private ArrivalCurve arrival_curve = Curve.getFactory().createTokenBucket(ac_r, ac_b);
	
	private ServerGraph server_graph;

	public Tandem_ServerGraph_Generator(int i) {
		server_graph = createServerGraph(i);
	}

	public ServerGraph getServerGraph() {
		return server_graph;
	}

	public ServerGraph createServerGraph(int i) {
		server_graph = new ServerGraph();
		for(int j=0;j<i;j++) {
			serv_list[j]  =  server_graph.addServer(service_curve);
			serv_list[j].useMaxSC(false);		// I don't know what this does 
	        serv_list[j].useMaxScRate(false);	// I don't know what this does 
		}

		try {
		// connect the servers
			for(int j=0;j<i-1;j++) {
				turn_list[j] = server_graph.addTurn(serv_list[j],serv_list[j+1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}


		try {
			// create the flow of interest
			// if i == 1 then nothing is added to the flow of interest, there is none
			// if i > 2 then the appropriate turns are added and the graph is okay
			if (i > 1) {
				List<Turn> foi = new LinkedList<>(); 
				for(int j=0;j<i-1;j++) {
					foi.add(turn_list[j]);
				}
				// this adds the middle (two node) flows to the servergraph
				for(int j=0;j<i-1;j++) {
					String alias = "f"+j;
					LinkedList<Turn> path = new LinkedList<Turn>();
					path.add(turn_list[j]);
					server_graph.addFlow(alias, arrival_curve, path);
				}
				
				// this adds the initial (one node) and final (one node) flows, following the pattern of flows of the tandem network
				server_graph.addFlow("first", arrival_curve, serv_list[0]);
				server_graph.addFlow("last", arrival_curve, serv_list[i-1]);
				server_graph.addFlow("foi", arrival_curve, foi);
			}
			
			else{ // i == 1
				List<Server> foi = new LinkedList<>(); 
				foi.add(serv_list[0]);			
				server_graph.addFlow("first", arrival_curve, serv_list[0]);
				server_graph.addFlow("last", arrival_curve, serv_list[i-1]);
				server_graph.addFlow("foi", arrival_curve, foi);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return server_graph;
	}

	public void reinitializeCurves() {
		service_curve = Curve.getFactory().createRateLatency(sc_R, sc_T);
		for (Server server : server_graph.getServers()) {
			server.setServiceCurve(service_curve);
		}

		arrival_curve = Curve.getFactory().createTokenBucket(ac_r, ac_b);
		for (Flow flow : server_graph.getFlows()) {
			flow.setArrivalCurve(arrival_curve);
		}
	}

	@Override
	public ServerGraph createServerGraph() {
		// TODO Auto-generated method stub
		return null;
	}

}


