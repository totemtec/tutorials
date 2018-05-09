package com.devglan.config.websocket;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {

    HashMap<Long, User> users = new HashMap<>();

    {

        User user1 = new User(1);
        user1.setAvatar("http://images.newsmth.net/nForum/img/face_default_f.jpg");
        user1.setName("逍遥一狂");
        users.put(new Long(1), user1);

        User user2 = new User(2);
        user2.setAvatar("http://images.newsmth.net/nForum/uploadFace/D/douzi.2046.jpg");
        user2.setName("miaomimiya");
        users.put(new Long(2), user2);

        User user3 = new User(3);
        user3.setAvatar("http://images.newsmth.net/nForum/uploadFace/M/Missing7.8821.jpg");
        user3.setName("豆子");
        users.put(new Long(3), user3);

        User user4 = new User(4);
        user4.setAvatar("http://images.newsmth.net/nForum/uploadFace/M/MaxKevin.7754.jpg");
        user4.setName("高飞");
        users.put(new Long(4), user4);

        User user5 = new User(5);
        user5.setAvatar("http://images.newsmth.net/nForum/uploadFace/H/huangdh.4336.jpg");
        user5.setName("老正太");
        users.put(new Long(5), user5);
    }



    private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);

    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    Map<String, List<User>> gamePlayers = new HashMap<>();  // game key
    Map<String, User> sessionUserMap = new HashMap<>();  // session id

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException, IOException {
        Map<String, String> value = new Gson().fromJson(message.getPayload(), Map.class);

        /*for(WebSocketSession webSocketSession : sessions) {
            webSocketSession.sendMessage(new TextMessage("Hello " + value.get("name") + " !"));
		}*/

        String userId = value.get("user");
        String code = value.get("code");
        String coor = value.get("coor");

        List<User> users = joinGame(session, code, coor, userId);
        Response response = new Response();
        response.setCode(1);
        response.setMessage("OK");
        response.setUsers(users);

        Gson gson = new Gson();
        String str = gson.toJson(response);

        session.sendMessage(new TextMessage(str));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        InetSocketAddress clientAddress = session.getRemoteAddress();
        HttpHeaders handshakeHeaders = session.getHandshakeHeaders();

        //the messages will be broadcasted to all users.
        logger.info("Accepted connection from: {}:{}", clientAddress.getHostString(), clientAddress.getPort());
//        logger.info("Client hostname: {}", clientAddress.getHostName());
//        logger.info("Client ip: {}", clientAddress.getAddress().getHostAddress());
//        logger.info("Client port: {}", clientAddress.getPort());
//
//        logger.info("Session accepted protocols: {}", session.getAcceptedProtocol());
//        logger.info("Session binary message size limit: {}", session.getBinaryMessageSizeLimit());
//        logger.info("Session id: {}", session.getId());
//        logger.info("Session text message size limit: {}", session.getTextMessageSizeLimit());
//        logger.info("Session uri: {}", session.getUri().toString());
//
//        logger.info("Handshake header: Accept {}", handshakeHeaders.toString());
//        logger.info("Handshake header: User-Agent {}", handshakeHeaders.get("User-Agent").toString());
//        logger.info("Handshake header: Sec-WebSocket-Extensions {}", handshakeHeaders.get("Sec-WebSocket-Extensions").toString());
//        logger.info("Handshake header: Sec-WebSocket-Key {}", handshakeHeaders.get("Sec-WebSocket-Key").toString());
//        logger.info("Handshake header: Sec-WebSocket-Version {}", handshakeHeaders.get("Sec-WebSocket-Version").toString());


        sessions.add(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//        removeUserFromGame(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        removeUserFromGame(session);
    }

    private void removeUserFromGame(WebSocketSession session)
    {
        String sessionId = session.getId();
        User user = sessionUserMap.get(sessionId);
        if (user == null) return;

        String gameKey = user.getGameKey();
        List players = gamePlayers.get(gameKey);
        if (players == null) return;
        players.remove(user);

        if (players.size() == 0)
        {
            gamePlayers.remove(gameKey);
        }
    }

    private List<User> joinGame(WebSocketSession session, String code, String coor, String userId)
    {
        String[] coors = coor.split(",");
        double latitude = Double.parseDouble(coors[0]);
        double longitude = Double.parseDouble(coors[1]);
        String lat = String.format("%.3f", latitude);
        String lng = String.format("%.3f", longitude);

        User user = users.get(new Long(userId));
        if (user == null)
        {
            user = new User(new Long(userId));
        }

        sessionUserMap.put(session.getId(), user);

        String key = code+"_"+lat+","+lng;
        user.setGameKey(key);
        List players = gamePlayers.get(key);
        if (players == null)
        {
            players = new ArrayList();
            players.add(user);

            gamePlayers.put(key, players);
        }
        else
        {
            if (!players.contains(user))
            {
                players.add(user);
            }
        }

        return players;
    }
}
