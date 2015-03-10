package webserver.henacat.servlet;

import webserver.henacat.util.Loggers;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author Yoshimasa Tanabe
 */
class SessionManager {

  private static final Logger LOGGER = Loggers.from(SessionManager.class);

  private static SessionManager instance;
  private final ScheduledExecutorService scheduler;
  private final ScheduledFuture<?> cleanerHandle;

  private final int CLEAN_INTERNAL = 60; // seconds
  private final int SESSION_TIMEOUT = 30; // minutes;

  private Map<String, HttpSessionImpl> sessions = new ConcurrentHashMap<>();
  private SessionIdGenerator sessionIdGenerator;

  private SessionManager() {
    scheduler = Executors.newSingleThreadScheduledExecutor();

    Runnable cleaner = () -> {
      LOGGER.info("Do clean sessions");
      cleanSessions();
    };

    cleanerHandle = scheduler.scheduleWithFixedDelay(cleaner, CLEAN_INTERNAL, CLEAN_INTERNAL, TimeUnit.SECONDS);
    sessionIdGenerator = new SessionIdGenerator();
  }

  static SessionManager getInstance() {
    if (instance == null) {
      instance = new SessionManager();
    }
    return instance;
  }

  synchronized HttpSessionImpl getSession(String id) {
    HttpSessionImpl session = sessions.get(id);
    if (session != null) {
      session.access();
    }
    return session;
  }

  HttpSessionImpl createSession() {
    String id = sessionIdGenerator.generateSessionId();
    HttpSessionImpl session = new HttpSessionImpl(id);
    sessions.put(id, session);
    return session;
  }

  private synchronized void cleanSessions() {
    for (Iterator<String> it = sessions.keySet().iterator(); it.hasNext();) {
      String id = it.next();
      HttpSessionImpl session = sessions.get(id);
      if (session.getLastAccessedTime()
        < (System.currentTimeMillis() - (SESSION_TIMEOUT * 60 * 1000))) {
        it.remove();
      }
    }
  }

}
