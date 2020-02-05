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
import java.util.List;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.AnalysisConfig.*;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.tandem.analyses.TotalFlowAnalysis;
import org.networkcalculus.dnc.tandem.analyses.SeparateFlowAnalysis;
import org.networkcalculus.dnc.tandem.analyses.PmooAnalysis;

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
    	org.networkcalculus.num.Num[] res_pmoo = new org.networkcalculus.num.Num[20];
    	
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
	        AnalysisConfig configuration_FIFO = 
	        		new AnalysisConfig( MultiplexingEnforcement.GLOBAL_FIFO, MaxScEnforcement.GLOBALLY_OFF, MaxScEnforcement.SERVER_LOCAL, 
	        				arrival_bound_methods, 
	        				false, false,false);
	        
	        AnalysisConfig configuration_ARB = 
	        		new AnalysisConfig( MultiplexingEnforcement.GLOBAL_ARBITRARY, MaxScEnforcement.GLOBALLY_OFF, MaxScEnforcement.SERVER_LOCAL, 
	        				arrival_bound_methods, 
	        				false, false, false);
			
			TotalFlowAnalysis tfa = new TotalFlowAnalysis(network,configuration_ARB);
			SeparateFlowAnalysis sfa = new SeparateFlowAnalysis(network,configuration_ARB);
			PmooAnalysis pmoo = new PmooAnalysis(network,configuration_ARB);
			
			tfa.performAnalysis(foi);
			sfa.performAnalysis(foi);
			pmoo.performAnalysis(foi);
			org.networkcalculus.num.Num restfa = tfa.getDelayBound();
			org.networkcalculus.num.Num ressfa = sfa.getDelayBound();
			org.networkcalculus.num.Num respmoo = pmoo.getDelayBound();
			
			res_tfa[i-1] = restfa;
			res_sfa[i-1] = ressfa;
			res_pmoo[i-1] = respmoo;
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
		System.out.println("PMOO: ");
		for (int i = 0; i< 20;i ++) {
			System.out.println((i+1)+": "+res_pmoo[i]);
		}
		
    }
}
