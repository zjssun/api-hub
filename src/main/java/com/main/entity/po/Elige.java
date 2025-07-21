package com.main.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2025-07-22
 */
@Data
@EqualsAndHashCode(callSuper = false)

@TableName("elige")
public class Elige implements PlayerMatchData {

    private static final long serialVersionUID = 1L;

    @TableField("nick_name")
    private String nickName;

    @TableField("team")
    private String team;

    @TableField("match_map")
    private String matchMap;

    @TableField("match_score")
    private String matchScore;

    @TableField("match_result")
    private String matchResult;

    @TableField("match_id")
    private String matchId;

    @TableField("room_url")
    private String roomUrl;

    @TableField("best_of")
    private String bestOf;

    @TableField("effective_ranking")
    private String effectiveRanking;

    @TableField("total_kills")
    private String totalKills;

    @TableField("total_deaths")
    private String totalDeaths;

    @TableField("total_assistsL")
    private String totalAssistsl;

    @TableField("rating")
    private String rating;

    @TableField("triple_kill")
    private String tripleKill;

    @TableField("quadro_kill")
    private String quadroKill;

    @TableField("penta_kill")
    private String pentaKill;

    @TableId(value = "timestamp", type = IdType.NONE)
    private String timestamp;

    @TableField("adr")
    private String adr;


}
