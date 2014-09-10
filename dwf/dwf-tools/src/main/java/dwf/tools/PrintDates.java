package dwf.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

public class PrintDates {

	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Now        :" + System.currentTimeMillis());
		System.out.println("Yesterday  :" + DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -1).getTime());
		System.out.println("Today      :" + DateUtils.truncate(new Date(), Calendar.DATE).getTime());
		System.out.println("Tomorrow   :" + DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), +1).getTime());

		while(true) {
			System.out.print("Time: ");
			String inputTime = br.readLine();
			long time = Long.parseLong(inputTime);
			System.out.println(" Date: " + new Date(time));
		}
	}

}
