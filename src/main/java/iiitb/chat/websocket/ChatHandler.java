//package iiitb.chat.websocket;
//
//import org.springframework.web.socket.*;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.concurrent.ConcurrentHashMap;
//
//public class ChatHandler extends TextWebSocketHandler {
//
//    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        String userId = session.getUri().getQuery().split("=")[1];
//        sessions.put(userId, session);
//    }
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        // Format: "toUserId::ciphertext"
//        String[] parts = message.getPayload().split("::", 2);
//        String toUserId = parts[0];
//        String ciphertext = parts[1];
//
//        WebSocketSession recipientSession = sessions.get(toUserId);
//        if (recipientSession != null && recipientSession.isOpen()) {
//            recipientSession.sendMessage(new TextMessage(ciphertext));
//        }
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        sessions.values().remove(session);
//    }
//}
