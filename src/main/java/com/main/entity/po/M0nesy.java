package com.main.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;


/**
 * 
 */
public class M0nesy implements Serializable {


	/**
	 * 
	 */
	private String time;

	/**
	 * 
	 */
	private String nickName;

	/**
	 * 
	 */
	private String team;

	/**
	 * 
	 */
	private String matchMap;

	/**
	 * 
	 */
	private String matchScore;

	/**
	 * 
	 */
	private String matchResult;

	/**
	 * 
	 */
	private String matchId;

	/**
	 * 
	 */
	private String roomUrl;

	/**
	 * 
	 */
	private String bestOf;

	/**
	 * 
	 */
	private String effectiveRanking;

	/**
	 * 
	 */
	private String totalKills;

	/**
	 * 
	 */
	private String totalDeaths;

	/**
	 * 
	 */
	private String totalAssistsl;

	/**
	 * 
	 */
	private String rating;

	/**
	 * 
	 */
	private String tripleKill;

	/**
	 * 
	 */
	private String quadroKill;

	/**
	 * 
	 */
	private String pentaKill;

	/**
	 * 
	 */
	private String timestamp;

	/**
	 * 
	 */
	private String adr;


	public void setTime(String time){
		this.time = time;
	}

	public String getTime(){
		return this.time;
	}

	public void setNickName(String nickName){
		this.nickName = nickName;
	}

	public String getNickName(){
		return this.nickName;
	}

	public void setTeam(String team){
		this.team = team;
	}

	public String getTeam(){
		return this.team;
	}

	public void setMatchMap(String matchMap){
		this.matchMap = matchMap;
	}

	public String getMatchMap(){
		return this.matchMap;
	}

	public void setMatchScore(String matchScore){
		this.matchScore = matchScore;
	}

	public String getMatchScore(){
		return this.matchScore;
	}

	public void setMatchResult(String matchResult){
		this.matchResult = matchResult;
	}

	public String getMatchResult(){
		return this.matchResult;
	}

	public void setMatchId(String matchId){
		this.matchId = matchId;
	}

	public String getMatchId(){
		return this.matchId;
	}

	public void setRoomUrl(String roomUrl){
		this.roomUrl = roomUrl;
	}

	public String getRoomUrl(){
		return this.roomUrl;
	}

	public void setBestOf(String bestOf){
		this.bestOf = bestOf;
	}

	public String getBestOf(){
		return this.bestOf;
	}

	public void setEffectiveRanking(String effectiveRanking){
		this.effectiveRanking = effectiveRanking;
	}

	public String getEffectiveRanking(){
		return this.effectiveRanking;
	}

	public void setTotalKills(String totalKills){
		this.totalKills = totalKills;
	}

	public String getTotalKills(){
		return this.totalKills;
	}

	public void setTotalDeaths(String totalDeaths){
		this.totalDeaths = totalDeaths;
	}

	public String getTotalDeaths(){
		return this.totalDeaths;
	}

	public void setTotalAssistsl(String totalAssistsl){
		this.totalAssistsl = totalAssistsl;
	}

	public String getTotalAssistsl(){
		return this.totalAssistsl;
	}

	public void setRating(String rating){
		this.rating = rating;
	}

	public String getRating(){
		return this.rating;
	}

	public void setTripleKill(String tripleKill){
		this.tripleKill = tripleKill;
	}

	public String getTripleKill(){
		return this.tripleKill;
	}

	public void setQuadroKill(String quadroKill){
		this.quadroKill = quadroKill;
	}

	public String getQuadroKill(){
		return this.quadroKill;
	}

	public void setPentaKill(String pentaKill){
		this.pentaKill = pentaKill;
	}

	public String getPentaKill(){
		return this.pentaKill;
	}

	public void setTimestamp(String timestamp){
		this.timestamp = timestamp;
	}

	public String getTimestamp(){
		return this.timestamp;
	}

	public void setAdr(String adr){
		this.adr = adr;
	}

	public String getAdr(){
		return this.adr;
	}

	@Override
	public String toString (){
		return "time:"+(time == null ? "空" : time)+"，nickName:"+(nickName == null ? "空" : nickName)+"，team:"+(team == null ? "空" : team)+"，matchMap:"+(matchMap == null ? "空" : matchMap)+"，matchScore:"+(matchScore == null ? "空" : matchScore)+"，matchResult:"+(matchResult == null ? "空" : matchResult)+"，matchId:"+(matchId == null ? "空" : matchId)+"，roomUrl:"+(roomUrl == null ? "空" : roomUrl)+"，bestOf:"+(bestOf == null ? "空" : bestOf)+"，effectiveRanking:"+(effectiveRanking == null ? "空" : effectiveRanking)+"，totalKills:"+(totalKills == null ? "空" : totalKills)+"，totalDeaths:"+(totalDeaths == null ? "空" : totalDeaths)+"，totalAssistsl:"+(totalAssistsl == null ? "空" : totalAssistsl)+"，rating:"+(rating == null ? "空" : rating)+"，tripleKill:"+(tripleKill == null ? "空" : tripleKill)+"，quadroKill:"+(quadroKill == null ? "空" : quadroKill)+"，pentaKill:"+(pentaKill == null ? "空" : pentaKill)+"，timestamp:"+(timestamp == null ? "空" : timestamp)+"，adr:"+(adr == null ? "空" : adr);
	}
}
