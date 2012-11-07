package org.rscangel.client.model;

/**
 * 
 * @author Hikilaka
 *
 */
public final class ColorMutator extends Thread {

	private final Object lock = new Object();

	private volatile int offset;

	public ColorMutator() {
		setDaemon(true);
	}

	@Override
	public void run() {
		int counter = 10;
		boolean incr = true;
		while (true) {
			try {
				if (counter >= 10) {
					synchronized (lock) {
						if (offset >= 16) {
							incr = false;
						} else if (offset <= 0){
							incr = true;
						}				
						offset += (incr ? 2 : -2);
						counter = 0;
					}
				}
				counter++;
				Thread.sleep(1000);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public int getOffset() {
		synchronized (lock) {
			return offset;
		}
	}
}