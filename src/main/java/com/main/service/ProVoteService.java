package com.main.service;

import com.main.entity.po.ProVote;
import com.main.mappers.ProVoteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class ProVoteService {
    private static final Logger logger = LoggerFactory.getLogger(ProVoteService.class);
    private static final int MAX_NAME_LENGTH = 32;

    private final ProVoteMapper proVoteMapper;

    public ProVoteService(ProVoteMapper proVoteMapper) {
        this.proVoteMapper = proVoteMapper;
    }

    public boolean vote(String rawName) {
        String name = normalizeName(rawName);
        if (name == null) {
            return false;
        }
        String nameKey = name.toLowerCase(Locale.ROOT);
        proVoteMapper.upsertVote(nameKey, name);
        logger.info("收到投票: {}", name);
        return true;
    }

    public List<ProVote> stats() {
        return proVoteMapper.findAllOrderByVotesDesc();
    }

    private String normalizeName(String rawName) {
        if (rawName == null) {
            return null;
        }
        String normalized = rawName.trim().replaceAll("\\s+", " ");
        if (normalized.isEmpty() || normalized.length() > MAX_NAME_LENGTH) {
            return null;
        }
        return normalized;
    }
}
