package com.kksk.net.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.kksk.net.client.Task.TaskType;

/**
 * usage:<br />
 * <pre>
 * long connectionId = client.open(inetAddress, port);
 * final byte[] ret = client.requestReply(connectionId, Connection.generateId(), data);
 * </pre>
 *
 */
public class Client implements Closeable {
	private static final ThreadGroup THREAD_GROUP = new ThreadGroup("CLIENT_THREADS");
	private static final BlockingQueue<Task> TASK_POOL;
	private static final BlockingQueue<Task> TASK_QUEUE;
	private static final BlockingQueue<Message> MESSAGE_POOL;
	private static final BlockingQueue<ReceiveMessageHandler> RECEIVE_MESSAGE_HANDLER_POOL;
	private static final BlockingQueue<Message> SEND_MESSAGE_QUEUE;
	private static final int QUEUE_SIZE = 10000;
	private static final Map<Long, ReceiveMessageHandler> RECEIVE_MESSAGE_HANDLER_MAP = new ConcurrentHashMap<>(QUEUE_SIZE);
	private static Map<Long, Connection> connectionMap = new ConcurrentHashMap<>();
	public static Object lock = new Object();
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
					case RECEIVE:
						try {
							Message message = MESSAGE_POOL.take();
							boolean result = task.connection.receive(message);
							if (result) {
								Long messageId = message.getMessageId();
								if (RECEIVE_MESSAGE_HANDLER_MAP.containsKey(messageId)) {
									ReceiveMessageHandler messageHandler = RECEIVE_MESSAGE_HANDLER_MAP.get(messageId);
									messageHandler.handle(message.getConnection(), message.getMessageId(), message.getData());
									RECEIVE_MESSAGE_HANDLER_MAP.remove(messageId);
								}
								seq.incrementAndGet();
								if (seq.compareAndSet(1000, 0)) {
									metricsLocal.put("MESSAGE_POOL", MESSAGE_POOL.size());
									metricsLocal.put("SEND_MESSAGE_QUEUE", SEND_MESSAGE_QUEUE.size());
									metricsLocal.put("RECEIVE_MESSAGE_HANDLER_POOL", RECEIVE_MESSAGE_HANDLER_POOL.size());
									metricsLocal.put("TASK_QUEUE", TASK_QUEUE.size());
									metricsLocal.put("TASK_POOL", TASK_POOL.size());
									METRICS.putAll(metricsLocal);
								}
								MESSAGE_POOL.put(message);
								TASK_POOL.put(task);
							} else {
								MESSAGE_POOL.put(message);
								TASK_QUEUE.put(task);
							}
						} catch (IOException e) {
							TASK_QUEUE.put(task);
						}
						spin = true;
						break;
					case SEND:
						try {
							Message message = SEND_MESSAGE_QUEUE.take();
							task.connection.send(message);
							MESSAGE_POOL.put(message);
							TASK_POOL.put(task);
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
		RECEIVE_MESSAGE_HANDLER_POOL = new ArrayBlockingQueue<>(QUEUE_SIZE);
		Message[] send_messages = new Message[QUEUE_SIZE];
		ReceiveMessageHandler[] receive_message_handlers = new ReceiveMessageHandler[QUEUE_SIZE];
		for (int i = 0; i < QUEUE_SIZE; i++) {
			tasks[i] = new Task();
			send_messages[i] = new Message();
			receive_message_handlers[i] = new ReceiveMessageHandler();
		}
		Collections.addAll(TASK_POOL, tasks);
		Collections.addAll(MESSAGE_POOL, send_messages);
		Collections.addAll(RECEIVE_MESSAGE_HANDLER_POOL, receive_message_handlers);

		TASK_QUEUE = new ArrayBlockingQueue<>(QUEUE_SIZE);
		Task spin = new Task();
		spin.set(TaskType.SPIN, null);
		TASK_QUEUE.add(spin);

		SEND_MESSAGE_QUEUE = new ArrayBlockingQueue<>(QUEUE_SIZE);

		TASK_WORKER.start();
	}

	public Client() {
	}

	public long open(InetAddress inetAddress, int port) throws IOException {
		Long id = Connection.generateId();
		Socket socket = new Socket(inetAddress, port);
		Connection connection = new Connection(socket);
		connectionMap.put(id, connection);
		connection.handshake();
		// System.out.println(String.format("opend:[%d]", id));
		return id;
	}

	public void request(Long connectionId, long messageId, byte[] data) {
		try {
			// System.out.println(String.format("connectionId:[%d], messageId:[%d]", connectionId, messageId));
			Message message = MESSAGE_POOL.take();
			message.set(connectionMap.get(connectionId), true, messageId, data);
			SEND_MESSAGE_QUEUE.put(message);
			Task task = TASK_POOL.take();
			task.set(TaskType.SEND, connectionMap.get(connectionId));
			TASK_QUEUE.put(task);
		} catch (InterruptedException e) {
		}
	}

	public byte[] requestReply(Long connectionId, long messageId, byte[] data) {
		try {
			// System.out.println(String.format("connectionId:[%d], messageId:[%d]", connectionId, messageId));
			Connection connection = connectionMap.get(connectionId);
			ReceiveMessageHandler receiveMessageHandler = RECEIVE_MESSAGE_HANDLER_POOL.take();
			receiveMessageHandler.set(messageId);
			RECEIVE_MESSAGE_HANDLER_MAP.put(messageId, receiveMessageHandler);
			Message message = MESSAGE_POOL.take();
			message.set(connection, false, messageId, data);
			SEND_MESSAGE_QUEUE.put(message);
			Task task = TASK_POOL.take();
			task.set(TaskType.RECEIVE, connection);
			TASK_QUEUE.put(task);
			task = TASK_POOL.take();
			task.set(TaskType.SEND, connection);
			TASK_QUEUE.put(task);
			byte[] result = null;
			synchronized (receiveMessageHandler.lock) {
				receiveMessageHandler.lock.wait();
				result = receiveMessageHandler.getData();
			}
			RECEIVE_MESSAGE_HANDLER_POOL.put(receiveMessageHandler);
			return result;
		} catch (InterruptedException e) {
			return null;
		}
	}

	@Override
	public void close() throws IOException {
		IOException ioException = null;
		for (Connection connection : connectionMap.values()) {
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
