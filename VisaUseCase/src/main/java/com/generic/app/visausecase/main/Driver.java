package com.generic.app.visausecase.main;

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


//import java.io.Console;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.datastax.driver.core.Session;
import com.generic.app.visausecase.dao.VisaDaoModified;
import com.generic.app.visausecase.dao.DB2Dao;
import com.generic.app.visausecase.dto.VisaOffers;
import com.generic.app.visausecase.utils.CassandraConnectionUtil;

public class Driver {
	// 127.0.0.1 writes 1000 2 100 1
	
	private static String serverNames = "";
	private static int partitioncount = 10;
	private static int clcount = 1;
	private static String operation = "";
	private static int batchsize = 25;
	private static int startpartition = 1;

	public static void main(String args[]) throws InterruptedException,
			IOException {
		if (args.length < 3) {
			System.out
					.println("Usage to write rows: java -jar Visa-1.0-SNAPSHOT.jar servername  writes #partitions #clustercolsperpartition #batchsize #startpartition");
	
			return;
		} else if (args[1].equals("writes")) {
			serverNames = args[0];
			operation = args[1];
			partitioncount = Integer.parseInt(args[2]);
			if (args.length > 3)
				clcount = Integer.parseInt(args[3]);
			else
				System.out
						.println("Clustering columns size not specfied.. defaulting to 1");

			if (args.length > 4)
				batchsize = Integer.parseInt(args[4]);
			else
				System.out.println("defaulting to original batchsize"
						+ batchsize);

			if (args.length > 5)
				startpartition = Integer.parseInt(args[5]);
			else
				System.out.println("defaulting to original startpartition"
						+ startpartition);

			System.out.println("server is: " + serverNames + " operation: "
					+ operation + "  Number of partitions:" + partitioncount
					+ " clustering columns:" + clcount + " Batchsize:"
					+ batchsize);
			writeVisaData(serverNames, partitioncount, clcount, batchsize,
					startpartition);

		} 
	}

	
	private static void writeVisaData(String serverNames, int partitioncount,
			int clcount, int batchsize, int startpartition) throws IOException {
		CassandraConnectionUtil casConUtil = CassandraConnectionUtil
				.getInstance(serverNames);
		Session session = casConUtil.getSession();
		VisaDaoModified VisaDao = new VisaDaoModified(session);
		
		System.out.println("Write operations");
		Runtime runtime = Runtime.getRuntime();

		DB2Dao dao = new DB2Dao(partitioncount, clcount, startpartition);
		List<VisaOffers> list = dao.getVisaOffers();
		long starttime = System.currentTimeMillis();

		VisaDao.VisaInsertData(list, batchsize);
		long endtime = System.currentTimeMillis();
		long diff = endtime - starttime;
		String str = new Date().toString() + " " + " :in " + (diff * 1000) + ""
				+ " us" + ":in " + (diff) + "" + " ms" + ":in " + (diff / 1000)
				+ "" + " seconds";
	    // Run the garbage collector
	    runtime.gc();
	    // Calculate the used memory
	    long memory = runtime.totalMemory() - runtime.freeMemory();
	    System.out.println("Used memory is bytes: " + memory);
		casConUtil.shutdown();
		System.out.println(str);

	}



	
}
