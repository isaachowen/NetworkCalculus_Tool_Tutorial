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

import org.networkcalculus.dnc.numbers.Num;
import org.networkcalculus.dnc.numbers.NumBackend;
import org.networkcalculus.dnc.numbers.implementations.RationalBigInt;
import org.networkcalculus.dnc.numbers.implementations.RationalInt;
import org.networkcalculus.dnc.numbers.implementations.RealDoublePrecision;
import org.networkcalculus.dnc.numbers.implementations.RealSinglePrecision;
import org.networkcalculus.dnc.numbers.values.NaN; 
import org.networkcalculus.dnc.numbers.values.NegativeInfinity;
import org.networkcalculus.dnc.numbers.values.PositiveInfinity;

public class Analysis_1_thru_20 {

    public Analysis_1_thru_20() {
    }

    public static void main(String[] args) {
        Analysis_1_thru_20 demo = new Analysis_1_thru_20();

        try {
            demo.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() throws Exception {
    	org.networkcalculus.num.Num[] res_tfa = new org.networkcalculus.num.Num[20];
    	org.networkcalculus.num.Num[] res_sfa = new org.networkcalculus.num.Num[20];
    	
		for(int i=1;i<=20;i++) {
			Tandem_ServerGraph_Generator network_factory = new Tandem_ServerGraph_Generator(i);
			ServerGraph network = network_factory.getServerGraph();
			Set<Flow> set = new HashSet<>();
			set = network.getFlows();
			List<Flow> list = new ArrayList<>(set);
			Flow foi = null;
			for(int j=0;j<list.size();j++) {
				if(list.get(j).getAlias() == "foi") {
					foi = list.get(j);
				}
			}
			
	        Set<ArrivalBoundMethod> arrival_bound_methods = new HashSet<ArrivalBoundMethod>(Collections.singleton(ArrivalBoundMethod.AGGR_PBOO_CONCATENATION)); 
	        AnalysisConfig configuration = 
	        		new AnalysisConfig( MultiplexingEnforcement.GLOBAL_ARBITRARY, MaxScEnforcement.GLOBALLY_OFF, MaxScEnforcement.SERVER_LOCAL, 
	        				arrival_bound_methods, 
	        				false, false, true);
			
			TotalFlowAnalysis tfa = new TotalFlowAnalysis(network);
			SeparateFlowAnalysis sfa = new SeparateFlowAnalysis(network);
			tfa.performAnalysis(foi);
			sfa.performAnalysis(foi);
			org.networkcalculus.num.Num restfa = tfa.getDelayBound();
			org.networkcalculus.num.Num ressfa = sfa.getDelayBound();
			res_tfa[i-1] = restfa;
			res_sfa[i-1] = ressfa;
		}
		
		// This prints out the flow of interest latencies for tandem networks of size 1-20 using TFA, and then using SFA
		System.out.println("TFA: ");
		for (int i = 0; i< 20;i ++) {
			System.out.println((i+1)+": "+res_tfa[i]);
		}
		System.out.println("SFA: ");
		for (int i = 0; i< 20;i ++) {
			System.out.println((i+1)+": "+res_sfa[i]);
		}
		
    }
}