package lin.wish.updater;

abstract public class TimeTask implements Runnable {
	private Thread currentThread = null;
	
	public void onTimer() {
		currentThread = new Thread(this);
		currentThread.start();
	}

	public void run() {
		doTimer();
	}

	/**
	 * 定时调用函数。
	 * */
	abstract protected void doTimer();
}
