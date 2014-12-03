package com.app;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@EnableScheduling
public class ScheduledTasks {

	@Scheduled(fixedRate = 5000)
	public void performSearch() {
		System.out.println("The current time is: " + new Date());
	}
}