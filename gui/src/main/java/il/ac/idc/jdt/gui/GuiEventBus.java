package il.ac.idc.jdt.gui;

import com.google.common.eventbus.EventBus;

public class GuiEventBus {

	private EventBus eventBus;

	private static GuiEventBus guiEventBus;

	private static Object[] lock = new Object[0];

	public static GuiEventBus instance() {
		synchronized (lock) {
			if (guiEventBus == null) {
				guiEventBus = new GuiEventBus();
			}
			return guiEventBus;
		}
	}

	private GuiEventBus() {
		eventBus = new EventBus();
	}

	public void post(Object event) {
		eventBus.post(event);
	}

	public void register(Object handler) {
		eventBus.register(handler);
	}
}
