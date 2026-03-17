package com.main.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Locale;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum PlayerEnum {
    M0NESY("m0nesy", "93306681-bce6-4369-8c41-e0bdba2597ed"),
    DONK("donk", "e5e8e2a6-d716-4493-b949-e16965f41654"),
    S1MPLE("s1mple", "ac71ba3c-d3d4-45e7-8be2-26aa3986867d"),
    IM("im", "09701d83-187e-41e6-997d-a50b3e8d4d38"),
    ELIGE("elige", "97f7d868-7221-46eb-a250-38ffaf1cc5c1"),
    ROPZ("ropz", "d3de50b6-c0fb-4d93-a304-0bdf7749ea5d"),
    ZYWOO("zywoo", "3b536dda-e3dd-40cd-baed-7e66ab050c8f"),
    JL("jl", "23b6ffbb-86ec-47ab-acaa-9c76bed0af66"),
    W0NDERFUL("w0nderful", "591e26a3-eb86-4d4c-afa8-b5754455dc03"),
    NIKO("niko", "19606e0c-137b-4885-a904-744fa12d25f6"),
    KYOUSUKE("kyousuke", "64000660-f2d2-4c9b-8e29-6ad5ec2b49cf"),
    TWISTZZ("twistzz", "a51c1404-1c5e-4688-b82c-ade59245e5b1"),
    FROZEN("frozen", "18911949-a23d-4b42-bd0b-7a970b51824c"),
    BOROS("boros", "4c3a4a59-0cdb-4477-b90b-1109a1d0cf13");

    private final String name;
    private final String uuid;

    public static Optional<PlayerEnum> fromName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        String normalized = name.toLowerCase(Locale.ROOT);
        for (PlayerEnum player : values()) {
            if (player.getName().equals(normalized)) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
    }
}
