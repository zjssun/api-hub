package com.main.entity.enums;

import com.main.entity.po.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum PlayerEnum {
    M0NESY("m0nesy", "93306681-bce6-4369-8c41-e0bdba2597ed", M0nesy.class),
    DONK("donk", "e5e8e2a6-d716-4493-b949-e16965f41654", Donk.class),
    S1MPLE("s1mple", "ac71ba3c-d3d4-45e7-8be2-26aa3986867d", S1mple.class),
    IM("im", "09701d83-187e-41e6-997d-a50b3e8d4d38", Im.class),
    ELIGE("elige", "97f7d868-7221-46eb-a250-38ffaf1cc5c1", Elige.class),
    ROPZ("ropz", "d3de50b6-c0fb-4d93-a304-0bdf7749ea5d", Ropz.class),
    ZYWOO("zywoo", "3b536dda-e3dd-40cd-baed-7e66ab050c8f", Zywoo.class),
    JL("jl", "23b6ffbb-86ec-47ab-acaa-9c76bed0af66", Jl.class),
    W0NDERFUL("w0nderful", "591e26a3-eb86-4d4c-afa8-b5754455dc03", W0nderful.class),
    NIKO("niko", "19606e0c-137b-4885-a904-744fa12d25f6", Niko.class),
    KYOUSUKE("kyousuke", "64000660-f2d2-4c9b-8e29-6ad5ec2b49cf", Kyousuke.class),
    TWISTZZ("twistzz", "a51c1404-1c5e-4688-b82c-ade59245e5b1", Twistzz.class),
    FROZEN("frozen", "18911949-a23d-4b42-bd0b-7a970b51824c", Frozen.class);

    private final String name;
    private final String uuid;
    private final Class<? extends PlayerMatchData> dataClass; // 存储对应的玩家数据类类型

    PlayerEnum(String name, String uuid, Class<? extends PlayerMatchData> dataClass) {
        this.name = name;
        this.uuid = uuid;
        this.dataClass = dataClass;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public Class<? extends PlayerMatchData> getDataClass() {
        return dataClass;
    }

    //创建一个静态的 Map 用于反向查找。
    private static final Map<Class<? extends PlayerMatchData>, PlayerEnum> CLASS_TO_ENUM_MAP = new HashMap<>();
    //从名称字符串进行反向查找
    private static final Map<String,PlayerEnum> NAME_TO_ENUM_MAP = new HashMap<>();
    static {
        for (PlayerEnum player : values()) {
            CLASS_TO_ENUM_MAP.put(player.getDataClass(), player);
            NAME_TO_ENUM_MAP.put(player.getName(),player);
        }
    }
    //通过类名来找对应的枚举
    public static Optional<PlayerEnum> fromDataClass(Class<? extends PlayerMatchData> dataClass) {
        return Optional.ofNullable(CLASS_TO_ENUM_MAP.get(dataClass));
    }
    //通过名称找对应的枚举
    public static Optional<PlayerEnum> fromName(String name){
        return Optional.ofNullable(NAME_TO_ENUM_MAP.get(name));
    }

}
