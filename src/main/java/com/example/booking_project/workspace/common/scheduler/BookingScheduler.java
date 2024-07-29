package com.example.booking_project.workspace.common.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Sivasankar.Thalavai
 *
 *         Apr 3, 2023
 */
@Component
public class BookingScheduler {

	private static final Logger logger = LoggerFactory.getLogger(BookingScheduler.class);

//	@Autowired
//	private CheckInDetailsRepository checkInDetailsRepository;
//
//	@Schedules({ @Scheduled(cron = "${every-monday-to-friday-fn-cron}"),
//			@Scheduled(cron = "${every-monday-to-friday-an-cron}"),
//			@Scheduled(cron = "${every-monday-to-friday-ed-cron}") })
//	public void remindAnUserToBookASeat() {
//
//		logger.info("CheckInCheckOutSchduler :: remindAnUserToBookASeat :: STARTS");
//
//		Map<String, java.time.LocalDateTime> employeeCheckInDetailsMap = new TreeMap<>();
//		Map<String, Object> employeeBookingDetailsMap = new TreeMap<>();
//
//		Runnable runnableTask = () -> {
//
//		};
//
//		ExecutorService executor = Executors.newSingleThreadExecutor();
//		executor.execute(runnableTask);
//		executor.submit(runnableTask);
//		executor.shutdownNow();
//
//		logger.info("CheckInCheckOutSchduler :: remindAnUserToBookASeat :: ENDS");
//
//	}

}