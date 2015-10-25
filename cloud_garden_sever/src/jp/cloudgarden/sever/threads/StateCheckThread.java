package jp.cloudgarden.sever.threads;

import jp.cloudgarden.sever.jax.CloudController;

public class StateCheckThread extends Thread {
	private CloudController controller;
	private boolean isRunning = false;
	private int intervalSec = 3600;

	public StateCheckThread(CloudController controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		isRunning = true;

		while (isRunning) {
			controller.updateAllCurrentStates();

			try {
				Thread.sleep(intervalSec * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopThread(){
		this.isRunning = false;
	}

	public boolean isRunning(){
		return isRunning;
	}

}
