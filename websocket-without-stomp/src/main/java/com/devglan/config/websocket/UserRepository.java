package com.devglan.config.websocket;

import java.util.HashMap;

public class UserRepository
{
    static HashMap<Long, User> users = new HashMap<>();

    static
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


    public static User getUser(Long userId)
    {
        User user = users.get(userId);
        if (user == null)
        {
            user = new User(new Long(userId));
        }

        return user;
    }


}
