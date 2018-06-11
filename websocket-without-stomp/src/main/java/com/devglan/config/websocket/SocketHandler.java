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

    private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);

//    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    Map<String, List<WebSocketSession>> gamePlayers = new HashMap<>();  // game key <-> session user

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException, IOException {
        Map<String, String> value = new Gson().fromJson(message.getPayload(), Map.class);

        /*for(WebSocketSession webSocketSession : sessions) {
            webSocketSession.sendMessage(new TextMessage("Hello " + value.get("name") + " !"));
		}*/

        String userId = value.get("user");
        String code = value.get("code");
        String coor = value.get("coor");

        joinGame(session, code, coor, userId);
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


//        sessions.add(session);
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
        Object g = session.getAttributes().get("game");
        if (g == null) return;

        String game = (String)g;

        List players = gamePlayers.get(game);
        if (players == null) return;

        players.remove(session);

        if (players.size() == 0)
        {
            gamePlayers.remove(game);
        }
        else
        {
            sendMessageToPlayersForGame(game);
        }
    }

    private void joinGame(WebSocketSession session, String code, String coor, String userId)
    {
        String[] coors = coor.split(",");
        double latitude = Double.parseDouble(coors[0]);
        double longitude = Double.parseDouble(coors[1]);
        String lat = String.format("%.3f", latitude);
        String lng = String.format("%.3f", longitude);

        String game = code+"_"+lat+","+lng;

        //存取用户信息
        Object userObject = session.getAttributes().get("user");
        User user;
        if (userObject == null)
        {
            user = UserRepository.getUser(new Long(userId));
            session.getAttributes().put("user", user);
            session.getAttributes().put("game", game);
        }
        else
        {
            user = (User) userObject;
        }

        List<WebSocketSession> players = gamePlayers.get(game);
        if (players == null)
        {
            players = new ArrayList();
            players.add(session);

            gamePlayers.put(game, players);
        }
        else
        {
            if (!players.contains(session))
            {
                players.add(session);
            }
        }

        sendMessageToPlayersForGame(game);
    }

    private void sendMessageToPlayersForGame(String game)
    {
        List<WebSocketSession> sessions = gamePlayers.get(game);
        List<User> players = new ArrayList<>();
        for (WebSocketSession session : sessions)
        {
            if (session.isOpen())
            {
                Object u = session.getAttributes().get("user");
                if(u != null)
                {
                    players.add((User)u);
                }
            }
        }

        Response response = new Response();
        response.setCode(1);
        response.setMessage("OK");
        response.setUsers(players);

        Gson gson = new Gson();
        String str = gson.toJson(response);

        //给参加比赛的所有用户发送消息
        for (WebSocketSession session : sessions)
        {
            if (session.isOpen())
            {
                try
                {
                    session.sendMessage(new TextMessage(str));
                }
                catch (IOException e)
                {
                    //发送失败，直接删除用户
                    sessions.remove(session);
                }
            }
        }
    }
}
