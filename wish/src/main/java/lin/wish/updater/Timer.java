package lin.wish.updater;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timer extends Thread {
	//private long current;
	private Logger logger = LoggerFactory.getLogger(Timer.class);
	private List<TimeTaskStruct> timerHandlers = new ArrayList<TimeTaskStruct>();

//	public void start() {
//		//current = System.currentTimeMillis();
//		super.start();
//	}

	public void run() {
		while (!Thread.interrupted()) {
			try {
				minuPeriod();
				alarm();
				//logger.debug("Timer sleep 1 sec");
				Thread.sleep(1000);
			} catch (Throwable e) {
				logger.error("Timer运行异常。", e);
			}
		}
	}

	/**
	 * @param period
	 *            单位为秒。
	 * */
	public void addTimerTask(TimeTask task, long period) {
		if (task == null) {
			return;
		}

		TimeTaskStruct taskStruct = new TimeTaskStruct();
		taskStruct.interval = period;
		taskStruct.period = period;
		taskStruct.task = task;

		this.timerHandlers.add(taskStruct);
	}

	public void removeTimer(TimeTask task) {
		if (task == null) {
			return;
		}

		for (TimeTaskStruct taskStruct : timerHandlers) {
			if (taskStruct.task.equals(task)) {
				timerHandlers.remove(taskStruct);
			}
		}
	}

	// {{private

	private void minuPeriod() {
		for (TimeTaskStruct taskStruct : timerHandlers) {
			taskStruct.period--;
		}
	}

	/**
	 * 异步唤醒。
	 * */
	private void alarm() {
		for (TimeTaskStruct taskStruct : timerHandlers) {
			if (taskStruct.period < 0) {
				taskStruct.period = taskStruct.interval;
				taskStruct.task.onTimer();
			}
		}
	}

	// }}


	private class TimeTaskStruct {
		public long interval;
		public long period;
		public TimeTask task;
	}
}
