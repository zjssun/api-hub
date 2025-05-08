package com.main.entity.enums;

public enum PlayerEnum {
    MONESY("m0nesy", "93306681-bce6-4369-8c41-e0bdba2597ed");
//    DONK("donk", "e5e8e2a6-d716-4493-b949-e16965f41654"),
//    S1MPLE("s1mple", "ac71ba3c-d3d4-45e7-8be2-26aa3986867d"),
//    IM("im", "09701d83-187e-41e6-997d-a50b3e8d4d38"),
//    ELIGE("elige", "97f7d868-7221-46eb-a250-38ffaf1cc5c1"),
//    ROPZ("ropz", "d3de50b6-c0fb-4d93-a304-0bdf7749ea5d"),
//    ZYWOO("zywoo", "3b536dda-e3dd-40cd-baed-7e66ab050c8f"),
//    JL("jl", "23b6ffbb-86ec-47ab-acaa-9c76bed0af66"),
//    W0NDERFUL("w0nderful", "591e26a3-eb86-4d4c-afa8-b5754455dc03"),
//    NIKO("niko", "19606e0c-137b-4885-a904-744fa12d25f6"),
//    JAME("jame", "f4c95e23-8930-49a5-b49d-94eff8b412f4"),
//    JKS("jks", "9a8ea9d5-61c7-4b38-b64f-35f4945048fa"),
//    TWISTZZ("twistzz", "a51c1404-1c5e-4688-b82c-ade59245e5b1"),
//    ELECTRONIC("electronic", "928857e9-48e7-41b1-b4e8-217fd1a6e51b"),
//    KSCERATO("kscerato", "c365e0fe-ae29-41d7-b331-ed8904e32d2b");

    private final String name;
    private final String uuid;

    PlayerEnum(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    //根据 name 查找枚举
    public static PlayerEnum fromName(String name) {
        for (PlayerEnum p : PlayerEnum.values()) {
            if (p.name.equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }
    //根据 uuid 查找枚举
    public static PlayerEnum fromUuid(String uuid) {
        for (PlayerEnum p : PlayerEnum.values()) {
            if (p.uuid.equalsIgnoreCase(uuid)) {
                return p;
            }
        }
        return null;
    }
}
