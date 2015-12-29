package com.kksk.net.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.kksk.net.server.Task.TaskType;

/**
 * usage:
 * 
 * <pre>
 * MessageHandler handler = new MessageHandler() {
 * 	&#64;Override
 * 	public void handle(Server server, Connection connection, long id, byte[] data) {
 * 		...
 * 	}
 * };
 * Server svr = new Server(port);
 * while(true) svr.receive(handler);
 * </pre>
 *
 */
public class Server implements Closeable {
	private static final ThreadGroup THREAD_GROUP = new ThreadGroup("SERVER_THREADS");
	private static final BlockingQueue<Task> TASK_POOL;
	private static final BlockingQueue<Task> TASK_QUEUE;
	private static final BlockingQueue<Message> MESSAGE_POOL;
	private static final BlockingQueue<Message> RECEIVE_MESSAGE_QUEUE;
	private static final BlockingQueue<Message> SEND_MESSAGE_QUEUE;
	private static final int QUEUE_SIZE = 10000;
	private static AtomicInteger seq = new AtomicInteger();
	public static Map<String, Integer> METRICS = new ConcurrentHashMap<>();
	private static Map<String, Integer> metricsLocal = new HashMap<>();
	private static final Thread TASK_WORKER = new Thread(THREAD_GROUP, new Runnable() {
		@Override
		public void run() {
			boolean spin = false;
			while (true) {
				try {
					Task task = TASK_QUEUE.take();
					switch (task.type) {
					case SPIN:
						if (spin) {
							Thread.sleep(1);
						}
						TASK_QUEUE.put(task);
						spin = true;
						break;
					case HANDSHAKE:
						try {
							boolean result = task.connection.handshake();
							if (result) {
								task.set(TaskType.RECEIVE, task.connection, 0, null);
							}
							TASK_QUEUE.put(task);
						} catch (IOException e) {
							TASK_QUEUE.put(task);
						}
						spin = false;
						break;
					case RECEIVE:
						try {
							Message message = MESSAGE_POOL.take();
							boolean result = task.connection.receive(message);
							if (result) {
								RECEIVE_MESSAGE_QUEUE.put(message);
								seq.incrementAndGet();
								if (seq.compareAndSet(1000, 0)) {
									metricsLocal.put("MESSAGE_POOL", MESSAGE_POOL.size());
									metricsLocal.put("SEND_MESSAGE_QUEUE", SEND_MESSAGE_QUEUE.size());
									metricsLocal.put("RECEIVE_MESSAGE_QUEUE", RECEIVE_MESSAGE_QUEUE.size());
									metricsLocal.put("TASK_QUEUE", TASK_QUEUE.size());
									metricsLocal.put("TASK_POOL", TASK_POOL.size());
									METRICS.putAll(metricsLocal);
								}
							} else {
								MESSAGE_POOL.put(message);
							}
							TASK_QUEUE.put(task);
						} catch (IOException e) {
							TASK_QUEUE.put(task);
						}
						spin = true;
						break;
					case SEND:
						try {
							Message message = SEND_MESSAGE_QUEUE.take();
							task.connection.send(message);
							TASK_POOL.put(task);
							MESSAGE_POOL.put(message);
						} catch (IOException e) {
							TASK_QUEUE.put(task);
						}
						spin = false;
						break;
					default:
						spin = false;
						break;
					}
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}, "TaskWorker");

	static {
		TASK_POOL = new ArrayBlockingQueue<>(QUEUE_SIZE);
		Task[] tasks = new Task[QUEUE_SIZE];
		MESSAGE_POOL = new ArrayBlockingQueue<>(QUEUE_SIZE);
		Message[] messages = new Message[QUEUE_SIZE];
		for (int i = 0; i < QUEUE_SIZE; i++) {
			tasks[i] = new Task();
			messages[i] = new Message();
		}
		Collections.addAll(TASK_POOL, tasks);
		Collections.addAll(MESSAGE_POOL, messages);

		TASK_QUEUE = new ArrayBlockingQueue<>(QUEUE_SIZE);
		Task spin = new Task();
		spin.set(TaskType.SPIN, null, 0, null);
		TASK_QUEUE.add(spin);

		RECEIVE_MESSAGE_QUEUE = new ArrayBlockingQueue<>(QUEUE_SIZE);
		SEND_MESSAGE_QUEUE = new ArrayBlockingQueue<>(QUEUE_SIZE);

		TASK_WORKER.start();
	}

	private final ServerSocket serverSocket;
	private final Thread acceptor;
	private final List<Connection> connections = new ArrayList<>();

	public Server(Integer port) throws IOException {
		serverSocket = new ServerSocket(port);
		acceptor = new Thread(THREAD_GROUP, new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket socket = serverSocket.accept();
						Task task = TASK_POOL.take();
						Connection connection = new Connection(socket);
						connections.add(connection);
						task.set(TaskType.HANDSHAKE, connection, 0, null);
						TASK_QUEUE.put(task);
					} catch (IOException e) {
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		}, "Acceptor#" + port);
		acceptor.start();
	}

	public void receive(MessageHandler... messageHandlers) {
		try {
			Message message = RECEIVE_MESSAGE_QUEUE.take();
			for (MessageHandler messageHandler : messageHandlers) {
				messageHandler.handle(this, message.getConnection(), message.getMessageId(), message.getData());
			}
			MESSAGE_POOL.put(message);
		} catch (InterruptedException e) {
		}
	}

	public void send(Connection connection, long messageId, byte[] data) {
		try {
			Message message = MESSAGE_POOL.take();
			message.set(connection, messageId, data);
			SEND_MESSAGE_QUEUE.put(message);
			Task task = TASK_POOL.take();
			task.set(Task.TaskType.SEND, connection, messageId, data);
			TASK_QUEUE.put(task);
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void close() throws IOException {
		IOException ioException = null;
		for (Connection connection : connections) {
			try {
				connection.close();
			} catch (IOException e) {
				if (ioException == null) {
					ioException = e;
				}
			}
		}
		if (ioException != null) {
			throw ioException;
		}
	}
}
