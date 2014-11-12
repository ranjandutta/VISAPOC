/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.generic.app.visausecase.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.ExecutionInfo;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.QueryTrace;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.UnavailableException;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import com.datastax.driver.core.policies.LoggingRetryPolicy;
import com.generic.app.visausecase.dto.VisaOffers;
import com.generic.app.visausecase.utils.CassandraConnectionUtil;

public class VisaDaoModified {

	private static Session session = null;

	public VisaDaoModified(Session session) {
		this.session = session;
	}

	
	public void VisaInsertData(List<VisaOffers> list, int batchsize) {
		List<ResultSetFuture> results = new ArrayList<ResultSetFuture>();

		PreparedStatement ps = session
				.prepare("INSERT INTO Visa.testtable "
						+ "(id, uuid,version,created,last_update,currency,userid,points) "
						+ "VALUES (?,?,?,?,?,?,?,?);");

		PreparedStatement ps1 = session
				.prepare("INSERT INTO Visa.testtable1 "
						+ "(id, uuid,version,created,last_update,currency,userid,points) "
						+ "VALUES (?,?,?,?,?,?,?,?);");
		ps.setConsistencyLevel(ConsistencyLevel.ONE);
		ps1.setConsistencyLevel(ConsistencyLevel.ONE);

		
		System.out.println("ps get consistency"+ ps.getConsistencyLevel());
		StringBuilder sb = new StringBuilder("BEGIN BATCH \n");
		
		long count = list.size();
		int i = 0;
		for (VisaOffers Visa : list) {
			i++;
			
			
					if (i % 100000 == 0) {
				System.out.println("Number of rows inserted so far..." + i);
			}
			

					results.add(session.executeAsync(
					ps.bind(Visa.getId(), Visa.getUuid(), Visa.getVersion(),
							Visa.getCreated(), Visa.getLast_update(),
							Visa.getCurrency(), Visa.getUserid(), Visa.getPoints())));	
					results.add(session.executeAsync(
							ps1.bind(Visa.getId(), Visa.getUuid(), Visa.getVersion(),
									Visa.getCreated(), Visa.getLast_update(),
									Visa.getCurrency(), Visa.getUserid(), Visa.getPoints())));	
			/*
			for (ResultSetFuture future : results) {
				try{
				
				ResultSet uninterruptibly = future.getUninterruptibly() ;
		
				}
				catch( UnavailableException e)
				{

					System.out.println("Exception Thrown here,"+e.getMessage());
				}
				
			}
			*/
			results.clear();
			sb = null;

		}
		return;
		


	
	}

}
